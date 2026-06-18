package com.example.sitetrufas.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Produto {

    private String id;
    private String nome;
    private String descricao;
    private BigDecimal preco;
    private String imagemId;
    private String caminhoImagem;

    public Produto() {}

    public Produto(String id, String nome, String descricao, BigDecimal preco, String imagemId, String caminhoImagem) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.imagemId = imagemId;
        this.caminhoImagem = caminhoImagem;
    }

    public String getId()                { return id; }
    public String getNome()              { return nome; }
    public String getDescricao()         { return descricao; }
    public BigDecimal getPreco()         { return preco; }
    public String getImagemId()          { return imagemId; }
    public String getCaminhoImagem()     { return caminhoImagem; }

    public void setId(String id)                         { this.id = id; }
    public void setNome(String nome)                     { this.nome = nome; }
    public void setDescricao(String descricao)           { this.descricao = descricao; }
    public void setPreco(BigDecimal preco)               { this.preco = preco; }
    public void setImagemId(String imagemId)             { this.imagemId = imagemId; }
    public void setCaminhoImagem(String caminhoImagem)   { this.caminhoImagem = caminhoImagem; }

    public static Produto converter(Map<String, Object> registro) {
        UUID id              = (UUID)       registro.get("id");
        String nome          = (String)     registro.get("nome");
        String descricao     = (String)     registro.get("descricao");
        BigDecimal preco     = (BigDecimal) registro.get("preco");
        UUID imagemId        = (UUID)       registro.get("imagem_id");
        String caminhoImagem = (String)     registro.get("caminho");

        return new Produto(
            id.toString(),
            nome,
            descricao,
            preco,
            imagemId != null ? imagemId.toString() : null,
            caminhoImagem
        );
    }

    public static List<Produto> converterTodos(List<Map<String, Object>> registros) {
        List<Produto> lista = new ArrayList<>();
        for (Map<String, Object> registro : registros) {
            lista.add(converter(registro));
        }
        return lista;
    }
}
