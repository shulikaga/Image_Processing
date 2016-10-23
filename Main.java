
import org.opencv.core.Core;
public class Main {
   public static void main( String[] args ) {
   
      try {
         System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
         RGB2Other rgb2other=new RGB2Other();
      
         String input = "cosy.jpg";
         rgb2other.ChangeColourModel(input);
//      									   String in, double alpha, double beta 
         ContrastEnchancement changecontrast = new ContrastEnchancement(input,2,50);
         
      } catch (Exception e) {
         System.out.println("Error: " + e.getMessage());
      }
   }
}