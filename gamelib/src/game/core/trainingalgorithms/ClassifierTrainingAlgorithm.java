package game.core.trainingalgorithms;

import game.core.Block;
import game.core.ElementTemplate;
import game.core.TrainingAlgorithm;
import game.core.blocks.Classifier;
import game.plugins.blocks.decoders.ProbabilityDecoder;
import game.plugins.valuetemplates.LabelTemplate;
import game.plugins.valuetemplates.VectorTemplate;

import com.ios.Property;
import com.ios.triggers.MasterSlaveTrigger;

public abstract class ClassifierTrainingAlgorithm<C extends Classifier> extends TrainingAlgorithm<C> {

	public ClassifierTrainingAlgorithm() {
		addTrigger(new MasterSlaveTrigger(this, "block.datasetTemplate", "block.outputTemplate") {
			private ClassifierTrainingAlgorithm self = ClassifierTrainingAlgorithm.this;
			@Override
			public void updateSlave(Property slave, Object content) {
				if (self.block == null)
					return;
				if (self.block.compatibilityError(self.block.datasetTemplate) == null) {
					int dimension = self.block.datasetTemplate.targetTemplate.getSingleton(LabelTemplate.class).labels.size();
					self.block.setContent("outputTemplate", new ElementTemplate(new VectorTemplate(dimension)));
				} else {
					self.block.setContent("outputTemplate", new ElementTemplate(new VectorTemplate(0)));
				}
			}
		});
		
		addTrigger(new MasterSlaveTrigger<Block>(this, "block", "block.decoder") {
			private boolean listening = true;
			@Override
			public void updateSlave(Property slave, Block content) {
				if (listening) {
					listening = false;
					if (content != null)
						content.setContent("decoder", new ProbabilityDecoder());
					listening = true;
				}
			}
		});
	}
	
}
