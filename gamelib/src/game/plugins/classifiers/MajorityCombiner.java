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
package game.plugins.classifiers;

import game.core.DataTemplate.Data;
import game.core.Encoding;
import game.core.InstanceTemplate;
import game.core.blocks.Combiner;
import game.plugins.datatemplates.LabelTemplate;
import game.plugins.decoders.ProbabilityOneHotDecoder;
import game.plugins.encoders.OneHotEncoder;

import java.util.List;

import org.apache.commons.math3.linear.RealMatrix;

import com.ios.constraints.SubclassConstraint;

public class MajorityCombiner extends Combiner {
	
	public MajorityCombiner() {
		setContent("outputEncoder", new OneHotEncoder());
		
		addConstraint("outputEncoder", new SubclassConstraint(OneHotEncoder.class));
	}

	@Override
	public boolean isCompatible(InstanceTemplate template) {
		return template.outputTemplate instanceof LabelTemplate;
	}

	@Override
	public Encoding transform(Data input) {
		List<Encoding> parentsEncodings = getParentsEncodings(input);
		RealMatrix ret = new Encoding(getFeatureNumber(), input.length());
		
		ProbabilityOneHotDecoder decoder = new ProbabilityOneHotDecoder();
		decoder.interpolate = false;
		decoder.setContent("encoder", outputEncoder);
		
		for(Encoding encoding: parentsEncodings) {
			Data output = decoder.decode(encoding);
			ret = ret.add(outputEncoder.transform(output));
		}
		
		return new Encoding(ret);
	}

	@Override
	public FeatureType getFeatureType(int featureIndex) {
		return FeatureType.NOMINAL;
	}

}
