package game.plugins.valuetemplates;

import game.core.ValueTemplate;

import java.util.ArrayList;
import java.util.List;

import com.ios.errorchecks.PositivenessCheck;

public class StringListTemplate extends ValueTemplate<List<String>> {
	
	@InName
	public int dimension = 0;
	
	public StringListTemplate() {
		addErrorCheck(new PositivenessCheck("dimension", false));
	}

	@Override
	public int getDescriptionLength() {
		return dimension;
	}

	@Override
	public List<String> loadValue(List<String> description) {
		return new ArrayList<String>(description);
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof StringListTemplate) {
			return this.dimension == ((StringListTemplate)other).dimension;
		} else {
			return false;
		}
	}

}
