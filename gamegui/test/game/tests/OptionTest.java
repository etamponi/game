package game.tests;

import static org.junit.Assert.assertEquals;
import game.configuration.Configurable;
import game.editorsystem.Editor;
import game.editorsystem.Option;
import game.plugins.editors.NumberEditor;
import javafx.application.Application;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import org.junit.Test;

public class OptionTest extends Application {
	
	private static class ConfigurableImplA extends Configurable {
		
		public String optionA1;
		
		public int optionA2;
		
		public double optionA3;
		
		public byte optionA4;
		
		public ConfigurableImplB optionA5;
		
		public ConfigurableImplA() {
			addOptionBinding("optionA3", "optionA5.optionB2");
		}
		
	}
	
	private static class ConfigurableImplB extends Configurable {
		
		public String optionB1;
		
		public double optionB2;
		
	}

	@Test
	public void test() throws Exception {
		launch();
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		Configurable object = new ConfigurableImplA();
		
		Option option = new Option(object, "optionA3");
		
		Editor best = option.getBestEditor();
		assertEquals(NumberEditor.class, best.getClass());
		best.setModel(option);
		
		System.exit(0);
	}

}
