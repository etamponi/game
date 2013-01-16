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

import game.utils.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoCopyable;

public class Dataset extends ArrayList<Instance> implements KryoCopyable<Dataset> {
	
	private DatasetTemplate template;

	public Dataset(DatasetTemplate template) {
		this.template = template;
	}
	
	public Dataset(DatasetTemplate template, Collection<? extends Instance> collection) {
		super(collection);
		this.template = template;
	}
	
	public DatasetTemplate getTemplate() {
		return template;
	}
	
	public class SampleIterator implements Iterator<Sample> {
		
		private Block sourceFilter;
		private Block targetFilter;
		private Iterator<Instance> instanceIterator = iterator();
		private Data currentSourceSequence;
		private Data currentTargetSequence;
		private Data currentPredictionSequence;
		private int indexInInstance;
		
		private SampleIterator() {
			prepareForNextInstance();
		}
		
		public SampleIterator(Block sourceFilter, Block targetFilter) {
			this.sourceFilter = sourceFilter;
			this.targetFilter = targetFilter;
			prepareForNextInstance();
		}
		
		public void reset() {
			instanceIterator = iterator();
			prepareForNextInstance();
		}
		
		private void prepareForNextInstance() {
			Instance inst = instanceIterator.next();
			currentSourceSequence = sourceFilter == null ? inst.getSource() : sourceFilter.transform(inst.getSource());
			currentTargetSequence = targetFilter == null ? inst.getTarget() : targetFilter.transform(inst.getTarget());
			currentPredictionSequence = inst.getPrediction();
			indexInInstance = 0;
		}

		@Override
		public boolean hasNext() {
			return instanceIterator.hasNext() ||
					indexInInstance < currentSourceSequence.size();
		}

		@Override
		public Sample next() {
			if (indexInInstance == currentTargetSequence.size()) {
				prepareForNextInstance();
			}
			
			Sample ret = new Sample(currentSourceSequence.get(indexInInstance), currentTargetSequence.get(indexInInstance));
			if (currentPredictionSequence != null)
				ret.setPrediction(currentPredictionSequence.get(indexInInstance));	
			indexInInstance++;
			return ret;
		}
		
		public Block getOutputFilter() {
			return targetFilter;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("Cannot remove samples from a dataset");
		}
		
	}

	public SampleIterator sampleIterator() {
		return new SampleIterator();
	}
	
	public SampleIterator sampleIterator(Block sourceFilter, Block targetFilter) {
		return new SampleIterator(sourceFilter, targetFilter);
	}

	public List<Dataset> getFolds(int folds) {
		List<Dataset> ret = new ArrayList<>(folds);
		
		List<Integer> temp = Utils.range(0, size());
		Collections.shuffle(temp, Experiment.getRandom());
		
		int foldSize = size() / folds;
		
		for(int i = 0; i < folds; i++) {
			Dataset fold = new Dataset(template);
			for(int j = 0; j < foldSize; j++)
				fold.add(get(temp.get(i*foldSize+j)));
			ret.add(fold);
		}
		
		return ret;
	}
	
	public List<Dataset> getComplementaryFolds(List<Dataset> folds) {
		List<Dataset> ret = new ArrayList<>(folds.size());
		for(Dataset fold: folds) {
			Dataset complementary = new Dataset(template, this);
			complementary.removeAll(fold);
			ret.add(complementary);
		}
		return ret;
	}
	
	public Dataset getRandomSubset(double percent) {
		assert(percent > 0 && percent <= 1);
		List<Integer> temp = Utils.range(0, size());
		Collections.shuffle(temp, Experiment.getRandom());
		
		Dataset ret = new Dataset(template);
		
		int subsetSize = (int) (size()*percent);
		for(int i = 0; i < subsetSize; i++)
			ret.add(get(temp.get(i)));
		
		return ret;
	}
	
	public Dataset getFirsts(double percent) {
		assert(percent > 0 && percent <= 1);
		int lastIndex = (int) (size()*percent);
		Dataset ret = new Dataset(template);
		for(int i = 0; i < lastIndex; i++)
			ret.add(get(i));
		return ret;
	}
	
	public Dataset getLasts(double percent) {
		assert(percent > 0 && percent <= 1);
		int firstIndex = (int) (size()*(1-percent));
		Dataset ret = new Dataset(template);
		for(int i = firstIndex; i < size(); i++)
			ret.add(get(i));
		return ret;
	}
	
	@Override
	public Dataset copy(Kryo kryo) {
		kryo.reference(this);
		
		DatasetTemplate copyTmp = kryo.copy(template);
		Dataset ret = new Dataset(copyTmp);
		for(Instance i: this) {
			Instance copy = new Instance(new Data(i.getSource()), new Data(i.getTarget()), new Data(i.getPrediction()));
			ret.add(copy);
		}
		return ret;
	}
}
