package game.plugins.blocks.pipes;

import game.core.Data;
import game.core.Element;
import game.core.ElementTemplate;
import game.core.ValueTemplate;
import game.core.blocks.Pipe;
import game.plugins.valuetemplates.VectorTemplate;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

public class SplitVector extends Pipe {

	@Override
	protected Data transduce(Data input) {
		Data ret = new Data();
		for(Element e: input) {
			Element out = new Element();
			for (Object o: e) {
				RealVector v = (RealVector) o;
				for(int i = 0; i < v.getDimension(); i++)
					out.add(new ArrayRealVector(new double[]{v.getEntry(i)}));
				ret.add(out);
			}
		}
		return ret;
	}

	@Override
	public boolean supportsInputTemplate(ElementTemplate inputTemplate) {
		for(ValueTemplate template: inputTemplate)
			if (!(template instanceof VectorTemplate))
				return false;
		return true;
	}

	@Override
	protected void setup() {
		ElementTemplate tpl = new ElementTemplate();
		ElementTemplate parentTemplate = getParentTemplate();
		if (parentTemplate != null && supportsInputTemplate(parentTemplate)) {
			for (ValueTemplate template: parentTemplate) {
				int dimension = template.getContent("dimension");
				for(int i = 0; i < dimension; i++) {
					VectorTemplate value = new VectorTemplate();
					value.dimension = 1;
					value.name = "D"+i;
					tpl.add(value);
				}
			}
		}
		setContent("outputTemplate", tpl);
	}

}
