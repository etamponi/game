/*******************************************************************************
 * Copyright (c) 2012 Emanuele.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Emanuele - initial API and implementation
 ******************************************************************************/
package game.core.blocks;

import game.configuration.ConfigurableList;
import game.core.Block;
import game.core.DataTemplate.Data;
import game.core.Decoder;
import game.core.Encoding;
import game.core.Instance;
import game.core.InstanceTemplate;
import game.plugins.constraints.CompatibleWith;

import java.util.LinkedList;

public class Graph extends Transducer {

	public ConfigurableList classifiers = new ConfigurableList(this, Transducer.class);
	
	public ConfigurableList inputEncoders = new ConfigurableList(this, Encoder.class);
	
	public ConfigurableList pipes = new ConfigurableList(this, Pipe.class);
	
	public Decoder decoder;
	
	public Transducer outputClassifier;
	
	public Graph() {
		setOptionBinding("template", "classifiers.*.template");
		setOptionConstraints("classifiers.*", new CompatibleWith(this, "template"));
		
		setOptionBinding("template.inputTemplate", "inputEncoders.*.template");
		setOptionConstraints("inputEncoders.*", new CompatibleWith(this, "template.inputTemplate"));
		
		setOptionBinding("outputClassifier.outputEncoder", 	"decoder.encoder", "outputEncoder");
		setOptionConstraints("decoder", new CompatibleWith(this, "outputClassifier.outputEncoder"));

		omitFromErrorCheck("classifiers", "inputEncoders", "pipes");
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
			return "graph can not contain directed cycles.";
		path.add(current);
		for (Block parent: current.parents.getList(Block.class)) {
			String ret = recursivelyAddAll(parent, new LinkedList(path));
			if (ret != null)
				return ret;
		}
		return null;
	}

	@Override
	public Encoding transform(Data input) {
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
