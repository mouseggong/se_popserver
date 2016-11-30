package com.se.apiserver.controller;

import com.google.gson.Gson;
import com.se.apiserver.entity.GroupedWord;
import com.se.apiserver.entity.NewsContent;
import com.se.apiserver.repository.IndexedWordRepository;
import com.se.apiserver.repository.NewsContentRepository;
import com.se.apiserver.service.NewsService;
import com.se.apiserver.service.SearchService;
import com.se.apiserver.viewcomponent.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
/**
 * Created by LeeHyungRae on 2016. 11. 29..
 */
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
    IndexedWordRepository indexedWordRepository;

    @Autowired
    NewsContentRepository newsContentRepository;

    @Autowired
    Gson gson; //json을 더 좋게 사용하기 위한 google의 gson

    String bubbleCSV;
    // Django에서 요청받은 키워드를 가지고 해당되는 뉴스의 내용을 담고있는 Newscontent를 이용하여 뉴스에 대한 내용을 뿌려준다
    // JSON형식으로 출력

//    @RequestMapping(method = RequestMethod.GET, value = "/start/main_photo", produces = MediaType.APPLICATION_JSON_VALUE)
//    public @ResponseBody ResponseEntity<Object> getMain_PhotoList() throws Exception {
//        List<String> photh_url = indexedWordRepository.getAllPhotoURL();
//        List<String> url = indexedWordRepository.getAllURL();
//        List<String> result = new ArrayList<String>();
//        int random = 0;
//        for(int i = 0; i < 20; i++) {
//            random = (int)(Math.random() * photh_url.size());
//            result.add(photh_url.get(random) + " " + url.get(random));
//        }
//
//        return new ResponseEntity<Object>(gson.toJson(result), HttpStatus.OK);
//    }
    //서버 돌아갈때 가장 먼저 돌아가는 함수
    @RequestMapping(method = RequestMethod.GET, value="/start/",produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<Object> createbubbleWordList() throws Exception {
        int i;
        List<Integer> allIdList = new ArrayList<Integer>();
        for (i = 1; i <= newsContentRepository.getAllNewsCount(); i++) {
            allIdList.add(i);
        }
        List<GroupedWord> bubbleWordList = searchService.getGroupedWordList(allIdList).subList(0, 40);
        bubbleCSV = searchService.groupedWordBubbleToCSV(bubbleWordList);
        return new ResponseEntity<Object>(bubbleCSV, HttpStatus.OK);
    }
    //버블생성
    @RequestMapping(method = RequestMethod.GET, value="/start/bubble/",produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<Object> bubbleWordList() throws Exception {
        return new ResponseEntity<Object>(bubbleCSV, HttpStatus.OK);
    }
    @RequestMapping(method = RequestMethod.GET, value="/search/news/{keyword}",produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<Object> findNewsList(@PathVariable("keyword")String keyword) throws Exception{
        List<Integer> newsIdList = searchService.searchNewsContentIdList(keyword);
        List<NewsContent> newsContentList = searchService.searchNewsContent(newsIdList);
        return new ResponseEntity<Object>(gson.toJson(newsContentList),HttpStatus.OK);
    }
    // Django에서 요청받은 키워드를 가지고 해당되는 단어의 내용을 담고있는 GroupedWordList를 이용하여 단어에 대한 내용 (주로  TF-IDF구함 및 정렬) 을 뿌려준다
    // JSON형식으로 출력
    @RequestMapping(method = RequestMethod.GET, value="/search/keyword/{keyword}",produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<Object> findImportantWord(@PathVariable("keyword")String keyword) throws Exception {
        List<Integer> newsIdList = searchService.searchNewsContentIdList(keyword);
        List<GroupedWord> groupedWordList = searchService.getGroupedWordList(newsIdList);
        return new ResponseEntity<Object>(gson.toJson(groupedWordList), HttpStatus.OK);
    }
    // Django에서 요청받은 키워드를 가지고 Third Page의 WordCloud를 만든다
    // CSV형식으로 출력
    @RequestMapping(method = RequestMethod.GET, value="/search/cloud/{keyword}",produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<Object> wordCloudList(@PathVariable("keyword")String keyword) throws Exception{
        List<Integer> newsIdList = searchService.searchNewsContentIdList(keyword);
        List<GroupedWord> groupedWordList = searchService.getGroupedWordList(newsIdList).subList(1,50);
        return new ResponseEntity<Object>(searchService.groupedWordCloudToCSV(groupedWordList),HttpStatus.OK);
    }
    @RequestMapping(method = RequestMethod.GET, value="/search/bubble/{keyword}",produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<Object> taste(@PathVariable("keyword")String keyword) throws Exception{
        List<Integer> newsIdList = searchService.searchNewsContentIdList(keyword);
        List<GroupedWord> groupedWordList = searchService.getGroupedWordList(newsIdList).subList(1,50);
        return new ResponseEntity<Object>(searchService.groupedWordBubbleToCSV(groupedWordList),HttpStatus.OK);
    }

    // Django에서 요청받은 키워드를 가지고 Second Page의 트리의 노드를 구성한다
    // JSON형식으로 출력
    @RequestMapping(method = RequestMethod.GET, value="/search/tree/{keyword}",produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<Object> makeTreeStem(@PathVariable("keyword")String keyword) throws Exception{
        Node Root_node = new Node();//level 1의 Root_node는 사용자가 검색한 단어
        Root_node.name= keyword; //검색어 = keyword -> root노드

        List<Integer> newsIdList = searchService.searchNewsContentIdList(keyword);//root 키워드에 대한 연관 기사
        List<GroupedWord> groupedWordList = searchService.getGroupedWordList(newsIdList).subList(0,7);

        //level 2의 sub_node는 keyword에 대한 연관 keyword
        Node[] sub_node = new Node[7];//1차 Split node개수는 7개
        Node[] leaf_node = new Node[100]; //기자수는 관련 기사에 따라 각각 다름
        for(int i = 0; i < 7; i++){
            sub_node[i] = new Node();
            sub_node[i].name = groupedWordList.get(i).getWord();
            Root_node.children.add(sub_node[i]);

            //root키워드와 sub키워드의 nid 교집합을 구한다.
            List<Integer> newsIdList_sub = searchService.searchNewsContentIdList(sub_node[i].name);//sub 키워드에 대한 연관 기사
            List<Integer> Intersection_nid = new ArrayList<Integer>();
            for(Integer nid : newsIdList_sub){
                if(newsIdList.contains(nid)){
                    Intersection_nid.add(nid);
                }
            }

            //기자이름의 중복을 제거하고 단일 이름을 추출한다.
            List<NewsContent> groupedNewsList = searchService.searchNewsContent(Intersection_nid);
            HashSet<String> removed = new HashSet<String>();
            List<String> remove_duplication_name = new ArrayList<String>();

            for(NewsContent news : groupedNewsList){
                removed.add(news.getReporter());
            }
            for(String reporter : removed){
                remove_duplication_name.add(reporter);
            }

            //level 3의 leaf_node는 sub_node와 연관된 기사를 쓴 기자들
            int index = 0;
            int count = 0;
            while(remove_duplication_name.size() != 0 && count != 3){//leaf node는 최대 3개가 된다.
                leaf_node[index] = new Node();
                List<String> node3_url = new ArrayList<String>(); // 한 기자가 쓴 연관 기사들의 list
                if(remove_duplication_name.get(index) == "") {//기자이름이 없는 경우
                    remove_duplication_name.remove("");
                    continue;
                }
                leaf_node[index].name = remove_duplication_name.get(index);
                for(NewsContent news_r : groupedNewsList){
                    if(news_r.getReporter().equals(leaf_node[index].name))
                        node3_url.add(news_r.getUrl());
                }
                leaf_node[index].URL = node3_url;
                sub_node[i].children.add(leaf_node[index]);
                leaf_node[index].parent_node = sub_node[i].name;
                remove_duplication_name.remove(leaf_node[index].name);
                count++;
            }
        }
        return new ResponseEntity<Object>(gson.toJson(Root_node),HttpStatus.OK);
    }
    // Django에서 요청받은 키워드, 연관키워드, 기자이름을 가지고 해당 나뭇잎에 해당하는 기사들을 출력한다
    @RequestMapping(method = RequestMethod.GET, value="/search/tree/{keyword}/{sub_keyword}/{reporter}",produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<Object> newscontentByReporter(@PathVariable("keyword")String keyword, @PathVariable("sub_keyword")String sub_keyword, @PathVariable("reporter")String reporter) throws Exception {
        List<NewsContent> newsList = new ArrayList<NewsContent>();
        List<Integer> sub_newsIdList = searchService.searchNewsContentIdList(sub_keyword);
        List<Integer> main_newsIdList = searchService.searchNewsContentIdList(keyword);
        List<Integer> Intersection_nid = new ArrayList<Integer>();
        for(Integer nid : sub_newsIdList){
            if(main_newsIdList.contains(nid)){
                Intersection_nid.add(nid);
            }
        }

        List<NewsContent> subkeywordNews = searchService.searchNewsContent(Intersection_nid);
        for(NewsContent newsurl : subkeywordNews){
            if(reporter.equals(newsurl.getReporter())) {
                newsList.add(newsurl);
            }
        }
        return new ResponseEntity<Object>(gson.toJson(newsList),HttpStatus.OK);
    }
}