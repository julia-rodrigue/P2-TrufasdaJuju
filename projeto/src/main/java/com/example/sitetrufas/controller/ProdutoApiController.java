package com.example.sitetrufas.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.sitetrufas.model.ImagemProduto;
import com.example.sitetrufas.model.ProdutoService;

/**
 * Endpoints AJAX usados pela tela de cadastro de produto do administrador:
 * upload de uma nova imagem para a biblioteca e criação do produto.
 *
 * O acesso a /api/admin/** é restrito a usuários do tipo ADMINISTRADOR
 * pelo AutenticacaoInterceptor.
 */
@RestController
@RequestMapping("/api/admin/produtos")
public class ProdutoApiController {

    @Autowired
    private ProdutoService produtoService;

    @PostMapping("/imagens")
    public ResponseEntity<?> enviarImagem(@RequestParam("arquivo") MultipartFile arquivo) {
        try {
            ImagemProduto imagem = produtoService.enviarNovaImagem(arquivo);
            return ResponseEntity.ok(imagem);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(erro(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(erro("Não foi possível enviar a imagem."));
        }
    }

    @PostMapping
    public ResponseEntity<?> cadastrarProduto(@RequestParam("nome") String nome,
                                               @RequestParam(value = "descricao", required = false) String descricao,
                                               @RequestParam("preco") BigDecimal preco,
                                               @RequestParam(value = "imagemId", required = false) String imagemId) {
        try {
            produtoService.cadastrarProduto(nome, descricao, preco, imagemId);
            Map<String, String> body = new HashMap<>();
            body.put("mensagem", "Produto cadastrado com sucesso.");
            return ResponseEntity.ok(body);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(erro(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(erro("Não foi possível cadastrar o produto."));
        }
    }

    private Map<String, String> erro(String mensagem) {
        Map<String, String> body = new HashMap<>();
        body.put("erro", mensagem);
        return body;
    }
}
