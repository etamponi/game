package game.tests;

import javafx.application.Application;
import javafx.stage.Stage;
import game.configuration.Configurable;
import game.editorsystem.Editor;
import game.editorsystem.Option;

import org.junit.Test;
import static org.junit.Assert.*;

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
	public void test() {
		launch();
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		Configurable object = new ConfigurableImplA();
		
		Option option = new Option(object, "optionA3");
		
		System.out.println(Number.class.isAssignableFrom(double.class));
		
		Editor best = option.getBestEditor();
		
		System.exit(0);
	}

}
