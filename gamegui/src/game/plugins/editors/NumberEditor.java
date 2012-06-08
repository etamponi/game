package game.plugins.editors;


public class NumberEditor extends TextFieldEditor {
	
	@Override
	protected Object parseText() {
		try {
			if (getModel().getType() == byte.class || getModel().getType() == Byte.class)
				return Byte.parseByte(textField.getText());
			if (getModel().getType() == short.class || getModel().getType() == Short.class)
				return Short.parseShort(textField.getText());
			if (getModel().getType() == int.class || getModel().getType() == Integer.class)
				return Integer.parseInt(textField.getText());
			if (getModel().getType() == long.class || getModel().getType() == Long.class)
				return Long.parseLong(textField.getText());
			if (getModel().getType() == float.class || getModel().getType() == Float.class)
				return Float.parseFloat(textField.getText());
			if (getModel().getType() == double.class || getModel().getType() == Double.class)
				return Double.parseDouble(textField.getText());
		} catch (NumberFormatException ex) {}
		return null;
	}

	@Override
	public Class getBaseEditableClass() {
		return Number.class;
	}

	@Override
	public boolean canEdit(Class type) {
		return super.canEdit(type)
				|| (type.isPrimitive() && !type.equals(boolean.class));
	}

}
