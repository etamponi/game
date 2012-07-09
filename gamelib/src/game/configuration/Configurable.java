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
import game.plugins.Implementation;
import game.plugins.PluginManager;
import game.plugins.constraints.TrueConstraint;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.SortedSet;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import com.thoughtworks.xstream.io.xml.DomDriver;

public abstract class Configurable extends Observable implements Observer {
	
	@XStreamOmitField
	private static XStream configStream = new XStream(new DomDriver());
	@XStreamOmitField
	private static ConfigurationConverter converter = new ConfigurationConverter();
	
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
	
	public String name;
	
	private LinkedList<OptionBinding> optionBindings = new LinkedList<>();
	private HashMap<String, LinkedList<ErrorCheck>> optionChecks = new HashMap<>();
	protected HashMap<String, Constraint> optionConstraints = new HashMap<>();
	private LinkedList<String> omittedFromErrorCheck = new LinkedList<>();
	private LinkedList<String> omittedFromConfiguration = new LinkedList<>();
	private LinkedList<String> internalOptions = new LinkedList<>();
	private boolean notify = true;
	
	static {
		configStream.registerConverter(converter);
	}
	
	public Configurable() {
		this.name = String.format("%s%03d", getClass().getSimpleName(), hashCode() % 1000);
		
		setOptionChecks("name", new LengthCheck(1));
	}
	
	public static <T> T createFromConfiguration(File configFile) {
		return (T)configStream.fromXML(configFile);
	}
	
	public static void setClassLoader(ClassLoader loader) {
		configStream.setClassLoader(loader);
		converter.setClassLoader(loader);
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
		setOption(optionPath, content, true, null);
	}
	
	public void setOption(String optionPath, Object content, boolean notify, Object setter) {
		if (optionPath.isEmpty())
			return;
		
		this.notify = notify;
		
		int dotIndex = optionPath.indexOf('.');
		int firstOptionIndex = dotIndex < 0 ? optionPath.length() : dotIndex;
		
		if (dotIndex < 0) {
			setLocalOption(optionPath, content, setter);
		} else {
			Configurable object = (Configurable)getLocalOption(optionPath.substring(0, firstOptionIndex));
			if (object != null)
				object.setOption(optionPath.substring(firstOptionIndex+1), content, notify, setter);
		}
		
		this.notify = true;
	}
	
	public LinkedList<String> getUnboundOptionNames() {
		LinkedList<String> bound = new LinkedList<>();
		setChanged();
		notifyObservers(new RequestForBoundOptions(bound, ""));
		
		LinkedList<String> ret = getOptionNames();
		ret.removeAll(bound);
		return ret;
	}
	
	public LinkedList<String> getAllOptionNames() {
		LinkedList<String> ret = new LinkedList<>();
		for (Field f: getClass().getFields())
			if (!Modifier.isStatic(f.getModifiers()) && !Modifier.isFinal(f.getModifiers()))
				ret.add(f.getName());
		return ret;
	}
	
	public LinkedList<String> getOptionNames() {
		LinkedList<String> ret = getAllOptionNames();
		ret.removeAll(internalOptions);
		return ret;
	}
	
	public HashMap<String, Object> getOptionsMap() {
		HashMap<String, Object> ret = new HashMap<>();
		
		for (String name: getAllOptionNames())
			ret.put(name, getLocalOption(name));
		
		return ret;
	}
	
	public LinkedList<String> getConfigurationErrors() {
		LinkedList<String> ret = getErrors();
		
		ret.addAll(getConfigurationErrors(new HashSet<Configurable>()));
		
		return ret;
	}
	
	private LinkedList<String> getConfigurationErrors(Set<Configurable> seen) {
		LinkedList<String> ret = new LinkedList<>();
		seen.add(this);
		for (Map.Entry<String, Object> entry: getOptionsMap().entrySet()) {
			if (entry.getValue() == null && !internalOptions.contains(entry.getKey())) {
				ret.add(entry.getKey() + ": is null");
			} else {
				if (optionChecks.containsKey(entry.getKey())) {
					for (ErrorCheck check: optionChecks.get(entry.getKey())) {
						String error = check.getError(entry.getValue());
						if (error != null)
							ret.add(entry.getKey() + ": " + error);
					}
				}
				if (entry.getValue() instanceof Configurable
						&& !seen.contains(entry.getValue())
						&& !omittedFromErrorCheck.contains(entry.getKey()))
					ret.addAll(putPrefix(entry.getKey() + ".",
							             ((Configurable)entry.getValue()).getConfigurationErrors(seen)));
			}
		}
		
		// TODO All not respected binding.
	
		return ret;
	}
	
	public <T> SortedSet<Implementation<T>> getCompatibleOptionImplementations(String optionName, PluginManager manager) {
		return manager.getCompatibleImplementationsOf(getOptionType(optionName), getOptionConstraint(optionName));
	}
	
	public Class getOptionType(String optionName) {
		try {
			return getClass().getField(optionName).getType();
		} catch (NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public String getOptionNameFromContent(Object content) {
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
	
	public String getConfiguration() {
		return configStream.toXML(this);
	}
	
	public void loadConfiguration(String fileName) {
		configStream.fromXML(new File(fileName), this);
	}

	public void saveConfiguration(String fileName) {
		try {
			FileOutputStream stream = new FileOutputStream(new File(fileName));
			configStream.toXML(this, stream);
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public <T extends Configurable> T cloneConfiguration() {
		Configurable clone = (Configurable)configStream.fromXML(this.getConfiguration());
		clone.name = String.format("%s%03d", getClass().getSimpleName(), clone.hashCode() % 1000);
		return (T)clone;
	}
	
	@Override
	public void update(Observable observedOption, Object message) {
		if (message instanceof Change) {
			Change change = (Change)message;
			String changedOption = getOptionNameFromContent(observedOption);
				   changedOption += change.getPath().isEmpty() ? "" : "." + change.getPath();

			observedOption.deleteObserver(this);
			propagateUpdate(changedOption, change.getSetter());
			observedOption.addObserver(this);
		}
		
		if (message instanceof RequestForBoundOptions) {
			RequestForBoundOptions request = (RequestForBoundOptions)message;
			String pathToParent = getOptionNameFromContent(observedOption) + "." + request.getPath();
			for (OptionBinding binding: optionBindings) {
				request.getBoundOptions().addAll(binding.getBoundOptions(pathToParent));
			}
			
			observedOption.deleteObserver(this);
			setChanged();
			notifyObservers(new RequestForBoundOptions(request.getBoundOptions(), pathToParent));
			observedOption.addObserver(this);
		}
	}

	public boolean isOmittedFromConfiguration(String optionName) {
		return omittedFromConfiguration.contains(optionName);
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	protected void propagateUpdate(String changedOption, Object setter) {
		if (notify) {
			if (!changedOption.isEmpty())
				updateOptionBindings(changedOption);
			setChanged();
			notifyObservers(new Change(changedOption, setter));
		}
	}
	
	protected void setInternalOptions(String... optionNames) {
		for (String optionName: optionNames)
			internalOptions.add(optionName);
	}
	
	protected void omitFromErrorCheck(String... optionNames) {
		for (String optionName: optionNames)
			omittedFromErrorCheck.add(optionName);
	}
	
	protected void omitFromConfiguration(String... optionNames) {
		for (String optionName: optionNames) {
			omittedFromConfiguration.add(optionName);
			omittedFromErrorCheck.add(optionName);
		}
	}

	protected void setOptionBinding(String masterPath, String... slaves) {
		optionBindings.add(new OptionBinding(this, masterPath, slaves));
	}
	
	protected void setOptionChecks(String optionName, ErrorCheck... checks) {
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
			//e.printStackTrace();
			return null;
		}
	}
	
	protected void setLocalOption(String optionName, Object content, Object setter) {
		try {
			Object oldContent = getLocalOption(optionName);
			if (oldContent instanceof Configurable) {
				((Configurable)oldContent).deleteObserver(this);
			}
			
			Method setterMethod = getMethodByName(getAccessorName("set", optionName));
			if (setterMethod != null) {
				setterMethod.invoke(this, content);
			} else {
				getClass().getField(optionName).set(this, content);
			}
			
			if (content instanceof Configurable) {
				((Configurable)content).addObserver(this);
			}
			
			propagateUpdate(optionName, setter);
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchFieldException | SecurityException e) {
			//e.printStackTrace();
		}
	}
	
	protected void updateOptionBindings(String changedOption) {
		for (OptionBinding binding: optionBindings)
			binding.updateOnChange(changedOption);
	}
	
	protected LinkedList<String> getErrors() {
		LinkedList<String> ret = new LinkedList<>();
		
		
		
		return ret;
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
