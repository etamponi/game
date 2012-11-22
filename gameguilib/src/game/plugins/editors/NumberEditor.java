/*******************************************************************************
 * Copyright (c) 2012 Emanuele Tamponi.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Emanuele Tamponi - initial API and implementation
 ******************************************************************************/
package game.plugins.editors;


public class NumberEditor extends TextFieldEditor {
	
	@Override
	protected Object parseText() {
		try {
			if (getModel().getContentType() == byte.class || getModel().getContentType() == Byte.class)
				return Byte.parseByte(textField.getText());
			if (getModel().getContentType() == short.class || getModel().getContentType() == Short.class)
				return Short.parseShort(textField.getText());
			if (getModel().getContentType() == int.class || getModel().getContentType() == Integer.class)
				return Integer.parseInt(textField.getText());
			if (getModel().getContentType() == long.class || getModel().getContentType() == Long.class)
				return Long.parseLong(textField.getText());
			if (getModel().getContentType() == float.class || getModel().getContentType() == Float.class)
				return Float.parseFloat(textField.getText());
			if (getModel().getContentType() == double.class || getModel().getContentType() == Double.class)
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
