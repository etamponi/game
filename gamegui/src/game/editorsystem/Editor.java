package game.editorsystem;

import javafx.scene.Node;


public interface Editor {
	
	Node getNode();
	
	Option getModel();

	void setModel(Option option);
	
	boolean canEdit(Class type);
	
	Class getBaseEditableClass();
	
}
