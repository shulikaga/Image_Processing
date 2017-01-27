
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;


public class Main {
    
     private static XYDataset createDataset(double[]x,String setup){
           final XYSeries data = new XYSeries(setup); 
         for(int i = 0;i<x.length;i++){ 
      data.add(i+1, x[i]);
     }
          final XYSeriesCollection dataset = new XYSeriesCollection( );          
         dataset.addSeries( data );
         return dataset;
     
     }
    
    public static void plot(double[]x ,  String tableName, String columnName,String setup) throws IOException {
           DefaultCategoryDataset line_chart_dataset = new DefaultCategoryDataset();
           
     

       JFreeChart xylineChart = ChartFactory.createXYLineChart(
         tableName ,
         "Anzahl der Farbkanäle" ,
         "Erfolgsrate%" ,
         createDataset(x,setup),
         PlotOrientation.VERTICAL ,
         true , true , false); 
       
       

      int width = 640; /* Width of the image */
      int height = 480; /* Height of the image */ 
      File lineChart = new File( "/Users/gannashulika/Documents/5_Semester/Bildverarbeitung_Wahlfach/CancerClassification/src/LineChart.jpeg" ); 
      ChartUtilities.saveChartAsJPEG(lineChart ,xylineChart, width ,height);
  
   
}
    
   

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, SQLException {
      Images images = new Images();
      images.loadImages("/Users/gannashulika/Documents/5_Semester/Bildverarbeitung_Wahlfach/Cellac_Cancer_Classification/src/All_Images_Folder");
      final String patientMappingFileLocation = "/Users/gannashulika/Documents/5_Semester/Bildverarbeitung_Wahlfach/Cellac_Cancer_Classification/src/pit-images/patientmapping.csv";
     
      
          
      boolean vectorsInDB = false;
        
       
      //default parameters
      double contrastClipLimit = 2; 
      int contrastGridsize = 4; 
      int fftAmplitude = 10;      
      int extractFeatureRadius = 45;      
      int[] colorChannelIndexes = new int[]{1,1,1,1,1,1,1,1,1,1,1,1};
      int k = 5;
      
      
      double[] successRateCannels = new double[12]; 
      
      double[] successRateK = new double[5]; 
      
      double[] successRatefftAmp = new double[5]; 
      
      double[] successRateRadius = new double[15]; 
   
      double[] successRateContrastClipLimit = new double[10]; 
      
      double[] successRateContrastGridSize = new double[10]; 
       
      double[] successRateDefault = new double[1]; 
      
      String parameter = "1";
      int counter = 0;
     // while(counter < 5){
      
      switch(parameter){
                   
          case "channels": 
         images.process(vectorsInDB, contrastClipLimit, contrastGridsize, fftAmplitude, extractFeatureRadius, colorChannelIndexes, patientMappingFileLocation, k);

           successRateCannels[0] = testOneRandomColorChannel(images, vectorsInDB, contrastClipLimit, contrastGridsize, fftAmplitude, extractFeatureRadius,patientMappingFileLocation, k);          
           successRateCannels[1] = testTwoRandomColorChannels( images, vectorsInDB, contrastClipLimit, contrastGridsize, fftAmplitude, extractFeatureRadius,patientMappingFileLocation, k);
           successRateCannels[2] = testThreeRandomColorChannels(images, vectorsInDB, contrastClipLimit, contrastGridsize,  fftAmplitude, extractFeatureRadius,patientMappingFileLocation, k);
           successRateCannels[3] = testFourRandomColorChannels( images, vectorsInDB, contrastClipLimit, contrastGridsize, fftAmplitude, extractFeatureRadius,patientMappingFileLocation, k);
           successRateCannels[4] = testFiveRandomColorChannels( images, vectorsInDB, contrastClipLimit, contrastGridsize, fftAmplitude, extractFeatureRadius,patientMappingFileLocation, k);
           successRateCannels[5] = testSixRandomColorChannels( images, vectorsInDB, contrastClipLimit, contrastGridsize,  fftAmplitude, extractFeatureRadius,patientMappingFileLocation, k);
           successRateCannels[6] = testSevenRandomColorChannels( images, vectorsInDB, contrastClipLimit, contrastGridsize,  fftAmplitude, extractFeatureRadius,patientMappingFileLocation, k);
           successRateCannels[7] = testEightRandomColorChannels( images, vectorsInDB, contrastClipLimit, contrastGridsize, fftAmplitude, extractFeatureRadius,patientMappingFileLocation, k);
           successRateCannels[8] = testNineRandomColorChannels( images, vectorsInDB, contrastClipLimit, contrastGridsize, fftAmplitude, extractFeatureRadius,patientMappingFileLocation, k);
           successRateCannels[9] = testTenRandomColorChannels( images, vectorsInDB, contrastClipLimit, contrastGridsize, fftAmplitude, extractFeatureRadius,patientMappingFileLocation, k);
           successRateCannels[10] = testElevenRandomColorChannels(images, vectorsInDB, contrastClipLimit, contrastGridsize, fftAmplitude, extractFeatureRadius,patientMappingFileLocation, k);
           successRateCannels[11] = testTwelveRandomColorChannels(images, vectorsInDB, contrastClipLimit, contrastGridsize, fftAmplitude, extractFeatureRadius, patientMappingFileLocation, k);
        
           plot(successRateCannels, "Erfolgsrate / Anzahl der Farbkanäle", "Anzahl der Farbkanäle", 
                   "contrastClipLimit = "+ contrastClipLimit+", contrastGridsize = "+  contrastGridsize+"\n"+
                   "fftAmplitude = " +fftAmplitude+"\n"+
                   "extractFeatureRadius = "+extractFeatureRadius+"\n"+
                   "k = "+k );
           break;
              
          case "k": k = 3;
              while(k<=9){int c=0;
               images.process(vectorsInDB, contrastClipLimit, contrastGridsize,  fftAmplitude, extractFeatureRadius, colorChannelIndexes, patientMappingFileLocation, k);
                successRateK[c] = images.getResults(contrastClipLimit, contrastGridsize, fftAmplitude, extractFeatureRadius, colorChannelIndexes, patientMappingFileLocation, k);


                k+=2; c++;}  k = 3;  break;
          
          case "fftAmplitude": fftAmplitude = 10; while(fftAmplitude<5000){ int c = 0;
                images.process(vectorsInDB, contrastClipLimit, contrastGridsize, fftAmplitude, extractFeatureRadius, colorChannelIndexes, patientMappingFileLocation, k);
                 successRatefftAmp[c] = images.getResults(contrastClipLimit, contrastGridsize, fftAmplitude, extractFeatureRadius, colorChannelIndexes, patientMappingFileLocation, k);


                 fftAmplitude*=5; c++;}  fftAmplitude = 10; break;
              
          case "radius": extractFeatureRadius = 5; 
              while(extractFeatureRadius<128){int c = 0;
                 images.process(vectorsInDB, contrastClipLimit, contrastGridsize, fftAmplitude, extractFeatureRadius, colorChannelIndexes, patientMappingFileLocation, k);
                  successRateRadius[c] = images.getResults(contrastClipLimit, contrastGridsize, fftAmplitude, extractFeatureRadius, colorChannelIndexes, patientMappingFileLocation, k);


                  c++; extractFeatureRadius+=10; }  extractFeatureRadius = 5;break;
         
          case "contrastClipLimit": contrastClipLimit = 1;
              while(contrastClipLimit<10){int c = 0;
               images.process(vectorsInDB, contrastClipLimit, contrastGridsize, fftAmplitude, extractFeatureRadius, colorChannelIndexes, patientMappingFileLocation, k);
               successRateContrastClipLimit[c] = images.getResults(contrastClipLimit, contrastGridsize, fftAmplitude, extractFeatureRadius, colorChannelIndexes, patientMappingFileLocation, k);

                c++; contrastClipLimit++; } break;
         
          case "contrastGridSize":  contrastGridsize = 1;
              while(contrastGridsize<10){int c=0;
                 images.process(vectorsInDB, contrastClipLimit, contrastGridsize,  fftAmplitude, extractFeatureRadius, colorChannelIndexes, patientMappingFileLocation, k);
                successRateContrastGridSize[c] =  images.getResults(contrastClipLimit, contrastGridsize, fftAmplitude, extractFeatureRadius, colorChannelIndexes, patientMappingFileLocation, k);


                 c++; contrastGridsize++; }  contrastGridsize = 10; break;
      
          case "1":  
             //default parameters
       contrastClipLimit = 2; 
       contrastGridsize = 4; 
       fftAmplitude = 10;      
       extractFeatureRadius = 55;      
     colorChannelIndexes = new int[]{0,0,0,0,0,0,0,0,0,0,1,0};
       k = 9;
      
                 images.process(vectorsInDB, contrastClipLimit, contrastGridsize,  fftAmplitude, extractFeatureRadius, colorChannelIndexes, patientMappingFileLocation, k);
              double ergebnis =  images.getResults(contrastClipLimit, contrastGridsize, fftAmplitude, extractFeatureRadius, colorChannelIndexes, patientMappingFileLocation, k);
                System.out.println(ergebnis);

                 break;
              
              
          default: images.process(vectorsInDB, contrastClipLimit, contrastGridsize, fftAmplitude, extractFeatureRadius, colorChannelIndexes, patientMappingFileLocation, k);
                  break;              
      }
       contrastClipLimit += 3; 
       contrastGridsize += 3; 
       fftAmplitude *= 5;      
       extractFeatureRadius +=20 ;           
       k+=2;
      
    //  }
      
    }
    private static double testOneRandomColorChannel(Images images,
                                                  boolean vectorsInDB,
                                                  double  contrastClipLimit, 
                                                  int contrastGridsize,
                                                  int fftAmplitude, 
                                                  int extractFeatureRadius, 
                                                  String patientMappingFileLocation,
                                                  int  k) throws SQLException, IOException{
          int[] colorChannelIndexes = new int[]{0,0,0,0,0,0,0,0,0,0,0,0};             
               colorChannelIndexes[(int)(Math.random()*12)]=1;
            return  images.getResults(contrastClipLimit, contrastGridsize, fftAmplitude, extractFeatureRadius, colorChannelIndexes, patientMappingFileLocation, k);
    }
    
    private static double testTwoRandomColorChannels(Images images, 
                                                   boolean vectorsInDB,
                                                   double  contrastClipLimit, 
                                                   int contrastGridsize,
                                                   int fftAmplitude, 
                                                   int extractFeatureRadius, 
                                                   String patientMappingFileLocation,
                                                   int  k) throws SQLException, IOException{
          
            return  images.getResults(contrastClipLimit, contrastGridsize, fftAmplitude, extractFeatureRadius, getRandomIndexes(2), patientMappingFileLocation, k);
    }
    
    private static double testThreeRandomColorChannels(Images images, 
                                                     boolean vectorsInDB,
                                                     double  contrastClipLimit, 
                                                     int contrastGridsize,
                                                     int fftAmplitude, 
                                                     int extractFeatureRadius, 
                                                     String patientMappingFileLocation,
                                                     int  k) throws SQLException, IOException{
         
            return  images.getResults(contrastClipLimit, contrastGridsize, fftAmplitude, extractFeatureRadius, getRandomIndexes(3), patientMappingFileLocation, k);
    }
    
    private static double testFourRandomColorChannels(Images images, 
                                                    boolean vectorsInDB,
                                                    double  contrastClipLimit, 
                                                    int contrastGridsize,
                                                    int fftAmplitude, 
                                                    int extractFeatureRadius, 
                                                    String patientMappingFileLocation,
                                                    int  k) throws SQLException, IOException{
        
            return  images.getResults(contrastClipLimit, contrastGridsize, fftAmplitude, extractFeatureRadius, getRandomIndexes(4), patientMappingFileLocation, k);
    }
    
    private static double testFiveRandomColorChannels(Images images, 
                                                    boolean vectorsInDB,
                                                    double  contrastClipLimit, 
                                                    int contrastGridsize,
                                                    int fftAmplitude, 
                                                    int extractFeatureRadius, 
                                                    String patientMappingFileLocation,
                                                    int  k) throws SQLException, IOException{
         
            return  images.getResults(contrastClipLimit, contrastGridsize, fftAmplitude, extractFeatureRadius, getRandomIndexes(5), patientMappingFileLocation, k);
             
             
    }
    
    private static double testSixRandomColorChannels(Images images, 
                                                   boolean vectorsInDB,
                                                   double  contrastClipLimit, 
                                                   int contrastGridsize,
                                                   int fftAmplitude, 
                                                   int extractFeatureRadius, 
                                                   String patientMappingFileLocation,
                                                   int  k) throws SQLException, IOException{
         
            return  images.getResults(contrastClipLimit, contrastGridsize, fftAmplitude, extractFeatureRadius, getRandomIndexes(6), patientMappingFileLocation, k);
    }
    
    private static double testSevenRandomColorChannels(Images images, 
                                                    boolean vectorsInDB,
                                                    double  contrastClipLimit, 
                                                    int contrastGridsize,
                                                    int fftAmplitude, 
                                                    int extractFeatureRadius, 
                                                    String patientMappingFileLocation,
                                                    int  k) throws SQLException, IOException{
        
            return  images.getResults(contrastClipLimit, contrastGridsize, fftAmplitude, extractFeatureRadius, getRandomIndexes(7), patientMappingFileLocation, k);
            
    }
    
    private static double testEightRandomColorChannels(Images images, 
                                                    boolean vectorsInDB,
                                                    double  contrastClipLimit, 
                                                    int contrastGridsize,                                                
                                                    int fftAmplitude, 
                                                    int extractFeatureRadius, 
                                                    String patientMappingFileLocation,
                                                    int  k) throws SQLException, IOException{
          
            return  images.getResults(contrastClipLimit, contrastGridsize, fftAmplitude, extractFeatureRadius, getRandomIndexes(8), patientMappingFileLocation, k);
    }
    
    private static double testNineRandomColorChannels(Images images, 
                                                    boolean vectorsInDB,
                                                    double  contrastClipLimit, 
                                                    int contrastGridsize,                                                 
                                                    int fftAmplitude, 
                                                    int extractFeatureRadius, 
                                                    String patientMappingFileLocation,
                                                    int  k) throws SQLException, IOException{
        
            return  images.getResults(contrastClipLimit, contrastGridsize, fftAmplitude, extractFeatureRadius, getRandomIndexes(9), patientMappingFileLocation, k);
    }
    
    private static double testTenRandomColorChannels(Images images, 
                                                   boolean vectorsInDB,
                                                   double  contrastClipLimit, 
                                                   int contrastGridsize,                                                 
                                                   int fftAmplitude, 
                                                   int extractFeatureRadius, 
                                                   String patientMappingFileLocation,
                                                   int  k) throws SQLException, IOException{
            
               
            return  images.getResults(contrastClipLimit, contrastGridsize, fftAmplitude, extractFeatureRadius, getRandomIndexes(10), patientMappingFileLocation, k);
    }
    
    private static double testElevenRandomColorChannels(Images images, 
                                                      boolean vectorsInDB,
                                                      double  contrastClipLimit, 
                                                      int contrastGridsize,                                                   
                                                      int fftAmplitude, 
                                                      int extractFeatureRadius, 
                                                      String patientMappingFileLocation,
                                                      int  k) throws SQLException, IOException{
        
        
            return  images.getResults(contrastClipLimit, contrastGridsize, fftAmplitude, extractFeatureRadius, getRandomIndexes(11), patientMappingFileLocation, k);
    }
    
    private static double testTwelveRandomColorChannels(Images images, 
                                                      boolean vectorsInDB,
                                                      double  contrastClipLimit, 
                                                      int contrastGridsize,                                                    
                                                      int fftAmplitude, 
                                                      int extractFeatureRadius, 
                                                      String patientMappingFileLocation,
                                                      int  k) throws SQLException, IOException{
            int[]  colorChannelIndexes = new int[]{1,1,1,1,1,1,1,1,1,1,1,1};
            return  images.getResults(contrastClipLimit, contrastGridsize, fftAmplitude, extractFeatureRadius, colorChannelIndexes, patientMappingFileLocation, k);
    }

    
    private static int[] getRandomIndexes(int number){
        List<Integer> numbers = new ArrayList<>();
                
        for (int i = 0; i < 12; i++){
           numbers.add(i);
        }
        
        Collections.shuffle(numbers);
        
        int[] numRandom = new int[number];
        for(int i = 0;i<numRandom.length;i++){
            numRandom[i] = numbers.get(0);
            numbers.remove(0);
             Collections.shuffle(numbers);
        }
        
        int[] a = new int[12];
        for(int i = 0; i<numRandom.length ;i++){
            a[numRandom[i]] = 1;
        }
        return a;
    }
    
}
