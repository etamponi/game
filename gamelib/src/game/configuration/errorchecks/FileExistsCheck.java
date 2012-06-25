package game.configuration.errorchecks;

import game.configuration.ErrorCheck;

import java.io.File;

public class FileExistsCheck implements ErrorCheck<File> {

	@Override
	public String getError(File value) {
		if (!value.exists())
			return "file specified does not exist";
		else
			return null;
	}

}
