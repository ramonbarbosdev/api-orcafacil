package com.api_orcafacil.service.logo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.api_orcafacil.exception.BusinessException;

class LogoImagemValidacaoServiceTest {

    private final LogoImagemValidacaoService service = new LogoImagemValidacaoService();

    @Test
    void deveAceitarPngValido() throws Exception {
        byte[] bytes = criarPng(400, 120);
        MockMultipartFile file = new MockMultipartFile("file", "logo.png", "image/png", bytes);

        LogoImagemValidacaoService.ResultadoValidacao resultado = service.validar(file);

        assertEquals("png", resultado.extensao());
        assertEquals("image/png", resultado.contentType());
        assertEquals(400, resultado.largura());
        assertEquals(120, resultado.altura());
    }

    @Test
    void deveRejeitarArquivoVazio() {
        MockMultipartFile file = new MockMultipartFile("file", "logo.png", "image/png", new byte[0]);
        assertThrows(BusinessException.class, () -> service.validar(file));
    }

    @Test
    void deveRejeitarProporcaoInvalida() throws Exception {
        byte[] bytes = criarPng(1200, 100);
        MockMultipartFile file = new MockMultipartFile("file", "logo.png", "image/png", bytes);
        BusinessException ex = assertThrows(BusinessException.class, () -> service.validar(file));
        assertEquals("Proporcao invalida. Envie uma logo horizontal ou proporcional (entre 1:1 e 5:1)", ex.getMessage());
    }

    @Test
    void deveRejeitarExtensaoFalsa() throws Exception {
        byte[] bytes = criarPng(400, 120);
        MockMultipartFile file = new MockMultipartFile("file", "logo.pdf", "application/pdf", bytes);
        assertThrows(BusinessException.class, () -> service.validar(file));
    }

    @Test
    void deveRejeitarSvg() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getSize()).thenReturn(100L);
        when(file.getOriginalFilename()).thenReturn("logo.svg");
        assertThrows(BusinessException.class, () -> service.validar(file));
    }

    private byte[] criarPng(int largura, int altura) throws Exception {
        BufferedImage imagem = new BufferedImage(largura, altura, BufferedImage.TYPE_INT_RGB);
        var grafico = imagem.createGraphics();
        grafico.setColor(Color.WHITE);
        grafico.fillRect(0, 0, largura, altura);
        grafico.dispose();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(imagem, "png", out);
        return out.toByteArray();
    }
}
