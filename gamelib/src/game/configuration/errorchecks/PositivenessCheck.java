package game.configuration.errorchecks;

import game.configuration.ErrorCheck;

public class PositivenessCheck implements ErrorCheck<Number> {
	
	private boolean zeroAccepted;
	
	public PositivenessCheck(boolean zeroAccepted) {
		this.zeroAccepted = zeroAccepted;
	}

	@Override
	public String getError(Number value) {
		if (value.doubleValue() < 0 || (!zeroAccepted && value.doubleValue() == 0))
			return "should be positive" + (zeroAccepted ? " or zero" : "");
		else
			return null;
	}

}
