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
import game.core.DatasetTemplate;
import game.core.ElementTemplate;
import game.core.Instance;
import game.plugins.valuetemplates.LabelTemplate;
import game.plugins.valuetemplates.VectorTemplate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ios.Property;
import com.ios.errorchecks.FileExistsCheck;
import com.ios.triggers.MasterSlaveTrigger;

public class CSVDatasetLoader extends DatasetBuilder {
	
	public File file = new File("nonexistent.txt");
	
	public String separators = "[, +]";
	
	public boolean hasHeader = false;
	
	public CSVDatasetLoader() {
		addTrigger(new MasterSlaveTrigger(this, "", "datasetTemplate.sequences") {
			@Override protected void updateSlave(Property slave, Object content) {
				slave.setContent(false);
			}
		});
		addErrorCheck("file", new FileExistsCheck());
	}

	@Override
	public Dataset buildDataset() {
		Dataset ret = new Dataset(datasetTemplate);
		
		int sourceDim = datasetTemplate.sourceTemplate.getDescriptionLength();
		int targetDim = datasetTemplate.targetTemplate.getDescriptionLength();
		
		if (file.exists()) {
			try {
				int index = 0, count = 0;
				BufferedReader reader = new BufferedReader(new FileReader(file));
				if (hasHeader)
					reader.readLine();
				for(String line = reader.readLine(); line != null && (instanceNumber < 0 || count < instanceNumber); line = reader.readLine(), index++) {
					if (index < startIndex)
						continue;
					String[] tokens = line.split(separators);
					if (tokens.length != (sourceDim + targetDim)) {
						reader.close();
						throw new RuntimeException("Expected " + (sourceDim + targetDim) + " tokens, found " + tokens.length);
					}
					
					Data source = new Data();
					source.add(datasetTemplate.sourceTemplate.loadElement(Arrays.asList(Arrays.copyOfRange(tokens, 0, sourceDim))));
					Data target = new Data();
					target.add(datasetTemplate.targetTemplate.loadElement(Arrays.asList(Arrays.copyOfRange(tokens, sourceDim, tokens.length))));
					ret.add(new Instance(source, target));
					count++;
				}
				reader.close();
			} catch (IOException e) {}
		}
		
		return ret;
	}

	@Override
	public void prepare() {
		if (file.exists()) {
			try {
				BufferedReader reader = new BufferedReader(new FileReader(file));
				String line = reader.readLine();
				
				String[] tokens = line.split(separators);
				DatasetTemplate template = new DatasetTemplate();
				template.setContent("sourceTemplate", new ElementTemplate(new VectorTemplate(tokens.length-1)));
				
				List<String> labels = new ArrayList<>();
				do {
					if (hasHeader) {
						line = reader.readLine();
						continue;
					}
					tokens = line.split(separators);
					if (!labels.contains(tokens[tokens.length-1]))
						labels.add(tokens[tokens.length-1]);
					
					line = reader.readLine();
				} while(line != null);
				
				template.setContent("targetTemplate", new ElementTemplate(new LabelTemplate(labels.toArray(new String[]{}))));
				
				reader.close();
			} catch (IOException e) {}
		}
	}

}
