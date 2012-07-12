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
import game.configuration.Configurable;
import game.core.Dataset;
import game.core.DataTemplate;
import game.core.Encoding;
import game.core.Graph;
import game.core.InstanceTemplate;
import game.core.blocks.Encoder;
import game.core.blocks.Transducer;
import game.core.datatemplates.SequenceTemplate;
import game.editorsystem.Option;
import game.editorsystem.OptionEditor;
import game.editorsystem.Settings;
import game.plugins.Constraint;
import game.plugins.Implementation;
import game.plugins.datatemplates.LabelTemplate;
import game.plugins.datatemplates.VectorTemplate;
import game.plugins.editors.ImplementationChooserEditor;
import game.plugins.editors.NumberEditor;
import game.plugins.editors.graph.OuterGraphEditor;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import org.junit.Test;

public class EditorSystemTest extends Application {
	
	public static class ConfigurableImplA extends Configurable {
		
		public String optionA1;
		
		public int optionA2;
		
		public double optionA3;
		
		public byte optionA4;
		
		public ConfigurableAbstract optionA5;
		
		public Configurable graph;
		
		public ConfigurableImplA() {
			setOptionBinding("optionA3", "optionA5.optionK1");
			
			setOptionConstraint("optionA5", new Constraint<ConfigurableAbstract>() {
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
	
	public static abstract class ConfigurableAbstract extends Configurable {
		
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
		public boolean isTrained() {
			return false;
		}

		@Override
		protected void train(Dataset trainingSet) {
			
		}

		@Override
		public Encoding transform(Object inputData) {
			return null;
		}

		@Override
		public boolean isCompatible(InstanceTemplate object) {
			return true;
		}
		
	}
	
	public static class ClassifierB extends Transducer {

		@Override
		public boolean isTrained() {
			return false;
		}

		@Override
		protected void train(Dataset trainingSet) {
			
		}

		@Override
		public Encoding transform(Object inputData) {
			return null;
		}

		@Override
		public boolean isCompatible(InstanceTemplate object) {
			return true;
		}
		
	}
	
	public static class EncoderA extends Encoder<VectorTemplate> {

		@Override
		public Encoding transform(Object inputData) {
			return null;
		}

		@Override
		public boolean isCompatible(DataTemplate object) {
			return object instanceof VectorTemplate;
		}
		
	}
	
	public static class EncoderB extends Encoder<LabelTemplate> {

		@Override
		public Encoding transform(Object inputData) {
			return null;
		}

		@Override
		public boolean isCompatible(DataTemplate object) {
			return object instanceof LabelTemplate;
		}
		
	}
	
	public static class EncoderC extends Encoder<SequenceTemplate> {

		@Override
		public Encoding transform(Object inputData) {
			return null;
		}

		@Override
		public boolean isCompatible(DataTemplate object) {
			return object instanceof SequenceTemplate;
		}
		
	}

	@Test
	public void test() throws Exception {
		Settings.getInstance().getPluginManager().setOption("packages.add", "testgame");
		launch();
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		Configurable object = new ConfigurableImplA();
		
		Option option = new Option(object, "optionA3");
		
		OptionEditor best = option.getBestEditor(false);
		assertEquals(NumberEditor.class, best.getClass());
		best.connect(option);
		
		TextField tf = (TextField)best.getView();
		tf.setText("3.14");
		assertEquals(3.14, option.getContent());
		
		option.setContent(2.71);
		assertEquals("2.71", tf.getText());
		
		tf.setText("notworking");
		assertEquals(2.71, option.getContent());
		best.disconnect();
		
		object.setOption("optionA2", 0);
		option = new Option(object, "optionA5");
		best = option.getBestEditor(false);
		assertEquals(ImplementationChooserEditor.class, best.getClass());
		best.connect(option);
		
		ComboBox<Implementation> cb = (ComboBox<Implementation>)((HBox)best.getView()).getChildren().get(0);
		assertEquals("<null>", cb.getValue().toString());
		assertEquals(3, cb.getItems().size());
		
		object.setOption("optionA2", 1);
		assertEquals(2, cb.getItems().size());
		
		option.setContent(new ConfigurableImplB());
		assertEquals(option.getContent(), cb.getValue().getContent());
		best.disconnect();
		
		final Graph graph = new Graph();
		graph.setOption("classifiers.add", new ClassifierA());
		graph.setOption("classifiers.add", new ClassifierB());
		graph.setOption("classifiers.add", new ClassifierB());
		graph.setOption("classifiers.add", new ClassifierA());
		graph.setOption("classifiers.add", new ClassifierA());
		graph.setOption("classifiers.add", new ClassifierB());
		graph.setOption("classifiers.add", new ClassifierA());
		graph.setOption("classifiers.add", new ClassifierB());
		graph.setOption("inputEncoders.add", new EncoderA());
		graph.setOption("inputEncoders.add", new EncoderA());
		
		graph.setOption("outputClassifier", graph.getOption("classifiers.0"));
		graph.setOption("classifiers.0.parents.add", graph.getOption("classifiers.1"));
		graph.setOption("classifiers.0.parents.add", graph.getOption("classifiers.2"));
		graph.setOption("classifiers.1.parents.add", graph.getOption("classifiers.3"));
		graph.setOption("classifiers.2.parents.add", graph.getOption("classifiers.3"));
		graph.setOption("classifiers.3.parents.add", graph.getOption("classifiers.4"));
		graph.setOption("classifiers.3.parents.add", graph.getOption("classifiers.5"));
		graph.setOption("classifiers.4.parents.add", graph.getOption("inputEncoders.0"));
		graph.setOption("classifiers.5.parents.add", graph.getOption("inputEncoders.0"));
		graph.setOption("classifiers.4.parents.add", graph.getOption("classifiers.2"));
		
		object = new ConfigurableImplA();
		object.setOption("graph", graph);
		option = new Option(object, "graph");
		OptionEditor graphEditor = option.getBestEditor(true);
		assertEquals(OuterGraphEditor.class, graphEditor.getClass());
		
		Platform.exit();
	}

}
