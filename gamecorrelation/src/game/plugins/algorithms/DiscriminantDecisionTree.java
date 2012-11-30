package game.plugins.algorithms;

import game.core.Block;
import game.core.Dataset;
import game.core.Instance;
import game.core.TrainingAlgorithm;
import game.core.blocks.Encoder;
import game.plugins.classifiers.Criterion;
import game.plugins.classifiers.DecisionTree;
import game.plugins.classifiers.Node;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.util.ArithmeticUtils;

import com.ios.ErrorCheck;

public class DiscriminantDecisionTree extends TrainingAlgorithm<DecisionTree> {
	
	public int featuresPerPlane = 1;
	
	public int planesPerNode = 0;
	
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
	
	@Override
	protected void train(Dataset dataset) {
		Node root = recursiveTrain(dataset);
		
		block.setContent("root", root);
	}
	
	public static class InstanceComparator implements Comparator<Instance> {
		
		private Block inputEncoder;
		private int feature;

		public InstanceComparator(Block inputEncoder, int feature) {
			this.inputEncoder = inputEncoder;
			this.feature = feature;
		}

		@Override
		public int compare(Instance o1, Instance o2) {
			double value1 = inputEncoder.transform(o1.getInput()).getElement(0).getEntry(feature);
			double value2 = inputEncoder.transform(o2.getInput()).getElement(0).getEntry(feature);
			return Double.compare(value1, value2);
		}
		
	}
	
	private Node recursiveTrain(Dataset dataset) {
		Node node = new Node();
		
		if (dataset.size() <= featuresPerPlane + 2) {
			node.setProbability(probability(dataset));
			return node;
		}
		
		if (information(dataset) == 0) {
			node.setProbability(probability(dataset));
			return node;
		}
		
		List<Dataset> splits = new ArrayList<>();
		Criterion criterion = bestCriterion(dataset, splits);
		if (criterion == null) {
			node.setProbability(probability(dataset));
			return node;
		}
		
		node.setCriterion(criterion);
		for(Dataset split: splits) {
			node.getChildren().add(recursiveTrain(split));
		}
		
		return node;
	}
	
	private Criterion bestCriterion(Dataset dataset, List<Dataset> finalSplits) {
		Criterion bestCriterion = null;
		double bestGainRatio = 0;
		
		
		
		return bestCriterion;
	}

	private double information(Dataset dataset) {
		return information(probability(dataset));
	}
	
	private double informationGain(Dataset dataset, List<Dataset> splits) {
		double gain = information(dataset);
		for(Dataset split: splits)
			gain -= information(split) * split.size() / dataset.size();
		return gain;
	}
	
	private double splitInformation(Dataset dataset, List<Dataset> splits) {
		double splitInfo = 0;
		for(Dataset split: splits)
			splitInfo -= Math.log(1.0 * split.size() / dataset.size()) * split.size() / dataset.size();
		return splitInfo;
	}
	
	private double gainRatio(Dataset dataset, List<Dataset> splits) {
		return informationGain(dataset, splits) / splitInformation(dataset, splits);
	}
		
	private double information(RealVector probability) {
		double info = 0;
		
		for(int j = 0; j < probability.getDimension(); j++) {
			double p = probability.getEntry(j);
			if (p == 0 || p == 1)
				continue;
			
			info -= p * Math.log(p);
		}
		
		return info;
	}

	private RealVector probability(Dataset dataset) {
		Encoder outputEncoder = block.outputEncoder;
		
		RealVector ret = new ArrayRealVector(outputEncoder.getFeatureNumber());
		for(Instance instance: dataset)
			ret = ret.add(outputEncoder.transform(instance.getOutput()).getElement(0));
		ret.mapDivideToSelf(dataset.size());
		
		return ret;
	}

	@Override
	protected String getManagedPropertyNames() {
		return "root";
	}

}
