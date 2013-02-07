package game.plugins.blocks.pipes;

import game.core.Data;
import game.core.Element;
import game.core.ElementTemplate;
import game.core.ValueTemplate;
import game.core.blocks.Pipe;
import game.plugins.valuetemplates.VectorTemplate;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

public class JoinVector extends Pipe {

	@Override
	protected Data transduce(Data input) {
		Data ret = new Data();
		for(Element e: input) {
			RealVector out = new ArrayRealVector();
			for (Object o: e) {
				RealVector v = (RealVector) o;
				for(int i = 0; i < v.getDimension(); i++)
					out = out.append(v.getEntry(i));
			}
			ret.add(new Element(out));
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
			int dimension = 0;
			for (ValueTemplate template: parentTemplate) {
				dimension += (int)template.getContent("dimension");
			}
			VectorTemplate vec = new VectorTemplate();
			vec.dimension = dimension;
			tpl.add(vec);
		}
		setContent("outputTemplate", tpl);
	}

}
