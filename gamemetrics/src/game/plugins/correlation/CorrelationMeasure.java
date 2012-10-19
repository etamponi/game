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
package game.plugins.correlation;

import game.configuration.Configurable;
import game.core.Block;
import game.core.DataTemplate;
import game.core.DataTemplate.Data;
import game.core.Dataset;
import game.core.Dataset.SampleIterator;
import game.core.Encoding;
import game.core.blocks.Encoder;
import game.plugins.datatemplates.LabelTemplate;
import game.plugins.encoders.IntegerEncoder;
import game.plugins.encoders.OneHotEncoder;
import game.plugins.pipes.Concatenator;

import java.util.List;

import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

public abstract class CorrelationMeasure extends Configurable {
	
	public class HelperEncoder extends Encoder<LabelTemplate> {
		
		public OneHotEncoder oneHot;
		public IntegerEncoder integer;
		public Concatenator concatenator = new Concatenator();
		
		public HelperEncoder() {
			setAsInternalOptions("oneHot", "integer", "concatenator");

			setOptionBinding("template", "oneHot.template", "integer.template");
			
			setOption("oneHot", new OneHotEncoder());
			setOption("integer", new IntegerEncoder());
			
			concatenator.parents.add(oneHot);
			concatenator.parents.add(integer);
		}

		@Override
		public boolean isCompatible(DataTemplate object) {
			return object instanceof LabelTemplate;
		}

		@Override
		protected Encoding baseEncode(Data input) {
			return null;
		}

		@Override
		public Encoding transform(Data input) {
			return concatenator.transform(input);
		}

		@Override
		protected int getBaseFeatureNumber() {
			return oneHot.getFeatureNumber() + integer.getFeatureNumber();
		}
		
	}
	
	public RealMatrix inputCorrelationMatrix;
	
	public RealVector ioCorrelation;
	
	public double syntheticValue;
	
	public List<RealVector> ioCorrelationPerClass;
	
	public RealVector syntheticValuesPerClass;
	
	public CorrelationMeasure() {
		setAsInternalOptions("inputCorrelationMatrix", "ioCorrelation", "ioCorrelationPerClass");
		setAsInternalOptions("syntheticValuesPerClass", "syntheticValue");
	}
	
	public void evaluateEverything(Dataset dataset, Block inputEncoder, int samples) {
		HelperEncoder helperEncoder = new HelperEncoder();
		helperEncoder.setOption("template", dataset.template.outputTemplate);
		SampleIterator it = dataset.encodedSampleIterator(inputEncoder, helperEncoder, false);
		computeInputCorrelationMatrix(it, samples);
		computeIOCorrelation(it, samples);
		computeSyntheticValues(it, samples);
	}
	
	public abstract void computeInputCorrelationMatrix(SampleIterator it, int samples);
	
	public abstract void computeIOCorrelation(SampleIterator it, int samples);
	
	public abstract void computeSyntheticValues(SampleIterator it, int samples);

}
