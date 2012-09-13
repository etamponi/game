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
package game.core.blocks;

import game.configuration.ConfigurableList;
import game.core.Block;
import game.core.Dataset;
import game.core.Decoder;
import game.core.Encoding;
import game.core.GraphTrainer;
import game.core.Instance;
import game.core.InstanceTemplate;
import game.plugins.constraints.CompatibleWith;

import java.util.LinkedList;
import java.util.List;

public class Graph extends Transducer {
	
	public boolean trained = false;
	
	public InstanceTemplate template;

	public ConfigurableList classifiers = new ConfigurableList(this, Transducer.class);
	
	public ConfigurableList inputEncoders = new ConfigurableList(this, Encoder.class);
	
	public ConfigurableList pipes = new ConfigurableList(this, Pipe.class);
	
	public Decoder decoder;
	
	public Transducer outputClassifier;
	
	public GraphTrainer trainer;
	
	public Graph() {
		setOptionBinding("template", "classifiers.*.template");
		setOptionConstraints("classifiers.*", new CompatibleWith(this, "template"));
		
		setOptionBinding("template.inputTemplate", "inputEncoders.*.template");
		setOptionConstraints("inputEncoders.*", new CompatibleWith(this, "template.inputTemplate"));
		
		setOptionBinding("outputClassifier.outputEncoder", 	"decoder.encoder", "outputEncoder");
		setOptionConstraints("decoder", new CompatibleWith(this, "outputClassifier.outputEncoder"));
		
		setOptionConstraints("trainer", new CompatibleWith(this));

		omitFromErrorCheck("classifiers", "inputEncoders", "pipes");
		
		setPrivateOptions("trained");
	}
	/*
	@Override
	public String getTaskDescription() {
		return "dataset classification using " + name;
	}
	
	public Dataset startDatasetClassification(Dataset dataset, String outputDirectory) {
		return startTask(dataset, outputDirectory);
	}
	
	public List classify(List input) {
		return decoder.decode(outputClassifier.transform(input));
	}
	
	protected Dataset classifyAll(Dataset dataset, String outputDirectory) {
		Dataset ret = new Dataset(outputDirectory, false);
		double singleIncrease = 1.0 / dataset.size();
		int count = 1;
		InstanceIterator it = dataset.instanceIterator();
		while (it.hasNext()) {
			Instance instance = it.next();
			Encoding encoding = outputClassifier.transform(instance.getInput());
			instance.setPredictionEncoding(encoding);
			instance.setPrediction(decoder.decode(encoding));
			ret.add(instance);
			if ((count-1) % 10 == 0 || count == dataset.size())
				updateStatus(getCurrentPercent()+singleIncrease, "instances predicted " + count + "/" + dataset.size());
			count++;
		}
		ret.setReadOnly();
		return ret;
	}

	@Override
	protected Object execute(Object... params) {
		return classifyAll((Dataset)params[0], (String)params[1]);
	}
	*/
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
			return "graph can not contain directed cycles.";
		path.add(current);
		for (Block parent: current.parents.getList(Block.class)) {
			String ret = recursivelyAddAll(parent, new LinkedList(path));
			if (ret != null)
				return ret;
		}
		return null;
	}
	/*
	public void setTrained() {
		classifiers.clear();
		inputEncoders.clear();
		pipes.clear();
		classifiers.setOption("add", outputClassifier, false, null);
		outputClassifier.name = name;
	}
	*/
	@Override
	public boolean isTrained() {
		return trained;
	}
	@Override
	protected void train(Dataset trainingSet) {
		startAnotherTaskAndWait(1, trainer, this, trainingSet);
		trained = true;
	}
	@Override
	public Encoding transform(List input) {
		return outputClassifier.transform(input);
	}
	@Override
	public boolean acceptsParents() {
		return false;
	}
	public void classifyInstance(Instance instance) {
		Encoding encoding = this.transform(instance.getInput());
		instance.setPredictionEncoding(encoding);
		instance.setPrediction(decoder.decode(encoding));
	}
	@Override
	public boolean isCompatible(InstanceTemplate object) {
		return true;
	}
	
}
