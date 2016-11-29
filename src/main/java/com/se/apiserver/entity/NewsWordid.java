package com.se.apiserver.entity;


import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
// 3번테이블 - 뉴스와 단어의 연관 테이블
@Embeddable
public class NewsWordId implements Serializable {
    @Column(name = "nid", nullable = false)
    private int nid;
    @Column(name = "wid", nullable = false)
    private int wid;

    public int getNid() {
        return nid;
    }

    public void setNid(int nid) {
        this.nid = nid;
    }

    public int getWid() {
        return wid;
    }

    public void setWid(int wid) {
        this.wid = wid;
    }
}