package game.core.nodes;

import game.configuration.errorchecks.SizeCheck;
import game.core.Encoding;
import game.core.InstanceTemplate;
import game.core.Node;

public abstract class Classifier extends Node {
	
	public InstanceTemplate instanceTemplate;
	
	public Encoder outputEncoder;
	
	public Classifier() {
		addOptionChecks("parents", new SizeCheck(1, 1));
		addOptionBinding("instanceTemplate.outputTemplate", "outputEncoder.template");
	}
	
	protected abstract Encoding classify(Encoding inputEncoded);

	@Override
	protected Encoding transform(Object inputData) {
		return classify(parents.get(0).startTransform(inputData));
	}

}
