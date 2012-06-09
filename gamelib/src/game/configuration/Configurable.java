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
package game.configuration;

import game.configuration.errorchecks.LengthCheck;
import game.plugins.Constraint;
import game.plugins.PluginManager;
import game.plugins.constraints.TrueConstraint;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

public abstract class Configurable extends Observable implements Observer {
	
	public class Change {
		private String path;
		
		public Change(String path) {
			this.path = path;
		}
		
		public String getPath() {
			return path;
		}
		
		public boolean pathContains(String element) {
			List<String> tokens = Arrays.asList(path.split("\\."));
			return tokens.contains(element);
		}
	}
	
	private class RequestForBoundOptions {
		private LinkedList<String> boundOptions;
		private String path;
		
		public RequestForBoundOptions(LinkedList<String> boundOptions, String path) {
			this.boundOptions = boundOptions;
			this.path = path;
		}
		
		public LinkedList<String> getBoundOptions() { return boundOptions; }
		public String getPath() { return path; }
	}
	
	private class OptionBinding {
		private String masterPath;
		private String[] slaves;
		
		public OptionBinding(String masterPath, String... slaves) {
			this.masterPath = masterPath;
			this.slaves = slaves;
		}
		
		public void updateOnChange(String changedOption) {
			Object masterContent = Configurable.this.getOption(masterPath);
			if (isOnPath(masterPath, changedOption)) {
				for (String slave: slaves) {
					if (Configurable.this.getOption(slave) == null)
						Configurable.this.setOption(slave, masterContent);
				}
			} else {
				for (String slave: slaves) {
					String pathToParent = getParentPath(slave);
					if (isOnPath(pathToParent, changedOption) && Configurable.this.getOption(slave) == null)
						Configurable.this.setOption(slave, masterContent);
				}
			}
		}
		
		public LinkedList<String> getBoundOptions(String pathToParent) {
			LinkedList<String> ret = new LinkedList<>();
			for (String slave: slaves) {
				if (isOnPath(slave, pathToParent)) {
					if (slave.split("\\.").length == pathToParent.split("\\.").length+1)
						ret.add(slave.substring(slave.lastIndexOf('.')+1));
				}
			}
			return ret;
		}
		
		private boolean isOnPath(String reference, String changePath) {
			String[] referenceTokens = reference.split("\\.");
			String[] changePathTokens = changePath.split("\\.");
			
			if (changePathTokens.length > referenceTokens.length)
				return false;
			
			for (int i = 0; i < changePathTokens.length; i++) {
				if (referenceTokens[i].equals("*"))
					continue;
				if (!referenceTokens[i].equals(changePathTokens[i]))
					return false;
			}
			
			return true;
		}
		
		private String getParentPath(String optionPath) {
			int dotIndex = optionPath.lastIndexOf('.');
			if (dotIndex < 0)
				return "";
			else
				return optionPath.substring(0, dotIndex);
		}
	}

	public String name;
	
	private LinkedList<OptionBinding> optionBindings = new LinkedList<>();
	private HashMap<String, LinkedList<ErrorCheck>> optionChecks = new HashMap<>();
	protected HashMap<String, Constraint> optionConstraints = new HashMap<>();
	
	public Configurable() {
		this.name = String.format("%s%03d", getClass().getSimpleName(), hashCode() % 1000);
		
		addOptionChecks("name", new LengthCheck(1));
	}
	
	public <T> T getOption(String optionPath) {
		if (optionPath.isEmpty())
			return null;
		
		int dotIndex = optionPath.indexOf('.');
		int firstOptionIndex = dotIndex < 0 ? optionPath.length() : dotIndex;
		Object object = getLocalOption(optionPath.substring(0, firstOptionIndex));
		
		if (dotIndex < 0 || object == null)
			return (T)object;
		else
			return (T)((Configurable)object).getOption(optionPath.substring(firstOptionIndex+1));
	}
	
	public void setOption(String optionPath, Object content) {
		if (optionPath.isEmpty())
			return;
		
		int dotIndex = optionPath.indexOf('.');
		int firstOptionIndex = dotIndex < 0 ? optionPath.length() : dotIndex;
		
		if (dotIndex < 0) {
			setLocalOption(optionPath, content);
		} else {
			Configurable object = getOption(optionPath.substring(0, firstOptionIndex));
			if (object != null)
				object.setOption(optionPath.substring(firstOptionIndex+1), content);
		}
	}
	
	public LinkedList<String> getUnboundOptionNames() {
		LinkedList<String> bound = new LinkedList<>();
		setChanged();
		notifyObservers(new RequestForBoundOptions(bound, ""));
		
		LinkedList<String> ret = getOptionNames();
		ret.removeAll(bound);
		return ret;
	}
	
	public LinkedList<String> getOptionNames() {
		LinkedList<String> ret = new LinkedList<>();
		for (Field f: getClass().getFields())
			ret.add(f.getName());
		return ret;
	}
	
	public HashMap<String, Object> getOptionsMap() {
		HashMap<String, Object> ret = new HashMap<>();
		
		for (String name: getOptionNames())
			ret.put(name, getLocalOption(name));
		
		return ret;
	}
	
	public LinkedList<String> getConfigurationErrors() {
		LinkedList<String> ret = getErrors();
		
		for (Map.Entry<String, Object> entry: getOptionsMap().entrySet()) {
			if (entry.getValue() == null) {
				ret.add(entry.getKey() + ": is null");
			} else {
				if (optionChecks.containsKey(entry.getKey())) {
					for (ErrorCheck check: optionChecks.get(entry.getKey())) {
						String error = check.getError(entry.getValue());
						if (error != null)
							ret.add(entry.getKey() + ": " + error);
					}
				}
				if (entry.getValue() instanceof Configurable)
					ret.addAll(putPrefix(entry.getKey() + ".",
							             ((Configurable)entry.getValue()).getConfigurationErrors()));
			}
		}
	
		return ret;
	}
	
	public <T> Set<T> getCompatibleOptionInstances(String optionName, PluginManager manager) {
		return manager.getCompatibleInstancesOf(getOptionType(optionName), getOptionConstraint(optionName));
	}
	
	public Class getOptionType(String optionName) {
		try {
			return getClass().getField(optionName).getType();
		} catch (NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void update(Observable observedOption, Object message) {
		if (message instanceof Change) {
			Change change = (Change)message;
			String changedOption = getOptionNameFromContent(observedOption);
				   changedOption += change.getPath().isEmpty() ? "" : "." + change.getPath();
			
			updateOptionBindings(changedOption);
			setChanged();
			notifyObservers(new Change(changedOption));
		}
		
		if (message instanceof RequestForBoundOptions) {
			RequestForBoundOptions request = (RequestForBoundOptions)message;
			String pathToParent = getOptionNameFromContent(observedOption) + "." + request.getPath();
			for (OptionBinding binding: optionBindings) {
				request.getBoundOptions().addAll(binding.getBoundOptions(pathToParent));
			}
			
			setChanged();
			notifyObservers(new RequestForBoundOptions(request.getBoundOptions(), pathToParent));
		}
	}
	
	@Override
	public String toString() {
		return name;
	}

	protected void addOptionBinding(String masterPath, String... slaves) {
		optionBindings.add(new OptionBinding(masterPath, slaves));
	}
	
	protected void addOptionChecks(String optionName, ErrorCheck... checks) {
		if (!optionChecks.containsKey(optionName))
			optionChecks.put(optionName, new LinkedList<ErrorCheck>());
		for (ErrorCheck check: checks)
			optionChecks.get(optionName).add(check);
	}
	
	protected void setOptionConstraint(String optionName, Constraint c) {
		optionConstraints.put(optionName, c);
	}
	
	protected Constraint getOptionConstraint(String optionName) {
		if (!optionConstraints.containsKey(optionName))
			return TrueConstraint.getInstance();
		else
			return optionConstraints.get(optionName);
	}
	
	protected Object getLocalOption(String optionName) {
		try {
			Method getter = getMethodByName(getAccessorName("get", optionName));
			if (getter != null) {
				return getter.invoke(this);
			} else {
				return getClass().getField(optionName).get(this);
			}
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	protected void setLocalOption(String optionName, Object content) {
		try {
			Object oldContent = getLocalOption(optionName);
			if (oldContent instanceof Configurable) {
				((Configurable)oldContent).deleteObserver(this);
			}
			
			Method setter = getMethodByName(getAccessorName("set", optionName));
			if (setter != null) {
				setter.invoke(this, content);
			} else {
				getClass().getField(optionName).set(this, content);
			}
			
			if (content instanceof Configurable) {
				((Configurable)content).addObserver(this);
			}
			
			updateOptionBindings(optionName);
			setChanged();
			notifyObservers(new Change(optionName));
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
		}
	}
	
	protected String getOptionNameFromContent(Object content) {
		try {
			for (Field field: getClass().getFields()) {
				if (field.get(this) == content)
					return field.getName();
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	protected void updateOptionBindings(String changedOption) {
		for (OptionBinding binding: optionBindings)
			binding.updateOnChange(changedOption);
	}
	
	protected LinkedList<String> getErrors() {
		return new LinkedList<>();
	}
	
	private String getAccessorName(String prefix, String optionName) {
		return prefix + optionName.substring(0,1).toUpperCase() + optionName.substring(1);
	}
	
	private LinkedList<String> putPrefix(String prefix, LinkedList<String> list) {
		LinkedList<String> ret = new LinkedList<>();
		
		for (String s: list)
			ret.add(prefix + s);
		
		return ret;
	}
	
	private Method getMethodByName(String methodName) {
		for (Method method: getClass().getMethods()) {
			if (method.getName().equals(methodName))
				return method;
		}
		return null;
	}
	
}
