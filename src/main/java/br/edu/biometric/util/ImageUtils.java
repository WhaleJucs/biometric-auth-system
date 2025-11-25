package br.edu.biometric.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Utilitários para manipulação de imagens
 */
public class ImageUtils {

    private static final String BIOMETRIC_DATA_DIR = "data/biometric";

    /**
     * Redimensiona uma imagem mantendo a proporção
     * 
     * @param image     Imagem original
     * @param maxWidth  Largura máxima
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
     * 
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
     * 
     * @param image    Imagem a ser salva
     * @param filePath Caminho do arquivo
     * @param format   Formato (jpg, png, etc)
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
     * 
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

    /**
     * Copia uma imagem biométrica para o diretório do usuário
     * 
     * @param sourcePath Caminho da imagem original
     * @param userId     ID do usuário
     * @return Caminho da imagem copiada ou null se houver erro
     */
    public static String copyBiometricImage(String sourcePath, String userId) {
        try {
            // Cria o diretório do usuário se não existir
            Path userDir = Paths.get(BIOMETRIC_DATA_DIR, userId);
            Files.createDirectories(userDir);

            // Obtém o nome do arquivo e extensão
            File sourceFile = new File(sourcePath);
            String fileName = sourceFile.getName();

            // Define o caminho de destino
            Path destPath = userDir.resolve(fileName);

            // Copia o arquivo
            Files.copy(sourceFile.toPath(), destPath, StandardCopyOption.REPLACE_EXISTING);

            return destPath.toString();
        } catch (IOException e) {
            System.err.println("Erro ao copiar imagem biométrica: " + e.getMessage());
            return null;
        }
    }

    /**
     * Deleta todas as imagens biométricas de um usuário
     * 
     * @param userId ID do usuário
     * @return true se deletou com sucesso
     */
    public static boolean deleteBiometricImages(String userId) {
        try {
            Path userDir = Paths.get(BIOMETRIC_DATA_DIR, userId);
            if (Files.exists(userDir)) {
                // Deleta todos os arquivos do diretório
                Files.walk(userDir)
                        .sorted((p1, p2) -> -p1.compareTo(p2)) // Reverse order to delete files before directories
                        .forEach(path -> {
                            try {
                                Files.delete(path);
                            } catch (IOException e) {
                                System.err.println("Erro ao deletar: " + path);
                            }
                        });
                return true;
            }
            return false;
        } catch (IOException e) {
            System.err.println("Erro ao deletar imagens biométricas: " + e.getMessage());
            return false;
        }
    }

    /**
     * Obtém a extensão de um arquivo
     * 
     * @param filePath Caminho do arquivo
     * @return Extensão do arquivo (sem o ponto) ou string vazia
     */
    public static String getFileExtension(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return "";
        }
        int lastDot = filePath.lastIndexOf('.');
        if (lastDot > 0 && lastDot < filePath.length() - 1) {
            return filePath.substring(lastDot + 1).toLowerCase();
        }
        return "";
    }
}
