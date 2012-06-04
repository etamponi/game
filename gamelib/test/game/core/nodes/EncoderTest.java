package game.core.nodes;

import static org.junit.Assert.*;
import game.core.Encoding;
import game.core.datatemplates.VectorTemplate;

import org.junit.Test;

public class EncoderTest {
	
	private static class EncoderTestA extends Encoder<VectorTemplate> {

		@Override
		protected Encoding transform(Object inputData) {
			return null;
		}
		
	}

	@Test
	public void test() {
		Encoder encoder = new EncoderTestA();
		
		assertEquals(VectorTemplate.class, encoder.getBaseTemplateClass());
	}

}
