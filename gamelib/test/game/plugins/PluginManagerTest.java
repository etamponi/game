package game.plugins;

import game.plugins.subpack.ChildD;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import static org.junit.Assert.*;

public class PluginManagerTest {
	
	@Test
	public void test() {
		PluginManager manager = new PluginManager();
		
		Set<Class> set = manager.getImplementationsOf(Parent.class);
		Set<Class> real = new HashSet<>();
		real.add(ChildAA.class);
		real.add(ChildB.class);
		real.add(ChildD.class);
		assertEquals(3, set.size());
		assertTrue(set.containsAll(set));
		
		set = manager.getImplementationsOf(Interface.class);
		real.clear();
		real.add(ChildAA.class);
		real.add(ChildC.class);
		assertEquals(2, set.size());
		assertTrue(set.containsAll(real));
	}

}
