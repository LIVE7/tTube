package tTube;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

/**방송시작을 누르면 영상부분은 UDP서버를 사용*/

public class tTubeUdpServer implements Runnable{
	
	public DatagramSocket socket;
	public InetAddress ia;
	public int port;
	
	tTubeMain ttm;
	public tTubeUdpServer(tTubeMain ttm) {
		this.ttm = ttm;
 	}
	

	public void UDPserverStart() {
		try {
			socket = new DatagramSocket();//새로운 데이터그램 소켓 생성 보내주는쪽에서는 DatagramSocket() 안에 포트번호를 넣지 않는다.
			ia = InetAddress.getByName("192.168.0.255");//udp영상을 보내주고 싶은 ip주소 설정 192.168.0.255 => 192.168.0. 으로 시작되는 모든 ip주소에 전송
			port = ttm.port;//udp영상을 보내주고 싶은 포트번호 설정
			Thread listener = new Thread(this);
			listener.start();// 스레드 실행
		} catch (IOException e) {
			System.out.println("tTubeUdpServer() 예외: "+e);
		}
	}
	@Override
	/**원리: 프레임 마다 jpg방식 이미지로 변환하여 보낸다 받을때는 그 이미지를 비디오패널에 계속 띄워주는 방식*/
	public void run() {		   
		try {
			while (true) {
				ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);//1024의 버퍼를 담는 ByteArrayOutputStream형인 baos를 생성
				ImageIO.write(tTubeCam.webcam.getImage(), "jpg", baos);//tTubeCam의 영상 프레임마다 jpg방식의 이미지로 만들어 baos에 넣음
				baos.flush();//flush
				byte[] image_buffer = baos.toByteArray();//baos를 바이트 형으로 변환하여 image_buffer 바이트 배열에 담음
//				System.out.println(image_buffer.length+"바이트 전송했습니다.");
				DatagramPacket packet = new DatagramPacket(image_buffer, 0, image_buffer.length, ia, port);//보내주고 싶은 ip주소와 포트번호에 DatagramPacket을 생성하여 image_buffer길이의 데이터를 담는다
				socket.send(packet);//소켓을 통해 packet보내줌

				ByteArrayInputStream bain = new ByteArrayInputStream(image_buffer);//방송하기 패널에서만 해당 image_buffer를 바로 받아와서 ByteArrayInputStream형으로 변환
				ttm.pnBjWebcam.DrawImage(ImageIO.read(bain));//tTubeMain의 pnBjWebcam에 해당 프레임 이미지를 띄움
			}
		} catch (Exception e) {
			System.out.println("tTubeUdpServer run() 예외:"+e);
		}
	}
}
