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

import game.configuration.ConfigurableList;
import game.core.blocks.Transducer;
import game.core.blocks.Encoder;
import game.core.blocks.Pipe;
import game.plugins.constraints.CompatibleWith;

import java.util.LinkedList;

public class Graph extends LongTask {
	
	public InstanceTemplate template; 

	public TemplateConstrainedList classifiers = new TemplateConstrainedList(this, Transducer.class);
	public TemplateConstrainedList inputEncoders = new TemplateConstrainedList(this, Encoder.class);
	public ConfigurableList pipes = new ConfigurableList(this, Pipe.class);
	
	public Decoder decoder;
	public Transducer outputClassifier;
	
	public Graph() {
		setOptionBinding("template", 						"classifiers.constraint");
		setOptionBinding("template.inputTemplate", 			"inputEncoders.constraint");
		setOptionBinding("outputClassifier.outputEncoder", 	"decoder.encoder");
		
		setOptionConstraint("decoder", new CompatibleWith(this, "outputClassifier.outputEncoder"));

		omitFromErrorCheck("classifiers", "inputEncoders", "pipes");
	}

	@Override
	public String getTaskDescription() {
		return "dataset classification using " + name;
	}
	
	public Dataset startDatasetClassification(Dataset dataset) {
		return startTask(dataset);
	}
	
	public Object classify(Object inputData) {
		return decoder.decode(outputClassifier.transform(inputData));
	}
	
	protected Dataset classifyAll(Dataset dataset) {
		double singleIncrease = 1.0 / dataset.size();
		int count = 1;
		for (Instance i: dataset) {
			Encoding encoding = outputClassifier.transform(i.getInputData());
			i.setPredictionEncoding(encoding);
			i.setPredictedData(decoder.decode(encoding));
			updateStatus(getCurrentPercent()+singleIncrease, "instance predicted " + count + "/" + dataset.size());
			count++;
		}
		return dataset;
	}

	@Override
	protected Object execute(Object... params) {
		return classifyAll((Dataset)params[0]);
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
		for (Block parent: current.parents.getList(Block.class)) {
			String ret = recursivelyAddAll(parent, new LinkedList(path));
			if (ret != null)
				return ret;
		}
		return null;
	}

	public void setTrained() {
		classifiers.clear();
		inputEncoders.clear();
		pipes.clear();
		classifiers.setOption("add", outputClassifier, false, null);
		outputClassifier.name = name;
	}

}
