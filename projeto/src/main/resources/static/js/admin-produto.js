// Cadastro de produto pelo administrador: upload de imagem para a
// biblioteca (AJAX) e envio do formulário de produto (AJAX).

function exibirMensagemProduto(texto, tipo) {
    const elemento = document.getElementById("mensagem-produto");
    if (!elemento) return;

    elemento.textContent = texto;
    elemento.className = "mensagem " + (tipo === "erro" ? "mensagem-erro" : "mensagem-sucesso");
    elemento.style.display = "block";
}

function adicionarImagemNaGaleria(imagem) {
    const galeria = document.getElementById("galeria-imagens");

    const label = document.createElement("label");
    label.className = "item-galeria";
    label.dataset.imagemId = imagem.id;

    const input = document.createElement("input");
    input.type = "radio";
    input.name = "imagemId";
    input.value = imagem.id;
    input.checked = true;

    const img = document.createElement("img");
    img.src = imagem.caminho;
    img.alt = imagem.nomeArquivo;

    const btnExcluir = document.createElement("button");
    btnExcluir.type = "button";
    btnExcluir.className = "item-galeria-excluir";
    btnExcluir.title = "Excluir imagem";
    btnExcluir.dataset.id = imagem.id;
    btnExcluir.innerHTML = '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M5 7h14"/><path d="M9 7V5a1 1 0 0 1 1-1h4a1 1 0 0 1 1 1v2"/><path d="M7 7l1 13a1 1 0 0 0 1 1h6a1 1 0 0 0 1-1l1-13"/><path d="M10.5 11v6M13.5 11v6"/></svg>';
    ligarBotaoExcluirImagem(btnExcluir);

    label.appendChild(input);
    label.appendChild(img);
    label.appendChild(btnExcluir);
    galeria.prepend(label);
}

// Liga o evento de exclusão a um botão de imagem da galeria.
function ligarBotaoExcluirImagem(botao) {
    botao.addEventListener("click", async (evento) => {
        // Impede que o clique selecione o radio / marque a label
        evento.preventDefault();
        evento.stopPropagation();

        const id = botao.dataset.id;
        if (!confirm("Excluir esta imagem da biblioteca? Esta ação não pode ser desfeita.")) {
            return;
        }

        botao.disabled = true;

        try {
            const resposta = await fetch(`${URL_API_PRODUTOS}/imagens/${id}`, { method: "DELETE" });

            if (!resposta.ok) {
                const erroBody = await resposta.json().catch(() => ({}));
                throw new Error(erroBody.erro || "Não foi possível excluir a imagem.");
            }

            const label = botao.closest(".item-galeria");
            if (label) label.remove();

        } catch (erro) {
            alert(erro.message);
            botao.disabled = false;
        }
    });
}

// Liga os botões de exclusão das imagens já renderizadas no carregamento.
document.querySelectorAll(".item-galeria-excluir").forEach(ligarBotaoExcluirImagem);

document.getElementById("btn-enviar-imagem").addEventListener("click", async () => {
    const inputArquivo = document.getElementById("novaImagem");
    const statusUpload = document.getElementById("status-upload");
    const botao = document.getElementById("btn-enviar-imagem");

    if (!inputArquivo.files || inputArquivo.files.length === 0) {
        statusUpload.textContent = "Selecione um arquivo primeiro.";
        return;
    }

    const formData = new FormData();
    formData.append("arquivo", inputArquivo.files[0]);

    botao.disabled = true;
    statusUpload.textContent = "Enviando...";

    try {
        const resposta = await fetch(`${URL_API_PRODUTOS}/imagens`, {
            method: "POST",
            body: formData
        });

        if (!resposta.ok) {
            const erroBody = await resposta.json().catch(() => ({}));
            throw new Error(erroBody.erro || "Não foi possível enviar a imagem.");
        }

        const imagem = await resposta.json();
        adicionarImagemNaGaleria(imagem);
        statusUpload.textContent = "Imagem enviada e selecionada.";
        inputArquivo.value = "";

    } catch (erro) {
        statusUpload.textContent = erro.message;
    } finally {
        botao.disabled = false;
    }
});

document.getElementById("form-novo-produto").addEventListener("submit", async (evento) => {
    evento.preventDefault();

    const nome = document.getElementById("nome").value.trim();
    const descricao = document.getElementById("descricao").value.trim();
    const preco = document.getElementById("preco").value;
    const imagemSelecionada = document.querySelector('input[name="imagemId"]:checked');

    if (!nome || !preco) {
        exibirMensagemProduto("Nome e preço são obrigatórios.", "erro");
        return;
    }

    const formData = new FormData();
    formData.append("nome", nome);
    formData.append("descricao", descricao);
    formData.append("preco", preco);
    if (imagemSelecionada) {
        formData.append("imagemId", imagemSelecionada.value);
    }

    const botaoSubmit = document.querySelector('#form-novo-produto button[type="submit"]');
    botaoSubmit.disabled = true;
    botaoSubmit.textContent = "Cadastrando...";

    try {
        const resposta = await fetch(URL_API_PRODUTOS, {
            method: "POST",
            body: formData
        });

        if (!resposta.ok) {
            const erroBody = await resposta.json().catch(() => ({}));
            throw new Error(erroBody.erro || "Não foi possível cadastrar o produto.");
        }

        exibirMensagemProduto("Produto cadastrado com sucesso.", "sucesso");
        document.getElementById("form-novo-produto").reset();

    } catch (erro) {
        exibirMensagemProduto(erro.message, "erro");
    } finally {
        botaoSubmit.disabled = false;
        botaoSubmit.textContent = "Cadastrar Produto";
    }
});