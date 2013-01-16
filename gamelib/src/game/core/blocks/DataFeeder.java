package game.core.blocks;

import game.core.Block;
import game.core.Data;
import game.core.ElementTemplate;

import com.ios.errorchecks.SizeCheck;
import com.ios.triggers.BoundProperties;
import com.ios.triggers.MasterSlaveTrigger;

public class DataFeeder extends Block {
	
	public DataFeeder() {
		this(true);
	}
	
	public DataFeeder(boolean source) {
		super();
		addTrigger(new BoundProperties(this, "parents"));
		if (source)
			addTrigger(new MasterSlaveTrigger(this, "datasetTemplate.sourceTemplate", "outputTemplate"));
		else
			addTrigger(new MasterSlaveTrigger(this, "datasetTemplate.targetTemplate", "outputTemplate"));
		
		addErrorCheck("parents", new SizeCheck(0, 0));
	}

	@Override
	public Data transform(Data input) {
		return input;
	}

	@Override
	public boolean supportsInputTemplate(ElementTemplate inputTemplate) {
		return false; // NO PARENTS
	}

	@Override
	protected void setup() {
		// nothing to do
	}

	@Override
	public boolean acceptsParents() {
		return false;
	}

}
