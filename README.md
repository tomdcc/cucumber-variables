cucumber-variables [![Build Status](https://travis-ci.org/tomdcc/cucumber-variables.svg?branch=master)](https://travis-ci.org/tomdcc/cucumber-variables)
==================

A library for developers writing cucumber-jvm steps.

It allows writing cucumber steps that can reference set up test data and other
variables without hard-coding values into your feature files.

For example:

```cucumber
   
Given a user
When I go to the profile page for the user id
Then the email field has the value of the user email
And the status field has the value of 'Current'

```

In this case, the `Given a user` step has set the variables `user id`
and `user email`. The matching steps then match `the user id` to the id, 
`the user email` to the email address and `'Current'` to that string,
minus the quotes.

This allows writing of flexible steps that can interchange literals and
variables.

Mostly at the moment this is used by the
[geb-cucumber](https://github.com/tomdcc/geb-cucumber) project. See more
examples there and in the build output at https://travis-ci.org/tomdcc/geb-cucumber. 

Installation
------------

Geb Cucumber is deployed to Maven Central and can be added to your project as a dependency using the following coordinates:

    groupId: io.jdev.cucumber
    artifactId: cucumber-variables
    version: 0.5

Or just download the jar from http://search.maven.org/ if your build system is a bit less connected.

Usage
-----

`cucumber-variables` sets up a *variable scope* for each cucumber scenario. If you are using the
`cucumber-java` or `cucumber-groovy` backends, you can include the built-in steps that are bundled
with `cucumber-variables` and they will take care of setting up the scope in each scenario. To do
this, add `classpath:io.jdev.cucumber.variables.java.en` for Java or `classpath:io.jdev.cucumber.variables.groovy.en`
for Groovy to your cucumber glue directory list.

If you aren't using one of those `cucumber-jvm` backends, you'll need to set the variable scope up yourself.
In a before hook, call `VariableScope.getCurrentScope(scenario, decoder)`, passing in the variable decoder
that you would like to use (currently there is only an `EnglishDecoder`). The call will return a scope
object that you can then store variables in and query later.

```java
VariableScope scope = VariableScope.getCurrentScope(scenario, new EnglishDcoder());
scope.storeVariable("customer id", 1234);
scope.storeVariable("customer id", 5678);
assertEquals(1234, scope.decodeVariable("the 1st customer id"));
assertEquals(5678, scope.decodeVariable("the 2nd customer id"));
assertEquals(5678, scope.decodeVariable("the customer id")); // without index, will return most recent
```

`cucumber-variables` will also decode literals that look like strings or regular expression patterns
for you:

```java
assertEquals("1234", scope.decodeVariable("'1234'"));
Pattern p = scope.decodeVariable("/\\d+/");
assertTrue(p.matcher("1234").matches());
```

This lets you mix and match references to stored values and literals in your cucumber tests, making writing
more generic steps easy.

`cucumber-variables` can also return values nested inside maps or objects:

```java
Map customer = Collections.singletonMap("firstName", "Tom");
scope.storeVariable("most recent customer", customer);
assertEquals("Tom", scope.decodeVariable("most recent customer first name"));

Product p = new Product();
p.setReferenceId(1234);
scope.storeVariable("new product", p);
assertEquals(1234, scope.decodeVariable("new product reference id"));
```

Languages
---------

Currently only English is supported. Contributions of other language decoders are very welcome.

Changelog
---------
#### Version 0.5
 - Resolve values from nested variables
 
#### Version 0.4
 - More flexible string literals

#### Version 0.3
 - Regex literals 

#### Version 0.2
 - Support for cucumber-groovy steps

#### Version 0.1
 - Initial version
 - Support for cucumber-java steps

