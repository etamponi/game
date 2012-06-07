package game.plugins.editors;


public class StringEditor extends TextFieldEditor {

	@Override
	protected Object parseText() {
		return getText();
	}

	@Override
	public Class getBaseEditableClass() {
		return String.class;
	}

}
