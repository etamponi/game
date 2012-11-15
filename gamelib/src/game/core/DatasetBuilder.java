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
package game.core;

import com.ios.Compatible;
import com.ios.IObject;
import com.ios.errorchecks.CompatibilityCheck;
import com.ios.errorchecks.PositivenessCheck;

public abstract class DatasetBuilder extends IObject implements Compatible<InstanceTemplate> {
	
	public InstanceTemplate template;
	
	public int startIndex = 0;
	
	public int instanceNumber = 1000;
	
	public DatasetBuilder() {
		addErrorCheck("template", new CompatibilityCheck(this));
		addErrorCheck("instanceNumber", new PositivenessCheck(false));
		addErrorCheck("startIndex", new PositivenessCheck(true));
	}
	
	public abstract Dataset buildDataset();

}
