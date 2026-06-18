package com.example.sitetrufas.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.sitetrufas.config.AutenticacaoInterceptor;
import com.example.sitetrufas.model.Pedido;
import com.example.sitetrufas.model.PedidoService;
import com.example.sitetrufas.model.ProdutoService;
import com.example.sitetrufas.model.Usuario;

@Controller
public class PaginaController {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private ProdutoService produtoService;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    // Cardápio do cliente - lista produtos com botão de adicionar ao carrinho
    @GetMapping("/cliente/cardapio")
    public String cardapioCliente(Model model) {
        model.addAttribute("produtos", produtoService.listarProdutos());
        return "cliente-cardapio";
    }

    // Mantém /produtos para compatibilidade (admin e visitantes)
    @GetMapping("/produtos")
    public String produtos(Model model) {
        model.addAttribute("produtos", produtoService.listarProdutos());
        // Necessário para o modal de edição do administrador (galeria de imagens).
        model.addAttribute("imagens", produtoService.listarImagensDaBiblioteca());
        return "produtos";
    }

    // Tela em que o cliente logado cria um novo pedido
    @GetMapping("/pedidos")
    public String pedidos() {
        return "pedidos";
    }

    @PostMapping("/pedidos")
    public String salvarPedido(Pedido pedido, HttpServletRequest request) {
        Usuario usuarioLogado = usuarioDaSessao(request);
        pedidoService.inserirPedido(pedido, usuarioLogado.getId());
        return "redirect:/meus-pedidos";
    }

    // Histórico de pedidos do próprio cliente
    @GetMapping("/meus-pedidos")
    public String meusPedidos(Model model, HttpServletRequest request) {
        Usuario usuarioLogado = usuarioDaSessao(request);
        model.addAttribute("pedidos", pedidoService.listarPedidosDoUsuario(usuarioLogado.getId()));
        return "meus-pedidos";
    }

    // Histórico completo (todos os pedidos), exclusivo do administrador
    @GetMapping("/admin/historico")
    public String historico(Model model) {
        model.addAttribute("pedidos", pedidoService.listarPedidos());
        return "historico";
    }

    // Cadastro de novos produtos, exclusivo do administrador
    @GetMapping("/admin/produtos/novo")
    public String formNovoProduto(Model model) {
        model.addAttribute("imagens", produtoService.listarImagensDaBiblioteca());
        return "admin-novo-produto";
    }

    private Usuario usuarioDaSessao(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session != null ? (Usuario) session.getAttribute(AutenticacaoInterceptor.ATRIBUTO_SESSAO_USUARIO) : null;
    }

}