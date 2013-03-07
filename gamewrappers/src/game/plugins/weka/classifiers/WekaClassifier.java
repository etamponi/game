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
package game.plugins.weka.classifiers;

import game.core.Data;
import game.core.DatasetTemplate;
import game.core.Element;
import game.core.blocks.Classifier;
import game.plugins.valuetemplates.VectorTemplate;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

import weka.core.Instance;
import weka.core.Instances;

public class WekaClassifier extends Classifier {
	
	public Instances dataset;
	
	public weka.classifiers.Classifier internal;

	@Override
	public Data classify(Data input) {
		Data ret = new Data();
		
		for(Element e: input) {
			Instance i = new Instance(1.0, e.get(RealVector.class).toArray());
			i.setDataset(dataset);
			try {
				ret.add(new Element(new ArrayRealVector(internal.distributionForInstance(i))));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		
		return ret;
	}

	@Override
	public String classifierCompatibilityError(DatasetTemplate template) {
		return template.sourceTemplate.isSingletonTemplate(VectorTemplate.class) ? null : "can only handle singleton VectorTemplate";
	}

}
