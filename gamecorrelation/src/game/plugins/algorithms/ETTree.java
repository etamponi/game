package game.plugins.algorithms;

import game.core.Block;
import game.core.Dataset;
import game.plugins.classifiers.Criterion;

import java.util.List;

public class ETTree extends C45Like {
	
	public int minimumSize = 300;
	
	@Override
	public boolean isCompatible(Block object) {
		return super.isCompatible(object) && object.getContent("template.outputTemplate.labels", List.class).size() == 2;
	}

	@Override
	protected Criterion bestCriterion(Dataset dataset) {
		if (dataset.size() >= minimumSize) {
			Block inputEncoder = block.getParent();

			DiscriminantCriterion criterion = new DiscriminantCriterion(dataset, inputEncoder);
			
			return criterion;
		} else {
			return super.bestCriterion(dataset);
		}
	}

	@Override
	protected String getManagedPropertyNames() {
		return "root";
	}

}
