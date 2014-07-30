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

import cucumber.api.Scenario
import io.jdev.cucumber.variables.VariableScope
import spock.lang.Specification

class BasicVariableStepsSpec extends Specification {

	BasicVariableSteps steps
	Scenario scenario
	Scenario previousScenario

	void setup() {
		steps = new BasicVariableSteps()
		scenario = Mock(Scenario)
		previousScenario = Mock(Scenario)
	}

	void cleanup() {
		VariableScope.removeCurrentScope()
	}

	void "before step should set up variable scope when none present"() {
		given: 'no current scope attached to thread'
		VariableScope.removeCurrentScope()

		when: 'call before method'
		steps.before(scenario)

		then: 'scope created and attached to given scenario'
		def scope = VariableScope.currentScope
		scope != null
		scope.scenario == scenario
	}

	void "before step should replace variable scope when one already present"() {
		given: 'existing scope attached to thread'
		def previousScope = VariableScope.getCurrentScope(previousScenario, new EnglishDecoder())
		previousScope.storeVariable("dog name", 'Mork')

		when: 'call before method'
		steps.before(scenario)

		then: 'scope has replaced previous one on the thread'
		def scope = VariableScope.currentScope
		scope != null
		scope.scenario == scenario

		// ok it's more of an integration test
		and: 'does not have previously set variable'
		scope.decodeVariable('the dog name') == null

		when: 'set a variable'
		scope.storeVariable("dog name", 'Mindy')

		then: 'decodes as expected'
		scope.decodeVariable('the dog name') == 'Mindy'
		scope.decodeVariable('the 1st dog name') == 'Mindy'

		and: 'previous scope unchanged'
		previousScope.decodeVariable('the 1st dog name') == 'Mork'
		previousScope.decodeVariable('the dog name') == 'Mork'
	}

	void "set step should set variable"() {
		given: 'current scope attached to thread'
		steps.before(scenario)

		when: 'call set method'
		def val = 'blah'
		steps.set('foo', "'$val'")

		then: 'scope has given variable'
		VariableScope.currentScope.decodeVariable('the foo') == val
	}

}
