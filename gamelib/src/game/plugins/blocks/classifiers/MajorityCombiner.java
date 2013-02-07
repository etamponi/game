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
package game.plugins.blocks.classifiers;

import game.core.Data;
import game.core.ElementTemplate;
import game.core.Element;
import game.core.blocks.Sink;
import game.plugins.valuetemplates.LabelTemplate;

import java.util.List;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

public class MajorityCombiner extends Sink {

	@Override
	protected Data combine(List<Data> inputs) {
		List<String> labels = outputTemplate.getSingleton(LabelTemplate.class).labels;
		RealMatrix counts = new Array2DRowRealMatrix(labels.size(), inputs.get(0).length());
		for(Data input: inputs) {
			for(int j = 0; j < input.length(); j++) {
				int i = labels.indexOf(input.get(j).get(0));
				counts.setEntry(i, j, counts.getEntry(i, j)+1);
			}
		}
		
		Data ret = new Data();
		for(int j = 0; j < counts.getColumnDimension(); j++)
			ret.add(new Element(labels.get(counts.getColumnVector(j).getMaxIndex())));
		return ret;
	}

	@Override
	public boolean supportsInputTemplate(ElementTemplate inputTemplate) {
		return inputTemplate.isSingletonTemplate(LabelTemplate.class);
	}

	@Override
	protected void setup() {
		// nothing to do
	}

}
