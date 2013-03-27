package game.plugins.valuetemplates;

import game.core.ValueTemplate;

import java.util.List;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

import com.ios.IList;

public class TaxonomyTemplate extends ValueTemplate<RealVector> {
	
	public IList<String> labels;
	
	public TaxonomyTemplate() {
		setContent("labels", new IList<>(String.class));
	}
	
	public TaxonomyTemplate(String... ls) {
		setContent("labels", new IList<>(String.class, ls));
	}

	@Override
	public int getDescriptionLength() {
		return labels.size();
	}

	@Override
	public RealVector loadValue(List<String> description) {
		RealVector ret = new ArrayRealVector(labels.size());
		int i = 0;
		for(String e: description) {
			if (e.equals("0"))
				ret.setEntry(i, 0);
			else
				ret.setEntry(i, 1);
			i++;
		}
		return ret;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof TaxonomyTemplate) {
			TaxonomyTemplate o = (TaxonomyTemplate)other;
			return labels == null ? o.labels == null : labels.equals(o.labels);
		} else {
			return false;
		}
	}

}
