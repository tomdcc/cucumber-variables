cucumber-variables ![Build status](https://travis-ci.org/tomdcc/cucumber-variables.svg?branch=master)
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

Changelog
---------

#### Version 0.4
 - More flexible string literals

#### Version 0.3
 - Regex literals 

#### Version 0.2
 - Support for cucumber-groovy steps

#### Version 0.1
 - Initial version
 - Support for cucumber-java steps

