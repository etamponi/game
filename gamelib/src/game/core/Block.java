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
package game.core;

import game.configuration.Configurable;
import game.configuration.ConfigurableList;
import game.core.DataTemplate.Data;
import game.plugins.constraints.CompatibleWith;

import java.util.LinkedList;
import java.util.List;

public abstract class Block extends Configurable {

	public enum FeatureType {
		NUMERIC,
		NOMINAL
	}
	
	public static class Position extends Configurable {
		public int x = -1;
		public int y = -1;
		
		public boolean isValid() {
			return x >= 0 && y >= 0;
		}
	}
	
	public Position position = new Position();
	
	public String name;
	
	public ConfigurableList parents = new ConfigurableList(this, Block.class);
	
	public boolean trained = false;
	
	public TrainingAlgorithm trainingAlgorithm;
	
	public Block() {
		setOptionBinding("self", "trainingAlgorithm.block");
		setOptionConstraints("trainingAlgorithm", new CompatibleWith(this));
		
		setOption("trainingAlgorithm", new NoTraining());
		
		setAsInternalOptions("position");
		omitFromErrorCheck("parents");
	}
	
	public abstract Encoding transform(Data input);
	
	public abstract boolean acceptsParents();
	
	public abstract int getFeatureNumber();
	public abstract FeatureType getFeatureType(int featureIndex);
	
	protected List<Encoding> getParentsEncodings(Data input) {
		List<Encoding> ret = new LinkedList<>();
		
		for (Block parent: parents.getList(Block.class)) {
			ret.add(parent.transform(input));
		}
		
		return ret;
	}
	
	public Encoding getParentEncoding(int i, Data input) {
		assert(i >= 0 && i < parents.size());
		return ((Block)parents.get(i)).transform(input);
	}
	
	public <T extends Block> T getParent(int i) {
		if (parents.size() <= i)
			return null;
		return (T)parents.get(i);
	}
	
	public void setTrainingAlgorithm(TrainingAlgorithm algorithm) {
		if (trainingAlgorithm != null) {
			unsetAsInternalOptions(trainingAlgorithm.getManagedBlockOptions());
		}
		trainingAlgorithm = algorithm;
		setAsInternalOptions(algorithm.getManagedBlockOptions());
	}

}
