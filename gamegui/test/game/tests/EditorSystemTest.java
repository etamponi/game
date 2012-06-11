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
package game.tests;

import static org.junit.Assert.assertEquals;
import game.configuration.Configurable;
import game.core.Dataset;
import game.core.Encoding;
import game.core.Graph;
import game.core.InstanceTemplate;
import game.core.blocks.Classifier;
import game.core.blocks.Encoder;
import game.editorsystem.Editor;
import game.editorsystem.EditorWindow;
import game.editorsystem.Option;
import game.main.Settings;
import game.plugins.Constraint;
import game.plugins.datatemplates.LabelTemplate;
import game.plugins.datatemplates.SequenceTemplate;
import game.plugins.datatemplates.VectorTemplate;
import game.plugins.editors.ImplementationChooserEditor;
import game.plugins.editors.ImplementationChooserEditor.Implementation;
import game.plugins.editors.NumberEditor;
import game.plugins.editors.graph.GraphEditor;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import org.junit.Test;

public class EditorSystemTest extends Application {
	
	public static class ConfigurableImplA extends Configurable {
		
		public String optionA1;
		
		public int optionA2;
		
		public double optionA3;
		
		public byte optionA4;
		
		public ConfigurableAbstract optionA5;
		
		public ConfigurableImplA() {
			addOptionBinding("optionA3", "optionA5.optionK1");
			
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
	
	public static class ClassifierA extends Classifier {

		@Override
		public boolean supportsTemplate(InstanceTemplate template) {
			return true;
		}

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
		
	}
	
	public static class ClassifierB extends Classifier {

		@Override
		public boolean supportsTemplate(InstanceTemplate template) {
			return true;
		}

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
		
	}
	
	public static class EncoderA extends Encoder<VectorTemplate> {

		@Override
		public Class getBaseTemplateClass() {
			return VectorTemplate.class;
		}

		@Override
		protected Encoding transform(Object inputData) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	
	public static class EncoderB extends Encoder<LabelTemplate> {

		@Override
		public Class getBaseTemplateClass() {
			return LabelTemplate.class;
		}

		@Override
		protected Encoding transform(Object inputData) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	
	public static class EncoderC extends Encoder<SequenceTemplate> {

		@Override
		public Class getBaseTemplateClass() {
			return SequenceTemplate.class;
		}

		@Override
		protected Encoding transform(Object inputData) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}

	@Test
	public void test() throws Exception {
		launch();
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		Settings.getInstance().getPluginManager().setOption("packages.add", "game.tests");
		
		Configurable object = new ConfigurableImplA();
		
		Option option = new Option(object, "optionA3");
		
		Editor best = option.getBestEditor();
		assertEquals(NumberEditor.class, best.getClass());
		best.setModel(option);
		
		TextField tf = (TextField)best.getView();
		tf.setText("3.14");
		assertEquals(3.14, option.getContent());
		
		option.setContent(2.71);
		assertEquals("2.71", tf.getText());
		
		tf.setText("notworking");
		assertEquals(2.71, option.getContent());
		
		object.setOption("optionA2", 0);
		option = new Option(object, "optionA5");
		best = option.getBestEditor();
		assertEquals(ImplementationChooserEditor.class, best.getClass());
		best.setModel(option);
		
		ChoiceBox<Implementation> cb = (ChoiceBox<Implementation>)((HBox)best.getView()).getChildren().get(0);
		assertEquals(null, cb.getValue());
		assertEquals(3, cb.getItems().size());
		
		object.setOption("optionA2", 1);
		assertEquals(2, cb.getItems().size());
		
		option.setContent(new ConfigurableImplB());
		assertEquals(option.getContent(), cb.getValue().getContent());
		
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
		//graph.setOption("classifiers.4.parents.add", graph.getOption("classifiers.2"));
		
		option = new Option(graph);
		Editor graphEditor = option.getBestEditor();
		assertEquals(GraphEditor.class, graphEditor.getClass());
		graphEditor.setModel(option);
		
		Button button = new Button("Click me");
		VBox parent = new VBox();
		parent.getChildren().addAll(new Label("Please put one classifier and one encoder in the graph"), button);
		button.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Option option = new Option(graph);
				Editor graphEditor = option.getBestEditor();
				graphEditor.setModel(option);
				
				new EditorWindow(graphEditor).show();
			}
		});
		
		primaryStage.setScene(new Scene(parent));
		primaryStage.show();
	}

}
