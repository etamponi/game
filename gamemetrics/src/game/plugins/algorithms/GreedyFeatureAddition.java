package game.plugins.algorithms;

import game.core.Block;
import game.core.Dataset;
import game.core.Dataset.SampleIterator;
import game.core.InstanceTemplate;
import game.core.TrainingAlgorithm;
import game.core.blocks.Encoder;
import game.plugins.correlation.CorrelationCoefficient;
import game.plugins.correlation.CorrelationRatio;
import game.plugins.datasetbuilders.CSVDatasetBuilder;
import game.plugins.datatemplates.LabelTemplate;
import game.plugins.datatemplates.VectorTemplate;
import game.plugins.encoders.IntegerEncoder;
import game.plugins.encoders.VectorEncoder;
import game.plugins.pipes.FeatureSelection;
import game.utils.Log;

import java.io.File;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.math3.linear.RealVector;

public class GreedyFeatureAddition extends TrainingAlgorithm<FeatureSelection> {
	
	public double featurePercent = 0.5;
	
	public CorrelationCoefficient coefficient;

	public double selectionThreshold = 1e-3;
	
	public int runs = 10;
	
	public double runPercent = 0.5;

	public int maxSelection = 5;

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
		
		public double getWeight() {
			return weight;
		}
		
		@Override
		public int compareTo(Candidate other) {
			return -Double.compare(this.weight, other.weight);
		}
		
	}

	@Override
	protected void train(Dataset dataset) {
		int baseFeatures = block.getParent(0).getFeatureNumber();
		int finalFeatures = (int) (featurePercent*baseFeatures);
		
		IntegerEncoder outputEncoder = new IntegerEncoder();
		outputEncoder.setOption("template", dataset.template.outputTemplate);
		
		SortedSet<Candidate> candidates = initCandidates(dataset, outputEncoder);
		SortedSet<Candidate> bestCandidates = null;
		for(int features = 1; features <= finalFeatures; features++) {
			bestCandidates = selectBestCandidates(candidates);
			Log.write(this, "Selected best %d candidates with weight %5.2f", bestCandidates.size(), 100*bestCandidates.first().getWeight());
			
			for(Candidate bestCandidate: bestCandidates) {
				int index = bestCandidate.getMask().indexOf('0');
				while(index != -1) {
					String newMask = bestCandidate.getMask().substring(0, index) + "1" + bestCandidate.getMask().substring(index+1);
					double newWeight = evaluateMaskWeight(newMask, dataset, outputEncoder);
					
					candidates.add(new Candidate(newMask, newWeight));
					
					index = bestCandidate.getMask().indexOf('0', index+1);
				}
			}
		}
		block.mask = bestCandidates.first().getMask();
	}

	private SortedSet<Candidate> selectBestCandidates(SortedSet<Candidate> candidates) {
		SortedSet<Candidate> ret = new TreeSet<>();
		for(Candidate candidate: candidates) {
			if (candidates.first().getWeight() - candidate.getWeight() <= selectionThreshold)
				ret.add(candidate);
			else
				break;
			if (ret.size() == maxSelection)
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
	
	public static void main(String... args) {
		InstanceTemplate template = new InstanceTemplate();
		template.setOption("inputTemplate", new VectorTemplate());
		template.setOption("outputTemplate", new LabelTemplate());
		template.setOption("inputTemplate.dimension", 47);
		template.setOption("outputTemplate.labels.0", "pd");
		template.setOption("outputTemplate.labels.1", "snp");
		
		CSVDatasetBuilder builder = new CSVDatasetBuilder();
		builder.setOption("file", new File("../gamegui/sampledata/HumVar.txt"));
		builder.setOption("template", template);
		builder.setOption("startIndex", 0);
		builder.setOption("instanceNumber", 3000);
		builder.setOption("shuffle", false);
		
		Dataset dataset = builder.buildDataset();
		
		VectorEncoder encoder = new VectorEncoder();
		encoder.setOption("template", template.inputTemplate);
		
		GreedyFeatureAddition ta = new GreedyFeatureAddition();
		ta.setOption("coefficient", new CorrelationRatio());
		ta.setOption("coefficient.samples", 1500);
		
		FeatureSelection selection = new FeatureSelection();
		selection.setOption("trainingAlgorithm", ta);
		selection.setOption("parents.0", encoder);
		
		ta.train(dataset);
		
		System.out.println(selection.mask);
	}

}
