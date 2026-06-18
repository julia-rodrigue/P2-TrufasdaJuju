package com.example.sitetrufas.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.sitetrufas.config.AutenticacaoInterceptor;
import com.example.sitetrufas.model.Usuario;
import com.example.sitetrufas.model.UsuarioService;

@Controller
public class AutenticacaoController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/cadastro")
    public String formCadastro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "cadastro";
    }

    @PostMapping("/cadastro")
    public String cadastrar(@ModelAttribute Usuario usuario,
                             @RequestParam("senha") String senha,
                             Model model) {
        try {
            usuarioService.cadastrarCliente(usuario, senha);
            model.addAttribute("sucesso", "Cadastro realizado com sucesso! Faça login para continuar.");
            return "login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("usuario", usuario);
            return "cadastro";
        } catch (Exception e) {
            model.addAttribute("erro", "Não foi possível concluir o cadastro. Tente novamente.");
            model.addAttribute("usuario", usuario);
            return "cadastro";
        }
    }

    @GetMapping("/login")
    public String formLogin() {
        return "login";
    }

    @PostMapping("/login")
    public String autenticar(@RequestParam("identificador") String identificador,
                              @RequestParam("senha") String senha,
                              HttpServletRequest request,
                              Model model) {
        Usuario usuario = usuarioService.autenticar(identificador, senha);

        if (usuario == null) {
            model.addAttribute("erro", "E-mail/CPF ou senha incorretos.");
            return "login";
        }

        request.getSession(true).setAttribute(AutenticacaoInterceptor.ATRIBUTO_SESSAO_USUARIO, usuario);
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String sair(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/login";
    }
}
