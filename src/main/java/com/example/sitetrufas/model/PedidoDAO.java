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

    public void inserirPedido(Pedido pedido) {
        String sql = "INSERT INTO pedido(nome_cliente, telefone, forma_pagamento, data_retirada) VALUES (?, ?, ?, ?::date)";

        String dataRetirada = pedido.getDataRetirada();
        Object dataObj = (dataRetirada != null && !dataRetirada.isEmpty()) ? dataRetirada : null;

        Object[] obj = new Object[]{ pedido.getNomeCliente(), pedido.getTelefone(), pedido.getFormaPagamento(), dataObj };

        jdbc.update(sql, obj);
    }

    public void atualizarPedido(Pedido novo, String uuid) {
        String sql = "UPDATE pedido " +
            "SET nome_cliente = ?, telefone = ?, forma_pagamento = ?, data_retirada = ?::date WHERE id = ?::uuid";
        Object[] obj = new Object[]{
            novo.getNomeCliente(),
            novo.getTelefone(),
            novo.getFormaPagamento(),
            novo.getDataRetirada(),
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
}
