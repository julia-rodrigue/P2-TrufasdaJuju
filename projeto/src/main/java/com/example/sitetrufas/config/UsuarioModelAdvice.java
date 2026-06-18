package com.example.sitetrufas.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.example.sitetrufas.model.Usuario;

/**
 * Disponibiliza o usuário logado (ou null) para todos os templates
 * Thymeleaf através do atributo "usuarioLogado", sem que cada
 * controller precise adicioná-lo manualmente ao Model.
 */
@ControllerAdvice
public class UsuarioModelAdvice {

    @ModelAttribute("usuarioLogado")
    public Usuario usuarioLogado(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        return (Usuario) session.getAttribute(AutenticacaoInterceptor.ATRIBUTO_SESSAO_USUARIO);
    }
}
