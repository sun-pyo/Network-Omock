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
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;

public class GameLobby extends JPanel {
	
	public JButton btnNewButton;
	
	public GameLobby() {
		
		setBounds(100, 100, 885, 488);
		setBorder(new EmptyBorder(5, 5, 5, 5));
		setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(22, 74, 613, 383);
		add(scrollPane);
		
		JList Gameroomlist = new JList(new DefaultListModel());
		Gameroomlist.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
		scrollPane.setViewportView(Gameroomlist);
		DefaultListModel Gameroommodel = (DefaultListModel) Gameroomlist.getModel();
		Gameroommodel.addElement(new GameRoom(1,"진행중","hi"));
		
		
		btnNewButton = new JButton("방 만들기");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		btnNewButton.setBounds(658, 25, 188, 32);
		add(btnNewButton);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(658, 74, 188, 383);
		add(scrollPane_1);
		
		JList Userlist = new JList();
		scrollPane_1.setViewportView(Userlist);
		
		JLabel lblNewLabel = new JLabel(String.format("%-40s %-40s %-20s", "방 번호" , "방 제목", "방 상태"));
		lblNewLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));
		lblNewLabel.setBounds(22, 38, 613, 45);
		add(lblNewLabel);
		setVisible(true);
		
	}
}
