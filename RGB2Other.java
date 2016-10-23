import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import javax.imageio.ImageIO;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class RGB2Other {
	
	public void ChangeColourModel(String in){
		try {
		 File input = new File(in);
       
		 BufferedImage image = ImageIO.read(input);
		 byte[] data = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
         Mat mat = new Mat(image.getHeight(),image.getWidth(), CvType.CV_8UC3);
         mat.put(0, 0, data);
		 Mat mat1 = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC3);
         Mat mat2 = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC3);
         Mat mat3 = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC3);
         Mat mat4 = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC3);
         Mat mat5 = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC3);
         Imgproc.cvtColor(mat, mat1, Imgproc.COLOR_RGB2HLS);
         Imgproc.cvtColor(mat, mat2, Imgproc.COLOR_RGB2HSV);
         Imgproc.cvtColor(mat, mat3, Imgproc.COLOR_RGB2Lab);
         Imgproc.cvtColor(mat, mat4, Imgproc.COLOR_RGB2Luv);
         Imgproc.cvtColor(mat, mat5, Imgproc.COLOR_RGB2YUV);
         
         byte[] data1 = new byte[mat1.rows()*mat1.cols()*(int)(mat1.elemSize())];
         mat1.get(0, 0, data1);
         BufferedImage image1 = new BufferedImage(mat1.cols(), mat1.rows(), 5);
         image1.getRaster().setDataElements(0, 0, mat1.cols(), mat1.rows(), data1);
         File ouptut1 = new File("hls.jpg");
         ImageIO.write(image1, "jpg", ouptut1);
         
         byte[] data2 = new byte[mat2.rows()*mat2.cols()*(int)(mat2.elemSize())];
         mat2.get(0, 0, data2);
         BufferedImage image2 = new BufferedImage(mat2.cols(), mat2.rows(), 5);
         image2.getRaster().setDataElements(0, 0, mat2.cols(), mat2.rows(), data2);
         File ouptut2 = new File("hsv.jpg");
         ImageIO.write(image2, "jpg", ouptut2);
         
         byte[] data3 = new byte[mat3.rows()*mat3.cols()*(int)(mat3.elemSize())];
         mat3.get(0, 0, data3);
         BufferedImage image3 = new BufferedImage(mat3.cols(), mat3.rows(), 5);
         image3.getRaster().setDataElements(0, 0, mat3.cols(), mat3.rows(), data3);
         File ouptut3 = new File("lab.jpg");
         ImageIO.write(image3, "jpg", ouptut3);
         
         byte[] data4 = new byte[mat4.rows()*mat4.cols()*(int)(mat4.elemSize())];
         mat4.get(0, 0, data4);
         BufferedImage image4 = new BufferedImage(mat4.cols(), mat4.rows(), 5);
         image4.getRaster().setDataElements(0, 0, mat4.cols(), mat4.rows(), data4);
         File ouptut4 = new File("luv.jpg");
         ImageIO.write(image4, "jpg", ouptut4);
         
         byte[] data5 = new byte[mat5.rows()*mat5.cols()*(int)(mat5.elemSize())];
         mat5.get(0, 0, data5);
         BufferedImage image5 = new BufferedImage(mat5.cols(), mat5.rows(), 5);
         image5.getRaster().setDataElements(0, 0, mat5.cols(), mat5.rows(), data5);
         File ouptut5 = new File("yuv.jpg");
         ImageIO.write(image5, "jpg", ouptut5);
         
	} catch (Exception e) {
        System.out.println("Error: " + e.getMessage());
     }
	}
}
	
