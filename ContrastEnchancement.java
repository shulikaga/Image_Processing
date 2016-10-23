import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

public class ContrastEnchancement {

	public ContrastEnchancement(String in, double alpha, double beta) {
		try {

			Mat source = Imgcodecs.imread(in, Imgcodecs.CV_LOAD_IMAGE_COLOR);
			Mat destination = new Mat(source.rows(), source.cols(), source.type());
			source.convertTo(destination, 1, alpha, beta);

			Imgcodecs.imwrite("0" + in, destination);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
