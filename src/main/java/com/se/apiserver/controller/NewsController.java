package com.se.apiserver.controller;

import com.google.gson.Gson;
import com.se.apiserver.service.NewsService;
import com.se.apiserver.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.ArrayList;

import static sun.plugin2.message.JavaScriptSlotOpMessage.GET;

@CrossOrigin
//@CrossOrigin 서버 두단을 합칠때 오류가 나지 않도록 설정해준
@RestController()
//@RestController() 실행시 저절로 객체를 생성시킨다.
public class NewsController {

    @Autowired
    NewsService newsService; //뉴 서비스 클래스 객체 생성

    @Autowired
    SearchService searchService;
    @Autowired
    Gson gson; //json을 더 좋게 사용하기 위한 google의 gson

    @RequestMapping(method = RequestMethod.GET, value="/search",produces = MediaType.APPLICATION_JSON_VALUE)
    //로컬의 서치루틴에서 해당 작업
    //@ResponseThirdPage.htmlBody 리턴 타입이 HTTP(Json)의 응답 메시지로 전송
    public @ResponseBody ResponseEntity<Object> findNewsList() throws Exception{ //nid가 10, 12인걸 뽑아내는곳
        ArrayList<Integer> idList = new ArrayList<Integer>();
        idList.add(10);
        idList.add(12);
        idList.add(18);
        idList.add(20);
        idList.add(22);
        idList.add(24);
        idList.add(26);
        return new ResponseEntity<Object>(gson.toJson(newsService.findNewsById(idList)),HttpStatus.OK);
    }
//    @RequestMapping(method = RequestMethod.GET, value="/search/{keyword}",produces = MediaType.APPLICATION_JSON_VALUE)
//    public @ResponseBody ResponseEntity<Object> findNewsList(@PathVariable("keyword")String keyword) throws Exception{
//
//        return new ResponseEntity<Object>(gson.toJson(searchService.stemmingKeyword(keyword)),HttpStatus.OK);
//    }
}