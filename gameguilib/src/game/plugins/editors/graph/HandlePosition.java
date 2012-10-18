/*******************************************************************************
 * Copyright (c) 2012 Emanuele.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Emanuele - initial API and implementation
 ******************************************************************************/
package game.plugins.editors.graph;

import java.io.Serializable;

public class HandlePosition implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public double x, y;
	
	public HandlePosition() {
		
	}
	
	public HandlePosition(double x, double y) {
		this.x = x;
		this.y = y;
	}
}
