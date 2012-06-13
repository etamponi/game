package game.plugins.editors.blocks;

import game.editorsystem.Editor;
import game.plugins.encoders.LabelEncoder;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;

public class LabelMappingEditor extends Editor {
	
	GridPane root = new GridPane();

	@Override
	public Node getView() {
		return root;
	}

	@Override
	public void connectView() {
		if (getModel() == null || getModel().getContent() == null) {
			root.getChildren().clear();
			return;
		}
		
		int row = 0;
		final LabelEncoder encoder = (LabelEncoder)getModel().getOwner();
		for (final String label: encoder.template.labels.getList(String.class)) {
			Label l = new Label(label);
			
			if (label == null)
				continue;
			
			final TextField tf = new TextField(toText(encoder.labelMapping.get(label)));
			tf.setOnKeyReleased(new EventHandler<KeyEvent>() {
				@Override
				public void handle(KeyEvent event) {
					double[] enc = fromText(tf.getText());
					if (enc != null)
						encoder.labelMapping.put(label, enc);
				}
			});
			
			root.addRow(row++, l, tf);
		}
	}
	
	public String toText(double[] v) {
		if (v == null)
			return "";
		
		StringBuilder ret = new StringBuilder();
		
		int count = 0;
		for (double e: v)
			ret.append(String.format("%.0f", e)).append(++count < v.length ? ", " : "");
		
		return ret.toString();
	}
	
	public double[] fromText(String text) {
		String[] tokens = text.split(" *, *");
		double[] ret = new double[tokens.length];
		int i = 0;
		for (String token: tokens) {
			try {
				ret[i] = Double.parseDouble(token);
			} catch (NumberFormatException ex) {
				return null;
			}
			i++;
		}
		return ret;
	}

	@Override
	public void updateView() {
		connectView();
	}

	@Override
	public void updateModel() {
		// Done by the single textfield (that's bad)
	}

	@Override
	public Class getBaseEditableClass() {
		return getClass();
	}

}
