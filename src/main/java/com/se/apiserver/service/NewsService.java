package com.se.apiserver.service;

import com.se.apiserver.entity.NewsContent;
import com.se.apiserver.repository.NewsContentRepository;
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
    NewsContentRepository newsContentRepository;

    public ArrayList<NewsContent> findNewsById(ArrayList<Integer> newsIdList) throws Exception{
        return (ArrayList<NewsContent>) newsContentRepository.findAll(newsIdList);
    }

    /**
     * 수집된 뉴스의 총 갯수를 리턴한다
     * @return 수집된 뉴스의 총 갯수
     */
    public double getAllNewsCnt(){
        return (double) newsContentRepository.count();
    }

}
