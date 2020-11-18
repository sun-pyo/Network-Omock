import javax.swing.ImageIcon;
import javax.swing.JPanel;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.util.ArrayList;

public class Board extends JPanel {

	/**
	 * Create the panel.
	 */
	private int initX, initY;
	private Image black, white, panelImage = null;
	private ArrayList<Stone> Gameprogress = new ArrayList<Stone>();
	private Graphics gc2 = null;
	
	public Board() {
		this.initX = 15;
		this.initY = 15;

		this.black = new ImageIcon("src/black.png").getImage();
		this.white = new ImageIcon("src/white.png").getImage();
	}
	
	public void ResetBoard() {
		Graphics g = this.getGraphics();
		Dimension d = getSize();
		ImageIcon imageicon = new ImageIcon("src/board.png");
		Image image = imageicon.getImage();
		g.drawImage(image, 0, 0, d.width,d.height, this);	
	}
	
	public void DrawBoard() {
		ResetBoard();	
		for(int i =0;i<19;i++)
			for(int j=0;j<19;j++)
		{
			{
				if(i%2==0 && j%2 == 0)
					Gameprogress.add(new Stone(i,j,0));
				else
					Gameprogress.add(new Stone(i,j,1));
			}
		}
		for(Stone s : Gameprogress) {
			if(s.getcolor() == 0) { 
				DrawBlack(s);			
			}
			else if(s.getcolor() == 1) {  
				DrawWhite(s);
			}
		}
	}
	
	public void DrawBlack(Stone s) {
		Graphics g = this.getGraphics();
		Graphics2D g2 = (Graphics2D) g;
		float opacity = s.getopacity();
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
		g.drawImage(black, initX+(25*s.getx())-s.getStonesize(), initY+(25*s.gety())-s.getStonesize(), this);
	}

	public void DrawWhite(Stone s) {
		Graphics g = this.getGraphics();
		Graphics2D g2 = (Graphics2D) g;
		float opacity = s.getopacity();
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
		g.drawImage(white, initX+(25*s.getx())-s.getStonesize(), initY+(25*s.gety())-s.getStonesize(), this);
	}
	
	
	public void AddGameprogress(int x, int y, int color) {
		int X = (int)Math.round(x/25);
		int Y = (int)Math.round(y/25);
		Stone s = new Stone(X,Y,color);
		Gameprogress.add(s);					
	}
	
	public void paint(Graphics g) {
		System.out.println("paint 메소드 호출");
	}
	public void repaint() {
		System.out.println("repaint 메소드 호출");
		super.repaint();
	}
	public void update(Graphics g) {
		System.out.println("update() 메소드 호출");
		super.update(g);
	}
	
}
