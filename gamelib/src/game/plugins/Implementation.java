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
package game.plugins;

public class Implementation<T> implements Comparable<Implementation<T>> {
	
	public T content;
	
	public Implementation(T content) {
		this.content = content;
	}
	
	public T getContent() {
		return content;
	}
	
	@Override
	public String toString() {
		if (content == null)
			return "<null>";
		else
			return content.getClass().getSimpleName();
	}

	@Override
	public int compareTo(Implementation o) {
		return this.toString().compareTo(o.toString());
	}

}
