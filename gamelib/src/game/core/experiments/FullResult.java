/*******************************************************************************
 * Copyright (c) 2012 Emanuele.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Emanuele - initial API and implementation
 ******************************************************************************/
package game.core.experiments;

import game.core.Dataset;
import game.core.Result;
import game.core.blocks.PredictionGraph;

import java.util.ArrayList;
import java.util.List;

public class FullResult extends Result {
	
	public List<PredictionGraph> trainedGraphs = new ArrayList<>();
	
	public List<Dataset> testedDatasets = new ArrayList<>();
	
}
