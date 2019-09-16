package tTube;

import java.io.BufferedInputStream;
import java.io.FileInputStream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class wavSound {
	
	Clip clip;
	
	public void sound(String file, boolean Loop){

		//사운드재생용메소드

		//사운드 파일을 받아들여 해당사운드를 재생


			try {
				AudioInputStream ais = AudioSystem.getAudioInputStream(new BufferedInputStream(new FileInputStream(file)));
				clip = AudioSystem.getClip();
				clip.open(ais);
				clip.start();
	
				if (Loop) clip.loop(-1);
				//Loop 값이 true면 사운드재생을 무한반복
				//false면 한번만 재생

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	public void soundstop() {
		clip.stop();
	}

}

