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
package testgame.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import game.configuration.Configurable;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import testgame.configuration.ConfigurableImplA;
import testgame.configuration.ConfigurableImplB;
import testgame.configuration.ConfigurableImplC;

public class ConfigurableTest {

	@Test
	public void testConfigurable() {
		Configurable object = new ConfigurableImplA(), object2;
		
		object.setOption("optionA4", new ConfigurableImplB());
		object.setOption("optionA1", "This is optionA1");
		object.setOption("optionA5", new ConfigurableImplC());
		object.setOption("optionA2", "This is optionA2");
		object.setOption("optionA3", "This is optionA3");
		object.setOption("optionA4.optionB3", "This is optionB3");
		object.setOption("optionA5.optionC3", "This is optionC3");
		object.setOption("optionList.add", new ConfigurableImplB());
		object.setOption("optionList.add", new ConfigurableImplB());
		object.setOption("optionList.0.optionB1", "This is optionB1 on list.0");
		object.setOption("optionList.1.optionB1", "This is optionB1 on list.1");

		assertEquals("This is optionA1", object.getOption("optionA1"));
		assertEquals("This is optionA2", object.getOption("optionA2"));
		assertEquals("This is optionA3", object.getOption("optionA3"));
		assertEquals("This is optionB1 on list.0", object.getOption("optionList.0.optionB1"));
		assertEquals("This is optionB1 on list.1", object.getOption("optionList.1.optionB1"));
		assertEquals("This is optionA3", object.getOption("optionList.0.optionB3"));
		assertEquals("This is optionA3", object.getOption("optionList.1.optionB3"));
		
		object.setOption("optionList.1", new ConfigurableImplB());
		assertEquals("This is optionA3", object.getOption("optionList.1.optionB3"));

		object.setOption("optionList.remove", 1);
		object.setOption("optionList.remove", object.getOption("optionList.0"));
		assertEquals(0, object.getOption("optionList", List.class).size());
		object.setOption("optionList.add", new ConfigurableImplB());

		object2 = object.getOption("optionA4");
		LinkedList<String> unbound = new LinkedList<>();
		unbound.add("optionB3");
		assertEquals(unbound.size(), object2.getUnboundOptionNames().size());
		assertTrue(object2.getUnboundOptionNames().containsAll(unbound));
		assertEquals("This is optionA1", object2.getOption("optionB1"));
		assertEquals("This is optionA1", object.getOption("optionA4.optionB1"));
		assertEquals("This is optionA2", object2.getOption("optionB2"));
		assertEquals("This is optionB3", object2.getOption("optionB3"));

		object2 = object.getOption("optionA5");
		unbound.remove(0);
		unbound.add("optionC3");
		assertEquals(unbound.size(), object2.getUnboundOptionNames().size());
		assertTrue(object2.getUnboundOptionNames().containsAll(unbound));
		assertEquals("This is optionA1", object2.getOption("optionC1"));
		assertEquals("This is optionA1", object.getOption("optionA5.optionC1"));
		assertEquals("This is optionB3", object2.getOption("optionC2"));
		assertEquals("This is optionC3", object2.getOption("optionC3"));
		
		object.setOption("optionA5", new ConfigurableImplC());
		object.setOption("optionA5.optionC3", "This is the new optionC3");
		
		unbound.add("optionC1");
		unbound.add("optionC2");
		assertEquals(unbound.size(), object2.getUnboundOptionNames().size());
		assertTrue(object2.getUnboundOptionNames().containsAll(unbound));
		assertEquals("This is optionC3", object2.getOption("optionC3"));
		
		object2 = object.getOption("optionA5");
		unbound.remove(1);
		unbound.remove(1);
		assertEquals(unbound.size(), object2.getUnboundOptionNames().size());
		assertTrue(object2.getUnboundOptionNames().containsAll(unbound));
		assertEquals("This is optionA1", object2.getOption("optionC1"));
		assertEquals("This is optionA1", object.getOption("optionA5.optionC1"));
		assertEquals("This is optionB3", object2.getOption("optionC2"));
		assertEquals("This is the new optionC3", object2.getOption("optionC3"));
		
		object2 = object.getOption("optionList.0");
		unbound.clear();
		unbound.add("optionB1");
		unbound.add("optionB2");
		assertEquals(unbound.size(), object2.getUnboundOptionNames().size());
		assertTrue(object2.getUnboundOptionNames().containsAll(unbound));
	}
	
	@Test
	public void testConfigurationErrors() {
		Configurable object = new ConfigurableImplA();
		object.setOption("optionA3", "Small string");
		
		LinkedList<String> errors = new LinkedList<>();
		errors.add("optionA1: is null");
		errors.add("optionA2: is null");
		errors.add("optionA3: must have at least 20 characters");
		errors.add("optionA4: is null");
		errors.add("optionA5: is null");
		assertEquals(5, object.getConfigurationErrors().size());
		assertTrue(object.getConfigurationErrors().containsAll(errors));

		object.setOption("optionA1", "Option set");
		object.setOption("optionA2", "Option set");
		object.setOption("optionA3", "Looooooooooong String!!!");
		object.setOption("optionA4", new ConfigurableImplB());
		errors.clear();
		errors.add("optionA4.optionB3: is null");
		errors.add("optionA5: is null");
		assertEquals(2, object.getConfigurationErrors().size());
		assertTrue(object.getConfigurationErrors().containsAll(errors));
		
		object.setOption("optionList.add", new ConfigurableImplB());
		errors.add("optionList.0.optionB1: is null");
		errors.add("optionList.0.optionB2: is null");
		assertEquals(4, object.getConfigurationErrors().size());
		assertTrue(object.getConfigurationErrors().containsAll(errors));
	}
	
	@Test
	public void serializationTest() {
		Configurable objectA = new ConfigurableImplA();
		objectA.setOption("optionA1", "This is optionA1");
		objectA.setOption("optionA2", "This is optionA2");
		objectA.setOption("optionA3", "This is optionA3");
		objectA.setOption("optionA4", new ConfigurableImplB());
		
		Configurable objectB = objectA.cloneConfiguration();
		assertEquals("This is optionA1", objectB.getOption("optionA1"));
		assertEquals("This is optionA2", objectB.getOption("optionA2"));
		assertEquals("This is optionA3", objectB.getOption("optionA3"));
		assertEquals("This is optionA1", objectB.getOption("optionA4.optionB1"));
		
		objectA.saveConfiguration("testdata/testconfig.xml");
		
		Configurable objectC = Configurable.loadFromConfiguration(new File("testdata/testconfig.xml"));
		assertEquals("This is optionA1", objectC.getOption("optionA1"));
		assertEquals("This is optionA2", objectC.getOption("optionA2"));
		assertEquals("This is optionA3", objectC.getOption("optionA3"));
		assertEquals("This is optionA1", objectC.getOption("optionA4.optionB1"));
		
		assertTrue(new File("testdata/testconfig.xml").delete());
	}
	
}
