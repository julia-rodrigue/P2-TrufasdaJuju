package com.example.sitetrufas.model;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

public class Usuario implements Serializable {

    public static final String TIPO_CLIENTE = "CLIENTE";
    public static final String TIPO_ADMINISTRADOR = "ADMINISTRADOR";

    private String id;
    private String nome;
    private String email;
    private String cpf;
    private String telefone;
    private String dataNascimento;
    private String senha;
    private String tipo;

    public Usuario() {}

    // SELECT (sem expor a senha/hash para a camada de visualização)
    public Usuario(String id, String nome, String email, String cpf, String telefone, String dataNascimento, String tipo) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.cpf = cpf;
        this.telefone = telefone;
        this.dataNascimento = dataNascimento;
        this.tipo = tipo;
    }

    // GETTERS
    public String getId()             { return id; }
    public String getNome()           { return nome; }
    public String getEmail()          { return email; }
    public String getCpf()            { return cpf; }
    public String getTelefone()       { return telefone; }
    public String getDataNascimento() { return dataNascimento; }
    public String getSenha()          { return senha; }
    public String getTipo()           { return tipo; }

    // SETTERS
    public void setId(String id)                             { this.id = id; }
    public void setNome(String nome)                         { this.nome = nome; }
    public void setEmail(String email)                       { this.email = email; }
    public void setCpf(String cpf)                           { this.cpf = cpf; }
    public void setTelefone(String telefone)                 { this.telefone = telefone; }
    public void setDataNascimento(String dataNascimento)     { this.dataNascimento = dataNascimento; }
    public void setSenha(String senha)                       { this.senha = senha; }
    public void setTipo(String tipo)                         { this.tipo = tipo; }

    public boolean isAdministrador() {
        return TIPO_ADMINISTRADOR.equalsIgnoreCase(tipo);
    }

    // CONVERTER (não inclui o hash da senha, apenas dados seguros para exibição/sessão)
    public static Usuario converter(Map<String, Object> registro) {
        UUID id              = (UUID)   registro.get("id");
        String nome          = (String) registro.get("nome");
        String email         = (String) registro.get("email");
        String cpf           = (String) registro.get("cpf");
        String telefone      = (String) registro.get("telefone");
        String dataNasc      = registro.get("data_nascimento") != null ? registro.get("data_nascimento").toString() : null;
        String tipo          = (String) registro.get("tipo");

        return new Usuario(id.toString(), nome, email, cpf, telefone, dataNasc, tipo);
    }
}
