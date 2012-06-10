package game.configuration.errorchecks;

import game.configuration.ErrorCheck;

import java.util.List;

public class NoNullElementsCheck implements ErrorCheck<List> {

	@Override
	public String getError(List value) {
		if (value.contains(null))
			return "this list cannot contain null elements";
		else
			return null;
	}

}
