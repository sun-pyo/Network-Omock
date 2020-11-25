import javax.swing.ImageIcon;
import javax.swing.JPanel;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.util.ArrayList;
import javax.swing.JButton;

public class Board extends JPanel {

	/**
	 * Create the panel.
	 */
	private int initX, initY;
	public int movex = -1, movey = -1;
	private Image black, white,image;
	private ArrayList<Stone> Gameprogress = new ArrayList<Stone>();
	private Graphics gc ,gc2 = null;
	private int[][] map = new int[19][19];
	private GameFrame frame;
	public Board(GameFrame frame) {
		this.frame = frame;
		
		this.initX = 15;
		this.initY = 15;
		
		this.black = new ImageIcon("src/black.png").getImage();
		this.white = new ImageIcon("src/white.png").getImage();
		this.image = new ImageIcon("src/board.png").getImage();
		
	}
	
	public void ResetBoard() {
		Gameprogress.clear();
		repaint();
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponents(g);
		Dimension d = getSize();
		g.drawImage(image, 0, 0, d.width,d.height, this);
		DrawBoard(g);
		if(check(movex, movey)) {
			Stone s =new Stone(movex, movey, frame.stonecolor);
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
			g.drawImage(black, initX+(25*s.getx())-s.getStonesize(), initY+(25*s.gety())-s.getStonesize(), this);
		else if(s.getcolor() == COLOR.WHITE)

			g.drawImage(white, initX+(25*s.getx())-s.getStonesize(), initY+(25*s.gety())-s.getStonesize(), this);
	}

	public void DrawWhite(Stone s, Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		float opacity = s.getopacity();
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
		g.drawImage(white, initX+(25*s.getx())-s.getStonesize(), initY+(25*s.gety())-s.getStonesize(), this);
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
			map[y][x] = 1;
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
