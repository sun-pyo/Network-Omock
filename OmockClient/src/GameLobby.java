import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import java.awt.Color;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JList;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
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
import java.awt.SystemColor;

public class GameLobby extends JPanel {
	
	public JButton btnNewButton;
	public DefaultListModel Gameroommodel, Usermodel;
	private MainFrame frame;
	public JList Gameroomlist;
	public GameLobby(MainFrame frame) {
		this.frame = frame;
		setBounds(100, 100, 871, 477);
		setBorder(new EmptyBorder(5, 5, 5, 5));
		setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(22, 74, 613, 383);
		add(scrollPane);
		
		Gameroomlist = new JList(new DefaultListModel());
		
		Gameroomlist.setBackground(new Color(255, 255, 255));
		Gameroomlist.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
		scrollPane.setViewportView(Gameroomlist);
		Gameroomlist.addMouseListener(new MyMouseEvent());
		Gameroommodel = (DefaultListModel) Gameroomlist.getModel();
		
		
		btnNewButton = new JButton("방 만들기");
		btnNewButton.setBackground(new Color(204, 255, 153));
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CreateGameRoom create = new CreateGameRoom();
				create.btnNewButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {		
						create.dispose();
						ChatMsg obcm = new ChatMsg(frame.UserName, "200", Boolean.toString(create.rdbtnNewRadioButton_1.isSelected()));
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
		
		JList Userlist = new JList(new DefaultListModel());
		Userlist.setBackground(new Color(255, 255, 255));
		scrollPane_1.setViewportView(Userlist);
		Usermodel = (DefaultListModel) Userlist.getModel();
		
		JPanel panel = new JPanel();
		panel.setBackground(new Color(204, 255, 153));
		panel.setBounds(22, 50, 613, 23);
		add(panel);
		setVisible(true);
		panel.setLayout(null);
		
		JLabel lblNewLabel = new JLabel(String.format("%-40s %-40s %-20s", "방 번호" , "방 제목", "방 상태"));
		lblNewLabel.setBackground(new Color(184, 134, 11));
		lblNewLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));
		lblNewLabel.setBounds(5, 0, 576, 20);
		panel.add(lblNewLabel);
		
		
		
	}
	public void paintComponent(Graphics g) {
        ImageIcon backgroundimage = new ImageIcon("src/background5.png");
		g.drawImage(backgroundimage.getImage(), 0, 0, this.getWidth(), this.getHeight(), this);
        setOpaque(false); //그림을 표시하게 설정,투명하게 조절
        super.paintComponent(g);
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
