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
package game.core;


import com.ios.IObject;

public class DatasetTemplate extends IObject {
	
	public ElementTemplate sourceTemplate;
	
	public ElementTemplate targetTemplate;
	
	public boolean sequences;
	
	public DatasetTemplate() {
		setContent("sourceTemplate", new ElementTemplate());
		setContent("targetTemplate", new ElementTemplate());
	}
	
	public DatasetTemplate(ElementTemplate sourceTemplate, ElementTemplate targetTemplate) {
		setContent("sourceTemplate", sourceTemplate);
		setContent("targetTemplate", targetTemplate);
	}

	public boolean isReady() {
		return !sourceTemplate.isEmpty() && !targetTemplate.isEmpty();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof DatasetTemplate) {
			DatasetTemplate other = (DatasetTemplate) o;
			return this.sourceTemplate.equals(other.sourceTemplate) && this.targetTemplate.equals(other.targetTemplate);
		} else {
			return false;
		}
	}

}