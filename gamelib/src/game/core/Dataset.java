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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Dataset extends ArrayList<Instance> {

	private static final long serialVersionUID = -5878467744412002806L;
	
	public Dataset() {
		
	}
	
	public Dataset(Collection<Instance> other) {
		super(other);
	}
	
	public Dataset getFold(int i, int folds) {
		Dataset ret = new Dataset();
		
		int elementsPerFold = size()/folds;
		ret.addAll(this.subList(i*elementsPerFold, (i+1)*elementsPerFold));
		
		return ret;
	}
	
	public Dataset[] getFolds(int folds) {
		Dataset[] ret = new Dataset[folds];
		
		for (int i = 0; i < folds; i++) 
			ret[i] = getFold(i, folds);
		
		return ret;
	}
	
	public Dataset getFoldComplement(int i, int folds) {
		Dataset ret = new Dataset();
		ret.addAll(this);
		ret.removeAll(getFold(i, folds));
		return ret;
	}
	
	public Dataset[] getFoldComplements(int folds) {
		Dataset[] ret = new Dataset[folds];
		
		for (int i = 0; i < folds; i++) 
			ret[i] = getFoldComplement(i, folds);
		
		return ret;
	}
	
	public Dataset getRandomSubset(double percent) {
		assert(percent > 0 && percent <= 1);
		Dataset ret = new Dataset(this);
		
		Collections.shuffle(ret);
		int size = (int)(percent*ret.size());
		ret.retainAll(ret.subList(0, size));
		
		return ret;
	}
	
	public List<EncodedSample> encode(Block inputEncoder, Block outputEncoder) {
		List<EncodedSample> ret = new LinkedList<>();
		
		for(Instance i: this) {
			Encoding inputEncoding = inputEncoder.startTransform(i.getInputData());
			Encoding outputEncoding = outputEncoder.startTransform(i.getOutputData());
			
			for(int k = 0; k < inputEncoding.length(); k++) {
				int outputK = outputEncoding.length() == inputEncoding.length() ? k : 1;
				EncodedSample sample = new EncodedSample(inputEncoding.get(k).clone(), outputEncoding.get(outputK).clone());
				ret.add(sample);
			}
		}
		
		return ret;
	}

}
