
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;

public class ImageProcessor {

   
    static void performFastFourierTransform(Mat colorChannel, int amp) {
        colorChannel.convertTo(colorChannel, CvType.CV_64F);

        double[] realPart = new double[colorChannel.rows() * colorChannel.cols()];
        double[] imagenaryPart = new double[colorChannel.rows() * colorChannel.cols()];
        colorChannel.get(0, 0, realPart);
       
        for (int i = 0; i < realPart.length; i++) {
            realPart[i] = amp * realPart[i]; // real part
            imagenaryPart[i] = 0.0; // imagiary part
        }
     
        //the LONI library for 2D fft used
        FastFourierTransform.fastFT(realPart, imagenaryPart, true); 
        
        colorChannel.put(0, 0, realPart);
        int cx = colorChannel.cols() / 2;
        int cy = colorChannel.rows() / 2;

        Mat q0 = new Mat(colorChannel, new Rect(0, 0, cx, cy));
        Mat q1 = new Mat(colorChannel, new Rect(cx, 0, cx, cy));
        Mat q2 = new Mat(colorChannel, new Rect(0, cy, cx, cy));
        Mat q3 = new Mat(colorChannel, new Rect(cx, cy, cx, cy));

        Mat tmp = new Mat();
        
        //replace 4 rectangles
        q0.copyTo(tmp);
        q3.copyTo(q0);
        tmp.copyTo(q3);
        
        q1.copyTo(tmp);
        q2.copyTo(q1);
        tmp.copyTo(q2);
    }

    static void extractFeature(double[][] vectors, Mat colorChannel, int indexOfColorChannel, int radius) {
        colorChannel.convertTo(colorChannel, CvType.CV_64F);  
        int lastValuesCounter = 0 , currentValuesCounter = 0, valuesCounter = 0, vectorCounter = 0; 
        int middle = colorChannel.rows() / 2;
       
        double lastSum = 0, currentSum = 0, currentMidle = 0, currentRingSum = 0;
        double[] circleArray;      
        Mat rect, circle;
         
        for (int c = radius; middle + c < colorChannel.rows(); c = c + radius) {
            rect = new Mat(colorChannel, new Rect(middle - c, middle - c, 2 * c, 2 * c));
            circleArray = new double[(int) rect.total()];
            rect.get(0, 0, circleArray);

            for (int i = 0; i < rect.rows(); i++) {
                for (int j = 0; j < rect.cols(); j++) {
                    if (((i - c) * (i - c) + (j - c) * (j - c)) <= c * c) {
                        currentSum += circleArray[(rect.rows() - 1) * i + j];
                        valuesCounter++;                       
                    }
                }
            }
            currentValuesCounter = valuesCounter - lastValuesCounter;
            currentRingSum = currentSum - lastSum;
            currentMidle = currentRingSum / currentValuesCounter;
            vectors[indexOfColorChannel][vectorCounter] = currentMidle;
            lastValuesCounter = valuesCounter; lastSum = currentSum;
            valuesCounter = 0; currentSum = 0;
            vectorCounter++;
        }

        //the last "ring" - square 
        circleArray = new double[colorChannel.rows() * colorChannel.cols()];
        for (int i = 0; i < colorChannel.rows(); i++) {
            for (int j = 0; j < colorChannel.cols(); j++) {
                if (((i - radius) * (i - radius) + (j - radius) * (j - radius)) <= radius * radius) {
                    currentSum += circleArray[colorChannel.rows() * i + j];
                    valuesCounter++;
                }
            }
        }
        currentValuesCounter = valuesCounter - lastValuesCounter;
        currentSum = currentSum - lastSum;

        currentMidle = currentSum / currentValuesCounter;
        vectors[indexOfColorChannel][vectorCounter + 1] = currentMidle;

        lastValuesCounter += currentValuesCounter;
        lastSum += currentSum;

        valuesCounter = 0;
        currentSum = 0;
    }

    static void classify(Images.Image image, ArrayList<Images.Image> trainingsImages, int[] channelIndexes, int k) {
        int classesIndex = 0;
        int[] allClasses = new int[k];//1, 2 or 3

        ArrayList<Images.Image> leaveOnePatientAwayList = new ArrayList<>();
        for (Images.Image im : trainingsImages) {
            if (image.patientId != im.patientId) {
                im.distance = getDistance(image.vectors, im.vectors, channelIndexes);
                leaveOnePatientAwayList.add(im);                
            }
        }
        //sort trainings images
        Collections.sort(leaveOnePatientAwayList, new Comparator<Images.Image>() {
            @Override
            public int compare(Images.Image im1, Images.Image im2) {
                return new Double(im1.distance).compareTo(new Double(im2.distance));
            }
        });

        //get k-top images     
        ArrayList<Images.Image> topk = new ArrayList<>(leaveOnePatientAwayList.subList(0, k));
        Iterator it = topk.iterator();
        while (it.hasNext()) {
            allClasses[classesIndex] = ((Images.Image) it.next()).cancerClass;
            classesIndex++;
        }
        image.cancerClass = findPopular(allClasses);
    }

    private static int findPopular(int[] classes) {
        if (classes == null || classes.length == 0) {return 0; }
        
        Arrays.sort(classes);
        int previous = classes[0], popular = classes[0];
        int count = 1, maxCount = 1;
        
        for (int i = 1; i < classes.length; i++) {
            if (classes[i] == previous) { count++;
            } else {
                if (count > maxCount) {popular = classes[i - 1]; maxCount = count;}
                previous = classes[i];  count = 1;
            }
        }
        return count > maxCount ? classes[classes.length - 1] : popular;
    }

    private static double getDistance(double[][] v1, double[][] v2, int[] channelIndexes) {
        double sum = 0;
        for (int i = 0; i < channelIndexes.length; i++) {
            if (channelIndexes[i] == 1) {
                for (int j = 0; j < v1[i].length; j++) {
                    sum += Math.pow((v1[i][j] - v2[i][j]), 2);
                }
            }
        }
        sum = Math.sqrt(sum);
        return sum;
    }
}
