package game.configuration;

import com.thoughtworks.xstream.converters.Converter;

public abstract class BaseConverter implements Converter {
	
	private ClassLoader classLoader = getClass().getClassLoader();

	public ClassLoader getClassLoader() {
		return classLoader;
	}

	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	@Override
	public boolean equals(Object other) {
		return this.getClass().equals(other.getClass());
	}

}
