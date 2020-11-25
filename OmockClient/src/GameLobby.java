import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import java.awt.Color;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JList;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;

public class GameLobby extends JPanel {
	
	public JButton btnNewButton;
	public DefaultListModel Gameroommodel;
	private MainFrame frame;
	public JList Gameroomlist;
	
	public GameLobby(MainFrame frame) {
		this.frame = frame;
		setBounds(100, 100, 885, 488);
		setBorder(new EmptyBorder(5, 5, 5, 5));
		setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(22, 74, 613, 383);
		add(scrollPane);
		
		Gameroomlist = new JList(new DefaultListModel());
		Gameroomlist.setFont(new Font("¸¼Àº °íµñ", Font.PLAIN, 14));
		scrollPane.setViewportView(Gameroomlist);
		Gameroomlist.addMouseListener(new MyMouseEvent());
		Gameroommodel = (DefaultListModel) Gameroomlist.getModel();
		
		
		btnNewButton = new JButton("¹æ ¸¸µé±â");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CreateGameRoom create = new CreateGameRoom();
				create.btnNewButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {						
						create.dispose();
						ChatMsg obcm = new ChatMsg(frame.UserName, "200", "create Room");
						obcm.roomname = create.textField.getText();
						frame.SendObject(obcm);
					}
				});
			}
		});
		btnNewButton.setBounds(658, 25, 188, 32);
		add(btnNewButton);
		
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(658, 74, 188, 383);
		add(scrollPane_1);
		
		JList Userlist = new JList();
		scrollPane_1.setViewportView(Userlist);
		
		JLabel lblNewLabel = new JLabel(String.format("%-40s %-40s %-20s", "¹æ ¹øÈ£" , "¹æ Á¦¸ñ", "¹æ »óÅÂ"));
		lblNewLabel.setFont(new Font("¸¼Àº °íµñ", Font.BOLD, 14));
		lblNewLabel.setBounds(22, 38, 613, 45);
		add(lblNewLabel);
		setVisible(true);
		
	}
	class MyMouseEvent implements MouseListener{

		@Override
		public void mouseClicked(MouseEvent e) {
			if(e.getClickCount() == 2) {
				GameRoom gameroom= (GameRoom) Gameroomlist.getSelectedValue();
				ChatMsg obcm = new ChatMsg(frame.UserName, "201", "enter Room");
				obcm.roomnum = gameroom.getroomnum();
				obcm.roomname = gameroom.getroomname();
				frame.SendObject(obcm);
			}
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
			// TODO Auto-generated method stub
		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			// TODO Auto-generated method stub
		}

		@Override
		public void mousePressed(MouseEvent arg0) {
			// TODO Auto-generated method stub
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			// TODO Auto-generated method stub		
		}
		
	}
	
}
