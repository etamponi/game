package game.plugins.blocks.filters;

import game.core.Data;
import game.core.DatasetTemplate;
import game.core.Element;
import game.core.ElementTemplate;
import game.core.ValueTemplate;
import game.core.blocks.Filter;
import game.plugins.valuetemplates.VectorTemplate;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

import com.ios.triggers.BoundProperties;

public class JoinVector extends Filter {
	
	public JoinVector() {
		addTrigger(new BoundProperties(this, "outputTemplate"));
	}

	@Override
	public Data transform(Data input) {
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
	public boolean isCompatible(DatasetTemplate template) {
		for(ValueTemplate tpl: template.sourceTemplate)
			if (!(tpl instanceof VectorTemplate))
				return false;
		return true;
	}

	@Override
	protected void updateOutputTemplate() {
		ElementTemplate tpl = new ElementTemplate();
		if (datasetTemplate != null && isCompatible(datasetTemplate)) {
			int dimension = 0;
			for (ValueTemplate template: datasetTemplate.sourceTemplate) {
				dimension += (int)template.getContent("dimension");
			}
			VectorTemplate vec = new VectorTemplate();
			vec.dimension = dimension;
			tpl.add(vec);
		}
		setContent("outputTemplate", tpl);
	}

}
