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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChainedDecoder implements Decoder {

	private List<Decoder> decoders;

	public ChainedDecoder(Decoder... decoders) {
		this.decoders = new ArrayList<Decoder>(Arrays.asList(decoders));
	}

	public void addDecoder(Decoder decoder) {
		decoders.add(decoder);
	}

	public List<Decoder> getDecoders() {
		return decoders;
	}

	@Override
	public Object decode(VariableSet vars, String name) {
		for(Decoder decoder : decoders) {
			Object value = decoder.decode(vars, name);
			if(value != null) {
				return value;
			}
		}
		return null;
	}

}
