import java.util.ArrayList;

public class GameRoom {
	private int roomnum;
	private String roomname, roomstate;
	private String player1, player2;
	private ArrayList<String> Observer;
	
	public GameRoom(int roomnum, String roomstate, String roomname) {
		this.roomnum = roomnum;
		this.roomstate = roomstate;
		this.roomname = roomname;
	}
	
	public void setplayer(String player) {
		if(player1 == null) {
			this.player1 = player;
			return;
		}
		else if(player2 == null) {
			this.player2 = player;
			return;
		}
	}
	
	public void addobserver(String observer) {
		this.Observer.add(observer);
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
	
	public int getroomnum() {
		return roomnum;
	}
	public String getroomname() {
		return roomname;
	}
	
	public String toString() {
		String info = String.format("%-45d %-45s %-20s", this.roomnum, this.roomname, this.roomstate);
		return info; 
	}
}
