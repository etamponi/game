package game.plugins.blocks.filters;

import game.core.Data;
import game.core.DatasetTemplate;
import game.core.ElementTemplate;
import game.core.ValueTemplate;
import game.core.blocks.Filter;
import game.plugins.valuetemplates.VectorTemplate;

public class VectorToStringList extends Filter {
	
	@InName
	public String format = "%.2f";

	@Override
	public String compatibilityError(DatasetTemplate template) {
		if (template.sourceTemplate == null || template.sourceTemplate.isEmpty())
			return "sourceTemplate is null or empty";
		
		for (ValueTemplate tpl: template.sourceTemplate) {
			if (!(tpl instanceof VectorTemplate))
				return "sourceTemplate must contain only VectorTemplates";
		}
		return null;
	}

	@Override
	public Data transform(Data input) {
		// TODO Auto-generated method stub
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
