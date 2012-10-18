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

import java.util.LinkedList;

public class OptionBinding {
	private final Configurable owner;
	private final String masterPath;
	private final String[] slaves;
	
	OptionBinding(Configurable owner, String masterPath, String... slaves) {
		this.owner = owner;
		this.masterPath = masterPath;
		this.slaves = slaves;
	}
	
	void updateOnChange(String changedOption) {
		Object masterContent;
		if (masterPath.equals("none"))
			return;
		if (masterPath.equals("self"))
			masterContent = owner;
		else
			masterContent = owner.getOption(masterPath);
		if (isOnPath(masterPath, changedOption)) {
			for (String slave: slaves) {
				owner.setOption(slave, masterContent);
			}
		} else {
			for (String slave: slaves) {
				String pathToParent = getParentPath(slave);
				if (isOnPath(pathToParent, changedOption))
					owner.setOption(slave, masterContent);
			}
		}
	}
	
	LinkedList<String> getBoundOptions(String pathToParent) {
		LinkedList<String> ret = new LinkedList<>();
		for (String slave: slaves) {
			
			if (pathToParent == "") {
				if (!slave.contains("."))
					ret.add(slave);
			} else if (isOnPath(slave, pathToParent)) {
				if (slave.split("\\.").length == pathToParent.split("\\.").length+1)
					ret.add(slave.substring(slave.lastIndexOf('.')+1));
			}
			
		}
		return ret;
	}
	
	private boolean isOnPath(String reference, String changePath) {
		String[] referenceTokens = reference.split("\\.");
		String[] changePathTokens = changePath.split("\\.");
		
		if (changePathTokens.length > referenceTokens.length)
			return false;
		
		for (int i = 0; i < changePathTokens.length; i++) {
			if (referenceTokens[i].equals("*"))
				continue;
			if (!referenceTokens[i].equals(changePathTokens[i]))
				return false;
		}
		
		return true;
	}
	
	private String getParentPath(String optionPath) {
		int dotIndex = optionPath.lastIndexOf('.');
		if (dotIndex < 0)
			return "";
		else
			return optionPath.substring(0, dotIndex);
	}
}
