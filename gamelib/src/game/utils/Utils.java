package game.utils;

import java.lang.reflect.Modifier;

public class Utils {

	public static boolean isConcrete(Class type) {
		return !Modifier.isAbstract(type.getModifiers()) && !Modifier.isInterface(type.getModifiers());
	}
	
	public static boolean isConcreteSubtype(Class type, Class base) {
		return isConcrete(type) && base.isAssignableFrom(type);
	}
	
}
