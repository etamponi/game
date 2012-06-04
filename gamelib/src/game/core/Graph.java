package game.core;

import game.core.nodes.Classifier;

public class Graph extends LongTask {

	private static final String CLASSIFY = "classify";
	private static final String CLASSIFYALL = "classifyall";
	
	public Classifier finalClassifier;
	
	public Decoder decoder;
	
	public Graph() {
		addOptionBinding("finalClassifier.outputEncoder", "decoder.encoder");
	}
	
	public <T> T startClassification(Object object) {
		if (object instanceof Dataset)
			return (T)startTask(CLASSIFYALL, object);
		else
			return (T)startTask(CLASSIFY, object);
	}
	
	protected Object classify(Object inputData) {
		return decoder.decode(finalClassifier.startTransform(inputData));
	}
	
	protected Dataset classifyAll(Dataset dataset) {
		for (Instance i: dataset) {
			i.setPredictedData(classify(i.getInputData()));
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

}
