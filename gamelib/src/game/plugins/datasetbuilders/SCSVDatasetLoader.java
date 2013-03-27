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

import game.core.Data;
import game.core.Dataset;
import game.core.DatasetBuilder;
import game.core.Instance;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import com.ios.Property;
import com.ios.errorchecks.FileExistsCheck;
import com.ios.triggers.MasterSlaveTrigger;

public class SCSVDatasetLoader extends DatasetBuilder {

	public File file = new File("nonexistent.txt");
	
	public String separators = "[, +]";
	
	public SCSVDatasetLoader() {
		addTrigger(new MasterSlaveTrigger(this, "", "datasetTemplate.sequences") {
			@Override protected void updateSlave(Property slave, Object content) {
				slave.setContent(true);
			}
		});
		addErrorCheck(new FileExistsCheck("file"));
	}

	@Override
	public Dataset buildDataset() {
		Dataset ret = new Dataset(datasetTemplate);
		
		int sourceDim = datasetTemplate.sourceTemplate.getDescriptionLength();
		int targetDim = datasetTemplate.targetTemplate.getDescriptionLength();
		
		if (file.exists()) {
			try {
				int count = 0, index = 0;
				BufferedReader reader = new BufferedReader(new FileReader(file));
				for(String line = reader.readLine(); line != null && count < getInstanceNumber(); line = reader.readLine(), index++) {
					Data sourceSequence = new Data();
					Data targetSequence = new Data();
					while (line != null && !line.matches("^$")) {
						String[] tokens = line.split(separators);
						if (tokens.length != (sourceDim + targetDim)) {
							reader.close();
							throw new RuntimeException("Expected " + (sourceDim + targetDim) + " tokens, found " + tokens.length);
						}
						
						sourceSequence.add(datasetTemplate.sourceTemplate.loadElement(Arrays.copyOfRange(tokens, 0, sourceDim)));
						targetSequence.add(datasetTemplate.targetTemplate.loadElement(Arrays.copyOfRange(tokens, sourceDim, tokens.length)));
						line = reader.readLine();
					}
					if (index < startIndex)
						continue;
					ret.add(new Instance(sourceSequence, targetSequence));
					count++;
				}
				reader.close();
			} catch (IOException e) {}
		}
		
		return ret;
	}

	@Override
	public void prepare() {
		// nothing to do
	}

}
