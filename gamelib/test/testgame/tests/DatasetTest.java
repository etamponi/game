package testgame.tests;

import static org.junit.Assert.assertEquals;
import game.core.DBDataset;
import game.core.DatasetBuilder;
import game.core.InstanceTemplate;
import game.plugins.datasetbuilders.CSVDatasetBuilder;
import game.plugins.datasetbuilders.SequenceCSVDatasetBuilder;
import game.plugins.datatemplates.LabelTemplate;
import game.plugins.datatemplates.SequenceTemplate;
import game.plugins.datatemplates.VectorTemplate;

import java.io.File;
import java.util.List;

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
		builder.setOption("databaseName", "test");
		DBDataset dataset = builder.buildDataset();
		
		assertEquals(150, dataset.size());
		List<DBDataset> folds = dataset.getFolds(4);
		List<DBDataset> complements = dataset.getComplementaryFolds(folds);
		assertEquals(150/4, folds.get(0).size());
		assertEquals(150-150/4, complements.get(0).size());

		DBDataset random1 = dataset.getRandomSubset(0.20);
		assertEquals(30, random1.size());
	}
	
	@Test
	public void testCSVSequence() {
		DatasetBuilder builder = new SequenceCSVDatasetBuilder();
		builder.setOption("template", sequenceTpl);
		builder.setOption("file", new File("testdata/csvsequence.txt"));
		builder.setOption("databaseName", "sequencetest");
		DBDataset dataset = builder.buildDataset();
		assertEquals(5, dataset.size());
	}

}
