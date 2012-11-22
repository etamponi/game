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
package game.core.experiments;

import game.core.Dataset;
import game.core.Result;
import game.core.blocks.PredictionGraph;

import com.ios.IList;

public class FullResult extends Result {
	
	public IList<PredictionGraph> trainedGraphs;
	
	public IList<Dataset> testedDatasets;
	
	public FullResult() {
		setContent("trainedGraphs", new IList<>(PredictionGraph.class));
		setContent("testedDatasets", new IList<>(Dataset.class));
	}
	
}
