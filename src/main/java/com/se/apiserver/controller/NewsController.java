package com.se.apiserver.controller;

import com.google.gson.Gson;
import com.se.apiserver.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@CrossOrigin // 서버  두단을 합칠때 오류가 나지 않도록 설
@RestController() //실행시 저절로 객체를 생성시킨다.
public class NewsController {

    @Autowired // 상위 패키지에 존재하는 클래스들을 가져 올 수 있도록 만든다
            NewsService newsService; //뉴 서비스 클래스 객체 생성

    @Autowired
    Gson gson; //json을 더 좋게 사용하기 위한 google의 gson

    @RequestMapping(method = RequestMethod.GET, value="/search",produces = MediaType.APPLICATION_JSON_VALUE)
    //로컬의 서치루틴에서 해당 작업
    public @ResponseBody ResponseEntity<Object> findNewsList() throws Exception{ //nid가 10, 12인걸 뽑아내는곳
        ArrayList<Integer> idList = new ArrayList<Integer>();
        idList.add(10);
        idList.add(12);
        return new ResponseEntity<Object>(gson.toJson(newsService.findNewsById(idList)),HttpStatus.OK);
    }

}