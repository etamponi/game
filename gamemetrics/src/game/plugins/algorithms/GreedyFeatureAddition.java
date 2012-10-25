package game.plugins.algorithms;

import game.configuration.ErrorCheck;
import game.core.Block;
import game.core.Dataset;
import game.core.Dataset.SampleIterator;
import game.core.TrainingAlgorithm;
import game.core.blocks.Encoder;
import game.plugins.correlation.CorrelationCoefficient;
import game.plugins.encoders.IntegerEncoder;
import game.plugins.pipes.FeatureSelection;
import game.utils.Log;

import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.math3.linear.RealVector;

public class GreedyFeatureAddition extends TrainingAlgorithm<FeatureSelection> {
	
	public int finalFeatures;
	
	public CorrelationCoefficient coefficient;
	
	public int runs = 10;
	
	public double runPercent = 0.5;

	public int selectedMasks = 5;
	
	public GreedyFeatureAddition() {
		setOptionChecks("finalFeatures", new ErrorCheck<Integer>() {
			@Override
			public String getError(Integer value) {
				if (block != null) {
					if (block.getParent(0) != null) {
						if (block.getParent(0).getFeatureNumber() < value)
							return "cannot be greater than starting feature number (" + block.getParent(0).getFeatureNumber() + ")";
					}
				}
				return null;
			}
		});
	}

	@Override
	public boolean isCompatible(Block object) {
		return object instanceof FeatureSelection;
	}
	
	private class Candidate implements Comparable<Candidate> {
		
		private double weight;
		
		private String mask;
		
		public Candidate(String mask, double weight) {
			Log.write(GreedyFeatureAddition.this, "%5.2f -> %s", weight*100, mask);
			
			this.mask = mask;
			this.weight = weight;
		}
		
		public String getMask() {
			return mask;
		}
		
		@Override
		public int compareTo(Candidate other) {
			return -Double.compare(this.weight, other.weight);
		}
		
		@Override
		public boolean equals(Object other) {
			if (other instanceof Candidate) {
				return this.mask.equals(((Candidate) other).mask);
			} else {
				return false;
			}
		}
		
	}

	@Override
	protected void train(Dataset dataset) {
		IntegerEncoder outputEncoder = new IntegerEncoder();
		outputEncoder.setOption("template", dataset.template.outputTemplate);
		
		SortedSet<Candidate> candidates = initCandidates(dataset, outputEncoder);
		SortedSet<Candidate> bestCandidates = null;
		for(int features = 1; features <= finalFeatures; features++) {
			bestCandidates = selectBestCandidates(candidates);
			Log.write(this, "Selected best %d candidates", selectedMasks);
			
			for(Candidate bestCandidate: bestCandidates) {
				int index = bestCandidate.getMask().indexOf('0');
				while(index != -1) {
					String newMask = bestCandidate.getMask().substring(0, index) + "1" + bestCandidate.getMask().substring(index+1);
					if (!candidates.contains(new Candidate(newMask, 0))) {
						double newWeight = evaluateMaskWeight(newMask, dataset, outputEncoder);
						candidates.add(new Candidate(newMask, newWeight));
					}
					index = bestCandidate.getMask().indexOf('0', index+1);
				}
			}
		}
		block.mask = bestCandidates.first().getMask();
	}

	private SortedSet<Candidate> selectBestCandidates(SortedSet<Candidate> candidates) {
		SortedSet<Candidate> ret = new TreeSet<>();
		for(Candidate candidate: candidates) {
			ret.add(candidate);
			if (ret.size() == selectedMasks)
				break;
		}
		return ret;
	}

	private double evaluateMaskWeight(String mask, Dataset dataset, Encoder outputEncoder) {
		block.mask = mask;
		double mean = 0;
		int count = 0;
		for(int run = 0; run < runs; run++) {
			SampleIterator it = dataset.getRandomSubset(runPercent).encodedSampleIterator(block, outputEncoder, false);
			RealVector values = coefficient.computeSyntheticValues(it);
			if (values != null) {
				count = 0;
				mean += values.getEntry(0);
			} else {
				if (count == runs) {
					mean = 0;
					break;
				}
				run--;
				count++;
			}
		}
		mean = mean / runs;
		
		return mean;
	}

	private SortedSet<Candidate> initCandidates(Dataset dataset, Encoder outputEncoder) {
		SortedSet<Candidate> ret = new TreeSet<>(); 

		int baseFeatures = block.getParent(0).getFeatureNumber();
		for(int i = 0; i < baseFeatures; i++) {
			StringBuilder mask = new StringBuilder();
			for (int j = 0; j < baseFeatures; j++) {
				if (j < i || j > i)
					mask.append('0');
				else
					mask.append('1');
			}
			double weight = evaluateMaskWeight(mask.toString(), dataset, outputEncoder);
			ret.add(new Candidate(mask.toString(), weight));
		}
		
		return ret;
	}

	@Override
	public String[] getManagedBlockOptions() {
		return new String[]{"mask"};
	}

}
