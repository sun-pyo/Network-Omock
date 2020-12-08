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

	private static final int BUF_LEN = 128; // Windows Ã³·³ BUF_LEN À» Á¤ÀÇ
	private Socket socket; // ¿¬°á¼ÒÄÏ
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
		setTitle(UserName + "´ÔÀÇ MainFrame");
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

	// Server Message¸¦ ¼ö½ÅÇØ¼­ È­¸é¿¡ Ç¥½Ã
	class ListenNetwork extends Thread{
		public void run() {
			while (true) {
				try {
					Object obcm = null;
					String msg = null;
					ChatMsg cm;
					InitMsg im;
					GameMsg gm;
					TimeMsg tm;
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
					else if(obcm instanceof TimeMsg) {
						tm = (TimeMsg)obcm;
						if(tm.code.matches("700")) {
							for(GameFrame game : GameFrameList) {
								if(tm.roomnum == game.roomnumber) {
									game.player1_time.setForeground (Color.BLACK);
									game.player2_time.setForeground (Color.BLACK);
									game.player1_time.setFont(new Font("³ª´®°íµñ ExtraBold", Font.PLAIN, 10));
									game.player2_time.setFont(new Font("³ª´®°íµñ ExtraBold", Font.PLAIN, 10));
									if(tm.player.matches("player1")) {
										if(game.role.matches("player1")) {
											game.player1_time.setForeground (Color.RED);
											game.player1_time.setFont(new Font("³ª´®°íµñ ExtraBold", Font.BOLD, 20));
										}
										game.player1_time.setText(tm.time); 
										game.player2_time.setText(game.time);
									}
									else if(tm.player.matches("player2")) {
										if(game.role.matches("player2")) {
											game.player2_time.setForeground (Color.RED);
											game.player2_time.setFont(new Font("³ª´®°íµñ ExtraBold", Font.BOLD, 20));
										}
										game.player2_time.setText(tm.time); 
										game.player1_time.setText(game.time);
									}
								}
							}
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
					} // catch¹® ³¡
				} // ¹Ù±ù catch¹®³¡

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
					}
					
					if(cm.data.matches("enter")) {
						if(!UserName.matches(cm.UserName))
							game.AppendText("[SERVICE] " + cm.UserName + "´ÔÀÌ ÀÔÀåÇÏ¼Ì½À´Ï´Ù.");
						else
							game.AppendText("[SERVICE] " + cm.roomnum +"¹ø ¹æ¿¡ ÀÔÀåÇÏ¼Ì½À´Ï´Ù.");
					}
					else if(cm.data.matches("exit"))
						game.AppendText("[SERVICE] " + cm.UserName + "´ÔÀÌ ÅðÀåÇÏ¼Ì½À´Ï´Ù.");
					
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
			if(cm.roomstate.matches("none")) break;
			LobbyPanel.Gameroommodel.addElement(new GameRoom(cm.roomnum, cm.roomstate, cm.roomname));
			break;
		case "205":
			for(GameFrame game : GameFrameList) {
				if(cm.roomnum == game.roomnumber) {
					game.dispose();
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
					game.dispose();
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
				if(gm.roomnum == game.roomnumber) {
					game.board.ResetBoard();
					game.board.endpanel.setVisible(false);
					game.board.startpanel.setVisible(false);
					game.state = 1;
				}
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
		case "302":  // Âø¼ö
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
			System.out.println("gameroom : " + gm.roomnum);
			for(GameFrame game : GameFrameList) {
				System.out.println(game.roomnumber + "   " + gm.roomnum);
				if(gm.roomnum == game.roomnumber) {
					game.state = 0;
					game.turn = 0;
					game.board.movex = -1;
					game.board.movey = -1;
					game.board.endpanel.setVisible(true);
					game.board.endLabel.setText("½Â¸®");
				}
			}
			break;
		case "502":
			for(GameFrame game : GameFrameList) {
				if(gm.roomnum == game.roomnumber) {
					game.state = 0;
					game.turn = 0;
					game.board.movex = -1;
					game.board.movey = -1;
					game.board.endpanel.setVisible(true);
					game.board.endLabel.setText("ÆÐ¹è");
				}
			}
			break;
		case "503":
			for(GameFrame game : GameFrameList) {
				if(gm.roomnum == game.roomnumber) {
					game.state = 0;
					game.turn = 0;
					game.board.movex = -1;
					game.board.movey = -1;
					game.board.endpanel.setVisible(true);
					game.board.endLabel.setText("¹«½ÂºÎ");
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
		GameFrame gameframe = new GameFrame(frame, cm.roomnum, cm.role, cm.data);
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

	
	public void SendObject(Object ob) { // ¼­¹ö·Î ¸Þ¼¼Áö¸¦ º¸³»´Â ¸Þ¼Òµå
		try {
			oos.writeObject(ob);
		} catch (IOException e) {
			// textArea.append("¸Þ¼¼Áö ¼Û½Å ¿¡·¯!!\n");
			//AppendText("SendObject Error");
		}
	}
}


