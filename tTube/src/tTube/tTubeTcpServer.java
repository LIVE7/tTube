package tTube;

import static java.lang.System.out;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

/**영상을 제외한 모든 통신은 TCP Server를 사용*/

public class tTubeTcpServer implements Runnable{
	public ServerSocket server;//서버를 가동하기 위한ServerSocket
	public int port;//해당 포트 번호와 내 ip주소로 접속했을때만 연결 가능
	public Vector<tTubeChatHandler> userV = new Vector<tTubeChatHandler>(5,3);//유저정보를 받는 벡터 userV (초기용량 5, 증가치 3)

	tTubeMain ttm;
	public tTubeTcpServer(tTubeMain ttm) {
		this.ttm = ttm;
 	}
	
	
	public void TCPserverStart() {
		try {
			port=ttm.port;
			server=new ServerSocket(port);
			out.println("### 채팅서버가 시작됨 ###");
			out.println("##["+port+"]번 포트에서 대기중 . . .");
			Thread listener = new Thread(this);//스레드 시작
			listener.start();
		} catch (IOException e) {
			out.println("TCPserverStart() 예외: "+e);
		}
	}

	//채팅방 접속을 기다리고 접속이 되면 tTubeChatHandler 생성후 동작
	@Override
	public void run() {
		while(true) {
			try {
				Socket sock = server.accept();//채팅방 접속을 감지
				out.println("###["+sock.getInetAddress()+"] 님이 접속했습니다###");

				tTubeChatHandler chat= new tTubeChatHandler(sock, userV);//-기본 생성자 //-chatHandler유형의 클래스에서 생성자 속성을 맞춰 건내주는 객체 생성
				chat.start();//tTubeChatHandler의 스레드가 시작
			} catch (Exception e) {
				out.println("TCPserverStart run() 예외: "+e);
			}
		}
	}
}