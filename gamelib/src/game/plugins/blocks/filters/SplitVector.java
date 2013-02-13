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

public class SplitVector extends Filter {

	@Override
	public Data transform(Data input) {
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
	public boolean isCompatible(DatasetTemplate template) {
		if (template.sourceTemplate == null)
			return false;
		for(ValueTemplate tpl: template.sourceTemplate)
			if (!(tpl instanceof VectorTemplate))
				return false;
		return true;
	}

	@Override
	protected void updateOutputTemplate() {
		ElementTemplate tpl = new ElementTemplate();
		for (ValueTemplate template: datasetTemplate.sourceTemplate) {
			int dimension = template.getContent("dimension");
			for(int i = 0; i < dimension; i++) {
				VectorTemplate value = new VectorTemplate();
				value.dimension = 1;
				tpl.add(value);
			}
		}
		setContent("outputTemplate", tpl);
	}

}
