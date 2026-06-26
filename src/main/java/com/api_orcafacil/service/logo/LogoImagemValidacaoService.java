package com.api_orcafacil.service.logo;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.api_orcafacil.exception.BusinessException;

@Service
public class LogoImagemValidacaoService {

    static final long TAMANHO_MAXIMO_BYTES = 2L * 1024 * 1024;
    static final int LARGURA_MINIMA = 200;
    static final int ALTURA_MINIMA = 80;
    static final int LARGURA_MAXIMA = 2000;
    static final int ALTURA_MAXIMA = 1000;
    static final double PROPORCAO_MINIMA = 1.0;
    static final double PROPORCAO_MAXIMA = 5.0;

    private static final Set<String> EXTENSOES_PERMITIDAS = Set.of("png", "jpg", "jpeg", "webp");
    private static final Set<String> MIME_PERMITIDOS = Set.of("image/png", "image/jpeg", "image/webp");
    private static final Map<String, String> MIME_POR_EXTENSAO = Map.of(
            "png", "image/png",
            "jpg", "image/jpeg",
            "jpeg", "image/jpeg",
            "webp", "image/webp");

    public ResultadoValidacao validar(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("Arquivo da logo e obrigatorio");
        }
        if (file.getSize() > TAMANHO_MAXIMO_BYTES) {
            throw new BusinessException("A logo deve ter no maximo 2 MB");
        }

        String extensao = extrairExtensaoSegura(file.getOriginalFilename());
        if (!EXTENSOES_PERMITIDAS.contains(extensao)) {
            throw new BusinessException("Formato nao permitido. Envie PNG, JPG ou WEBP");
        }

        byte[] bytes = file.getBytes();
        String mimeDetectado = detectarMime(bytes);
        if (mimeDetectado == null || !MIME_PERMITIDOS.contains(mimeDetectado)) {
            throw new BusinessException("Tipo de arquivo nao permitido ou arquivo invalido");
        }

        String mimeEsperado = MIME_POR_EXTENSAO.get(extensao);
        if (!mimeDetectado.equals(mimeEsperado)) {
            throw new BusinessException("A extensao do arquivo nao corresponde ao conteudo real da imagem");
        }

        BufferedImage imagem = ImageIO.read(new ByteArrayInputStream(bytes));
        if (imagem == null) {
            throw new BusinessException("Arquivo de imagem invalido ou corrompido");
        }

        int largura = imagem.getWidth();
        int altura = imagem.getHeight();
        if (largura < LARGURA_MINIMA || altura < ALTURA_MINIMA) {
            throw new BusinessException(
                    "A logo e muito pequena. Envie uma imagem com pelo menos " + LARGURA_MINIMA + "x" + ALTURA_MINIMA + " pixels");
        }
        if (largura > LARGURA_MAXIMA || altura > ALTURA_MAXIMA) {
            throw new BusinessException(
                    "A logo e muito grande. Envie uma imagem com no maximo " + LARGURA_MAXIMA + "x" + ALTURA_MAXIMA + " pixels");
        }

        double proporcao = (double) largura / altura;
        if (proporcao < PROPORCAO_MINIMA || proporcao > PROPORCAO_MAXIMA) {
            throw new BusinessException(
                    "Proporcao invalida. Envie uma logo horizontal ou proporcional (entre 1:1 e 5:1)");
        }

        return new ResultadoValidacao(bytes, extensao, mimeDetectado, largura, altura);
    }

    private String extrairExtensaoSegura(String nomeOriginal) {
        if (nomeOriginal == null || nomeOriginal.isBlank()) {
            throw new BusinessException("Nome do arquivo invalido");
        }
        String nome = nomeOriginal.replace("\\", "/");
        int barra = nome.lastIndexOf('/');
        if (barra >= 0) {
            nome = nome.substring(barra + 1);
        }
        int ponto = nome.lastIndexOf('.');
        if (ponto < 0 || ponto == nome.length() - 1) {
            throw new BusinessException("Arquivo sem extensao valida");
        }
        String extensao = nome.substring(ponto + 1).toLowerCase(Locale.ROOT).trim();
        if (!extensao.matches("[a-z0-9]+")) {
            throw new BusinessException("Extensao de arquivo invalida");
        }
        return extensao;
    }

    private String detectarMime(byte[] bytes) {
        if (bytes.length < 12) {
            return null;
        }
        if (bytes[0] == (byte) 0x89 && bytes[1] == 0x50 && bytes[2] == 0x4E && bytes[3] == 0x47) {
            return "image/png";
        }
        if ((bytes[0] & 0xFF) == 0xFF && (bytes[1] & 0xFF) == 0xD8 && (bytes[2] & 0xFF) == 0xFF) {
            return "image/jpeg";
        }
        if (bytes[0] == 'R' && bytes[1] == 'I' && bytes[2] == 'F' && bytes[3] == 'F'
                && bytes[8] == 'W' && bytes[9] == 'E' && bytes[10] == 'B' && bytes[11] == 'P') {
            return "image/webp";
        }
        return null;
    }

    public record ResultadoValidacao(
            byte[] bytes,
            String extensao,
            String contentType,
            int largura,
            int altura) {
    }
}
