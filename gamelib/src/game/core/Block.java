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
	
	public String name;
	
	public ConfigurableList parents = new ConfigurableList(this, Block.class);
	
	public Position position = new Position();
	
	public Block() {
		omitFromErrorCheck("parents");
		
		setPrivateOptions("position");
	}
	
	public abstract boolean isTrained();
	
	protected abstract void train(Dataset trainingSet);
	
	public abstract Encoding transform(List input);
	
	public abstract boolean acceptsParents();
	
	@Override
	public String getTaskDescription() {
		return "training of " + this;
	}

	public void startTraining(Dataset trainingSet) {
		startTask(trainingSet);
	}
	
	@Override
	protected Object execute(Object... params) {
		train((Dataset)params[0]);
		return null;
	}
	
	protected List<Encoding> getParentsEncodings(List input) {
		List<Encoding> ret = new LinkedList<>();
		
		for (Block parent: parents.getList(Block.class)) {
			ret.add(parent.transform(input));
		}
		
		return ret;
	}
	
	public Block getParent(int i) {
		return (Block)parents.get(i);
	}
	
	public Encoding getParentEncoding(int i, List input) {
		assert(i >= 0 && i < parents.size());
		return ((Block)parents.get(i)).transform(input);
	}

}
