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

import game.core.DataTemplate.Data;
import game.utils.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoCopyable;

public class Dataset extends ArrayList<Instance> implements KryoCopyable<Dataset> {
	
	private InstanceTemplate template;

	public Dataset(InstanceTemplate template) {
		this.template = template;
	}
	
	public Dataset(InstanceTemplate template, Collection<? extends Instance> collection) {
		super(collection);
		this.template = template;
	}
	
	public InstanceTemplate getTemplate() {
		return template;
	}
	
	public enum IterationType {
		EVERYTHING, IN_OUT, IN_ENC, IN_OUT_PRED, IN_OUT_ENC
	}
	
	public class SampleIterator<I, O> implements Iterator<Sample<I, O>> {
		
		private IterationType type;
		private Block inputEncoder;
		private Block outputEncoder;
		private Iterator<Instance> instanceIterator = iterator();
		private Data currentInputSequence;
		private Data currentOutputSequence;
		private Data currentPredictionSequence;
		private Encoding currentInputEncoding;
		private Encoding currentOutputEncoding;
		private Encoding currentPredictionEncoding;
		private int indexInInstance;
		
		private SampleIterator(IterationType type) {
			if (type == IterationType.IN_OUT_ENC || type == IterationType.EVERYTHING)
				throw new UnsupportedOperationException("Cannot use a sample iterator with encoding if you don't specify the encoders");
			this.type = type;
			prepareForNextInstance();
		}
		
		public SampleIterator(IterationType type, Block inputEncoder, Block outputEncoder) {
			this.type = type;
			this.inputEncoder = inputEncoder;
			this.outputEncoder = outputEncoder;
			prepareForNextInstance();
		}
		
		public void reset() {
			instanceIterator = iterator();
			prepareForNextInstance();
		}
		
		private void prepareForNextInstance() {
			Instance inst = instanceIterator.next();
			currentInputSequence = inst.getInput();
			currentOutputSequence = inst.getOutput();
			if (inst.getPrediction() != null)
				currentPredictionSequence = inst.getPrediction();
			if (inputEncoder != null && outputEncoder != null) {
				currentInputEncoding = inputEncoder.transform(inst.getInput());
				currentOutputEncoding = outputEncoder.transform(inst.getOutput());
			}
			currentPredictionEncoding = inst.getPredictionEncoding();
			indexInInstance = 0;
		}

		@Override
		public boolean hasNext() {
			return instanceIterator.hasNext() ||
					indexInInstance < currentInputSequence.size();
		}

		@Override
		public Sample<I, O> next() {
			if (indexInInstance == currentInputSequence.size()) {
				prepareForNextInstance();
			}
			
			Sample<I, O> ret = null;
			switch(type) {
			case EVERYTHING:
				ret = new Sample(
						currentInputSequence.get(indexInInstance),
						currentInputEncoding.getElement(indexInInstance),
						currentOutputSequence.get(indexInInstance),
						currentOutputEncoding.getElement(indexInInstance),
						currentPredictionSequence.get(indexInInstance),
						currentPredictionEncoding.getElement(indexInInstance));
				break;
			case IN_OUT:
				ret = new Sample(
						currentInputSequence.get(indexInInstance),
						currentOutputSequence.get(indexInInstance));
				break;
			case IN_OUT_PRED:
				ret = new Sample(
						currentInputSequence.get(indexInInstance),
						currentOutputSequence.get(indexInInstance),
						currentPredictionSequence.get(indexInInstance));
				break;
			case IN_OUT_ENC:
				ret = new Sample(
						currentInputSequence.get(indexInInstance),
						currentInputEncoding.getElement(indexInInstance),
						currentOutputSequence.get(indexInInstance),
						currentOutputEncoding.getElement(indexInInstance));
				break;
			default:
				break;
			}
			
			indexInInstance++;
			return ret;
		}
		
		public Block getOutputEncoder() {
			return outputEncoder;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("Cannot remove samples from a dataset");
		}
		
	}

	public SampleIterator sampleIterator(boolean includePrediction) {
		if (includePrediction)
			return new SampleIterator(IterationType.IN_OUT_PRED);
		else
			return new SampleIterator(IterationType.IN_OUT);
	}
	
	public SampleIterator encodedSampleIterator(Block inputEncoder, Block outputEncoder, boolean includePrediction) {
		if (includePrediction)
			return new SampleIterator(IterationType.EVERYTHING, inputEncoder, outputEncoder);
		else
			return new SampleIterator(IterationType.IN_OUT_ENC, inputEncoder, outputEncoder);
	}
	
	public List<Dataset> getFolds(int folds) {
		List<Dataset> ret = new ArrayList<>(folds);
		
		List<Integer> temp = Utils.range(0, size());
		Collections.shuffle(temp);
		
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
		Collections.shuffle(temp);
		
		Dataset ret = new Dataset(template);
		
		int subsetSize = (int) (size()*percent);
		for(int i = 0; i < subsetSize; i++)
			ret.add(get(temp.get(i)));
		
		return ret;
	}

	@Override
	public Dataset copy(Kryo kryo) {
		kryo.reference(this);
		
		InstanceTemplate copyTmp = kryo.copy(template);
		Dataset ret = new Dataset(copyTmp);
		for(Instance i: this) {
			Instance copy = copyTmp.newInstance();
			Data input = copyTmp.inputTemplate.newData();
			input.addAll(kryo.copy(new ArrayList(i.getInput())));
			Data output = copyTmp.outputTemplate.newData();
			output.addAll(kryo.copy(new ArrayList(i.getOutput())));
			copy.setInput(input);
			copy.setOutput(output);
			if (i.getPrediction() != null) {
				Data prediction = copyTmp.outputTemplate.newData();
				prediction.addAll(kryo.copy(new ArrayList(i.getPrediction())));
				copy.setPrediction(prediction);
			}
			if (i.getPredictionEncoding() != null) {
				copy.setPredictionEncoding(kryo.copy(i.getPredictionEncoding()));
			}
			ret.add(copy);
		}
		return ret;
	}
	
}
