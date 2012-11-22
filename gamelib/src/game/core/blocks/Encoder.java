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
package game.core.blocks;

import com.ios.Compatible;
import com.ios.ErrorCheck;
import com.ios.errorchecks.CompatibilityCheck;
import com.ios.errorchecks.PositivenessCheck;
import com.ios.errorchecks.SizeCheck;

import game.core.Block;
import game.core.DataTemplate;
import game.core.DataTemplate.Data;
import game.core.Encoding;

public abstract class Encoder<DT extends DataTemplate> extends Block implements Compatible<DataTemplate> {
	
	public DT template;
	
	public int windowSize = 1;
	
//	static {
//		getKryo().addDefaultSerializer(Encoder.class, new IObjectSerializer(Encoder.class));
//	}
	
	public Encoder() {
		addErrorCheck("parents", new SizeCheck(0, 0));
		addErrorCheck("template", new CompatibilityCheck(this));
		
		addErrorCheck("windowSize", new PositivenessCheck(false));
		addErrorCheck("windowSize", new ErrorCheck<Integer>() {
			Encoder encoder = Encoder.this;
			@Override
			public String getError(Integer value) {
				if (encoder.template != null && !encoder.template.sequence && encoder.windowSize != 1)
					return "has to be 1 (not a sequence)";
				else
					return null;
			}
		});
	}

	protected abstract Encoding baseEncode(Data input);
	
	protected abstract int getBaseFeatureNumber();
	
	protected abstract FeatureType getBaseFeatureType(int featureIndex);
	
	@Override
	public int getFeatureNumber() {
		return getBaseFeatureNumber()*windowSize;
	}
	
	@Override
	public FeatureType getFeatureType(int featureIndex) {
		assert(featureIndex >= 0 && featureIndex < getFeatureNumber());
		return getBaseFeatureType(featureIndex % getBaseFeatureNumber());
	}

	@Override
	public Encoding transform(Data input) {
		return baseEncode(input).makeWindowedEncoding(windowSize);
	}
	
	@Override
	public boolean acceptsParents() {
		return false;
	}

}
