package game.configuration;

import java.util.LinkedList;

import org.junit.Test;
import static org.junit.Assert.*;

public class ConfigurableTest {

	@Test
	public void testConfigurable() {
		Configurable object = new ConfigurableTestA(), object2;
		
		object.setOption("optionA4", new ConfigurableTestB());
		object.setOption("optionA1", "This is optionA1");
		object.setOption("optionA5", new ConfigurableTestC());
		object.setOption("optionA2", "This is optionA2");
		object.setOption("optionA3", "This is optionA3");
		object.setOption("optionA4.optionB3", "This is optionB3");
		object.setOption("optionA5.optionC3", "This is optionC3");

		assertEquals("This is optionA1", object.getOption("optionA1"));
		assertEquals("This is optionA2", object.getOption("optionA2"));
		assertEquals("This is optionA3", object.getOption("optionA3"));

		object2 = object.getOption("optionA4");
		LinkedList<String> unbound = new LinkedList<>();
		unbound.add("optionB3");
		unbound.add("name");
		assertEquals(2, object2.getUnboundOptionNames().size());
		assertTrue(object2.getUnboundOptionNames().containsAll(unbound));
		assertEquals("This is optionA1", object2.getOption("optionB1"));
		assertEquals("This is optionA1", object.getOption("optionA4.optionB1"));
		assertEquals("This is optionA2", object2.getOption("optionB2"));
		assertEquals("This is optionB3", object2.getOption("optionB3"));

		object2 = object.getOption("optionA5");
		unbound.remove(0);
		unbound.add("optionC3");
		assertEquals(2, object2.getUnboundOptionNames().size());
		assertTrue(object2.getUnboundOptionNames().containsAll(unbound));
		assertEquals("This is optionA1", object2.getOption("optionC1"));
		assertEquals("This is optionA1", object.getOption("optionA5.optionC1"));
		assertEquals("This is optionB3", object2.getOption("optionC2"));
		assertEquals("This is optionC3", object2.getOption("optionC3"));
		
		object.setOption("optionA5", new ConfigurableTestC());
		object.setOption("optionA5.optionC3", "This is the new optionC3");
		
		assertEquals(4, object2.getUnboundOptionNames().size());
		unbound.add("optionC1");
		unbound.add("optionC2");
		assertTrue(object2.getUnboundOptionNames().containsAll(unbound));
		assertEquals("This is optionC3", object2.getOption("optionC3"));
		
		object2 = object.getOption("optionA5");
		unbound.remove(2);
		unbound.remove(2);
		assertEquals(2, object2.getUnboundOptionNames().size());
		assertTrue(object2.getUnboundOptionNames().containsAll(unbound));
		assertEquals("This is optionA1", object2.getOption("optionC1"));
		assertEquals("This is optionA1", object.getOption("optionA5.optionC1"));
		assertEquals("This is optionB3", object2.getOption("optionC2"));
		assertEquals("This is the new optionC3", object2.getOption("optionC3"));
	}

}
