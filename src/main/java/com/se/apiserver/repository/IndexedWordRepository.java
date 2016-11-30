package com.se.apiserver.repository;

import com.se.apiserver.entity.IndexedWord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by LeeHyungRae on 2016. 11. 25..
 */

public interface IndexedWordRepository extends JpaRepository<IndexedWord, Integer> {
    @Query("select w from tbl_allnews w where realword in :wordList")
    List<IndexedWord> getAllNewsContentByWord(@Param("wordList") List<String> wordList);

    @Query("select nid from tbl_news where photo_url is not 'getPhoto_url Error'")
    List<Integer> getExistPhotoURL();
}
