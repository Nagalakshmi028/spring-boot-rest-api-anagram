package com.api.anagrams;

import com.api.anagrams.model.Word;
import org.junit.*;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@SqlGroup({
        @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:database/seed.sql"),
        @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:database/purge.sql")
        })
public class WordControllerTest {

    final static Logger LOG = LoggerFactory.getLogger(WordControllerTest.class);

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    private String getRootUrl() {
        return "http://localhost:"+port+"/api/v1";
    }

    private static final int expectedStatusCode = 200;


 // @Before
    @Ignore
    public void testPostCreateWordsFromDictionary() {
        Word newWord1 = new Word();
        newWord1.setValue("test");
        List<Word> wordList = new ArrayList<>();
        wordList.add(newWord1);

        ResponseEntity<List> postResponse = restTemplate
                .postForEntity(getRootUrl() + "/words/dictionary", wordList, List.class);

        LOG.info("Response_Body:"+postResponse.getBody());
        LOG.info("Response_Header:"+postResponse.getHeaders());

        Assert.assertNotNull(postResponse);
        Assert.assertNotNull(postResponse.getBody());
        Assert.assertEquals(expectedStatusCode, postResponse.getStatusCodeValue());
    }


    @Test
    public void testGetAllWords() {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);

        ResponseEntity<List> response = restTemplate.exchange(getRootUrl() + "/words",
                HttpMethod.GET, entity, List.class);

        LOG.info("Response_Body:"+response.getBody());
        LOG.info("Response_Header:"+response.getHeaders());

        Assert.assertNotNull(response.getBody());
        Assert.assertEquals(expectedStatusCode, response.getStatusCodeValue());
    }

    @Test
    public void testGetWordsById() {
        Word word = restTemplate.getForObject(getRootUrl() + "/word/id/1", Word.class);

        LOG.info("WordId:"+word.getId());
        LOG.info("WordValue:"+word.getValue());

        Assert.assertNotNull(word);
        Assert.assertEquals(1,word.getId());
    }

    @Test
    public void testGetWordByValue() {
        Word word = restTemplate.getForObject(getRootUrl() + "/word/value/read", Word.class);

        LOG.info("WordId:"+word.getId());
        LOG.info("WordValue:"+word.getValue());

        Assert.assertNotNull(word);
        Assert.assertEquals("read",word.getValue());
    }

    @Test
    public void testGetStats() {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);

        ResponseEntity<List> response = restTemplate.exchange(getRootUrl() + "/words/stats",
                HttpMethod.GET, entity, List.class);

        LOG.info("Response_Body:"+response.getBody());
        LOG.info("Response_Header:"+response.getHeaders());

        Assert.assertNotNull(response.getBody());
        Assert.assertEquals(expectedStatusCode, response.getStatusCodeValue());
    }

    @Test
    public void testGetAnagrams() {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);

        ResponseEntity<List> response = restTemplate.exchange(getRootUrl() + "/words/anagrams/silent/2",
                HttpMethod.GET, entity, List.class);

        LOG.info("Response_Body:"+response.getBody());
        LOG.info("Response_Header:"+response.getHeaders());

        Assert.assertNotNull(response.getBody());
        Assert.assertEquals(expectedStatusCode, response.getStatusCodeValue());
    }

    @Test
    public void testGetUrlAnagrams() {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);

        ResponseEntity<Map> response = restTemplate
                .exchange(getRootUrl() + "/words/anagrams/url/silent listen read enlist",
                HttpMethod.GET, entity, Map.class);

        LOG.info("Response_Body:"+response.getBody());
        LOG.info("Response_Header:"+response.getHeaders());

        Assert.assertNotNull(response.getBody());
        Assert.assertEquals(expectedStatusCode, response.getStatusCodeValue());
    }

    @Test
    public void testGetXSizedAnagrams() {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);

        ResponseEntity<List> response = restTemplate
                .exchange(getRootUrl() + "/words/anagrams/2", HttpMethod.GET, entity, List.class);

        LOG.info("Response_Body:"+response.getBody());
        LOG.info("Response_Header:"+response.getHeaders());

        Assert.assertNotNull(response.getBody());
        Assert.assertEquals(expectedStatusCode, response.getStatusCodeValue());
    }

    @Test
    public void testPostCreateWord() {
        Word newWord1 = new Word();
        newWord1.setValue("pot");
        Word newWord2 = new Word();
        newWord2.setValue("top");
        List<Word> wordList = new ArrayList<>();
        wordList.add(newWord1);
        wordList.add(newWord2);

        ResponseEntity<List> postResponse = restTemplate
                .postForEntity(getRootUrl() + "/words", wordList, List.class);

        LOG.info("Response_Body:"+postResponse.getBody());
        LOG.info("Response_Header:"+postResponse.getHeaders());

        Assert.assertNotNull(postResponse);
        Assert.assertNotNull(postResponse.getBody());
        Assert.assertEquals(expectedStatusCode, postResponse.getStatusCodeValue());
    }

    @Test
    public void testDeleteWord() {
        int id = 2;
        Word word = restTemplate.getForObject(getRootUrl() + "/words/" + id, Word.class);
        Assert.assertNotNull(word);

        restTemplate.delete(getRootUrl() + "/words" + id);

        try {
            word = restTemplate.getForObject(getRootUrl() + "/words" + id, Word.class);
        } catch (final HttpClientErrorException e) {
            Assert.assertEquals(e.getStatusCode(), HttpStatus.NOT_FOUND);
        }
    }

    @Test
    public void testDeleteAllAnagramWords() {
        List<Word> words = restTemplate.getForObject(getRootUrl() + "/words/anagrams/silent/3", List.class);
        Assert.assertNotNull(words);

        restTemplate.delete(getRootUrl() + "/words/anagrams/silent");

        try {
            words = restTemplate.getForObject(getRootUrl() + "/words/anagrams/silent/3", List.class);
        } catch (final HttpClientErrorException e) {
            Assert.assertEquals(e.getStatusCode(), HttpStatus.NOT_FOUND);
        }
    }

    @After
    public void testDeleteAllWords() {
        List<Word> words = restTemplate.getForObject(getRootUrl() + "/words", List.class);
        Assert.assertNotNull(words);

        restTemplate.delete(getRootUrl() + "/words/all");

        try {
            words = restTemplate.getForObject(getRootUrl() + "/words", List.class);
        } catch (final HttpClientErrorException e) {
            Assert.assertEquals(e.getStatusCode(), HttpStatus.NOT_FOUND);
        }
    }
}
