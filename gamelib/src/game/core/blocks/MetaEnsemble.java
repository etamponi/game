package game.core.blocks;

import com.ios.triggers.MasterSlaveTrigger;

import game.core.DataTemplate.Data;
import game.core.Encoding;
import game.core.InstanceTemplate;

public class MetaEnsemble extends Transducer {
	
	public Combiner combiner;
	
	public MetaEnsemble() {
		addTrigger(new MasterSlaveTrigger(this, "outputEncoder", "combiner.outputEncoder"));
		addTrigger(new MasterSlaveTrigger(this, "template", "combiner.template"));
	}

	@Override
	public boolean isCompatible(InstanceTemplate object) {
		if (combiner != null)
			return combiner.isCompatible(object);
		else
			return true;
	}

	@Override
	public Encoding transform(Data input) {
		return combiner.transform(input);
	}

	@Override
	public FeatureType getFeatureType(int featureIndex) {
		return combiner != null ? combiner.getFeatureType(featureIndex) : FeatureType.NUMERIC;
	}

}
