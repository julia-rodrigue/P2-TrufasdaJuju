// Ações de administrador na lista de produtos: editar (via modal) e excluir.
// Disponível apenas quando o usuário logado é administrador (o template só
// inclui este script nesse caso).

function exibirMensagemLista(texto, tipo) {
    const elemento = document.getElementById("mensagem-lista");
    if (!elemento) return;
    elemento.textContent = texto;
    elemento.className = "mensagem " + (tipo === "erro" ? "mensagem-erro" : "mensagem-sucesso");
    elemento.style.display = "block";
    elemento.scrollIntoView({ behavior: "smooth", block: "center" });
}

const modal = document.getElementById("modal-editar");

function abrirModal(produto) {
    document.getElementById("edit-id").value = produto.id;
    document.getElementById("edit-nome").value = produto.nome || "";
    document.getElementById("edit-descricao").value = produto.descricao || "";
    document.getElementById("edit-preco").value = produto.preco || "";

    // Marca a imagem atual na galeria do modal
    document.querySelectorAll('input[name="editImagemId"]').forEach((radio) => {
        radio.checked = (radio.value === produto.imagemId);
    });

    modal.style.display = "flex";
}

function fecharModal() {
    modal.style.display = "none";
}

// ---------- Botões EDITAR ----------
document.querySelectorAll(".btn-acao--editar").forEach((botao) => {
    botao.addEventListener("click", () => {
        const card = botao.closest(".produto-card");
        abrirModal({
            id: card.dataset.id,
            nome: card.dataset.nome,
            descricao: card.dataset.descricao === "null" ? "" : card.dataset.descricao,
            preco: card.dataset.preco,
            imagemId: card.dataset.imagemId === "null" ? "" : card.dataset.imagemId
        });
    });
});

// ---------- Salvar edição ----------
const btnSalvar = document.getElementById("btn-salvar-edicao");
if (btnSalvar) {
    btnSalvar.addEventListener("click", async () => {
        const id = document.getElementById("edit-id").value;
        const nome = document.getElementById("edit-nome").value.trim();
        const descricao = document.getElementById("edit-descricao").value.trim();
        const preco = document.getElementById("edit-preco").value;
        const imagemSelecionada = document.querySelector('input[name="editImagemId"]:checked');

        if (!nome || !preco) {
            alert("Nome e preço são obrigatórios.");
            return;
        }

        const params = new URLSearchParams();
        params.append("nome", nome);
        params.append("descricao", descricao);
        params.append("preco", preco);
        if (imagemSelecionada) {
            params.append("imagemId", imagemSelecionada.value);
        }

        btnSalvar.disabled = true;
        btnSalvar.textContent = "Salvando...";

        try {
            const resposta = await fetch(`${URL_API_PRODUTOS}/${id}`, {
                method: "PUT",
                headers: { "Content-Type": "application/x-www-form-urlencoded" },
                body: params.toString()
            });

            if (!resposta.ok) {
                const erroBody = await resposta.json().catch(() => ({}));
                throw new Error(erroBody.erro || "Não foi possível atualizar o produto.");
            }

            fecharModal();
            exibirMensagemLista("Produto atualizado com sucesso.", "sucesso");
            setTimeout(() => window.location.reload(), 800);

        } catch (erro) {
            alert(erro.message);
        } finally {
            btnSalvar.disabled = false;
            btnSalvar.textContent = "Salvar alterações";
        }
    });
}

// ---------- Cancelar edição ----------
const btnCancelar = document.getElementById("btn-cancelar-edicao");
if (btnCancelar) {
    btnCancelar.addEventListener("click", fecharModal);
}
if (modal) {
    modal.addEventListener("click", (e) => {
        if (e.target === modal) fecharModal();
    });
}

// ---------- Botões EXCLUIR ----------
document.querySelectorAll(".btn-acao--excluir").forEach((botao) => {
    botao.addEventListener("click", async () => {
        const id = botao.dataset.id;
        const nome = botao.dataset.nome;

        if (!confirm(`Tem certeza que deseja excluir o produto "${nome}"? Esta ação não pode ser desfeita.`)) {
            return;
        }

        botao.disabled = true;

        try {
            const resposta = await fetch(`${URL_API_PRODUTOS}/${id}`, { method: "DELETE" });

            if (!resposta.ok) {
                const erroBody = await resposta.json().catch(() => ({}));
                throw new Error(erroBody.erro || "Não foi possível excluir o produto.");
            }

            // Remove o card da tela
            const card = botao.closest(".produto-card");
            if (card) card.remove();
            exibirMensagemLista(`Produto "${nome}" excluído com sucesso.`, "sucesso");

        } catch (erro) {
            alert(erro.message);
            botao.disabled = false;
        }
    });
});
