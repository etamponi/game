package game.plugins.algorithms;

import game.core.Block;
import game.core.Dataset;
import game.core.Dataset.SampleIterator;
import game.core.Instance;
import game.core.Sample;
import game.core.TrainingAlgorithm;
import game.plugins.classifiers.Criterion;
import game.plugins.classifiers.DecisionTree;
import game.plugins.classifiers.Node;
import game.plugins.encoders.BooleanEncoder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

public class DiscriminantTree extends TrainingAlgorithm<DecisionTree> {
	
	public int minimumSize = 100;
	
	public int featuresPerNode = 0;
	
	@Override
	public boolean isCompatible(Block object) {
		return super.isCompatible(object) && object.getContent("template.outputTemplate.labels", List.class).size() == 2;
	}

	public static class DiscriminantCriterion extends Criterion {
		
		private RealVector transform;
		
		private double threshold;
		
		public DiscriminantCriterion(RealVector transform, double threshold) {
			super();
			this.transform = transform;
			this.threshold = threshold;
		}

		@Override
		public int decide(RealVector input) {
			if (transform.dotProduct(input) <= threshold)
				return 0;
			else
				return 1;
		}
		
	}

	@Override
	protected void train(Dataset dataset) {
		Block inputEncoder = block.getParent();
		
		DiscriminantFunction discriminant = new DiscriminantFunction();
		discriminant.dimensions = 1;
		
		Node root = recursiveTrain(inputEncoder, discriminant, dataset);
		
		block.setContent("root", root);
	}

	private Node recursiveTrain(Block inputEncoder, DiscriminantFunction discriminant, Dataset dataset) {
		Node current = new Node();
		
		if (dataset.size() < minimumSize) { // - 2 < inputEncoder.getFeatureNumber()) {
			C45Like subalgo = new C45Like();
			subalgo.setContent("block", this.block);
			subalgo.setContent("featuresPerNode", featuresPerNode);
			subalgo.recursiveTrain(dataset, current);
//			current.setProbability(getProbability(dataset));
			return current;
		}
		
		RealVector transform = discriminant.getTransform(dataset, inputEncoder).getRowVector(0);
		double threshold = getThreshold(transform, inputEncoder, dataset);
		
		DiscriminantCriterion criterion = new DiscriminantCriterion(transform, threshold);
		current.setCriterion(criterion);
		
		for(Dataset split: split(dataset, criterion)) {
			if (split.isEmpty()) {
				current.getChildren().clear();
				current.setProbability(getProbability(dataset));
				return current;
			}
			current.getChildren().add(recursiveTrain(inputEncoder, discriminant, split));
		}
		
		return current;
	}
	
	private double getThreshold(RealVector transform, Block inputEncoder, Dataset dataset) {
		BooleanEncoder outputEncoder = new BooleanEncoder();
		outputEncoder.setContent("template", dataset.getTemplate().outputTemplate);
		
		double zp = 0; double cp = 0;
		double zn = 0; double cn = 0;
		
		SampleIterator it = dataset.encodedSampleIterator(inputEncoder, outputEncoder, false);
		while(it.hasNext()) {
			Sample sample = it.next();
			double z = transform.dotProduct(sample.getEncodedInput());
			if (sample.getOutput().equals(outputEncoder.positiveLabel())) {
				zp += z;
				cp++;
			} else {
				zn += z;
				cn++;
			}
		}
		
		zp = zp / cp;
		zn = zn / cn;
		
		return (zp + zn)/2;
	}

	private List<Dataset> split(Dataset dataset, Criterion criterion) {
		List<Dataset> splits = new ArrayList<>();
		splits.add(new Dataset(block.template));
		splits.add(new Dataset(block.template));
		
		Iterator<Instance> it = dataset.iterator();
		while(it.hasNext()) {
			Instance instance = it.next();
			int split = criterion.decide(block.getParent().transform(instance.getInput()).getElement(0));
			splits.get(split).add(instance);
		}
		
		return splits;
	}
	
	private RealVector getProbability(Dataset dataset) {
		Map<String, Double> prob = new HashMap<>();
		Iterator<Instance> it = dataset.iterator();
		double sum = 0;
		while(it.hasNext()) {
			Instance i = it.next();
			String key = i.getOutput().get(0).toString();
			if (!prob.containsKey(key))
				prob.put(key, 0.0);
			prob.put(key, prob.get(key)+1.0);
			sum++;
		}
		
		List<String> labels = block.template.outputTemplate.getContent("labels");
		
		RealVector ret = new ArrayRealVector(labels.size());
		for(String key: prob.keySet()) {
			ret.setEntry(labels.indexOf(key), prob.get(key)/sum);
		}

		return ret;
	}

	@Override
	protected String getManagedPropertyNames() {
		return "root";
	}

}
