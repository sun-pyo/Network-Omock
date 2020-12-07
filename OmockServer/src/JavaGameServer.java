//JavaObjServer.java ObjectStream 기반 채팅 Server

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.awt.event.ActionEvent;
import javax.swing.SwingConstants;

public class JavaGameServer extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	JTextArea textArea;
	private JTextField txtPortNumber;
	private int roomnum;
	private ServerSocket socket; // 서버소켓
	private Socket client_socket; // accept() 에서 생성된 client 소켓
	private Vector UserVec = new Vector(); // 연결된 사용자를 저장할 벡터
	private static final int BUF_LEN = 128; // Windows 처럼 BUF_LEN 을 정의
	public ArrayList<GameRoom> GameRoomList;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					JavaGameServer frame = new JavaGameServer();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public JavaGameServer() {
		this.roomnum = 0;
		this.GameRoomList = new ArrayList<GameRoom>();
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 338, 440);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 10, 300, 298);
		contentPane.add(scrollPane);

		textArea = new JTextArea();
		textArea.setEditable(false);
		scrollPane.setViewportView(textArea);

		JLabel lblNewLabel = new JLabel("Port Number");
		lblNewLabel.setBounds(13, 318, 87, 26);
		contentPane.add(lblNewLabel);

		txtPortNumber = new JTextField();
		txtPortNumber.setHorizontalAlignment(SwingConstants.CENTER);
		txtPortNumber.setText("30000");
		txtPortNumber.setBounds(112, 318, 199, 26);
		contentPane.add(txtPortNumber);
		txtPortNumber.setColumns(10);

		JButton btnServerStart = new JButton("Server Start");
		btnServerStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					socket = new ServerSocket(Integer.parseInt(txtPortNumber.getText()));
				} catch (NumberFormatException | IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				AppendText("Chat Server Running..");
				btnServerStart.setText("Chat Server Running..");
				btnServerStart.setEnabled(false); // 서버를 더이상 실행시키지 못 하게 막는다
				txtPortNumber.setEnabled(false); // 더이상 포트번호 수정못 하게 막는다
				AcceptServer accept_server = new AcceptServer();
				accept_server.start();
			}
		});
		btnServerStart.setBounds(12, 356, 300, 35);
		contentPane.add(btnServerStart);
	}

	// 새로운 참가자 accept() 하고 user thread를 새로 생성한다.
	class AcceptServer extends Thread {
		@SuppressWarnings("unchecked")
		public void run() {
			while (true) { // 사용자 접속을 계속해서 받기 위해 while문
				try {
					AppendText("Waiting new clients ...");
					client_socket = socket.accept(); // accept가 일어나기 전까지는 무한 대기중
					AppendText("새로운 참가자 from " + client_socket);
					// User 당 하나씩 Thread 생성
					UserService new_user = new UserService(client_socket);
					UserVec.add(new_user); // 새로운 참가자 배열에 추가
					new_user.start(); // 만든 객체의 스레드 실행
					AppendText("현재 참가자 수 " + UserVec.size());
				} catch (IOException e) {
					AppendText("accept() error");
					// System.exit(0);
				}
			}
		}
	}

	public synchronized void AppendText(String str) {
		// textArea.append("사용자로부터 들어온 메세지 : " + str+"\n");
		textArea.append(str + "\n");
		textArea.setCaretPosition(textArea.getText().length());
	}

	public synchronized void AppendObject(ChatMsg msg) {
		// textArea.append("사용자로부터 들어온 object : " + str+"\n");
		textArea.append("code = " + msg.code + "\n");
		textArea.append("id = " + msg.UserName + "\n");
		textArea.append("data = " + msg.data + "\n");
		textArea.setCaretPosition(textArea.getText().length());
	}
	
	public synchronized void AppendGameMsg(GameMsg gm) {
		// textArea.append("사용자로부터 들어온 object : " + str+"\n");
		textArea.append("code = " + gm.code + "\n");
		textArea.append("name = " + gm.UserName + "\n");
		textArea.append("roomnum = " + gm.roomnum + "\n");
		textArea.append("x = " + gm.x + "\n");
		textArea.append("y = " + gm.y + "\n");		
		textArea.setCaretPosition(textArea.getText().length());
	}

	// User 당 생성되는 Thread
	// Read One 에서 대기 -> Write All
	class UserService extends Thread {
		private InputStream is;
		private OutputStream os;
		private DataInputStream dis;
		private DataOutputStream dos;

		private ObjectInputStream ois;
		private ObjectOutputStream oos;

		private Socket client_socket;
		private Vector user_vc;
		public String UserName = "";
		public String UserStatus;
		public UserService(Socket client_socket) {
			// TODO Auto-generated constructor stub
			// 매개변수로 넘어온 자료 저장
			this.client_socket = client_socket;
			this.user_vc = UserVec;
			try {
				oos = new ObjectOutputStream(client_socket.getOutputStream());
				oos.flush();
				ois = new ObjectInputStream(client_socket.getInputStream());
			} catch (Exception e) {
				AppendText("userService error");
			}
		}

		public void Logout() {
			String msg = "[" + UserName + "]님이 퇴장 하였습니다.\n";
			ChatMsg cm = new ChatMsg(this.UserName,"102","exit");
			WriteOthersObject(cm);
			UserVec.removeElement(this); // Logout한 현재 객체를 벡터에서 지운다
			AppendText("사용자 " + "[" + UserName + "] 퇴장. 현재 참가자 수 " + UserVec.size());
		}

		// 모든 User들에게 방송. 각각의 UserService Thread의 WriteONe() 을 호출한다.
		public synchronized void WriteAll(String str) {
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				if (user.UserStatus == "O")
					user.WriteOne(str);
			}
		}
		// 모든 User들에게 Object를 방송. 채팅 message와 image object를 보낼 수 있다
		public synchronized void WriteAllObject(Object ob) {
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				user.WriteOneObject(ob);
			}
		}

		// 나를 제외한 User들에게 방송. 각각의 UserService Thread의 WriteONe() 을 호출한다.
		public synchronized void WriteOthersObject(Object ob) {
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				if (user != this)
					user.WriteOneObject(ob);
			}
		}

		// Windows 처럼 message 제외한 나머지 부분은 NULL 로 만들기 위한 함수
		public byte[] MakePacket(String msg) {
			byte[] packet = new byte[BUF_LEN];
			byte[] bb = null;
			int i;
			for (i = 0; i < BUF_LEN; i++)
				packet[i] = 0;
			try {
				bb = msg.getBytes("euc-kr");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for (i = 0; i < bb.length; i++)
				packet[i] = bb[i];
			return packet;
		}

		// UserService Thread가 담당하는 Client 에게 1:1 전송
		public synchronized void WriteOne(String msg) {
			try {
				ChatMsg obcm = new ChatMsg("SERVER", "600", msg);
				oos.writeObject(obcm);
			} catch (IOException e) {
				AppendText("dos.writeObject() error");
				try {
					ois.close();
					oos.close();
					client_socket.close();
					client_socket = null;
					ois = null;
					oos = null;
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Logout(); // 에러가난 현재 객체를 벡터에서 지운다
			}
		}

		// 귓속말 전송
		public synchronized void WritePrivate(String msg) {
			try {
				ChatMsg obcm = new ChatMsg("귓속말", "600", msg);
				oos.writeObject(obcm);
			} catch (IOException e) {
				AppendText("dos.writeObject() error");
				try {
					oos.close();
					client_socket.close();
					client_socket = null;
					ois = null;
					oos = null;
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Logout(); // 에러가난 현재 객체를 벡터에서 지운다
			}
		}
		public synchronized void WriteOneObject(Object ob) {
			try {
			    oos.writeObject(ob);
			} 
			catch (IOException e) {
				AppendText("oos.writeObject(ob) error");		
				try {
					ois.close();
					oos.close();
					client_socket.close();
					client_socket = null;
					ois = null;
					oos = null;				
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Logout();
			}
		}
		public void start_game(GameRoom gameroom) {
			if(gameroom.start) {
				Timer time = new Timer();
				int delay = 1000;
				int period = 1000;
				time.scheduleAtFixedRate(new TimerTask() {
		            public void run() {
		                if (gameroom.interval == 0) {
		                	for(int a[]:gameroom.map) {
								Arrays.fill(a, 0);
							}
							gameroom.Gameprogress.clear();
							gameroom.start = false;
							if(gameroom.turn_player == null) return;
							UserService player1 = getUser(gameroom.player1);
							UserService player2 = getUser(gameroom.player2);
							if(gameroom.turn_player.matches("player2")) {
								if(player1 != null) {
									GameMsg winmsg = new GameMsg(gameroom.player1, "501", gameroom.getroomnum(), -1, -1, -1);
									player1.WriteOneObject(winmsg);
									gameroom.turn_player = null;
									gameroom.interval = 0;
								}
								if(player2 != null) {
									GameMsg lossmsg = new GameMsg(gameroom.player2, "502", gameroom.getroomnum(), -1, -1, -1);
									player2.WriteOneObject(lossmsg);
								}
							}
							else if(gameroom.turn_player.matches("player1")) {
								if(player2 != null) {
									GameMsg winmsg = new GameMsg(gameroom.player2, "501", gameroom.getroomnum(), -1, -1, -1);
									player2.WriteOneObject(winmsg);
									gameroom.turn_player = null;
									gameroom.interval = 0;
								}
								if(player1 != null) {
									GameMsg lossmsg = new GameMsg(gameroom.player1, "502", gameroom.getroomnum(), -1, -1, -1);
									player1.WriteOneObject(lossmsg);
								}
							}
		                    time.cancel();
		                } else {
		                	String time = Integer.toString(gameroom.setInterval());
		                	System.out.println(time);
		                	TimeMsg tm = new TimeMsg(gameroom.turn_player,"700",gameroom.getroomnum(), time);
		                	WriteInRoomObject(tm, gameroom.getroomnum());
            			}
		            }
		        }, delay, period);
			}
		}
		
		public UserService getUser(String username) {
			if(username == null) return null;
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				if(user.UserName.matches(username))
					return user;
			}
			return null;
		}
		
		public synchronized void WriteInRoomObject(Object ob, int roomnum) {
			for(GameRoom gameroom : GameRoomList) {
				if(gameroom.getroomnum() == roomnum) {
					UserService player1 = getUser(gameroom.player1);
					UserService player2 = getUser(gameroom.player2);
					if(player1 != null) player1.WriteOneObject(ob);
					if(player2 != null) player2.WriteOneObject(ob);
					for(String observer : gameroom.Observer) {
						UserService user = getUser(observer);
						if(user != null)
							user.WriteOneObject(ob);
					}
				}
			}
		}
		private synchronized boolean end_check(GameMsg gm, GameRoom gameroom) {
			int x = gm.x, y = gm.y, color = gm.color, count = 0;
			// 좌우 확인  
			while(x>=0 && gameroom.map[y][x] == color) {
				x--;
			}
			for(int i=1;i<=5;i++) {
				if(x+i<19 && gameroom.map[y][x+i] == color)
					count++;
			}
			if(count == 5) return true; // 오목 완성
			
			// 상하 확인  
			x = gm.x;
			y = gm.y;
			count = 0;
			while(y>=0 && gameroom.map[y][x] == color) {
				y--;
			}
			for(int i=1;i<=5;i++) {
				if(y+i<19 && gameroom.map[y+i][x] == color)
					count++;
			}
			if(count == 5) return true; // 오목 완성
			
			// /모양 대각선 확인
			x = gm.x;
			y = gm.y;
			count = 0;
			while(x>=0 && y<19 && gameroom.map[y][x] == color) {
				y++;
				x--;
			}
			for(int i=1;i<=5;i++) {
				if(x+i<19 && y-i >= 0 && gameroom.map[y-i][x+i] == color)
					count++;
			}
			if(count == 5) return true; // 오목 완성
			
			// \모양 대각선 확인
			x = gm.x;
			y = gm.y;
			count = 0;
			while(x>=0 && y>=0 && gameroom.map[y][x] == color) {
				y--;
				x--;
			}
			for(int i=1;i<=5;i++) {
				if(x+i<19 && y+i <19 && gameroom.map[y+i][x+i] == color)
					count++;
			}
			if(count == 5) return true; // 오목 완성
			return false;
			
		}
		public void DoGameMsg(GameMsg gm) {
			
			if(gm.code.matches("300")){
				// Game Start
				for(GameRoom gameroom : GameRoomList) {
					if(gm.roomnum == gameroom.getroomnum()) {
						gameroom.start = true;
						WriteInRoomObject(gm, gm.roomnum);
						UserService player = getUser(gameroom.player1);
						
						GameMsg gamemsg = new GameMsg(gameroom.player1, "301", gm.roomnum, -1, -1, -1);
						player.WriteOneObject(gamemsg);
						gameroom.turn_player = "player1";
						start_game(gameroom);
						gameroom.turn_player = gameroom.getmyrole(gameroom.getotherplyer(gm.UserName));
						gameroom.interval = Integer.parseInt(gameroom.turn_time);
						break;
					}
				}
			}
			else if(gm.code.matches("302")) { //착수
				WriteInRoomObject(gm, gm.roomnum);
				for(GameRoom gameroom : GameRoomList) {
					if(gm.roomnum == gameroom.getroomnum()) {
						gameroom.map[gm.y][gm.x] = gm.color;
						gameroom.Gameprogress.add(new Stone(gm.x, gm.y, gm.color));
						UserService player = getUser(gameroom.getotherplyer(this.UserName));
						if(end_check(gm, gameroom)) {
							for(int a[]:gameroom.map) {
								Arrays.fill(a, 0);
							}
							gameroom.Gameprogress.clear();
							gameroom.start = false;
							gameroom.turn_player = null;
							gameroom.interval = 0;
							GameMsg winmsg = new GameMsg(gm.UserName, "501", gm.roomnum, gm.x, gm.y, gm.color);
							this.WriteOneObject(winmsg);
							GameMsg lossmsg = new GameMsg(gm.UserName, "502", gm.roomnum, gm.x, gm.y, gm.color);
							player.WriteOneObject(lossmsg);
							break;
						}
						if(player != null)
						{
						
							GameMsg gamemsg = new GameMsg(gm.UserName, "301", gm.roomnum, gm.x, gm.y, gm.color);
							player.WriteOneObject(gamemsg);
							gameroom.turn_player = gameroom.getmyrole(gameroom.getotherplyer(gm.UserName));
							gameroom.interval = Integer.parseInt(gameroom.turn_time);
						}
						break;
					}
				}
			}
			else if(gm.code.matches("303")) {
				for(GameRoom gameroom : GameRoomList) {
					UserService player = getUser(gameroom.getotherplyer(gm.UserName));
					if(player != null) {
						
						GameMsg gamemsg = new GameMsg(gm.UserName, "301", gm.roomnum, -1, -1, gm.color);
						player.WriteOneObject(gamemsg);
						gameroom.turn_player = gameroom.getmyrole(gameroom.getotherplyer(gm.UserName));
						gameroom.interval = Integer.parseInt(gameroom.turn_time);
					}
				}
			}
			else if(gm.code.matches("400")) {
				for(GameRoom gameroom : GameRoomList) {
					UserService player = getUser(gameroom.getotherplyer(gm.UserName));
					if(player != null) player.WriteOneObject(gm);
				}
			}
			else if(gm.code.matches("401")) {
				for(GameRoom gameroom : GameRoomList) {
					UserService player1 = getUser(gameroom.getotherplyer(gameroom.player1));
					UserService player2 = getUser(gameroom.getotherplyer(gameroom.player2));
					
					GameMsg gm2 = new GameMsg(gm.UserName, "503", gm.roomnum, -1, -1, -1);
					
					for(int a[]:gameroom.map) {
						Arrays.fill(a, 0);
					}
					gameroom.Gameprogress.clear();
					
					if(player1 != null) player1.WriteOneObject(gm2);
					if(player2 != null) player2.WriteOneObject(gm2);
				}
			}
			else if(gm.code.matches("402")) {
				for(GameRoom gameroom : GameRoomList) {
					UserService player1 = getUser(gameroom.getotherplyer(gameroom.player1));
					UserService player2 = getUser(gameroom.getotherplyer(gameroom.player2));
					
					if(player1 != null) player1.WriteOneObject(gm);
					if(player2 != null) player2.WriteOneObject(gm);
				}
			}
			else if(gm.code.matches("403")) {
				for(GameRoom gameroom : GameRoomList) {
					if(gm.roomnum == gameroom.getroomnum()) {
						UserService player = getUser(gameroom.getotherplyer(this.UserName));
						if(player != null) {
							gameroom.start = false;
							gameroom.turn_player = null;
							gameroom.interval = 0;
							GameMsg winmsg = new GameMsg(gm.UserName, "501", gm.roomnum, gm.x, gm.y, gm.color);
							player.WriteOneObject(winmsg);
							GameMsg lossmsg = new GameMsg(gm.UserName, "502", gm.roomnum, gm.x, gm.y, gm.color);
							this.WriteOneObject(lossmsg);
						}
						break;
					}
				}
			}
		}
		// 플레이어 업데이트
		private synchronized void update_msg(GameRoom gameroom, String data) {
			ChatMsg chatmsg = new ChatMsg(UserName, "203", data);
			chatmsg.player1 = gameroom.player1;
			chatmsg.player2 = gameroom.player2;
			chatmsg.roomnum = gameroom.getroomnum();
			chatmsg.roomname = gameroom.getroomname();
			chatmsg.roomstate = gameroom.getroomstate();
			chatmsg.start = gameroom.start;
			WriteInRoomObject(chatmsg, chatmsg.roomnum);
		}
		
		
		public void run() {
			while (true) { 
				try {
					Object obcm = null;
					String msg = null;
					ChatMsg cm = null;
					GameMsg gm = null;
					if (socket == null)
						break;
					try {
						obcm = ois.readObject();
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return;
					}
					if (obcm == null)
						break;
					if (obcm instanceof ChatMsg) {
						cm = (ChatMsg) obcm;
						AppendObject(cm);
					} else if(obcm instanceof GameMsg) {
						gm = (GameMsg) obcm;
						AppendGameMsg(gm);
						DoGameMsg(gm);
						continue;
					}
					else
						continue;
					if (cm.code.matches("100")) {
						UserName = cm.UserName;
						InitMsg im = new InitMsg();
						for(GameRoom gameroom : GameRoomList) {
							im.addname(gameroom.getroomname());
							im.addnum(gameroom.getroomnum());
							im.addstate(gameroom.getroomstate());
						}
						for (int i = 0; i < user_vc.size(); i++) {
							UserService user = (UserService) user_vc.elementAt(i);
							im.UserList.add(user.UserName);
						}
						im.size = GameRoomList.size();
						WriteOneObject(im);
						ChatMsg entermsg = new ChatMsg(UserName, "101","new user");
						WriteOthersObject(entermsg);
					} 
					else if (cm.code.matches("600")) {  // 게임방 채팅
						msg = String.format("[%s] %s", cm.UserName, cm.data);
						AppendText(msg); // server 화면에 출력
						WriteInRoomObject(cm,cm.roomnum);
					} else if (cm.code.matches("400")) { // logout message 처리
						Logout();
						break;
					}
					else if(cm.code.matches("200")){  // 방생성
						cm.roomnum = roomnum++;
						GameRoom gameroom = new GameRoom(cm.roomnum,"none", cm.roomname);
						gameroom.setrole(cm.UserName);
						gameroom.setstate();
						gameroom.start = false;
						gameroom.watching = cm.start;
						gameroom.turn_time =  cm.data;
						gameroom.interval = Integer.parseInt(gameroom.turn_time);
						cm.roomstate = gameroom.getroomstate();
						cm.role = gameroom.getmyrole(cm.UserName);
						cm.player1 = gameroom.player1;
						cm.player2 = gameroom.player2;
						cm.data = gameroom.turn_time;
						GameRoomList.add(gameroom);
						WriteOneObject(cm);
						update_msg(gameroom, "enter");
					}
					else if(cm.code.matches("201")) {   // 방 입장
						for(GameRoom gameroom : GameRoomList) {
							if(gameroom.getroomnum() == cm.roomnum) {
								gameroom.setrole(cm.UserName);
								cm.role = gameroom.getmyrole(cm.UserName);
								cm.player1 = gameroom.player1;
								cm.player2 = gameroom.player2;
								if(cm.role.matches("player1") || cm.role.matches("player2")) {
									cm.data = gameroom.turn_time;
									cm.code = "200";
									WriteOneObject(cm);
								}
								else if(cm.role.matches("observer")) {
									if(gameroom.watching)
									{
										ChatMsg createmsg = new ChatMsg(cm.UserName,"200","observer enter");
										createmsg.roomnum = gameroom.getroomnum();
										createmsg.player1 = gameroom.player1;
										createmsg.player2 = gameroom.player2;
										createmsg.role = cm.role;
										createmsg.data = gameroom.turn_time;
										WriteOneObject(createmsg);
										
										for (Stone s : gameroom.Gameprogress) {
											GameMsg ingmsg = new GameMsg(UserName, "800", gameroom.getroomnum() ,s.getx(), s.gety(), s.getcolor());
											WriteOneObject(ingmsg);
										}
									}
									else
									{
										break;
									}
								}
								update_msg(gameroom, "enter");
								break;
							}
						}
					}
					else if(cm.code.matches("204")) {   // 방 상태 업데이트
						for(GameRoom gameroom : GameRoomList) {
							if(gameroom.getroomnum() == cm.roomnum) {
								gameroom.setstate();
								cm.roomstate = gameroom.getroomstate();
								WriteAllObject(cm);
								break;
							}
						}
					}
					else if(cm.code.matches("205")) {  // 게임방 퇴장 
						for(GameRoom gameroom : GameRoomList) {
							if(gameroom.getroomnum() == cm.roomnum) {
								
								if(gameroom.getmyrole(cm.UserName).matches("player1")) {
									if(gameroom.start) {
										for(int a[]:gameroom.map) {
											Arrays.fill(a, 0);
										}
										gameroom.Gameprogress.clear();
										WriteOneObject(cm);
										String orderplayer = gameroom.getotherplyer(cm.UserName);
										UserService user = getUser(cm.UserName);
										gameroom.turn_player = null;
										gameroom.interval = 0;
										GameMsg winmsg = new GameMsg(cm.UserName, "501", cm.roomnum, 0, 0, 0);
										user.WriteOneObject(winmsg);
										gameroom.start = false;
									}
									gameroom.player1 = null;
								}else if(gameroom.getmyrole(cm.UserName).matches("player2")) {
									if(gameroom.start) {
										for(int a[]:gameroom.map) {
											Arrays.fill(a, 0);
										}
										gameroom.Gameprogress.clear();
										UserService user = getUser(cm.UserName);
										GameMsg winmsg = new GameMsg(cm.UserName, "501", cm.roomnum, 0, 0, 0);
										gameroom.turn_player = null;
										gameroom.interval = 0;
										user.WriteOneObject(winmsg);
										gameroom.start = false;
									}
									gameroom.player2 = null;
								}else {
									for(String s:gameroom.Observer) {
										if(s.matches(cm.UserName)) {
											gameroom.Observer.remove(s);
											break;
										}
									}
								}
								gameroom.setstate();
								if(gameroom.getroomstate().matches("none")) {
									ChatMsg chatmsg = new ChatMsg(UserName,"206","방 삭제");
									chatmsg.roomnum = gameroom.getroomnum();
									WriteAllObject(chatmsg);
									GameRoomList.remove(gameroom);
									break;
								}
								update_msg(gameroom,"exit");
								
								ChatMsg cm2 = new ChatMsg(cm.UserName, "204", "room list update");
								cm2.player1 =  gameroom.player1;
								cm2.player2 = gameroom.player2;
								cm2.roomname = gameroom.getroomname();
								cm2.roomnum = gameroom.getroomnum();
								cm2.roomstate = gameroom.getroomstate();
								WriteAllObject(cm2);
								break;
							}
						}
					}
					else { // 300, 500, ... 기타 object는 모두 방송한다.
						WriteAllObject(cm);
					} 
				} catch (IOException e) {
					AppendText("ois.readObject() error");
					try {
						ois.close();
						oos.close();
						client_socket.close();
						Logout(); // 에러가난 현재 객체를 벡터에서 지운다
						break;
					} catch (Exception ee) {
						break;
					} // catch문 끝
				} // 바깥 catch문끝
			} // while
		} // run
	}

}
