package com.example.sitetrufas.model;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PedidoService {

    @Autowired
    PedidoDAO pedidoDAO;

    public void inserirPedido(Pedido pedido) {
        pedidoDAO.inserirPedido(pedido);
    }

    public ArrayList<Pedido> listarPedidos() {
        return pedidoDAO.listarPedidos();
    }

    public Pedido listarPedidos(String uuid) {
        return pedidoDAO.buscarPedido(uuid);
    }

    public void atualizarPedido(Pedido novo, String uuid) {
        pedidoDAO.atualizarPedido(novo, uuid);
    }

    public void deletarPedido(String uuid) {
        pedidoDAO.deletarPedido(uuid);
    }
}
