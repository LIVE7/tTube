package tTube;

import javax.swing.JFrame;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;

public class tTubeCam {
	
	static Webcam webcam;
	
		public static void cam() {
			
		webcam = Webcam.getDefault();
		webcam.setViewSize(WebcamResolution.QVGA.getSize());
		WebcamPanel panel = new WebcamPanel(webcam);
		panel.setFPSDisplayed(true);
		panel.setDisplayDebugInfo(true);
		panel.setImageSizeDisplayed(true);
		panel.setMirrored(true);
		
		JFrame window = new JFrame("미리보기");
		window.add(panel);
		window.setResizable(true);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.pack();
		window.setVisible(false);

		}
	     public static void main(String[] args) {
	    	 cam();
	}

}
