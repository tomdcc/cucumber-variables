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

package io.jdev.cucumber.variables.core

import spock.lang.Specification

class RegexVariableDecoderSpec extends Specification {

	RegexVariableDecoder decoder
	VariableSet vars

	void setup() {
		decoder = new RegexVariableDecoder("(?:die|der|das) (?:(\\d+)\\. )?(.*)", 2, 1);

		vars = new VariableSet()
		vars.bindVariable('hund', 'Max')
		vars.bindVariable('hund', 'Boris')
	}

	void "no index gives last variable bound"() {
		expect:
		decoder.decode(vars, 'der hund') == 'Boris'
	}

	void "specified index gives expected value"() {
		expect: '1st index gives 1st name'
		decoder.decode(vars, 'der 1. hund') == 'Max'

		and: '2nd index gives 2nd name'
		decoder.decode(vars, 'der 2. hund') == 'Boris'
	}

	void "invalid index returns null"() {
		expect:
		decoder.decode(vars, 'der 3. hund') == null
	}

	void "unbound name returns null"() {
		expect:
		decoder.decode(vars, 'die Katze') == null
	}
}
