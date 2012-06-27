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

import java.util.LinkedList;
import java.util.List;

public abstract class Block extends LongTask {
	
	public static class Position extends Configurable {
		public int x = -1;
		public int y = -1;
		
		public boolean isValid() {
			return x >= 0 && y >= 0;
		}
	}

	public static final String TRAININGTASK = "training";
	public static final String TRANSFORMTASK = "transformation";
	
	public ConfigurableList parents = new ConfigurableList(this, Block.class);
	
	public Position position = new Position();
	
	public Block() {
		omitFromErrorCheck("parents");
	}
	
	public abstract boolean isTrained();
	
	protected abstract void train(Dataset trainingSet);
	
	protected abstract Encoding transform(Object inputData);
	
	public abstract boolean acceptsNewParents();
	
	public void startTraining(Dataset trainingSet) {
		startTask(TRAININGTASK, trainingSet);
	}
	
	public Encoding startTransform(Object inputData) {
		return startTask(TRANSFORMTASK, inputData);
	}
	
	@Override
	protected Object execute(Object... params) {
		if (!isTrained() && getTaskType().equals(TRAININGTASK)) {
			train((Dataset)params[0]);
			return null;
		} else if (isTrained() && getTaskType().equals(TRANSFORMTASK))
			return transform(params[0]);
		else
			return null;
	}
	
	public ConfigurableList getParents() {
		return parents;
	}
	
	protected List<Encoding> getParentsEncodings(Object inputData) {
		List<Encoding> ret = new LinkedList<>();
		
		for (Block parent: parents.getList(Block.class)) {
			ret.add(parent.startTransform(inputData));
		}
		
		return ret;
	}
	
	public Encoding getParentEncoding(int i, Object inputData) {
		if (i < parents.size())
			return ((Block)parents.get(i)).startTransform(inputData);
		else
			return null;
	}

}
