package ch.swissbytes.procurement.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.logging.Logger;

/**
 * Created by Christian on 22/12/2015.
 */
public class ImageUtil implements Serializable {

    public static final Logger log = Logger.getLogger(ImageUtil.class.getName());

    private final static int maxWidth = 690;
    private final static int maxHeight = 200;


    public BufferedImage getNewImageResized(final byte[] originalImage) {
        log.info("resizing image header logo ");
        BufferedImage originalImageBf = convertByteArrayToBufferedImage(originalImage);
        BufferedImage newImage = new BufferedImage(690, 200, originalImageBf.getType());
        Graphics g = newImage.getGraphics();

        g.setColor(Color.white);
        g.fillRect(0, 0, 690, 200);
        g.drawImage(originalImageBf, 0, 0, null);
        g.dispose();
        return newImage;
    }

    public boolean hasDimensionHeaderLogoCorrect(final byte[] originalImage) {
        log.info("verifying dimension of header logo");
        InputStream in = new ByteArrayInputStream(originalImage);
        BufferedImage originalImageBf = null;
        try {
            originalImageBf = ImageIO.read(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.info("newImage width : " + originalImageBf.getWidth());
        log.info("newImage height : " + originalImageBf.getHeight());
        if (originalImageBf.getWidth() < maxWidth && originalImageBf.getHeight() < maxHeight) {
            return false;
        }
        return true;
    }

    public InputStream convertBufferedImageToInputStream(final BufferedImage bufferedImage){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(bufferedImage, "png", baos);
        } catch (IOException e) {
            e.printStackTrace();
        }
        InputStream is = new ByteArrayInputStream(baos.toByteArray());
        return is;
    }

    public BufferedImage convertByteArrayToBufferedImage(final byte[] byteImage){
        InputStream in = new ByteArrayInputStream(byteImage);
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bufferedImage;
    }

}
