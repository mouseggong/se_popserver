package com.se.apiserver.entity;

/**
 * Created by LeeHyungRae on 2016. 11. 29..
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
