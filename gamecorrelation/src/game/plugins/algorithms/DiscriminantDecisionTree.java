package game.plugins.algorithms;

import game.core.Block;
import game.core.Dataset;
import game.core.Experiment;
import game.core.Dataset.SampleIterator;
import game.core.Sample;
import game.core.TrainingAlgorithm;
import game.core.blocks.Encoder;
import game.plugins.classifiers.Criterion;
import game.plugins.classifiers.DecisionTree;
import game.plugins.classifiers.Node;
import game.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.util.ArithmeticUtils;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;

import com.ios.ErrorCheck;

public class DiscriminantDecisionTree extends TrainingAlgorithm<DecisionTree> {
	
	public boolean random = false;
	
	public int featuresPerPlane = 1;
	
	public int planesPerNode = 0;
	
	public double noiseSd = 1e-8;

	public boolean useCentroids;

	public boolean useProportionalThreshold;
	
	public DiscriminantDecisionTree() {
		addErrorCheck("", new ErrorCheck<DiscriminantDecisionTree>() {
			@Override
			public String getError(DiscriminantDecisionTree value) {
				Block encoder = value.getContent("block.parents.0");
				if (encoder == null)
					return null;
				
				if (featuresPerPlane < 1 || featuresPerPlane > encoder.getFeatureNumber())
					return "must be between 1 and " + encoder.getFeatureNumber();
				
				long combs = ArithmeticUtils.binomialCoefficient(encoder.getFeatureNumber(), featuresPerPlane);
				if (planesPerNode > combs)
					return "must be less than " + combs;
				
				return null;
			}
		});
	}
	
	public static class SampleDataset extends ArrayList<Sample> {
		
		public SampleDataset() {
			
		}
		
		public SampleDataset(Dataset dataset, Block inputEncoder, Block outputEncoder) {
			SampleIterator it = dataset.encodedSampleIterator(inputEncoder, outputEncoder, false);
			while(it.hasNext())
				this.add(it.next());
		}
		
	}
	
	@Override
	protected void train(Dataset dataset) {
		SampleDataset samples = new SampleDataset(dataset, block.getParent(), block.outputEncoder);
		
		int planes;
		if (planesPerNode == 0) {
			long combs = ArithmeticUtils.binomialCoefficient(block.getParent().getFeatureNumber(), featuresPerPlane);
			if (random) {
				planes = (int)(Utils.log2(combs) + 1);
			} else {
				planes = (int)combs;
			}
		} else {
			planes = planesPerNode;
		}
		
		System.out.println("Planes per node: " + planes);
		Node root = recursiveTrain(samples, planes, 1);
		
		block.setContent("root", root);
	}
	
	private Node recursiveTrain(SampleDataset samples, int planes, int level) {
//		updateStatus(getProgress(), "training node at level " + level + " with " + planes + " planes of " + featuresPerPlane + " features with " + samples.size() + " samples.");
		Node node = new Node();
		
		if (samples.size() <= featuresPerPlane + 2) {
			node.setProbability(probability(samples));
			return node;
		}
		
		if (information(samples) == 0) {
			node.setProbability(probability(samples));
			return node;
		}
		
		CriterionWithGainAndSplits criterion = bestCriterion(samples, planes);
		if (criterion.criterion == null) {
			node.setProbability(probability(samples));
			return node;
		}
		
		node.setCriterion(criterion.criterion);
		for(SampleDataset split: criterion.splits) {
			node.getChildren().add(recursiveTrain(split, planes, level+1));
		}
		
		return node;
	}
	
	private static class ZSample implements Comparable<ZSample> {
		private Sample sample;
		private double z;
		
		public ZSample(Sample sample, RealVector plane) {
			this.sample = sample;
			this.z = plane.dotProduct(sample.getEncodedInput());
		}

		@Override
		public int compareTo(ZSample o) {
			return Double.compare(this.z, o.z);
		}
	}
	
	private static class CriterionWithGainAndSplits {
		private Criterion criterion;
		private double gainRatio;
		private List<SampleDataset> splits;
		
		public CriterionWithGainAndSplits(Criterion criterion, double gainRatio, List<SampleDataset> splits) {
			this.criterion = criterion;
			this.gainRatio = gainRatio;
			this.splits = splits;
		}
	}
	
	private CriterionWithGainAndSplits bestCriterion(SampleDataset samples, int planes) {
		int inputFeatures = block.getParent().getFeatureNumber();

		RealMatrix H = new Array2DRowRealMatrix(inputFeatures, inputFeatures);
		RealMatrix E = new Array2DRowRealMatrix(inputFeatures, inputFeatures);
		evaluateMatrices(samples, H, E);
		
		CriterionWithGainAndSplits bestCriterion = new CriterionWithGainAndSplits(null, 0, null);
		
		List<Integer> features = Utils.range(0, inputFeatures);
		Collections.shuffle(features, Experiment.getRandom());
		
		List<Integer> indices = Utils.range(0, featuresPerPlane);
		int[] selectedFeatures = new int[featuresPerPlane];
		for(int index = 0; index < planes; index++) {
			selectFeatures(selectedFeatures, indices, features);
			
			RealVector plane = evaluatePlane(H, E, selectedFeatures);
			if (plane != null) {
				CriterionWithGainAndSplits criterion = useCentroids ?
						evaluateCriterionUsingCentroids(samples, plane)
						: evaluateCriterionWithGainAndSplits(samples, plane);
				if (criterion.gainRatio > bestCriterion.gainRatio) {
					bestCriterion = criterion;
				}
			}
			
			nextPermutation(indices, inputFeatures);
		}
		
		return bestCriterion;
	}
	
	private CriterionWithGainAndSplits evaluateCriterionUsingCentroids(SampleDataset samples, RealVector plane) {
		List<ZSample> list = new ArrayList<>(samples.size());
		List<Double> centroids = new ArrayList<>();
		centroids.add(0d); centroids.add(0d);
		double count0 = 0;
		for(Sample sample: samples) {
			ZSample zsample = new ZSample(sample, plane);
			list.add(zsample);
			int index = (int) sample.getEncodedOutput().getEntry(1);
			centroids.set(index, centroids.get(index)+zsample.z);
			if (index == 0)
				count0++;
		}
		centroids.set(0, centroids.get(0)/count0);
		centroids.set(1, centroids.get(1)/(list.size()-count0));
		double threshold = (useProportionalThreshold ? count0 / list.size() : 0.5) * (centroids.get(0) + centroids.get(1));
		List<SampleDataset> splits = new ArrayList<>();
		splits.add(new SampleDataset());
		splits.add(new SampleDataset());
		for(ZSample zsample: list) {
			if (zsample.z <= threshold)
				splits.get(0).add(zsample.sample);
			else
				splits.get(1).add(zsample.sample);
		}
		return new CriterionWithGainAndSplits(new PlaneCriterion(plane, threshold), gainRatio(samples, splits), splits);
	}

	public static class PlaneCriterion extends Criterion {
		
		private RealVector plane;
		private double threshold;
		
		public PlaneCriterion(RealVector plane, double threshold) {
			this.plane = plane;
			this.threshold = threshold;
		}

		@Override
		public int decide(RealVector input) {
			if (plane.dotProduct(input) <= threshold)
				return 0;
			else
				return 1;
		}
		
	}
	
	private CriterionWithGainAndSplits evaluateCriterionWithGainAndSplits(SampleDataset samples, RealVector plane) {
		List<ZSample> list = new ArrayList<>(samples.size());
		for(Sample sample: samples)
			list.add(new ZSample(sample, plane));
		Collections.sort(list);
		
		List<SampleDataset> bestSplits = new ArrayList<>();
		bestSplits.add(null);bestSplits.add(null);
		double bestGainRatio = 0;
		double bestThreshold = 0;
		
		List<SampleDataset> splits = new ArrayList<>();
		splits.add(new SampleDataset());
		splits.add((SampleDataset) samples.clone());
		ZSample previous = list.get(0);
		for(int i = 1; i < list.size(); i++) {
			ZSample current = list.get(i);
			splits.get(0).add(previous.sample);
			splits.get(1).remove(previous.sample);
			if (previous.z < current.z && !previous.sample.getOutput().equals(current.sample.getOutput())) {
				double gainRatio = gainRatio(samples, splits);
				if (gainRatio > bestGainRatio) {
					bestGainRatio = gainRatio;
					bestThreshold = 0.5 * (previous.z + current.z);
					bestSplits.set(0, (SampleDataset) splits.get(0).clone());
					bestSplits.set(1, (SampleDataset) splits.get(1).clone());
				}
			}
			previous = current;
		}

//		System.out.println("Plane found    : " + plane);
//		System.out.println("Threshold found: " + bestThreshold);
		
		return new CriterionWithGainAndSplits(new PlaneCriterion(plane, bestThreshold), bestGainRatio, bestSplits);
	}

	private RealVector evaluatePlane(RealMatrix Hcomplete, RealMatrix Ecomplete, int[] selected) {
		Matrix H = new Matrix(Hcomplete.getSubMatrix(selected, selected).getData());
		Matrix E = new Matrix(Ecomplete.getSubMatrix(selected, selected).getData());
		
		try {
			Matrix M = E.inverse().times(H);
			EigenvalueDecomposition dec = M.eig();
			
			int maxIndex = 0;
			for(int i = 1; i < dec.getD().getRowDimension(); i++) {
				if (dec.getD().get(i, i) > dec.getD().get(maxIndex, maxIndex))
					maxIndex = i;
			}
			
			RealVector temp = new ArrayRealVector(dec.getV().transpose().getArray()[maxIndex]);
			
			RealVector plane = new ArrayRealVector(Hcomplete.getRowDimension());
			int j = 0;
			for(int i: selected)
				plane.setEntry(i, temp.getEntry(j++));
			return plane;
		} catch (RuntimeException ex) {
			System.err.println("Singular matrix");
			H.print(10, 2);
			E.print(10, 2);
			System.out.println(Arrays.toString(selected));
			return null;
		}
	}

	private void evaluateMatrices(SampleDataset samples, RealMatrix Hfinal, RealMatrix Efinal) {
		NormalDistribution distribution = new NormalDistribution(0, noiseSd);
		
		List< List<RealMatrix> > y_lists = new ArrayList<>();
		y_lists.add(new ArrayList<RealMatrix>());
		y_lists.add(new ArrayList<RealMatrix>());
		for(Sample sample: samples)
			y_lists.get((int)sample.getEncodedOutput().getEntry(1)).add(new Array2DRowRealMatrix(injectNoise(sample.getEncodedInput().toArray(), distribution)));
		List<RealMatrix> y_means = new ArrayList<>();
//		y_means.add(injectNoise(evaluateMean(y_lists.get(0)), distribution));
//		y_means.add(injectNoise(evaluateMean(y_lists.get(1)), distribution));
		y_means.add(evaluateMean(y_lists.get(0)));
		y_means.add(evaluateMean(y_lists.get(1)));
		RealMatrix y_mean = y_means.get(0).scalarMultiply(y_lists.get(0).size())
							.add(y_means.get(1).scalarMultiply(y_lists.get(1).size()))
							.scalarMultiply(1.0/samples.size());

		RealMatrix H = Hfinal.copy();
		RealMatrix E = Efinal.copy();
		
		for(int i = 0; i < 2; i++) {
			RealMatrix temp = y_means.get(i).subtract(y_mean);
			H = H.add(temp.multiply(temp.transpose()).scalarMultiply(y_lists.get(i).size()));
		}
		
		for(int i = 0; i < 2; i++) {
			for(int j = 0; j < y_lists.get(i).size(); j++) {
				RealMatrix temp = y_lists.get(i).get(j).subtract(y_means.get(i));
				E = E.add(temp.multiply(temp.transpose()));
			}
		}

		Hfinal.setSubMatrix(H.getData(), 0, 0);
		Efinal.setSubMatrix(E.getData(), 0, 0);
	}
	
	private double[] injectNoise(double[] v, NormalDistribution distribution) {
		for(int i = 0; i < v.length; i++)
			v[i] = v[i] * (1 + distribution.sample()) + distribution.sample();
		return v;
	}
/*
	private RealMatrix injectNoise(RealMatrix v, NormalDistribution distribution) {
		for(int i = 0; i < v.getRowDimension(); i++)
			v.setEntry(i, 0, v.getEntry(i, 0) * (1+distribution.sample()));
		return v;
	}
*/
	private RealMatrix evaluateMean(List<RealMatrix> list) {
		RealMatrix ret = new Array2DRowRealMatrix(list.get(0).getRowDimension(), 1);
		for(RealMatrix v: list)
			ret = ret.add(v);
		ret = ret.scalarMultiply(1.0/list.size());
		return ret;
	}

	private void selectFeatures(int[] selected, List<Integer> indices, List<Integer> features) {
		int i = 0;
		for(int index: indices)
			selected[i++] = features.get(index);
		Arrays.sort(selected);
	}
	
	private void nextPermutation(List<Integer> indices, int choices) {
		int pos = indices.size() - 1;
		int maxIndex = choices - 1;
		while(true) {
			if (pos < 0)
				return;
			if (indices.get(pos) < maxIndex) {
				indices.set(pos, indices.get(pos)+1);
				break;
			} else {
				pos--;
				maxIndex--;
			}
		}
		for(pos = pos+1; pos < indices.size(); pos++) {
			indices.set(pos, indices.get(pos-1)+1);
		}
	}

	private double information(SampleDataset samples) {
		return information(probability(samples));
	}
	
	private double informationGain(SampleDataset samples, List<SampleDataset> splits) {
		double gain = information(samples);
		for(SampleDataset split: splits)
			gain -= information(split) * split.size() / samples.size();
		return gain;
	}
	/*
	private double splitInformation(SampleDataset samples, List<SampleDataset> splits) {
		double splitInfo = 0;
		for(SampleDataset split: splits)
			splitInfo -= Math.log(1.0 * split.size() / samples.size()) * split.size() / samples.size();
		return splitInfo;
	}
	*/
	private double gainRatio(SampleDataset samples, List<SampleDataset> splits) {
		return informationGain(samples, splits); // / splitInformation(samples, splits);
	}
		
	private double information(RealVector probability) {
		double info = 0;
		
		for(int j = 0; j < probability.getDimension(); j++) {
			double p = probability.getEntry(j);
			if (p == 0 || p == 1)
				continue;
			
			info -= p * Utils.log2(p);
		}
		
		return info;
	}

	private RealVector probability(SampleDataset samples) {
		Encoder outputEncoder = block.outputEncoder;
		
		RealVector ret = new ArrayRealVector(outputEncoder.getFeatureNumber());
		for(Sample sample: samples)
			ret = ret.add(sample.getEncodedOutput());
		ret.mapDivideToSelf(samples.size());
		
//		System.out.println(ret);
		
		return ret;
	}

	@Override
	protected String getManagedPropertyNames() {
		return "root";
	}

}
