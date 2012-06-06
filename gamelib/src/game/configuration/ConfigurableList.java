package game.configuration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class ConfigurableList<E> extends Configurable implements List<E> {
	
	private ArrayList<E> internal = new ArrayList<>();
	
	public ConfigurableList(Configurable owner) {
		super();
		this.addObserver(owner);
	}

	@Override
	public void setOption(String optionPath, Object content) {
		if (optionPath.startsWith("*.")) {
			String remainingPath = optionPath.substring(2);
			for (E element: internal)
				((Configurable)element).setOption(remainingPath, content);
		} else {
			super.setOption(optionPath, content);
		}
	}

	@Override
	public LinkedList<String> getOptionNames() {
		LinkedList<String> ret = super.getOptionNames();
		for(int i = 0; i < internal.size(); i++)
			ret.add(String.valueOf(i));
		return ret;
	}

	@Override
	public boolean add(E e) {
		add(internal.size(), e);
		return true;
	}

	@Override
	public void add(int index, E element) {
		String indexString = String.valueOf(index);

		internal.add(index, element);
		
		if (element instanceof Configurable) {
			((Configurable)element).addObserver(this);
		}
		
		updateOptionBindings(indexString);
		setChanged();
		notifyObservers(new Change(indexString));
		
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		return internal.addAll(c);
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		return internal.addAll(index, c);
	}

	@Override
	public void clear() {
		/*
		for (E element: internal) {
			if (element instanceof Configurable)
				((Configurable)element).deleteObserver(this);
		}
		*/
		internal.clear();
	}

	@Override
	public boolean contains(Object o) {
		return internal.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return internal.containsAll(c);
	}

	@Override
	public E get(int index) {
		return internal.get(index);
	}

	@Override
	public int indexOf(Object o) {
		return internal.indexOf(o);
	}

	@Override
	public boolean isEmpty() {
		return internal.isEmpty();
	}

	@Override
	public Iterator<E> iterator() {
		return internal.iterator();
	}

	@Override
	public int lastIndexOf(Object o) {
		return internal.lastIndexOf(o);
	}

	@Override
	public ListIterator<E> listIterator() {
		return internal.listIterator();
	}

	@Override
	public ListIterator<E> listIterator(int index) {
		return internal.listIterator(index);
	}

	@Override
	public boolean remove(Object o) {
		if (contains(o) && o instanceof Configurable)
			((Configurable)o).deleteObserver(this);
		
		return internal.remove(o);
	}

	@Override
	public E remove(int index) {
		if (index >= 0 && index < size() && get(index) instanceof Configurable)
			((Configurable)get(index)).deleteObserver(this);
		
		return internal.remove(index);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return internal.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return internal.retainAll(c);
	}

	@Override
	public E set(int index, E element) {
		if (index >= 0 && index < size()) {
			if (get(index) instanceof Configurable)
				((Configurable)get(index)).deleteObserver(this);
		}
		
		E ret = internal.set(index, element);
		
		if (index >= 0 && index < size()) {
			if (element instanceof Configurable)
				((Configurable)element).addObserver(this);
		}
		
		String indexString = String.valueOf(index);
		updateOptionBindings(indexString);
		setChanged();
		notifyObservers(new Change(indexString));
		
		return ret;
	}

	@Override
	public int size() {
		return internal.size();
	}

	@Override
	public List<E> subList(int fromIndex, int toIndex) {
		return internal.subList(fromIndex, toIndex);
	}

	@Override
	public Object[] toArray() {
		return internal.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return internal.toArray(a);
	}

	@Override
	protected Object getLocalOption(String optionName) {
		if (optionName.matches("\\d+")) {
			int index = Integer.parseInt(optionName);
			if (index < size())
				return get(index);
			else
				return null;
		} else
			return super.getLocalOption(optionName);
	}

	@Override
	protected void setLocalOption(String optionName, Object content) {
		if (optionName.matches("\\d+")) {
			int index = Integer.parseInt(optionName);
			if (index < size())
				set(index, (E)content);
		} else {
			switch (optionName) {
			case "add":
				add((E)content);
				break;
			case "remove":
				if (content instanceof Integer)
					remove((int)content);
				else
					remove(content);
				break;
			default:
				super.setLocalOption(optionName, content);
			}
		}
	}

	@Override
	protected String getOptionNameFromContent(Object content) {
		int index = internal.indexOf(content);
		if (index < 0)
			return super.getOptionNameFromContent(content);
		else
			return String.valueOf(index);
	}

}
