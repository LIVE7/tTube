package tTube;

import java.io.Serializable;
import java.sql.Date;

public class tTubeVO implements Serializable{
	
	private int idx;
	private String name;
	private String msg;
	private java.sql.Date wdate;
	
	public tTubeVO(int idx, String name, String msg, Date wdate) {
		super();
		this.idx = idx;
		this.name = name;
		this.msg = msg;
		this.wdate = wdate;
	}
	
	public tTubeVO() {
		this(0,null,null,null);
	}
	public int getIdx() {
		return idx;
	}

	public void setIdx(int idx) {
		this.idx = idx;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public java.sql.Date getWdate() {
		return wdate;
	}

	public void setWdate(java.sql.Date wdate) {
		this.wdate = wdate;
	}

}
