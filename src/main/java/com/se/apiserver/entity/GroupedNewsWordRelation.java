package com.se.apiserver.entity;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by LeeHyungRae on 2016. 11. 29..
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
