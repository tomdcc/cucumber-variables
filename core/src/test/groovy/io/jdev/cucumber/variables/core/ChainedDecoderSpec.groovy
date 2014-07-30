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

class ChainedDecoderSpec extends Specification {

	ChainedDecoder decoder
	Decoder decoder1
	Decoder decoder2
	VariableSet vars

	void setup() {
		decoder1 = Mock(Decoder)
		decoder2 = Mock(Decoder)
		decoder = new ChainedDecoder(decoder1, decoder2)
		vars = new VariableSet()
	}

	void "first decoder to return non-null value is used"() {
		when:
			def result = decoder.decode(vars, 'foo')

		then:
			decoder1.decode(vars, 'foo') >> 'bar'

		and:
			0 * decoder2._

		and:
			result == 'bar'
	}

	void "second decoder called when first returns null"() {
		when:
		def result = decoder.decode(vars, 'foo')

		then:
		decoder1.decode(vars, 'foo') >> null

		and:
		decoder2.decode(vars, 'foo') >> 'bar'

		and:
		result == 'bar'
	}

	void "null returned when all decoders return null"() {
		when:
		def result = decoder.decode(vars, 'foo')

		then:
		decoder1.decode(vars, 'foo') >> null

		and:
		decoder2.decode(vars, 'foo') >> null

		and:
		result == null
	}
}
