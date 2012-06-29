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

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;

import game.core.Dataset;
import game.plugins.datasetbuilders.FastaDatasetBuilder;

import org.junit.Test;

public class FastaDatasetBuilderTest {

	@Test
	public void test() {
		FastaDatasetBuilder builder = new FastaDatasetBuilder();
		builder.setOption("datasetFile", new File("testdata/fastadataset.txt"));
		Dataset dataset = builder.buildDataset();
		
		assertEquals(2, dataset.size());
		assertEquals("MKTAYIAKQRQISFVKSHFSRQLEERLGLIEVQAPILSRVGDGTQDNLSGAEKAVQVKVKALPDAQFEVVHSLAKWKRQTLGQHDFSAGEGLYTHMKALRPDEDRLSPLHSVYVDQWDWERVMGDGERQFSTLKSTVEAIWAGIKATEAAVSEEFGLAPFLPDQIHFVHSQELLSRYPDLDAKGRERAIAKDLGAVFLVGIGGKLSDGHRHDVRAPDYDDWSTPSELGHAGLNGDILVWNPVLEDAFELSSMGIRVDADTLKHQLALTGDEDRLELEWHQALLRGEMPQTIGGGIGQSRLTMLLLQLPHIGQVQAGVWPAAVRESVPSLL",
				getFasta(dataset.get(0).getInputData()));
		assertEquals("CCCCHHHHHHHHHHHHHHHHHHHHHHCCEEECCCCCEEECCCCCCCCCCCCCCCCEECCCCCCCCCEEECCCCCCHHHHHHHHHCCCCCCEEEEEEEEECCCCCCCCCCCCCEEEEEEEEEECCCCCCCHHHHHHHHHHHHHHHHHHHHHHHHHCCCCCCCCCCCEEEEHHHHHHHCCCCCHHHHHHHHHHHHCEEEEECCCCECCCCCECCCCCCCCECCCCECCCCCECCEEEEEEEECCCCEEEECEEEEEECCHHHHHHHCCCCCCCCHHHCHHHHHHHCCCCCCEEEEEEEHHHHHHHHHCCCCHHHCCCCCCCHHHHHHCCCCC",
				getFasta(dataset.get(0).getOutputData()));
	}
	
	private String getFasta(Object data) {
		StringBuilder ret = new StringBuilder();
		for(String e: (List<String>)data)
			ret.append(e);
		return ret.toString();
	}

}
