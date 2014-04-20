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

import java.util.List;
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

		List<Object> vals = vars.getVariable(matcher.group(variableNameRegexIndex));
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

}
