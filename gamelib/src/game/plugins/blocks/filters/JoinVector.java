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

public class JoinVector extends Filter {

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
	public String compatibilityError(DatasetTemplate template) {
		if (template.sourceTemplate == null)
			return "sourceTemplate is null";
		for(ValueTemplate tpl: template.sourceTemplate)
			if (!(tpl instanceof VectorTemplate))
				return "every ValueTemplate in sourceTemplate must be a VectorTemplate";
		return null;
	}

	@Override
	protected void updateOutputTemplate() {
		ElementTemplate tpl = new ElementTemplate();
		int dimension = 0;
		for (ValueTemplate template: datasetTemplate.sourceTemplate) {
			dimension += (int)template.getContent("dimension");
		}
		VectorTemplate vec = new VectorTemplate();
		vec.dimension = dimension;
		tpl.add(vec);
		setContent("outputTemplate", tpl);
	}

}
