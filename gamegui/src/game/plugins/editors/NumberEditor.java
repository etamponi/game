package game.plugins.editors;


public class NumberEditor extends TextFieldEditor {
	
	@Override
	protected Object parseText() {
		if (getModel().getType() == byte.class || getModel().getType() == Byte.class)
			return Byte.parseByte(getText());
		if (getModel().getType() == short.class || getModel().getType() == Short.class)
			return Short.parseShort(getText());
		if (getModel().getType() == int.class || getModel().getType() == Integer.class)
			return Integer.parseInt(getText());
		if (getModel().getType() == long.class || getModel().getType() == Long.class)
			return Long.parseLong(getText());
		if (getModel().getType() == float.class || getModel().getType() == Float.class)
			return Float.parseFloat(getText());
		if (getModel().getType() == double.class || getModel().getType() == Double.class)
			return Double.parseDouble(getText());
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
