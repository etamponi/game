package game.core.trainingalgorithms;

import game.core.ElementTemplate;
import game.core.TrainingAlgorithm;
import game.core.blocks.Classifier;
import game.plugins.valuetemplates.LabelTemplate;
import game.plugins.valuetemplates.VectorTemplate;

import com.ios.Property;
import com.ios.triggers.MasterSlaveTrigger;

public abstract class StandardClassifierTrainingAlgorithm<C extends Classifier> extends TrainingAlgorithm<C> {

	public StandardClassifierTrainingAlgorithm() {
		addTrigger(new MasterSlaveTrigger(this, "block.datasetTemplate", "block.outputTemplate.0.dimension") {
			private StandardClassifierTrainingAlgorithm self = StandardClassifierTrainingAlgorithm.this;
			@Override
			public void updateSlave(Property slave, Object content) {
				if (self.block == null)
					return;
				if (self.block.isCompatible(self.block.datasetTemplate)) {
					int dimension = self.block.datasetTemplate.targetTemplate.getSingleton(LabelTemplate.class).labels.size();
					self.block.setContent("outputTemplate", new ElementTemplate(new VectorTemplate(dimension)));
				} else {
					self.block.setContent("outputTemplate", new ElementTemplate(new VectorTemplate(0)));
				}
			}
		});
	}
	
}
