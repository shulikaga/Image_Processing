
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.imageio.ImageIO;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.CLAHE;
import org.opencv.imgproc.Imgproc;

public class Images {

    public static ImagesDatabase dbt;
    public static ArrayList<Images.Image> images;
    public static ArrayList<Images.Image> filteredImages;
    int height;
    int width;
    public static ArrayList<Images.Image> classifiedImages;
    public static ArrayList<Images.Image> results;

    public Images() throws IOException {
        images = new ArrayList<>();
        filteredImages = new ArrayList<>();
        classifiedImages = new ArrayList<>();
        results = new ArrayList<>();
    }

    public class Image {

        public Image() {
        }
        double contrastClipLimit;
        int contrastGridsize;
        int fftAmplitude;
        int extractFeatureRadius;
        int[] colorChannelIndexes = new int[12];
        int k;
        double percentCorrect;
        double distance;
        int[] classes;
        String imageId;
        int patientId;
        int cancerClass = 0;
        int height;
        int width;
        Mat imageRGB;
        Mat imageRGB_R;
        Mat imageRGB_G;
        Mat imageRGB_B;
        Mat imageHSV;
        Mat imageHSV_H;
        Mat imageHSV_S;
        Mat imageHSV_V;
        Mat imageHLS;
        Mat imageHLS_H;
        Mat imageHLS_L;
        Mat imageHLS_S;
        Mat imageLab;
        Mat imageLab_L;
        Mat imageLab_a;
        Mat imageLab_b;
        Mat imageYUV;
        Mat imageYUV_Y;
        Mat imageYUV_U;
        Mat imageYUV_V;
        double[][] vectors;
        int indexOf_HSV_H = 0;
        int indexOf_HSV_S = 1;
        int indexOf_HSV_V = 2;
        int indexOf_HLS_H = 3;
        int indexOf_HLS_L = 4;
        int indexOf_HLS_S = 5;
        int indexOf_Lab_L = 6;
        int indexOf_Lab_a = 7;
        int indexOf_Lab_b = 8;
        int indexOf_YUV_Y = 9;
        int indexOf_YUV_U = 10;
        int indexOf_YUV_V = 11;

        public Image(String fileName, byte[] data, int height, int width, int extractFeatureRadius) {
            distance = 0.0;
            classes = new int[12];

            imageId = fileName;
            this.height = height;
            this.width = width;
            if (data != null) {
                imageRGB = new Mat(height, width, CvType.CV_8UC3);
                imageRGB.put(0, 0, data);

                imageHSV = new Mat(height, width, CvType.CV_8UC3);
                imageHSV_H = new Mat(height, width, CvType.CV_8UC1);
                imageHSV_S = new Mat(height, width, CvType.CV_8UC1);
                imageHSV_V = new Mat(height, width, CvType.CV_8UC1);

                imageHLS = new Mat(height, width, CvType.CV_8UC3);
                imageHLS_H = new Mat(height, width, CvType.CV_8UC1);
                imageHLS_L = new Mat(height, width, CvType.CV_8UC1);
                imageHLS_S = new Mat(height, width, CvType.CV_8UC1);

                imageLab = new Mat(height, width, CvType.CV_8UC3);
                imageLab_L = new Mat(height, width, CvType.CV_8UC1);
                imageLab_a = new Mat(height, width, CvType.CV_8UC1);
                imageLab_b = new Mat(height, width, CvType.CV_8UC1);

                imageYUV = new Mat(height, width, CvType.CV_8UC3);
                imageYUV_Y = new Mat(height, width, CvType.CV_8UC1);
                imageYUV_U = new Mat(height, width, CvType.CV_8UC1);
                imageYUV_V = new Mat(height, width, CvType.CV_8UC1);
            }
            //4 colormodels with 3 color channels each converted to grayscale to FFT
            //vektor vor the classification
            vectors = new double[12][129];//[(int) (Math.ceil(width / extractFeatureRadius)) + 1];

        }

        public int getHeight() {
            return height;
        }

        public int getWidth() {
            return width;
        }

        private void splitTo3ColorChannels(Mat colorModel, Mat channel1, Mat channel2, Mat channel3) {

            List<Mat> lRgb = new ArrayList<>(3);
            Core.split(colorModel, lRgb);

            lRgb.get(0).copyTo(channel1);
            lRgb.get(1).copyTo(channel2);
            lRgb.get(2).copyTo(channel3);
        }

        private void convertTo4ColorModels() throws IOException {
            Imgproc.cvtColor(imageRGB, imageHSV, Imgproc.COLOR_RGB2HSV);
            Imgproc.cvtColor(imageRGB, imageHLS, Imgproc.COLOR_RGB2HLS);
            Imgproc.cvtColor(imageRGB, imageLab, Imgproc.COLOR_RGB2Lab);
            Imgproc.cvtColor(imageRGB, imageYUV, Imgproc.COLOR_RGB2YUV);
        }

        private void splitAllColorModelsTo3Channels() {

            splitTo3ColorChannels(imageHSV, imageHSV_H, imageHSV_S, imageHSV_V);
            splitTo3ColorChannels(imageHLS, imageHLS_H, imageHLS_L, imageHLS_S);
            splitTo3ColorChannels(imageLab, imageLab_L, imageLab_a, imageLab_b);
            splitTo3ColorChannels(imageYUV, imageYUV_Y, imageYUV_U, imageYUV_V);

        }

        private void enchanceContrast(double clipLimit, int gridsize) {
            CLAHE c = Imgproc.createCLAHE();
            c.setClipLimit(clipLimit);
            c.setTilesGridSize(new Size(gridsize, gridsize));

            c.apply(imageHSV_H, imageHSV_H);
            c.apply(imageHSV_S, imageHSV_S);
            c.apply(imageHSV_V, imageHSV_V);

            c.apply(imageHLS_H, imageHLS_H);
            c.apply(imageHLS_L, imageHLS_L);
            c.apply(imageHLS_S, imageHLS_S);

            c.apply(imageLab_L, imageLab_L);
            c.apply(imageLab_a, imageLab_a);
            c.apply(imageLab_b, imageLab_b);

            c.apply(imageYUV_Y, imageYUV_Y);
            c.apply(imageYUV_U, imageYUV_U);
            c.apply(imageYUV_V, imageYUV_V);
        }

        private void normalize() {
            Core.normalize(imageHSV_H, imageHSV_H, 0, 255, Core.NORM_MINMAX, CvType.CV_8UC1);
            Core.normalize(imageHSV_S, imageHSV_S, 0, 255, Core.NORM_MINMAX, CvType.CV_8UC1);
            Core.normalize(imageHSV_V, imageHSV_V, 0, 255, Core.NORM_MINMAX, CvType.CV_8UC1);

            Core.normalize(imageHLS_H, imageHLS_H, 0, 255, Core.NORM_MINMAX, CvType.CV_8UC1);
            Core.normalize(imageHLS_L, imageHLS_L, 0, 255, Core.NORM_MINMAX, CvType.CV_8UC1);
            Core.normalize(imageHLS_S, imageHLS_S, 0, 255, Core.NORM_MINMAX, CvType.CV_8UC1);

            Core.normalize(imageLab_L, imageLab_L, 0, 255, Core.NORM_MINMAX, CvType.CV_8UC1);
            Core.normalize(imageLab_a, imageLab_a, 0, 255, Core.NORM_MINMAX, CvType.CV_8UC1);
            Core.normalize(imageLab_b, imageLab_b, 0, 255, Core.NORM_MINMAX, CvType.CV_8UC1);

            Core.normalize(imageYUV_Y, imageYUV_Y, 0, 255, Core.NORM_MINMAX, CvType.CV_8UC1);
            Core.normalize(imageYUV_U, imageYUV_U, 0, 255, Core.NORM_MINMAX, CvType.CV_8UC1);
            Core.normalize(imageYUV_V, imageYUV_V, 0, 255, Core.NORM_MINMAX, CvType.CV_8UC1);

        }

        private void fft(int fftAmplitude) {

            ImageProcessor.performFastFourierTransform(imageHSV_H, fftAmplitude);
            ImageProcessor.performFastFourierTransform(imageHSV_S, fftAmplitude);
            ImageProcessor.performFastFourierTransform(imageHSV_V, fftAmplitude);

            ImageProcessor.performFastFourierTransform(imageHLS_H, fftAmplitude);
            ImageProcessor.performFastFourierTransform(imageHLS_L, fftAmplitude);
            ImageProcessor.performFastFourierTransform(imageHLS_S, fftAmplitude);

            ImageProcessor.performFastFourierTransform(imageLab_L, fftAmplitude);
            ImageProcessor.performFastFourierTransform(imageLab_a, fftAmplitude);
            ImageProcessor.performFastFourierTransform(imageLab_b, fftAmplitude);

            ImageProcessor.performFastFourierTransform(imageYUV_Y, fftAmplitude);
            ImageProcessor.performFastFourierTransform(imageYUV_U, fftAmplitude);
            ImageProcessor.performFastFourierTransform(imageYUV_V, fftAmplitude);
        }

        private void extractFeature(int radius) {
            ImageProcessor.extractFeature(vectors, imageHSV_H, indexOf_HSV_H, radius);
            ImageProcessor.extractFeature(vectors, imageHSV_S, indexOf_HSV_S, radius);
            ImageProcessor.extractFeature(vectors, imageHSV_V, indexOf_HSV_V, radius);

            ImageProcessor.extractFeature(vectors, imageHLS_H, indexOf_HLS_H, radius);
            ImageProcessor.extractFeature(vectors, imageHLS_L, indexOf_HLS_L, radius);
            ImageProcessor.extractFeature(vectors, imageHLS_S, indexOf_HLS_S, radius);

            ImageProcessor.extractFeature(vectors, imageLab_L, indexOf_Lab_L, radius);
            ImageProcessor.extractFeature(vectors, imageLab_a, indexOf_Lab_a, radius);
            ImageProcessor.extractFeature(vectors, imageLab_b, indexOf_Lab_b, radius);

            ImageProcessor.extractFeature(vectors, imageYUV_Y, indexOf_YUV_Y, radius);
            ImageProcessor.extractFeature(vectors, imageYUV_U, indexOf_YUV_U, radius);
            ImageProcessor.extractFeature(vectors, imageYUV_V, indexOf_YUV_V, radius);
        }
    }

    public void loadImages(String path) throws IOException {
        File dir = new File(path);
        Images.Image newImage;

        byte[] data;
        BufferedImage bi;
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        if (dir.isDirectory()) {

            for (File file : dir.listFiles(IMAGE_FILTER)) {

                BufferedImage image = ImageIO.read(file);
                data = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();

                int height = image.getHeight();
                int width = image.getWidth();

                newImage = new Images.Image(file.getName(), data, height, width, 129);
                images.add(newImage);
            }
        }
    }
    static final FilenameFilter IMAGE_FILTER = new FilenameFilter() {
        @Override
        public boolean accept(final File dir, final String name) {

            if (name.endsWith(".png")) {
                return (true);
            }
            return (false);
        }
    };

    private void preprocess(Image image,
            double clipLimit, int gridsize) throws IOException {

        image.convertTo4ColorModels(); //Lab, HSV, HLS, YUV
        image.splitAllColorModelsTo3Channels();
        image.enchanceContrast(clipLimit, gridsize); //each color channel
        image.normalize();  //each color channel     
    }

    public void process(boolean vectorsInDB, double contrastClipLimit, int contrastGridsize,int fftAmplitude, int extractFeatureRadius,int[] colorChannelIndexes, String patientMappingFileLocation, int k) throws IOException, SQLException {

        for (Images.Image resImage : images) {        
       
            preprocess(resImage, contrastClipLimit, contrastGridsize);
           
            resImage.fft(fftAmplitude);
            resImage.extractFeature(extractFeatureRadius);            
      
        }

        dbt = new ImagesDatabase(patientMappingFileLocation, images, 129);
    }

    private void buildTrainingsAndClassifiedArrayLists(ArrayList<Images.Image> list, String[][] resultSet, boolean classified) {
        //pre
        for (int i = 0; i < resultSet.length; i++) {
            Images.Image resImage = new Images.Image("", null, -1, -1, -1);
            for (int j = 0; j < resultSet[i].length; j++) {
                resImage.imageId = resultSet[i][0]; // imageId;
                resImage.patientId = Integer.parseInt(resultSet[i][1]); // patientId;
               
                if (classified == true) {resImage.cancerClass = Integer.parseInt(resultSet[i][2]);
                } else { resImage.cancerClass = 0;}

                String[] str = resultSet[i][3].split("}");//3
                double[][] dbArray = new double[str.length][129];

                for (int x = 0; x < str.length; x++) {
                    str[x] = str[x].replace(",{", "").replace("}", "");
                    String[] str2d = str[x].split(",");
                    for (int y = 0; y < str2d.length; y++) {
                        dbArray[x][y] = Double.parseDouble(str2d[y]);
                    }
                }
                System.arraycopy(dbArray, 0, resImage.vectors, 0, resImage.vectors.length);

            }


            list.add(resImage);
        }
    }

    public double getResults(double contrastClipLimit, int contrastGridsize,
            int fftAmplitude,
            int extractFeatureRadius,
            int[] colorChannelIndexes,
            String patientMappingFileLocation, int k) {

        String[][] resultSet = dbt.getResultSet();
        buildTrainingsAndClassifiedArrayLists(filteredImages, resultSet, false);
        buildTrainingsAndClassifiedArrayLists(classifiedImages, resultSet, true);

        //klassifizieren
        for (Images.Image resImage : filteredImages) {
            ImageProcessor.classify(resImage, classifiedImages, colorChannelIndexes, k);
        }

        int correctCounter = 0;
        int totalCounter = 1;

        //erfolgsrate
        Iterator cit = classifiedImages.iterator();
        for (Images.Image resImage : filteredImages) {
            Images.Image imc = (Images.Image) cit.next();
            if (resImage.imageId.equals(imc.imageId)) {
                if (resImage.cancerClass == imc.cancerClass && resImage.cancerClass!=0 &&  imc.cancerClass!=0) {
                    correctCounter++;
                }
            }
            totalCounter++;
        }

        double percentCorrect = (100 * (double) correctCounter) / (double) totalCounter;

        System.out.println("Auswertung von: ");
        System.out.println("contrastClipLimit = " + contrastClipLimit + ", contrastGridsize = " + contrastGridsize);
        System.out.println("fftAmplitude = " + fftAmplitude);
        System.out.println("extractFeatureRadius = " + extractFeatureRadius);

        System.out.print("colorChannelIndexes = ");
        for (int i = 0; i < colorChannelIndexes.length; i++) {
            System.out.print(colorChannelIndexes[i] + " ");
        }
        System.out.println();
        System.out.println("k = " + k);
        System.out.println("Erfolgsrate = " + percentCorrect + " %");
        System.out.println();

        return percentCorrect;
    }
}
