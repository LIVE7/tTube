package tTube;

import java.awt.event.KeyAdapter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import javafx.scene.input.KeyEvent;

public class tTubeLogin extends javax.swing.JFrame {

	public String host;
	public String port;
	public String nickname;
	public String time;
	public tTubeMain ttm;
	public tTubeMain ttubemain;
	private boolean isHideText = true;
	private final int tfLimitNum = 8;
	
	/** 생성자 */
	public tTubeLogin() {
		initComponents();
	}

	public tTubeLogin(tTubeMain ttm) {
		this.ttm = ttm;
	}

	/** 현재시간을 구하는 메소드 */
	public String getTime() {
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("hh시 mm분");
		String str = sdf.format(d);
		return str;
	}

	/** 메시지를 띄울때 사용하는 메소드 */
	private void showMsg(String message) {
		JOptionPane.showMessageDialog(this, message);
	}

	/** 시청하기 버튼을 눌렀을때 */
	private void btWatchActionPerformed(java.awt.event.ActionEvent evt) {
		String inputNickname = tfNickname.getText();
		
		//닉네임 유효성 검사
		if (inputNickname == null || inputNickname.trim().isEmpty()) {
			showMsg("닉네임을 입력하세요~~");
			tfNickname.setText("");
			tfNickname.requestFocus();
			return;
		}

		String connectAddress = JOptionPane.showInputDialog(this,
				inputNickname + "님 접속을 환영합니다~~ \n접속하실 ip주소와 포트번호를 입력하세요~~\nex) 192.168.0.1:3000       [IP주소] : [포트번호]");
		if (connectAddress == null || connectAddress.trim().isEmpty()) {
			showMsg("ip주소와 포트번호를 예시된 양식에 맞춰 다시 입력하세요\n ex) 192.168.0.1:3000");
			return;
		}
		
		//입력된connectAddress의 양식과, ip주소, 포트번호의 유효성을 검사
		if(connectAddress.contains(":")) {
			String tokens[] = connectAddress.split("\\:");
			
			if(!isCheckHost(tokens[0])) {
				showMsg("ip주소를 다시 입력하세요\n ex) 0.0.0.0 ~ 255.255.255.255");
				return;
				
			} else if(!isCheckPort(tokens[1])) {
				showMsg("포트번호는 1024 ~ 49151번 사이의 숫자로 다시 입력하세요\n ex) 192.168.0.1:3000");
				return;
				
			} else {
				//시청시작
				this.nickname = inputNickname; // tfNickname에서 텍스트를 받아 nickname으로
				this.time = getTime(); // 현재시간정보를 받아 time으로
				this.host = tokens[0];
				this.port = tokens[1];
				
				ttubemain = new tTubeMain("watch", this);
				ttubemain.setVisible(true);
				this.dispose();
				
			}
			
		} else {
			showMsg("ip주소와 포트번호를 예시된 양식에 맞춰 다시 입력하세요\n ex) 192.168.0.1:3000");
			return;
		}
	}


	/** 방송하기 버튼을 눌렀을때 */
	private void btBroadcastActionPerformed(java.awt.event.ActionEvent evt) {
		String inputNickname = tfNickname.getText();
		
		//닉네임 유효성 검사
		if (inputNickname == null || inputNickname.trim().isEmpty()) {
			showMsg("닉네임을 입력하세요~~");
			tfNickname.setText("");
			tfNickname.requestFocus();
			return;
		}

		String strPort = JOptionPane.showInputDialog(this,
				inputNickname + "님 접속을 환영합니다~ \n서버를 개설할때 필요한 포트번호를 입력해주세요~\nex)포트번호는 1024 ~ 49151번 사이에서 입력");
		if (strPort == null || strPort.trim().isEmpty()) {
			showMsg("포트번호를 제대로 입력하세요~");
			return;
		}
		
		//입력된 포트번호의 유효성을 검사
		if(!isCheckPort(strPort)) {
			showMsg("포트번호는 1024 ~ 49151번 사이의 숫자로 다시 입력하세요");
			return;
		
		} else {
			//방송시작
			this.nickname = inputNickname;
			this.time = getTime();
			this.port = strPort;
			
			//유효한 포트번호 등록
			
			ttubemain = new tTubeMain("broadcast", this);
			ttubemain.setVisible(true);
			this.dispose();
		}
	}

	/** ip주소의 유효성을 검사 */
	private boolean isCheckHost(String host) {
		String regex = "^([01]?\\d?\\d|2[0-4]\\d|25[0-5])\\."
						+ "([01]?\\d?\\d|2[0-4]\\d|25[0-5])\\."
						+ "([01]?\\d?\\d|2[0-4]\\d|25[0-5])\\."
						+ "([01]?\\d?\\d|2[0-4]\\d|25[0-5])$";
//		ip주소 정규식 : ^([01]?\d?\d|2[0-4]\d|25[0-5])\.([01]?\d?\d|2[0-4]\d|25[0-5])\.([01]?\d?\d|2[0-4]\d|25[0-5])\.([01]?\d?\d|2[0-4]\d|25[0-5])$
		
		boolean isHostRegex = Pattern.matches(regex, host);
		
		if(isHostRegex) {
			return true;
		} else {
			return false;			
		}
	}
	
	/** port번호의 유효성을 검사 */
	private boolean isCheckPort(String port) {
		try {
			int iPort = Integer.parseInt(port);
			if(1024 <= iPort && iPort <= 49151) {
				return true;
			} else {
				return false;
			}
		} catch(NumberFormatException e) {
			return false;
		}
	}

	/**
	 * NetBean - initComponents()
	 */
	private void initComponents() {
		
		setResizable(false);

		pnBase = new javax.swing.JPanel();
		btBroadcast = new javax.swing.JButton();
		btWatch = new javax.swing.JButton();
		tfNickname = new javax.swing.JTextField();
		lbLogo = new javax.swing.JLabel();

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

		pnBase.setBackground(new java.awt.Color(0, 51, 124));

		btBroadcast.setBackground(new java.awt.Color(0, 51, 124));
		btBroadcast.setFont(new java.awt.Font("나눔바른고딕", 0, 14)); // NOI18N
		btBroadcast.setForeground(new java.awt.Color(255, 255, 255));
		btBroadcast.setText("방송하기");
		btBroadcast.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btBroadcastActionPerformed(evt);
			}
		});

		btWatch.setBackground(new java.awt.Color(0, 51, 124));
		btWatch.setFont(new java.awt.Font("나눔바른고딕", 0, 14)); // NOI18N
		btWatch.setForeground(new java.awt.Color(255, 255, 255));
		btWatch.setText("시청하기");
		btWatch.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btWatchActionPerformed(evt);
			}
		});

		tfNickname.setBackground(new java.awt.Color(0, 51, 124));
		tfNickname.setFont(new java.awt.Font("나눔바른고딕", 0, 18)); // NOI18N
		tfNickname.setForeground(new java.awt.Color(255, 255, 255));
		tfNickname.setHorizontalAlignment(javax.swing.JTextField.CENTER);
		tfNickname.setToolTipText("");
		tfNickname.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));
		tfNickname.setDocument(new JTextFieldLimit(8));
		tfNickname.setText("이름을입력하세요");
		tfNickname.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				if(isHideText) {
					tfNickname.setText("");
					isHideText = false;
				}
			}
		});

		lbLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ttube/images/logo_cha.png"))); // NOI18N

		javax.swing.GroupLayout pnBaseLayout = new javax.swing.GroupLayout(pnBase);
		pnBase.setLayout(pnBaseLayout);
		pnBaseLayout.setHorizontalGroup(pnBaseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(pnBaseLayout.createSequentialGroup().addGap(80, 80, 80)
						.addComponent(lbLogo, javax.swing.GroupLayout.PREFERRED_SIZE, 184,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
				.addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
						pnBaseLayout.createSequentialGroup().addContainerGap(52, Short.MAX_VALUE)
								.addGroup(pnBaseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
										.addComponent(tfNickname, javax.swing.GroupLayout.PREFERRED_SIZE, 240,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addGroup(pnBaseLayout.createSequentialGroup()
												.addComponent(btWatch, javax.swing.GroupLayout.PREFERRED_SIZE, 112,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addGap(18, 18, 18).addComponent(btBroadcast,
														javax.swing.GroupLayout.PREFERRED_SIZE, 114,
														javax.swing.GroupLayout.PREFERRED_SIZE)))
								.addGap(51, 51, 51)));
		pnBaseLayout.setVerticalGroup(pnBaseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(pnBaseLayout.createSequentialGroup().addGap(58, 58, 58)
						.addComponent(lbLogo, javax.swing.GroupLayout.PREFERRED_SIZE, 60,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 51, Short.MAX_VALUE)
						.addComponent(tfNickname, javax.swing.GroupLayout.PREFERRED_SIZE, 52,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGap(28, 28, 28)
						.addGroup(pnBaseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
								.addComponent(btWatch, javax.swing.GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)
								.addComponent(btBroadcast, javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
						.addGap(45, 45, 45)));

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
						.addComponent(pnBase, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGap(0, 0, Short.MAX_VALUE)));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(
				pnBase, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
				javax.swing.GroupLayout.PREFERRED_SIZE));

		pack();
		
		btWatch.requestFocus();		
	}// </editor-fold>

	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[]) {
		/* Set the Nimbus look and feel */
		// <editor-fold defaultstate="collapsed" desc=" Look and feel setting code
		// (optional) ">
		/*
		 * If Nimbus (introduced in Java SE 6) is not available, stay with the default
		 * look and feel. For details see
		 * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
		 */
		try {
			for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					javax.swing.UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException ex) {
			java.util.logging.Logger.getLogger(tTubeLogin.class.getName()).log(java.util.logging.Level.SEVERE, null,
					ex);
		} catch (InstantiationException ex) {
			java.util.logging.Logger.getLogger(tTubeLogin.class.getName()).log(java.util.logging.Level.SEVERE, null,
					ex);
		} catch (IllegalAccessException ex) {
			java.util.logging.Logger.getLogger(tTubeLogin.class.getName()).log(java.util.logging.Level.SEVERE, null,
					ex);
		} catch (javax.swing.UnsupportedLookAndFeelException ex) {
			java.util.logging.Logger.getLogger(tTubeLogin.class.getName()).log(java.util.logging.Level.SEVERE, null,
					ex);
		}
		// </editor-fold>

		/* Create and display the form */
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new tTubeLogin().setVisible(true);
			}
		});

	}

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JButton btBroadcast;
	private javax.swing.JButton btWatch;
	private javax.swing.JLabel lbLogo;
	private javax.swing.JPanel pnBase;
	private javax.swing.JTextField tfNickname;
	private javax.swing.JLabel icCha;
	// End of variables declaration//GEN-END:variables
}


class JTextFieldLimit extends PlainDocument {
	private int limit = 8; // 제한할 길이

	public JTextFieldLimit(int limit) // 생성자 : 제한할 길이를 인자로 받음
	{
		super();
		this.limit = limit;
	}

	// 텍스트 필드를 채우는 메써드 : 오버라이드
	public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
		if (str == null)
			return;
		if (getLength() + str.length() <= limit)
			super.insertString(offset, str, attr);
	}
}
