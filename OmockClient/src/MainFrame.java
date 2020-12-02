import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Stroke;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import javax.swing.JToggleButton;
import javax.swing.JList;
import javax.swing.JOptionPane;

import java.awt.Canvas;
import javax.swing.border.TitledBorder;

public class MainFrame extends JFrame{
	/**
	 * 
	 */

	private static final int BUF_LEN = 128; // Windows 처럼 BUF_LEN 을 정의
	private Socket socket; // 연결소켓
	private DataInputStream dis;
	private DataOutputStream dos;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private GameLobby LobbyPanel;
	public ArrayList<GameFrame> GameFrameList;
	private JPanel contentPane;
	private MainFrame frame;
	public String UserName;
	/**
	 * Create the frame.
	 */
	public MainFrame(String username, String ip_addr, String port_no) {
		
		this.UserName = username;
		this.frame = this;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 895, 518);
		setVisible(true);
		GameFrameList = new ArrayList<GameFrame>();
		LobbyPanel = new GameLobby(frame);
		changePanel(LobbyPanel);
		setTitle(UserName + "님의 MainFrame");
		setVisible(true); 
		try {
			
			socket = new Socket(ip_addr, Integer.parseInt(port_no));

			oos = new ObjectOutputStream(socket.getOutputStream());
			oos.flush();
			ois = new ObjectInputStream(socket.getInputStream());

			ChatMsg obcm = new ChatMsg(UserName, "100", "Login");
			SendObject(obcm);

			ListenNetwork net = new ListenNetwork();
			net.start();

		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//AppendText("connect error");
		}
		

	}
	
	public void changePanel(JPanel panel) {
		getContentPane().removeAll();
		getContentPane().add(panel);
		revalidate();
		repaint();
	}

	// Server Message를 수신해서 화면에 표시
	class ListenNetwork extends Thread{
		public void run() {
			while (true) {
				try {
					Object obcm = null;
					String msg = null;
					ChatMsg cm;
					InitMsg im;
					GameMsg gm;
					try {
						obcm = ois.readObject();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
						break;
					}
					if (obcm == null)
						break;
					if (obcm instanceof ChatMsg) {
						cm = (ChatMsg) obcm;
						msg = String.format("[%s] %s", cm.UserName, cm.data);
						DoChatMsg(cm);
					}
					else if(obcm instanceof GameMsg) {
						gm = (GameMsg) obcm;
						DoGameMsg(gm);
					}
					else if(obcm instanceof InitMsg) {
						im = (InitMsg)obcm;
						for(int i = 0;i<im.size;i++) {
							LobbyPanel.Gameroommodel.addElement(new GameRoom(im.roomnumList.get(i), im.roomstateList.get(i), im.roomnameList.get(i)));
						}
						for(String u : im.UserList) {
							LobbyPanel.Usermodel.addElement(u);
						}
					}
					else
						continue;
				} catch (IOException e) {
					//AppendText("ois.readObject() error");
					try {
						ois.close();
						oos.close();
						socket.close();

						break;
					} catch (Exception ee) {
						break;
					} // catch문 끝
				} // 바깥 catch문끝

			}
		}
	}
	
	private void DoChatMsg(ChatMsg cm) {
		System.out.println(cm.UserName + " code : " + cm.code + " player1 " +cm.player1 + " player2 " +cm.player2 );
		switch (cm.code) {
		case "101":
			if(!UserName.matches(cm.UserName))
				LobbyPanel.Usermodel.addElement(cm.UserName);
			break;
		case "102":
			LobbyPanel.Usermodel.removeElement(cm.UserName);
			break;
		case "600": 
			for(GameFrame game : GameFrameList) {
				if(cm.roomnum == game.roomnumber) {
					if(UserName.matches(cm.UserName)) {
						String msg = cm.data;
						game.AppendTextR(msg);						
					}
					else {
						String msg = cm.UserName + ":" + cm.data;
						game.AppendText(msg);
					}
				}
			}
			break;
		case "200":     // enter
			enterGameRoom(cm);
			break;
		case "203":  // player update
			for(GameFrame game : GameFrameList) {
				if(cm.roomnum == game.roomnumber) {
					if(game.role.matches("observer")) {
						game.state = 0;
						game.board.startpanel.setVisible(false);
					}
					else if(cm.start) {
						game.state = 1;
						game.board.startpanel.setVisible(false);
					}else {
						game.state = 0;
						game.board.startpanel.setVisible(true);
					}
					
					if(cm.data.matches("enter")) {
						if(!UserName.matches(cm.UserName))
							game.AppendText("[SERVICE] " + cm.UserName + "님이 입장하셨습니다.");
						else
							game.AppendText("[SERVICE] " + cm.roomnum +"번 방에 입장하셨습니다.");
					}
					else if(cm.data.matches("exit"))
						game.AppendText("[SERVICE] " + cm.UserName + "님이 퇴장하셨습니다.");
					
					if(cm.player1 == null)
						game.player1label.setText(" ");
					else if(!game.player1label.getText().matches(cm.player1))
						game.player1label.setText(cm.player1);
					if(cm.player2 == null)
						game.player2label.setText(" ");
					else if(cm.player2 != null && !game.player2label.getText().matches(cm.player2))
						game.player2label.setText(cm.player2);
				}
			}
			break;
		case "204":    // gameroom list update
			GameRoom gameroom = getGameRoom(cm.roomnum);
			if(gameroom != null) {
				LobbyPanel.Gameroommodel.removeElement(gameroom);
			}
			LobbyPanel.Gameroommodel.addElement(new GameRoom(cm.roomnum, cm.roomstate, cm.roomname));
			break;
		case "205":
			for(GameFrame game : GameFrameList) {
				if(cm.roomnum == game.roomnumber) {
					GameFrameList.remove(game);
					break;
				}
			}
			break;
		case "206":
			GameRoom room = getGameRoom(cm.roomnum);
			if(room != null)
				LobbyPanel.Gameroommodel.removeElement(room);
			for(GameFrame game : GameFrameList) {
				if(cm.roomnum == game.roomnumber) {
					//game.dispose();
					GameFrameList.remove(game);
					break;
				}
			}
			break;
		}
	}
	private void DoGameMsg(GameMsg gm) {
		switch (gm.code) {
		case "300":
			for(GameFrame game : GameFrameList) {
				game.board.ResetBoard();
				game.board.endpanel.setVisible(false);
				game.board.startpanel.setVisible(false);
				game.state = 1;
			}
			break;
		case "301":  // your turn
			//System.out.println("username : " + gm.UserName + "x = " + gm.x + " y =" + gm.y + " color" + gm.color);
			for(GameFrame game : GameFrameList) {
				if(gm.roomnum == game.roomnumber) {
					game.turn = 1;
				}
			}
			break;
		case "302":  // 착수
			for(GameFrame game : GameFrameList) {
				if(gm.roomnum == game.roomnumber) {
					game.board.AddGameprogress(gm.x, gm.y, gm.color);
				}
			}
			break;
		case "400":
			for(GameFrame game : GameFrameList) {
				if(gm.roomnum == game.roomnumber) {
					game.state = 0;
					game.board.panel.setVisible(true);
					break;
				}
			}
			break;
		case "402":
			for(GameFrame game : GameFrameList) {
				if(gm.roomnum == game.roomnumber) {
					game.state = 1;
					break;
				}
			}
			break;
		case "501":
			for(GameFrame game : GameFrameList) {
				if(gm.roomnum == game.roomnumber) {
					game.state = 0;
					game.turn = 0;
					game.board.endpanel.setVisible(true);
					game.board.endLabel.setText("승리");
				}
			}
			break;
		case "502":
			for(GameFrame game : GameFrameList) {
				if(gm.roomnum == game.roomnumber) {
					game.state = 0;
					game.turn = 0;
					game.board.endpanel.setVisible(true);
					game.board.endLabel.setText("패배");
				}
			}
			break;
		case "503":
			for(GameFrame game : GameFrameList) {
				if(gm.roomnum == game.roomnumber) {
					game.state = 0;
					game.turn = 0;
					game.board.endpanel.setVisible(true);
					game.board.endLabel.setText("무승부");
				}
			}
			break;
		case "800":
			for(GameFrame game : GameFrameList) {
				if(gm.roomnum == game.roomnumber) {
					game.board.AddGameprogress(gm.x, gm.y, gm.color);
				}
			}
			break;
		}
	}
	
	
	private GameRoom getGameRoom(int roomnum) {
		for (int i=0;i<LobbyPanel.Gameroommodel.getSize();i++) {
			GameRoom gameroom = (GameRoom)LobbyPanel.Gameroommodel.getElementAt(i);
			if(roomnum == gameroom.getroomnum()) {
				return gameroom;
			}
		}
		return null;
	}
	private void enterGameRoom(ChatMsg cm) {
		GameFrame gameframe = new GameFrame(frame, cm.roomnum, cm.role);
		if(cm.role.matches("observer")) {
			gameframe.turn = 0;
			gameframe.state = 0;
			gameframe.board.endpanel.setVisible(false);
		}
		else {
			gameframe.turn = 0;
		}
		
		gameframe.repaint();
		GameFrameList.add(gameframe);
		ChatMsg chatmsg = new ChatMsg(cm.UserName, "204", "update roomlist");
		chatmsg.roomnum = cm.roomnum;
		chatmsg.roomname = cm.roomname;
		SendObject(chatmsg);
	}

	public void SendMessage(String msg) {
		try {
			ChatMsg obcm = new ChatMsg(UserName, "600", msg);
			oos.writeObject(obcm);
		} catch (IOException e) {
			try {
				ois.close();
				oos.close();
				socket.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				System.exit(0);
			}
		}
	}

	
	public void SendObject(Object ob) { // 서버로 메세지를 보내는 메소드
		try {
			oos.writeObject(ob);
		} catch (IOException e) {
			// textArea.append("메세지 송신 에러!!\n");
			//AppendText("SendObject Error");
		}
	}
}


