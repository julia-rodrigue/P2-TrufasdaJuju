package com.example.sitetrufas.model;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import jakarta.annotation.PostConstruct;

@Repository
public class UsuarioDAO {

    @Autowired
    DataSource dataSource;

    JdbcTemplate jdbc;

    @PostConstruct
    private void initialize() {
        jdbc = new JdbcTemplate(dataSource);
    }

    public void cadastrarUsuario(Usuario usuario, String senhaHash) {
        String sql = "INSERT INTO usuario(nome, email, cpf, telefone, data_nascimento, senha_hash, tipo) " +
                     "VALUES (?, ?, ?, ?, ?::date, ?, ?)";

        Object[] obj = new Object[]{
            usuario.getNome(),
            usuario.getEmail(),
            usuario.getCpf(),
            usuario.getTelefone(),
            usuario.getDataNascimento(),
            senhaHash,
            Usuario.TIPO_CLIENTE
        };

        jdbc.update(sql, obj);
    }

    public boolean existeEmailOuCpf(String email, String cpf) {
        String sql = "SELECT COUNT(*) FROM usuario WHERE email = ? OR cpf = ?";
        Integer total = jdbc.queryForObject(sql, Integer.class, email, cpf);
        return total != null && total > 0;
    }

    /**
     * Busca um usuário pelo identificador de login, que pode ser o e-mail
     * ou o CPF, retornando também o hash da senha (uso interno restrito
     * à validação de login).
     */
    public Map<String, Object> buscarParaLogin(String identificador) {
        String sql = "SELECT * FROM usuario WHERE email = ? OR cpf = ?";
        try {
            return jdbc.queryForMap(sql, identificador, identificador);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public Usuario buscarPorId(String id) {
        String sql = "SELECT * FROM usuario WHERE id = ?::uuid";
        try {
            return Usuario.converter(jdbc.queryForMap(sql, id));
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<Map<String, Object>> listarTodos() {
        String sql = "SELECT * FROM usuario ORDER BY data_cadastro DESC";
        return jdbc.queryForList(sql);
    }
}
