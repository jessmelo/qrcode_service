package qrcodeapi;

import io.micrometer.common.util.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import qrcodeapi.models.ErrorResponse;
import qrcodeapi.services.QRCodeService;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RestController
class QRCodeController {

    @GetMapping("/api/health")
    ResponseEntity<Object> health() {
        return ResponseEntity.status(200).body(null);
    }

    @GetMapping("/api/qrcode")
    ResponseEntity<Object> generateQRCode(@RequestParam(value = "contents", defaultValue = "") String contents,
                                          @RequestParam(value = "correction", defaultValue = "L", required = false) String correction,
                                          @RequestParam(value = "size", defaultValue = "250", required = false) int size,
                                          @RequestParam(value = "type", defaultValue = "png", required = false) String type) {

        if (contents == null || contents.isEmpty() || StringUtils.isBlank(contents)) {
            return ResponseEntity
                    .status(400)
                    .body(new ErrorResponse("Contents cannot be null or blank"));
        }
        if (size >= 150 && size <= 350) {
            String allowedCorrections = "L,M,Q,H";
            if (correction != null && !allowedCorrections.contains(correction)) {
                return ResponseEntity
                        .status(400)
                        .body(new ErrorResponse("Permitted error correction levels are L, M, Q, H"));
            } else if (correction == null) {
                correction = "L";
            }

            String allowedTypes = "png,jpeg,gif";
            if (!allowedTypes.contains(type)) {
                return ResponseEntity
                        .status(400)
                        .body(new ErrorResponse("Only png, jpeg and gif image types are supported"));
            }
            BufferedImage image = QRCodeService.generateQRCode(contents, size, correction);
            if (image == null) {
                return ResponseEntity.status(500).body(
                        new ErrorResponse("Failed to generate QR code"));
            }
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                MediaType mediaType = MediaType.IMAGE_PNG;
                switch (type) {
                    case "png":
                        ImageIO.write(image, "png", baos);
                        break;
                    case "jpeg":
                        ImageIO.write(image, "jpeg", baos);
                        mediaType = MediaType.IMAGE_JPEG;
                        break;
                    case "gif":
                        ImageIO.write(image, "gif", baos);
                        mediaType = MediaType.IMAGE_GIF;
                        break;
                }
                byte[] imageBytes = baos.toByteArray();
                return ResponseEntity.status(200).contentType(mediaType).body(imageBytes);
            } catch (IOException e) {
                return ResponseEntity.status(500).body(null);
            }
        } else {
            return ResponseEntity
                    .status(400)
                    .body(new ErrorResponse("Image size must be between 150 and 350 pixels"));
        }
    }
}