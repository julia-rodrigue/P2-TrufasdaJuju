package com.example.sitetrufas.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.sitetrufas.model.Pedido;
import com.example.sitetrufas.model.PedidoService;

@Controller
public class PaginaController {

    @Autowired
    private PedidoService pedidoService;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/produtos")
    public String produtos() {
        return "produtos";
    }

    @GetMapping("/pedidos")
    public String pedidos() {
        return "pedidos";
    }

    @PostMapping("/pedidos")
    public String salvarPedido(Pedido pedido) {
        pedidoService.inserirPedido(pedido);
        return "redirect:/historico";
    }

    @GetMapping("/historico")
    public String historico(Model model) {
        model.addAttribute("pedidos", pedidoService.listarPedidos());
        return "historico";
    }

    @GetMapping("/pedido/{id}/editar")
    public String formAtualizar(@PathVariable("id") String uuid, Model model) {
        Pedido pedido = pedidoService.listarPedidos(uuid);
        model.addAttribute("nome", pedido.getNomeCliente());
        model.addAttribute("telefone", pedido.getTelefone());
        model.addAttribute("data_pedido", pedido.getDataRetirada());
        model.addAttribute("forma_pagamento", pedido.getFormaPagamento());
        model.addAttribute("id", uuid);
        return "formupdpedido";
    }

    @PostMapping("/pedido/{id}/editar")
    public String atualizarPedido(@PathVariable("id") String id,
                                   Model model,
                                   @ModelAttribute Pedido cli) {
        pedidoService.atualizarPedido(cli, id);
        return "redirect:/historico";
    }

    @PostMapping("/pedido/{id}/deletar")
    public String deletarPedido(@PathVariable("id") String id) {
        pedidoService.deletarPedido(id);
        return "redirect:/historico";
    }
    
}
