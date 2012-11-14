/*******************************************************************************
 * Copyright (c) 2012 Emanuele.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Emanuele - initial API and implementation
 ******************************************************************************/
package game.configuration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

public class PluginManager {
	
	public static class PluginConfiguration extends IObject {
	
		public IList<String> packages;
	
		public IList<File> paths;
		
		public PluginConfiguration() {
			setContent("packages", new IList<>(String.class));
			setContent("paths", new IList<>(File.class));
		}
		
	}
	
	private static PluginConfiguration configuration;

	private static Reflections internal;
	
	public static PluginConfiguration getConfiguration() {
		return configuration.copy();
	}
	
	public static void initialize(PluginConfiguration pluginConf) {
		if (configuration != null)
			return; // Already initialized
		
		configuration = pluginConf;
		
		ConfigurationBuilder conf = new ConfigurationBuilder();
		
		List<File> paths = getExistentPaths(configuration.paths);
		if (!paths.isEmpty()) {
			ClassLoader loader = null;
			try {
				URL[] urls = new URL[paths.size()];
				int i = 0;
				for(File path: paths)
					urls[i++] = new URL("file", "localhost", path.getAbsolutePath());
				loader = new URLClassLoader(urls, PluginManager.class.getClassLoader());
				conf.addUrls(urls);
			} catch (IOException e) {
				e.printStackTrace();
			}
			conf.addClassLoader(loader);
			IObject.getKryo().setClassLoader(loader);
		}
		
		FilterBuilder filter = new FilterBuilder();
		for (String p: configuration.packages) {
			if (p != null && !p.isEmpty())
				filter.include(FilterBuilder.prefix(p));
		}
		
		conf.filterInputsBy(filter);
		conf.addUrls(ClasspathHelper.forClassLoader());
		
		internal = new Reflections(conf);
	}
	
	public static void initialize(InputStream confStream) {
		PluginConfiguration pluginConf = IObject.load(confStream);
		
		initialize(pluginConf);
	}
	
	private static List<File> getExistentPaths(List<File> paths) {
		List<File> ret = new LinkedList<>();
		
		for (File file: paths) {
			if (file != null && file.exists())
				ret.add(file);
		}
		
		return ret;
	}
	
	public static <T> Set<Class> getImplementationsOf(Class<T> base) {
		Set<Class<? extends T>> all = internal.getSubTypesOf(base);
		Set<Class> ret = new HashSet<>();
		
		for (Class<? extends T> c: all) {
			if (isImplementation(c))
				ret.add(c);
		}
		
		return ret;
	}
	
	private static boolean isImplementation(Class type) {
		return isConcrete(type)
				&& Modifier.isPublic(type.getModifiers())
				&& (type.getEnclosingClass() == null || Modifier.isStatic(type.getModifiers()));
	}
	
	private static boolean isConcrete(Class type) {
		return type.isPrimitive() ||
				(!Modifier.isAbstract(type.getModifiers()) && !Modifier.isInterface(type.getModifiers()));
	}
	
	public static <T> Set<Class> getCompatibleImplementationsOf(Class<T> base, Constraint constraint) {
		List<Constraint> temp = new ArrayList<>(1); temp.add(constraint);
		return getCompatibleImplementationsOf(base, temp);
	}
	
	public static <T> Set<Class> getCompatibleImplementationsOf(Class<T> base, List<Constraint> constraints) {
		Set<Class> all = getImplementationsOf(base);
		Set<Class> ret = new HashSet<>();
		
		for (Class<? extends T> type: all) {
			boolean accepted = true;
			try {
				T instance = type.newInstance();
				for (Constraint c: constraints) {
					if (!c.isValid(instance)) {
						accepted = false;
						break;
					}
				}
			} catch (InstantiationException | IllegalAccessException e) {
				System.err.println("Cannot create instance of " + type + " to verify compatibility.");
			}
			if (accepted)
				ret.add(type);
		}
		
		return ret;
	}

}
