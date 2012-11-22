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

import game.core.DataTemplate.Data;

import java.util.LinkedList;
import java.util.List;

import com.ios.IList;
import com.ios.IObject;
import com.ios.Property;
import com.ios.Trigger;
import com.ios.constraints.CompatibleWith;
import com.ios.listeners.SubPathListener;
import com.ios.triggers.BoundProperties;
import com.ios.triggers.MasterSlaveTrigger;
import com.ios.triggers.SimpleTrigger;

public abstract class Block extends IObject {

	public enum FeatureType {
		NUMERIC,
		NOMINAL
	}
	
	public static class Position extends IObject {
		public int x = -1;
		public int y = -1;
		
		public boolean isValid() {
			return x >= 0 && y >= 0;
		}
	}
	
	public Position position = new Position();
	
	public IList<Block> parents;
	
	public boolean trained = false;
	
	public TrainingAlgorithm trainingAlgorithm;
	
	public Block() {
		addTrigger(new MasterSlaveTrigger(this, "", "trainingAlgorithm.block"));
		
		final Trigger t = new BoundProperties(this, "nothing");
		addTrigger(t);
		addTrigger(new SimpleTrigger(new SubPathListener(new Property(this, "trainingAlgorithm"))) {
			private Block block = Block.this;
			private Trigger trigger = t;
			@Override
			public void action(Property changedPath) {
				trigger.getBoundProperties().clear();
				
				for(Object path: block.trainingAlgorithm.getManagedProperties())
					trigger.getBoundProperties().add(new Property(block, path.toString()));
			}
		});
		
		addConstraint("trainingAlgorithm", new CompatibleWith(new Property(this, "")));

		setContent("parents", new IList<>(Block.class));
		setContent("trainingAlgorithm", new NoTraining());
	}
	
	public abstract Encoding transform(Data input);
	
	public abstract boolean acceptsParents();
	
	public abstract int getFeatureNumber();
	public abstract FeatureType getFeatureType(int featureIndex);
	
	protected List<Encoding> getParentsEncodings(Data input) {
		List<Encoding> ret = new LinkedList<>();
		
		for (Block parent: parents) {
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

}
