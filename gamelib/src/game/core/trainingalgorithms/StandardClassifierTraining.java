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

public abstract class StandardClassifierTraining<C extends Classifier> extends TrainingAlgorithm<C> {

	public StandardClassifierTraining() {
		addTrigger(new MasterSlaveTrigger(this, "block.datasetTemplate", "block.outputTemplate") {
			private StandardClassifierTraining self = StandardClassifierTraining.this;
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
		
		addTrigger(new MasterSlaveTrigger<Block>(this, "block", "block.decoder") {
			@Override
			public void updateSlave(Property slave, Block content) {
				if (content != null)
					content.setContent("decoder", new ProbabilityDecoder());
			}
		});
	}
	
}
