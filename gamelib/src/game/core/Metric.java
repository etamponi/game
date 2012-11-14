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

import game.configuration.Compatible;
import game.configuration.IObject;

public abstract class Metric<R extends Result> extends IObject implements Compatible<Result> {
	
	private R result;
	
	public R getResult() {
		return result;
	}
	
	public void setResult(R result) {
		this.result = result;
		prepare();
	}
	
	protected abstract void prepare();
	
	public abstract String prettyPrint();
	
}
