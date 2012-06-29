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

import game.core.Experiment;
import game.core.Graph;
import game.core.GraphTrainer;
import game.plugins.constraints.CompatibleWith;

public abstract class FullExperiment extends Experiment {
	
	public Graph graph;
	
	public GraphTrainer trainer;
	
	public FullExperiment() {
		setOptionBinding("template", "graph.template");
		
		setOptionConstraint("trainer", new CompatibleWith(this, "graph"));
	}

}
