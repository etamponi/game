package testgame.tests;

import static org.junit.Assert.*;

import java.io.File;

import game.core.Dataset;
import game.core.DatasetBuilder;
import game.core.Instance;
import game.core.InstanceTemplate;
import game.plugins.datasetbuilders.CSVDatasetBuilder;
import game.plugins.datatemplates.LabelTemplate;
import game.plugins.datatemplates.VectorTemplate;

import org.junit.Test;

public class DatasetTest {
	
	private static final InstanceTemplate template = new InstanceTemplate();
	
	static {
		template.inputTemplate = new VectorTemplate();
		template.outputTemplate = new LabelTemplate();
		template.inputTemplate.setOption("featureNumber", 4);
		template.outputTemplate.setOption("labels.add", "Iris-setosa");
		template.outputTemplate.setOption("labels.add", "Iris-versicolor");
		template.outputTemplate.setOption("labels.add", "Iris-virginica");
	}

	@Test
	public void test() {
		DatasetBuilder builder = new CSVDatasetBuilder();
		builder.setOption("template", template);
		builder.setOption("file", new File("testdata/iris.data.txt"));
		Dataset dataset = builder.buildDataset();
		
		assertEquals(150, dataset.size());
		Dataset fold = dataset.getFold(0, 4);
		Dataset complement = dataset.getFoldComplement(0, 4);
		assertEquals(150/4, fold.size());
		assertEquals(150-150/4, complement.size());
		assertFalse(containsAny(fold, complement));

		Dataset random1 = dataset.getRandomSubset(0.20);
		Dataset random2 = dataset.getRandomSubset(0.20);
		assertEquals(30, random1.size());
		assertFalse(random2.containsAll(random1));
	}
	
	private boolean containsAny(Dataset a, Dataset b) {
		for(Instance i: b) {
			if (a.contains(i))
				return true;
		}
		return false;
	}

}
