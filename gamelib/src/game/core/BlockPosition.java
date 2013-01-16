package game.core;

import com.ios.IObject;

public class BlockPosition extends IObject {
	public int x = -1;
	public int y = -1;
	
	public boolean isValid() {
		return x >= 0 && y >= 0;
	}
}