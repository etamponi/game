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
import game.core.datatemplates.SequenceTemplate;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Modifier;
import java.util.Scanner;

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
	
	public static String readFile(File file) {
		String ret = null;
		try {
			Scanner scanner = new Scanner(file);
			ret = scanner.useDelimiter("\\Z").next();
			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return ret; 
	}
	
	public static String relativize(File file) {
		return relativize(file.getAbsolutePath(), System.getProperty("user.dir"));
	}
	
	public static String relativize(String path, String prefix) {
		StringBuilder relativePath = null;
		
		path = path.replaceAll("\\\\", "/"); 
		prefix = prefix.replaceAll("\\\\", "/");

		if (!path.equals(prefix)) {
			String[] pathTokens = path.split("/");
			String[] prefixTokens = prefix.split("/");
	
			//Get the shortest of the two paths
			int length = pathTokens.length < prefixTokens.length ? 
						 pathTokens.length : prefixTokens.length;
	
			//Use to determine where in the loop we exited
			int lastCommonRoot = -1;
			int index;
	
			//Find common root
			for (index = 0; index < length; index++) {
				if (pathTokens[index].equals(prefixTokens[index])) {
					lastCommonRoot = index;
				} else {
					break;
					//If we didn't find a common prefix then throw
				}
			}
			if (lastCommonRoot != -1) {
				//Build up the relative path
				relativePath = new StringBuilder();
				//Add on the ..
				for (index = lastCommonRoot + 1; index < prefixTokens.length; index++) {
					if (prefixTokens[index].length() > 0) {
						relativePath.append("../");
					}
				}
				for (index = lastCommonRoot + 1; index < pathTokens.length - 1; index++) {
					relativePath.append(pathTokens[index] + "/");
				}
				relativePath.append(pathTokens[pathTokens.length - 1]);
			}
		} 
		return relativePath == null ? null : relativePath.toString();
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

	public static boolean checkTemplateClass(DataTemplate outputTemplate, Class<? extends DataTemplate> type) {
		if (outputTemplate == null)
			return false;
		return type.isAssignableFrom(outputTemplate.getClass())
				|| (outputTemplate instanceof SequenceTemplate 
						&& type.isAssignableFrom(outputTemplate.getOption("atom").getClass()));
	}
	
}
