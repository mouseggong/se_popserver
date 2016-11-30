package com.se.apiserver.entity;

import javax.persistence.*;

/**
 * 단어의 Wid와 Wid의 매칭되는 진짜 단어, 전체 문서에서의 해당 단어의 개수를 구할 수 있다 TF를 구하기 쉽도록 만듬
 * Created by LeeHyungRae on 2016. 11. 22..
 */
@Entity(name = IndexedWord.TABLE_NAME )
public class IndexedWord {

    public static final String TABLE_NAME = "tbl_allnews";
    private static final String COL_WORD = "realword";


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int wid;

    @Column(name = COL_WORD, unique = true)
    private String word;

    private int allcount;

    public IndexedWord(){}

    public IndexedWord(String word, int allcount) {
        this.word = word;
        this.allcount = allcount;
    }

    public int getWid() {
        return wid;
    }

    public void setWid(int wid) {
        this.wid = wid;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public int getAllcount() {
        return allcount;
    }

    public void setAllcount(int allcount) {
        this.allcount = allcount;
    }

    @Override
    public int hashCode() {
        return wid;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj.hashCode()==this.hashCode())
            return true;
        else
            return false;
    }
}