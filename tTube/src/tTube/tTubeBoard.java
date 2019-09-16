package tTube;

import java.awt.event.MouseAdapter;
import static tTube.tTubeMain.bjInfo;
import static tTube.tTubeMain.nickname;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
//import javax.swing.ImageIcon;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.*;

import tTube.tTubeDAO;
import tTube.tTubeVO;

public class tTubeBoard extends javax.swing.JFrame {

	public tTubeDAO ttubeDao;
	String[][] data = null;
	String[] colHeader = { "번호", "응원", "닉네임", "작성일" };
	String nname;

	public tTubeBoard() {
		initComponents();
		ttubeDao = new tTubeDAO();
	}
	

	public void setnick(String sett) {
		nname = sett;
	}
	

	public void initData() {
		try {
			ArrayList<tTubeVO> arr1 = ttubeDao.listMemo(nname);
			lbNickname.setText(""+nickname);
			showTable(arr1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void initComponents() {
		
		setResizable(false);

		jPanel1 = new javax.swing.JPanel();
		lbDelMemo = new javax.swing.JLabel();
		tfMemo = new javax.swing.JTextField();
		lbWriteMemo = new javax.swing.JLabel();
		jScrollPane2 = new javax.swing.JScrollPane();
		memoTable = new javax.swing.JTable();
		lbNickname = new javax.swing.JLabel();
		lbMemoBg = new javax.swing.JLabel();

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				dispose();
			}
		});

		setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

		// setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

		jPanel1.setLayout(null);

		lbDelMemo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/tTube/images/icDelMemo.png"))); // NOI18N
		jPanel1.add(lbDelMemo);
		lbDelMemo.setBounds(750, 130, 80, 100);
		lbDelMemo.addMouseListener(btHover);
		tfMemo.setFont(new java.awt.Font("나눔바른고딕", 0, 14)); // NOI18N
		tfMemo.setText("");
		tfMemo.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
		tfMemo.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				tfMemoActionPerformed(evt);
			}
		});
		jPanel1.add(tfMemo);
		tfMemo.setBounds(250, 190, 330, 20);

		lbWriteMemo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/tTube/images/icWriteMemo.png"))); // NOI18N
		jPanel1.add(lbWriteMemo);
		lbWriteMemo.setBounds(670, 130, 80, 100);
		lbWriteMemo.addMouseListener(btHover);

		memoTable.setModel(new javax.swing.table.DefaultTableModel(new Object[][] {

		}, new String[] { "번호", "응원", "닉네임", "작성일" }) {
			boolean[] canEdit = new boolean[] { false, false, false, false };

			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return canEdit[columnIndex];
			}
		});
		memoTable.setGridColor(new java.awt.Color(255, 255, 255));
		memoTable.setSelectionBackground(new java.awt.Color(0, 51, 124));
		jScrollPane2.setViewportView(memoTable);

		jPanel1.add(jScrollPane2);
		jScrollPane2.setBounds(190, 240, 635, 200);
		memoTable.setFont(new java.awt.Font("나눔바른고딕", 0, 12)); // NOI18N

		lbNickname.setFont(new java.awt.Font("나눔바른고딕", 1, 14)); // NOI18N
		
		jPanel1.add(lbNickname);
		lbNickname.setBounds(250, 110, 190, 30);

		lbMemoBg.setIcon(new javax.swing.ImageIcon(getClass().getResource("/tTube/images/imgMemoBg.png"))); // NOI18N
		jPanel1.add(lbMemoBg);
		lbMemoBg.setBounds(0, 0, 910, 523);

		lbWriteMemo.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mousePressed(java.awt.event.MouseEvent evt) {
				lbWriteMemoMouseClicked(evt);
			}
		});

		lbDelMemo.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mousePressed(java.awt.event.MouseEvent evt) {
				lbDelMemoMouseClicked(evt);
			}
		});

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(
				jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 908, javax.swing.GroupLayout.PREFERRED_SIZE));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 523, Short.MAX_VALUE));

		pack();
	}// </editor-fold>

	private void lbWriteMemoMouseClicked(java.awt.event.MouseEvent evt) {
		String name = lbNickname.getText();
		String msg = tfMemo.getText();

		if (name == null || name.trim().isEmpty()) {
			showMsg("닉네임을 입력해 주세요.");
			lbNickname.requestFocus();
			System.out.println(bjInfo);
			System.out.println("138");
			return;
		} else if (name.trim().length() > 8) {
			showMsg("닉네임은 8자 이하여야 해요.");
			lbNickname.requestFocus();
			return;
		}
		if (msg == null || msg.trim().isEmpty()) {
			showMsg("응원을 입력해 주세요.");
			tfMemo.requestFocus();
			return;
		} else if (msg.length() > 40) {
			showMsg("응원은 40자 이하로 입력해주세요.");
			tfMemo.requestFocus();
			return;
		}
		tTubeVO memo = new tTubeVO(0, name, msg, null);
		try {
			int n = ttubeDao.insertMemo(memo);
			String str = (n > 0) ? "응원하기 완료" : "응원하기 실패";
			showMsg(str);
			ArrayList<tTubeVO> arr1 = ttubeDao.listMemo(bjInfo);
			showTable(arr1);
			tfMemo.setText("");
		} catch (SQLException e) {
			showMsg("응원은 40자 이하로 입력해주세요.");
		}
	}

	public void showTable(ArrayList<tTubeVO> arr1) {
		data = new String[arr1.size()][4];
		for (int i = 0; i < data.length; i++) {
			tTubeVO m = arr1.get(i);
			data[i][0] = m.getIdx() + "";
			data[i][1] = m.getMsg();
			data[i][2] = m.getName();
			data[i][3] = m.getWdate().toString();
		}

		DefaultTableModel md = new DefaultTableModel(data, colHeader);
		DefaultTableCellRenderer ts = new DefaultTableCellRenderer();
		ts.setHorizontalAlignment(SwingConstants.CENTER);
		TableColumnModel cm = memoTable.getColumnModel();

		memoTable.setAutoCreateRowSorter(true);

		TableRowSorter tablesorter = new TableRowSorter(memoTable.getModel());

		memoTable.setRowSorter(tablesorter);
		memoTable.setModel(md);
		if (memoTable.getColumnModel().getColumnCount() > 0) {
			memoTable.getColumnModel().getColumn(0).setMinWidth(45);
			memoTable.getColumnModel().getColumn(0).setPreferredWidth(45);
			memoTable.getColumnModel().getColumn(0).setMaxWidth(45);
			memoTable.getColumnModel().getColumn(0).setCellRenderer(ts);
			memoTable.getColumnModel().getColumn(1).setMinWidth(375);
			memoTable.getColumnModel().getColumn(1).setPreferredWidth(375);
			memoTable.getColumnModel().getColumn(1).setMaxWidth(375);
			memoTable.getColumnModel().getColumn(2).setMinWidth(110);
			memoTable.getColumnModel().getColumn(2).setPreferredWidth(110);
			memoTable.getColumnModel().getColumn(2).setMaxWidth(110);
			memoTable.getColumnModel().getColumn(3).setMinWidth(90);
			memoTable.getColumnModel().getColumn(3).setPreferredWidth(90);
			memoTable.getColumnModel().getColumn(3).setMaxWidth(90);
			memoTable.getColumnModel().getColumn(3).setCellRenderer(ts);
			memoTable.getTableHeader().setReorderingAllowed(false);
			memoTable.getTableHeader().setResizingAllowed(false);
			memoTable.setRowHeight(29);
		}
	}

	public void showMsg(String str) {
		JOptionPane.showMessageDialog(this, str);
	}

	private void lbDelMemoMouseClicked(java.awt.event.MouseEvent evt) {
		String stidx = JOptionPane.showInputDialog("삭제할 응원글 번호를 입력해 주세요.");
		if (stidx == null || stidx.trim().isEmpty()) {
			showMsg("번호를 입력해 주세요");
			return;
		}
		try {
			int n = ttubeDao.deleteMemo(Integer.parseInt(stidx));
			String str = (n > 0) ? "응원 삭제" : "삭제 실패";
			showMsg(str);
			ArrayList<tTubeVO> arr1 = ttubeDao.listMemo(bjInfo);
			showTable(arr1);
		} catch (SQLException e) {
			showMsg("Error: " + e.getMessage());
		} catch (NumberFormatException e) {
			showMsg("숫자로 입력해 주세요.");
		}
	}

	private void tfMemoActionPerformed(java.awt.event.ActionEvent evt) {
	}

	MouseListener btHover = new MouseAdapter() {

		public void mouseEntered(java.awt.event.MouseEvent e) {
			Object b = e.getSource();
			if (b == lbWriteMemo) {
				lbWriteMemo.setIcon(icWriteMemoHover);
			} else if (b == lbDelMemo) {
				lbDelMemo.setIcon(icDelMemoHover);
			}
		}

		public void mouseExited(java.awt.event.MouseEvent e) {

			Object b = e.getSource();
			if (b == lbWriteMemo) {
				lbWriteMemo.setIcon(icWriteMemo);
			} else if (b == lbDelMemo) {
				lbDelMemo.setIcon(icDelMemo);
			}
		};
	};

	public static void main(String args[]) {

		try {
			for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					javax.swing.UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException ex) {
			java.util.logging.Logger.getLogger(tTubeBoard.class.getName()).log(java.util.logging.Level.SEVERE, null,
					ex);
		} catch (InstantiationException ex) {
			java.util.logging.Logger.getLogger(tTubeBoard.class.getName()).log(java.util.logging.Level.SEVERE, null,
					ex);
		} catch (IllegalAccessException ex) {
			java.util.logging.Logger.getLogger(tTubeBoard.class.getName()).log(java.util.logging.Level.SEVERE, null,
					ex);
		} catch (javax.swing.UnsupportedLookAndFeelException ex) {
			java.util.logging.Logger.getLogger(tTubeBoard.class.getName()).log(java.util.logging.Level.SEVERE, null,
					ex);
		}

		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new tTubeBoard().setVisible(true);
			}
		});
	}

	private javax.swing.JLabel lbNickname;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JScrollPane jScrollPane2;
	private javax.swing.JLabel lbDelMemo;
	private javax.swing.JLabel lbMemoBg;
	private javax.swing.JLabel lbWriteMemo;
	private javax.swing.JTable memoTable;
	private javax.swing.JTextField tfMemo;
	public ImageIcon icWriteMemo = new javax.swing.ImageIcon(getClass().getResource(("/tTube/images/icWriteMemo.png")));
	public ImageIcon icWriteMemoHover = new javax.swing.ImageIcon(
			getClass().getResource(("/tTube/images/icWriteMemoHover.png")));
	public ImageIcon icDelMemo = new javax.swing.ImageIcon(getClass().getResource(("/tTube/images/icDelMemo.png")));
	public ImageIcon icDelMemoHover = new javax.swing.ImageIcon(getClass().getResource(("/tTube/images/icDelMemoHover.png")));
	public ImageIcon imgMemoBg = new javax.swing.ImageIcon(getClass().getResource(("/tTube/images/imgMemoBg.png")));

	

}
