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
		object.setOption("optionList.add", new ConfigurableTestB());
		object.setOption("optionList.add", new ConfigurableTestB());
		object.setOption("optionList.0.optionB1", "This is optionB1 of the 0 element of the list");
		object.setOption("optionList.1.optionB1", "This is optionB1 of the 1 element of the list");

		assertEquals("This is optionA1", object.getOption("optionA1"));
		assertEquals("This is optionA2", object.getOption("optionA2"));
		assertEquals("This is optionA3", object.getOption("optionA3"));
		assertEquals("This is optionB1 of the 0 element of the list", object.getOption("optionList.0.optionB1"));
		assertEquals("This is optionB1 of the 1 element of the list", object.getOption("optionList.1.optionB1"));
		assertEquals("This is optionA3", object.getOption("optionList.0.optionB2"));
		assertEquals("This is optionA3", object.getOption("optionList.1.optionB2"));

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
	/*
	@Test
	public void testConfigurationErrors() {
		Configurable object = new ConfigurableTestA();
		object.setOption("optionA3", "Small string");
		
		LinkedList<String> errors = new LinkedList<>();
		errors.add("optionA1: is null");
		errors.add("optionA2: is null");
		errors.add("optionA3: should have at least 20 characters");
		errors.add("optionA4: is null");
		errors.add("optionA5: is null");
		assertEquals(5, object.getConfigurationErrors().size());
		assertTrue(object.getConfigurationErrors().containsAll(errors));

		object.setOption("optionA1", "Option set");
		object.setOption("optionA2", "Option set");
		object.setOption("optionA3", "Looooooooooong String!!!");
		object.setOption("optionA4", new ConfigurableTestB());
		errors.clear();
		errors.add("optionA4.optionB3: is null");
		errors.add("optionA5: is null");
		assertEquals(2, object.getConfigurationErrors().size());
		assertTrue(object.getConfigurationErrors().containsAll(errors));
	}
	*/
}
