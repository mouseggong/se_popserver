package com.se.apiserver.entity;

import javax.persistence.*;


/**
 * 모든 뉴스의 내용을 담고있는 테이블
 * Created by LeeHyungRae on 2016. 11. 22..
 */
// 1번 테이블 - 전체 뉴스 들어갈 테이블 생성
@Entity(name = NewsContent.TABLE_NAME)
public class NewsContent {

    public static final String TABLE_NAME = "tbl_news";
    private static final String COL_TITLE = "title";
    private static final String COL_BODY = "body";
    private static final String COL_DATE = "date";
    private static final String COL_REPORTER = "reporter";
    private static final String COL_URL = "url";
    private static final String COL_WORDCOUNT = "wordcount";
    private static final String COL_PHOTO = "photo_url";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int nid;

    @Column(name = COL_TITLE,nullable = false)
    private String title;

    @Column(name=COL_BODY,columnDefinition="LONGTEXT")
    private String body;

    @Column(name=COL_DATE)
    private String date;

    @Column(name=COL_REPORTER)
    private String reporter;

    @Column(name=COL_URL, unique = true)
    private String url;

    @Column(name=COL_WORDCOUNT)
    private int wordcount;

    @Column(name=COL_PHOTO)
    private String photo_url;

    public NewsContent(){

    }

    public NewsContent(String url,String title,String body,String date,String reporter, int wordcount, String photo_url){
        this.url = url;
        this.title = title;
        this.body = body;
        this.date = date;
        this.reporter = reporter;
        this.wordcount = wordcount;
        this.photo_url = photo_url;
    }


    public int getNid() {
        return nid;
    }

    public void setNid(int nid) {
        this.nid = nid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getReporter() {
        return reporter;
    }

    public void setReporter(String reporter) {
        this.reporter = reporter;
    }

    public String getUrl(){
        return url;
    }

    public void setUrl(String url){
        this.url = url;
    }

    public int getWordcount() { return wordcount; }

    public void setWordcount(int wordcount) { this.wordcount = wordcount; }

    public void setPhoto_url(String photo_url) { this.photo_url = photo_url; }

    public String getPhoto_url() { return photo_url; }
}