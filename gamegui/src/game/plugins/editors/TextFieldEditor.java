package game.plugins.editors;

import game.editorsystem.Editor;
import game.editorsystem.Option;
import game.utils.Utils;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

public abstract class TextFieldEditor extends TextField implements Editor {
	
	private Option model;
	
	protected abstract Object parseText();

	@Override
	public Node getNode() {
		return this;
	}

	@Override
	public Option getModel() {
		return model;
	}

	@Override
	public void setModel(Option option) {
		this.model = option;
		if (option != null)
			connect();
		else
			disconnect();
	}
	
	@Override
	public boolean canEdit(Class type) {
		return Utils.isConcreteSubtype(type, getBaseEditableClass());
	}
	
	private void connect() {
		setText(model.getContent().toString());
		setOnKeyReleased(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				model.setContent(parseText());
			}
		});
	}
	
	private void disconnect() {
		setOnKeyReleased(null);
	}

}
