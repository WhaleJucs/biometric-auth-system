package br.edu.biometric.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Utilitários para manipulação de imagens
 */
public class ImageUtils {

    /**
     * Redimensiona uma imagem mantendo a proporção
     * @param image Imagem original
     * @param maxWidth Largura máxima
     * @param maxHeight Altura máxima
     * @return Imagem redimensionada
     */
    public static BufferedImage resizeImage(BufferedImage image, int maxWidth, int maxHeight) {
        int width = image.getWidth();
        int height = image.getHeight();
        
        // Calcula as novas dimensões mantendo a proporção
        double widthRatio = (double) maxWidth / width;
        double heightRatio = (double) maxHeight / height;
        double ratio = Math.min(widthRatio, heightRatio);
        
        int newWidth = (int) (width * ratio);
        int newHeight = (int) (height * ratio);
        
        // Redimensiona a imagem
        Image scaledImage = image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(scaledImage, 0, 0, null);
        g2d.dispose();
        
        return resizedImage;
    }

    /**
     * Carrega uma imagem de um arquivo
     * @param filePath Caminho do arquivo
     * @return BufferedImage ou null se houver erro
     */
    public static BufferedImage loadImage(String filePath) {
        try {
            return ImageIO.read(new File(filePath));
        } catch (IOException e) {
            System.err.println("Erro ao carregar imagem: " + e.getMessage());
            return null;
        }
    }

    /**
     * Salva uma imagem em um arquivo
     * @param image Imagem a ser salva
     * @param filePath Caminho do arquivo
     * @param format Formato (jpg, png, etc)
     * @return true se salvou com sucesso
     */
    public static boolean saveImage(BufferedImage image, String filePath, String format) {
        try {
            File file = new File(filePath);
            file.getParentFile().mkdirs();
            return ImageIO.write(image, format, file);
        } catch (IOException e) {
            System.err.println("Erro ao salvar imagem: " + e.getMessage());
            return false;
        }
    }

    /**
     * Verifica se um arquivo é uma imagem válida
     * @param filePath Caminho do arquivo
     * @return true se é uma imagem válida
     */
    public static boolean isValidImage(String filePath) {
        try {
            BufferedImage image = ImageIO.read(new File(filePath));
            return image != null;
        } catch (IOException e) {
            return false;
        }
    }
}

