package com.se.apiserver.service;

import com.se.apiserver.entity.NewsContent;
import com.se.apiserver.repository.NewsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;


/**
 * Created by LeeHyungRae on 2016. 11. 22..
 */
//@Service 서비스 객체

@Service
public class NewsService { //컨트롤러에서 함수를 정의했다면 여기는 구현해 놓는 클래스

    @Autowired
    NewsRepository newsRepository;

    public ArrayList<NewsContent> findNewsById(ArrayList<Integer> newsIdList) throws Exception{
        return (ArrayList<NewsContent>)newsRepository.findAll(newsIdList);
    }
    //tf - idf 구하는 함수 추가
//    public ArrayList<NewsContent> findTfIdfByWord(ArrayList<Integer> newsIdList) throws Exception{
//        return (ArrayList<NewsContent>)newsRepository.findAll(newsIdList);
//    }
}
