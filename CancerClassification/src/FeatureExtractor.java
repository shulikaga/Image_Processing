
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;


public class FeatureExtractor {

    static  void  extractFeature(double[][] vector, Mat colorChannel,String channelName, String colorSpaceName, int radius) {
        
       colorChannel.convertTo(colorChannel, CvType.CV_64F);
       
        
        int vectorColorSpace = 0;
        
        if(colorSpaceName.equals("HSV")){
            vectorColorSpace = 0;
        }else if(colorSpaceName.equals("HLS")){
            vectorColorSpace = 1;
        }else if(colorSpaceName.equals("Lab")){
            vectorColorSpace = 2;
        }else if(colorSpaceName.equals("YUV")){
            vectorColorSpace = 3;
        }
        
        int vectorColorChannel = 0;
        
        if(channelName.equals("red")){
            vectorColorChannel = 0;
        }else if(channelName.equals("green")){
            vectorColorChannel = 1;
        }else if(channelName.equals("blue")){
            vectorColorChannel = 2;
        }
        
            
        Mat rect;
        Mat circle;
        
        double lastSum = 0;   
        double currentSum = 0;
        
        double currentMidle = 0;
      
        int lastValuesCounter = 0; 
        int currentValuesCounter = 0; 
        int valuesCounter = 0; 
        double currentRingSum =0;
        int middle = colorChannel.rows()/2;
        
        double[] circleArray;
        int vectorCounter = 0;

        for (int c = radius;middle+c<colorChannel.rows(); c=c+radius){
             rect = new Mat(colorChannel,new Rect(middle-c, middle-c, 2*c , 2*c));
           //  circle = colorChannel.submat(rect);
             
             circleArray = new double[(int)rect.total()];
             rect.get(0, 0, circleArray);
             
             for(int i = 0; i<rect.rows(); i++){
                 for(int j = 0; j < rect.cols(); j++){
                     
                     if(((i-c)*(i-c)+(j-c)*(j-c)) <= c*c){
                          currentSum += circleArray[(rect.rows()-1)*i+j];
                          valuesCounter++;
                     if(c ==7){
                         
                     System.out.print(circleArray[(rect.rows()-1)*i+j]+" ");}
                     }
                 }
                 System.out.println();
             }
             
          
             currentValuesCounter = valuesCounter-lastValuesCounter;
             currentRingSum = currentSum - lastSum;
             
              System.out.println(lastSum+" = lastSum");
              System.out.println(currentSum+" = currentSum");
              System.out.println(currentRingSum+" = currentRingSum");
              System.out.println(valuesCounter+" = valuesCounter");
              System.out.println(lastValuesCounter+" = lastValuesCounter");
              System.out.println(currentValuesCounter+" = currentValuesCounter");
              
            
             currentMidle = currentRingSum/currentValuesCounter;
             vector[vectorColorSpace+vectorColorChannel][vectorCounter] = currentMidle;
             
             System.out.println(vector[vectorColorSpace+vectorColorChannel][vectorCounter]+" - "+vectorCounter);
             
             lastValuesCounter = valuesCounter;           
             lastSum = currentSum;
             
             valuesCounter=0;
             currentSum = 0;   
             
             vectorCounter++;
             System.out.println();
        }
        
        //the last ring 
             circleArray = new double[colorChannel.rows()*colorChannel.cols()];
            
             for(int i = 0; i<colorChannel.rows(); i++){
                 for(int j = 0; j < colorChannel.cols(); j++){
                     //(a-r)(a-r)+(b-r)(b-r) <= r*r. (a,b) indexes
                     if(((i-radius)*(i-radius)+(j-radius)*(j-radius)) <= radius*radius){
                     
                     currentSum += circleArray[colorChannel.rows()*i+j];
                     valuesCounter++;
                     }
                 }
          System.out.println();

             }
            
             currentValuesCounter = valuesCounter-lastValuesCounter;
             currentSum = currentSum - lastSum;
            
             currentMidle = currentSum/currentValuesCounter;
             vector[vectorColorSpace+vectorColorChannel][vectorCounter+1] = currentMidle;
             
             
             lastValuesCounter += currentValuesCounter;           
             lastSum+= currentSum;
             
             valuesCounter=0;
             currentSum = 0; 
             
             
             
             
    }
}
