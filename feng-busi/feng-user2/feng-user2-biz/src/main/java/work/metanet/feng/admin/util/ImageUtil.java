package work.metanet.feng.admin.util;

import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @ClassName ImageUtil
 * @Deacription TODO
 * @author edison
 * @Date 2021/11/3 10:45
 * @Version 1.0
 **/
public class ImageUtil {


    /**
     * 裁剪PNG图片工具类
     *
     * @param fromFile 源文件
     * @param toFile   裁剪后的文件
     */
    public static void resizePng(File fromFile, File toFile) {
        try {
            BufferedImage bi2 = ImageIO.read(fromFile);
            int newWidth = bi2.getWidth();
            int newHeight = bi2.getHeight();
            // 1 - 2M 的
            if ((1024 * 1024) < fromFile.length() && fromFile.length() <= (1024 * 1024 * 2)) {
                newWidth = (int) (newWidth * 0.65f);
                newHeight = (int) (newHeight * 0.65f);
            }
            // 2M -3M 以上的
            else if ((1024 * 1024 * 2) < fromFile.length() && fromFile.length() <= (1024 * 1024 * 3)) {
                newWidth = (int) (newWidth * 0.5f);
                newHeight = (int) (newHeight * 0.5f);
                // 3M -5M 以上的
            } else if ((1024 * 1024 * 3) < fromFile.length() && fromFile.length() <= (1024 * 1024 * 5)) {
                newWidth = (int) (newWidth * 0.3f);
                newHeight = (int) (newHeight * 0.3f);
            }
            // 5M -10M 以上的
            else if ((1024 * 1024 * 5) < fromFile.length() && fromFile.length() <= (1024 * 1024 * 10)) {
                newWidth = (int) (newWidth * 0.2f);
                newHeight = (int) (newHeight * 0.2f);
            }
            // 大于10M
            else if ((1024 * 1024 * 10) < fromFile.length()) {
                newWidth = (int) (newWidth * 0.1f);
                newHeight = (int) (newHeight * 0.1f);
            }

            BufferedImage to = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);

            Graphics2D g2d = to.createGraphics();

            to = g2d.getDeviceConfiguration().createCompatibleImage(newWidth, newHeight,

                    Transparency.TRANSLUCENT);

            g2d.dispose();

            g2d = to.createGraphics();

            @SuppressWarnings("static-access")

            Image from = bi2.getScaledInstance(newWidth, newHeight, bi2.SCALE_AREA_AVERAGING);

            g2d.drawImage(from, 0, 0, null);

            g2d.dispose();

            ImageIO.write(to, "png", toFile);

        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    /**
     * MultipartFile 转 File
     *
     * @param file
     * @throws Exception
     */
    public static File multipartFileToFile(MultipartFile file) throws Exception {

        File toFile = null;
        if (file.equals("") || file.getSize() <= 0) {
            file = null;
        } else {
            InputStream ins = null;
            ins = file.getInputStream();
            toFile = new File(file.getOriginalFilename());
            inputStreamToFile(ins, toFile);
            ins.close();
        }
        return toFile;
    }

    //获取流文件
    private static void inputStreamToFile(InputStream ins, File file) {
        try {
            OutputStream os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            ins.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 测试
     */

//    public static void main(String[] args) throws Exception {
//        File fromFile = new File("D:/110.png");
//        File toFile = new File("D:/112.png");
//        resizePng(fromFile, toFile);
//    }

}