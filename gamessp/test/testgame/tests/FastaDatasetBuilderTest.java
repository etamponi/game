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
import game.core.Instance;
import game.core.InstanceTemplate;
import game.plugins.datasetbuilders.FastaDatasetBuilder;
import game.plugins.datatemplates.ProteinHECStructure;
import game.plugins.datatemplates.ProteinPrimaryStructure;

import java.io.File;
import java.util.List;

import org.junit.Test;

public class FastaDatasetBuilderTest {

	@Test
	public void test() {
		InstanceTemplate template = new InstanceTemplate();
		template.inputTemplate = new ProteinPrimaryStructure();
		template.outputTemplate = new ProteinHECStructure();
		FastaDatasetBuilder builder = new FastaDatasetBuilder();
		builder.setOption("file", new File("testdata/fastadataset.txt"));
		builder.setOption("template", template);
		builder.setOption("shuffle", false);
		Dataset dataset = builder.buildDataset();
		
		Instance inst = dataset.instanceIterator().next();
		assertEquals(2, dataset.size());
		assertEquals("MKTAYIAKQRQISFVKSHFSRQLEERLGLIEVQAPILSRVGDGTQDNLSGAEKAVQVKVKALPDAQFEVVHSLAKWKRQTLGQHDFSAGEGLYTHMKALRPDEDRLSPLHSVYVDQWDWERVMGDGERQFSTLKSTVEAIWAGIKATEAAVSEEFGLAPFLPDQIHFVHSQELLSRYPDLDAKGRERAIAKDLGAVFLVGIGGKLSDGHRHDVRAPDYDDWSTPSELGHAGLNGDILVWNPVLEDAFELSSMGIRVDADTLKHQLALTGDEDRLELEWHQALLRGEMPQTIGGGIGQSRLTMLLLQLPHIGQVQAGVWPAAVRESVPSLL",
				getFasta(inst.getInput()));
		assertEquals("CCCCHHHHHHHHHHHHHHHHHHHHHHCCEEECCCCCEEECCCCCCCCCCCCCCCCEECCCCCCCCCEEECCCCCCHHHHHHHHHCCCCCCEEEEEEEEECCCCCCCCCCCCCEEEEEEEEEECCCCCCCHHHHHHHHHHHHHHHHHHHHHHHHHCCCCCCCCCCCEEEEHHHHHHHCCCCCHHHHHHHHHHHHCEEEEECCCCECCCCCECCCCCCCCECCCCECCCCCECCEEEEEEEECCCCEEEECEEEEEECCHHHHHHHCCCCCCCCHHHCHHHHHHHCCCCCCEEEEEEEHHHHHHHHHCCCCHHHCCCCCCCHHHHHHCCCCC",
				getFasta(inst.getOutput()));
	}
	
	private String getFasta(Object data) {
		StringBuilder ret = new StringBuilder();
		for(String e: (List<String>)data)
			ret.append(e);
		return ret.toString();
	}

}
