Feature: Library Management View High Level
  Specifications of the behavior of the Library Management View

  Background: 
    Given The database contains a few books
    And The Library Management View is shown

  Scenario: Add a new book
    Given The user provides book data in the text fields
    When The user clicks the "Add Book" button
    Then The list contains the new book

  Scenario: Add a new book with an existing id
    Given The user provides book data in the text fields, specifying an existing id
    When The user clicks the "Add Book" button
    Then An error is shown containing the name of the existing book

  Scenario: Delete a book
    Given The user selects a book from the list
    When The user clicks the "Delete Selected Book" button
    Then The book is removed from both lists