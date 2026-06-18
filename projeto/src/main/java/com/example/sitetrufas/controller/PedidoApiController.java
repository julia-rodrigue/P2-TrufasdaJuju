package com.example.sitetrufas.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.sitetrufas.model.Pedido;
import com.example.sitetrufas.model.PedidoService;

/**
 * Controller REST usado pela página de Histórico (exclusiva do
 * administrador) para realizar as operações de CRUD via AJAX
 * (edição inline e exclusão), sem precisar recarregar a página.
 *
 * O acesso a /api/pedidos/** é restrito a usuários do tipo
 * ADMINISTRADOR pelo AutenticacaoInterceptor.
 */
@RestController
@RequestMapping("/api/pedidos")
public class PedidoApiController {

    @Autowired
    private PedidoService pedidoService;

    // READ (um pedido específico, usado ao entrar em modo de edição)
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPedido(@PathVariable("id") String id) {
        try {
            Pedido pedido = pedidoService.listarPedidos(id);
            if (pedido == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(erro("Pedido não encontrado."));
            }
            return ResponseEntity.ok(pedido);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(erro("Pedido não encontrado."));
        }
    }

    // UPDATE (edição inline)
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarPedido(@PathVariable("id") String id, @RequestBody Pedido dadosRecebidos) {
        try {
            if (dadosRecebidos.getNomeCliente() == null || dadosRecebidos.getNomeCliente().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(erro("O nome do cliente é obrigatório."));
            }
            if (dadosRecebidos.getTelefone() == null || dadosRecebidos.getTelefone().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(erro("O telefone é obrigatório."));
            }

            pedidoService.atualizarPedido(dadosRecebidos, id);
            Pedido atualizado = pedidoService.listarPedidos(id);
            return ResponseEntity.ok(atualizado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(erro("Não foi possível atualizar o pedido."));
        }
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarPedido(@PathVariable("id") String id) {
        try {
            pedidoService.deletarPedido(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(erro("Não foi possível excluir o pedido."));
        }
    }

    private Map<String, String> erro(String mensagem) {
        Map<String, String> body = new HashMap<>();
        body.put("erro", mensagem);
        return body;
    }
}
