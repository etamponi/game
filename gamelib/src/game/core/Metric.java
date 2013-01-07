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


import java.util.ArrayList;
import java.util.List;

import com.esotericsoftware.reflectasm.MethodAccess;
import com.ios.Compatible;
import com.ios.IMap;
import com.ios.IObject;

public abstract class Metric<R extends Result> extends IObject implements Compatible<Result> {
	
	public IMap< List<LabeledMatrix> > dataMap;
	
	public IMap< LabeledStatisticsMatrix > statMap;
	
	public Metric() {
		setContent("dataMap", new IMap<>(List.class));
		setContent("statMap", new IMap<>(LabeledStatisticsMatrix.class));
	}
	
	public void prepare(ResultList<R> list) {
		String[] keys = getMetricNames().split(" ");
		MethodAccess access = MethodAccess.get(getClass());
		
		for(R result: list.results) {
			prepareForEvaluation(result);
			
			for(String key: keys) {
				String methodName = "evaluate" + key.substring(0, 1).toUpperCase() + key.substring(1);
				if (!dataMap.containsKey(key))
					dataMap.put(key, new ArrayList<LabeledMatrix>(list.results.size()));
				dataMap.get(key).add((LabeledMatrix)access.invoke(this, methodName, result));
			}
		}
		
		for(String key: keys)
			statMap.put(key, new LabeledStatisticsMatrix(dataMap.get(key)));
		
	}
	
	protected abstract String getMetricNames();
	
	protected abstract void prepareForEvaluation(R result);

}
