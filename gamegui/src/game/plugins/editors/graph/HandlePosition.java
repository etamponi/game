package game.plugins.editors.graph;

import java.io.Serializable;

public class HandlePosition implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public double x, y;
	
	public HandlePosition() {
		
	}
	
	public HandlePosition(double x, double y) {
		this.x = x;
		this.y = y;
	}
}
