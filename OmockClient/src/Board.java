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
		
		//StartButton.setEnabled(false);
		
		endpanel.setVisible(false);
		
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
	
	public boolean is_ban(int x,int y) {
		if(frame.stonecolor == COLOR.BLACK) {
			int is_44_or_6 = is_44_6(x,y);
			if(is_33(x,y)) {
				frame.AppendText("[SERVICE] (3,3)금수로 둘 수 없습니다.");
				return true;
			}
			else if(is_44_or_6 == 4) {
				frame.AppendText("[SERVICE] (4,4)금수로 둘 수 없습니다.");
				return true;	
			}
			else if(is_44_or_6 == 6) {
				frame.AppendText("[SERVICE] 장목(6목이상)금수로 둘 수 없습니다.");
				return true;	
			}
		}
		return false;
	}
	public int is_44_6(int x, int y) {
		int blank_count = 0, count= 0;
		int stone_count = 0, xx = x, yy = y;
		int check_4[] = new int[3];
		int check_6 = 0;
		
		// 우측 
		while(true) {
			xx++;
			if(xx < 0 || xx >= 19 || yy < 0 || yy >= 19) {
				check_4[blank_count] = stone_count;
				break;
			}
			if(map[yy][xx] == 0)
			{
				check_4[blank_count] = stone_count;
				blank_count++;
				if(blank_count == 2) {
					blank_count--;
					break;
				}else if(xx+1 < 19 && map[yy][xx+1] == 0) {
					blank_count--;
					break;
				}
				continue;
			}
			if(map[yy][xx] == COLOR.WHITE) {
				check_4[blank_count] = stone_count;
				break;
			}
			if(blank_count == 0) check_6++;
			stone_count++;
		}
		// 좌측
		for(int j = blank_count;j>=0;j--) {
			stone_count = 0;
			yy = y;
			xx = x;
			blank_count = j;
			while(true) {
				xx--;
				if(xx < 0 || xx >= 19 || yy < 0 || yy >= 19) {
					check_4[j] += stone_count;
					break;
				}
				if(map[yy][xx] == 0)
				{
					blank_count++;
					if(blank_count == 2) {
						check_4[j] += stone_count;
						blank_count--;
						break;
					}
					continue;
				}
				if(map[yy][xx] == COLOR.WHITE) {
					check_4[j] += stone_count;
					break;
				}
				if(blank_count == 0) check_6++;
				stone_count++;
			}
			if(check_4[j] == 3) {
				count++;
				break;
			}
			if(check_6 >= 5) {
				return 6;
			}
			
		}
		
		// 위
		blank_count = 0;
		stone_count = 0;
		xx = x;
		yy = y;
		check_4[0] = check_4[1] = check_4[2] = 0;
		check_6 = 0;
		while(true) {
			yy++;
			if(xx < 0 || xx >= 19 || yy < 0 || yy >= 19) {
				check_4[blank_count] = stone_count;
				break;
			}
			if(map[yy][xx] == 0)
			{
				check_4[blank_count] = stone_count;
				blank_count++;
				if(blank_count == 2) {
					blank_count--;
					break;
				}else if(yy+1 < 19 && map[yy+1][xx] == 0) {
					blank_count--;
					break;
				}
				continue;
			}
			if(map[yy][xx] == COLOR.WHITE) {
				check_4[blank_count] = stone_count;
				break;
			}
			if(blank_count == 0) check_6++;
			stone_count++;
		}
		// 아래
		for(int j = blank_count;j>=0;j--) {
			stone_count = 0;
			yy = y;
			xx = x;
			blank_count = j;
			while(true) {
				yy--;
				if(xx < 0 || xx >= 19 || yy < 0 || yy >= 19) {
					check_4[j] += stone_count;
					break;
				}
				if(map[yy][xx] == 0)
				{
					blank_count++;
					if(blank_count == 2) {
						check_4[j] += stone_count;
						blank_count--;
						break;
					}
					continue;
				}
				if(map[yy][xx] == COLOR.WHITE) {
					check_4[j] += stone_count;
					break;
				}
				if(blank_count == 0) check_6++;
				stone_count++;
			}
			if(check_4[j] == 3) {
				count++;
				break;
			}
			if(check_6 >= 5) {
				return 6;
			}
			
		}
		
		
		// \대각 우측
		blank_count = 0;
		stone_count = 0;
		xx = x;
		yy = y;
		check_4[0] = check_4[1] = check_4[2] = 0;
		check_6 = 0;
		while(true) {
			yy++;
			xx++;
			if(xx < 0 || xx >= 19 || yy < 0 || yy >= 19) {
				check_4[blank_count] = stone_count;
				break;
			}
			if(map[yy][xx] == 0)
			{
				check_4[blank_count] = stone_count;
				blank_count++;
				if(blank_count == 2) {
					blank_count--;
					break;
				}else if(xx+1 < 19 && yy+1 < 19 && map[yy+1][xx+1] == 0) {
					blank_count--;
					break;
				}
				continue;
			}
			if(map[yy][xx] == COLOR.WHITE) {
				check_4[blank_count] = stone_count;
				break;
			}
			if(blank_count == 0) check_6++;
			stone_count++;
		}
		// \대각 좌측
		for(int j = blank_count;j>=0;j--) {
			stone_count = 0;
			yy = y;
			xx = x;
			blank_count = j;
			while(true) {
				yy--;
				xx--;
				if(xx < 0 || xx >= 19 || yy < 0 || yy >= 19) {
					check_4[j] += stone_count;
					break;
				}
				if(map[yy][xx] == 0)
				{
					blank_count++;
					if(blank_count == 2) {
						check_4[j] += stone_count;
						blank_count--;
						break;
					}
					continue;
				}
				if(map[yy][xx] == COLOR.WHITE) {
					check_4[j] += stone_count;
					break;
				}
				if(blank_count == 0) check_6++;
				stone_count++;
			}
			if(check_4[j] == 3) {
				count++;
				break;
			}
			if(check_6 >= 5) {
				return 6;
			}
			
		}
		
		
		// /대각 우측
		blank_count = 0;
		stone_count = 0;
		xx = x;
		yy = y;
		check_4[0] = check_4[1] = check_4[2] = 0;
		check_6 = 0;
		while(true) {
			yy--;
			xx++;
			if(xx < 0 || xx >= 19 || yy < 0 || yy >= 19) {
				check_4[blank_count] = stone_count;
				break;
			}
			if(map[yy][xx] == 0)
			{
				check_4[blank_count] = stone_count;
				blank_count++;
				if(blank_count == 2) {
					blank_count--;
					break;
				}else if(xx+1 < 19 && yy-1 >= 0 && map[yy-1][xx+1] == 0) {
					blank_count--;
					break;
				}
				continue;
			}
			if(map[yy][xx] == COLOR.WHITE) {
				check_4[blank_count] = stone_count;
				break;
			}
			if(blank_count == 0) check_6++;
			stone_count++;
		}
		// /대각 좌측
		for(int j = blank_count;j>=0;j--) {
			stone_count = 0;
			yy = y;
			xx = x;
			blank_count = j;
			while(true) {
				yy++;
				xx--;
				if(xx < 0 || xx >= 19 || yy < 0 || yy >= 19) {
					check_4[j] += stone_count;
					break;
				}
				if(map[yy][xx] == 0)
				{
					blank_count++;
					if(blank_count == 2) {
						check_4[j] += stone_count;
						blank_count--;
						break;
					}
					continue;
				}
				if(map[yy][xx] == COLOR.WHITE) {
					check_4[j] += stone_count;
					break;
				}
				if(blank_count == 0) check_6++;
				stone_count++;
			}
			if(check_4[j] == 3) {
				count++;
				break;
			}
		
			if(check_6 >= 5) {
				return 6;
			}
		}
		
		
		if(count>=2) return 4;
		else return 0;
		
	}
	public boolean is_33(int x, int y) {
	  /* 
	     x : 빈공간, ○ : (x,y) , ● : 놓여진 흑돌  
	     x○●●x
		 x○x●●x
		 x○●x●x
		 x●○●x
		 x●○x●x
	 */
		
	// x○●●x
	int count = 0;
	if(x-1 >= 0 && x+3 < 19 && map[y][x-1] == 0 && map[y][x+1] == COLOR.BLACK && map[y][x+2] == COLOR.BLACK && map[y][x+3] == 0) {
		count++;
	}
	if(x-3 >= 0 && x+1 < 19 && map[y][x-3] == 0 && map[y][x-2] == COLOR.BLACK && map[y][x-1] == COLOR.BLACK && map[y][x+1] == 0) {
		count++;
	}
	if(y-1 >= 0 && y+3 < 19 && map[y-1][x] == 0 && map[y+1][x] == COLOR.BLACK && map[y+2][x] == COLOR.BLACK && map[y+3][x] == 0) {
		count++;
	}
	if(y-3 >= 0 && y+1 < 19 && map[y-3][x] == 0 && map[y-2][x] == COLOR.BLACK && map[y-1][x] == COLOR.BLACK && map[y+1][x] == 0) {
		count++;
	}
	if( y-1 >= 0 && y+3 < 19 && x-1 >= 0 && x+3 < 19 && map[y-1][x-1] == 0 && map[y+1][x+1] == COLOR.BLACK && map[y+2][x+2] == COLOR.BLACK && map[y+3][x+3] == 0) {
		count++;
	}
	if( y-3 >= 0 && y+1 < 19 && x-3 >= 0 && x+1 < 19 && map[y+1][x+1] == 0 && map[y-1][x-1] == COLOR.BLACK && map[y-2][x-2] == COLOR.BLACK && map[y-3][x-3] == 0) {
		count++;
	}
	if( y-1 >= 0 && y+3 < 19 && x-3 >= 0 && x+1 < 19 && map[y-1][x+1] == 0 && map[y+1][x-1] == COLOR.BLACK && map[y+2][x-2] == COLOR.BLACK && map[y+3][x-3] == 0) {
		count++;
	}
	if( y-3 >= 0 && y+1 < 19 && x-1 >= 0 && x+3 < 19 && map[y+1][x-1] == 0 && map[y-1][x+1] == COLOR.BLACK && map[y-2][x+2] == COLOR.BLACK && map[y-3][x+3] == 0) {
		count++;
	}
	
	//x○x●●x
	if( x-1 >= 0 && x+4 < 19 && map[y][x-1] == 0 && map[y][x+1] == 0 && map[y][x+2] == COLOR.BLACK && map[y][x+3] == COLOR.BLACK && map[y][x+4] == 0) {
		count++;
	}
	if( x-4 >= 0 && x+1 < 19 && map[y][x+1] == 0 && map[y][x-1] == 0 && map[y][x-2] == COLOR.BLACK && map[y][x-3] == COLOR.BLACK && map[y][x-4] == 0) {
		count++;
	}
	if( y-1 >= 0 && y+4 < 19 && map[y-1][x] == 0 && map[y+1][x] == 0 && map[y+2][x] == COLOR.BLACK && map[y+3][x] == COLOR.BLACK && map[y+4][x] == 0) {
		count++;
	}
	if( y-4 >= 0 && y+1 < 19 && map[y+1][x] == 0 && map[y-1][x] == 0 && map[y-2][x] == COLOR.BLACK && map[y-3][x] == COLOR.BLACK && map[y-4][x] == 0) {
		count++;
	}
	if( y-1 >= 0 && y+4 < 19 && x-1 >= 0 && x+4 < 19 && map[y-1][x-1] == 0 && map[y+1][x+1] == 0 && map[y+2][x+2] == COLOR.BLACK && map[y+3][x+3] == COLOR.BLACK && map[y+4][x+4] == 0) {
		count++;
	}
	if( y-4 >= 0 && y+1 < 19 && x-4 >= 0 && x+1 < 19 && map[y+1][x+1] == 0 && map[y-1][x-1] == 0 && map[y-2][x-2] == COLOR.BLACK && map[y-3][x-3] == COLOR.BLACK && map[y-4][x-4] == 0) {
		count++;
	}
	if( y-1 >= 0 && y+4 < 19 && x-4 >= 0 && x+1 < 19 && map[y-1][x+1] == 0 && map[y+1][x-1] == 0 && map[y+2][x-2] == COLOR.BLACK && map[y+3][x-3] == COLOR.BLACK && map[y+4][x-4] == 0) {
		count++;
	}
	if( y-4 >= 0 && y+1 < 19 && x-1 >= 0 && x+4 < 19 && map[y+1][x-1] == 0 && map[y-1][x+1] == 0 && map[y-2][x+2] == COLOR.BLACK && map[y-3][x+3] == COLOR.BLACK && map[y-4][x+4] == 0) {
		count++;
	}
	
	//x○●x●x
	if( x-1 >= 0 && x+4 < 19 && map[y][x-1] == 0 && map[y][x+1] == COLOR.BLACK && map[y][x+2] == 0 && map[y][x+3] == COLOR.BLACK && map[y][x+4] == 0) {
		count++;
	}
	if( x-4 >= 0 && x+1 < 19 && map[y][x+1] == 0 && map[y][x-1] == COLOR.BLACK && map[y][x-2] == 0 && map[y][x-3] == COLOR.BLACK && map[y][x-4] == 0) {
		count++;
	}
	if( y-1 >= 0 && y+4 < 19 && map[y-1][x] == 0 && map[y+1][x] == COLOR.BLACK && map[y+2][x] == 0 && map[y+3][x] == COLOR.BLACK && map[y+4][x] == 0) {
		count++;
	}
	if( y-4 >= 0 && y+1 < 19 && map[y+1][x] == 0 && map[y-1][x] == COLOR.BLACK && map[y-2][x] == 0 && map[y-3][x] == COLOR.BLACK && map[y-4][x] == 0) {
		count++;
	}
	if( y-1 >= 0 && y+4 < 19 && x-1 >= 0 && x+4 < 19 && map[y-1][x-1] == 0 && map[y+1][x+1] == COLOR.BLACK && map[y+2][x+2] == 0 && map[y+3][x+3] == COLOR.BLACK && map[y+4][x+4] == 0) {
		count++;
	}
	if( y-4 >= 0 && y+1 < 19 && x-4 >= 0 && x+1 < 19 && map[y+1][x+1] == 0 && map[y-1][x-1] == COLOR.BLACK && map[y-2][x-2] == 0 && map[y-3][x-3] == COLOR.BLACK && map[y-4][x-4] == 0) {
		count++;
	}
	if( y-1 >= 0 && y+4 < 19 && x-4 >= 0 && x+1 < 19 && map[y-1][x+1] == 0 && map[y+1][x-1] == COLOR.BLACK && map[y+2][x-2] == 0 && map[y+3][x-3] == COLOR.BLACK && map[y+4][x-4] == 0) {
		count++;
	}
	if( y-4 >= 0 && y+1 < 19 && x-1 >= 0 && x+4 < 19 && map[y+1][x-1] == 0 && map[y-1][x+1] == COLOR.BLACK && map[y-2][x+2] == 0 && map[y-3][x+3] == COLOR.BLACK && map[y-4][x+4] == 0) {
		count++;
	}
	
	// x●○●x
	if( x-2 >= 0 && x+2 < 19 && map[y][x-2] == 0 && map[y][x-1] == COLOR.BLACK && map[y][x+1] == COLOR.BLACK && map[y][x+2] == 0) {
		count++;
		if(x-3 >=0 && map[y][x-3] == COLOR.BLACK) count--;
		else if(x+3 <19 && map[y][x+3] == COLOR.BLACK) count--;
	}
	if( y-2 >= 0 && y+2 < 19 && map[y-2][x] == 0 && map[y-1][x] == COLOR.BLACK && map[y+1][x] == COLOR.BLACK && map[y+2][x] == 0) {
		count++;
		if(y-3 >=0 && map[y-3][x] == COLOR.BLACK) count--;
		else if(y+3 <19 && map[y+3][x] == COLOR.BLACK) count--;
	}
	if( y-2 >= 0 && y+2 < 19 && x-2 >= 0 && x+2 < 19 && map[y-2][x-2] == 0 && map[y-1][x-1] == COLOR.BLACK && map[y+1][x+1] == COLOR.BLACK && map[y+2][x+2] == 0) {
		count++;
		if(x+3 <19 && y+3 < 19 && map[y+3][x+3] == COLOR.BLACK) count--;
		else if(x-3 >=0 && y-3 >=0 && map[y-3][x-3] == COLOR.BLACK) count--;
	}
	if( y-2 >= 0 && y+2 < 19 && x-2 >= 0 && x+2 < 19 && map[y-2][x+2] == 0 && map[y-1][x+1] == COLOR.BLACK && map[y+1][x-1] == COLOR.BLACK && map[y+2][x-2] == 0) {
		count++;
		if(x-3 >=0 && y+3 < 19 && map[y+3][x-3] == COLOR.BLACK) count--;
		else if(y-3 >=0 && x+3 < 19 && map[y-3][x+3] == COLOR.BLACK) count--;
	}
	
	// x●○x●x
	if( x-2 >= 0 && x+3 < 19 && map[y][x-2] == 0 && map[y][x-1] == COLOR.BLACK && map[y][x+1] == 0 && map[y][x+2] == COLOR.BLACK && map[y][x+3] == 0) {
		count++;
	}
	if( x-3 >= 0 && x+2 < 19 && map[y][x+2] == 0 && map[y][x+1] == COLOR.BLACK && map[y][x-1] == 0 && map[y][x-2] == COLOR.BLACK && map[y][x-3] == 0) {
		count++;
	}
	if( y-2 >= 0 && y+3 < 19 && map[y-2][x] == 0 && map[y-1][x] == COLOR.BLACK && map[y+1][x] == 0 && map[y+2][x] == COLOR.BLACK && map[y+3][x] == 0) {
		count++;
	}
	if(  y-3 >= 0 && y+2 < 19 && map[y+2][x] == 0 && map[y+1][x] == COLOR.BLACK && map[y-1][x] == 0 && map[y-2][x] == COLOR.BLACK && map[y-3][x] == 0) {
		count++;
	}
	if( y-2 >= 0 && y+3 < 19 && x-2 >= 0 && x+3 < 19 && map[y-2][x-2] == 0 && map[y-1][x-1] == COLOR.BLACK && map[y+1][x+1] == 0 && map[y+2][x+2] == COLOR.BLACK && map[y+3][x+3] == 0) {
		count++;
	}
	if( y-3 >= 0 && y+2 < 19 && x-3 >= 0 && x+2 < 19 && map[y+2][x+2] == 0 && map[y+1][x+1] == COLOR.BLACK && map[y-1][x-1] == 0 && map[y-2][x-2] == COLOR.BLACK && map[y-3][x-3] == 0) {
		count++;
	}
	if( y-2 >= 0 && y+3 < 19 && x-3 >= 0 && x+2 < 19 && map[y-2][x+2] == 0 && map[y-1][x+1] == COLOR.BLACK && map[y+1][x-1] == 0 && map[y+2][x-2] == COLOR.BLACK && map[y+3][x-3] == 0) {
		count++;
	}
	if( y-3 >= 0 && y+2 < 19 && x-2 >= 0 && x+3 < 19 && map[y+2][x-2] == 0 && map[y+1][x-1] == COLOR.BLACK && map[y-1][x+1] == 0 && map[y-2][x+2] == COLOR.BLACK && map[y-3][x+3] == 0) {
		count++;
	}
	
	if(count >= 2) {
		return true;
	}
	else {
		return false;
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
