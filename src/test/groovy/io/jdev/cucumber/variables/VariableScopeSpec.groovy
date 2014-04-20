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

package io.jdev.cucumber.variables

import cucumber.api.Scenario
import io.jdev.cucumber.variables.core.Decoder
import io.jdev.cucumber.variables.core.VariableSet
import spock.lang.Specification

import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class VariableScopeSpec extends Specification {
	Scenario scenario;
	Decoder decoder;
	static ExecutorService executor

	void setupSpec() {
		executor = Executors.newSingleThreadExecutor()
	}

	void cleanupSpec() {
		executor.shutdownNow()
	}

	void setup() {
		scenario = Mock(Scenario)
		decoder = Mock(Decoder)
	}

	void cleanup() {
		VariableScope.removeCurrentScope()
		// remove from other thread too
		executor.submit({-> VariableScope.removeCurrentScope()})
	}

	void "getCurrentScope with scenario binds to current thread if no existing scope"() {
		when:
		def scope = VariableScope.getCurrentScope(scenario, Mock(Decoder))

		then: 'current thread has it bound'
		VariableScope.getCurrentScope() == scope

		and:
		executor.submit({->VariableScope.getCurrentScope()} as Callable).get() == null
	}

	void "getCurrentScope replaces current scope if bound to different scenario"() {
		given: 'current scope bound to different scenario'
		VariableScope.getCurrentScope(Mock(Scenario), Mock(Decoder))

		when:
		def scope = VariableScope.getCurrentScope(scenario, Mock(Decoder))

		then: 'current thread has it bound'
		VariableScope.getCurrentScope() == scope
	}

	void "getCurrentScope returns current scope if bound same scenario"() {
		given: 'current scope bound to different scenario'
		def origScope = VariableScope.getCurrentScope(scenario, Mock(Decoder))

		when:
		def scope = VariableScope.getCurrentScope(scenario, Mock(Decoder))

		then: 'same one returned'
		scope == origScope

		and: 'current thread has it bound'
		VariableScope.getCurrentScope() == origScope
	}

	void "removeCurrentScope removes scope from thread"() {
		given: 'scope on both threads'
		VariableScope.getCurrentScope(scenario, Mock(Decoder))
		def otherThreadScope = executor.submit({-> VariableScope.getCurrentScope(scenario, Mock(Decoder)) } as Callable).get()

		when: 'remove on current thread'
		VariableScope.removeCurrentScope()

		then: 'not there any more'
		VariableScope.getCurrentScope() == null

		when: 'fetch from other thread'
		def returnedFromOtherThread = executor.submit({-> VariableScope.getCurrentScope() } as Callable).get()

		then: 'still there on other thread'
		otherThreadScope == returnedFromOtherThread
	}

	void "store and decode calls appropriate decoder"() {
		given: 'scope'
			def scope = new VariableScope(decoder, scenario)

		when: 'store'
			scope.storeVariable('foo', 'bar')
			scope.storeVariable('foo', 'baz')

		and: 'ask for variable'
			def result = scope.decodeVariable('foo')

		then: 'decoder called with expected stuff'
			decoder.decode(_, 'foo') >> { VariableSet vars, String name ->
				assert vars.getVariable(name) == ['bar', 'baz']
				return 'blah'
			}

		and: 'get expected result from decoder'
			result == 'blah'
	}

}
