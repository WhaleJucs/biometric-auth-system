package br.edu.biometric.service;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Serviço responsável pelo reconhecimento facial usando OpenCV
 */
public class FacialRecognitionService {

    private static final String HAAR_CASCADE_PATH = "haarcascades/haarcascade_frontalface_default.xml";
    private static final int CONFIDENCE_THRESHOLD = 70; // Quanto menor, mais confiança
    private static final Size FACE_SIZE = new Size(200, 200);

    private CascadeClassifier faceDetector;
    // Armazena os histogramas das faces treinadas por label
    private Map<Integer, List<Mat>> trainedFaces = new HashMap<>();
    private boolean initialized;

    public FacialRecognitionService() {
        try {
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
            initialize();
        } catch (UnsatisfiedLinkError e) {
            System.err.println("Erro ao carregar biblioteca OpenCV: " + e.getMessage());
            initialized = false;
        }
    }

    private void initialize() {
        // Carrega o classificador Haar Cascade
        faceDetector = new CascadeClassifier();

        // Tenta carregar do resources
        File cascadeFile = new File(HAAR_CASCADE_PATH);
        if (!cascadeFile.exists()) {
            // Tenta carregar do resources
            String resourcePath = getClass().getClassLoader().getResource(HAAR_CASCADE_PATH) != null
                    ? getClass().getClassLoader().getResource(HAAR_CASCADE_PATH).getPath()
                    : HAAR_CASCADE_PATH;
            cascadeFile = new File(resourcePath);
        }

        if (cascadeFile.exists()) {
            faceDetector.load(cascadeFile.getAbsolutePath());
        } else {
            System.err.println("Arquivo Haar Cascade não encontrado!");
            initialized = false;
            return;
        }

        initialized = !faceDetector.empty();
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
            System.err.println("Serviço de reconhecimento facial não inicializado!");
            return faces;
        }

        Mat image = Imgcodecs.imread(imagePath);
        if (image.empty()) {
            System.err.println("Não foi possível carregar a imagem: " + imagePath);
            return faces;
        }

        Mat grayImage = new Mat();
        Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY);
        Imgproc.equalizeHist(grayImage, grayImage);

        MatOfRect faceDetections = new MatOfRect();
        faceDetector.detectMultiScale(grayImage, faceDetections);

        for (Rect rect : faceDetections.toArray()) {
            faces.add(rect);
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
            System.err.println("Serviço não inicializado!");
            return;
        }

        List<Mat> faces = new ArrayList<>();

        for (String path : imagePaths) {
            Mat face = extractFace(path);
            if (face != null) {
                faces.add(face);
            }
        }

        if (faces.isEmpty()) {
            System.err.println("Nenhuma face foi extraída para treinamento!");
            return;
        }

        trainedFaces.put(label, faces);
    }

    /**
     * Reconhece uma face na imagem usando comparação de histogramas
     * 
     * @param imagePath Caminho da imagem a ser reconhecida
     * @return Array com [label, confidence] ou null se não reconhecer
     */
    public int[] recognizeFace(String imagePath) {
        if (!initialized || trainedFaces.isEmpty()) {
            return null;
        }

        Mat face = extractFace(imagePath);
        if (face == null) {
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
            return null;
        }

        // Converte distância para confiança (quanto menor a distância, maior a
        // confiança)
        int confidence = (int) (bestDistance * 100); // Ajuste conforme necessário

        return new int[] { bestLabel, confidence };
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
