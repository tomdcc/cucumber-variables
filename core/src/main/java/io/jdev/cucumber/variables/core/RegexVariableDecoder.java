/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 the original author or authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package io.jdev.cucumber.variables.core;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexVariableDecoder implements Decoder {

	private final Pattern regex;
	private final int variableNameRegexIndex;
	private final int indexRegexIndex;
	public RegexVariableDecoder(String regex, int variableNameRegexIndex, int indexRegexIndex) {
		this.regex = Pattern.compile(regex);
		this.variableNameRegexIndex = variableNameRegexIndex;
		this.indexRegexIndex = indexRegexIndex;
	}

	@Override
	public Object decode(VariableSet vars, String name) {
		Matcher matcher = regex.matcher(name);
		if(!matcher.matches()) {
			return null;
		}

		List<Object> vals = findVariable(vars, matcher.group(variableNameRegexIndex));
		if(vals == null || vals.isEmpty()) {
			// nothing there
			return null;
		}

		int index;
		String indexMatch = matcher.group(indexRegexIndex);
		if(indexMatch != null && !indexMatch.isEmpty()) {
			// matches result with specified index
			index = Integer.parseInt(indexMatch) - 1;
		} else {
				// none specified, just use the last one bound
			index = vals.size() - 1;
		}
		if((vals.size() - 1) < index) {
			return null;
		} else {
			return vals.get(index);
		}
	}

    // some of this may have to be moved out of here - it's not clear to me as an english-only
    // speaker how much of the "foo bar baz" -> foo.bar.baz or fooBar.baz logic works across
    // languages

    protected List<Object> findVariable(VariableSet vars, String varName) {
        varName = varName.trim();

        // just try straight up name
        List<Object> vals = vars.getVariable(varName);
        if(vals != null && !vals.isEmpty()) {
            return vals;
        }

        if(!varName.contains(" ")) {
            // no nesting possible, not there
            return null;
        }

        // ok, need to do a bit more searching
        // just split the var name up into words and try various attempts
        // at joining the bits together - map indexing and property fetching
        List<String> bits = Arrays.asList(varName.trim().split(" +"));
        List<String> remainingBits = new ArrayList<String>(bits);
        StringBuilder sb = new StringBuilder();
        remainingBits = new ArrayList<String>(remainingBits);
        ListIterator<String> bitIter = remainingBits.listIterator();
        while(bitIter.hasNext()) {
            String bit = bitIter.next();
            bitIter.remove();
            if(sb.length() > 0) {
                sb.append(" ");
            }
            sb.append(bit);
            String base = sb.toString();
            Object value = findVariable(vars, base, remainingBits);
            if(value != null) {
                return Collections.singletonList(value);
            }
        }
        return null;
    }

    protected Object findVariable(VariableSet vars, String base, List<String> remainingBits) {
        // first, does this base exist?
        List<Object> vals = vars.getVariable(base);
        if(vals != null && !vals.isEmpty()) {
            // don't support getting nth child thing
            Object value = findVariable(vals.get(0), remainingBits);
            if(value != null) {
                return value;
            }
        }
        return null;
    }

    protected Object findVariable(Object baseObj, List<String> remainingBits) {
        if(baseObj instanceof Map) {
            return findVariableInMap((Map) baseObj, remainingBits);
        } else {
            return findPropertyOnObject(baseObj, remainingBits);
        }
    }

    protected <T> Object findNestedValue(T target, List<String> remainingBits, PropertyGetter<T> getter) {
        // search for matching keys, camel case style
        StringBuilder sb = new StringBuilder();
        remainingBits = new ArrayList<String>(remainingBits);
        ListIterator<String> bitIter = remainingBits.listIterator();
        while(bitIter.hasNext()) {
            String bit = bitIter.next();
            bitIter.remove();
            if(sb.length() == 0) {
                sb.append(bit.toLowerCase());
            } else {
                sb.append(properCase(bit));
            }
            String key = sb.toString();
            Object value = getter.getProperty(target, key);
            if(value != null) {
                if(remainingBits.isEmpty()) {
                    // looks like we've got a match
                    return value;
                } else {
                    // look into it
                    value = findVariable(value, remainingBits);
                    if (value != null) {
                        return value;
                    }
                }
            }
        }

        return null;
    }

    protected Object findPropertyOnObject(Object target, List<String> remainingBits) {
        return findNestedValue(target, remainingBits, reflectionPropertyGetterInstance);
    }
    protected Object findVariableInMap(Map<?,?> baseMap, List<String> remainingBits) {
        return findNestedValue(baseMap, remainingBits, mapPropertyGetterInstance);
    }

    protected static String properCase(String src) {
        src = src.toLowerCase();
        return src.substring(0, 1).toUpperCase() + src.substring(1);
    }

    private static interface PropertyGetter<T> {
        public Object getProperty(T target, String name);
    }

    private static final MapPropertyGetter mapPropertyGetterInstance = new MapPropertyGetter();
    private static class MapPropertyGetter implements PropertyGetter<Map<?,?>> {
        public Object getProperty(Map<?,?> target, String name) {
            return target.get(name);
        }
    }

    private static final ReflectionPropertyGetter reflectionPropertyGetterInstance = new ReflectionPropertyGetter();
    private static class ReflectionPropertyGetter implements PropertyGetter<Object> {
        public Object getProperty(Object target, String name) {
            try {
                String methodName = "get" + name.substring(0, 1).toUpperCase() + name.substring(1);
                Method getterMethod = target.getClass().getMethod(methodName, new Class[0]);
                if(getterMethod == null) {
                    return null;
                }
                return getterMethod.invoke(target);
            } catch(Exception e) {
                return null;
            }
        }
    }
}
