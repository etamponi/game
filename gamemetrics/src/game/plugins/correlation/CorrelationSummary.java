package game.plugins.correlation;

import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

public class CorrelationSummary {
	
	private RealMatrix inputCorrelationMatrix;
	
	private RealMatrix ioCorrelationMatrix;
	
	private RealVector syntheticValues;

	public RealMatrix getInputCorrelationMatrix() {
		return inputCorrelationMatrix;
	}

	public void setInputCorrelationMatrix(RealMatrix inputCorrelationMatrix) {
		this.inputCorrelationMatrix = inputCorrelationMatrix;
	}

	public RealMatrix getIOCorrelationMatrix() {
		return ioCorrelationMatrix;
	}

	public void setIOCorrelationMatrix(RealMatrix ioCorrelationMatrix) {
		this.ioCorrelationMatrix = ioCorrelationMatrix;
	}

	public RealVector getSyntheticValues() {
		return syntheticValues;
	}

	public void setSyntheticValues(RealVector syntheticValues) {
		this.syntheticValues = syntheticValues;
	}
	
	@Override
	public String toString() {
		return syntheticValues.toString();
	}

}
