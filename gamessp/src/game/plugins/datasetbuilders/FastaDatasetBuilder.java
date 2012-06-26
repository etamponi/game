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
				BufferedReader reader = new BufferedReader(new FileReader(datasetFile));
				boolean primary = false;
				List<String> sequence = null;
				Instance instance = null;
				for (String line = reader.readLine(); line != null; line = reader.readLine()) {
					if (line.startsWith(">")) {
						if (sequence != null) {
							if (primary) {
								instance = new Instance();
								instance.setInputData(sequence);
							} else {
								instance.setOutputData(sequence);
								ret.add(instance);
							}
						}
						primary = !primary;
						sequence = new LinkedList<>();
					} else {
						for (int i = 0; i < line.length(); i++)
							sequence.add(String.valueOf(line.charAt(i)));
					}
				}
			} catch (IOException e) {}
		}
		
		return ret;
	}

}
