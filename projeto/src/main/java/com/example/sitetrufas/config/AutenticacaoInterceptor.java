package com.example.sitetrufas.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.web.servlet.HandlerInterceptor;

import com.example.sitetrufas.model.Usuario;

public class AutenticacaoInterceptor implements HandlerInterceptor {

    public static final String ATRIBUTO_SESSAO_USUARIO = "usuarioLogado";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String caminho = request.getRequestURI();

        boolean exigeAdministrador = caminho.startsWith("/admin")
                || caminho.startsWith("/api/admin")
                || caminho.startsWith("/api/pedidos");

        boolean exigeLogin = exigeAdministrador
                || caminho.equals("/pedidos")
                || caminho.startsWith("/pedidos")
                || caminho.equals("/meus-pedidos")
                || caminho.startsWith("/cliente/");

        if (!exigeLogin) {
            return true;
        }

        HttpSession session = request.getSession(false);
        Usuario usuario = session != null ? (Usuario) session.getAttribute(ATRIBUTO_SESSAO_USUARIO) : null;

        if (usuario == null) {
            if (caminho.startsWith("/api")) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "É necessário estar logado.");
            } else {
                response.sendRedirect("/login");
            }
            return false;
        }

        if (exigeAdministrador && !usuario.isAdministrador()) {
            if (caminho.startsWith("/api")) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acesso restrito ao administrador.");
            } else {
                response.sendRedirect("/");
            }
            return false;
        }

        return true;
    }
}
