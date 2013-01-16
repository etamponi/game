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
import game.core.DatasetTemplate;
import game.core.ElementTemplate;
import game.plugins.datasetbuilders.CSVDatasetBuilder;
import game.plugins.datasetbuilders.SequenceCSVDatasetBuilder;
import game.plugins.datatemplates.LabelTemplate;
import game.plugins.datatemplates.VectorTemplate;

import java.io.File;
import java.util.List;

import org.junit.Test;

public class DatasetTest {

	private static final DatasetTemplate templateIris = new DatasetTemplate();
	private static final DatasetTemplate templateProtein = new DatasetTemplate();
	
	static {
		templateIris.sourceTemplate = new ElementTemplate(new VectorTemplate());
		templateIris.targetTemplate = new ElementTemplate(new LabelTemplate());
		templateIris.sourceTemplate.setContent("0.dimension", 4);
		templateIris.targetTemplate.getContent("0.labels", List.class).add("Iris-setosa");
		templateIris.targetTemplate.getContent("0.labels", List.class).add("Iris-versicolor");
		templateIris.targetTemplate.getContent("0.labels", List.class).add("Iris-virginica");

		templateProtein.sourceTemplate = new ElementTemplate(new VectorTemplate());
		templateProtein.targetTemplate = new ElementTemplate(new LabelTemplate());
		templateProtein.sourceTemplate.setContent("0.dimension", 20);
		templateProtein.targetTemplate.getContent("0.labels", List.class).add("H");
		templateProtein.targetTemplate.getContent("0.labels", List.class).add("E");
		templateProtein.targetTemplate.getContent("0.labels", List.class).add("C");
	}

	@Test
	public void testCSV() {
		DatasetBuilder builder = new CSVDatasetBuilder();
		builder.setContent("datasetTemplate", templateIris);
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
		builder.setContent("datasetTemplate", templateProtein);
		builder.setContent("file", new File("testdata/csvsequence.txt"));
		Dataset dataset = builder.buildDataset();
		assertEquals(5, dataset.size());
	}

}
