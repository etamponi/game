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

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Modifier;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import org.apache.commons.math3.linear.RealVector;

public class Utils {

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
	
	public static double getDistance(String type, RealVector v1, RealVector v2) {
		switch(type.toLowerCase()) {
		case "l1":
			return v1.getL1Distance(v2);
		case "l2":
			return v1.getDistance(v2);
		case "linf":
			return v1.getLInfDistance(v2);
		}
		return -1;
	}
	
	public static int count(String s, char c) {
		int count = 0;
		for(char curr: s.toCharArray())
			if (curr == c)
				count++;
		return count;
	}
	
	private static Random generator = new Random();

	public static String randomString() {
		return new BigInteger(130, generator).toString(10);
	}

	public static List<Integer> range(int start, int end) {
		List<Integer> ret = new ArrayList<>();
		for (int i = start; i < end; i++)
			ret.add(i);
		return ret;
	}

	public static double log2(double x) {
		return Math.log(x) / Math.log(2);
	}

	public static int sum(int[] v) {
		int ret = 0;
		for(int i: v) ret += i;
		return ret;
	}
}
