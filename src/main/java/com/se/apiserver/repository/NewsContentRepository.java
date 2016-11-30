package com.se.apiserver.repository;

import com.se.apiserver.entity.IndexedWord;
import com.se.apiserver.entity.NewsContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by LeeHyungRae on 2016. 11. 26..
 */
public interface NewsContentRepository extends JpaRepository<NewsContent,Integer> { //호찬이 한테 물어보기
    @Query("select count(*) from tbl_news")
    int getAllNewsCount();

}
