import java.io.Serializable;

public class GameMsg implements Serializable{
	
	private static final long serialVersionUID = 1L;
	public String code; 
	public String UserName;
	public int roomnum;
	public int x,y, color;
	public boolean start;
	
	
	public GameMsg(String UserName, String code, int roomnum, int x, int y, int color) {
		this.code = code;
		this.UserName = UserName;
		this.roomnum = roomnum;
		this.x = x;
		this.y = y;
		this.color = color;
	}
	
}
