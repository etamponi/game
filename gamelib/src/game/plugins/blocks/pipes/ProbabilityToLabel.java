package game.plugins.blocks.pipes;

import game.core.Data;
import game.core.Element;
import game.plugins.datatemplates.LabelTemplate;
import game.plugins.datatemplates.VectorTemplate;

import org.apache.commons.math3.linear.RealVector;

import com.ios.ErrorCheck;

public class ProbabilityToLabel extends VectorToLabel {
	
	public ProbabilityToLabel() {
		addErrorCheck("", new ErrorCheck<ProbabilityToLabel>() {
			@Override
			public String getError(ProbabilityToLabel self) {
				VectorTemplate parentTemplate = self.getParentTemplate() == null ? null : self.getParentTemplate().getSingleton(VectorTemplate.class);
				if (parentTemplate == null)
					return null;
				if (parentTemplate.dimension != self.outputTemplate.getSingleton(LabelTemplate.class).labels.size())
					return "needs exactly " + parentTemplate.dimension + " labels";
				else
					return null;
			}
		});
	}

	@Override
	protected Data transduce(Data input) {
		Data output = new Data();
		for (Element element: input) {
			RealVector vec = (RealVector) element.get(0);
			output.add(new Element(labels.get(vec.getMaxIndex())));
		}
		return output;
	}

	@Override
	protected void setup() {
		// nothing to do
	}

}
