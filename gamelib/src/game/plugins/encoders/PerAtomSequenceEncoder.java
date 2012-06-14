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
package game.plugins.encoders;

import game.configuration.errorchecks.PositivenessCheck;
import game.core.Encoding;
import game.core.blocks.Encoder;
import game.core.blocks.SequenceEncoder;
import game.plugins.constraints.CompatibleEncoderConstraint;
import game.plugins.datatemplates.SequenceTemplate;

import java.util.List;

public class PerAtomSequenceEncoder extends SequenceEncoder {
	
	public Encoder atomEncoder;
	
	public int windowSize = 1;
	
	public PerAtomSequenceEncoder() {
		addOptionBinding("template.atom", "atomEncoder.template");
		
		setOptionConstraint("atomEncoder", new CompatibleEncoderConstraint(this, "template.atom"));
		
		addOptionChecks("windowSize", new PositivenessCheck(false));
	}
	
	@Override
	public Class getBaseTemplateClass() {
		return SequenceTemplate.class;
	}

	@Override
	protected Encoding transform(Object inputData) {
		Encoding ret = new Encoding();
		
		List input = (List)inputData;
		for(Object atom: input)
			ret.addAll(atomEncoder.startTransform(atom));
		
		return ret.makeWindowedEncoding(windowSize);
	}

}
