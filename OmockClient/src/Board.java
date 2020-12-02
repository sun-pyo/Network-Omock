import javax.swing.ImageIcon;
import javax.swing.JPanel;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.SystemColor;
import javax.swing.SwingConstants;

public class Board extends JPanel {
	public JPanel startpanel, endpanel;
	private int initX, initY;
	public int movex = -1, movey = -1;
	private Image black, white,image;
	private ArrayList<Stone> Gameprogress = new ArrayList<Stone>();
	private Graphics gc ,gc2 = null;
	public int[][] map = new int[19][19];
	private GameFrame frame;
	public JPanel panel;
	private JLabel lblNewLabel;
	private JButton yesButton;
	private JButton noButton;
	private JButton exitButton, StartButton;
	private JPanel panel_1;
	public JLabel endLabel;
	private JButton btnNewButton;
	
	public Board(GameFrame frame) {
		this.frame = frame;
		
		this.initX = 17;
		this.initY = 17;
		
		this.black = new ImageIcon("src/black.png").getImage();
		this.white = new ImageIcon("src/white.png").getImage();
		this.image = new ImageIcon("src/board.png").getImage();
		setLayout(null);
		
		endpanel = new JPanel();
		endpanel.setBounds(137, 195, 287, 152);
		add(endpanel);
		endpanel.setLayout(null);
		
		endLabel = new JLabel("New label");
		endLabel.setFont(new Font("휴먼엑스포", Font.BOLD, 20));
		endLabel.setHorizontalAlignment(SwingConstants.CENTER);
		endLabel.setBounds(69, 23, 156, 49);
		endpanel.add(endLabel);
		
		btnNewButton = new JButton("확인");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				endpanel.setVisible(false);
				startpanel.setVisible(true);
				frame.state = 0;
				StartButton.setText("다시하기");
			}
		});
		btnNewButton.setBounds(101, 97, 91, 23);
		endpanel.add(btnNewButton);
		
		startpanel = new JPanel();
		startpanel.setBackground(new Color(218, 165, 32));
		startpanel.setBounds(137, 195, 287, 152);
		add(startpanel);
		startpanel.setLayout(null);
		
		StartButton = new JButton("게임 시작");
		StartButton.setBackground(new Color(240, 240, 240));
		StartButton.setBounds(79, 10, 131, 50);
		StartButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {		
				GameMsg gm = new GameMsg(frame.frame.UserName,"300",frame.roomnumber, 0, 0, frame.stonecolor);
				frame.frame.SendObject(gm);
			}
		});
		startpanel.add(StartButton);
		
		exitButton = new JButton("나가기");
		exitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ChatMsg msg = new ChatMsg(frame.frame.UserName, "205", "방 퇴장");
				msg.roomnum = frame.roomnumber;
				frame.frame.SendObject(msg);
				frame.dispose();
			}
		});
		exitButton.setBackground(SystemColor.menu);
		exitButton.setBounds(79, 79, 131, 50);
		startpanel.add(exitButton);
		
		panel = new JPanel();
		panel.setBounds(137, 195, 287, 152);
		add(panel);
		panel.setLayout(null);
		
		lblNewLabel = new JLabel("무승부 신청");
		lblNewLabel.setFont(new Font("한컴 고딕", Font.BOLD, 16));
		lblNewLabel.setBounds(104, 10, 98, 28);
		panel.add(lblNewLabel);
		
		yesButton = new JButton("승인");
		yesButton.setBounds(48, 87, 71, 23);
		yesButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {		
				GameMsg gm = new GameMsg(frame.frame.UserName,"401",frame.roomnumber, 0, 0, frame.stonecolor);
				frame.frame.SendObject(gm);
				panel.setVisible(false);
				
			}
		});
		panel.add(yesButton);
		
		noButton = new JButton("거절");
		noButton.setBounds(160, 87, 71, 23);
		noButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {		
				GameMsg gm = new GameMsg(frame.frame.UserName,"402",frame.roomnumber, 0, 0, frame.stonecolor);
				frame.frame.SendObject(gm);
				panel.setVisible(false);
				frame.state = 1;
			}
		});
		panel.add(noButton);
		panel.setVisible(false);
		startpanel.setVisible(true);
		
		//StartButton.setEnabled(false);
		
		endpanel.setVisible(false);
		
	}
	
	public void ResetBoard() {
		Gameprogress.clear();
		for(int a[]:map) {
			Arrays.fill(a, 0);
		}
		repaint();
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponents(g);
		Dimension d = getSize();
		g.drawImage(image, 0, 0, d.width,d.height, this);
		DrawBoard(g);
		if(check(movex, movey)) {
			Stone s = new Stone(movex, movey, frame.stonecolor);
			s.setopacity(0.5f);
			DrawStone(s, g);
		}
		
		//System.out.println("paintComponents!!!");
	}
	
	public void DrawBoard(Graphics g) {
		for(Stone s : Gameprogress) { 
			DrawStone(s, g);			
			
		}
	}
	
	public void DrawStone(Stone s, Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		float opacity = s.getopacity();
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
		if(s.getcolor() == COLOR.BLACK)
			g.drawImage(black, initX+(28*s.getx())-s.getStonesize(), initY+(28*s.gety())-s.getStonesize(), this);
		else if(s.getcolor() == COLOR.WHITE)
			g.drawImage(white, initX+(28*s.getx())-s.getStonesize(), initY+(28*s.gety())-s.getStonesize(), this);
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
	}

	public boolean check(int x, int y) {
		if(x>=0 && x<19 && y<19 && y>=0) {
			if(map[y][x] == 0) 
				return true;
			else
				return false;
		}
		return false;
	}
	
	public void AddGameprogress(int x, int y, int color) {
		if(check(x,y)) {
			Stone s = new Stone(x,y,color);
			Gameprogress.add(s);
			map[y][x] = color;
			repaint();
		}
	}

	
	public void paint(Graphics g) {
		super.paint(g);;
		//System.out.println("paint 메소드 호출");
	}
	public void repaint() {
		super.repaint();
		//System.out.println("repaint 메소드 호출");
	}
	public void update(Graphics g) {
		super.update(g);
		//System.out.println("update() 메소드 호출");
	}
}
