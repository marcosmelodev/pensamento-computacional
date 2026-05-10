package com.udfilasystem.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorConfig;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class TotpService {

    @Value("${app.totp.issuer:udfilasystem}")
    private String issuer;

    @Value("${app.totp.window:1}")
    private int window;

    private GoogleAuthenticator buildAuthenticator() {
        GoogleAuthenticatorConfig config = new GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder()
                .setTimeStepSizeInMillis(TimeUnit.SECONDS.toMillis(30))
                .setWindowSize(window * 2 + 1)
                .build();
        return new GoogleAuthenticator(config);
    }

    /**
     * Gera um novo segredo TOTP base32 para o usuario.
     */
    public String gerarSegredo() {
        GoogleAuthenticatorKey key = buildAuthenticator().createCredentials();
        return key.getKey();
    }

    /**
     * Verifica se o codigo TOTP informado e valido para o segredo.
     */
    public boolean verificarCodigo(String segredo, int codigo) {
        try {
            return buildAuthenticator().authorize(segredo, codigo);
        } catch (Exception e) {
            log.warn("Erro ao verificar codigo TOTP: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Gera a URL otpauth:// para exibicao no QR Code.
     */
    public String gerarUrlOtpAuth(String email, String segredo) {
        String encodedIssuer  = URLEncoder.encode(issuer, StandardCharsets.UTF_8);
        String encodedAccount = URLEncoder.encode(email, StandardCharsets.UTF_8);
        return String.format(
                "otpauth://totp/%s:%s?secret=%s&issuer=%s&algorithm=SHA1&digits=6&period=30",
                encodedIssuer, encodedAccount, segredo, encodedIssuer
        );
    }

    /**
     * Gera o QR Code em Base64 PNG para exibicao no frontend.
     */
    public String gerarQrCodeBase64(String email, String segredo) {
        String otpUrl = gerarUrlOtpAuth(email, segredo);
        try {
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix matrix  = writer.encode(otpUrl, BarcodeFormat.QR_CODE, 300, 300);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(matrix, "PNG", baos);
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (Exception e) {
            log.error("Erro ao gerar QR Code: {}", e.getMessage(), e);
            throw new RuntimeException("Nao foi possivel gerar o QR Code", e);
        }
    }
}
