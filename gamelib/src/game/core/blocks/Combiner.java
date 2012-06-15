package game.core.blocks;

import game.configuration.ErrorCheck;

import java.util.List;



public abstract class Combiner extends Classifier {
	
	public static class ClassifiersOnlyListCheck implements ErrorCheck<List> {

		@Override
		public String getError(List list) {
			for (Object element: list) {
				if (!(element instanceof Classifier))
					return "this list can only contain Classifiers";
			}
			
			return null;
		}
		
	}
	
	public Combiner() {
		addOptionBinding("outputEncoder", "parents.*.outputEncoder");
		
		addOptionChecks("parents", new ClassifiersOnlyListCheck());
	}

}
