package net.basilwang.entity;

public class Curriculum {
	private int id;
	private String name;
	private String teachername;
	private int credit;
	private int timeSpan;
	private int noteNum;
	private String workInfo;

	public Curriculum() {
	}

	public Curriculum(String name, String teachername, int credit,
			int timeSpan, int noteNum, String workInfo) {
		super();
		this.name = name;
		this.teachername = teachername;
		this.credit = credit;
		this.timeSpan = timeSpan;
		this.noteNum = noteNum;
		this.workInfo = workInfo;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTeachername() {
		return teachername;
	}

	public void setTeachername(String teachername) {
		this.teachername = teachername;
	}

	public int getCredit() {
		return credit;
	}

	public void setCredit(int credit) {
		this.credit = credit;
	}

	public int getTimeSpan() {
		return timeSpan;
	}

	public void setTimeSpan(int timeSpan) {
		this.timeSpan = timeSpan;
	}

	public int getNoteNum() {
		return noteNum;
	}

	public void setNoteNum(int noteNum) {
		this.noteNum = noteNum;
	}

	public String getWorkInfo() {
		return workInfo;
	}

	public void setWorkInfo(String workInfo) {
		this.workInfo = workInfo;
	}

}
