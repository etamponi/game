package game.plugins.trainingalgorithms;

import game.core.Data;
import game.core.Dataset;
import game.core.DatasetTemplate;
import game.core.Element;
import game.core.ElementTemplate;
import game.core.Instance;
import game.core.blocks.DynamicTemplateClassifier;
import game.core.blocks.Mapping;
import game.core.trainingalgorithms.ClassifierTrainingAlgorithm;
import game.plugins.valuetemplates.LabelTemplate;
import game.plugins.valuetemplates.VectorTemplate;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

public class BinaryDatasetBalancer extends ClassifierTrainingAlgorithm<DynamicTemplateClassifier> {

	@Override
	protected void train(final Dataset dataset) {
		final Dataset adjusted = balanceDataset(dataset);
		
		block.internal.setContent("datasetTemplate", adjusted.getTemplate());
		
		executeAnotherTaskAndWait(1.0, block.internal.trainingAlgorithm, adjusted);
		
		block.setContent("mapping", new Mapping() {
			int changedIndex = findChangedIndex(dataset, adjusted);
			@Override
			public Data map(Data input) {
				Data ret = new Data();
				for(Element e: input) {
					RealVector v = e.get();
					RealVector vret = new ArrayRealVector(2);
					vret.setEntry(changedIndex, Math.max(v.getEntry(changedIndex), v.getEntry(changedIndex+1)));
					vret.setEntry(1-changedIndex, v.getEntry(changedIndex == 0 ? 2 : 0));
					vret.mapDivideToSelf(vret.getL1Norm());
					ret.add(new Element(vret));
				}
				return ret;
			}
			private int findChangedIndex(Dataset dataset, Dataset adjusted) {
				List<String> original = dataset.getTemplate().targetTemplate.getSingleton(LabelTemplate.class).labels;
				List<String> balanced = adjusted.getTemplate().targetTemplate.getSingleton(LabelTemplate.class).labels;
				return original.get(0).equals(balanced.get(0)) ? 1 : 0;
			}
		});
	}

	private Dataset balanceDataset(Dataset dataset) {
		List<String> labels = dataset.getTemplate().targetTemplate.getSingleton(LabelTemplate.class).labels;
		int countPos = count(dataset, labels.get(0));
		int countNeg = dataset.size() - countPos;
		int majorIndex = countPos > countNeg ? 0 : 1;
		String majorLabel = labels.get(majorIndex);
		
		String[] splitted = new String[3];
		splitted[majorIndex] = labels.get(majorIndex) + "A";
		splitted[majorIndex+1] = labels.get(majorIndex) + "B";
		splitted[majorIndex == 0 ? 2 : 0] = labels.get(1-majorIndex);
		
		DatasetTemplate newTpl = new DatasetTemplate(dataset.getTemplate().sourceTemplate, 
				new ElementTemplate(new LabelTemplate(splitted)));
		
		Dataset ret = new Dataset(newTpl);
		
		RealVector centroid = getCentroid(dataset, majorLabel);
		RealVector mostDistal = getMostDistal(dataset, majorLabel, centroid);
		
		double[] distances = getDistances(dataset, majorLabel, mostDistal);
		double threshold = getThresholdDistance(distances, (int)Math.abs(countPos-countNeg));
		
		int index = 0;
		for(Instance i: dataset) {
			if (i.getTarget().get().get().equals(majorLabel)) {
				if (distances[index] < threshold) {
					ret.add(new Instance(i.getSource(), new Data(new Element(splitted[majorIndex]))));
				} else {
					ret.add(new Instance(i.getSource(), new Data(new Element(splitted[majorIndex+1]))));
				}
				index++;
			} else {
				ret.add(i);
			}
		}
		
		return ret;
	}

	private double getThresholdDistance(double[] distances, int k) {
		if (k == 0)
			return -1;
		double[] sorted = Arrays.copyOf(distances, distances.length);
		Arrays.sort(sorted);
		return sorted[k];
	}

	private double[] getDistances(Dataset dataset, String label, RealVector mostDistal) {
		double[] distances = new double[count(dataset, label)];
		
		int index = 0;
		for(Instance i: dataset) {
			if (!i.getTarget().get().get().equals(label))
				continue;
			RealVector cur = i.getSource().get().get();
			distances[index] = cur.getDistance(mostDistal);
			index++;
		}
		
		return distances;
	}

	private RealVector getMostDistal(Dataset dataset, String label, RealVector centroid) {
		RealVector ret = centroid;
		double maxDist = 0;
		for(Instance i: dataset) {
			if (!i.getTarget().get().get().equals(label))
				continue;
			RealVector cur = i.getSource().get().get();
			double curDist = cur.getDistance(centroid);
			if (curDist > maxDist) {
				ret = cur;
				maxDist = curDist;
			}
		}
		return ret;
	}

	private RealVector getCentroid(Dataset dataset, String label) {
		RealVector ret = null;
		int count = 0;
		for(Instance i: dataset) {
			if (!i.getTarget().get().get().equals(label))
				continue;
			RealVector cur = i.getSource().get().get();
			if (ret == null)
				ret = cur;
			else
				ret = ret.add(cur);
			count++;
		}
		ret.mapDivideToSelf(count);
		return ret;
	}

	private int count(Dataset dataset, String label) {
		int ret = 0;
		for(Instance i: dataset) {
			if (i.getTarget().get().get().equals(label))
				ret++;
		}
		return ret;
	}

	@Override
	protected String getTrainingPropertyNames() {
		return "mapping";
	}

	@Override
	protected boolean isCompatible(DatasetTemplate datasetTemplate) {
		return datasetTemplate.isReady() && datasetTemplate.targetTemplate.getSingleton(LabelTemplate.class).labels.size() == 2
				&& datasetTemplate.sourceTemplate.isSingletonTemplate(VectorTemplate.class)
				&& datasetTemplate.sequences == false;
	}

}
