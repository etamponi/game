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


import java.util.List;

import com.ios.Compatible;
import com.ios.IList;
import com.ios.IObject;

public abstract class Metrics<R extends Result> extends IObject implements Compatible<Result> {
	
	public IList<LabeledMatrix> data;
	
	public LabeledStatisticsMatrix statistics;
	
	public Metrics() {
		setContent("data", new IList<>(List.class));
	}
	
	public void prepare(ResultList<R> list) {
		for(R result: list.results) {
			prepareForEvaluation(result);
			data.add(evaluateMetrics(result));
		}
		
		statistics = new LabeledStatisticsMatrix(data);
		
	}
	
	protected abstract LabeledMatrix evaluateMetrics(R result);
	
	protected abstract void prepareForEvaluation(R result);
	
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

}
