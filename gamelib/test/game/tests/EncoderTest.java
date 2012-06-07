package game.tests;

import static org.junit.Assert.*;
import game.core.Encoding;
import game.core.datatemplates.VectorTemplate;
import game.core.nodes.Encoder;

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
