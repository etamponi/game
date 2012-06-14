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
import game.plugins.Constraint;
import game.plugins.PluginManager;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import testgame.plugins.ChildAA;
import testgame.plugins.ChildB;
import testgame.plugins.ChildC;
import testgame.plugins.Interface;
import testgame.plugins.Parent;
import testgame.plugins.subpack.ChildD;

public class PluginManagerTest {
	
	public static class ChildNested extends Parent {
		
	}
	
	public class ChildInner extends Parent {
		
	}
	
	@Test
	public void test() {
		PluginManager manager = new PluginManager();
		manager.setOption("packages.add", "testgame");
		
		Set<Class> set = classSet(manager.getInstancesOf(Parent.class));
		Set<Class> real = new HashSet<>();
		real.add(ChildAA.class);
		real.add(ChildB.class);
		real.add(ChildD.class);
		real.add(ChildNested.class);
		assertEquals(4, set.size());
		assertTrue(set.containsAll(real));
		
		set = classSet(manager.getInstancesOf(Interface.class));
		real.clear();
		real.add(ChildAA.class);
		real.add(ChildC.class);
		assertEquals(2, set.size());
		assertTrue(set.containsAll(real));
		
		set = classSet(manager.getCompatibleInstancesOf(Parent.class, new Constraint() {
			@Override
			public boolean isValid(Object o) {
				return o.getClass().getSimpleName().length() < 8;
			}
		}));
		real.clear();
		real.add(ChildAA.class);
		real.add(ChildB.class);
		real.add(ChildD.class);
		assertEquals(3, set.size());
		assertTrue(set.containsAll(real));
	}
	
	private Set<Class> classSet(Set set) {
		Set<Class> ret = new HashSet<>();
		for (Object o: set)
			ret.add(o.getClass());
		return ret;
	}

}
