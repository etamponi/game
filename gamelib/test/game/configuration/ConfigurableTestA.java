package game.configuration;

import game.configuration.Configurable;

public class ConfigurableTestA extends Configurable {
	
	public String optionA1;
	
	public String optionA2;
	
	public String optionA3;
	
	public ConfigurableTestB optionA4;
	
	public ConfigurableTestC optionA5;
	
	public ConfigurableTestA() {
		addOptionBinding("optionA1", "optionA4.optionB1", "optionA5.optionC1");
		addOptionBinding("optionA2", "optionA4.optionB2");
		addOptionBinding("optionA4.optionB3", "optionA5.optionC2");
	}

}
