package com.api_orcafacil.service.logo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.api_orcafacil.exception.BusinessException;

@Service
public class LogoArmazenamentoLocalService {

    private final Path baseDir;

    public LogoArmazenamentoLocalService(@Value("${app.upload.base-dir:uploads}") String baseDir) {
        this.baseDir = Paths.get(baseDir).toAbsolutePath().normalize();
    }

    public String salvar(Long idOrganizacao, byte[] bytes, String extensao) throws IOException {
        String nomeSalvo = "logo-" + UUID.randomUUID() + "." + extensao;
        String caminhoRelativo = "organizacoes/" + idOrganizacao + "/logo/" + nomeSalvo;
        Path destino = resolverCaminhoSeguro(idOrganizacao, caminhoRelativo);
        Files.createDirectories(destino.getParent());
        Files.write(destino, bytes);
        return caminhoRelativo;
    }

    public byte[] ler(String caminhoRelativo, Long idOrganizacao) throws IOException {
        Path arquivo = resolverCaminhoSeguro(idOrganizacao, caminhoRelativo);
        if (!Files.exists(arquivo) || !Files.isRegularFile(arquivo)) {
            throw new BusinessException("Arquivo da logo nao encontrado");
        }
        return Files.readAllBytes(arquivo);
    }

    public Path caminhoAbsoluto(String caminhoRelativo, Long idOrganizacao) {
        return resolverCaminhoSeguro(idOrganizacao, caminhoRelativo);
    }

    public void remover(String caminhoRelativo, Long idOrganizacao) throws IOException {
        Path arquivo = resolverCaminhoSeguro(idOrganizacao, caminhoRelativo);
        if (Files.exists(arquivo)) {
            Files.delete(arquivo);
        }
        Path pastaLogo = arquivo.getParent();
        if (pastaLogo != null && Files.isDirectory(pastaLogo) && isDiretorioVazio(pastaLogo)) {
            Files.delete(pastaLogo);
        }
    }

    private Path resolverCaminhoSeguro(Long idOrganizacao, String caminhoRelativo) {
        if (caminhoRelativo == null || caminhoRelativo.isBlank()) {
            throw new BusinessException("Caminho da logo invalido");
        }
        String normalizado = caminhoRelativo.replace("\\", "/");
        if (normalizado.contains("..") || normalizado.startsWith("/")) {
            throw new BusinessException("Caminho da logo invalido");
        }
        String prefixoEsperado = "organizacoes/" + idOrganizacao + "/logo/";
        if (!normalizado.startsWith(prefixoEsperado)) {
            throw new BusinessException("Caminho da logo invalido para a organizacao");
        }
        Path resolvido = baseDir.resolve(normalizado).normalize();
        if (!resolvido.startsWith(baseDir)) {
            throw new BusinessException("Caminho da logo invalido");
        }
        return resolvido;
    }

    private boolean isDiretorioVazio(Path diretorio) throws IOException {
        try (var stream = Files.list(diretorio)) {
            return stream.findAny().isEmpty();
        }
    }
}
