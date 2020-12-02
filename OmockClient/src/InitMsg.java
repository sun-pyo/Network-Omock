import java.io.Serializable;
import java.util.ArrayList;

public class InitMsg implements Serializable{
	private static final long serialVersionUID = 1L;
	public ArrayList<Integer> roomnumList = new ArrayList<>();
	public ArrayList<String> roomnameList = new ArrayList<>();
	public ArrayList<String> roomstateList = new ArrayList<>();
	public ArrayList<String> UserList = new ArrayList<>();
	public int size;
	
	public void addnum(int num) {
		roomnumList.add(num);
	}
	
	public void addname(String name) {
		roomnameList.add(name);
	}

	public void addstate(String state) {
		roomstateList.add(state);
	}
	
}
