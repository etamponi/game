package game.configuration.errorchecks;

import game.configuration.ErrorCheck;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NoRepetitionCheck implements ErrorCheck<List> {

	@Override
	public String getError(List value) {
		Set set = new HashSet<>(value);
		if (set.size() < value.size())
			return "cannot have repetitions";
		else
			return null;
	}

}
