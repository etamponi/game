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

import game.core.Encoding;
import game.core.InstanceTemplate;
import game.plugins.datatemplates.LabelTemplate;
import game.plugins.encoders.OneHotEncoder;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.Instance;

public class WekaMultilayerPerceptron extends WekaClassifier {
	
	public int hiddenNeurons = 5;
	
	public MultilayerPerceptron nn;

	public WekaMultilayerPerceptron() {
		outputEncoder = new OneHotEncoder();
		setFixedOptions("outputEncoder");
	}

	@Override
	public boolean isCompatible(InstanceTemplate template) {
		return template.outputTemplate instanceof LabelTemplate;
	}

	@Override
	protected Encoding classify(Encoding inputEncoded) {
		Encoding ret = new Encoding(getFeatureNumber(), inputEncoded.length());
		for(int j = 0; j < ret.length(); j++) {
			Instance i = new Instance(1.0, inputEncoded.getElement(j).toArray());
			try {
				ret.setColumn(j, nn.distributionForInstance(i));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return ret;
	}

}
