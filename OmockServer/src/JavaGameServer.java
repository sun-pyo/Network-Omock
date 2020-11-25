//JavaObjServer.java ObjectStream ��� ä�� Server

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
	private ServerSocket socket; // ��������
	private Socket client_socket; // accept() ���� ������ client ����
	private Vector UserVec = new Vector(); // ����� ����ڸ� ������ ����
	private static final int BUF_LEN = 128; // Windows ó�� BUF_LEN �� ����
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
				btnServerStart.setEnabled(false); // ������ ���̻� �����Ű�� �� �ϰ� ���´�
				txtPortNumber.setEnabled(false); // ���̻� ��Ʈ��ȣ ������ �ϰ� ���´�
				AcceptServer accept_server = new AcceptServer();
				accept_server.start();
			}
		});
		btnServerStart.setBounds(12, 356, 300, 35);
		contentPane.add(btnServerStart);
	}

	// ���ο� ������ accept() �ϰ� user thread�� ���� �����Ѵ�.
	class AcceptServer extends Thread {
		@SuppressWarnings("unchecked")
		public void run() {
			while (true) { // ����� ������ ����ؼ� �ޱ� ���� while��
				try {
					AppendText("Waiting new clients ...");
					client_socket = socket.accept(); // accept�� �Ͼ�� �������� ���� �����
					AppendText("���ο� ������ from " + client_socket);
					// User �� �ϳ��� Thread ����
					UserService new_user = new UserService(client_socket);
					UserVec.add(new_user); // ���ο� ������ �迭�� �߰�
					new_user.start(); // ���� ��ü�� ������ ����
					AppendText("���� ������ �� " + UserVec.size());
				} catch (IOException e) {
					AppendText("accept() error");
					// System.exit(0);
				}
			}
		}
	}

	public void AppendText(String str) {
		// textArea.append("����ڷκ��� ���� �޼��� : " + str+"\n");
		textArea.append(str + "\n");
		textArea.setCaretPosition(textArea.getText().length());
	}

	public void AppendObject(ChatMsg msg) {
		// textArea.append("����ڷκ��� ���� object : " + str+"\n");
		textArea.append("code = " + msg.code + "\n");
		textArea.append("id = " + msg.UserName + "\n");
		textArea.append("data = " + msg.data + "\n");
		textArea.setCaretPosition(textArea.getText().length());
	}
	
	public void AppendGameMsg(GameMsg gm) {
		// textArea.append("����ڷκ��� ���� object : " + str+"\n");
		textArea.append("code = " + gm.code + "\n");
		textArea.append("name = " + gm.UserName + "\n");
		textArea.append("roomnum = " + gm.roomnum + "\n");
		textArea.append("x = " + gm.x + "\n");
		textArea.append("y = " + gm.y + "\n");		
		textArea.setCaretPosition(textArea.getText().length());
	}

	// User �� �����Ǵ� Thread
	// Read One ���� ��� -> Write All
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
			// �Ű������� �Ѿ�� �ڷ� ����
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

		public void Login() {		
		}

		public void Logout() {
			String msg = "[" + UserName + "]���� ���� �Ͽ����ϴ�.\n";
			UserVec.removeElement(this); // Logout�� ���� ��ü�� ���Ϳ��� �����
			WriteAll(msg); // ���� ������ �ٸ� User�鿡�� ����
			AppendText("����� " + "[" + UserName + "] ����. ���� ������ �� " + UserVec.size());
		}

		// ��� User�鿡�� ���. ������ UserService Thread�� WriteONe() �� ȣ���Ѵ�.
		public void WriteAll(String str) {
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				if (user.UserStatus == "O")
					user.WriteOne(str);
			}
		}
		// ��� User�鿡�� Object�� ���. ä�� message�� image object�� ���� �� �ִ�
		public void WriteAllObject(Object ob) {
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				user.WriteOneObject(ob);
			}
		}

		// ���� ������ User�鿡�� ���. ������ UserService Thread�� WriteONe() �� ȣ���Ѵ�.
		public void WriteOthers(String str) {
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				if (user != this && user.UserStatus == "O")
					user.WriteOne(str);
			}
		}

		// Windows ó�� message ������ ������ �κ��� NULL �� ����� ���� �Լ�
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

		// UserService Thread�� ����ϴ� Client ���� 1:1 ����
		public void WriteOne(String msg) {
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
				Logout(); // �������� ���� ��ü�� ���Ϳ��� �����
			}
		}

		// �ӼӸ� ����
		public void WritePrivate(String msg) {
			try {
				ChatMsg obcm = new ChatMsg("�ӼӸ�", "600", msg);
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
				Logout(); // �������� ���� ��ü�� ���Ϳ��� �����
			}
		}
		public void WriteOneObject(Object ob) {
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
		
		public UserService getUser(String username) {
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				if(user.UserName.matches(username))
					return user;
			}
			return null;
		}
		
		public void WriteInRoomObject(Object ob, int roomnum) {
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
		public void DoGameMsg(GameMsg gm) {
			
			if (gm.code.matches("300")){
				// Game Start
			}
			else if(gm.code.matches("302")) {
				WriteInRoomObject(gm, gm.roomnum);
				for(GameRoom gameroom : GameRoomList) {
					if(gm.roomnum == gameroom.getroomnum()) {
						UserService player = getUser(gameroom.getotherplyer(this.UserName));
						if(player != null)
						{
							GameMsg gamemsg = new GameMsg(gm.UserName, "301", gm.roomnum, gm.x, gm.y, gm.color);
							player.WriteOneObject(gamemsg);
						}
					}
				}
			}
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
						im.size = GameRoomList.size();
						WriteOneObject(im);
						//Login();
					} 
					else if (cm.code.matches("600")) {
						msg = String.format("[%s] %s", cm.UserName, cm.data);
						AppendText(msg); // server ȭ�鿡 ���
						String[] args = msg.split(" "); // �ܾ���� �и��Ѵ�.
						if (args.length == 1) { // Enter key �� ���� ��� Wakeup ó���� �Ѵ�.
							UserStatus = "O";
						} else if (args[1].matches("/exit")) {
							Logout();
							break;
						}else { // �Ϲ� ä�� �޽���
							WriteAllObject(cm);
						}
					} else if (cm.code.matches("400")) { // logout message ó��
						Logout();
						break;
					}
					else if(cm.code.matches("200")){  // �����
						cm.roomnum = roomnum++;
						cm.roomstate = "�� ��";
						GameRoom gameroom = new GameRoom(cm.roomnum, cm.roomstate, cm.roomname);
						gameroom.setrole(cm.UserName);
						cm.role = gameroom.getmyrole(cm.UserName);
						cm.player1 = gameroom.player1;
						GameRoomList.add(gameroom);
						WriteOneObject(cm);
					}
					else if(cm.code.matches("201")) {   // �� ����
						for(GameRoom gameroom : GameRoomList) {
							if(gameroom.getroomnum() == cm.roomnum) {
								gameroom.setrole(cm.UserName);
								cm.role = gameroom.getmyrole(cm.UserName);
								cm.player1 = gameroom.player1;
								cm.player2 = gameroom.player2;
								if(cm.role.matches("player1") || cm.role.matches("player2")) {
									cm.code = "200";
									WriteOneObject(cm);
								}
								else if(cm.role.matches("observer")) {
								}
								ChatMsg chatmsg = new ChatMsg(cm.UserName, "203", "enter room");
								chatmsg.player1 = gameroom.player1;
								chatmsg.player2 = gameroom.player2;
								chatmsg.roomnum = cm.roomnum;
								chatmsg.roomname = cm.roomname;
								chatmsg.roomstate = cm.roomstate;
								WriteInRoomObject(chatmsg, chatmsg.roomnum);
								break;
							}
						}
					}
					else if(cm.code.matches("204")) {   // �� ���� ������Ʈ
						for(GameRoom gameroom : GameRoomList) {
							if(gameroom.getroomnum() == cm.roomnum) {
								gameroom.setstate();
								cm.roomstate = gameroom.getroomstate();
								WriteAllObject(cm);
								break;
							}
						}
					}
					else { // 300, 500, ... ��Ÿ object�� ��� ����Ѵ�.
						WriteAllObject(cm);
					} 
				} catch (IOException e) {
					AppendText("ois.readObject() error");
					try {
						ois.close();
						oos.close();
						client_socket.close();
						Logout(); // �������� ���� ��ü�� ���Ϳ��� �����
						break;
					} catch (Exception ee) {
						break;
					} // catch�� ��
				} // �ٱ� catch����
			} // while
		} // run
	}

}
