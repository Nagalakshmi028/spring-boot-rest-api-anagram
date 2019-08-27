package com.api.anagrams.repository;

import com.api.anagrams.model.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * The interface Word repository.
 *
 * @author Nagalakshmi Paramasivam
 */
@Repository
public interface WordRepository extends JpaRepository<Word, Long> {
    Word findByValue(String word);
    Word findTop1ByOrderByValueAsc();
    List<Word> findAllByOrderByValueAsc();
    Word findTop1ByOrderByValueDesc();
}
