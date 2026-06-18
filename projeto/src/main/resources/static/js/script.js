// CRUD da página de Histórico de Pedidos (Trufas da Juju)
// Edição inline e exclusão via fetch/AJAX, consumindo /api/pedidos

const OPCOES_PAGAMENTO = ["Pix", "Cartão", "Dinheiro"];

// Ícones SVG (mesmo estilo "line icon" usado no resto do site), usados
// nos botões criados dinamicamente por este script.
const ICONES_SVG = {
    salvar: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M5 12.5 9.5 17 19 7"/></svg>',
    cancelar: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M6 6l12 12M18 6 6 18"/></svg>',
    editar: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M4 20h4.2L19 9.2a2 2 0 0 0 0-2.8l-1.4-1.4a2 2 0 0 0-2.8 0L4 15.8V20Z"/><path d="M13.5 6.5l3 3"/></svg>',
    excluir: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M5 7h14"/><path d="M9 7V5a1 1 0 0 1 1-1h4a1 1 0 0 1 1 1v2"/><path d="M7 7l1 13a1 1 0 0 0 1 1h6a1 1 0 0 0 1-1l1-13"/><path d="M10.5 11v6M13.5 11v6"/></svg>'
};

function definirConteudoBotao(botao, chaveIcone, texto) {
    botao.innerHTML = ICONES_SVG[chaveIcone] + " " + texto;
}

function exibirMensagem(texto, tipo) {
    const elemento = document.getElementById("mensagem");
    if (!elemento) return;

    elemento.textContent = texto;
    elemento.className = "mensagem " + (tipo === "erro" ? "mensagem-erro" : "mensagem-sucesso");
    elemento.style.display = "block";

    window.clearTimeout(elemento._timeoutId);
    elemento._timeoutId = window.setTimeout(() => {
        elemento.style.display = "none";
    }, 4000);
}

function obterLinha(botao) {
    return botao.closest("tr");
}

function obterId(linha) {
    return linha.getAttribute("data-id");
}

// Transforma as células da linha em campos editáveis
function editarLinha(botao) {
    const linha = obterLinha(botao);

    // Evita entrar em edição duas vezes na mesma linha
    if (linha.classList.contains("editando")) {
        return;
    }
    linha.classList.add("editando");

    const celulas = linha.querySelectorAll(".campo-visualizacao");

    celulas.forEach((celula) => {
        const campo = celula.getAttribute("data-campo");
        const valorAtual = celula.textContent.trim();
        celula.setAttribute("data-valor-original", valorAtual);

        if (campo === "formaPagamento") {
            const select = document.createElement("select");
            select.className = "input-edicao";
            OPCOES_PAGAMENTO.forEach((opcao) => {
                const optionEl = document.createElement("option");
                optionEl.value = opcao;
                optionEl.textContent = opcao;
                if (opcao === valorAtual) {
                    optionEl.selected = true;
                }
                select.appendChild(optionEl);
            });
            celula.textContent = "";
            celula.appendChild(select);

        } else if (campo === "dataRetirada") {
            const input = document.createElement("input");
            input.type = "date";
            input.className = "input-edicao";
            input.value = valorAtual && valorAtual !== "—" ? valorAtual : "";
            celula.textContent = "";
            celula.appendChild(input);

        } else if (campo === "status") {
            // Status não é editável neste fluxo, mantém apenas leitura
            celula.setAttribute("data-somente-leitura", "true");

        } else {
            const input = document.createElement("input");
            input.type = "text";
            input.className = "input-edicao";
            input.value = valorAtual === "—" ? "" : valorAtual;
            celula.textContent = "";
            celula.appendChild(input);
        }
    });

    // Troca os botões de Ações para Salvar / Cancelar
    const celulaAcoes = linha.querySelector(".acoes");
    celulaAcoes.innerHTML = "";

    const botaoSalvar = document.createElement("button");
    botaoSalvar.type = "button";
    botaoSalvar.className = "btn btn-salvar";
    definirConteudoBotao(botaoSalvar, "salvar", "Salvar");
    botaoSalvar.onclick = () => salvarLinha(botaoSalvar);

    const botaoCancelar = document.createElement("button");
    botaoCancelar.type = "button";
    botaoCancelar.className = "btn btn-cancelar";
    definirConteudoBotao(botaoCancelar, "cancelar", "Cancelar");
    botaoCancelar.onclick = () => cancelarEdicao(botaoCancelar);

    celulaAcoes.appendChild(botaoSalvar);
    celulaAcoes.appendChild(botaoCancelar);
}

// Restaura a linha ao estado de visualização, descartando alterações
function cancelarEdicao(botao) {
    const linha = obterLinha(botao);
    restaurarVisualizacao(linha);
}

function restaurarVisualizacao(linha, dadosAtualizados) {
    const celulas = linha.querySelectorAll(".campo-visualizacao");

    celulas.forEach((celula) => {
        const campo = celula.getAttribute("data-campo");
        const valorOriginal = celula.getAttribute("data-valor-original");
        const valorFinal = dadosAtualizados ? (dadosAtualizados[campo] ?? "") : valorOriginal;

        celula.textContent = (valorFinal === null || valorFinal === undefined || valorFinal === "") ? "—" : valorFinal;
        celula.removeAttribute("data-somente-leitura");
    });

    linha.classList.remove("editando");

    const celulaAcoes = linha.querySelector(".acoes");
    celulaAcoes.innerHTML = "";

    const botaoEditar = document.createElement("button");
    botaoEditar.type = "button";
    botaoEditar.className = "btn btn-editar";
    definirConteudoBotao(botaoEditar, "editar", "Editar");
    botaoEditar.onclick = () => editarLinha(botaoEditar);

    const botaoDeletar = document.createElement("button");
    botaoDeletar.type = "button";
    botaoDeletar.className = "btn btn-deletar";
    definirConteudoBotao(botaoDeletar, "excluir", "Excluir");
    botaoDeletar.onclick = () => deletarPedido(botaoDeletar);

    celulaAcoes.appendChild(botaoEditar);
    celulaAcoes.appendChild(botaoDeletar);
}

// Envia os dados editados para a API (PUT) e atualiza a linha com a resposta
async function salvarLinha(botao) {
    const linha = obterLinha(botao);
    const id = obterId(linha);

    const nomeCliente = linha.querySelector('[data-campo="nomeCliente"] input').value.trim();
    const telefone = linha.querySelector('[data-campo="telefone"] input').value.trim();
    const formaPagamento = linha.querySelector('[data-campo="formaPagamento"] select').value;
    const dataRetirada = linha.querySelector('[data-campo="dataRetirada"] input').value;

    if (!nomeCliente || !telefone) {
        exibirMensagem("Nome e telefone são obrigatórios.", "erro");
        return;
    }

    const payload = {
        nomeCliente: nomeCliente,
        telefone: telefone,
        formaPagamento: formaPagamento,
        dataRetirada: dataRetirada
    };

    botao.disabled = true;
    botao.textContent = "Salvando...";

    try {
        const resposta = await fetch(`${URL_API}/${id}`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload)
        });

        if (!resposta.ok) {
            const erroBody = await resposta.json().catch(() => ({}));
            throw new Error(erroBody.erro || "Não foi possível salvar as alterações.");
        }

        const pedidoAtualizado = await resposta.json();
        restaurarVisualizacao(linha, pedidoAtualizado);
        exibirMensagem("Pedido atualizado com sucesso.", "sucesso");

    } catch (erro) {
        exibirMensagem(erro.message, "erro");
        botao.disabled = false;
        definirConteudoBotao(botao, "salvar", "Salvar");
    }
}

// Remove o pedido após confirmação do usuário
async function deletarPedido(botao) {
    const linha = obterLinha(botao);
    const id = obterId(linha);
    const nome = linha.querySelector('[data-campo="nomeCliente"]').textContent.trim();

    const confirmou = window.confirm(`Tem certeza que deseja excluir o pedido de "${nome}"? Essa ação não pode ser desfeita.`);
    if (!confirmou) {
        return;
    }

    botao.disabled = true;
    botao.textContent = "Excluindo...";

    try {
        const resposta = await fetch(`${URL_API}/${id}`, { method: "DELETE" });

        if (!resposta.ok) {
            const erroBody = await resposta.json().catch(() => ({}));
            throw new Error(erroBody.erro || "Não foi possível excluir o pedido.");
        }

        linha.remove();
        exibirMensagem("Pedido excluído com sucesso.", "sucesso");

        const tabela = document.getElementById("tabela-historico");
        const restantes = tabela.querySelectorAll("tbody tr").length;
        if (restantes === 0) {
            const mensagemVazio = document.createElement("p");
            mensagemVazio.className = "vazio";
            mensagemVazio.textContent = "Nenhum pedido registrado até o momento.";
            tabela.insertAdjacentElement("afterend", mensagemVazio);
        }

    } catch (erro) {
        exibirMensagem(erro.message, "erro");
        botao.disabled = false;
        definirConteudoBotao(botao, "excluir", "Excluir");
    }
}
