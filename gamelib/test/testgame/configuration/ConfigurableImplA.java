/*******************************************************************************
 * Copyright (c) 2012 Emanuele.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Emanuele - initial API and implementation
 ******************************************************************************/
package testgame.configuration;

import game.configuration.Configurable;
import game.configuration.ConfigurableList;
import game.configuration.errorchecks.LengthCheck;



public class ConfigurableImplA extends Configurable {
	
	@SuppressWarnings("unused")
	private String stateA1;
	
	@SuppressWarnings("unused")
	private String stateA2;
	
	public String optionA1;
	
	public String optionA2;
	
	public String optionA3;
	
	public ConfigurableImplB optionA4;
	
	public ConfigurableImplC optionA5;
	
	public ConfigurableList optionList = new ConfigurableList(this, ConfigurableImplB.class);
	
	public ConfigurableImplA() {
		setOptionBinding("optionA1",			"optionA4.optionB1", "optionA5.optionC1");
		setOptionBinding("optionA2",			"optionA4.optionB2");
		setOptionBinding("optionA4.optionB3",	"optionA5.optionC2");
		setOptionBinding("optionA3",			"optionList.*.optionB3");
		
		setOptionChecks("optionA3", new LengthCheck(20));
	}

}
