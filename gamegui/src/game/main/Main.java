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
package game.main;

import game.editorsystem.Editor;
import game.editorsystem.Option;
import game.plugins.editors.ConfigurableEditor;
import game.plugins.experiments.SimpleExperiment;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		Editor editor = new ConfigurableEditor();
		editor.setModel(new Option(new SimpleExperiment()));
		primaryStage.setScene(new Scene((Parent)editor.getView()));
		primaryStage.show();
	}

}
