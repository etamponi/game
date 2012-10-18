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
package game.plugins;

import game.configuration.Configurable;
import game.configuration.ConfigurableList;
import game.configuration.errorchecks.ListMustContainCheck;
import game.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

public class PluginManager extends Configurable {
	
	private Reflections internal;
	
	public ConfigurableList packages = new ConfigurableList(this, String.class);
	
	public ConfigurableList paths = new ConfigurableList(this, File.class);
	
	private static PluginManager manager = new PluginManager(); 
	
	public static PluginManager get() {
		return manager;
	}
	
	public PluginManager() {
		setOptionChecks("packages", new ListMustContainCheck("game"));
		/*
		this.addObserver(new Observer() {
			@Override
			public void update(Observable o, Object m) {
				if (m instanceof Change) {
					Change change = (Change)m;
					if (change.getPath().startsWith("packages") || change.getPath().startsWith("paths")) {
						reset();	
					}
				}
			}
		});
		*/
		packages.add("game");
	}
	
	public static void updateManager(PluginManager newManager) {
		ConfigurationBuilder conf = new ConfigurationBuilder();
		
		List<File> paths = getExistentPaths(newManager.paths.getList(File.class));
		if (!paths.isEmpty()) {
			ClassLoader loader = null;
			try {
				URL[] urls = new URL[paths.size()];
				int i = 0;
				for(File path: paths)
					urls[i++] = new URL("file", "localhost", path.getAbsolutePath());
				loader = new URLClassLoader(urls, newManager.getClass().getClassLoader());
				conf.addUrls(urls);
			} catch (IOException e) {
				e.printStackTrace();
			}
			conf.addClassLoader(loader);
			Configurable.setClassLoader(loader);
		}
		
		FilterBuilder filter = new FilterBuilder();
		for (String p: newManager.packages.getList(String.class)) {
			if (p != null && !p.isEmpty())
				filter.include(FilterBuilder.prefix(p));
		}
		conf.filterInputsBy(filter);
		
		conf.addUrls(ClasspathHelper.forClassLoader());
		
		newManager.internal = new Reflections(conf);
		
		manager = newManager;
	}
	
	private static List<File> getExistentPaths(List<File> paths) {
		List<File> ret = new LinkedList<>();
		
		for (File file: paths) {
			if (file != null && file.exists())
				ret.add(file);
		}
		
		return ret;
	}
	
	public static <T> SortedSet<Implementation<T>> getImplementationsOf(Class<T> base) {
		Set<Class<? extends T>> all = manager.internal.getSubTypesOf(base);
		SortedSet<Implementation<T>> ret = new TreeSet<>();
		
		try {
			for (Class<? extends T> c: all) {
			if (Utils.isConcrete(c)	&& Modifier.isPublic(c.getModifiers())
					&& (c.getEnclosingClass() == null || Modifier.isStatic(c.getModifiers())))
				
					ret.add(new Implementation(c.newInstance()));
			}
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		
		return ret;
	}
	
	public static <T> SortedSet<Implementation<T>> getCompatibleImplementationsOf(Class<T> base, Constraint constraint) {
		List<Constraint> temp = new ArrayList<>(1); temp.add(constraint);
		return getCompatibleImplementationsOf(base, temp);
	}
	
	public static <T> SortedSet<Implementation<T>> getCompatibleImplementationsOf(Class<T> base, List<Constraint> constraints) {
		Set<Implementation<T>> all = getImplementationsOf(base);
		SortedSet<Implementation<T>> ret = new TreeSet<>();
		
		for (Implementation i: all) {
			boolean accepted = true;
			for (Constraint c: constraints) {
				if (!c.isValid(i.getContent())) {
					accepted = false;
					break;
				}
			}
			if (accepted)
				ret.add(i);
		}
		
		return ret;
	}

}
