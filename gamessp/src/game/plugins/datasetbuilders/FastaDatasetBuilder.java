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

import game.core.Dataset;
import game.core.DatasetBuilder;
import game.core.Instance;
import game.core.InstanceTemplate;
import game.plugins.datatemplates.ProteinDSSPStructure;
import game.plugins.datatemplates.ProteinHECStructure;
import game.plugins.datatemplates.ProteinPrimaryStructure;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class FastaDatasetBuilder extends DatasetBuilder {
	
	public File datasetFile = new File("nonexistent.txt");

	@Override
	public boolean isCompatible(InstanceTemplate template) {
		return template.inputTemplate instanceof ProteinPrimaryStructure
				&& (template.outputTemplate instanceof ProteinDSSPStructure
						|| template.outputTemplate instanceof ProteinHECStructure);
	}

	@Override
	public Dataset buildDataset() {
		Dataset ret = new Dataset();
		
		if (datasetFile.exists()) {
			try {
				int index = 0, count = 0;
				BufferedReader reader = new BufferedReader(new FileReader(datasetFile));
				boolean primary = true;
				List<String> sequence = null;
				Instance instance = null;
				for (String line = reader.readLine(); line != null && count < instanceNumber; line = reader.readLine()) {
					if (line.startsWith(">")) {
						sequence = new LinkedList<>();
						instance = new Instance();
					} else {
						for (int i = 0; i < line.length(); i++)
							sequence.add(String.valueOf(line.charAt(i)));
						if (primary) {
							instance.setInputData(sequence);
							sequence = new LinkedList<>();
						} else {
							instance.setOutputData(sequence);
							if (index++ >= startIndex) {
								ret.add(instance);
								count++;
							}
						}
						primary = !primary;
					}
				}
				reader.close();
			} catch (IOException e) {}
		}
		
		return ret;
	}

}
