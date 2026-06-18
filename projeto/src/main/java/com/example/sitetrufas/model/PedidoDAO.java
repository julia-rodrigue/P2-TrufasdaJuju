package com.example.sitetrufas.model;

import java.util.ArrayList;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import jakarta.annotation.PostConstruct;

@Repository
public class PedidoDAO {

    @Autowired
    DataSource dataSource;

    JdbcTemplate jdbc;

    @PostConstruct
    private void initialize() {
        jdbc = new JdbcTemplate(dataSource);
    }

    public void inserirPedido(Pedido pedido, String usuarioId) {
        String sql = "INSERT INTO pedido(usuario_id, nome_cliente, telefone, forma_pagamento, data_retirada) " +
                     "VALUES (?::uuid, ?, ?, ?, ?::date)";

        String dataRetirada = pedido.getDataRetirada();
        Object dataObj = (dataRetirada != null && !dataRetirada.isEmpty()) ? dataRetirada : null;

        Object[] obj = new Object[]{ usuarioId, pedido.getNomeCliente(), pedido.getTelefone(), pedido.getFormaPagamento(), dataObj };

        jdbc.update(sql, obj);
    }

    public void atualizarPedido(Pedido novo, String uuid) {
        String sql = "UPDATE pedido " +
            "SET nome_cliente = ?, telefone = ?, forma_pagamento = ?, data_retirada = ?::date WHERE id = ?::uuid";

        String dataRetirada = novo.getDataRetirada();
        Object dataObj = (dataRetirada != null && !dataRetirada.isEmpty()) ? dataRetirada : null;

        Object[] obj = new Object[]{
            novo.getNomeCliente(),
            novo.getTelefone(),
            novo.getFormaPagamento(),
            dataObj,
            uuid
        };
        jdbc.update(sql, obj);
    }

    public void deletarPedido(String uuid) {
        String sql = "DELETE FROM pedido WHERE id = ?::uuid";
        jdbc.update(sql, uuid);
    }

    public Pedido buscarPedido(String uuid) {
        String sql = "SELECT * FROM pedido WHERE id = ?::uuid";
        return Pedido.converter(jdbc.queryForMap(sql, uuid));
    }

    public ArrayList<Pedido> listarPedidos() {
        String sql = "SELECT * FROM pedido ORDER BY data_pedido DESC";
        return Pedido.converterTodos(jdbc.queryForList(sql));
    }

    public ArrayList<Pedido> listarPedidosDoUsuario(String usuarioId) {
        String sql = "SELECT * FROM pedido WHERE usuario_id = ?::uuid ORDER BY data_pedido DESC";
        return Pedido.converterTodos(jdbc.queryForList(sql, usuarioId));
    }
}
