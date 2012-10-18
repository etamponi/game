/*******************************************************************************
 * Copyright (c) 2012 Emanuele Tamponi.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Emanuele Tamponi - initial API and implementation
 ******************************************************************************/
package game.plugins.datasetbuilders;

import game.configuration.errorchecks.FileExistsCheck;
import game.core.DataTemplate;
import game.core.Dataset;
import game.core.DatasetBuilder;
import game.core.InstanceTemplate;
import game.plugins.datatemplates.LabelTemplate;
import game.plugins.datatemplates.VectorTemplate;
import game.utils.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.math3.linear.ArrayRealVector;

public class CSVDatasetBuilder extends DatasetBuilder {
	
	public File file = new File("nonexistent.txt");
	
	public String separators = "[, +]";
	
	public CSVDatasetBuilder() {
		setOptionChecks("file", new FileExistsCheck());
	}

	@Override
	public boolean isCompatible(InstanceTemplate template) {
		return isCompatible(template.inputTemplate) && isCompatible(template.outputTemplate);
	}

	private boolean isCompatible(DataTemplate template) {
		return (template instanceof VectorTemplate
				|| template instanceof LabelTemplate) && template.sequence == false;
	}

	@Override
	public Dataset buildDataset() {
		Dataset ret = new Dataset(template, CACHEDIRECTORY, Utils.randomString(), shuffle);
		
		int inputDim = getDimension(template.inputTemplate);
		int outputDim = getDimension(template.outputTemplate);
		
		if (file.exists()) {
			try {
				int index = 0, count = 0;
				BufferedReader reader = new BufferedReader(new FileReader(file));
				for(String line = reader.readLine(); line != null && count < instanceNumber; line = reader.readLine(), index++) {
					if (index < startIndex)
						continue;
					String[] tokens = line.split(separators);
					Object input = getData(Arrays.copyOfRange(tokens, 0, inputDim), template.inputTemplate);
					Object output = getData(Arrays.copyOfRange(tokens, inputDim, inputDim+outputDim), template.outputTemplate);
					ret.add(template.newInstance(input, output));
					count++;
				}
				reader.close();
				ret.setReadOnly();
			} catch (IOException e) {}
		}
		
		return ret;
	}

	private Object getData(String[] tokens, DataTemplate template) {
		if (template instanceof VectorTemplate) {
			double[] ret = new double[tokens.length];
			for (int i = 0; i < ret.length; i++) {
				try {
					ret[i] = Double.parseDouble(tokens[i]);
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
			}
			return new ArrayRealVector(ret);
		}
		if (template instanceof LabelTemplate) {
			if (((LabelTemplate) template).labels.contains(tokens[0]))
				return tokens[0];
			else
				return null;
		}
		return null;
	}

	private int getDimension(DataTemplate template) {
		if (template instanceof VectorTemplate) {
			return ((VectorTemplate) template).dimension;
		}
		if (template instanceof LabelTemplate) {
			return 1;
		}
		
		return 0;
	}

}
