package game.plugins.algorithms;

import game.core.Block;
import game.core.Dataset;
import game.core.Instance;
import game.core.TrainingAlgorithm;
import game.plugins.classifiers.Criterion;
import game.plugins.classifiers.DecisionTree;
import game.plugins.classifiers.Node;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DiscriminantTree extends TrainingAlgorithm<DecisionTree> {
	
	public int minimumSize = 100;
	
	public int featuresPerNode = 0;
	
	@Override
	public boolean isCompatible(Block object) {
		return super.isCompatible(object) && object.getContent("template.outputTemplate.labels", List.class).size() == 2;
	}

	@Override
	protected void train(Dataset dataset) {
		Block inputEncoder = block.getParent();
		/*
		DiscriminantFunction discriminant = new DiscriminantFunction();
		discriminant.dimensions = 1;
		*/
		Node root = recursiveTrain(inputEncoder/*, discriminant*/, dataset);
		
		block.setContent("root", root);
	}

	private Node recursiveTrain(Block inputEncoder/*, DiscriminantFunction discriminant*/, Dataset dataset) {
		Node node = new Node();
		
		if (dataset.size() < minimumSize) { // - 2 < inputEncoder.getFeatureNumber()) {
			C45Like subalgo = new C45Like();
			subalgo.setContent("block", this.block);
			subalgo.setContent("featuresPerNode", featuresPerNode);
			subalgo.recursiveTrain(dataset, node);
			return node;
		}
		/*
		RealVector transform = discriminant.getTransform(dataset, inputEncoder).getRowVector(0);
		double threshold = getThreshold(transform, inputEncoder, dataset);
		
		DiscriminantCriterion criterion = new DiscriminantCriterion(transform, threshold);
		*/
		DiscriminantCriterion criterion = new DiscriminantCriterion(dataset, inputEncoder, inputEncoder.getFeatureNumber());
		node.setCriterion(criterion);
		
		for(Dataset split: split(dataset, criterion)) {
			if (split.isEmpty() || split.size() == dataset.size()) {
				C45Like subalgo = new C45Like();
				subalgo.setContent("block", this.block);
				subalgo.setContent("featuresPerNode", featuresPerNode);
				subalgo.recursiveTrain(dataset, node);
				return node;
			}
			node.getChildren().add(recursiveTrain(inputEncoder/*, discriminant*/, split));
		}
		
		return node;
	}
/*
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
		
		return (zp + zn) / 2;
	}
*/
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
/*
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
*/
	@Override
	protected String getManagedPropertyNames() {
		return "root";
	}

}
