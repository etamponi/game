package game.plugins.pipes;

import org.apache.commons.math3.linear.RealVector;

import com.ios.IObject;
import com.ios.errorchecks.RangeCheck;
import com.ios.errorchecks.RangeCheck.Bound;

public abstract class CombinationFunction extends IObject {
	
	public int operands = 2;
	
	public CombinationFunction() {
		addErrorCheck("operands", new RangeCheck(2, Bound.LOWER));
	}
	
	public abstract double evaluate(RealVector values);
	
}