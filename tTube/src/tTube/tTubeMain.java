package tTube;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class tTubeMain extends javax.swing.JFrame implements Runnable {

   @Override
   public String toString() {
      return "tTubeMain [nickname=" + nickname + "]";
   }
   
   /**방송하기 하실때 원하는 포트번호 입력하셔서 방송시작을 눌러서 서버를 실행하신후
    * 시청하기로 로그인 할때 [접속할 아이피 주소:접속할 포트번호] 입력하시면 접속됩니다.
    * ex) 방송하는 사람이 포트번호 2222로 설정후 방송시작 했다면
    * 시청하기 했을시 192.168.0.00:2222 입력하고 접속
    * 자신의 컴퓨터로 확인하고 싶을때는 (포트번호 2222)
    * 시청하기 눌렀을때 
    * :2222
    * localhost:2222
    * 내ip주소:2222
    * 이렇게 셋중에 하나만 입력하면 됩니다.
    * */
   
   
   /** 전역변수가 들어갈 자리*/
   
   public Socket sock;
   public String host;
   public int port;
   
   public ObjectOutputStream oos;//객체 보내기
   public ObjectInputStream in;//객체 받기
    
   public boolean isStop=false;//대화중
   public boolean isSendOne=false; //쪽지?
   
   public boolean serverStart=false;//서버 시작 유무확인
    
   public static String nickname;// 닉네임
   public String time;// 입장시간
   public static String bjInfo;

   public static final int EXIT = 0;// 퇴장
   
   public String changeNickname;//닉네임 변경할때 사용할 변수
   public String changeTitle;//방송제목 변경할때 사용
   
   public int fontColor=Color.black.getRGB();//글꼴색(디폴트 검정)
   
   public wavSound sound;//소리가 나게 해준다
   
   
   //tTube 메인 컬러 
   public Color tBlue = new Color(0, 51, 124);
   public Color tBluePale = new Color(232, 243, 249);
   public Color tBlueLight = new Color(196, 225, 245);
   public Color tBlueMedium = new Color(80, 124, 177);
  
   
   
//   public int recommendCount;//추천수
	
	StyledDocument doc;
   
   /** 비디오 패널 관련 전역변수 생성 */
   tTube.VideoPanel pnBjWebcam;
   tTube.VideoPanel pnWatchState;

   /** 전역변수는 여기까지 */

   


   private tTubeLogin ttubelogin;
   tTubeUdpClient tuc = new tTubeUdpClient(this);
   tTubeTcpServer tts = new tTubeTcpServer(this);
   tTubeLogin ttl = new tTubeLogin(this);
   tTubeUdpServer tus =new tTubeUdpServer(this);
   tTubeBoard ttb = new tTubeBoard();
   tTubeSendMsg tsm = null;
   tTubeGetMsg tgm = null;
   Container gp=(Container)this.getGlassPane();
   
   
   
   /** 생성자 */

   public tTubeMain(String identification, tTubeLogin ttubelogin) {
      initComponents();
      this.ttubelogin = ttubelogin;
      this.nickname = this.ttubelogin.nickname;//ttubelogin의 닉네임을 가져와서 this.nickname에
      this.time = this.ttubelogin.time;//ttubelogin의 time값을 가져와서 this.time에
      this.host = this.ttubelogin.host;//ttubelogin의 host값을 가져와서 this.host에
      this.port = Integer.parseInt(this.ttubelogin.port);//ttubelogin의 port값을 가져와서 this.port에 넣고 int형 변환

      /**시청하기로 로그인 했을때*/
      if (identification == "watch") {
         setEnabledTab(1, 0);//시청하기탭 활성화
         tuc.connect();//방송영상을 보기위한 udpClient 연결
         chatConnect();//채팅창을 활성화 시키기 위한 chatConnect
         lbNickname.setText(nickname);//패널 우측 상단에 닉네임을 띄움
      }
      /** 방송하기로 로그인 했을때 */
      if (identification == "broadcast") {
         setEnabledTab(0, 1);//방송하기탭 활성화
         tTubeCam.cam();// 캠띄우기 (tTubeCam에서 setvisible이 false이므로 보이지는 않음)
         lbNickname.setText(nickname);//패널 우측 상단에 닉네임을 띄움
         setTitle(nickname);
      }
   }

   /** 메시지 띄우고 싶을때 쓰는 메소드 */
   public void showMsg(String message) {
      JOptionPane.showMessageDialog(this, message);
   }

   /** 탭 활성화 관련 메소드 */
   private void setEnabledTab(int enableTab, int disableTab) {
      this.jTabbedPane1.setEnabledAt(enableTab, true);
      this.jTabbedPane1.setEnabledAt(disableTab, false);
      this.jTabbedPane1.setSelectedIndex(enableTab);// 활성화된 탭을 자동으로 선택
   }

   @SuppressWarnings("unchecked")

   

   private void initComponents() {
	   
	   setResizable(false);

      /** 비디오 패널 객체 생성 */
      pnBjWebcam = new tTube.VideoPanel();
      this.pnWatchState = new tTube.VideoPanel();
      /** 비디오 패널 사이즈 설정 */
      pnWatchState.setPreferredSize(new Dimension(320, 240));
      this.pnWatchState.setPreferredSize(new Dimension(320, 240));

      Color tBlue = new Color(0, 51, 124);
      
      
      pnBase = new javax.swing.JPanel();
      jTabbedPane1 = new javax.swing.JTabbedPane();
      pnBj = new javax.swing.JPanel();
      tfBjtTubeTitle = new javax.swing.JTextField();
      lbBjRecommendCount = new javax.swing.JLabel();
      lbBjTitleIcon = new javax.swing.JLabel();
      pnBjBts = new javax.swing.JPanel();
      btBjBoard = new javax.swing.JLabel();
      btBjStart = new javax.swing.JLabel();
      lbBjBoardInfo = new javax.swing.JLabel();
      lbBjRecommendInfo = new javax.swing.JLabel();
      lbBjBts = new javax.swing.JLabel();
      btBjChange = new javax.swing.JLabel();
      pnWatch = new javax.swing.JPanel();
      pnWatchBts = new javax.swing.JPanel();
      btWatchRecommend = new javax.swing.JLabel();
      btWatchBoard = new javax.swing.JLabel();
      lbWatchRecommendInfo = new javax.swing.JLabel();
      lbWatchBoardInfo = new javax.swing.JLabel();
      lbWatchBjName = new javax.swing.JLabel();
      lbWatchBts = new javax.swing.JLabel();
      lbWatchtTubeTitle = new javax.swing.JLabel();
      lbWatchRecommendCount = new javax.swing.JLabel();
      lbRecoBg = new javax.swing.JLabel();
      lbWatchTitleIcon = new javax.swing.JLabel();
      jScrollPane1 = new javax.swing.JScrollPane();
      userTable = new javax.swing.JTable();
      jScrollPane2 = new javax.swing.JScrollPane();
      tpMessage = new javax.swing.JTextPane();
      tfChat = new javax.swing.JTextField();
      comboColor = new javax.swing.JComboBox<>();
      lbNickname = new javax.swing.JLabel();
      lbNickHi = new javax.swing.JLabel();
      btMsg = new javax.swing.JLabel();
      btChangeNickname = new javax.swing.JLabel();
      btLogout = new javax.swing.JLabel();

      //창닫기 이벤트 처리
        addWindowListener(new WindowAdapter() {
           public void windowClosing(WindowEvent e) {
              exitProcess();
           }
        });

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

//      setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

      pnBase.setBackground(new java.awt.Color(255, 255, 255));

      jTabbedPane1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
      jTabbedPane1.setFont(new java.awt.Font("나눔바른고딕", 1, 14)); // NOI18N

      pnBj.setBackground(new java.awt.Color(255, 255, 255));

      pnBjWebcam.setBackground(new java.awt.Color(255, 255, 255));
      pnBjWebcam.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
      pnBjWebcam.setPreferredSize(new java.awt.Dimension(320, 240));

      javax.swing.GroupLayout pnBjWebcamLayout = new javax.swing.GroupLayout(pnBjWebcam);
      pnBjWebcam.setLayout(pnBjWebcamLayout);
      pnBjWebcamLayout.setHorizontalGroup(pnBjWebcamLayout
            .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 318, Short.MAX_VALUE));
      pnBjWebcamLayout.setVerticalGroup(pnBjWebcamLayout
            .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 238, Short.MAX_VALUE));

      tfBjtTubeTitle.setFont(new java.awt.Font("나눔바른고딕", 0, 14)); // NOI18N
      tfBjtTubeTitle.setText("　");
      tfBjtTubeTitle.setToolTipText("");
      tfBjtTubeTitle.setBorder(javax.swing.BorderFactory.createEtchedBorder());
      tfBjtTubeTitle.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            //tfBjtTubeTitleActionPerformed(evt);
         }
      });

      lbBjRecommendCount.setFont(new java.awt.Font("나눔고딕", 1, 14)); // NOI18N

      lbBjTitleIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/tTube/images/icPlay2.png"))); // NOI18N

      pnBjBts.setLayout(null);

      btBjBoard.setIcon(new javax.swing.ImageIcon(getClass().getResource("/tTube/images/icBtTrans.png"))); // NOI18N
      pnBjBts.add(btBjBoard);
      btBjBoard.setBounds(130, 10, 70, 90);
      btBjBoard.addMouseListener(btHover);

      btBjStart.setIcon(new javax.swing.ImageIcon(getClass().getResource("/tTube/images/icBtTrans.png"))); // NOI18N
      pnBjBts.add(btBjStart);
      btBjStart.setBounds(210, 10, 70, 90);
      btBjStart.addMouseListener(btHover);

      lbBjBoardInfo.setFont(new java.awt.Font("나눔바른고딕", 1, 14)); // NOI18N
      lbBjBoardInfo.setForeground(new java.awt.Color(51, 51, 51));
      lbBjBoardInfo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
      lbBjBoardInfo.setText("");
      pnBjBts.add(lbBjBoardInfo);
      lbBjBoardInfo.setBounds(120, 60, 80, 30);

      lbBjRecommendInfo.setFont(new java.awt.Font("나눔바른고딕", 1, 18)); // NOI18N
      lbBjRecommendInfo.setForeground(new java.awt.Color(0, 51, 124));
      lbBjRecommendInfo.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
      lbBjRecommendInfo.setText("0");
      pnBjBts.add(lbBjRecommendInfo);
      lbBjRecommendInfo.setBounds(60, 30, 50, 40);

      lbBjBts.setIcon(new javax.swing.ImageIcon(getClass().getResource("/tTube/images/imgBjBg.png"))); // NOI18N
      lbBjBts.setText("jLabel1");
      lbBjBts.setPreferredSize(new java.awt.Dimension(365, 102));
      pnBjBts.add(lbBjBts);
      lbBjBts.setBounds(-20, 0, 320, 120);

      btBjChange.setIcon(new javax.swing.ImageIcon(getClass().getResource("/tTube/images/icBjChange.png"))); // NOI18N
      btBjChange.addMouseListener(btHover);

      javax.swing.GroupLayout pnBjLayout = new javax.swing.GroupLayout(pnBj);
      pnBj.setLayout(pnBjLayout);
      pnBjLayout
            .setHorizontalGroup(pnBjLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                  .addGroup(pnBjLayout.createSequentialGroup().addContainerGap().addGroup(pnBjLayout
                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(
                              javax.swing.GroupLayout.Alignment.TRAILING,
                              pnBjLayout.createSequentialGroup().addGap(0, 0, Short.MAX_VALUE)
                                    .addComponent(lbBjRecommendCount).addGap(181, 181, 181))
                        .addGroup(pnBjLayout.createSequentialGroup().addGroup(pnBjLayout
                              .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                              .addGroup(pnBjLayout.createSequentialGroup().addGap(9, 9, 9)
                                    .addComponent(lbBjTitleIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 18,
                                          javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(tfBjtTubeTitle, javax.swing.GroupLayout.PREFERRED_SIZE,
                                          218, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(btBjChange))
                              .addComponent(pnBjWebcam, javax.swing.GroupLayout.PREFERRED_SIZE,
                                    javax.swing.GroupLayout.DEFAULT_SIZE,
                                    javax.swing.GroupLayout.PREFERRED_SIZE))
                              .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                  .addGroup(pnBjLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(pnBjLayout.createSequentialGroup().addGap(32, 32, 32)
                              .addComponent(pnBjBts, javax.swing.GroupLayout.PREFERRED_SIZE, 293,
                                    javax.swing.GroupLayout.PREFERRED_SIZE)
                              .addContainerGap(19, Short.MAX_VALUE))));
      pnBjLayout.setVerticalGroup(pnBjLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnBjLayout.createSequentialGroup().addContainerGap()
                  .addComponent(pnBjWebcam, javax.swing.GroupLayout.PREFERRED_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                  .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                  .addComponent(lbBjRecommendCount)
                  .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                  .addGroup(pnBjLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(lbBjTitleIcon, javax.swing.GroupLayout.DEFAULT_SIZE,
                              javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(tfBjtTubeTitle).addComponent(btBjChange,
                              javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                              Short.MAX_VALUE))
                  .addContainerGap(125, Short.MAX_VALUE))
            .addGroup(pnBjLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                  javax.swing.GroupLayout.Alignment.TRAILING,
                  pnBjLayout.createSequentialGroup().addContainerGap(315, Short.MAX_VALUE).addComponent(pnBjBts,
                        javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())));

      jTabbedPane1.addTab("방송하기", pnBj);

      pnWatch.setBackground(new java.awt.Color(255, 255, 255));

      pnWatchBts.setLayout(null);

      btWatchRecommend.setIcon(new javax.swing.ImageIcon(getClass().getResource("/tTube/images/icBtTrans.png"))); // NOI18N
      pnWatchBts.add(btWatchRecommend);
      btWatchRecommend.setBounds(220, 0, 70, 90);
      btWatchRecommend.addMouseListener(btHover);

      btWatchBoard.setIcon(new javax.swing.ImageIcon(getClass().getResource("/tTube/images/icBtTrans.png"))); // NOI18N
      pnWatchBts.add(btWatchBoard);
      btWatchBoard.setBounds(140, 0, 70, 90);
      btWatchBoard.addMouseListener(btHover);

      lbWatchRecommendInfo.setFont(new java.awt.Font("나눔바른고딕", 0, 15)); // NOI18N
      lbWatchRecommendInfo.setForeground(new java.awt.Color(51, 51, 51));
      lbWatchRecommendInfo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
      lbWatchRecommendInfo.setText("0");
      pnWatchBts.add(lbWatchRecommendInfo);
      lbWatchRecommendInfo.setBounds(220, 60, 70, 30);
      
      lbRecoBg.setIcon(icRecoBg);

      lbWatchBoardInfo.setFont(new java.awt.Font("나눔바른고딕", 1, 14)); // NOI18N
      lbWatchBoardInfo.setForeground(new java.awt.Color(51, 51, 51));
      lbWatchBoardInfo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
      lbWatchBoardInfo.setText("");
      pnWatchBts.add(lbWatchBoardInfo);
      lbWatchBoardInfo.setBounds(130, 60, 80, 30);

      lbWatchBjName.setFont(new java.awt.Font("나눔바른고딕", 1, 15)); // NOI18N
      lbWatchBjName.setForeground(new java.awt.Color(0, 51, 124));
      lbWatchBjName.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
      lbWatchBjName.setText("");
      pnWatchBts.add(lbWatchBjName);
      lbWatchBjName.setBounds(10, 60, 110, 30);

      lbWatchBts.setIcon(new javax.swing.ImageIcon(getClass().getResource("/tTube/images/imgWatchBg.png"))); // NOI18N
      lbWatchBts.setText("jLabel1");
      lbWatchBts.setPreferredSize(new java.awt.Dimension(365, 102));
      pnWatchBts.add(lbWatchBts);
      lbWatchBts.setBounds(-10, -10, 330, 120);

      lbWatchtTubeTitle.setFont(new java.awt.Font("나눔바른고딕", 0, 16)); // NOI18N
      lbWatchtTubeTitle.setForeground(new java.awt.Color(0, 51, 124));
      lbWatchtTubeTitle.setText("");

      lbWatchRecommendCount.setFont(new java.awt.Font("나눔고딕", 1, 14)); // NOI18N

      lbWatchTitleIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/tTube/images/icPlay1.png"))); // NOI18N

      javax.swing.GroupLayout pnWatchLayout = new javax.swing.GroupLayout(pnWatch);
      pnWatch.setLayout(pnWatchLayout);
      pnWatchLayout.setHorizontalGroup(pnWatchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnWatchLayout.createSequentialGroup().addContainerGap()
                  .addGroup(pnWatchLayout
                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(
                              javax.swing.GroupLayout.Alignment.TRAILING,
                              pnWatchLayout.createSequentialGroup().addComponent(lbWatchRecommendCount)
                                    .addGap(169, 169, 169))
                        .addGroup(pnWatchLayout
                              .createSequentialGroup()
                              .addComponent(
                                    pnWatchState, javax.swing.GroupLayout.PREFERRED_SIZE,
                                    javax.swing.GroupLayout.DEFAULT_SIZE,
                                    javax.swing.GroupLayout.PREFERRED_SIZE)
                              .addGap(0, 0, Short.MAX_VALUE))
                        .addGroup(pnWatchLayout.createSequentialGroup().addGap(10, 10, 10)
                              .addGroup(pnWatchLayout
                                    .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(pnWatchLayout.createSequentialGroup()
                                          .addComponent(pnWatchBts,
                                                javax.swing.GroupLayout.PREFERRED_SIZE, 310,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                          .addGap(0, 0, Short.MAX_VALUE))
                                    .addGroup(pnWatchLayout.createSequentialGroup()
                                          .addComponent(lbWatchTitleIcon)
                                          .addPreferredGap(
                                                javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                          .addComponent(lbWatchtTubeTitle,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                Short.MAX_VALUE)))))));
      pnWatchLayout.setVerticalGroup(pnWatchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnWatchLayout.createSequentialGroup().addContainerGap()
                  .addComponent(pnWatchState, javax.swing.GroupLayout.PREFERRED_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                  .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                  .addGroup(pnWatchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(lbWatchTitleIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 45,
                              javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lbWatchtTubeTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 45,
                              javax.swing.GroupLayout.PREFERRED_SIZE))
                  .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                  .addComponent(lbWatchRecommendCount)
                  .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(pnWatchBts,
                        javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                  .addGap(67, 67, 67)));

      jTabbedPane1.addTab("시청하기", pnWatch);

      userTable.setBackground(new java.awt.Color(255, 255, 255));//테이블 배경색
      userTable.setFont(new java.awt.Font("나눔바른고딕", 0, 12)); // NOI18N
     
      userTable.setModel(new javax.swing.table.DefaultTableModel(new Object[][] {

      }, new String[] { "닉네임", "입장시간" }) {
         Class[] types = new Class[] { java.lang.String.class, java.lang.String.class, java.lang.String.class };
         
         @Override
         public boolean isCellEditable(int row, int column) {
            return false;
         }

         public Class getColumnClass(int columnIndex) {
            return types[columnIndex];
         }
      });
      
      jScrollPane1.setViewportView(userTable);

      tpMessage.setBackground(new java.awt.Color(0, 51, 124));
      tpMessage.setFont(new java.awt.Font("나눔바른고딕 Light", 0, 14)); // NOI18N
      tpMessage.setEnabled(true);//tpMessage 활성화 비활성화
      
     
      jScrollPane2.setViewportView(tpMessage);

      tfChat.setBorder(javax.swing.BorderFactory.createEtchedBorder());
      tfChat.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            tfChatActionPerformed(evt);
         }
      });

      comboColor.setFont(new java.awt.Font("나눔바른고딕", 0, 12)); // NOI18N
      comboColor.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "검정", "빨강", "노랑", "파랑", " " }));
      comboColor.setBorder(javax.swing.BorderFactory.createEtchedBorder());
      comboColor.addItemListener(new java.awt.event.ItemListener() {
         public void itemStateChanged(java.awt.event.ItemEvent evt) {
            comboColorItemStateChanged(evt);
         }
      });
      comboColor.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            // comboColorActionPerformed(evt);
         }
      });

      lbNickname.setFont(new java.awt.Font("나눔바른고딕", 1, 14)); // NOI18N
      lbNickname.setForeground(new java.awt.Color(0, 51, 124));
      lbNickname.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
      

      lbNickHi.setFont(new java.awt.Font("나눔바른고딕", 0, 14)); // NOI18N
      lbNickHi.setForeground(new java.awt.Color(0, 51, 124));
      lbNickHi.setText("님 안녕하세요!");

      btMsg.setIcon(new javax.swing.ImageIcon(getClass().getResource("/tTube/images/icMsg.png"))); // NOI18N
      btMsg.addMouseListener(btHover);
      btMsg.addMouseListener(new java.awt.event.MouseAdapter() {
        	public void mouseClicked(java.awt.event.MouseEvent evt) {
              btMsgMouseClicked(evt);
          }
      	
        });

      btChangeNickname.setIcon(new javax.swing.ImageIcon(getClass().getResource("/tTube/images/icChangeNickname.png"))); // NOI18N
      btChangeNickname.addMouseListener(btHover);
      btChangeNickname.addMouseListener(new java.awt.event.MouseAdapter() {
      	public void mouseClicked(java.awt.event.MouseEvent evt) {
      		btChangeNicknameClicked(evt);
        }
    	
      });

      btLogout.setIcon(new javax.swing.ImageIcon(getClass().getResource("/tTube/images/icLogout.png"))); // NOI18N
      btLogout.addMouseListener(btHover);

      javax.swing.GroupLayout pnBaseLayout = new javax.swing.GroupLayout(pnBase);
      pnBase.setLayout(pnBaseLayout);
      pnBaseLayout.setHorizontalGroup(pnBaseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnBaseLayout.createSequentialGroup().addContainerGap()
                  .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 337,
                        javax.swing.GroupLayout.PREFERRED_SIZE)
                  .addGap(18, 18, 18)
                  .addGroup(pnBaseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(pnBaseLayout.createSequentialGroup()
                              .addComponent(tfChat, javax.swing.GroupLayout.PREFERRED_SIZE, 216,
                                    javax.swing.GroupLayout.PREFERRED_SIZE)
                              .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                              .addComponent(comboColor, javax.swing.GroupLayout.PREFERRED_SIZE, 53,
                                    javax.swing.GroupLayout.PREFERRED_SIZE)
                              .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                              .addComponent(btMsg, javax.swing.GroupLayout.DEFAULT_SIZE,
                                    javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(pnBaseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                              .addGroup(pnBaseLayout.createSequentialGroup()
                                    .addComponent(lbNickname, javax.swing.GroupLayout.PREFERRED_SIZE, 118,
                                          javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(lbNickHi, javax.swing.GroupLayout.PREFERRED_SIZE, 83,
                                          javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(btChangeNickname, javax.swing.GroupLayout.PREFERRED_SIZE,
                                          45, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(btLogout, javax.swing.GroupLayout.PREFERRED_SIZE, 46,
                                          javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(6, 6, 6))
                              .addGroup(pnBaseLayout
                                    .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 320,
                                          javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 320,
                                          javax.swing.GroupLayout.PREFERRED_SIZE))))
                  .addContainerGap(26, Short.MAX_VALUE)));
      pnBaseLayout.setVerticalGroup(pnBaseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnBaseLayout.createSequentialGroup()
                  .addGap(0, 26, Short.MAX_VALUE)
                  .addGroup(pnBaseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(pnBaseLayout.createSequentialGroup().addGroup(pnBaseLayout
                              .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                              .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnBaseLayout
                                    .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(btChangeNickname, javax.swing.GroupLayout.DEFAULT_SIZE,
                                          javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(lbNickHi, javax.swing.GroupLayout.DEFAULT_SIZE,
                                          javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(lbNickname, javax.swing.GroupLayout.DEFAULT_SIZE,
                                          javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                              .addComponent(btLogout, javax.swing.GroupLayout.Alignment.TRAILING))
                              .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                              .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 116,
                                    javax.swing.GroupLayout.PREFERRED_SIZE)
                              .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                              .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 249,
                                    javax.swing.GroupLayout.PREFERRED_SIZE)
                              .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                              .addGroup(pnBaseLayout
                                    .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(btMsg, javax.swing.GroupLayout.PREFERRED_SIZE, 40,
                                          javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(pnBaseLayout
                                          .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                          .addComponent(tfChat, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                38, javax.swing.GroupLayout.PREFERRED_SIZE)
                                          .addComponent(comboColor,
                                                javax.swing.GroupLayout.PREFERRED_SIZE, 38,
                                                javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGroup(pnBaseLayout.createSequentialGroup()
                              .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 463,
                                    javax.swing.GroupLayout.PREFERRED_SIZE)
                              .addGap(2, 2, 2)))
                  .addContainerGap(28, Short.MAX_VALUE)));

      javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
      getContentPane().setLayout(layout);
      layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(
            pnBase, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
            javax.swing.GroupLayout.PREFERRED_SIZE));
      layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(
            pnBase, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));

      pack();
   }


   
   /** 호버되는 버튼 관련 */
   MouseListener btHover = new MouseAdapter() {

      public void mousePressed(java.awt.event.MouseEvent e) {

         Object b = e.getSource();

         /** BJ가 서버시작 버튼을 누를때 */
         if (b == btBjStart) {
        	if(!serverStart) {
        		serverStart=true;
	            tus.UDPserverStart();//UDPserverStart클래스가 시작되어 영상 전송이 시작되고 비디오 패널에도 화면이 보임
	            tts.TCPserverStart();// tTubeTcpServer클래스가 시작되어 채팅서버 시작되고 시청자의 채팅방 입장을 기다림 
	            try {
	               Thread.sleep(1000);//1초후
	               chatConnect();//BJ도 채팅방에 입장하기 위한 메소드
	               changeTitleProcess();
	            } catch (InterruptedException e1) {
	               System.out.println("채팅방 입장에 실패하였습니다.");
	            }
	         }
        	else showMsg("이미 서버가 실행중입니다.");
         }
         
         /**퇴장 버튼을 누를때 (BJ, 시청자 모두) */
         if (b == btLogout) {
        	 exitProcess();
         } 
         
         /**닉네임 변경 (BJ, 시청자 모두) */

         
         /**방송 제목 변경 (BJ) */
         if (b == btBjChange) {
        	 changeTitleProcess();
         }
         
         /**추천버튼 (시청자) */
         if (b == btWatchRecommend) {
        	 recommendProcess();
          }
         
         if(b==btWatchBoard) {
        	 String sett=bjInfo;
        	 ttb.setnick(sett);
        	 ttb.initData();
        	 ttb.setVisible(true);
         }
         if(b==btBjBoard) {
        	 String sett=bjInfo;
        	 ttb.setnick(sett);
        	 ttb.initData();
        	 ttb.setVisible(true);
         }
         
      };
      

	public void mouseEntered(java.awt.event.MouseEvent e) {

         Object b = e.getSource();

         if (b == btLogout) {
            btLogout.setIcon(icLogoutHover);
         } else if (b == btBjChange) {
            btBjChange.setIcon(icBjChangeHover);
         } else if (b == btChangeNickname) {
            btChangeNickname.setIcon(icChangeNicknameHover);
         } else if (b == btMsg) {
            btMsg.setIcon(icMsgHover);
         } else if (b == btBjBoard) {
            lbBjBts.setIcon(imgBjBgL);
            lbBjBoardInfo.setForeground(Color.white);
         } else if (b == btBjStart) {
            lbBjBts.setIcon(imgBjBgR);
         } else if (b == btWatchBoard) {
            lbWatchBts.setIcon(imgWatchBgL);
            lbWatchBoardInfo.setForeground(Color.white);
         } else if (b == btWatchRecommend) {
            lbWatchBts.setIcon(imgWatchBgR);
            lbWatchRecommendInfo.setForeground(Color.white);
         }

      }

      public void mouseExited(java.awt.event.MouseEvent e) {

         Object b = e.getSource();

         if (b == btLogout) {
            btLogout.setIcon(icLogout);
         } else if (b == btBjChange) {
            btBjChange.setIcon(icBjChange);
         } else if (b == btChangeNickname) {
            btChangeNickname.setIcon(icChangeNickname);
         } else if (b == btMsg) {
            btMsg.setIcon(icMsg);
         } else if (b == btBjBoard || b == btBjStart) {
            lbBjBts.setIcon(imgBjBg);
            lbBjBoardInfo.setForeground(tBlue);

         }

         else if (b == btWatchBoard || b == btWatchRecommend) {
            lbWatchBts.setIcon(imgWatchBg);
            lbWatchBoardInfo.setForeground(tBlue);
            lbWatchRecommendInfo.setForeground(tBlue);
         }
      };
   };

   /** 패널 우측 채팅입력관련 */
   private void tfChatActionPerformed(java.awt.event.ActionEvent evt) {
      String msg = tfChat.getText();//tfChat에 입력된 문자를 msg에 보냄
      sendMsg(msg);//msg를 담아서 sendMsg메소드 호출
      tfChat.setText("");//tfChat은 빈칸이 된다.
      tfChat.requestFocus();//tfChat에 입력포커스를 준다.
   }


   /** 패널 우측 글자색 변경 */
	private void comboColorItemStateChanged(java.awt.event.ItemEvent evt) {
		String cr = comboColor.getSelectedItem().toString();
		if (cr.contentEquals("검정")) {
			this.fontColor = Color.black.getRGB();//검정일때 RGB값
		} else if (cr.contentEquals("파랑")) {
			this.fontColor = Color.blue.getRGB();//파랑일때 RGB값
		} else if (cr.contentEquals("빨강")) {
			this.fontColor = Color.red.getRGB();//빨강일때 RGB값
		} else if (cr.contentEquals("초록")) {
			this.fontColor = Color.green.getRGB();//초록일떄 RGB값
		} else if (cr.contentEquals("핑크")) {
			this.fontColor = Color.pink.getRGB();//핑크일떄 RGB값
		} else if (cr.contentEquals("노랑")) {
			this.fontColor = Color.yellow.getRGB();//노랑일떄 RGB값
		}
	}

	/*-------------------------------------------------------------------------------------------------------------------------------------------------------------*/

   /** 서버 시작 클릭시 발생할 메소드 */

	/**TCP 채팅 관련 모두 밑에 있음*/
	/*bj나 시청자가 채팅방을 입장할때*/
	public void chatConnect() {
		try {
			isStop=false;//isStop = false
			if(sock==null) {//현재 소켓이 null이라면? //처음 입장할때 해당
				sock = new Socket(host,port);//설정해 놓은 ip주소와 포트번호를 설정한 소켓생성
				tpMessage.setText("##채팅 서버와 연결됨##\r\n");//서버 연결됨
	            oos=new ObjectOutputStream(sock.getOutputStream());//객체 전송하는 스트림/ tTubeChatHandler에서는 ObjectInputStream 을 먼저생성
	            in=new ObjectInputStream(sock.getInputStream());//객체 수신하는 스트림
	            
	            /*tTubeChatHandler에서 보내오는 메시지를 받는 스레드 동작*/
	            Thread listener = new Thread(this);
	            listener.start();//run() 실행
	            
	            oos.writeObject(nickname+"|"+time);//tTubeChatHandler에 닉네임|입장시간 형식의 객체 전송
	            oos.flush();   
			}
		}catch (ConnectException e) {
			System.out.println("tTubeMain chatConnect() 예외: "+e);
			showMsg("서버에 문제가 있거나 없는 호스트 입니다.\n프로그램을 다시 실행해 주세요.");
			System.exit(0);
		} catch (IOException e) {
			System.out.println("tTubeMain chatConnect() 예외: "+e);
		}
	}//chatConnect()
	

	//서버가 보내오는 메시지를 계속 읽음
	/** 서버 실행시 run 하게됨 */
	@Override
	public void run() {
	/**채팅 실행*/
		try {
			while(!isStop) {//isStop이 true일때만 반복 맨처음 입장할때는 false이지만 !이기 때문에 true랑 같음
				String serverMsg=(String) in.readObject();//tTubeChatHandler로 부터 객체를 받아 string형으로 형변환후 serverMsg에 담음
				// ex) serverMsg = 400|닉네임|폰트컬러|메시지
				if(serverMsg==null) return;//serverMsg가 비어있으면? 리턴
	             
				//tTubeChatHandler로부터 받은 메시지가 있다면?
				//이를 분석(파싱)하는 메소드 호출
				process(serverMsg);//ex) serverMsg = 400|닉네임|폰트컬러|메시지
			}
		} catch (Exception e) {
			System.out.println("tTubeMain run() 예외: "+e);
			JOptionPane.showMessageDialog(this, "방송이 종료되었습니다.\n시청해주셔서 감사합니다~~ ^__^");
		}
	}//run()
	
	/*run()이 실행되었다는 전제하에 tfChat에 메시지를 입력했을때*/
	/**대화 내용을 서버로 전송*/
	public void sendMsg(String msg) {//msg는 tfChat에서 입력한 메시지 내용   
		msg=msg.replaceAll("\\|", "ㅣ");//만약 보내는 메시지에 |문자가 있다면 ㅣ로 변환한다.
		if(msg==null||msg.isEmpty()) return;//msg내용이 비어있으면 리턴
		if(isSendOne) {

		} else {
			//2. 모두에게 보낼 경우
			//400은 일반 대화시에 사용되는 프로토콜 번호
			String sendMsg="400|"+fontColor+"|"+msg; //sendMsg에는 400|폰트컬러|msg 내용이 담겨있음
			try {
				oos.writeObject(sendMsg);//400|폰트컬러|msg 내용이 담겨있는 sendMsg를 tTubeChatHandler로 송신
				oos.flush();
			} catch (IOException e) {
				System.out.println("tTubeMain sendMsg() 예외: "+e);
			}
		}
	}
   
	/**프로토콜 별로 로직을 처리*/
	public void process(String msg) {
		String tokens [] = msg.split("\\|");//chatandler의 msg를 |를 기준으로 분류하여 배열에 넣는다.
		switch(tokens[0]) {//프로토콜값 분석
			case "100":{//100|닉네임|입장시간
				DefaultTableModel userModel=(DefaultTableModel)userTable.getModel();//userTable에 정보를 올림
				String rowData[]= {tokens[1], tokens[2]};//닉네임, 입장시간
				lbWatchRecommendInfo.setText(tokens[3]);//추천수
				lbWatchtTubeTitle.setText(tokens[4]);//방송제목
				userModel.addRow(rowData);//열에 올린다.
				String str="["+tokens[1]+"] 님이 접속하셨습니다. :)";
				showChat(tBlueLight, tBlue, "나눔바른고딕", str);   
				bjInfo = (String) userTable.getValueAt(0, 0);//userTable의 0,0 의 값을 가져와서 String형으로 형변환후
				lbWatchBjName.setText(bjInfo);
			}break;
			case "400":{//글자색
				String fromChat=tokens[1];//닉네임
				String fntCr=tokens[2];//폰트컬러
				String fromMsg=tokens[3];//메시지
				//텍스트페인에 대화내용 출력
				showChat(fromChat,fntCr,fromMsg);//showChat메소드에 닉네임, 폰트컬러, 메시지 내용을 담아서 호출
			}break;
			case "500":{//추천
				String nickName=tokens[1];//닉네임
				String recommendCount=tokens[2];//추천
				String str="["+tokens[1]+"] 님이 추천하셨습니다. :)";
				showChat(Color.white, tBlueMedium, "나눔바른고딕 Bold", str);   
				sound = new wavSound();
	            sound.sound("src/tTube/sounds/recommend.wav",false);
				lbBjRecommendInfo.setText(recommendCount);//각 label에 추천수 증가를 표시
				lbWatchRecommendInfo.setText(recommendCount);//각 label에 추천수 증가를 표시
			}break;
			case "600":{//제목 변경 
				String changeTitle=tokens[1];//바꿀제목
				lbWatchtTubeTitle.setText(changeTitle);//lbWatchtTubeTitle에 제목 변경
				String str="방송제목 ☞ ["+tokens[1]+"]";
				showChat(Color.white, tBlueMedium, "나눔바른고딕 Bold", str);   
			}break;
			case "700":{//닉네임 중복 -> chathandler에서 700을 받아옴 700|
				tuc.socket.close();//tTubeUdpClient의 소켓 close()
				showMsg(nickname+"=> 이 대화명은 이미 사용중인 대화명입니다. :(");
				this.dispose();//이 창을 닫는다
				ttl=new tTubeLogin();//새로운 로그인 객체 생성하여
				ttl.setVisible(true);//띄운다.
			}break;
			case "777":{//기존 사용자의 닉네임변경 
				String oldNick=tokens[1];//기존닉
				String newNick=tokens[2];//새닉
				String str="["+oldNick+"]님이 ☞ ["+newNick+"]님이 되었습니다.";
				changeNickAll(oldNick,newNick);
				showChat(Color.white, tBlueMedium, "나눔바른고딕 Bold", str);   
				
			}break;
			case "900":{//종료
				String exitChat=tokens[1];//닉네임
				logout(exitChat,EXIT);//닉네임과 EXIT속성을 갖고 logout메소드 호출
			}break;
			case "999":{//쪽지
				String msgUser=tokens[1];//쪽지 받는 사람 이름 
				String msgNote=tokens[2];//쪽지 내용
				String sendUser=tokens[3];//쪽지 보낸 사람
				sound = new wavSound();
	            sound.sound("src/tTube/sounds/buddy.wav",false);
				tgm = new tTubeGetMsg(sendUser,msgNote); // 쪽지 받는 창을 띄움. 
				tgm.setVisible(true);
			}break;
		}
	}//process(String msg)
   
	/**대화할때 실행되는 메소드 tTubeChatHandler -> run -> process -> 400일때 showChat*/
	public synchronized void showChat(String fromChat, String fntCr, String fromMsg) {//showChat메소드에 닉네임, 폰트컬러, 메시지 내용
		int rgb=Integer.parseInt(fntCr);//글꼴색을 int형으로 변환
		SimpleAttributeSet attr =new SimpleAttributeSet();//AttributeSet객체 생성 - 속성을 지정할 객체를 만들고 여러가지 속성 부여
		StyleConstants.setForeground(attr, new Color(rgb));//해당객체에 배경색 지정
		StyleConstants.setFontSize(attr, 14);//해당객체의 폰트 사이즈
		doc=tpMessage.getStyledDocument();//tpMessage의 문서 모델 얻기

		int offset=doc.getEndPosition().getOffset()-1;//끝의 위치-1
		tpMessage.setCaretPosition(offset);//커서를 맨 끝으로
      
		String msg="["+fromChat+"]"+fromMsg+"\r\n";//msg= [보낸사람 닉네임] 메시지내용 
      
		try {
			//tpMessage에 문자열 추가
			doc.insertString(offset, msg, attr);//해당 위치에 메시지를 추가하고 attr속성을 적용
		} catch (BadLocationException e) {//-삽입할 위치를 잘못 지정했을때
			System.out.println("tTubeMain showChat() 예외: "+e);
		}
	}//showChat(String fromChat, String fntCr, String fromMsg)
   
   /**대화를 제외한 메시지를 스타일을 적용해서 보여주는 메소드 showChat 오버로딩 */
	public synchronized void showChat(Color bg, Color fg, String fm, String msg) {
		SimpleAttributeSet attr = new SimpleAttributeSet();
		StyleConstants.setBackground(attr, bg);//배경색
		StyleConstants.setForeground(attr, fg);//글자색
		StyleConstants.setFontFamily(attr, fm);//폰트
		StyleConstants.setFontSize(attr, 14);//폰트사이즈
		doc=tpMessage.getStyledDocument();
		int offset=doc.getEndPosition().getOffset()-1;
		tpMessage.setCaretPosition(offset);
		try {
			//tpMessage에 문자열 추가
			doc.insertString(offset, msg+"\r\n", attr);
		} catch (Exception e) {
			System.out.println("tTubeMain showChat() 예외: "+e);
		}
	}//showChat(Color bg, Color fg, String fm, String msg)
   
	/**퇴장버튼이나 X를 누를떄 실행되는 메소드*/
	public void exitProcess() {
		int yn=JOptionPane.showConfirmDialog(this, "T tube를 종료하시겠습니까?","T tube를 종료하시겠습니까?" ,JOptionPane.YES_NO_OPTION);
		if(yn==JOptionPane.YES_OPTION) {//yes누를시
			if(sock!=null&&!sock.isClosed()) {//서버 연결중일때
				try {
					oos.writeObject("900|"+nickname);//900|닉네임 tTubeChatHandler로 송신
					oos.flush();
				} catch (IOException e) {
					System.out.println("tTubeMain exitProcess() 예외: "+e);
					System.exit(0);//프로그램 종료
				}
			}else {// 서버 연결중이 아닐때
				System.exit(0);//프로그램 종료
			}
		}
	}//exitProcess()
 
	/**exitProcess -> tTubeChatHandler -> run -> process -> 900일때: logout*/
	public void logout(String logoutchat, int mode) {//닉네임과 LOGOUT or EXIT
		//나가는 사람이 본인이 아니면
		DefaultTableModel userModel = (DefaultTableModel) userTable.getModel();
		String tmpId="";
		for(int i=0; i<userModel.getRowCount();i++) {
			String cname=(String) userModel.getValueAt(i, 0);
			if(cname.contentEquals(logoutchat)) {
				userModel.removeRow(i);//삭제
	            tmpId=cname;
	            break;
			}
		}
		if(mode==EXIT) {//종료
			String str="["+tmpId+"] 님이 프로그램을 종료하셨습니다.";
			showChat(tBluePale, tBlue, "나눔바른고딕", str);   
		}
		
		//나가는 사람이 본인이면
		if(logoutchat.contentEquals(nickname)) {
			isStop=true;
			exitChat(mode);//exitChat(EXIT or LOGOUT)
		}
	}//logout(String logoutchat, int mode)
    
	/**logout() -> exitChat()*/
	public void exitChat(int mode) {
		isStop=true;
		lbNickname.setText("");
		try {
			if(oos!=null) oos.close();
			if(in!=null) in.close();
			if(sock!=null) {
				sock.close();
				sock=null;
			}
			if(tuc.socket!=null)tuc.socket.close();//모두 close()
		}catch (Exception e) {
			System.out.println("tTubeMain exitChat() 예외: "+e);
		}
		if (mode==EXIT) {//퇴장
			this.dispose();
			System.exit(0);//종료
		}
	}//exitChat(int mode)
	
	/**추천버튼 누를때 실행되는 메소드*/
	public void recommendProcess() {
		try {
			oos.writeObject("500|"+nickname);//500|닉네임|추천
			oos.flush();
			
			gp.setLayout(null);
			gp.add(lbRecoBg);
			lbRecoBg.setBounds(-10, -10, 728, 546);
			gp.setVisible(true);
			gp.validate();
			gp.addMouseListener(new java.awt.event.MouseAdapter() {
	        	public void mousePressed(java.awt.event.MouseEvent evt) {
	               gp.setVisible(false);
	            }
	        	
	          });
			
			
			
			
		} catch (IOException e) {
			System.out.println("tTubeMain recommendProcess() 예외: "+e);
		}
	}//recommendProcess()
	
	/** 방제목 변경 누를때 실행되는 메소드*/
	public void changeTitleProcess() {
		changeTitle=tfBjtTubeTitle.getText();//tfBjtTubeTitle에 있는 텍스트를 불러옴
		try {
			oos.writeObject("600|"+changeTitle);//600|바꿀방송제목
			oos.flush();
		} catch (IOException e) {
			System.out.println("tTubeMain changeTitleProcess() 예외: "+e);
		}
	}//changeTitleProcess()
	
	
	/**쪽지버튼 누를때 실행되는 메소드*/
	 private void btMsgMouseClicked(java.awt.event.MouseEvent evt) {                    
		
		 try {
			 int row=userTable.getSelectedRow();
			 String msgUserName=(String) userTable.getModel().getValueAt(row, 0);
				
			 tTubeSendMsg tsm = new tTubeSendMsg(this,msgUserName);
			 tsm.setVisible(true);
		 }
		 catch(Exception e){ // 쪽지 보낼 사람 클릭 안 하면
			 JOptionPane.showMessageDialog(this, "쪽지를 보낼 사람을 클릭해주세요!");
	    } 
	 }
	 
	 /**쪽지관련 프로토콜 메소드 */	 
	 public void sendPersMsg(String msgUser, String msgNote) {
		 
        if(msgNote != null){
       	 try {
				oos.writeObject("999|" + msgUser + "|" + msgNote + "|" + nickname); // 999|받는사람|쪽지내용|보내는사람
				oos.flush();
				
			} catch (IOException e) {	
				System.out.println("tTubeMain sendPersMsg() 예외: "+e);
			} 
        }
	 }  
	 
	
	 
	 /** 닉변메소드  */	
	 private void btChangeNicknameClicked(java.awt.event.MouseEvent evt) {                    
			String newNick = JOptionPane.showInputDialog("바꿀 닉네임을 입력하세요.");
			if(newNick == null || newNick.trim().isEmpty()) {
				showMsg("닉네임을 입력하세요");
				return;
			}
			int allRow = userTable.getRowCount();
			for(int i=0; i<allRow; i++) {
				String nicks = (String) userTable.getModel().getValueAt(i, 0);
				if(newNick.equals(nicks)) {
					showMsg("이미 있는 닉네임입니다!");
					return;
				}
			}
			try {
				oos.writeObject("777|" + nickname + "|" + newNick );// 777|받는사람|기존닉넴|새닉넴
				oos.flush();
			} catch (IOException e) {
				System.out.println("tTubeMain btChangeNicknameCilcked() 예외: "+e);
			} 
			
			nickname=newNick;
			lbNickname.setText(nickname);
		 
	 }
	 
	 /** 모든 사람의 테이블에 닉변이 적용되도롱 하는 메소드  */
	 private void changeNickAll(String oldNick, String newNick) {   
		 int allRow = userTable.getRowCount();
		 for(int i=0; i<allRow; i++) {
				String nicks = (String) userTable.getModel().getValueAt(i, 0);
				if(oldNick.equals(nicks)) {
					userTable.setValueAt(newNick, i, 0);
					break;
				}
			}
	 }
	 
	

   /**
    * @param args the command line arguments
    */
   public static void main(String args[]) {
      try {
         for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
            if ("Nimbus".equals(info.getName())) {
               javax.swing.UIManager.setLookAndFeel(info.getClassName());
               break;
            }
         }
      } catch (ClassNotFoundException ex) {
         java.util.logging.Logger.getLogger(tTubeMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
      } catch (InstantiationException ex) {
         java.util.logging.Logger.getLogger(tTubeMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
      } catch (IllegalAccessException ex) {
         java.util.logging.Logger.getLogger(tTubeMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
      } catch (javax.swing.UnsupportedLookAndFeelException ex) {
         java.util.logging.Logger.getLogger(tTubeMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
      }
      // </editor-fold>

      /* Create and display the form */
      java.awt.EventQueue.invokeLater(new Runnable() {
         public void run() {
//            new tTubeMain().setVisible(true);
         }
      });
   }

   // Variables declaration - do not modify//GEN-BEGIN:variables
   private javax.swing.JLabel btBjBoard;
   private javax.swing.JLabel btBjChange;
   private javax.swing.JButton btBjServerStart;
   private javax.swing.JLabel btLogout;
   private javax.swing.JLabel btChangeNickname;
   private javax.swing.JLabel btMsg;
   private javax.swing.JLabel btWatchBoard;
   private javax.swing.JLabel btWatchRecommend;
   private javax.swing.JLabel btBjStart;
   private javax.swing.JLabel btBjRecommend;
   private javax.swing.JLabel btWatchBjProfile;
   private javax.swing.JCheckBox chkWhisper;
   private javax.swing.JComboBox<String> comboColor;
   private javax.swing.JScrollPane jScrollPane1;
   private javax.swing.JScrollPane jScrollPane2;
   private javax.swing.JTabbedPane jTabbedPane1;
   private javax.swing.JLabel lbBjRecommendCount;
   private javax.swing.JLabel lbWatchHeart;
   private javax.swing.JLabel lbBjRecommendInfo;
   private javax.swing.JLabel lbBjTitleIcon;
   private javax.swing.JLabel lbBjBoardInfo;
   private javax.swing.JLabel lbBjBar2;
   private javax.swing.JLabel lbBjBar1;
   private javax.swing.JLabel lbWatchBoardInfo2;
   private javax.swing.JLabel bjStartInfo;
   private javax.swing.JLabel lbWatchBar2;
   private javax.swing.JLabel lbBjStartInfo;
   private javax.swing.JLabel lbWatchBoardInfo;
   private javax.swing.JLabel lbWatchBar;
   private javax.swing.JLabel lbWatchBar1;
   private javax.swing.JLabel lbWatchBoardInfo1;
   public javax.swing.JLabel lbNickname;
   private javax.swing.JLabel lbNickHi;
   private javax.swing.JLabel lbBjTitle;
   private javax.swing.JLabel lbWatchTitleIcon;
   private javax.swing.JLabel lbViewerInfo;
   private javax.swing.JLabel lbWatchRecommendCount;
   private javax.swing.JLabel lbWatchRecommendInfo;
   private javax.swing.JLabel lbWatchTime;
   private javax.swing.JLabel lbRecoBg;
   private javax.swing.JLabel lbWatchTitle;
   public javax.swing.JLabel lbWatchtTubeTitle;
   private javax.swing.JLabel lbBjBts;
   private javax.swing.JLabel lbWatchBts;
   public javax.swing.JLabel lbWatchBjName;
   private javax.swing.JPanel pnBase;
   private javax.swing.JPanel pnBj;
   private javax.swing.JPanel pnBjInfo;
   private javax.swing.JPanel pnWatch;
   private javax.swing.JPanel pnWatchInfo;
   private javax.swing.JPanel pnBjBts;
   private javax.swing.JPanel pnWatchBts;
   private javax.swing.JTextField tfBjtTubeTitle;
   private javax.swing.JTextField tfChat;
   public javax.swing.JTextPane tpMessage;
   public javax.swing.JTable userTable;

   // icons
   public ImageIcon icLogout = new javax.swing.ImageIcon(getClass().getResource(("/tTube/images/icLogout.png")));
   public ImageIcon icLogoutHover = new javax.swing.ImageIcon(getClass().getResource(("/tTube/images/icLogoutHover.png")));
   public ImageIcon icChangeNickname = new javax.swing.ImageIcon(
         getClass().getResource(("/tTube/images/icChangeNickname.png")));
   public ImageIcon icChangeNicknameHover = new javax.swing.ImageIcon(
         getClass().getResource(("/tTube/images/icChangeNicknameHover.png")));
   public ImageIcon icMsg = new javax.swing.ImageIcon(getClass().getResource(("/tTube/images/icMsg.png")));
   public ImageIcon icMsgHover = new javax.swing.ImageIcon(getClass().getResource(("/tTube/images/icMsgHover.png")));
   public ImageIcon icBjChange = new javax.swing.ImageIcon(getClass().getResource(("/tTube/images/icBjChange.png")));
   public ImageIcon icBjChangeHover = new javax.swing.ImageIcon(
         getClass().getResource(("/tTube/images/icBjChangeHover.png")));
   public ImageIcon icBtTrans = new javax.swing.ImageIcon(getClass().getResource(("/tTube/images/icBtTrans.png")));
   public ImageIcon icPlay1 = new javax.swing.ImageIcon(getClass().getResource(("/tTube/images/icPlay1.png")));
   public ImageIcon icPlay2 = new javax.swing.ImageIcon(getClass().getResource(("/tTube/images/icPlay2.png")));
   public ImageIcon imgBjBg = new javax.swing.ImageIcon(getClass().getResource(("/tTube/images/imgBjBg.png")));
   public ImageIcon imgBjBgL = new javax.swing.ImageIcon(getClass().getResource(("/tTube/images/imgBjBgL.png")));
   public ImageIcon imgBjBgR = new javax.swing.ImageIcon(getClass().getResource(("/tTube/images/imgBjBgR.png")));
   public ImageIcon imgWatchBg = new javax.swing.ImageIcon(getClass().getResource(("/tTube/images/imgWatchBg.png")));
   public ImageIcon imgWatchBgL = new javax.swing.ImageIcon(getClass().getResource(("/tTube/images/imgWatchBgL.png")));
   public ImageIcon imgWatchBgR = new javax.swing.ImageIcon(getClass().getResource(("/tTube/images/imgWatchBgR.png")));
   
   public ImageIcon icRecoBg=new javax.swing.ImageIcon(getClass().getResource(("/tTube/images/imgEffect.gif")));
   // End of variables declaration//GEN-END:variables
}