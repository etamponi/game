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
package game.configuration;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class Change {
	
	private String path;
	
	private Object setter;

	private Set<Configurable> propagators;
	
	Change(String path, Object setter, Set<Configurable> propagators) {
		this.path = path;
		this.setter = setter;
		this.propagators = propagators;
	}
	
	public String getPath() {
		return path;
	}
	
	public Object getSetter() {
		return setter;
	}

	public boolean pathContains(String element) {
		List<String> tokens = Arrays.asList(path.split("\\."));
		return tokens.contains(element);
	}
	
	Set<Configurable> getPropagators() {
		return propagators;
	}
	
}
