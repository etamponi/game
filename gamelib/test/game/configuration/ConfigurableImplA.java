package game.configuration;



public class ConfigurableImplA extends Configurable {
	
	public String optionA1;
	
	public String optionA2;
	
	public String optionA3;
	
	public ConfigurableImplB optionA4;
	
	public ConfigurableImplC optionA5;
	
	public ConfigurableList optionList = new ConfigurableList(this, ConfigurableImplB.class);
	
	private class StringLengthCheck implements ErrorCheck<String> {
		
		private int minimumLength;
		
		public StringLengthCheck(int minimumLength) {
			this.minimumLength = minimumLength;
		}
		
		@Override
		public String getError(String value) {
			if (value.length() < minimumLength)
				return "should have at least " + minimumLength + " characters";
			else
				return null;
		}

	}
	
	public ConfigurableImplA() {
		addOptionBinding("optionA1",			"optionA4.optionB1", "optionA5.optionC1");
		addOptionBinding("optionA2",			"optionA4.optionB2");
		addOptionBinding("optionA4.optionB3",	"optionA5.optionC2");
		addOptionBinding("optionA3",			"optionList.*.optionB3");
		
		addOptionChecks("optionA3", new StringLengthCheck(20));
	}

}
