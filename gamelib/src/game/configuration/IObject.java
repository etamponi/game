package game.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.objenesis.strategy.StdInstantiatorStrategy;
import org.pcollections.HashTreePSet;
import org.pcollections.PSet;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.reflectasm.FieldAccess;

public class IObject {

	public static final int MAXIMUM_CHANGE_PROPAGATION = 5;

	private static final Kryo kryo = new Kryo();

	static {
		kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
	}
	
	public static Kryo getKryo() {
		return kryo;
	}
	
	public static <T extends IObject> T load(File inFile) {
		try {
			FileInputStream in = new FileInputStream(inFile);
			T ret = (T)load(in);
			in.close();
			return ret;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static <T extends IObject> T load(InputStream in) {
		Input input = new Input(in);
		T ret = (T) kryo.readClassAndObject(input);
		input.close();
		return ret;
	}

	private final List<Property> parentsLinkToThis = new ArrayList<>();

	private final List<Listener> listeners = new ArrayList<>();
	
	private final Map<Property, List<ErrorCheck>> errorChecks = new HashMap<>();
	
	private final Map<Property, List<Constraint>> constraints = new HashMap<>();

	public String name = String.format("%s-%02d", getClass().getSimpleName(), hashCode() % 100);
	
	protected void addConstraint(String propertyName, Constraint constraint) {
		Property property = new Property(this, propertyName);
		if (!constraints.containsKey(property))
			constraints.put(property, new ArrayList<Constraint>());
		constraints.get(property).add(constraint);
	}

	protected void addErrorCheck(String propertyName, ErrorCheck check) {
		Property property = new Property(this, propertyName);
		if (!errorChecks.containsKey(property))
			errorChecks.put(property, new ArrayList<ErrorCheck>());
		errorChecks.get(property).add(check);
	}

	protected void addListener(Listener listener) {
		this.listeners.add(listener);
	}

	protected Property appendChild(Property path, Property child) {
		if (path.getPath().isEmpty())
			return child;
		else
			return new Property(path.getRoot(), path.getPath() + "." + child.getPath());
	}

	private List<String> checkErrors(Property property) {
		List<String> ret = new ArrayList<>();
		Object content = property.getContent();
		if (content == null) {
			ret.add("is null");
		} else {
			if (errorChecks.containsKey(property)) {
				for (ErrorCheck check: errorChecks.get(property))
					ret.add(check.getError(content));
			}
		}
		
		return ret;
	}

	public <T extends IObject> T copy() {
		List<Property> temp = new ArrayList<>(parentsLinkToThis);
		parentsLinkToThis.clear();
		
		IObject copy = kryo.copy(this);

		copy.removeInvalidLinks(copy, new HashSet<IObject>(), new HashSet<IObject>());
		
		parentsLinkToThis.addAll(temp);

		return (T) copy;
	}

	private boolean descendFrom(IObject ancestor) {
		if (this == ancestor)
			return true;
		for (Property linkToThis : parentsLinkToThis) {
			if (linkToThis.getRoot().descendFrom(ancestor))
				return true;
		}
		return false;
	}

	public void detach() {
		for (Property linkToThis : new ArrayList<>(parentsLinkToThis))
			linkToThis.setContent(null);

		for (Property intelligentProperty : getIntelligentProperties())
			intelligentProperty.setContent(null);
	}

	public List<Property> getBoundProperties() {
		List<Property> ret = new ArrayList<>();
		recursivelyFindBoundProperties(new Property(this, ""), ret, HashTreePSet.<Property> empty());
		return ret;
	}
	
	public Set<Class> getCompatibleContentTypes(String propertyName) {
		Property path = new Property(this, propertyName);
		
		List<Constraint> list = new ArrayList<>();
		recursivelyFindConstraints(path, list, HashTreePSet.<Property> empty());
		
		return PluginManager.getCompatibleImplementationsOf(getContentType(propertyName, false), list);
	}
	
	public <T> T getContent(String propertyPath) {
		if (propertyPath.isEmpty())
			return null;

		int firstSplit = propertyPath.indexOf('.');
		if (firstSplit < 0) {
			return (T)getLocal(propertyPath);
		} else {
			String localProperty = propertyPath.substring(0, firstSplit);
			String remainingPath = propertyPath.substring(firstSplit + 1);
			IObject local = (IObject) getLocal(localProperty);
			if (local != null)
				return local.getContent(remainingPath);
			else
				return null;
		}
	}
	
	public <T> T getContent(String propertyPath, Class<T> contentType) {
		return (T) getContent(propertyPath);
	}
	
	public Class<?> getContentType(String propertyName, boolean runtime) {
		if (runtime) {
			Object content = getLocal(propertyName);
			return content == null ? getContentType(propertyName, false) : content.getClass();
		} else {
			try {
				return getClass().getField(propertyName).getType();
			} catch (NoSuchFieldException | SecurityException e) {
				return null;
			}
		}
	}
	
	public Map<Property, List<String>> getErrors() {
		Map<Property, List<String>> ret = new LinkedHashMap<>();

		recursivelyFindErrors(new Property(this, ""), ret, new HashSet<IObject>());
		
		return ret;
	}

	public List<String> getFieldPropertyNames() {
		List<String> ret = new ArrayList<>();
		
		Stack<Class<?>> types = new Stack<>();
		types.add(getClass());
		while (!types.peek().equals(IObject.class))
			types.push(types.peek().getSuperclass());

		while(!types.isEmpty()) {
			Class<?> type = types.pop();
			for (Field field : type.getDeclaredFields()) {
				int mod = field.getModifiers();
				if (Modifier.isPublic(mod) && !Modifier.isStatic(mod)
						&& !Modifier.isFinal(mod))
					ret.add(field.getName());
			}
		}
		
		return ret;
	}

	protected List<Property> getInstanceProperties() {
		return new ArrayList<>();
	}

	public List<Property> getIntelligentProperties() {
		List<Property> ret = new ArrayList<>();
		for (Property property: getProperties()) {
			if (property.getContent() instanceof IObject)
				ret.add(property);
		}
		return ret;
	}

	protected Object getLocal(String propertyName) {
		// TODO Add access through getter
		FieldAccess fieldAccess = FieldAccess.get(getClass());
		return fieldAccess.get(this, propertyName);
	}
	
	public List<Property> getParentsLinksToThis() {
		return Collections.unmodifiableList(parentsLinkToThis);
	}
	
	protected List<Property> getParentsLinksToThis(boolean editable) {
		return parentsLinkToThis;
	}

	public List<Property> getProperties() {
		List<Property> ret = new ArrayList<>();

		for(String name: getFieldPropertyNames())
			ret.add(new Property(this, name));
		
		ret.addAll(getInstanceProperties());
		return ret;
	}

	public List<Property> getUnboundProperties() {
		List<Property> ret = getProperties();
		ret.removeAll(getBoundProperties());
		return ret;
	}

	private void innerSetLocal(String propertyName, Object content) {
		Property property = new Property(this, propertyName);

		Object oldContent = getLocal(propertyName);

		if (oldContent == content)
			return;

		if (oldContent instanceof IObject) {
			((IObject) oldContent).parentsLinkToThis.remove(property);
		}

		setLocal(propertyName, content);

		if (content instanceof IObject) {
			((IObject) content).parentsLinkToThis.add(property);
		}

		propagateChange(property, HashTreePSet.<Property> empty(), 0);
	}

	private void notifyChange(Property changedPath) {
		for (Listener listener : listeners) {
			if (listener.isListeningOn(changedPath))
				listener.action(changedPath);
		}
	}

	protected Property prependParent(Property parent, Property path) {
		if (path.getPath().isEmpty())
			return parent;
		else
			return new Property(parent.getRoot(), parent.getPath() + "." + path.getPath());
	}

	public String printErrors() {
		Map<Property, List<String>> errors = getErrors();
		StringBuilder builder = new StringBuilder();
		
		for(Property property: errors.keySet()) {
			List<String> currentErrors = errors.get(property);
			if (currentErrors.isEmpty())
				continue;
			
			builder.append(property).append(":\n");
			for(String error: currentErrors)
				builder.append("\t").append(error).append("\n");
		}
		
		return builder.toString();
	}
	
	protected void propagateChange(Property property) {
		propagateChange(property, HashTreePSet.<Property> empty(), 0);
	}
	
	private void propagateChange(Property property, PSet<Property> seen, int level) {
		property.getRoot().notifyChange(property);

		if (level == MAXIMUM_CHANGE_PROPAGATION)
			return;

		for (Property linkToThis : parentsLinkToThis) {
			if (seen.contains(linkToThis))
				continue;
			linkToThis.getRoot().propagateChange(prependParent(linkToThis, property), seen.plus(linkToThis), level + 1);
		}
	}

	private void recursivelyFindBoundProperties(Property prefixPath, List<Property> list, PSet<Property> seen) {
		for (Listener l: listeners)
			list.addAll(l.getBoundProperties(prefixPath));

		for (Property linkToThis: parentsLinkToThis) {
			if (seen.contains(linkToThis))
				continue;
			linkToThis.getRoot().recursivelyFindBoundProperties(prependParent(linkToThis, prefixPath), list, seen.plus(linkToThis));
		}
	}

	private void recursivelyFindConstraints(Property path, List<Constraint> list, PSet<Property> seen) {
		for(Property constrained: constraints.keySet()) {
			if (constrained.includes(path))
				list.addAll(constraints.get(constrained));
		}

		for (Property linkToThis: parentsLinkToThis) {
			if (seen.contains(linkToThis))
				continue;
			linkToThis.getRoot().recursivelyFindConstraints(prependParent(linkToThis, path), list, seen.plus(linkToThis));
		}
	}

	private void recursivelyFindErrors(Property basePath, Map<Property, List<String>> errors, Set<IObject> seen) {
		if (seen.contains(this))
			return;
		else
			seen.add(this);
		
		for(Property property: getUnboundProperties()) {
			Property complete = appendChild(basePath, property);
			
			List<String> list = checkErrors(property);
			if (!list.isEmpty())
				errors.put(complete, list);
			
			if (property.getContent() instanceof IObject) {
				property.getContent(IObject.class).recursivelyFindErrors(complete, errors, seen);
			}
		}
	}

	private void removeInvalidLinks(IObject root, HashSet<IObject> descendents, HashSet<IObject> nonDescendents) {
		for (Property linkToThis : new ArrayList<>(parentsLinkToThis)) {
			IObject parent = linkToThis.getRoot();

			if (descendents.contains(parent)) {
				continue;
			}

			if (nonDescendents.contains(parent)) {
				linkToThis.setContent(null);
				continue;
			}

			if (parent.descendFrom(root)) {
				descendents.add(parent);
			} else {
				nonDescendents.add(parent);
				linkToThis.setContent(null);
			}
			
			for (Property childProperty: getIntelligentProperties()) {
				childProperty.getContent(IObject.class).removeInvalidLinks(root, descendents, nonDescendents);
			}

		}
		
	}

	public void setContent(String propertyPath, Object content) {
		if (propertyPath.isEmpty())
			return;

		int firstSplit = propertyPath.indexOf('.');
		if (firstSplit < 0) {
			innerSetLocal(propertyPath, content);
		} else {
			String localProperty = propertyPath.substring(0, firstSplit);
			String remainingPath = propertyPath.substring(firstSplit + 1);
			IObject local = (IObject) getLocal(localProperty);
			if (local != null)
				local.setContent(remainingPath, content);
		}
	}
	
	protected void setLocal(String propertyName, Object content) {
		// TODO Access through setter
		FieldAccess fieldAccess = FieldAccess.get(getClass());
		fieldAccess.set(this, propertyName, content);
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	public void write(File outFile) {
		OutputStream out;
		try {
			out = new FileOutputStream(outFile);
			write(out);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void write(OutputStream out) {
		Output output = new Output(out);
		kryo.writeClassAndObject(output, this.copy());
		output.close();
	}

}
