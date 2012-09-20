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

import game.configuration.Configurable;
import game.configuration.errorchecks.CompatibilityCheck;
import game.configuration.errorchecks.PositivenessCheck;
import game.plugins.constraints.Compatible;

public abstract class DatasetBuilder extends Configurable implements Compatible<InstanceTemplate> {
	
	public static final String CACHEDIRECTORY = "dataset_cache/";
	
	public InstanceTemplate template;
	
	public int startIndex = 0;
	
	public int instanceNumber = 1000;
	
	public boolean shuffle = true;
	
	public DatasetBuilder() {
		setOptionChecks("template", new CompatibilityCheck(this));
		setOptionChecks("instanceNumber", new PositivenessCheck(false));
		setOptionChecks("startIndex", new PositivenessCheck(true));
	}
	
	public abstract Dataset buildDataset();

}
