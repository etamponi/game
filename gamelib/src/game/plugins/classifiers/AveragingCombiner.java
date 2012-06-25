package game.plugins.classifiers;

import game.core.Dataset;
import game.core.Encoding;
import game.core.InstanceTemplate;
import game.core.blocks.Combiner;

import java.util.List;

public class AveragingCombiner extends Combiner {
	
	public boolean trained = false;
	
	public AveragingCombiner() {
		setInternalOptions("trained");
	}

	@Override
	public boolean isCompatible(InstanceTemplate object) {
		return true;
	}

	@Override
	public boolean isTrained() {
		return trained;
	}

	@Override
	protected double train(Dataset trainingSet) {
		trained = true;
		return 1.0;
	}

	@Override
	protected Encoding transform(Object inputData) {
		List<Encoding> encs = getParentsEncodings(inputData);
		
		if (encs.size() == 1)
			return encs.get(0);
		
		Encoding ret = new Encoding();
		double normalization = 1.0 / encs.size();
		
		for(Encoding enc: encs)
			sumEncodings(ret, enc);
		ret.mulBy(normalization);
		
		return ret;
	}
	
	private void sumEncodings(Encoding to, Encoding from) {
		if (to.isEmpty()) {
			to.addAll(from);
			return;
		}
		for(int k = 0; k < to.length(); k++) {
			double[] element = to.get(k);
			double[] other = from.get(k);
			for (int i = 0; i < to.getElementSize(); i++)
				element[i] += other[i];
		}
	}

}
