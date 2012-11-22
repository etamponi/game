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
package testgame.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import game.core.DataTemplate;
import game.core.DataTemplate.Data;
import game.core.Decoder;
import game.core.Encoding;
import game.core.InstanceTemplate;
import game.core.blocks.Encoder;
import game.core.blocks.PredictionGraph;
import game.core.blocks.Transducer;
import game.plugins.datatemplates.LabelTemplate;
import game.plugins.datatemplates.VectorTemplate;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.ios.IObject;
import com.ios.PluginManager;
import com.ios.PluginManager.PluginConfiguration;

public class GraphTest {
	
	public static class EncoderImplA extends Encoder<VectorTemplate> {
		@Override
		public Encoding baseEncode(Data inputData) {
			return null;
		}

		@Override
		public boolean isCompatible(DataTemplate object) {
			return object instanceof VectorTemplate;
		}

		@Override
		protected int getBaseFeatureNumber() {
			return 0;
		}

		@Override
		protected FeatureType getBaseFeatureType(int featureIndex) {
			return null;
		}
	}
	
	public static class EncoderImplB extends Encoder<LabelTemplate> {
		@Override
		public Encoding baseEncode(Data inputData) {
			return null;
		}

		@Override
		public boolean isCompatible(DataTemplate object) {
			return object instanceof LabelTemplate;
		}

		@Override
		protected int getBaseFeatureNumber() {
			return 0;
		}

		@Override
		protected FeatureType getBaseFeatureType(int featureIndex) {
			return null;
		}
	}
	
	public static class EncoderImplC extends EncoderImplB {
		
	}
	
	public static class DecoderImplA extends Decoder<EncoderImplA> {
		@Override
		protected Data baseDecode(Encoding outputEncoded) {
			return null;
		}

		@Override
		public boolean isCompatible(Encoder object) {
			return object instanceof EncoderImplA;
		}
	}
	
	public static class DecoderImplB extends Decoder<EncoderImplB> {
		@Override
		protected Data baseDecode(Encoding outputEncoded) {
			return null;
		}

		@Override
		public boolean isCompatible(Encoder object) {
			return object instanceof EncoderImplB;
		}
	}
	
	public static class ClassifierImplA extends Transducer {
		@Override
		public Encoding transform(Data inputData) {
			return null;
		}
		@Override
		public boolean isCompatible(InstanceTemplate object) {
			return true;
		}

		@Override
		public FeatureType getFeatureType(int featureIndex) {
			return null;
		}
	}
	
	public static class ClassifierImplB extends Transducer {
		@Override
		public Encoding transform(Data inputData) {
			return null;
		}
		@Override
		public boolean isCompatible(InstanceTemplate object) {
			return false;
		}

		@Override
		public FeatureType getFeatureType(int featureIndex) {
			return null;
		}
	}

	@Test
	public void test() {
		PluginConfiguration conf = new PluginConfiguration();
		conf.packages.remove("game");
		conf.packages.add("testgame");
		PluginManager.initialize(conf);
		
		PredictionGraph graph = new PredictionGraph();
		
		graph.setContent("template", new InstanceTemplate());
		graph.setContent("template.inputTemplate", new VectorTemplate());
		graph.setContent("template.inputTemplate.dimension", 3);
		graph.setContent("template.outputTemplate", new LabelTemplate());
		graph.getContent("template.outputTemplate.labels", List.class).add("A");
		graph.getContent("template.outputTemplate.labels", List.class).add("B");
		graph.getContent("template.outputTemplate.labels", List.class).add("C");
		
		IObject classifiers = graph.getContent("classifiers");
		Set<Class> set = classifiers.getCompatibleContentTypes("*");
		Set<Class> real = new HashSet<>();
		real.add(ClassifierImplA.class);
		assertEquals(real.size(), set.size());
		assertTrue(set.containsAll(real));
		
		graph.classifiers.add(new ClassifierImplA());
		assertEquals(graph.getContent("template"), graph.getContent("classifiers.0.template"));
		
		graph.setContent("outputClassifier", graph.getContent("classifiers.0"));
		
		IObject object = graph.getContent("outputClassifier");
		set = object.getCompatibleContentTypes("outputEncoder");
		real.clear();
		real.add(EncoderImplB.class);
		real.add(EncoderImplC.class);
		assertEquals(real.size(), set.size());
		assertTrue(set.containsAll(real));
		
		graph.setContent("outputClassifier.outputEncoder", new EncoderImplB());
		assertEquals(graph.getContent("template.outputTemplate"), graph.getContent("outputClassifier.outputEncoder.template"));
		
		set = graph.getCompatibleContentTypes("decoder");
		real.clear();
		real.add(DecoderImplB.class);
		assertEquals(real.size(), set.size());
		assertTrue(set.containsAll(real));
		graph.setContent("decoder", new DecoderImplB());
		assertEquals(graph.getContent("outputClassifier.outputEncoder"), graph.getContent("decoder.encoder"));
		
		object = graph.getContent("inputEncoders");
		set = object.getCompatibleContentTypes("*");
		real.clear();
		real.add(EncoderImplA.class);
		assertEquals(real.size(), set.size());
		assertTrue(set.containsAll(real));
		
		graph.inputEncoders.add(new EncoderImplA());
		set = object.getCompatibleContentTypes("0");
		assertEquals(real.size(), set.size());
		assertTrue(set.containsAll(real));
		
		assertEquals(graph.getContent("template.inputTemplate"), object.getContent("0.template"));
	}

}
