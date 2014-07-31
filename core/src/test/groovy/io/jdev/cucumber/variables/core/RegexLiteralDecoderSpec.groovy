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

import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException

class RegexLiteralDecoderSpec extends Specification {

	RegexLiteralDecoder decoder
	VariableSet vars

	void setup() {
		decoder = new RegexLiteralDecoder()
		vars = Mock(VariableSet)
	}

	void "regex literal is decoded correctly"() {
		when:
		def result = decoder.decode(vars, "/foo/")

        then:
        result instanceof Pattern
        result.pattern() == 'foo'
	}

	void "invalid regex literal throws exception"() {
		when:
		decoder.decode(vars, "/\\/")

        then:
        thrown(PatternSyntaxException)
	}

	void "non-regex-literal returns null"() {
		expect:
		decoder.decode(vars, "foo/") == null

		and:
		decoder.decode(vars, "/foo") == null

		and:
		decoder.decode(vars, "foo") == null

        and:
        decoder.decode(vars, "m/foo/") == null

        and:
        decoder.decode(vars, "/foo/m") == null
	}
}
