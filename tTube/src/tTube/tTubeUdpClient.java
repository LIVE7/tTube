package tTube;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import javax.imageio.ImageIO;

/**시청하기에서 영상을 받아올때 사용하는 UDP CLient*/

public class tTubeUdpClient implements Runnable{
	
	tTubeMain ttm;
	DatagramSocket socket;
	
 	public tTubeUdpClient(tTubeMain ttm) {
		this.ttm = ttm;
 	}
 	tTubeChatHandler tch;
	public tTubeUdpClient(tTubeChatHandler tch) {
		this.tch = tch;
 	}

	public void connect() {
		
		try {
			socket = new DatagramSocket(ttm.port);
			System.out.println("socket");
			Thread listener = new Thread(this);
			listener.start();
		} catch (SocketException e) {
			System.out.println("tTubeUdpClient connect() 예외: "+e);
		}
		
	}
	@Override
	public void run() {
		 try {
			while(true){
	        	byte[] buffer = new byte[65508]; //한번에 받을 수 있는 최대 용량의 데이터 공간은 기본정보 공간을 제외한 65508
	        	DatagramPacket packet = new DatagramPacket(buffer,0, buffer.length);
	        	socket.receive(packet);
	        	//System.out.println("받는중"+buffer.length);
	            byte[] buff = packet.getData();
	            ByteArrayInputStream bain = new ByteArrayInputStream(buff);
	            ttm.pnWatchState.DrawImage(ImageIO.read(bain));
	        }
		} catch (IOException e) {
			System.out.println("tTubeUdpClient run() 예외: "+e);
		}
	}

}
