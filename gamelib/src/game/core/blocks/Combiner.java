package game.core.blocks;

import game.configuration.ErrorCheck;
import game.configuration.errorchecks.SizeCheck;

import java.util.List;



public abstract class Combiner extends Transducer {
	
	public static class ClassifiersOnlyListCheck implements ErrorCheck<List> {

		@Override
		public String getError(List list) {
			for (Object element: list) {
				if (!(element instanceof Transducer))
					return "this list can only contain Classifiers";
			}
			
			return null;
		}
		
	}
	
	public Combiner() {
		setOptionBinding("outputEncoder", "parents.*.outputEncoder");
		
		setOptionChecks("parents", new ClassifiersOnlyListCheck(), new SizeCheck(1));
	}

}
