package game.core.blocks;

import game.configuration.listeners.PropertyBinding;
import game.core.DataTemplate.Data;
import game.core.Encoding;
import game.core.InstanceTemplate;

public class MetaEnsemble extends Transducer {
	
	public Combiner combiner;
	
	public MetaEnsemble() {
		addListener(new PropertyBinding(this, "outputEncoder", "combiner.outputEncoder"));
		addListener(new PropertyBinding(this, "template", "combiner.template"));
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
