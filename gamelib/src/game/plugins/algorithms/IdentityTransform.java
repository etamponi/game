package game.plugins.algorithms;

import game.core.Dataset;
import game.core.TrainingAlgorithm;
import game.plugins.pipes.LinearTransform;

import org.apache.commons.math3.linear.MatrixUtils;

public class IdentityTransform extends TrainingAlgorithm<LinearTransform> {

	@Override
	protected void train(Dataset dataset) {
		block.setContent("transform", MatrixUtils.createRealIdentityMatrix(block.getParent(0).getFeatureNumber()));
	}

	@Override
	protected String getManagedPropertyNames() {
		return "transform";
	}

}
