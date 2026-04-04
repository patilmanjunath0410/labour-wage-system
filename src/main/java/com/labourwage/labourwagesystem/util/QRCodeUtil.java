package com.labourwage.labourwagesystem.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.io.ByteArrayOutputStream;
import java.time.Instant;
import java.util.Map;

@Component
public class QRCodeUtil {

    @Value("${qr.secret.key}")
    private String secretKey;

    public byte[] generateQR(String workerId,
                             String workerCode,
                             String siteId) throws Exception {

        String payload = buildPayload(
                workerId, workerCode, siteId,
                Instant.now().toString());

        String signature = sign(payload);
        String signedPayload = payload + "." + signature;

        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix matrix = writer.encode(
                signedPayload,
                BarcodeFormat.QR_CODE,
                400, 400,
                Map.of(EncodeHintType.ERROR_CORRECTION,
                        ErrorCorrectionLevel.H)
        );

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(matrix, "PNG", out);
        return out.toByteArray();
    }

    public boolean verifyQR(String payload,
                            String signature) {
        String expected = sign(payload);
        System.out.println("Expected sig: " + expected);
        System.out.println("Received sig: " + signature);
        return expected.equals(signature);
    }

    public String buildSignedPayload(String workerId,
                                     String workerCode,
                                     String siteId,
                                     String issuedAt) {
        String payload = buildPayload(
                workerId, workerCode, siteId, issuedAt);
        return payload + "." + sign(payload);
    }

    private String buildPayload(String workerId,
                                String workerCode,
                                String siteId,
                                String issuedAt) {
        return String.format(
                "{\"workerId\":\"%s\",\"workerCode\":\"%s\"," +
                        "\"siteId\":\"%s\",\"issuedAt\":\"%s\"}",
                workerId, workerCode, siteId, issuedAt);
    }

    private String sign(String payload) {
        return new HmacUtils(
                HmacAlgorithms.HMAC_SHA_256, secretKey)
                .hmacHex(payload);
    }
}