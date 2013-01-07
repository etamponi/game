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

		Node root = recursiveTrain(inputEncoder, dataset);
		
		block.setContent("root", root);
	}

	private Node recursiveTrain(Block inputEncoder, Dataset dataset) {
		Node node = new Node();
		
		if (dataset.size() < minimumSize) {
			C45Like subalgo = new C45Like();
			subalgo.setContent("block", this.block);
			subalgo.setContent("featuresPerNode", featuresPerNode);
			subalgo.recursiveTrain(dataset, node);
			return node;
		}

		DiscriminantCriterion criterion = new DiscriminantCriterion(dataset, inputEncoder);
		node.setCriterion(criterion);
		
		for(Dataset split: split(dataset, criterion)) {
			if (split.isEmpty() || split.size() == dataset.size()) {
				node.getChildren().clear();
				C45Like subalgo = new C45Like();
				subalgo.setContent("block", this.block);
				subalgo.setContent("featuresPerNode", featuresPerNode);
				subalgo.recursiveTrain(dataset, node);
				return node;
			}
			node.getChildren().add(recursiveTrain(inputEncoder, split));
		}
		
		return node;
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

	@Override
	protected String getManagedPropertyNames() {
		return "root";
	}

}
