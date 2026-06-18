package com.example.sitetrufas.model;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import jakarta.annotation.PostConstruct;

@Repository
public class ImagemProdutoDAO {

    @Autowired
    DataSource dataSource;

    JdbcTemplate jdbc;

    @PostConstruct
    private void initialize() {
        jdbc = new JdbcTemplate(dataSource);
    }

    public ImagemProduto registrarImagem(String nomeArquivo, String caminho) {
        String sql = "INSERT INTO imagem_produto(nome_arquivo, caminho) VALUES (?, ?) RETURNING id";
        String id = jdbc.queryForObject(sql, String.class, nomeArquivo, caminho);
        return new ImagemProduto(id, nomeArquivo, caminho);
    }

    public List<ImagemProduto> listarTodas() {
        String sql = "SELECT * FROM imagem_produto ORDER BY data_upload DESC";
        return ImagemProduto.converterTodos(jdbc.queryForList(sql));
    }

    public ImagemProduto buscarPorId(String id) {
        String sql = "SELECT * FROM imagem_produto WHERE id = ?::uuid";
        return ImagemProduto.converter(jdbc.queryForMap(sql, id));
    }

    /** Quantos produtos usam esta imagem. Usado antes de excluir. */
    public int contarProdutosUsando(String imagemId) {
        String sql = "SELECT COUNT(*) FROM produto WHERE imagem_id = ?::uuid";
        Integer total = jdbc.queryForObject(sql, Integer.class, imagemId);
        return total != null ? total : 0;
    }

    public void excluirImagem(String id) {
        jdbc.update("DELETE FROM imagem_produto WHERE id = ?::uuid", id);
    }
}