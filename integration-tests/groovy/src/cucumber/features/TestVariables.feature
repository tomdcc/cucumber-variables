Feature: Test Variables

  Scenario: Test Single Variable
    Given the dog name is set to 'Max'
    Then the dog name variable has value 'Max'
    Then the dog name variable has the value of 'Max'
    Then the dog name variable has the value of /\w{3}/
    Then the dog name variable has the value of the dog name

  Scenario: Test Multiple Variables
    Given the dog name is set to 'Max'
    And the dog name is set to 'Kensington'
    Then the dog name variable has value 'Kensington'
    And the 1st dog name variable has value 'Max'
    And the 2nd dog name variable has value 'Kensington'
    And the 2nd dog name variable has value /Kens.*/

  Scenario Outline: Test Variables With Examples
    Given the cat name is set to <cat_name>
    Then the cat name variable has value <cat_name>
  Examples:
    | cat_name     |
    | 'Piccadilly' |
    | 'Blah'       |
