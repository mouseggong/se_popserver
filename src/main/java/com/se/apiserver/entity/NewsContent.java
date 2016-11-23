package com.se.apiserver.entity;

import javax.persistence.*;
import java.util.Date;

import static com.se.apiserver.entity.NewsContent.TABLE_NAME;

/**
 * Created by LeeHyungRae on 2016. 11. 22..
 */
@Entity(name=TABLE_NAME)
public class NewsContent { // 디비에서 가져올때 디비를 생성한곳과 맞춰주는 곳 (알맞게 가져오기 위해)
    public static final String TABLE_NAME = "tbl_news";
    private static final String COL_TITLE = "title";
    private static final String COL_BODY = "body";
    private static final String COL_DATE = "date";
    private static final String COL_REPORTER = "reporter";
    private static final String COL_URL = "url";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = COL_TITLE,nullable = false)
    private String title;

    @Column(name=COL_BODY,columnDefinition="LONGTEXT")
    private String body;

    @Column(name=COL_DATE)
    private Date date;

    @Column(name=COL_REPORTER)
    private String reporter;

    @Column(name=COL_URL)
    private String url;

    public NewsContent(){

    }

    public NewsContent(String url,String title,String body,Date date,String reporter){
        this.url = url;
        this.title = title;
        this.body = body;
        this.date = date;
        this.reporter = reporter;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
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
}
