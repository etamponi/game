package game.plugins.encog.classifiers;

import game.configuration.errorchecks.PositivenessCheck;
import game.configuration.errorchecks.RangeCheck;
import game.core.Dataset;
import game.core.EncodedSample;
import game.core.Encoding;
import game.core.InstanceTemplate;
import game.core.blocks.Classifier;

import java.util.List;

import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.back.Backpropagation;

public class NNClassifier extends Classifier {
	
	public int hiddenNeurons = 5;
	public int maxIterations = 1000;
	public double maxError = 0.01;
	public double momentum = 0.1;
	public double learningRate = 0.001;
	
	public double validationPercent = 0.1;
	public int validateAfter = 10;
	
	public BasicNetwork network;
	
	public NNClassifier() {
		setOptionChecks("hiddenNeurons", new PositivenessCheck(false));
		setOptionChecks("maxIterations", new PositivenessCheck(false));
		setOptionChecks("maxError", new RangeCheck(0.0, 0.5));
		setOptionChecks("momentum", new RangeCheck(0.0, 1.0));
		setOptionChecks("learningRate", new RangeCheck(0.0, 1.0));
		
		setOptionChecks("validationPercent", new RangeCheck(0.0, 1.0));
		setOptionChecks("validateAfter", new RangeCheck(RangeCheck.LOWER, 1));
		
		setInternalOptions("network");
	}

	@Override
	public boolean isCompatible(InstanceTemplate object) {
		return true;
	}

	@Override
	protected Encoding classify(Encoding inputEncoded) {
		Encoding ret = new Encoding();
		
		for(double[] input: inputEncoded) {
			double[] output = network.compute(new BasicMLData(input)).getData();
			ret.add(output);
		}
		
		return ret;
	}

	@Override
	public boolean isTrained() {
		return network != null;
	}

	@Override
	protected void train(Dataset trainingSet) {
		List<EncodedSample> samples = trainingSet.encode(getParent(), outputEncoder);
		
		updateStatus(0.01, "start converting dataset");

		int inputLayerNeurons = samples.get(0).getInput().length;
		int outputLayerNeurons = samples.get(0).getOutput().length;
		
		network = new BasicNetwork();
		network.addLayer(new BasicLayer(null,true,inputLayerNeurons));
		network.addLayer(new BasicLayer(new ActivationSigmoid(),true, hiddenNeurons));
		network.addLayer(new BasicLayer(new ActivationSigmoid(),false,outputLayerNeurons));
		network.getStructure().finalizeStructure();
		network.reset();
		
		MLDataSet ts = new BasicMLDataSet();
		for(EncodedSample sample: samples) {
			MLData input = new BasicMLData(sample.getInput());
			MLData output = new BasicMLData(sample.getOutput());
			ts.add(input, output);
		}
		
		updateStatus(0.05, "dataset converted: start training");
		
		Backpropagation train = new Backpropagation(network, ts);
		train.setMomentum(momentum);
		train.setLearningRate(learningRate);
		int epoch = 1;
		do {
			train.iteration();
			updateStatus(0.05 + 0.95*epoch/maxIterations, "Epoch #" + epoch + " Error:" + train.getError());
			epoch++;
		} while(train.getError() > maxError && epoch < maxIterations);
		
		updateStatus(1.00, "network trained");
	}

}
