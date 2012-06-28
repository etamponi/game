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
package game.configuration;

import java.util.Arrays;
import java.util.List;

public class Change {
	
	private String path;
	
	public Change(String path) {
		this.path = path;
	}
	
	public String getPath() {
		return path;
	}
	
	public boolean pathContains(String element) {
		List<String> tokens = Arrays.asList(path.split("\\."));
		return tokens.contains(element);
	}
	
}
