# Sample REST CRUD API with Spring Boot, Mysql, JPA and Hibernate

## Steps to Setup

**1. Download and unzip the spring-boot-rest-api-anagram

**2. Create Mysql database**
```bash
create database user_database
```

**3. Change mysql username and password as per your installation**

+ open `src/main/resources/application.properties`

+ change `spring.datasource.username` and `spring.datasource.password` as per your mysql installation

**4. Build and run the app using Intellij**
** Right click the Application.class in the Project panel, click Run 'Application.main()' or Ctrl+Shift+F10**

The app will start running at <http://localhost:8080>.

**5. The Unit test runs using the H2 database(In memory database). The below configurations for H2 DB is stored in application-test.properties**

+ open `src/test/resources/application-test.properties`

**6. The Unit test uses the local port instead of port 8080

<http://localhost:LOCALPORT/api/v1>.

## Explore Rest APIs

The app defines following CRUD APIs.

    - Get all words
    GET /api/v1/words

    - Get a word which matches the id
    GET /api/v1/word/id/{id}

    - Get a word which matches the value
    GET /api/v1/word/value/{value}

    - Endpoint that returns a count of words in the mysql DB and min/max/median/average word length
    GET /api/v1/words/stats

    - Returns a JSON array of English-language words that are anagrams of the word passed in the URL
    - This endpoint should support an optional query param that indicates the maximum number of results to return.  
    GET /api/v1/words/anagrams/{value}/{listSize}
    
    - Endpoint that identifies words with the most anagrams
    GET /api/v1/words/anagrams/max
    
    - Endpoint that takes a set of words and returns whether or not they are all anagrams of each other
    GET /api/v1/words/anagrams/url/{value}
    
    - Endpoint to return all anagram groups of size >= *x*
    GET /api/v1/words/anagrams/{listSize}

    - Takes a JSON array of English-language words and adds them to the mysql DB
    POST /api/v1/words

    - Takes a JSON array of English-language words from dictionary.txt of the resources folder and adds them to the mysql DB
    POST /api/v1/words/dictionary
    
    - `DELETE /words/:word.json`: Deletes a single word from the mysql DB
    DELETE /api/v1/word/value/{value}

    - Endpoint to delete a word *and all of its anagrams*
    DELETE /api/v1/words/anagrams/{value}

    - `DELETE /words.json`:Deletes all contents of the mysql DB
    DELETE /api/v1/words/all

