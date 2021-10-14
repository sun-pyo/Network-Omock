
public class Stone {
	private int x,y, Stonesize;
	private float opacity;
	private int color;
	public Stone(int x, int y, int color ) {
		this.x = x;
		this.y = y;
		this.color = color;   // black = 0 , white = 1
		this.Stonesize = 10;
		this.opacity = 1f;
	}
	
	public int getx() {
		return this.x;
	}
	
	public int gety() {
		return this.y;
	}
	
	public int getStonesize() {
		return this.Stonesize;
	}
	
	public int getcolor() {
		return this.color;
	}
	
	public float getopacity() {
		return this.opacity;
	}
	
	public void setopacity(float num) {
		this.opacity = num;
	}
	
}
