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
package game.core;

import game.plugins.constraints.Compatible;

public abstract class GraphTrainer extends LongTask implements Compatible<Graph> {
	
	protected abstract void trainGraph(Graph graph, DBDataset trainingSet);
	
	public <T> T startGraphTraining(Graph graph, DBDataset trainingSet) {
		return startTask(graph, trainingSet);
	}

	@Override
	protected Object execute(Object... params) {
		Graph graph = (Graph)params[0];
		trainGraph(graph, (DBDataset)params[1]);
		graph.setTrained();
		
		return null;
	}

}
