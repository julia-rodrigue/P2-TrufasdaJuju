package com.example.sitetrufas.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Pedido {

    private String id;
    private String nomeCliente;
    private String telefone;
    private String formaPagamento;
    private String dataRetirada;
    private String status;

    public Pedido() {}

    // SELECT
    public Pedido(String id, String nomeCliente, String telefone, String formaPagamento, String dataRetirada, String status){
        this.id = id;
        this.nomeCliente = nomeCliente;
        this.telefone = telefone;
        this.formaPagamento = formaPagamento;
        this.dataRetirada = dataRetirada;
        this.status = status;
    }

    // INSERT
    public Pedido(String nomeCliente, String telefone, String formaPagamento, String dataRetirada){
        this.nomeCliente = nomeCliente;
        this.telefone = telefone;
        this.formaPagamento = formaPagamento;
        this.dataRetirada = dataRetirada;
    }

    // GETTERS
    public String getId()             { return id; }
    public String getNomeCliente()    { return nomeCliente; }
    public String getTelefone()       { return telefone; }
    public String getFormaPagamento() { return formaPagamento; }
    public String getDataRetirada()   { return dataRetirada; }
    public String getStatus()         { return status; }

    // SETTERS
    public void setId(String id)                       { this.id = id; }
    public void setNomeCliente(String nomeCliente)     { this.nomeCliente = nomeCliente; }
    public void setTelefone(String telefone)           { this.telefone = telefone; }
    public void setFormaPagamento(String fp)           { this.formaPagamento = fp; }
    public void setDataRetirada(String dataRetirada)   { this.dataRetirada = dataRetirada; }
    public void setStatus(String status)               { this.status = status; }

    // CONVERTER
    public static Pedido converter(Map<String, Object> registro){
        UUID id             = (UUID)   registro.get("id");
        String nomeCliente  = (String) registro.get("nome_cliente");
        String telefone     = (String) registro.get("telefone");
        String formaPag     = (String) registro.get("forma_pagamento");
        String dataRetirada = registro.get("data_retirada") != null ? registro.get("data_retirada").toString() : null;
        String status       = (String) registro.get("status");

        return new Pedido(id.toString(), nomeCliente, telefone, formaPag, dataRetirada, status);
    }

    public static ArrayList<Pedido> converterTodos(List<Map<String, Object>> registros){
        ArrayList<Pedido> lista = new ArrayList<>();
        for(Map<String, Object> registro : registros){
            lista.add(converter(registro));
        }
        return lista;
    }
}
