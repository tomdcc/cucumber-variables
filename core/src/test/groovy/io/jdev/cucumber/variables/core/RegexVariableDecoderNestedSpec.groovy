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

class RegexVariableDecoderNestedSpec extends Specification {

	RegexVariableDecoder decoder
	VariableSet vars

	void setup() {
        decoder = new RegexVariableDecoder("(?:die|der|das) (?:(\\d+)\\. )?(.*)", 2, 1);

		vars = new VariableSet()
	}

    void "find map value"() {
        given:
        def map = [foo: 'bar', fooBar: 'baz1', fooBarBaz:[fooBar:'baz2']]

        expect:
        decoder.findVariableInMap(map, ['foo']) == 'bar'
        decoder.findVariableInMap(map, ['foo', 'bar']) == 'baz1'
        decoder.findVariableInMap(map, ['foo', 'bar', 'baz']) == [fooBar:'baz2']
        decoder.findVariableInMap(map, ['foo', 'bar', 'baz', 'foo']) == null
        decoder.findVariableInMap(map, ['foo', 'bar', 'baz', 'foo', 'bar']) == 'baz2'
    }

    void "find bound map values"() {
        given:
        def nestedMap = [fooBar:'baz2']
        def map = [foo: 'bar', fooBar: 'baz1', fooBarBaz: nestedMap]
        vars.bindVariable('map', map)

        expect:
        decoder.decode(vars, 'das map') == map
        decoder.decode(vars, 'das map foo') == 'bar'
        decoder.decode(vars, 'das map foo bar') == 'baz1'
        decoder.decode(vars, 'das map foo bar baz') == nestedMap
        decoder.decode(vars, 'das map foo bar baz foo') == null
        decoder.decode(vars, 'das map foo bar baz foo bar') == 'baz2'
    }

    void "find property value"() {
        given:
        def nestedCar = new Car(name: 'Cruze')
        def man = new Manufacturer(tradingName: 'Holden', carReference: nestedCar)
        def car = new Car(name: 'Commodore', manufacturer: man)

        expect:
        decoder.findPropertyOnObject(car, ['notthere']) == null
        decoder.findPropertyOnObject(car, ['name']) == 'Commodore'
        decoder.findPropertyOnObject(car, ['manufacturer']) == man
        decoder.findPropertyOnObject(car, ['manufacturer', 'trading']) == null
        decoder.findPropertyOnObject(car, ['manufacturer', 'trading', 'name']) == 'Holden'
        decoder.findPropertyOnObject(car, ['manufacturer', 'car']) == null
        decoder.findPropertyOnObject(car, ['manufacturer', 'car', 'reference']) == nestedCar
        decoder.findPropertyOnObject(car, ['manufacturer', 'car', 'reference', 'name']) == 'Cruze'
    }

    void "find bound property value"() {
        given:
        def nestedCar = new Car(name: 'Cruze')
        def man = new Manufacturer(tradingName: 'Holden', carReference: nestedCar)
        def car = new Car(name: 'Commodore', manufacturer: man)
        vars.bindVariable('car', car)

        expect:
        decoder.decode(vars, 'das car notthere') == null
        decoder.decode(vars, 'das car name') == 'Commodore'
        decoder.decode(vars, 'das car manufacturer') == man
        decoder.decode(vars, 'das car manufacturer trading') == null
        decoder.decode(vars, 'das car manufacturer trading name') == 'Holden'
        decoder.decode(vars, 'das car manufacturer car') == null
        decoder.decode(vars, 'das car manufacturer car reference') == nestedCar
        decoder.decode(vars, 'das car manufacturer car reference name') == 'Cruze'
    }

}

class Car {
    String name
    Manufacturer manufacturer
}

class Manufacturer {
    String tradingName
    Car carReference
}
