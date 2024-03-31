package qrcodeapi.services;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.awt.image.BufferedImage;
import java.util.Map;

public class QRCodeService {
    private static final QRCodeWriter writer = new QRCodeWriter();

    public static BufferedImage generateQRCode(String data, int size, String correction) {
        ErrorCorrectionLevel errorCorrectionLevel = switch (correction) {
            case "M" -> ErrorCorrectionLevel.M;
            case "Q" -> ErrorCorrectionLevel.Q;
            case "H" -> ErrorCorrectionLevel.H;
            default -> ErrorCorrectionLevel.L;
        };
        Map<EncodeHintType, ?> hints = Map.of(EncodeHintType.ERROR_CORRECTION, errorCorrectionLevel);
        try {
            BitMatrix bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, size, size, hints);
            return MatrixToImageWriter.toBufferedImage(bitMatrix);
        } catch (WriterException e) {
            return null;
        }
    }

}
