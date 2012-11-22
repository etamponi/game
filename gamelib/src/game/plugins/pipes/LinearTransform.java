package game.plugins.pipes;

import game.core.DataTemplate.Data;
import game.core.Encoding;
import game.core.blocks.Pipe;

import org.apache.commons.math3.linear.RealMatrix;

import com.ios.errorchecks.SizeCheck;

public class LinearTransform extends Pipe {
	
	public RealMatrix transform;
	
	public LinearTransform() {
		addErrorCheck("parents", new SizeCheck(1, 1));
	}

	@Override
	public Encoding transform(Data input) {
		Encoding inputEnc = getParentEncoding(0, input);
		RealMatrix prod = transform.multiply(inputEnc);
		return new Encoding(prod);
	}

	@Override
	public int getFeatureNumber() {
		if (transform != null)
			return transform.getRowDimension();
		else
			return 0;
	}

	@Override
	public FeatureType getFeatureType(int featureIndex) {
		return FeatureType.NUMERIC;
	}
	
}
