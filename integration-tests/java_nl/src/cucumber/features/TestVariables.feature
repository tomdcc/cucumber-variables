#language: nl
Functionaliteit: Test Variabelen

  @Dutch
  Scenario: Test Enkelvoudige Variabele
    Gegeven de hondennaam is gevuld met 'Max'
    Dan heeft de hondennaam variabele waarde 'Max'
    Dan heeft de hondennaam variabele de waarde 'Max'
    Dan heeft de hondennaam variabele de waarde /\w{3}/
    Dan heeft de hondennaam variabele de waarde van de hondennaam

  @Dutch
  Scenario: Test Meervoudgie Variabelen
    Gegeven de hondennaam is gevuld met 'Max'
    En de hondennaam is gevuld met 'Kensington'
    Dan heeft de hondennaam variabele waarde 'Kensington'
    En heeft de 1ste hondennaam variabele waarde 'Max'
    En heeft de 2de hondennaam variabele waarde 'Kensington'
    En heeft de 2de hondennaam variabele waarde /Kens.*/

  @Dutch
  Abstract Scenario: Test Variabelen With Voorbeelden
    Gegeven de kattennaam is gevuld met <cat_name>
    Dan heeft de kattennaam variabele waarde <cat_name>
  Voorbeelden:
    | cat_name     |
    | 'Piccadilly' |
    | 'Blah'       |
