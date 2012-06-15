package game.plugins.classifiers;

import game.core.Dataset;
import game.core.Encoding;
import game.core.InstanceTemplate;
import game.core.blocks.Combiner;

import java.util.List;

public class AveragingCombiner extends Combiner {

	@Override
	public boolean isCompatible(InstanceTemplate object) {
		return true;
	}

	@Override
	public boolean isTrained() {
		return true;
	}

	@Override
	public boolean acceptsNewParents() {
		return true;
	}

	@Override
	protected double train(Dataset trainingSet) {
		throw new UnsupportedOperationException("You cannot train an AveragingCombiner");
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
		for(int k = 0; k < to.length(); k++) {
			double[] element = to.get(k);
			double[] other = from.get(k);
			for (int i = 0; i < to.getElementSize(); i++)
				element[i] += other[i];
		}
	}

}
