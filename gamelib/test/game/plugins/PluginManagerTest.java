package game.plugins;

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
		assertEquals(2, set.size());
		assertTrue(set.containsAll(set));
		
	}

}
