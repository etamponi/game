package game.plugins.editors;

import game.editorsystem.Editor;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.TextField;

public abstract class TextFieldEditor extends Editor {
	
	protected TextField textField = new TextField();
	
	public TextFieldEditor() {
		textField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(
					ObservableValue<? extends String> observable,
					String oldValue, String newValue) {
				updateModel();
			}
		});
	}
	
	protected abstract Object parseText();

	@Override
	public Node getView() {
		return textField;
	}

	@Override
	public void updateModel() {
		if (getModel() != null) {
			Object content = parseText();
			if (content != null)
				getModel().setContent(content);
		}
	}

	@Override
	public void updateView() {
		if (getModel() != null && getModel().getContent() != null)
			textField.setText(getModel().getContent().toString());
		else
			textField.setText("");
	}

	@Override
	public void connectView() {
		updateView();
	}

}
