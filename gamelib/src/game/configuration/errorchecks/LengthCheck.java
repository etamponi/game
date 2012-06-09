package game.configuration.errorchecks;

import game.configuration.ErrorCheck;

public class LengthCheck implements ErrorCheck<String> {
	
	private int minimumLength;
	
	public LengthCheck(int minimumLength) {
		this.minimumLength = minimumLength;
	}

	@Override
	public String getError(String value) {
		if (value.length() < minimumLength)
			return "must have at least " + minimumLength + " characters";
		else
			return null;
	}

}
