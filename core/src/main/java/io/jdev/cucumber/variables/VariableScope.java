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

package io.jdev.cucumber.variables;

import cucumber.api.Scenario;
import io.jdev.cucumber.variables.core.Decoder;
import io.jdev.cucumber.variables.core.VariableSet;

public class VariableScope {

	private static ThreadLocal<VariableScope> currentVariableScope = new ThreadLocal<VariableScope>();

	private final VariableSet vars;
	private final Scenario scenario;
	private final Decoder decoder;

	public VariableScope(Decoder decoder, Scenario scenario) {
		this.decoder = decoder;
		this.scenario = scenario;
		vars = new VariableSet();
	}

	public Object decodeVariable(String name) {
		return decoder.decode(vars, name);
	}

	public void storeVariable(String name, Object value) {
		vars.bindVariable(name, value);
	}

	public static VariableScope getCurrentScope(Scenario scenario, Decoder newDecoder) {
		VariableScope scope = currentVariableScope.get();
		if(scope == null || scope.scenario != scenario) {
			// no current scope, or must be left over from a previous scenario
			scope = new VariableScope(newDecoder, scenario);
			currentVariableScope.set(scope);
		}
		return scope;
	}

	public static VariableScope getCurrentScope() {
		return currentVariableScope.get();
	}

	public static void removeCurrentScope() {
		currentVariableScope.remove();
	}

	public String toString() {
		return super.toString() + vars.dumpVars();
	}
}
