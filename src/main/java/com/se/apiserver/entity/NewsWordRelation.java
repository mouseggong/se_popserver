package com.se.apiserver.entity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 뉴스와 워드의 관계를 나타내주는 테이블 (해당 문서의 해당 단어의 유무와 해당 단어가 몇번 나왔는지를 알 수 있다)
 * Created by LeeHyungRae on 2016. 11. 22..
 */
@Entity(name = NewsWordRelation.TABLE_NAME)
public class NewsWordRelation {
    public static final String TABLE_NAME = "tbl_news_word";

    @Id
    private int id;

    private int wid;

    private int nid;

    @Column(name = "partialcount")
    private int partialcount;


    @Column(name = "tf")
    private double tf;

    @Transient
    private double tfidf;

    public NewsWordRelation() {
    }

    public int getWid() {
        return wid;
    }

    public void setWid(int wid) {
        this.wid = wid;
    }

    public int getNid() {
        return nid;
    }

    public void setNid(int nid) {
        this.nid = nid;
    }

    public int getPartialcount() {
        return partialcount;
    }

    public void setPartialcount(int partialcount) {
        this.partialcount = partialcount;
    }


    public double getTf() {
        return tf;
    }

    public void setTf(int tf) {
        this.tf = tf;
    }

    public double getTfidf(){
        return tfidf;
    }

    public void setTfIdf(double tfidf){
        this.tfidf = tfidf;
    }
}