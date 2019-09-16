package tTube;

import java.sql.*;
import java.util.*;
import static tTube.tTubeMain.bjInfo;


public class tTubeDAO {

	private Connection con;
	private PreparedStatement ps;
	private ResultSet rs;
	

	public tTubeDAO() {
	}

	public int insertMemo(tTubeVO memo) throws SQLException {
		try {
			con = tTubeDBUtil.getCon();
			String sql = "insert into tTubeMemo(idx, tname, tmemo,tdate,bjname) values(ttube_seq.nextval,?,?,sysdate,?)";
			ps = con.prepareStatement(sql);
			ps.setString(1, memo.getName());
			ps.setString(2, memo.getMsg());
			ps.setString(3,bjInfo);
			int n = ps.executeUpdate();
			return n;
		} finally {
			close();
		}

	}

	public int deleteMemo(int idx) throws SQLException {
		try {
			con = tTubeDBUtil.getCon();

			String sql = "delete from ttubememo where idx=?";
			ps = con.prepareStatement(sql);
			ps.setInt(1, idx);
			int n = ps.executeUpdate();
			return n;
		} finally {
			close();
		}
	}

	public ArrayList<tTubeVO> listMemo(String bjInfo) throws SQLException {
		try {
			con = tTubeDBUtil.getCon();
			String sql = "select idx, tname, tmemo, nvl(tdate,'19-01-01') tdate from tTubeMemo where bjname=? order by idx desc";
			ps = con.prepareStatement(sql);
			ps.setString(1, bjInfo);
			rs = ps.executeQuery();
			return makeList(rs);
		} finally {
			close();
		}
	}

	public ArrayList<tTubeVO> makeList(ResultSet rs) throws SQLException {
		ArrayList<tTubeVO> arr = new ArrayList<>();
		while (rs.next()) {
			int idx = rs.getInt(1);
			String name = rs.getString(2);
			String msg = rs.getString(3);
			java.sql.Date wdate = rs.getDate(4);
			// record->MemoVO
			tTubeVO memo = new tTubeVO(idx, name, msg, wdate);
			arr.add(memo);
		}
		return arr;
	}

	public void close() {
		try {
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
			if (con != null)
				con.close();
		} catch (SQLException e) {
			System.out.println(e);
		}
	}

}
