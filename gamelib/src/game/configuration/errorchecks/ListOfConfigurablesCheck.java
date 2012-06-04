package game.configuration.errorchecks;

import game.configuration.Configurable;
import game.configuration.ErrorCheck;

import java.util.List;

public class ListOfConfigurablesCheck implements ErrorCheck<List<? extends Configurable>> {

	@Override
	public String getError(List<? extends Configurable> list) {
		StringBuilder ret = new StringBuilder();
		for (int i = 0; i < list.size(); i++) {
			Configurable element = list.get(i);
			for (String error: element.getConfigurationErrors())
				ret.append("\n\t").append("[").append(i).append("]: ").append(error);
		}
		return ret.toString();
	}

}
