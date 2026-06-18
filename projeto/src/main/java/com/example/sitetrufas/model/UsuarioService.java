package com.example.sitetrufas.model;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.sitetrufas.util.HashUtil;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioDAO usuarioDAO;

    /**
     * Cadastra um novo cliente. Lança IllegalArgumentException com uma
     * mensagem amigável quando os dados são inválidos ou já existe
     * um usuário com o mesmo e-mail/CPF.
     */
    public void cadastrarCliente(Usuario usuario, String senhaPura) {
        validarCamposObrigatorios(usuario, senhaPura);

        if (usuarioDAO.existeEmailOuCpf(usuario.getEmail(), usuario.getCpf())) {
            throw new IllegalArgumentException("Já existe um cadastro com este e-mail ou CPF.");
        }

        String senhaHash = HashUtil.sha256(senhaPura);
        usuarioDAO.cadastrarUsuario(usuario, senhaHash);
    }

    /**
     * Valida as credenciais informadas (e-mail ou CPF + senha).
     * Retorna o Usuario autenticado (sem dados sensíveis) ou null
     * se as credenciais forem inválidas.
     */
    public Usuario autenticar(String identificador, String senhaPura) {
        if (identificador == null || identificador.trim().isEmpty() || senhaPura == null || senhaPura.isEmpty()) {
            return null;
        }

        Map<String, Object> registro = usuarioDAO.buscarParaLogin(identificador.trim());
        if (registro == null) {
            return null;
        }

        String hashArmazenado = (String) registro.get("senha_hash");
        String hashInformado = HashUtil.sha256(senhaPura);

        if (!hashArmazenado.equals(hashInformado)) {
            return null;
        }

        return Usuario.converter(registro);
    }

    public Usuario buscarPorId(String id) {
        return usuarioDAO.buscarPorId(id);
    }

    private void validarCamposObrigatorios(Usuario usuario, String senhaPura) {
        if (vazio(usuario.getNome()))           throw new IllegalArgumentException("O nome é obrigatório.");
        if (vazio(usuario.getEmail()))          throw new IllegalArgumentException("O e-mail é obrigatório.");
        if (vazio(usuario.getCpf()))            throw new IllegalArgumentException("O CPF é obrigatório.");
        if (vazio(usuario.getTelefone()))       throw new IllegalArgumentException("O telefone é obrigatório.");
        if (vazio(usuario.getDataNascimento())) throw new IllegalArgumentException("A data de nascimento é obrigatória.");
        if (senhaPura == null || senhaPura.length() < 6) {
            throw new IllegalArgumentException("A senha deve ter no mínimo 6 caracteres.");
        }
    }

    private boolean vazio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }
}
