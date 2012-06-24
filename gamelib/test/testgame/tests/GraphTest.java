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
import game.configuration.Configurable;
import game.core.DataTemplate;
import game.core.Dataset;
import game.core.Decoder;
import game.core.Encoding;
import game.core.Graph;
import game.core.InstanceTemplate;
import game.core.blocks.Classifier;
import game.core.blocks.Encoder;
import game.plugins.Implementation;
import game.plugins.PluginManager;
import game.plugins.datatemplates.LabelTemplate;
import game.plugins.datatemplates.VectorTemplate;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public class GraphTest {
	
	public static class EncoderImplA extends Encoder<VectorTemplate> {
		@Override
		protected Encoding transform(Object inputData) {
			return null;
		}

		@Override
		public boolean isCompatible(DataTemplate object) {
			return object instanceof VectorTemplate;
		}
	}
	
	public static class EncoderImplB extends Encoder<LabelTemplate> {
		@Override
		protected Encoding transform(Object inputData) {
			return null;
		}

		@Override
		public boolean isCompatible(DataTemplate object) {
			return object instanceof LabelTemplate;
		}
	}
	
	public static class EncoderImplC extends EncoderImplB {
		
	}
	
	public static class DecoderImplA extends Decoder<EncoderImplA> {
		@Override
		public Object decode(Encoding outputEncoded) {
			return null;
		}

		@Override
		public boolean isCompatible(Encoder object) {
			return object instanceof EncoderImplA;
		}
	}
	
	public static class DecoderImplB extends Decoder<EncoderImplB> {
		@Override
		public Object decode(Encoding outputEncoded) {
			return null;
		}

		@Override
		public boolean isCompatible(Encoder object) {
			return object instanceof EncoderImplB;
		}
	}
	
	public static class ClassifierImplA extends Classifier {
		@Override
		public boolean isTrained() {
			return false;
		}
		@Override
		protected double train(Dataset trainingSet) {
			return 0;
		}
		@Override
		protected Encoding transform(Object inputData) {
			return null;
		}
		@Override
		public boolean isCompatible(InstanceTemplate object) {
			return true;
		}
	}
	
	public static class ClassifierImplB extends Classifier {
		@Override
		public boolean isTrained() {
			return false;
		}
		@Override
		protected double train(Dataset trainingSet) {
			return 0;
		}
		@Override
		protected Encoding transform(Object inputData) {
			return null;
		}
		@Override
		public boolean isCompatible(InstanceTemplate object) {
			return false;
		}
	}

	@Test
	public void test() {
		PluginManager manager = new PluginManager();
		manager.setOption("packages.remove", "game");
		manager.setOption("packages.add", "testgame");
		
		Graph graph = new Graph();
		
		graph.setOption("template", new InstanceTemplate());
		graph.setOption("template.inputTemplate", new VectorTemplate());
		graph.setOption("template.inputTemplate.featureNumber", 3);
		graph.setOption("template.outputTemplate", new LabelTemplate());
		graph.setOption("template.outputTemplate.labels.add", "A");
		graph.setOption("template.outputTemplate.labels.add", "B");
		graph.setOption("template.outputTemplate.labels.add", "C");
		
		Configurable classifiers = graph.getOption("classifiers");
		Set<Class> set = classSet(classifiers.getCompatibleOptionImplementations("*", manager));
		Set<Class> real = new HashSet<>();
		real.add(ClassifierImplA.class);
		assertEquals(real.size(), set.size());
		assertTrue(set.containsAll(real));
		
		graph.setOption("classifiers.add", new ClassifierImplA());
		assertEquals(graph.getOption("template"), graph.getOption("classifiers.0.template"));
		
		graph.setOption("outputClassifier", graph.getOption("classifiers.0"));
		
		Configurable object = graph.getOption("outputClassifier");
		set = classSet(object.getCompatibleOptionImplementations("outputEncoder", manager));
		real.clear();
		real.add(EncoderImplB.class);
		real.add(EncoderImplC.class);
		assertEquals(real.size(), set.size());
		assertTrue(set.containsAll(real));
		
		graph.setOption("outputClassifier.outputEncoder", new EncoderImplB());
		assertEquals(graph.getOption("template.outputTemplate"), graph.getOption("outputClassifier.outputEncoder.template"));
		
		set = classSet(graph.getCompatibleOptionImplementations("decoder", manager));
		real.clear();
		real.add(DecoderImplB.class);
		assertEquals(real.size(), set.size());
		assertTrue(set.containsAll(real));
		graph.setOption("decoder", new DecoderImplB());
		assertEquals(graph.getOption("outputClassifier.outputEncoder"), graph.getOption("decoder.encoder"));
		
		object = graph.getOption("inputEncoders");
		assertEquals(graph.getOption("template.inputTemplate"), object.getOption("constraint"));
		set = classSet(object.getCompatibleOptionImplementations("*", manager));
		real.clear();
		real.add(EncoderImplA.class);
		assertEquals(real.size(), set.size());
		assertTrue(set.containsAll(real));
		
		graph.setOption("inputEncoders.add", new EncoderImplA());
		set = classSet(object.getCompatibleOptionImplementations("0", manager));
		assertEquals(real.size(), set.size());
		assertTrue(set.containsAll(real));
		
		assertEquals(graph.getOption("template.inputTemplate"), object.getOption("0.template"));
	}
	
	private <T> Set<Class> classSet(Set<Implementation<T>> set) {
		Set<Class> ret = new HashSet<>();
		for (Implementation o: set)
			ret.add(o.getContent().getClass());
		return ret;
	}

}