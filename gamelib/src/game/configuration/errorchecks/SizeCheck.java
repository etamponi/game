package game.configuration.errorchecks;

import game.configuration.ErrorCheck;

import java.util.List;

public class SizeCheck implements ErrorCheck<List> {
	
	private int minimumSize, maximumSize;

	public SizeCheck(int minimumSize) {
		this.minimumSize = minimumSize;
		this.maximumSize = Integer.MAX_VALUE;
	}
	
	public SizeCheck(int minimumSize, int maximumSize) {
		this.minimumSize = minimumSize;
		this.maximumSize = maximumSize;
	}

	@Override
	public String getError(List value) {
		if (value.size() < minimumSize)
			return "should have at least " + minimumSize + " elements";
		else if (value.size() > maximumSize)
			return "should have no more than " + maximumSize + " elements";
		else
			return null;
	}

}
