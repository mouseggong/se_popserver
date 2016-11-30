package com.se.apiserver.entity;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * 기사의 전체 TF-IDF값을 구해 검색시의 뉴스의 출력 순서를 정하는데에 쓰이는 테이블
 * Created by LeeHyungRae on 2016. 11. 22..
 */
public class GroupedNewsWordRelation {
    private int nid;
    private List<NewsWordRelation> newsWordRelationList;

    public GroupedNewsWordRelation(){
        newsWordRelationList = Lists.newArrayList();
    }

    public int getNid(){
        return nid;
    }

    public void setNid(int nid){
        this.nid = nid;
    }

    public void addNewsWordRelation(NewsWordRelation newsWordRelation){
        newsWordRelationList.add(newsWordRelation);
    }

    public double calcTfIdfSum(){
        double sumOfTfidf = 0;
        for(NewsWordRelation rel : newsWordRelationList)
            sumOfTfidf+=rel.getTfidf();

        return sumOfTfidf;
    }
}
