package game.plugins;

import game.configuration.Configurable;
import game.configuration.ConfigurableList;
import game.configuration.errorchecks.ListMustContainCheck;

import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

public class PluginManager extends Configurable {
	
	private Reflections internal;
	
	public ConfigurableList<String> packages = new ConfigurableList<>(this);
	public ConfigurableList<String> paths = new ConfigurableList<>(this);
	
	public PluginManager() {
		addOptionChecks("packages", new ListMustContainCheck("game.plugins"));
		
		this.addObserver(new Observer() {
			@Override
			public void update(Observable o, Object m) {
				if (m instanceof Change) {
					Change change = (Change)m;
					if (change.getPath().contains("packages.") || change.getPath().contains("paths.")) {
						reset();	
					}
				}
			}
		});
		
		packages.add("game.plugins");
	}
	
	private void reset() {
		ConfigurationBuilder conf = new ConfigurationBuilder();
		
		if (!paths.isEmpty()) {
			ClassLoader loader = null;
			try {
				URL[] urls = new URL[paths.size()];
				int i = 0;
				for(String path: paths)
					urls[i++] = new URI(path).toURL();
				loader = new URLClassLoader(urls, getClass().getClassLoader());
			} catch (MalformedURLException | URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			conf.addClassLoader(loader);
		}
		
		for (String p: packages) {
			conf.filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix(p)));
		}
		
		conf.addUrls(ClasspathHelper.forClassLoader());
		
		internal = new Reflections(conf);
	}
	
	public <T> Set<Class> getImplementationsOf(Class<T> base) {
		Set<Class<? extends T>> all = internal.getSubTypesOf(base);
		Set<Class> ret = new HashSet<>();
		
		for (Class c: all) {
			if (!Modifier.isAbstract(c.getModifiers()) && !Modifier.isInterface(c.getModifiers()))
				ret.add(c);
		}
		
		return ret;
	}

}
