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
import game.core.blocks.Classifier;

public class ClassificationResult extends Result<ClassificationExperiment> {
	
	public Classifier trainedClassifier;
	
	public Dataset classifiedDataset;
	
}
