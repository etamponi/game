package game.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import game.configuration.Configurable;
import game.core.Dataset;
import game.core.Decoder;
import game.core.Encoding;
import game.core.Graph;
import game.core.InstanceTemplate;
import game.core.blocks.Classifier;
import game.core.blocks.Encoder;
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
			// TODO Auto-generated method stub
			return null;
		}
		@Override
		public Class getBaseTemplateClass() {
			return VectorTemplate.class;
		}
	}
	
	public static class EncoderImplB extends Encoder<LabelTemplate> {
		@Override
		protected Encoding transform(Object inputData) {
			// TODO Auto-generated method stub
			return null;
		}
		@Override
		public Class getBaseTemplateClass() {
			return LabelTemplate.class;
		}
	}
	
	public static class EncoderImplC extends EncoderImplB {
		
	}
	
	public static class DecoderImplA extends Decoder<EncoderImplA> {
		@Override
		public Object decode(Encoding outputEncoded) {
			// TODO Auto-generated method stub
			return null;
		}
		@Override
		public Class getBaseEncoderClass() {
			return EncoderImplA.class;
		}
	}
	
	public static class DecoderImplB extends Decoder<EncoderImplB> {
		@Override
		public Object decode(Encoding outputEncoded) {
			// TODO Auto-generated method stub
			return null;
		}
		@Override
		public Class getBaseEncoderClass() {
			return EncoderImplB.class;
		}
	}
	
	public static class ClassifierImplA extends Classifier {
		@Override
		public boolean isTrained() {
			// TODO Auto-generated method stub
			return false;
		}
		@Override
		protected double train(Dataset trainingSet) {
			// TODO Auto-generated method stub
			return 0;
		}
		@Override
		protected Encoding transform(Object inputData) {
			// TODO Auto-generated method stub
			return null;
		}
		@Override
		public boolean supportsTemplate(InstanceTemplate template) {
			return true;
		}
	}
	
	public static class ClassifierImplB extends Classifier {
		@Override
		public boolean isTrained() {
			// TODO Auto-generated method stub
			return false;
		}
		@Override
		protected double train(Dataset trainingSet) {
			// TODO Auto-generated method stub
			return 0;
		}
		@Override
		protected Encoding transform(Object inputData) {
			// TODO Auto-generated method stub
			return null;
		}
		@Override
		public boolean supportsTemplate(InstanceTemplate template) {
			return false;
		}
	}

	@Test
	public void test() {
		PluginManager manager = new PluginManager();
		
		Graph graph = new Graph();
		
		graph.setOption("template", new InstanceTemplate());
		graph.setOption("template.inputTemplate", new VectorTemplate());
		graph.setOption("template.inputTemplate.featureNumber", 3);
		graph.setOption("template.outputTemplate", new LabelTemplate());
		graph.setOption("template.outputTemplate.labels.add", "A");
		graph.setOption("template.outputTemplate.labels.add", "B");
		graph.setOption("template.outputTemplate.labels.add", "C");
		
		Configurable classifiers = graph.getOption("classifiers");
		Set<Class> set = classSet(classifiers.getCompatibleOptionInstances("*", manager));
		Set<Class> real = new HashSet<>();
		real.add(ClassifierImplA.class);
		assertEquals(real.size(), set.size());
		assertTrue(set.containsAll(real));
		
		graph.setOption("classifiers.add", new ClassifierImplA());
		assertEquals(graph.getOption("template"), graph.getOption("classifiers.0.template"));
		
		graph.setOption("outputClassifier", graph.getOption("classifiers.0"));
		
		Configurable object = graph.getOption("outputClassifier");
		set = classSet(object.getCompatibleOptionInstances("outputEncoder", manager));
		real.clear();
		real.add(EncoderImplB.class);
		real.add(EncoderImplC.class);
		assertEquals(real.size(), set.size());
		assertTrue(set.containsAll(real));
		
		graph.setOption("outputClassifier.outputEncoder", new EncoderImplB());
		assertEquals(graph.getOption("template.outputTemplate"), graph.getOption("outputClassifier.outputEncoder.template"));
		
		set = classSet(graph.getCompatibleOptionInstances("decoder", manager));
		real.clear();
		real.add(DecoderImplB.class);
		assertEquals(real.size(), set.size());
		assertTrue(set.containsAll(real));
		graph.setOption("decoder", new DecoderImplB());
		assertEquals(graph.getOption("outputClassifier.outputEncoder"), graph.getOption("decoder.encoder"));
		
		object = graph.getOption("inputEncoders");
		assertEquals(graph.getOption("template.inputTemplate"), object.getOption("template"));
		set = classSet(object.getCompatibleOptionInstances("*", manager));
		real.clear();
		real.add(EncoderImplA.class);
		assertEquals(real.size(), set.size());
		assertTrue(set.containsAll(real));
		
		graph.setOption("inputEncoders.add", new EncoderImplA());
		set = classSet(object.getCompatibleOptionInstances("0", manager));
		assertEquals(real.size(), set.size());
		assertTrue(set.containsAll(real));
		
		assertEquals(graph.getOption("template.inputTemplate"), object.getOption("0.template"));
	}
	
	private Set<Class> classSet(Set set) {
		Set<Class> ret = new HashSet<>();
		for (Object o: set)
			ret.add(o.getClass());
		return ret;
	}

}
