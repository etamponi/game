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

import game.core.blocks.Classifier;
import game.utils.WekaClassifierSerializer;

import com.ios.IObject;



public abstract class WekaClassifier extends Classifier {
	
	static {
		IObject.getKryo().addDefaultSerializer(weka.classifiers.Classifier.class, WekaClassifierSerializer.class);
	}
	
}
