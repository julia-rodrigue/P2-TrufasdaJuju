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

    const input = document.createElement("input");
    input.type = "radio";
    input.name = "imagemId";
    input.value = imagem.id;
    input.checked = true;

    const img = document.createElement("img");
    img.src = imagem.caminho;
    img.alt = imagem.nomeArquivo;

    label.appendChild(input);
    label.appendChild(img);
    galeria.prepend(label);
}

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
