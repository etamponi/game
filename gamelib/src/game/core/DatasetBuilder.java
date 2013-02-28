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

import com.ios.IObject;
import com.ios.errorchecks.PositivenessCheck;

public abstract class DatasetBuilder extends IObject {
	
	public DatasetTemplate datasetTemplate;
	
	public int startIndex = 0;
	
	public int instanceNumber = 0;
	
	public DatasetBuilder() {
		addErrorCheck(new PositivenessCheck("startIndex", true));
	}
	
	public abstract void prepare();
	
	public abstract Dataset buildDataset();
	
	protected int getInstanceNumber() {
		if (instanceNumber <= 0)
			return Integer.MAX_VALUE;
		else
			return instanceNumber;
	}

}
