package com.se.apiserver.repository;

import com.se.apiserver.entity.NewsWordRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by LeeHyungRae on 2016. 11. 26..
 */
public interface NewsWordRelationRepository extends JpaRepository<NewsWordRelation,Integer> {
    @Query("select nwr from tbl_news_word nwr where wid in :wordIdxList")
    List<NewsWordRelation> getNewsWordRelationListByWordIdxList(@Param("wordIdxList") List<Integer> wordIdxList);

    @Query("select nwr from tbl_news_word nwr where nid in :newsIdList")
    List<NewsWordRelation> getNewsWordRelationListByNewsIdList(@Param("newsIdList")List<Integer> newsIdList);

}
