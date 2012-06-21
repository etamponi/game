package game.plugins.datasetbuilders;

import game.core.DataTemplate;
import game.core.Dataset;
import game.core.DatasetBuilder;
import game.core.Instance;
import game.core.InstanceTemplate;
import game.plugins.datatemplates.LabelTemplate;
import game.plugins.datatemplates.VectorTemplate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public class CSVDatasetBuilder extends DatasetBuilder {
	
	public String fileName; // TODO Replace with File (and then write FileEditor)
	
	public String separators = "[, +]";

	@Override
	public boolean isCompatible(InstanceTemplate template) {
		return isCompatible(template.inputTemplate) && isCompatible(template.outputTemplate);
	}

	private boolean isCompatible(DataTemplate template) {
		return template instanceof VectorTemplate
				|| template instanceof LabelTemplate;
	}

	@Override
	public Dataset buildDataset() {
		Dataset ret = new Dataset();
		File file = new File(fileName);
		
		int inputDim = getDimension(template.inputTemplate);
		int outputDim = getDimension(template.outputTemplate);
		
		if (file.exists()) {
			try {
				BufferedReader reader = new BufferedReader(new FileReader(file));
				for(String line = reader.readLine(); line != null; line = reader.readLine()) {
					String[] tokens = line.split(separators);
					Object input = getData(Arrays.copyOfRange(tokens, 0, inputDim), template.inputTemplate);
					Object output = getData(Arrays.copyOfRange(tokens, inputDim, inputDim+outputDim), template.outputTemplate);
					ret.add(new Instance(input, output));
				}
			} catch (IOException e) {}
		}
		
		return ret;
	}

	private Object getData(String[] tokens, DataTemplate template) {
		if (template instanceof VectorTemplate) {
			double[] ret = new double[tokens.length];
			for (int i = 0; i < ret.length; i++) {
				ret[i] = Double.parseDouble(tokens[i]);
			}
			return ret;
		}
		if (template instanceof LabelTemplate) {
			return tokens[0];
		}
		return null;
	}

	private int getDimension(DataTemplate template) {
		if (template instanceof VectorTemplate) {
			return ((VectorTemplate) template).featureNumber;
		}
		if (template instanceof LabelTemplate) {
			return 1;
		}
		
		return 0;
	}

}
