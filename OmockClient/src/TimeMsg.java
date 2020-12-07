
// ChatMsg.java 채팅 메시지 ObjectStream 용.
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.ImageIcon;

class TimeMsg implements Serializable {
	private static final long serialVersionUID = 1L;
	public String code; 
	public String player;
	public int roomnum;
	public String time;
	
	
	public TimeMsg(String player, String code, int roomnum, String time) {
		this.code = code;
		this.player = player;
		this.roomnum = roomnum;
		this.time = time;
	}
}