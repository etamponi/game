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

import game.core.DataTemplate;
import game.plugins.datatemplates.SequenceTemplate;

import java.lang.reflect.Modifier;
import java.util.List;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class Utils {
	
	private static final XStream stream = new XStream(new DomDriver());

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
			ret += (v1[i]-v2[i])*(v1[i]-v2[i]);
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
	
	public static double[] normalize(double[] in) {
		double[] ret = new double[in.length];
		
		double sum = sum(in);
		for(int i = 0; i < ret.length; i++)
			ret[i] = in[i]/sum;
		
		return ret;
	}
	
	public static double sum(double[] v) {
		double sum = 0;
		
		for(double e: v)
			sum += e;
		
		return sum;
	}
	
	public static <T> T deepClone(T object) {
		return (T)stream.fromXML(stream.toXML(object));
	}
	
	public static List<String> getLabels(DataTemplate template) {
		if (template instanceof SequenceTemplate)
			return template.getOption("atom.labels");
		else
			return template.getOption("labels");
	}
	
}
