package com.example.sitetrufas.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ImagemProduto {

    private String id;
    private String nomeArquivo;
    private String caminho;

    public ImagemProduto() {}

    public ImagemProduto(String id, String nomeArquivo, String caminho) {
        this.id = id;
        this.nomeArquivo = nomeArquivo;
        this.caminho = caminho;
    }

    public String getId()          { return id; }
    public String getNomeArquivo() { return nomeArquivo; }
    public String getCaminho()     { return caminho; }

    public void setId(String id)                     { this.id = id; }
    public void setNomeArquivo(String nomeArquivo)    { this.nomeArquivo = nomeArquivo; }
    public void setCaminho(String caminho)            { this.caminho = caminho; }

    public static ImagemProduto converter(Map<String, Object> registro) {
        UUID id            = (UUID)   registro.get("id");
        String nomeArquivo = (String) registro.get("nome_arquivo");
        String caminho      = (String) registro.get("caminho");
        return new ImagemProduto(id.toString(), nomeArquivo, caminho);
    }

    public static List<ImagemProduto> converterTodos(List<Map<String, Object>> registros) {
        List<ImagemProduto> lista = new ArrayList<>();
        for (Map<String, Object> registro : registros) {
            lista.add(converter(registro));
        }
        return lista;
    }
}
