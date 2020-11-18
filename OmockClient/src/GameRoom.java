
public class GameRoom {
	private int roomnum;
	private String roomname, roomstate;
	
	public GameRoom(int roomnum, String roomstate, String roomname) {
		this.roomnum = roomnum;
		this.roomstate = roomstate;
		this.roomname = roomname;
	}
	public void setstate(String state) {
		this.roomstate = state;
	}
	public void setnum(int num) {
		this.roomnum = num;
	}
	public void setname(String name) {
		this.roomname = name;
	}
	
	public String toString() {
		String info = String.format("%-45d %-45s %-20s", this.roomnum, this.roomname, this.roomstate);
		return info; 
	}
}
