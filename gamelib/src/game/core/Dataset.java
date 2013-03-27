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

import game.core.blocks.Decoder;
import game.utils.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.FieldSerializer;
import com.ios.IOSSerializer;
import com.ios.IObject;

public class Dataset implements List<Instance> {
	
	public static final class DatasetSerializer extends IOSSerializer<Dataset> {

		private final FieldSerializer<Dataset> internal = new FieldSerializer<>(IObject.getKryo(), Dataset.class);
		
		@Override
		public void write(Kryo kryo, Output output, Dataset object) {
			internal.write(kryo, output, object);
		}

		@Override
		public Dataset read(Kryo kryo, Input input, Class<Dataset> type) {
			return internal.read(kryo, input, type);
		}

		@Override
		public Dataset copy(Kryo kryo, Dataset original) {
			return internal.copy(kryo, original);
		}
		
	}
	
	private final List<Instance> internal = new ArrayList<>();
	private final DatasetTemplate template;

	public Dataset(Dataset other) {
		this.template = other.template.copy();
		this.internal.addAll(other.internal);
	}
	
	public Dataset(DatasetTemplate template) {
		this.template = template;
	}
	
	public Dataset(DatasetTemplate template, Collection<? extends Instance> collection) {
		this.template = template;
		this.internal.addAll(collection);
	}
	
	public DatasetTemplate getTemplate() {
		return template;
	}
	
	public class SampleIterator implements Iterator<Sample> {
		
		private Block sourceFilter;
		private Block targetFilter;
		private Decoder decoder;
		private Iterator<Instance> instanceIterator = iterator();
		private Data currentSourceData;
		private Data currentTargetData;
		private Data currentPredictionData;
		private Data currentDecodedData;
		private int indexInInstance;
		
		private SampleIterator() {
			prepareForNextInstance();
		}
		
		public SampleIterator(Block sourceFilter, Block targetFilter, Decoder decoder) {
			this.sourceFilter = sourceFilter;
			this.targetFilter = targetFilter;
			this.decoder = decoder;
			prepareForNextInstance();
		}
		
		public void reset() {
			instanceIterator = iterator();
			prepareForNextInstance();
		}
		
		private void prepareForNextInstance() {
			if (!instanceIterator.hasNext())
				return;
			Instance inst = instanceIterator.next();
			currentSourceData = sourceFilter == null ? inst.getSource() : sourceFilter.transform(inst.getSource());
			currentTargetData = targetFilter == null ? inst.getTarget() : targetFilter.transform(inst.getTarget());
			currentPredictionData = inst.getPrediction();
			currentDecodedData = decoder == null || currentPredictionData == null ? null : decoder.transform(currentPredictionData);
			indexInInstance = 0;
		}

		@Override
		public boolean hasNext() {
			return instanceIterator.hasNext() || 
					(currentSourceData != null && indexInInstance < currentSourceData.size());
		}

		@Override
		public Sample next() {
			if (indexInInstance == currentTargetData.size()) {
				prepareForNextInstance();
			}
			
			Sample ret = new Sample(currentSourceData.get(indexInInstance), currentTargetData.get(indexInInstance));
			if (currentPredictionData != null)
				ret.setPrediction(currentPredictionData.get(indexInInstance));
			if (currentDecodedData != null)
				ret.setDecodedTarget(currentDecodedData.get(indexInInstance));
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
	
	public SampleIterator sampleIterator(Block sourceFilter, Block targetFilter, Decoder decoder) {
		return new SampleIterator(sourceFilter, targetFilter, decoder);
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

	// Methods
	@Override
	public boolean add(Instance e) {
		return internal.add(e);
	}

	@Override
	public void add(int index, Instance element) {
		internal.add(index, element);
	}

	@Override
	public boolean addAll(Collection<? extends Instance> c) {
		return internal.addAll(c);
	}

	@Override
	public boolean addAll(int index, Collection<? extends Instance> c) {
		return internal.addAll(index, c);
	}

	@Override
	public void clear() {
		internal.clear();
	}

	@Override
	public boolean contains(Object o) {
		return internal.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return internal.containsAll(c);
	}

	@Override
	public Instance get(int index) {
		return internal.get(index);
	}

	@Override
	public int indexOf(Object o) {
		return internal.indexOf(o);
	}

	@Override
	public boolean isEmpty() {
		return internal.isEmpty();
	}

	@Override
	public Iterator<Instance> iterator() {
		return internal.iterator();
	}

	@Override
	public int lastIndexOf(Object o) {
		return internal.lastIndexOf(o);
	}

	@Override
	public ListIterator<Instance> listIterator() {
		return internal.listIterator();
	}

	@Override
	public ListIterator<Instance> listIterator(int index) {
		return internal.listIterator(index);
	}

	@Override
	public boolean remove(Object o) {
		return internal.remove(o);
	}

	@Override
	public Instance remove(int index) {
		return internal.remove(index);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return internal.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return internal.retainAll(c);
	}

	@Override
	public Instance set(int index, Instance element) {
		return internal.set(index, element);
	}

	@Override
	public int size() {
		return internal.size();
	}

	@Override
	public List<Instance> subList(int fromIndex, int toIndex) {
		return internal.subList(fromIndex, toIndex);
	}

	@Override
	public Object[] toArray() {
		return internal.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return internal.toArray(a);
	}

	public Dataset apply(Block block) {
		Dataset ret = new Dataset(new DatasetTemplate(block.outputTemplate, template.targetTemplate));
		
		for(Instance inst: this) {
			Data out = block.transform(inst.getSource());
			Instance outInst = new Instance(out, inst.getTarget());
			ret.add(outInst);
		}
		
		return ret;
	}

	public List<Sample> toSampleList() {
		List<Sample> ret = new ArrayList<>();
		SampleIterator it = sampleIterator();
		while(it.hasNext())
			ret.add(it.next());
		return ret;
	}
	
}
