package game.core;

public abstract class Tester extends LongTask {
	
	private static final String TESTING = "testing";
	
	public Object startTest() {
		return startTask(TESTING);
	}
	
	protected abstract Object test();

	@Override
	protected Object execute(Object... params) {
		if (getTaskType().equals(TESTING))
			return test();
		else
			return null;
	}

}
