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

import com.ios.IList;
import com.ios.Property;
import com.ios.constraints.CompatibleWith;
import com.ios.triggers.MasterSlaveTrigger;

import game.core.DataTemplate.Data;
import game.core.Decoder;
import game.core.Encoding;
import game.core.Instance;
import game.core.InstanceTemplate;

public class PredictionGraph extends Transducer {

	public IList<Transducer> classifiers;
	
	public IList<Encoder> inputEncoders;
	
	public IList<Pipe> pipes;
	
	public Decoder decoder;
	
	public Transducer outputClassifier;
	
	public PredictionGraph() {
		setContent("classifiers", new IList<>(Transducer.class));
		setContent("inputEncoders", new IList<>(Encoder.class));
		setContent("pipes", new IList<>(Pipe.class));
		
		addTrigger(new MasterSlaveTrigger(this, "template", "classifiers.*.template"));
		addConstraint("classifiers.*", new CompatibleWith(new Property(this, "template")));
		
		addTrigger(new MasterSlaveTrigger(this, "template.inputTemplate", "inputEncoders.*.template"));
		addConstraint("inputEncoders.*", new CompatibleWith(new Property(this, "template.inputTemplate")));
		
		addTrigger(new MasterSlaveTrigger(this, "outputClassifier.outputEncoder", "decoder.encoder", "outputEncoder"));
		addConstraint("decoder", new CompatibleWith(new Property(this, "outputClassifier.outputEncoder")));
	}

	/* FIXME this must go as an addErrorCheck
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
		for (Block parent: current.parents) {
			String ret = recursivelyAddAll(parent, new LinkedList(path));
			if (ret != null)
				return ret;
		}
		return null;
	}
	*/
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

	@Override
	public FeatureType getFeatureType(int featureIndex) {
		if (outputClassifier != null)
			return outputClassifier.getFeatureType(featureIndex);
		else
			return FeatureType.NUMERIC;
	}
	
}
