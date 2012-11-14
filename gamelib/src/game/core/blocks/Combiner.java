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
package game.core.blocks;

import game.configuration.ErrorCheck;
import game.configuration.errorchecks.SizeCheck;
import game.core.Block;

import java.util.List;

public abstract class Combiner extends Transducer {
	
	public Combiner() {
		addErrorCheck("parents", new SizeCheck(1));
		addErrorCheck("parents", new ErrorCheck<List>() {
			@Override public String getError(List value) {
				for (Object element: value) {
					if (!(element instanceof Transducer))
						return "parents of Combiner can only be Transducers";
				}
				return null;
			}
		});
		addErrorCheck("parents", new ErrorCheck<List<Block>>() {
			@Override public String getError(List<Block> value) {
				if (value.isEmpty())
					return null;
				if (outputEncoder == null)
					return null;
				for (Block parent: value) {
					if (!outputEncoder.equals(parent.getContent("outputEncoder")))
						return "parent Transducers must have the same outputEncoder as this Combiner";
				}
				return null;
			}
		});
	}

}
