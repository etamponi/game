package game.plugins;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import game.base.Subparent;
import game.plugins.subpack.ChildD;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public class PluginManagerTest {
	
	public static class ChildNested extends Parent {
		
	}
	
	public class ChildInner extends Parent {
		
	}
	
	@Test
	public void test() {
		PluginManager manager = new PluginManager();
		
		Set<Class> set = manager.getImplementationsOf(Parent.class);
		Set<Class> real = new HashSet<>();
		real.add(ChildAA.class);
		real.add(ChildB.class);
		real.add(ChildD.class);
		real.add(ChildNested.class);
		assertEquals(4, set.size());
		assertTrue(set.containsAll(set));
		set = manager.getImplementationsOf(Subparent.class);
		assertEquals(4, set.size());
		assertTrue(set.containsAll(set));
		
		set = manager.getImplementationsOf(Interface.class);
		real.clear();
		real.add(ChildAA.class);
		real.add(ChildC.class);
		assertEquals(2, set.size());
		assertTrue(set.containsAll(real));
		
		set = manager.getCompatibleImplementationsOf(Parent.class, new Constraint() {
			@Override
			public boolean isValid(Object o) {
				return o.getClass().getSimpleName().length() < 8;
			}
		});
		real.clear();
		real.add(ChildAA.class);
		real.add(ChildB.class);
		real.add(ChildD.class);
		assertEquals(3, set.size());
		assertTrue(set.containsAll(set));
	}

}
