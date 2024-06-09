package org.apache.nicolasBecasPI;

import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;




public class ImageGenerator {
	
	/**
	 * 
	 * @param width
	 * @param height
	 * @param numPrimitives number of forms per photo
	 * @param numColors number of color for random color
	 * @param path where you want to save this photo
	 * @throws IOException 
	 */
    public static void generateImage(int width, int height, int numPrimitives, int numColors, String path) throws IOException {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

     
        g2d.setColor(getRandomColor(numColors));
        g2d.fillRect(0, 0, width, height);
        
        int primitiveSize = width / numPrimitives;

        for (int i = 0; i < numPrimitives; i++) {
            int x = (int) (Math.random() * width);
            int y = (int) (Math.random() * height);
            int shapeType = (int) (Math.random() * 3); // 0: Line, 1: Circle, 2: Rectangle

            g2d.setColor(getRandomColor(numColors));
            switch (shapeType) {
                case 0:
                    g2d.drawLine(x, y, (int) (Math.random() * width), (int) (Math.random() * height));
                    break;
                case 1:
                    g2d.fillOval(x, y, primitiveSize, primitiveSize);
                    break;
                case 2:
                    g2d.fillRect(x, y, primitiveSize, primitiveSize);
                    break;
            }
        }

        File f = new File(path);
        ImageIO.write(image, "jpg", f);
    }

    private static Color getRandomColor(int numColors) {
        return new Color((int) (Math.random() * 256), (int) (Math.random() * 256), (int) (Math.random() * 256));
    }
    

                
   
}

