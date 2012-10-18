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
package game.configuration.errorchecks;

import game.configuration.ErrorCheck;

import java.io.File;

public class FileExistsCheck implements ErrorCheck<File> {

	@Override
	public String getError(File value) {
		if (!value.exists())
			return "file specified does not exist";
		else
			return null;
	}

}
