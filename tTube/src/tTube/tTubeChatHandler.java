package tTube;

import static java.lang.System.out;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Iterator;
import java.util.Vector;

/** 실제 클라이언트와 메시지를 주고 받는 스레드 */

public class tTubeChatHandler extends Thread {

	public Socket sock;
	public Vector<tTubeChatHandler> userV;
	public ObjectInputStream in;
	public ObjectOutputStream oos;
	public String nickName, time;

	public static int recommendCount;
	public static String changeTitle;

	tTubeUdpClient tuc = new tTubeUdpClient(this);

	/** 생성자 */
	/* tTubeTcpServer에서 chat.start()로 run()이 작동함 */
	public tTubeChatHandler(Socket sock, Vector<tTubeChatHandler> userV) {
		super();
		this.sock = sock;
		this.userV = userV;
		try {
			in = new ObjectInputStream(this.sock.getInputStream());
			oos = new ObjectOutputStream(this.sock.getOutputStream());
		} catch (IOException e) {
			System.out.println("tTubeChatHandler() 예외: " + e);
		}
	}// -------------

	/* 1번째 tTubeTcpServer에서 chat.start()로 run()이 작동 */
	public void run() {
		// 채팅방에 접속하면 먼저 닉네임을 보냄
		try {
			/** -입장 메시지 */
			Object obj = in.readObject();// tTubeMain으로 부터 닉네임|입장시간 형식의 객체 수신
			if (obj instanceof String) {// objrk String형이라면
				String cmsg = (String) obj;// obj를 String으로 형변환 한 후 cmsg
				String tokens[] = cmsg.split("\\|");// |로 구분하여 배열 tokens에 담음
				this.nickName = tokens[0]; // tokens[0]에 있는것은 닉네임
				this.time = tokens[1];// tokens[1]에 있는것은 입장시간
			}
			boolean isExist = isDuplicateChatName(nickName);// 닉네임 중복여부 체크 중복이면 true
			if (isExist) {// 만약 닉네임이 중복?
				this.sendMessaageTo("700|");// 중복된 닉네임이라면 700을 보냄
				if (tuc.socket != null)
					tuc.socket.close();
			} else {// 중복이 아니면 이미 접속된 사람 정보를 접속한 지금 사람에게 보여줌
				for (tTubeChatHandler userChat : userV) {// tTubeChatHandler 유형의 userV에 userChat정보를 for문
					String msg = "100|" + userChat.nickName + "|" + userChat.time + "|" + recommendCount + "|"
							+ changeTitle;// 100|닉네임|입장시간
					sendMessaageTo(msg);// 100|닉네임|입장시간을 보냄
				}
				userV.add(this);// <tTubeChatHandler> 저장
				String msg = "100|" + this.nickName + "|" + this.time + "|" + recommendCount + "|" + changeTitle;
				// 지금접속한사람의정보를이미접속한사람에게보여줌
				sendMessageAll(msg);// 100|닉네임|입장시간을 보냄
			}

			/** -대화 메시지 */
			/* 대화중일때 tTubeMain으로 부터 내용을 받아옴 */
			while (true) {// 대화할때
				String cMsg = (String) in.readObject();// ObjectInputStream으로 메시지를 듣고 cMsg에 담아
				parsing(cMsg);// parsing으로 보낸다
			}
		} catch (Exception e) {
			System.out.println("tTubeChatHandler run()예외 : " + e);
		}
	}

	/** 대화명 중복여부를 체크 */
	private boolean isDuplicateChatName(String nickName) {
		Iterator<tTubeChatHandler> it = userV.iterator();// userV순차적으로 접근
		while (it.hasNext()) {
			tTubeChatHandler userChat = it.next();
			if (userChat.nickName.contentEquals(nickName)) { // 닉네임이 서로 중복이면
				return true;// true 반환
			}
		}
		return false;
	}

	/** 프로토콜을 파싱하여 프로토콜별로 로직을 처리 */
	private void parsing(String cMsg) {
	      String tokens []= cMsg.split("\\|");//chatandler의 cMsg를 |를 기준으로 분류하여 배열에 넣는다.
	      switch (tokens[0]) {//프로토콜 번호로 구별
	         case "400":{//400|폰트컬러|msg 내용이 담겨있던 sendMsg
	            String fntCr=tokens[1];//폰트컬러
	            String msg=tokens[2];//msg 내용
	            sendMessageAll("400|"+nickName+"|"+fntCr+"|"+msg);//400|닉네임|폰트컬러|msg 정보를 담아 sendMessageAll메소드 호출
	         }break;
	         case "500":{//500|닉네임|추천
	             String nickname=tokens[1];//닉네임
	             recommendCount+=1;//추천수 1 올라가게
	             sendMessageAll("500|"+nickname+"|"+recommendCount);//500|닉네임|추천 정보를 담아 sendMessageAll메소드 호출
	          }break;
	         case "600":{//600|바꿀방송제목
	             changeTitle=tokens[1];//바꿀방송제목
	             sendMessageAll("600|"+changeTitle);//600|바꿀방송제목 정보를 담아 sendMessageAll메소드 호출
	          }break;
	         case "777":{//777|기존닉|바꿀닉
	        	 
					String oldNick=tokens[1];//기존닉
					String newNick=tokens[2];//새닉
					try {
						changeNick(oldNick,newNick);
						sendMessageAll("777|"+oldNick+"|"+newNick);
					} catch (IOException e) {
						System.out.println("tTubeChatHandler parsing 777 에러 ");
					}
					
				}break;
	         case "900":{//900|닉네임
	            String exitChat=tokens[1];//닉네임
	            sendMessageAll("900|"+exitChat);//900|닉네임 정보를 담아 sendMessageAll메소드 호출
	            userV.remove(this);//벡터에서 퇴장하는 클라이언트와 통신하는 ChatHandler 제거
	            closeAll();
	         }break;//closeAll메소드 호출\
	         case "999":{//999|쪽지받는사람|메시지내용|보내는사람
	             String msgUser=tokens[1];
	             String msgNote=tokens[2];
	             String sendUser=tokens[3];
	             try {
					sendPerMessageTo(msgUser,"999|"+msgUser+"|"+msgNote+"|"+sendUser);
				} catch (IOException e) {
					System.out.println("tTubeChatHandler parsing-999쪽지보내기 예외: "+e);
				}
	             
	          }break;
	      }
	   }

	private void closeAll() {
		try {
			if (in != null)
				in.close();
			if (oos != null)
				oos.close();
			if (sock != null) {
				sock.close();
				sock = null;
			}
			if (tuc.socket != null)
				tuc.socket.close();// 모두 close()
		} catch (Exception e) {
			System.out.println("tTubeChatHandler closeAll()예외: " + e);
		}
	}

	/** 서버에 접속해 있는 모든 클에게 메시지를 보냄 */
	private synchronized void sendMessageAll(String msg) {// 400|닉네임|폰트컬러|msg 정보가 담겨있는 String형 msg // 100|닉네임|입장시간
		for (tTubeChatHandler userChat : userV) {// userV에 존재하는 모든 사람에게 돌아가면서
			try {
				userChat.sendMessaageTo(msg);// msg정보가 담긴 sendMessaageTo메소드 호출
			} catch (IOException e) {
				System.out.println("tTubeChatHandler sendMessageAll()예외; " + e);
				userV.removeElement(userChat);
				break;
			}
		}
	}

	/**
	 * 서버와 접속한 특정 클라이언트 한명에게 메시지를 보냄
	 * 
	 * @throws IOException
	 */
	private synchronized void sendMessaageTo(String msg) throws IOException {
		oos.writeObject(msg);// msg정보를 tTubeMain에 전송 ex) 400|닉네임|폰트컬러|메시지 ex)700| ex) 100|닉네임|입장시간
		oos.flush();
	}
	
	/** 쪽지 보냄 ( 닉네임 동일하면 ) */
	   private synchronized void sendPerMessageTo(String msgUser,String msg) throws IOException {
		  for(tTubeChatHandler userChat :userV) {
			  if(userChat.nickName.equals(msgUser)) {
				  userChat.sendMessaageTo(msg);
			  }
		  }
	   }
	   
	   /** 닉네임 변경 */
	   private synchronized void changeNick(String oldNick,String newNick) throws IOException {
			  for(tTubeChatHandler userChat :userV) {
				  if(userChat.nickName.equals(oldNick)) {
					  userChat.nickName=newNick;
					  return;
				  }
			  }
		   }
}