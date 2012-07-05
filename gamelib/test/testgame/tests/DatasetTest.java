package testgame.tests;

import static org.junit.Assert.*;

import java.io.File;

import game.core.Dataset;
import game.core.DatasetBuilder;
import game.core.Instance;
import game.core.InstanceTemplate;
import game.plugins.datasetbuilders.CSVDatasetBuilder;
import game.plugins.datasetbuilders.SequenceCSVDatasetBuilder;
import game.plugins.datatemplates.LabelTemplate;
import game.plugins.datatemplates.SequenceTemplate;
import game.plugins.datatemplates.VectorTemplate;

import org.junit.Test;

public class DatasetTest {

	private static final InstanceTemplate template = new InstanceTemplate();
	private static final InstanceTemplate sequenceTpl = new InstanceTemplate();
	
	static {
		template.inputTemplate = new VectorTemplate();
		template.outputTemplate = new LabelTemplate();
		template.inputTemplate.setOption("featureNumber", 4);
		template.outputTemplate.setOption("labels.add", "Iris-setosa");
		template.outputTemplate.setOption("labels.add", "Iris-versicolor");
		template.outputTemplate.setOption("labels.add", "Iris-virginica");
		
		sequenceTpl.inputTemplate = new SequenceTemplate();
		sequenceTpl.outputTemplate = new SequenceTemplate();
		sequenceTpl.inputTemplate.setOption("atom", new VectorTemplate());
		sequenceTpl.inputTemplate.setOption("atom.featureNumber", 20);
		sequenceTpl.outputTemplate.setOption("atom", new LabelTemplate());
		sequenceTpl.outputTemplate.setOption("atom.labels.add", "H");
		sequenceTpl.outputTemplate.setOption("atom.labels.add", "E");
		sequenceTpl.outputTemplate.setOption("atom.labels.add", "C");
	}

	@Test
	public void testCSV() {
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
	
	@Test
	public void testCSVSequence() {
		DatasetBuilder builder = new SequenceCSVDatasetBuilder();
		builder.setOption("template", sequenceTpl);
		builder.setOption("file", new File("testdata/csvsequence.txt"));
		Dataset dataset = builder.buildDataset();
		assertEquals(5, dataset.size());
	}
	
	private boolean containsAny(Dataset a, Dataset b) {
		for(Instance i: b) {
			if (a.contains(i))
				return true;
		}
		return false;
	}

}
