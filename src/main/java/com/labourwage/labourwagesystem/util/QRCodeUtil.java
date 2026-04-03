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

        // Build payload
        String payload = String.format(
                "{\"workerId\":\"%s\",\"workerCode\":\"%s\"," +
                        "\"siteId\":\"%s\",\"issuedAt\":\"%s\"}",
                workerId, workerCode, siteId,
                Instant.now().toString()
        );

        // Sign with HMAC-SHA256
        String signature = new HmacUtils(
                HmacAlgorithms.HMAC_SHA_256, secretKey)
                .hmacHex(payload);

        String signedPayload = payload + "." + signature;

        // Generate QR using ZXing
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
        String expected = new HmacUtils(
                HmacAlgorithms.HMAC_SHA_256, secretKey)
                .hmacHex(payload);
        return expected.equals(signature);
    }
}