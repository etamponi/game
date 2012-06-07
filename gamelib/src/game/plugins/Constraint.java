package game.plugins;

public interface Constraint<T> {
	
	boolean isValid(T o);

}
