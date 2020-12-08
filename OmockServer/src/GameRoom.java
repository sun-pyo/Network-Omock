import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class GameRoom {
	private int roomnum;
	private String roomname, roomstate;
	public String player1 = null, player2 = null;
	public ArrayList<String> Observer = new ArrayList<>();
	public int[][] map = new int[19][19];
	public ArrayList<Stone> Gameprogress = new ArrayList<Stone>();
	public boolean watching; //���� ���� ���� 
	public boolean start; // ���� ���� ����
	public int interval;
	public String turn_time,turn_player = "player1";

	
	public GameRoom(int roomnum, String roomstate, String roomname) {
		this.roomnum = roomnum;
		this.roomstate = roomstate;
		this.roomname = roomname;
	}
	
	public String getotherplyer(String myname) {
		if(player1.matches(myname)) {
			return player2;
		}
		else if(player2.matches(myname)) {
			return player1;
		}
		return null;
	}
	
	public void setrole(String player) {
		if(player1 == null) {
			this.player1 = player;
			return;
		}
		else if(player2 == null) {
			this.player2 = player;
			return;
		}
		else {
			Observer.add(player);
		}
	}
	
	public String getmyrole(String user) {
		if(player1 != null && player1.matches(user)) {
			return "player1";
		}
		else if(player2 != null && player2.matches(user)) {
			return "player2";
		}
		else{
			for(String name : Observer) {
				if(name.matches(user))
					return "observer";
			}
			return null;
		}
		
	}
	
	public void setstate() {
		if(this.player1 != null && this.player2 != null) {
			if(watching)
				this.roomstate = "��������";
			else
				this.roomstate = "����Ұ�";
		}
		else if(this.player1 != null || this.player2 != null){
			this.roomstate = "�����";
		}
		else {
			this.roomstate = "none";
		}
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
	public String getroomstate() {
		return roomstate;
	}
	
	public String toString() {
		String info = String.format("%-45d %-45s %-20s", this.roomnum, this.roomname, this.roomstate);
		return info; 
	}
	
	public synchronized int setInterval() {
        return --this.interval;
    }
}
