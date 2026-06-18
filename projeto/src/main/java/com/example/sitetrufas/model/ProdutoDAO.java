package com.example.sitetrufas.model;

import java.math.BigDecimal;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import jakarta.annotation.PostConstruct;

@Repository
public class ProdutoDAO {

    @Autowired
    DataSource dataSource;

    JdbcTemplate jdbc;

    private static final String SELECT_BASE =
        "SELECT p.id, p.nome, p.descricao, p.preco, p.imagem_id, i.caminho " +
        "FROM produto p LEFT JOIN imagem_produto i ON p.imagem_id = i.id ";

    @PostConstruct
    private void initialize() {
        jdbc = new JdbcTemplate(dataSource);
    }

    public void cadastrarProduto(String nome, String descricao, BigDecimal preco, String imagemId) {
        String sql = "INSERT INTO produto(nome, descricao, preco, imagem_id) VALUES (?, ?, ?, ?::uuid)";
        jdbc.update(sql, nome, descricao, preco, imagemId);
    }

    public void atualizarProduto(String id, String nome, String descricao, BigDecimal preco, String imagemId) {
        String sql = "UPDATE produto SET nome = ?, descricao = ?, preco = ?, imagem_id = ?::uuid WHERE id = ?::uuid";
        jdbc.update(sql, nome, descricao, preco, imagemId, id);
    }

    public void excluirProduto(String id) {
        jdbc.update("DELETE FROM produto WHERE id = ?::uuid", id);
    }

    public List<Produto> listarTodos() {
        String sql = SELECT_BASE + "ORDER BY p.nome ASC";
        return Produto.converterTodos(jdbc.queryForList(sql));
    }

    public Produto buscarPorId(String id) {
        String sql = SELECT_BASE + "WHERE p.id = ?::uuid";
        return Produto.converter(jdbc.queryForMap(sql, id));
    }
}