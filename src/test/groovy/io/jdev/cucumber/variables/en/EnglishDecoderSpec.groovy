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

package io.jdev.cucumber.variables.en

import io.jdev.cucumber.variables.core.VariableSet;
import spock.lang.Specification
import spock.lang.Unroll

/**
 * This is basically an integration test for EnglishDecoder and RegexVariableDecoder
 */
public class EnglishDecoderSpec extends Specification {

	EnglishDecoder decoder
	VariableSet vars

	void setup() {
		decoder = new EnglishDecoder();
		vars = Mock(VariableSet)
	}

	void "decodes single present variable correctly"() {
		given: 'val'
		def val = 1234L

		when:
		def result = decoder.decode(vars, "the very long variable name")

		then:
		vars.getVariable('very long variable name') >> [val]

		and:
		result == val
	}

	@Unroll
	void "missing variable returns null"() {
		when:
		def result = decoder.decode(vars, "the very long variable name")

		then:
		vars.getVariable('very long variable name') >> varVal

		and:
		result == null

		where:
		varVal << [null, []]
	}

	void "decodes last present variable when no index given"() {
		given: 'val'
		def vals = [1234L, 'asdf', 'qwerty']

		when:
		def result = decoder.decode(vars, "the very long variable name")

		then:
		vars.getVariable('very long variable name') >> vals

		and:
		result == vals.last()
	}

	void "decodes first present variable correctly"() {
		given: 'val'
		def vals = [1234L, 'asdf']

		when:
		def result = decoder.decode(vars, "the 1st very long variable name")

		then:
		vars.getVariable('very long variable name') >> vals

		and:
		result == vals[0]
	}

	@Unroll
	void "decodes #time present variable correctly"() {
		given: 'val'
		def vals = [1234L, 'asdf']

		when:
		def result = decoder.decode(vars, "the $time very long variable name")

		then:
		vars.getVariable('very long variable name') >> vals

		and:
		result == vals[expectedIndex]

		where:
		time  | expectedIndex
		'1st' | 0
		'2nd' | 1
	}

}