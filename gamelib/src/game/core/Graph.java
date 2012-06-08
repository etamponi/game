package game.core;

import game.configuration.Configurable;
import game.configuration.ConfigurableList;
import game.core.blocks.Classifier;
import game.core.blocks.Encoder;
import game.plugins.constraints.CompatibleDecoderConstraint;
import game.plugins.constraints.CompatibleEncoderConstraint;

import java.util.LinkedList;

public class Graph extends LongTask {
	
	public static class EncoderList extends ConfigurableList<Encoder> {
		
		public DataTemplate template;
		
		public EncoderList(Configurable owner) {
			super(owner);
			
			addOptionBinding("template", "*.template");
			
			setOptionConstraint("*", new CompatibleEncoderConstraint(this, "template"));
		}
		
	}

	private static final String CLASSIFY = "classify";
	private static final String CLASSIFYALL = "classifyall";
	
	public InstanceTemplate template; 
	
	public Classifier outputClassifier;

	public ConfigurableList<Classifier> classifiers = new ConfigurableList<>(this);
	public EncoderList inputEncoders = new EncoderList(this);
	
	public Decoder decoder;
	
	public Graph() {
		addOptionBinding("template", 						"classifiers.*.template");
		addOptionBinding("template.inputTemplate", 			"inputEncoders.template");
		addOptionBinding("outputClassifier.outputEncoder", 	"decoder.encoder");
		
		setOptionConstraint("decoder", new CompatibleDecoderConstraint(this, "outputClassifier.outputEncoder"));
	}
	
	public <T> T startClassification(Object object) {
		if (object instanceof Dataset)
			return (T)startTask(CLASSIFYALL, object);
		else
			return (T)startTask(CLASSIFY, object);
	}
	
	protected Object classify(Object inputData) {
		return decoder.decode(outputClassifier.startTransform(inputData));
	}
	
	protected Dataset classifyAll(Dataset dataset) {
		double singleIncrease = 1.0 / dataset.size();
		for (Instance i: dataset) {
			i.setPredictedData(
					startAnotherTaskAndWait(getCurrentPercent()+singleIncrease, this, CLASSIFY, i.getInputData())
					);
		}
		return dataset;
	}

	@Override
	protected Object execute(Object... params) {
		if (getTaskType().equals(CLASSIFYALL))
			return classifyAll((Dataset)params[0]);
		else if (getTaskType().equals(CLASSIFY))
			return classify(params[0]);
		return null;
	}

	@Override
	protected LinkedList<String> getErrors() {
		LinkedList<String> ret = super.getErrors();
		
		LinkedList myNodes = new LinkedList(classifiers);
		myNodes.addAll(inputEncoders);
		
		LinkedList graphNodes = new LinkedList();
		String cycleFound = recursivelyAddAll(outputClassifier, graphNodes);
		if (cycleFound != null)
			ret.add(cycleFound);
		if (!myNodes.containsAll(graphNodes))
			ret.add("graph has some nodes that are not registered.");
		
		return ret;
	}
	
	private String recursivelyAddAll(Block current, LinkedList all) {
		if (all.contains(current))
			return "graph cannot have directed cycles.";
		all.add(current);
		for (Block parent: current.parents) {
			String ret = recursivelyAddAll(parent, all);
			if (ret != null)
				return ret;
		}
		return null;
	}

}
