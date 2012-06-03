package game.main;

import game.configuration.Configurable;
import game.configuration.ConfigurableTestA;
import game.configuration.ConfigurableTestB;
import game.configuration.ConfigurableTestC;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Configurable object = new ConfigurableTestA(), object2;
		
		object.setOption("optionA4", new ConfigurableTestB());
		object.setOption("optionA1", "This is optionA1");
		object.setOption("optionA5", new ConfigurableTestC());
		object.setOption("optionA2", "This is optionA2");
		object.setOption("optionA3", "This is optionA3");
		object.setOption("optionA4.optionB3", "This is optionB3");
		object.setOption("optionA5.optionC3", "This is optionC3");
		
		System.out.println(String.format("%s %s %s",
				object.getOption("optionA1"), object.getOption("optionA2"), object.getOption("optionA3")));
		
		object2 = object.getOption("optionA4");
		for(String s: object2.getUnboundOptionNames())
			System.out.println("optionA4." + s + " is unbound.");
		System.out.println(String.format("optionA4: %s %s %s", object2.getOption("optionB1"), object2.getOption("optionB2"), object2.getOption("optionB3")));
		
		object2 = object.getOption("optionA5");
		for(String s: object2.getUnboundOptionNames())
			System.out.println("optionA5." + s + " is unbound.");
		System.out.println(String.format("optionA5: %s %s %s", object2.getOption("optionC1"), object2.getOption("optionC2"), object2.getOption("optionC3")));
		
		object.setOption("optionA5", new ConfigurableTestC());
		object.setOption("optionA5.optionC3", "This is the new optionC3");
		for(String s: object2.getUnboundOptionNames())
			System.out.println("old optionA5." + s + " is unbound.");
		System.out.println(String.format("old optionA5: %s %s %s", object2.getOption("optionC1"), object2.getOption("optionC2"), object2.getOption("optionC3")));
		
		object2 = object.getOption("optionA5");
		for(String s: object2.getUnboundOptionNames())
			System.out.println("new optionA5." + s + " is unbound.");
		System.out.println(String.format("new optionA5: %s %s %s", object2.getOption("optionC1"), object2.getOption("optionC2"), object2.getOption("optionC3")));
		
	}

}
