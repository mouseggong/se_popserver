package com.se.apiserver.entity;

/**
 * word와 wid에 매칭되는 전체 뉴스의 tf-idf값을 갖고있는다 -> 메인 페이지 버블차트를 구성할때 쓰인다
 * Created by LeeHyungRae on 2016. 11. 22..
 */
public class GroupedWord {
    private double tfidf;
    private String word;
    private int wid;

    public int getWid(){
        return wid;
    }

    public void setWid(int wid){
        this.wid = wid;
    }

    public double getTfidf() {
        return tfidf;
    }

    public void setTfidf(double tfidf) {
        this.tfidf = tfidf;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }
}
