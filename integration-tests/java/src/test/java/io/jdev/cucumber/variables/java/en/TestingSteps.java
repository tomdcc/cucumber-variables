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

package io.jdev.cucumber.variables.java.en;

import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Then;
import io.jdev.cucumber.variables.core.BasicSteps;
import io.jdev.cucumber.variables.en.EnglishDecoder;
import org.junit.Assert;

import java.util.regex.Pattern;

public class TestingSteps extends BasicSteps {

    BasicSteps steps = new BasicSteps();

	@Before
	public void before(Scenario scenario) {
		steps.before(scenario, new EnglishDecoder());
	}

	@After
	public void after(Scenario scenario) {
		steps.after(scenario);
	}

	@Then("^(the .*) variable has (?:the )?value (?:of )?(.*)$")
	public void assertValue(String name, String rawValue) {
        Object expectedValue = steps.getVariable(rawValue);
        String actualValue = (String) steps.getVariable(name);
        if(expectedValue instanceof Pattern) {
            Assert.assertTrue(((Pattern) expectedValue).matcher(actualValue).matches());
        } else {
            Assert.assertEquals(expectedValue, actualValue);
        }
	}

}
