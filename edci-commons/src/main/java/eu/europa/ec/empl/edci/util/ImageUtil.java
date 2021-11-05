package eu.europa.ec.empl.edci.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import eu.europa.ec.empl.edci.config.service.IConfigService;
import eu.europa.ec.empl.edci.constants.ControlledList;
import eu.europa.ec.empl.edci.constants.ControlledListConcept;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Code;
import eu.europa.ec.empl.edci.service.ControlledListCommonsService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ImageUtil {

    @Autowired
    ControlledListCommonsService controlledListCommonsService;

    protected static final Logger logger = Logger.getLogger(ImageUtil.class);

    //Height and width in PX
    public static int LOGO_HEIGHT = 200;
    public static int LOGO_WIDTH = 0; //Proporcional to height

    public static int BACKGROUND_HEIGHT = 1754; //For 150 ppi
    public static int BACKGROUND_WIDTH = 0; //Proporcional to height

    @Autowired
    private IConfigService configService;

//    protected final static Pattern mimeType = Pattern.compile("^data:([a-zA-Z0-9]+/([a-zA-Z0-9]+)).*,.*");
//
//    public BufferedImage decodeToImage(String base64Image) {
//
//        BufferedImage image = null;
//        byte[] imageByte;
//        try {
//            BASE64Decoder decoder = new BASE64Decoder();
//            imageByte = decoder.decodeBuffer(base64Image);
//            try (ByteArrayInputStream bis = new ByteArrayInputStream(imageByte)) {
//                image = ImageIO.read(bis);
//            }
//        } catch (Exception e) {
//            logger.error(e);
//        }
//        return image;
//    }
//
//    public String encodeToString(BufferedImage image, String type) {
//        String imageString = null;
//
//        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
//            ImageIO.write(image, type, bos);
//            byte[] imageBytes = bos.toByteArray();
//
//            BASE64Encoder encoder = new BASE64Encoder();
//            imageString = encoder.encode(imageBytes);
//
//        } catch (IOException e) {
//            logger.error(e);
//        }
//        return imageString;
//    }

//    public String getMimeType(String base64Image) {
//        final Matcher matcher = mimeType.matcher(base64Image);
//        if (!matcher.find()) {
//            return null;
//        }
//        return matcher.group(1).toLowerCase();
//    }
//
//    public String getType(String base64Image) {
//        final Matcher matcher = mimeType.matcher(base64Image);
//        if (!matcher.find()) {
//            return null;
//        }
//        return matcher.group(2).toLowerCase();
//    }
//
//    public String resizeImage(String base64Image, int targetWidth, int targetHeight) throws IOException {
//        BufferedImage img = decodeToImage(base64Image);
//        BufferedImage resizedImg = resizeImage(img, targetWidth, targetHeight);
//        return encodeToString(resizedImg, getType(base64Image));
//    }

    public String getImageDownloadUrl() {
        return getConfigService().getString("png.download.url");
    }

    /**
     * Generates an image in JPG format from a given HTML usign the defined in "png.download.url" property.
     * The process generates a PDF from the HTML and then a PNG from the PDF. After this the PNG is converted to a JPG to use less disk when stored.
     * @param html html used for the image generation
     * @param size Size of the Image generated. If the size is smaller than the resulting image, this one will be split into different pages
     * @param margin image margins
     * @return
     * @throws IOException
     */
    public byte[] htmlToImage(String html, String size, String margin) throws IOException {

        String header = "<head>\n" +
                " <style type=\"text/css\"> \n" +
                "     @page { size: " + size + "; margin: " + margin + "; } \n" +
                "</style>\n" +
                "</head>\n";

        String imageDownloadURL = getImageDownloadUrl();

        byte[] bytes = null;

        if (imageDownloadURL != null && !imageDownloadURL.isEmpty()) {
            bytes = new EDCIRestRequestBuilder(HttpMethod.POST, imageDownloadURL)
                    .addHeaderRequestedWith()
                    .addHeaders(MediaType.MULTIPART_FORM_DATA, MediaType.IMAGE_PNG)
                    .addBody(EDCIRestRequestBuilder.prepareMultiPartStringBody("html", header + html, new HashMap<>()))
                    .buildRequest(byte[].class)
                    .execute();
        }

        byte[] jpegImage = null;
        try (ByteArrayInputStream in = new ByteArrayInputStream(bytes); ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
            BufferedImage image = ImageIO.read(in);
            BufferedImage result = new BufferedImage(
                    image.getWidth(),
                    image.getHeight(),
                    BufferedImage.TYPE_INT_RGB);
            result.createGraphics().drawImage(image, 0, 0, Color.WHITE, null);
            ImageIO.write(result, "jpg", buffer);
            jpegImage = buffer.toByteArray();
        }

        return jpegImage;

    }


    public BufferedImage resizeImage(BufferedImage originalImage, int targetHeightpx, int targetWidthpx) throws IOException {

        Dimension finalDimension = getScaledDimension(new Dimension(originalImage.getWidth(), originalImage.getHeight()), new Dimension(targetWidthpx, targetHeightpx));

        BufferedImage resizedImage = new BufferedImage((int) finalDimension.getWidth(), (int) finalDimension.getHeight(), originalImage.getColorModel().hasAlpha() ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.drawImage(originalImage, 0, 0, (int) finalDimension.getWidth(), (int) finalDimension.getHeight(), null);
        graphics2D.dispose();
        return resizedImage;
    }

    public byte[] resizeImage(byte[] imageData, String type, int targetHeightpx, int targetWidthpx) throws IOException {

        BufferedImage buffImg = null;
        try (ByteArrayInputStream in = new ByteArrayInputStream(imageData); ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
            BufferedImage img = ImageIO.read(in);
            buffImg = resizeImage(img, targetHeightpx, targetWidthpx);

            ImageIO.write(buffImg, type.toLowerCase(), buffer);

            return buffer.toByteArray();
        }

    }

    public static Dimension getScaledDimension(Dimension imgSize, Dimension boundary) {

        int original_width = imgSize.width;
        int original_height = imgSize.height;
        int bound_width = boundary.width > 0 ? boundary.width : imgSize.width;
        int bound_height = boundary.height;
        int new_width = original_width;
        int new_height = original_height;

        // first check if we need to scale width
//        if (original_width > bound_width) {
//            //scale width to fit
//            new_width = bound_width;
//            //scale height to maintain aspect ratio
//            new_height = (new_width * original_height) / original_width;
//        }

        // then check if we need to scale even with the new height
        if (new_height > bound_height) {
            //scale height to fit instead
            new_height = bound_height;
            //scale width to maintain aspect ratio
            new_width = (new_height * original_width) / original_height;
        }

        return new Dimension(new_width, new_height);
    }

    public BufferedImage generateQRCodeImage(String barcodeText) throws Exception {
        QRCodeWriter barcodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix =
                barcodeWriter.encode(barcodeText, BarcodeFormat.QR_CODE, 200, 200);

        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }

    public byte[] generateQRCodeImageBytes(String barcodeText, String format) throws Exception {

        BufferedImage image = generateQRCodeImage(barcodeText);

        byte[] returnValue = null;
        try (ByteArrayOutputStream bao = new ByteArrayOutputStream()) {
            ImageIO.write(image, format, bao);
            returnValue = bao.toByteArray();
        }

        return returnValue;

    }

    public Code getBase64Encoding() {
        return controlledListCommonsService.searchConceptByUri(ControlledList.ENCODING.getUrl(), ControlledListConcept.ENCODING_BASE64.getUrl(), LocaleContextHolder.getLocale().toString());
    }

    protected IConfigService getConfigService() {
        return configService;
    }

}
