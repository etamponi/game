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
package game.tests;

import static org.junit.Assert.*;
import game.core.Encoding;
import game.core.blocks.Encoder;
import game.plugins.datatemplates.VectorTemplate;

import org.junit.Test;

public class EncoderTest {
	
	private static class EncoderTestA extends Encoder<VectorTemplate> {
		@Override
		protected Encoding transform(Object inputData) {
			return null;
		}
		@Override
		public Class getBaseTemplateClass() {
			return VectorTemplate.class;
		}
	}

	@Test
	public void test() {
		Encoder encoder = new EncoderTestA();
		
		assertEquals(VectorTemplate.class, encoder.getBaseTemplateClass());
	}

}
