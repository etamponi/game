package testgame.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import game.configuration.IList;
import game.configuration.IObject;
import game.configuration.Property;
import game.configuration.listeners.PropertyBinding;

import org.junit.Test;

public class IListTest {
	
	public static class A extends IObject {
		public IList<String> list;
		
		public IList<A> listA;
		
		public IList<A> listB;
		
		public String first;
		
		public String third;
		
		public String other;
		
		public A() {
			setContent("list", new IList<>(String.class));
			setContent("listA", new IList<>(A.class));
			setContent("listB", new IList<>(A.class));
			
			addListener(new PropertyBinding(this, "list.0", "first"));
			addListener(new PropertyBinding(this, "list.2", "third"));
			
			addListener(new PropertyBinding(this, "listA.0.first", "other"));
			
			addListener(new PropertyBinding(this, "other", "listB.*.first"));
		}
	}
	
	@Test
	public void test() {
		A a = new A();
		a.list.add("Hello first");
		a.list.add("Hello second");
		a.list.add("Hello third");
		assertEquals(a.getContent("list.0"), a.first);
		assertEquals(a.getContent("list.2"), a.third);
		
		a.list.remove(2);
		assertEquals(null, a.third);
		
		a.listA.add(a);
		a.listA.get(0).list.add("Hello first nested");
		assertEquals(a.listA.get(0).list.get(0), a.other);
		
		A copy = a.copy();
		
		assertEquals("Hello first", copy.list.get(0));
		assertEquals(copy, copy.listA.get(0));
		
		copy.list.set(0, "Hello first copy");
		assertEquals("Hello first", a.first);
		assertEquals("Hello first copy", copy.first);
		
		copy.listA.set(0, a);
		assertEquals(0, copy.getParentsLinksToThis().size());
		assertEquals("Hello first", copy.other);
		assertEquals(2, a.getParentsLinksToThis().size());
		assertTrue(a.getParentsLinksToThis().contains(new Property(a.listA, "0")));
		assertTrue(a.getParentsLinksToThis().contains(new Property(copy.listA, "0")));
		
		copy.listA.add(0, copy);
		assertTrue(!a.getParentsLinksToThis().contains(new Property(copy.listA, "0")));
		assertTrue(a.getParentsLinksToThis().contains(new Property(copy.listA, "1")));
		
		copy.listA.remove(copy);
		assertTrue(a.getParentsLinksToThis().contains(new Property(copy.listA, "0")));
		assertTrue(!a.getParentsLinksToThis().contains(new Property(copy.listA, "1")));

		copy.listB.add(new A());
		copy.listB.add(new A());
		assertEquals("Hello first", copy.listB.get(1).first);
	}

}
