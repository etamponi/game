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
package testgame.tests;

import static org.junit.Assert.assertEquals;
import game.core.Dataset;
import game.core.DatasetBuilder;
import game.core.InstanceTemplate;
import game.plugins.datasetbuilders.CSVDatasetBuilder;
import game.plugins.datasetbuilders.SequenceCSVDatasetBuilder;
import game.plugins.datatemplates.LabelTemplate;
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
		template.inputTemplate.setContent("dimension", 4);
		template.outputTemplate.getContent("labels", List.class).add("Iris-setosa");
		template.outputTemplate.getContent("labels", List.class).add("Iris-versicolor");
		template.outputTemplate.getContent("labels", List.class).add("Iris-virginica");
		
		sequenceTpl.inputTemplate = new VectorTemplate();
		sequenceTpl.outputTemplate = new LabelTemplate();
		sequenceTpl.inputTemplate.setContent("sequence", true);
		sequenceTpl.inputTemplate.setContent("dimension", 20);
		sequenceTpl.outputTemplate.setContent("sequence", true);
		sequenceTpl.outputTemplate.getContent("labels", List.class).add("H");
		sequenceTpl.outputTemplate.getContent("labels", List.class).add("E");
		sequenceTpl.outputTemplate.getContent("labels", List.class).add("C");
	}

	@Test
	public void testCSV() {
		DatasetBuilder builder = new CSVDatasetBuilder();
		builder.setContent("template", template);
		builder.setContent("file", new File("testdata/iris.data.txt"));
		Dataset dataset = builder.buildDataset();
		
		assertEquals(150, dataset.size());
		List<Dataset> folds = dataset.getFolds(4);
		List<Dataset> complements = dataset.getComplementaryFolds(folds);
		assertEquals(150/4, folds.get(0).size());
		assertEquals(150-150/4, complements.get(0).size());

		Dataset random1 = dataset.getRandomSubset(0.20);
		assertEquals(30, random1.size());
	}
	
	@Test
	public void testCSVSequence() {
		DatasetBuilder builder = new SequenceCSVDatasetBuilder();
		builder.setContent("template", sequenceTpl);
		builder.setContent("file", new File("testdata/csvsequence.txt"));
		Dataset dataset = builder.buildDataset();
		assertEquals(5, dataset.size());
	}

}
