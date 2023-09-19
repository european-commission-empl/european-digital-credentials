package eu.europa.ec.empl.edci.util;

import eu.europa.ec.empl.base.AbstractUnitBaseTest;
import eu.europa.ec.empl.edci.config.service.BaseConfigService;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;

public class ImageUtilTest extends AbstractUnitBaseTest {

    @Mock
    BaseConfigService configService;

    @InjectMocks
    private ImageUtil imageUtil;

    @Test
    public void resizeImage_shouldResizeImage_whenABufferedImageIsPassed_1() throws Exception {

        //image dimension
        int width = 500;
        int height = 1000;
        //create buffered image object img
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        BufferedImage resizedImg = imageUtil.resizeImage(img, 200, 0);

        System.out.println("Orginal height: " + height + " Width: " + width);
        System.out.println("Resized height: " + resizedImg.getHeight() + " Width: " + resizedImg.getWidth());
        Assert.assertTrue(resizedImg.getHeight() <= height);
        Assert.assertTrue(resizedImg.getWidth() <= width);

    }

   /* @Test
    public void htmlToImage_shouldReturnAnImage_givenAnHTML() throws Exception {

        Mockito.doReturn("https://dgempl-single-portal-demo-1.arhs-developments.com/europass/eportfolio/api/office/generate/png")
                .when(configService).getString("png.download.url");

        //GEt this image in DinA4 format
        byte[] imageData = imageUtil.htmlToImage("<div style=\"height:300px;background:red;\">TEST</div>",
                EDCIConfig.Defaults.DIPLOMA_PAGE_SIZE, EDCIConfig.Defaults.DIPLOMA_PAGE_MARGINS);

        try (ByteArrayInputStream in = new ByteArrayInputStream(imageData); ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
            BufferedImage img = ImageIO.read(in);

            Assert.assertEquals(img.getHeight(), 1123);
            Assert.assertEquals(img.getWidth(), 794);
        }

    }*/

    @Test
    public void resizeImage_shouldResizeImage_whenABufferedImageIsPassed_2() throws Exception {

        //image dimension
        int width = 500;
        int height = 1000;
        //create buffered image object img
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        BufferedImage resizedImg = imageUtil.resizeImage(img, 200, 150);

        System.out.println("Orginal height: " + height + " Width: " + width);
        System.out.println("Resized height: " + resizedImg.getHeight() + " Width: " + resizedImg.getWidth());
        Assert.assertTrue(resizedImg.getHeight() <= height);
        Assert.assertTrue(resizedImg.getWidth() <= width);

    }

    @Test
    public void resizeImage_shouldResizeImage_whenABufferedImageIsPassed_3() throws Exception {

        //image dimension
        int width = 2000;
        int height = 1000;
        //create buffered image object img
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        BufferedImage resizedImg = imageUtil.resizeImage(img, 200, 150);

        System.out.println("Orginal height: " + height + " Width: " + width);
        System.out.println("Resized height: " + resizedImg.getHeight() + " Width: " + resizedImg.getWidth());
        Assert.assertTrue(resizedImg.getHeight() <= height);
        Assert.assertTrue(resizedImg.getWidth() <= width);

    }

    @Test
    public void resizeImage_shouldResizeImage_whenABufferedImageIsPassed_4() throws Exception {

        //image dimension
        int width = 50;
        int height = 100;
        //create buffered image object img
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        BufferedImage resizedImg = imageUtil.resizeImage(img, 200, 150);

        System.out.println("Orginal height: " + height + " Width: " + width);
        System.out.println("Resized height: " + resizedImg.getHeight() + " Width: " + resizedImg.getWidth());
        Assert.assertTrue(resizedImg.getHeight() <= height);
        Assert.assertTrue(resizedImg.getWidth() <= width);

    }

    @Test
    public void resizeImage_shouldResizeImage_whenABufferedImageIsPassed_5() throws Exception {

        //image dimension
        int width = 50;
        int height = 100;
        //create buffered image object img
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        BufferedImage resizedImg = imageUtil.resizeImage(img, 200, 0);

        System.out.println("Orginal height: " + height + " Width: " + width);
        System.out.println("Resized height: " + resizedImg.getHeight() + " Width: " + resizedImg.getWidth());
        Assert.assertTrue(resizedImg.getHeight() <= height);
        Assert.assertTrue(resizedImg.getWidth() <= width);

    }

    @Test
    public void resizeImage_shouldResizeImage_whenImageBytesArePassed() throws Exception {

        File initialFile = new File("src/test/resources/imgTest/lego.png");

        byte[] fileByte = FileUtils.readFileToByteArray(initialFile);

        byte[] resizedImgBytes = imageUtil.resizeImage(fileByte, "png", 200, 100);

        try (ByteArrayInputStream in = new ByteArrayInputStream(resizedImgBytes); ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
            BufferedImage resizedImg = ImageIO.read(in);

            System.out.println("Resized height: " + resizedImg.getHeight() + " Width: " + resizedImg.getWidth());
            Assert.assertTrue(resizedImg.getHeight() <= 200);
        }

    }

}
