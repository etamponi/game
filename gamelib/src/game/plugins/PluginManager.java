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

import game.configuration.Change;
import game.configuration.Configurable;
import game.configuration.ConfigurableList;
import game.configuration.errorchecks.ListMustContainCheck;
import game.utils.Utils;

import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Observable;
import java.util.Observer;
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
	public ConfigurableList paths = new ConfigurableList(this, String.class);
	
	public PluginManager() {
		setOptionChecks("packages", new ListMustContainCheck("game"));
		
		this.addObserver(new Observer() {
			@Override
			public void update(Observable o, Object m) {
				if (m instanceof Change) {
					Change change = (Change)m;
					if (change.getPath().startsWith("packages.") || change.getPath().startsWith("paths.")) {
						reset();	
					}
				}
			}
		});
		
		packages.add("game");
	}
	
	private void reset() {
		ConfigurationBuilder conf = new ConfigurationBuilder();
		
		if (!paths.isEmpty()) {
			ClassLoader loader = null;
			try {
				URL[] urls = new URL[paths.size()];
				int i = 0;
				for(String path: paths.getList(String.class))
					urls[i++] = new URI(path).toURL();
				loader = new URLClassLoader(urls, getClass().getClassLoader());
			} catch (MalformedURLException | URISyntaxException e) {
				e.printStackTrace();
			}
			
			conf.addClassLoader(loader);
		}
		
		FilterBuilder filter = new FilterBuilder();
		for (String p: packages.getList(String.class)) {
			filter.include(FilterBuilder.prefix(p));
		}
		conf.filterInputsBy(filter);
		
		conf.addUrls(ClasspathHelper.forClassLoader());
		
		internal = new Reflections(conf);
	}
	
	public <T> SortedSet<Implementation<T>> getImplementationsOf(Class<T> base) {
		Set<Class<? extends T>> all = internal.getSubTypesOf(base);
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
	
	public <T> SortedSet<Implementation<T>> getCompatibleImplementationsOf(Class<T> base, Constraint c) {
		Set<Implementation<T>> all = getImplementationsOf(base);
		SortedSet<Implementation<T>> ret = new TreeSet<>();
		
		for (Implementation i: all) {
			if (c.isValid(i.getContent()))
				ret.add(i);
		}
		
		return ret;
	}

}
