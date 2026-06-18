package com.example.sitetrufas.model;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoDAO produtoDAO;

    @Autowired
    private ImagemProdutoDAO imagemProdutoDAO;

    // Pasta física onde as imagens enviadas pelo administrador são salvas.
    // Configurável via variável de ambiente UPLOAD_DIR; por padrão usa uma
    // pasta "uploads/produtos" ao lado do jar em execução. Essa pasta é
    // exposta publicamente em /img/produtos/** (ver WebConfig).
    @Value("${app.upload-dir:uploads/produtos}")
    private String diretorioUpload;

    public List<Produto> listarProdutos() {
        return produtoDAO.listarTodos();
    }

    public List<ImagemProduto> listarImagensDaBiblioteca() {
        return imagemProdutoDAO.listarTodas();
    }

    public void cadastrarProduto(String nome, String descricao, BigDecimal preco, String imagemId) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do produto é obrigatório.");
        }
        if (preco == null || preco.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O preço deve ser maior que zero.");
        }
        produtoDAO.cadastrarProduto(nome.trim(), descricao, preco, imagemId);
    }

    /**
     * Salva um novo arquivo de imagem enviado pelo administrador na pasta
     * de uploads e registra a entrada correspondente na biblioteca de imagens.
     */
    public ImagemProduto enviarNovaImagem(MultipartFile arquivo) {
        if (arquivo == null || arquivo.isEmpty()) {
            throw new IllegalArgumentException("Selecione um arquivo de imagem para enviar.");
        }

        String nomeOriginal = arquivo.getOriginalFilename();
        String extensao = "";
        if (nomeOriginal != null && nomeOriginal.contains(".")) {
            extensao = nomeOriginal.substring(nomeOriginal.lastIndexOf('.'));
        }

        String nomeArmazenado = UUID.randomUUID().toString() + extensao;

        try {
            Path diretorio = Paths.get(diretorioUpload);
            Files.createDirectories(diretorio);

            Path destino = diretorio.resolve(nomeArmazenado);
            Files.copy(arquivo.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Não foi possível salvar a imagem enviada.", e);
        }

        String caminhoPublico = "/img/produtos/" + nomeArmazenado;
        return imagemProdutoDAO.registrarImagem(nomeOriginal != null ? nomeOriginal : nomeArmazenado, caminhoPublico);
    }
}
