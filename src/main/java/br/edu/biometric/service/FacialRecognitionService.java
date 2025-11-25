package br.edu.biometric.service;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Serviço responsável pelo reconhecimento facial usando OpenCV
 */
public class FacialRecognitionService {

    private static final Logger logger = LoggerFactory.getLogger(FacialRecognitionService.class);
    private static final String HAAR_CASCADE_PATH = "haarcascades/haarcascade_frontalface_default.xml";
    private static final int CONFIDENCE_THRESHOLD = 70; // Quanto menor, mais confiança
    private static final Size FACE_SIZE = new Size(200, 200);

    private CascadeClassifier faceDetector;
    // Armazena os histogramas das faces treinadas por label
    private Map<Integer, List<Mat>> trainedFaces = new HashMap<>();
    private boolean initialized;

    public FacialRecognitionService() {
        try {
            logger.info("Carregando biblioteca OpenCV...");
            // A biblioteca org.openpnp:opencv carrega as bibliotecas nativas
            // automaticamente
            nu.pattern.OpenCV.loadLocally();
            logger.info("Biblioteca OpenCV carregada com sucesso. Versão: {}", Core.VERSION);
            initialize();
        } catch (Exception e) {
            logger.error("Erro ao carregar biblioteca OpenCV: {}", e.getMessage(), e);
            initialized = false;
        }
    }

    private void initialize() {
        logger.info("Inicializando serviço de reconhecimento facial...");
        faceDetector = new CascadeClassifier();

        // Tenta múltiplos caminhos para encontrar o arquivo Haar Cascade
        String cascadePath = null;

        // Tentativa 1: Carregar do classpath como recurso
        try {
            URL resourceUrl = getClass().getClassLoader().getResource(HAAR_CASCADE_PATH);
            if (resourceUrl != null) {
                cascadePath = resourceUrl.getPath();
                // Remove a barra inicial no Windows (ex: /C:/... -> C:/...)
                if (cascadePath.startsWith("/") && cascadePath.contains(":")) {
                    cascadePath = cascadePath.substring(1);
                }
                logger.info("Tentando carregar Haar Cascade do classpath: {}", cascadePath);
                if (new File(cascadePath).exists()) {
                    faceDetector.load(cascadePath);
                    if (!faceDetector.empty()) {
                        initialized = true;
                        logger.info("Haar Cascade carregado com sucesso do classpath!");
                        return;
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("Não foi possível carregar do classpath: {}", e.getMessage());
        }

        // Tentativa 2: Carregar de target/classes
        try {
            cascadePath = "target/classes/" + HAAR_CASCADE_PATH;
            logger.info("Tentando carregar Haar Cascade de: {}", cascadePath);
            if (new File(cascadePath).exists()) {
                faceDetector.load(cascadePath);
                if (!faceDetector.empty()) {
                    initialized = true;
                    logger.info("Haar Cascade carregado com sucesso de target/classes!");
                    return;
                }
            }
        } catch (Exception e) {
            logger.warn("Não foi possível carregar de target/classes: {}", e.getMessage());
        }

        // Tentativa 3: Carregar de src/main/resources
        try {
            cascadePath = "src/main/resources/" + HAAR_CASCADE_PATH;
            logger.info("Tentando carregar Haar Cascade de: {}", cascadePath);
            if (new File(cascadePath).exists()) {
                faceDetector.load(cascadePath);
                if (!faceDetector.empty()) {
                    initialized = true;
                    logger.info("Haar Cascade carregado com sucesso de src/main/resources!");
                    return;
                }
            }
        } catch (Exception e) {
            logger.warn("Não foi possível carregar de src/main/resources: {}", e.getMessage());
        }

        logger.error("Arquivo Haar Cascade não encontrado em nenhum dos caminhos tentados!");
        initialized = false;
    }

    /**
     * Detecta faces em uma imagem
     * 
     * @param imagePath Caminho da imagem
     * @return Lista de retângulos representando faces detectadas
     */
    public List<Rect> detectFaces(String imagePath) {
        List<Rect> faces = new ArrayList<>();

        if (!initialized) {
            logger.error("Serviço de reconhecimento facial não inicializado!");
            return faces;
        }

        try {
            Mat image = Imgcodecs.imread(imagePath);
            if (image.empty()) {
                logger.error("Não foi possível carregar a imagem: {}", imagePath);
                return faces;
            }

            Mat grayImage = new Mat();
            Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY);
            Imgproc.equalizeHist(grayImage, grayImage);

            MatOfRect faceDetections = new MatOfRect();
            // Parâmetros baseados no projeto de referência
            faceDetector.detectMultiScale(grayImage, faceDetections, 1.3, 3, 0, new Size(30, 30), new Size());

            for (Rect rect : faceDetections.toArray()) {
                faces.add(rect);
            }

            logger.info("Detectadas {} face(s) na imagem: {}", faces.size(), imagePath);
        } catch (Exception e) {
            logger.error("Erro ao detectar faces: {}", e.getMessage(), e);
        }

        return faces;
    }

    /**
     * Extrai e normaliza a região da face
     * 
     * @param imagePath Caminho da imagem
     * @return Mat contendo a face normalizada ou null se não encontrar face
     */
    public Mat extractFace(String imagePath) {
        List<Rect> faces = detectFaces(imagePath);

        if (faces.isEmpty()) {
            return null;
        }

        Mat image = Imgcodecs.imread(imagePath);
        Mat grayImage = new Mat();
        Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY);

        // Pega a maior face detectada
        Rect faceRect = faces.get(0);
        Mat face = new Mat(grayImage, faceRect);

        // Normaliza o tamanho
        Mat normalizedFace = new Mat();
        Imgproc.resize(face, normalizedFace, FACE_SIZE);

        return normalizedFace;
    }

    /**
     * Treina o reconhecedor com as imagens de um usuário
     * Armazena as faces normalizadas para comparação posterior
     */
    public void trainRecognizer(List<String> imagePaths, int label) {
        if (!initialized) {
            logger.error("Serviço não inicializado!");
            return;
        }

        List<Mat> faces = new ArrayList<>();

        for (String path : imagePaths) {
            try {
                Mat face = extractFace(path);
                if (face != null) {
                    faces.add(face);
                    logger.debug("Face extraída com sucesso de: {}", path);
                } else {
                    logger.warn("Nenhuma face detectada em: {}", path);
                }
            } catch (Exception e) {
                logger.error("Erro ao extrair face de {}: {}", path, e.getMessage());
            }
        }

        if (faces.isEmpty()) {
            logger.error("Nenhuma face foi extraída para treinamento do label {}!", label);
            return;
        }

        trainedFaces.put(label, faces);
        logger.info("Reconhecedor treinado para label {} com {} face(s)", label, faces.size());
    }

    /**
     * Reconhece uma face na imagem usando comparação de histogramas
     * 
     * @param imagePath Caminho da imagem a ser reconhecida
     * @return Array com [label, confidence] ou null se não reconhecer
     */
    public int[] recognizeFace(String imagePath) {
        if (!initialized || trainedFaces.isEmpty()) {
            logger.warn("Reconhecimento impossível: initialized={}, trainedFaces={}", initialized, trainedFaces.size());
            return null;
        }

        try {
            Mat face = extractFace(imagePath);
            if (face == null) {
                logger.warn("Nenhuma face detectada em: {}", imagePath);
                return null;
            }

            // Calcula histograma da face a ser reconhecida
            Mat hist = calculateHistogram(face);

            int bestLabel = -1;
            double bestDistance = Double.MAX_VALUE;

            // Compara com todas as faces treinadas
            for (Map.Entry<Integer, List<Mat>> entry : trainedFaces.entrySet()) {
                int label = entry.getKey();
                List<Mat> trainedFacesForLabel = entry.getValue();

                for (Mat trainedFace : trainedFacesForLabel) {
                    Mat trainedHist = calculateHistogram(trainedFace);
                    double distance = compareHistograms(hist, trainedHist);

                    if (distance < bestDistance) {
                        bestDistance = distance;
                        bestLabel = label;
                    }
                }
            }

            if (bestLabel == -1) {
                logger.warn("Nenhum match encontrado para a imagem: {}", imagePath);
                return null;
            }

            // Converte distância para confiança (quanto menor a distância, maior a
            // confiança)
            int confidence = (int) (bestDistance * 100);

            logger.info("Face reconhecida - Label: {}, Confiança: {}, Distância: {}", bestLabel, confidence,
                    bestDistance);
            return new int[] { bestLabel, confidence };

        } catch (Exception e) {
            logger.error("Erro ao reconhecer face: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Calcula histograma de uma imagem em escala de cinza
     */
    private Mat calculateHistogram(Mat image) {
        Mat hist = new Mat();
        MatOfInt histSize = new MatOfInt(256);
        MatOfFloat ranges = new MatOfFloat(0f, 256f);
        MatOfInt channels = new MatOfInt(0);

        Imgproc.calcHist(
                java.util.Collections.singletonList(image),
                channels,
                new Mat(),
                hist,
                histSize,
                ranges);

        return hist;
    }

    /**
     * Compara dois histogramas usando correlação
     * Retorna um valor de distância (quanto menor, mais similar)
     */
    private double compareHistograms(Mat hist1, Mat hist2) {
        // Normaliza os histogramas
        Core.normalize(hist1, hist1, 0, 1, Core.NORM_MINMAX, -1, new Mat());
        Core.normalize(hist2, hist2, 0, 1, Core.NORM_MINMAX, -1, new Mat());

        // Usa correlação (quanto maior, mais similar)
        // Invertemos para ter distância (quanto menor, mais similar)
        double correlation = Imgproc.compareHist(hist1, hist2, Imgproc.HISTCMP_CORREL);

        // Converte correlação (0-1) para distância (1-0)
        return 1.0 - correlation;
    }

    /**
     * Verifica se a confiança do reconhecimento é aceitável
     * 
     * @param confidence Valor de confiança retornado pelo reconhecedor
     * @return true se a confiança é boa (valor baixo = mais confiança)
     */
    public boolean isConfidenceAcceptable(double confidence) {
        return confidence <= CONFIDENCE_THRESHOLD;
    }

    /**
     * Converte o valor de confiança para porcentagem (0-100%)
     * Quanto maior a porcentagem, melhor a confiança
     */
    public double getConfidencePercentage(double confidence) {
        // Normaliza o valor para 0-100%
        // Confiança 0 = 100%, Confiança 100+ = 0%
        double percentage = Math.max(0, Math.min(100, 100 - confidence));
        return percentage;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public int getConfidenceThreshold() {
        return CONFIDENCE_THRESHOLD;
    }
}
