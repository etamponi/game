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
import game.core.Instance;
import game.core.InstanceTemplate;
import game.plugins.datatemplates.LabelTemplate;
import game.plugins.datatemplates.SequenceTemplate;
import game.plugins.datatemplates.VectorTemplate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class SequenceCSVDatasetBuilder extends DatasetBuilder {

	public File file = new File("nonexistent.txt");
	
	public String separators = "[, +]";
	
	public SequenceCSVDatasetBuilder() {
		setOptionChecks("file", new FileExistsCheck());
	}

	@Override
	public boolean isCompatible(InstanceTemplate template) {
		return isCompatible(template.inputTemplate) && isCompatible(template.outputTemplate);
	}
	
	private boolean isCompatible(DataTemplate template) {
		if (template instanceof SequenceTemplate)
			template = ((SequenceTemplate)template).atom;
		else
			return false;
		
		if (template instanceof LabelTemplate ||
				template instanceof VectorTemplate)
			return true;
		else
			return false;
	}

	@Override
	public Dataset buildDataset() {
		Dataset ret = new Dataset();
		
		InstanceTemplate atom = new InstanceTemplate();
		atom.inputTemplate = template.getOption("inputTemplate.atom");
		atom.outputTemplate = template.getOption("outputTemplate.atom");
		
		int inputDim = getDimension(atom.inputTemplate);
		int outputDim = getDimension(atom.outputTemplate);
		
		if (file.exists()) {
			try {
				BufferedReader reader = new BufferedReader(new FileReader(file));
				for(String line = reader.readLine(); line != null; line = reader.readLine()) {
					List<Object> inputSequence = new LinkedList<>();
					List<Object> outputSequence = new LinkedList<>();
					while (!line.matches("^$") && line != null) {
						String[] tokens = line.split(separators);
						inputSequence.add(getData(Arrays.copyOfRange(tokens, 0, inputDim), atom.inputTemplate));
						outputSequence.add(getData(Arrays.copyOfRange(tokens, inputDim, inputDim+outputDim), atom.outputTemplate));
						line = reader.readLine();
					}
					ret.add(new Instance(inputSequence, outputSequence));
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
			if (((LabelTemplate) template).labels.contains(tokens[0]))
				return tokens[0];
			else
				return null;
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
