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
package game.utils;

import java.lang.reflect.Modifier;

public class Utils {

	public static boolean isConcrete(Class type) {
		return type.isPrimitive() ||
				(!Modifier.isAbstract(type.getModifiers()) && !Modifier.isInterface(type.getModifiers()));
	}
	
	public static boolean isImplementation(Class type, Class base) {
		return isConcrete(type) && base.isAssignableFrom(type);
	}
	
	
	public static double getDistance(double[] v1, double[] v2) {
		double ret = 0;
		for (int i = 0; i < v1.length; i++)
			ret += Math.pow(v1[i]-v2[i], 2);
		return Math.sqrt(ret);
	}
	
	public static void sumTo(double[] to, double[] from) {
		for (int i = 0; i < to.length; i++)
			to[i] += from[i];
	}
	
	public static void subtractTo(double[] to, double[] from) {
		for (int i = 0; i < to.length; i++)
			to[i] -= from[i];
	}
	
	public static void scale(double[] v, double factor) {
		for (int i = 0; i < v.length; i++)
			v[i] *= factor;
	}
	
}
