package com.api.anagrams.controller;

import com.api.anagrams.anagram.AnagramImpl;
import com.api.anagrams.exception.ResourceNotFoundException;
import com.api.anagrams.model.Word;
import com.api.anagrams.repository.WordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * The type Word controller.
 *
 * @author Nagalakshmi Paramasivam
 */
@RestController
@RequestMapping("/api/v1")
public class WordController {

    @Autowired
    private WordRepository wordRepository;

    /**
     * Get all words list.
     *
     * @return the list
     */
    @GetMapping("/words")
    public List<Word> getAllWords() {

        return wordRepository.findAll();
    }

    /**
     * Gets words by id.
     *
     * @param wordId the word id
     * @return the words by id
     * @throws ResourceNotFoundException the resource not found exception
     */
    @GetMapping("/word/id/{id}")
    public ResponseEntity<Word> getWordById(@PathVariable(value = "id") Long wordId)
            throws ResourceNotFoundException {
        Word word = wordRepository
                        .findById(wordId)
                        .orElseThrow(() -> new ResourceNotFoundException("Word not found on :: " + wordId));

        return ResponseEntity.ok().body(word);
    }

    /**
     * Gets words by value.
     *
     * @param searchWord the word value
     * @return the word
     * @throws ResourceNotFoundException the resource not found exception
     */
    @GetMapping("/word/value/{value}")
    public ResponseEntity<Word> getWordByValue(@PathVariable(value = "value") String searchWord)
            throws ResourceNotFoundException {
        Word word = wordRepository
                        .findByValue(searchWord);

        return ResponseEntity.ok().body(word);
    }

    /**
     * Get count, smallest word, largest word, avg length, median of the words list.
     *
     * @return the list
     */
    @GetMapping("/words/stats")
    public List<String> getStats() {
        Long count = wordRepository.count();
        String minWord = wordRepository.findTop1ByOrderByValueAsc().getValue();
        String maxWord = wordRepository.findTop1ByOrderByValueDesc().getValue();
        List<Word> sortedWords = wordRepository.findAllByOrderByValueAsc();
        int sum = 0, median = 0;

        for(Word w: sortedWords) {
            sum = sum + w.getValue().length();
        }

        if((sortedWords.size() != 0) && (sortedWords.size()%2 == 0))
            median = (sortedWords.size()/2)-1;
        else
            median = sortedWords.size()/2;

        List<String> stats = new ArrayList<>();
        stats.add("count of words:"+ count);
        stats.add("smallest word:"+ minWord+", size:"+minWord.length());
        stats.add("largest word:"+ maxWord+", size:"+maxWord.length());
        stats.add("avg length of the list of words:"+ sum/count);
        stats.add("median word:"+sortedWords.get(median));

        return stats;
    }

    /**
     * Get the anagrams for a word with listSize.
     *
     * @param anagramWord the word value
     * @param resultCount the count of the anagrams to be retrieved
     * @return the list
     */
    @GetMapping("/words/anagrams/{value}/{listSize}")
    public List<String> getAnagrams(@PathVariable(value = "value") String anagramWord,
                                       @PathVariable(value = "listSize") int resultCount) {
        List<Word> words = wordRepository.findAll();
        List<String> outputs = new ArrayList<String>();

        for(Word w: words) {
            String output = AnagramImpl.Anagram(anagramWord, w.getValue());
            if(output != null && outputs.size() <= resultCount-1)
                outputs.add(output);
        }

        return outputs;
    }

    /**
     * Get word with max anagrams.
     *
     * @return the string
     */
    @GetMapping("/words/anagrams/max")
    public String getMaxAnagrams() {
        List<Word> words = wordRepository.findAll();
        HashMap<String, List<String>> map = findAllAnagrams(words);
        HashMap<String, List<String>> smap = sortMap(map);
        Iterator<Map.Entry<String, List<String>>> entries = smap.entrySet().iterator();
        int flag = 0;

        while (entries.hasNext()) {
            flag++;
            Map.Entry<String, List<String>> entry = entries.next();
            String mapKey=entry.getKey();
            if(flag == smap.size()) {
                return "Key = " + mapKey + ", Value = " + smap.get(mapKey);
            }
        }

        return null;
    }

    /**
     * Get all the anagrams for a word.
     *
     * @param words the list
     * @return the map
     */
    private HashMap<String, List<String>> findAllAnagrams(List<Word> words){
        HashMap<String, List<String>> map = new HashMap<>();

        for(Word w1 : words) {
            List<String> outputs = new ArrayList<String>();
            for(Word w2 : words) {
                String output = AnagramImpl.Anagram(w1.getValue(), w2.getValue());
                if (output != null)
                    outputs.add(output);
            }
            map.put(w1.getValue(),outputs);
        }

        return map;
    }

    /**
     * Get the list sorted by the anagrams result count for each word in the list.
     *
     * @param map the word value and its anagrams
     * @return the sorted map
     */
    private HashMap<String, List<String>> sortMap(HashMap<String, List<String>> map) {
        List<Map.Entry<String, List<String>> > list = new LinkedList<Map.Entry<String, List<String>> >(map.entrySet());
        HashMap<String, List<String>> temp = new LinkedHashMap<String, List<String>>();

        Collections.sort(list, new Comparator<Map.Entry<String, List<String>> >() {
            public int compare(Map.Entry<String, List<String>> o1, Map.Entry<String, List<String>> o2) {
                return (Integer.compare(o1.getValue().size(), o2.getValue().size()));
            }
        });

        for (Map.Entry<String, List<String>> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }

        return temp;
    }

    /**
     * Get the anagrams of the words given in the url
     *
     * @param stringWord containing more than one word
     * @return the map
     */
    @GetMapping("/words/anagrams/url/{value}")
    public HashMap<String, List<String>> getUrlAnagrams(@PathVariable(value = "value") String stringWord) {
        List<String> words = Arrays.asList(stringWord.split("\\s+"));
        List<Word> anagramWords = new ArrayList<>();

        for(int i = 0; i < words.size(); i++) {
        Word w = new Word();
        w.setId(i);
        w.setValue(words.get(i));
            anagramWords.add(w);
        }

        HashMap<String, List<String>> map = findAllAnagrams(anagramWords);

        return map;
    }

    /**
     * Get word with anagrams list size of x.
     *
     * @return the list
     */
    @GetMapping("/words/anagrams/{listSize}")
    public List<List<String>> getXSizedAnagrams(@PathVariable(value = "listSize") int resultCount) {
        List<Word> words = wordRepository.findAll();
        HashMap<String, List<String>> map = findAllAnagrams(words);
        List<List<String>> output = new ArrayList<>();
        Iterator it = map.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry<String, List<String>> pair = (Map.Entry)it.next();
            if(pair.getValue().size() == resultCount) {
                output.add(pair.getValue());
            }
        }

        return output;
    }

    /**
     * Create words words.
     *
     * @param word the list of words
     * @return the list
     */
    @PostMapping("/words")
    public List<Word> createWord(@Valid @RequestBody List<Word> word) {

        return wordRepository.saveAll(word);
    }

    /**
     * Create words from dictionary.txt.
     *
     * @return the list
     * @throws FileNotFoundException the file not found exception
     */
    @PostMapping("/words/dictionary")
    public List<Word> createWordsFromDictionary() throws FileNotFoundException {
        ClassLoader classLoader = new WordController().getClass().getClassLoader();
        File file = new File(classLoader.getResource("dictionary.txt").getFile());
        Scanner s = new Scanner(file);
        ArrayList<Word> words = new ArrayList<Word>();

        while (s.hasNext()) {
            Word w = new Word();
            w.setValue(s.next());
            words.add(w);
        }

        s.close();

        return wordRepository.saveAll(words);
    }

    /**
     * Delete the word.
     *
     * @param deleteWord the word value
     * @return the map
     */
    @DeleteMapping("/word/value/{value}")
    public Map<String, Boolean> deleteWord(@PathVariable(value = "value") String deleteWord)
            throws ResourceNotFoundException {
        Word word = wordRepository.findByValue(deleteWord);
        wordRepository.delete(word);
        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted word:" +deleteWord, Boolean.TRUE);

        return response;
    }

    /**
     * Delete a word with its anagrams.
     *
     * @return the map
     * @throws ResourceNotFoundException the resource not found exception
     */
    @DeleteMapping("/words/anagrams/{value}")
    public Map<String, Boolean> deleteAllAnagramWords(@PathVariable(value = "value") String deleteWord)
            throws ResourceNotFoundException {
        List<Word> words = wordRepository.findAll();
        HashMap<String, List<String>> map = findAllAnagrams(words);
        HashMap<String, List<String>> smap = sortMap(map);
        System.out.println(smap);
        Map<String, Boolean> response = new HashMap<>();

        Iterator<Map.Entry<String, List<String>>> entries = smap.entrySet().iterator();

        while (entries.hasNext()) {
            Map.Entry<String, List<String>> entry = entries.next();
            String mapKey = entry.getKey();
            if (mapKey.contains(deleteWord)) {
                for (String delWord : smap.get(mapKey)) {
                    Word word = wordRepository.findByValue(delWord);
                    wordRepository.delete(word);
                }
                response.put("deletedAll", Boolean.TRUE);
            }
        }

        return response;
    }

    /**
     * Delete all the list of words.
     *
     * @return the map
     */
    @DeleteMapping("/words/all")
    public Map<String, Boolean> deleteAllWords() {
        List<Word> words = wordRepository.findAll();
        wordRepository.deleteAll(words);
        Map<String, Boolean> response = new HashMap<>();
        response.put("deletedAll", Boolean.TRUE);

        return response;
    }
}
