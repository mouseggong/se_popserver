package com.se.apiserver.entity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by Ianohjh on 2016-11-23.
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