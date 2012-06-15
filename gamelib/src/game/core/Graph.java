/*******************************************************************************
 * Copyright (c) 2012 Emanuele Tamponi.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Emanuele Tamponi - initial API and implementation
 ******************************************************************************/
package game.core;

import game.configuration.Configurable;
import game.configuration.ConfigurableList;
import game.core.blocks.Classifier;
import game.core.blocks.Encoder;
import game.core.blocks.Pipe;
import game.plugins.constraints.CompatibleConstraint;

import java.util.LinkedList;

public class Graph extends LongTask {
	
	public static class ClassifierList extends ConfigurableList {
		
		public InstanceTemplate template;
		
		public ClassifierList(Configurable owner) {
			super(owner, Classifier.class);
			
			addOptionBinding("template", "*.template");
			
			setOptionConstraint("*", new CompatibleConstraint(this, "template"));
		}
		
	}
	
	public static class EncoderList extends ConfigurableList {
		
		public DataTemplate template;
		
		public EncoderList(Configurable owner) {
			super(owner, Encoder.class);
			
			addOptionBinding("template", "*.template");
			
			setOptionConstraint("*", new CompatibleConstraint(this, "template"));
		}
		
	}

	private static final String CLASSIFY = "classify";
	private static final String CLASSIFYALL = "classifyall";
	
	public InstanceTemplate template; 

	public ClassifierList classifiers = new ClassifierList(this);
	public EncoderList inputEncoders = new EncoderList(this);
	public ConfigurableList pipes = new ConfigurableList(this, Pipe.class);
	
	public Decoder decoder;
	public Classifier outputClassifier;
	
	public Graph() {
		addOptionBinding("template", 						"classifiers.template");
		addOptionBinding("template.inputTemplate", 			"inputEncoders.template");
		addOptionBinding("outputClassifier.outputEncoder", 	"decoder.encoder");
		
		setOptionConstraint("decoder", new CompatibleConstraint(this, "outputClassifier.outputEncoder"));

		omitFromErrorCheck("classifiers");
		omitFromErrorCheck("inputEncoders");
		omitFromErrorCheck("pipes");
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
		
		LinkedList graphNodes = new LinkedList();
		String cycleFound = recursivelyAddAll(outputClassifier, graphNodes);
		if (cycleFound != null)
			ret.add(cycleFound);
		
		return ret;
	}
	
	private String recursivelyAddAll(Block current, LinkedList path) {
		if (current == null)
			return null;
		if (path.contains(current))
			return "graph cannot have directed cycles.";
		path.add(current);
		for (Block parent: current.getParents().getList(Block.class)) {
			String ret = recursivelyAddAll(parent, new LinkedList(path));
			if (ret != null)
				return ret;
		}
		return null;
	}

}
