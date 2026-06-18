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

    public Produto buscarProduto(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Produto não informado.");
        }
        return produtoDAO.buscarPorId(id);
    }

    public void atualizarProduto(String id, String nome, String descricao, BigDecimal preco, String imagemId) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Produto não informado.");
        }
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do produto é obrigatório.");
        }
        if (preco == null || preco.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O preço deve ser maior que zero.");
        }
        produtoDAO.atualizarProduto(id, nome.trim(), descricao, preco, imagemId);
    }

    public void excluirProduto(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Produto não informado.");
        }
        produtoDAO.excluirProduto(id);
    }

    /**
     * Exclui uma imagem da biblioteca. Não permite excluir se algum produto
     * ainda estiver usando a imagem. Também remove o arquivo físico, quando
     * ele estiver na pasta de uploads (imagens enviadas pelo administrador).
     */
    public void excluirImagem(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Imagem não informada.");
        }

        int emUso = imagemProdutoDAO.contarProdutosUsando(id);
        if (emUso > 0) {
            throw new IllegalArgumentException(
                "Esta imagem está sendo usada por " + emUso + " produto(s). "
                + "Altere ou exclua esses produtos antes de remover a imagem.");
        }

        ImagemProduto imagem = imagemProdutoDAO.buscarPorId(id);
        imagemProdutoDAO.excluirImagem(id);

        // Remove o arquivo físico apenas se for um upload (pasta /img/produtos/).
        // As imagens originais do projeto (em static/img) não são apagadas do disco.
        if (imagem != null && imagem.getCaminho() != null
                && imagem.getCaminho().startsWith("/img/produtos/")) {
            try {
                String nomeArquivo = imagem.getCaminho().substring("/img/produtos/".length());
                Path arquivo = Paths.get(diretorioUpload).resolve(nomeArquivo);
                Files.deleteIfExists(arquivo);
            } catch (IOException e) {
                // O registro no banco já foi removido; falha ao apagar o arquivo
                // físico não deve quebrar a operação. Apenas ignora.
            }
        }
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