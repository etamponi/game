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
package testgame.tests;

import static org.junit.Assert.assertEquals;
import game.core.DataTemplate;
import game.core.DataTemplate.Data;
import game.core.Encoding;
import game.core.InstanceTemplate;
import game.core.blocks.Encoder;
import game.core.blocks.PredictionGraph;
import game.core.blocks.Transducer;
import game.editorsystem.PropertyEditor;
import game.plugins.datatemplates.LabelTemplate;
import game.plugins.datatemplates.VectorTemplate;
import game.plugins.editors.ImplementationChooserEditor;
import game.plugins.editors.ImplementationChooserEditor.Implementation;
import game.plugins.editors.NumberEditor;
import game.plugins.editors.graph.OuterGraphEditor;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import org.junit.Test;

import com.ios.Constraint;
import com.ios.IList;
import com.ios.IObject;
import com.ios.PluginManager;
import com.ios.Property;
import com.ios.PluginManager.PluginConfiguration;
import com.ios.triggers.MasterSlaveTrigger;

public class EditorSystemTest extends Application {
	
	public static class ConfigurableImplA extends IObject {
		
		public String optionA1;
		
		public int optionA2;
		
		public double optionA3;
		
		public byte optionA4;
		
		public ConfigurableAbstract optionA5;
		
		public IObject graph;
		
		public ConfigurableImplA() {
			addTrigger(new MasterSlaveTrigger(this, "optionA3", "optionA5.optionK1"));
			
			addConstraint("optionA5", new Constraint<ConfigurableAbstract>() {
				@Override
				public boolean isValid(ConfigurableAbstract o) {
					if (optionA2 == 1)
						return o.getClass().getSimpleName().endsWith("B");
					else
						return true;
				}
			});
		}
		
	}
	
	public static abstract class ConfigurableAbstract extends IObject {
		
		public double optionK1; 
		
	}
	
	public static class ConfigurableImplB extends ConfigurableAbstract {
		
		public String optionB1;
		
		public double optionB2;
		
	}
	
	public static class ConfigurableImplC extends ConfigurableAbstract {
		
		public String optionC1;
		
		public double optionC2;
		
	}
	
	public static class ClassifierA extends Transducer {

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
	
	public static class ClassifierB extends Transducer {

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
	
	public static class EncoderA extends Encoder<VectorTemplate> {

		@Override
		public Encoding baseEncode(Data inputData) {
			return null;
		}

		@Override
		public boolean isCompatible(DataTemplate object) {
			return object instanceof VectorTemplate && object.sequence == false;
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
	
	public static class EncoderB extends Encoder<LabelTemplate> {

		@Override
		public Encoding baseEncode(Data inputData) {
			return null;
		}

		@Override
		public boolean isCompatible(DataTemplate object) {
			return object instanceof LabelTemplate && object.sequence == false;
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
	
	public static class EncoderC extends Encoder<DataTemplate> {

		@Override
		public Encoding baseEncode(Data inputData) {
			return null;
		}

		@Override
		public boolean isCompatible(DataTemplate object) {
			return object.sequence == true;
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

	@Test
	public void test() throws Exception {
		PluginConfiguration conf = new PluginConfiguration();
		conf.packages.add("testgame");
		PluginManager.initialize(conf);
		launch();
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		IObject object = new ConfigurableImplA();
		
		Property option = new Property(object, "optionA3");
		
		PropertyEditor best = PropertyEditor.getBestEditor(option.getContentType(false));
		assertEquals(NumberEditor.class, best.getClass());
		best.connect(option);
		
		TextField tf = (TextField)best.getView();
		tf.setText("3.14");
		assertEquals(3.14, option.getContent());
		
		option.setContent(2.71);
		assertEquals("2.71", tf.getText());
		
		tf.setText("notworking");
		assertEquals(2.71, option.getContent());
		best.detach();
		
		object.setContent("optionA2", 0);
		option = new Property(object, "optionA5");
		best = PropertyEditor.getBestEditor(option.getContentType(false));
		assertEquals(ImplementationChooserEditor.class, best.getClass());
		best.connect(option);
		
		ComboBox<Implementation> cb = (ComboBox<Implementation>)((HBox)best.getView()).getChildren().get(0);
		assertEquals("<null>", cb.getValue().toString());
		assertEquals(3, cb.getItems().size());
		
		object.setContent("optionA2", 1);
		assertEquals(2, cb.getItems().size());
		
		option.setContent(new ConfigurableImplB());
		assertEquals(option.getContent(), cb.getValue().getInstance());
		best.detach();
		
		final PredictionGraph graph = new PredictionGraph();
		graph.classifiers.add(new ClassifierA());
		graph.classifiers.add(new ClassifierB());
		graph.classifiers.add(new ClassifierB());
		graph.classifiers.add(new ClassifierA());
		graph.classifiers.add(new ClassifierA());
		graph.classifiers.add(new ClassifierB());
		graph.classifiers.add(new ClassifierA());
		graph.classifiers.add(new ClassifierB());
		graph.inputEncoders.add(new EncoderA());
		graph.inputEncoders.add(new EncoderA());
		
		graph.setContent("outputClassifier", graph.getContent("classifiers.0"));
		IList parents = graph.getContent("classifiers.0.parents");
		parents.add(graph.getContent("classifiers.1"));
		parents.add(graph.getContent("classifiers.2"));
		parents = graph.getContent("classifiers.1.parents");
		parents.add(graph.getContent("classifiers.3"));
		parents = graph.getContent("classifiers.2.parents");
		parents.add(graph.getContent("classifiers.3"));
		parents = graph.getContent("classifiers.3.parents");
		parents.add(graph.getContent("classifiers.3"));
		parents.add(graph.getContent("classifiers.4"));
		parents = graph.getContent("classifiers.4.parents");
		parents.add(graph.getContent("inputEncoders.0"));
		parents.add(graph.getContent("classifiers.2"));
		parents = graph.getContent("classifiers.5.parents");
		parents.add(graph.getContent("inputEncoders.0"));
		
		object = new ConfigurableImplA();
		object.setContent("graph", graph);
		option = new Property(object, "graph");
		PropertyEditor graphEditor = PropertyEditor.getBestEditor(option.getContentType(true));
		assertEquals(OuterGraphEditor.class, graphEditor.getClass());
		
		Platform.exit();
	}

}
