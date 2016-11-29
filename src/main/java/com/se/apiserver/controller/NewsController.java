package com.se.apiserver.controller;

import com.google.gson.Gson;
import com.se.apiserver.entity.GroupedNewsWordRelation;
import com.se.apiserver.entity.GroupedWord;
import com.se.apiserver.entity.NewsContent;
import com.se.apiserver.repository.IndexedWordRepository;
import com.se.apiserver.service.NewsService;
import com.se.apiserver.service.SearchService;
import com.se.apiserver.viewcomponent.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    Gson gson; //json을 더 좋게 사용하기 위한 google의 gson

//    @RequestMapping(method = RequestMethod.GET, value="/search",produces = MediaType.APPLICATION_JSON_VALUE)
//    //로컬의 서치루틴에서 해당 작업
//    //@ResponseThirdPage.htmlBody 리턴 타입이 HTTP(Json)의 응답 메시지로 전송
//    public @ResponseBody ResponseEntity<Object> findNewsList() throws Exception{ //nid가 10, 12인걸 뽑아내는곳
//        ArrayList<Integer> idList = new ArrayList<Integer>();
//        idList.add(10);
//        idList.add(12);
//        idList.add(18);
//        idList.add(20);
//        idList.add(22);
//        idList.add(24);
//        idList.add(26);
//        return new ResponseEntity<Object>(gson.toJson(newsService.findNewsById(idList)),HttpStatus.OK);
//    }
    @RequestMapping(method = RequestMethod.GET, value="/search/news/{keyword}",produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<Object> findNewsList(@PathVariable("keyword")String keyword) throws Exception{
        List<Integer> newsIdList = searchService.searchNewsContentIdList(keyword);
        List<NewsContent> newsContentList = searchService.searchNewsContent(newsIdList);
        return new ResponseEntity<Object>(gson.toJson(newsContentList),HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value="/search/keyword/{keyword}",produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<Object> findImportantWord(@PathVariable("keyword")String keyword) throws Exception{
        List<Integer> newsIdList = searchService.searchNewsContentIdList(keyword);
        List<GroupedWord> groupedWordList = searchService.getGroupedWordList(newsIdList).subList(0,15);
        return new ResponseEntity<Object>(searchService.groupedWordListToCSV(groupedWordList),HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value="/search/test",produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<Object> treeTest() throws Exception{
        Node node = new Node();
        node.name= "이형래";
        Node node1 = new Node();
        node1.name="민재현";
        Node node2 = new Node();
        node2.name="이용환";
        Node node3 = new Node();
        node3.name = "오종훈";
        node.children.add(node1);
        node.children.add(node2);
        node1.children.add(node3);
        return new ResponseEntity<Object>(gson.toJson(node),HttpStatus.OK);
    }

//    /**
//     * keyword를 입력받아 반환값을 CSV형태로 바꿔주어 뿌려주는 함수
//     * @param keyword
//     * @return
//     * @throws Exception
//     */
//    @RequestMapping(method = RequestMethod.GET, value="/search/keyword/{keyword}",produces = MediaType.APPLICATION_JSON_VALUE)
//    public @ResponseBody ResponseEntity<Object> findImportantWord(@PathVariable("keyword")String keyword) throws Exception{
//        List<Integer> newsIdList = searchService.searchNewsContentIdList(keyword);
//        List<GroupedWord> groupedWordList = searchService.getGroupedWordList(newsIdList);
//        return new ResponseEntity<Object>(gson.toJson(groupedWordList),HttpStatus.OK);
//    }
//
//    /**
//     * 기자를 전달받아 쿼리문 전달받아서 뿌려준다
//     * @param keyword
//     * @return
//     * @throws Exception
//     */
//    @RequestMapping(method = RequestMethod.GET, value="/search/keyword/{keyword}",produces = MediaType.APPLICATION_JSON_VALUE)
//    public @ResponseBody ResponseEntity<Object> findImportantWord(@PathVariable("keyword")String keyword) throws Exception{
//        List<Integer> newsIdList = searchService.searchNewsContentIdList(keyword);
//        List<GroupedWord> groupedWordList = searchService.getGroupedWordList(newsIdList);
//        return new ResponseEntity<Object>(gson.toJson(groupedWordList),HttpStatus.OK);
//    }
//
//    /**
//     * 차트만들기 - date 별로 해당 단어의 빈도를 파악한다.(쉬울듯 - 호찬이가)
//     * @param keyword
//     * @return
//     * @throws Exception
//     */
//    @RequestMapping(method = RequestMethod.GET, value="/search/keyword/{keyword}",produces = MediaType.APPLICATION_JSON_VALUE)
//    public @ResponseBody ResponseEntity<Object> findImportantWord(@PathVariable("keyword")String keyword) throws Exception{
//        List<Integer> newsIdList = searchService.searchNewsContentIdList(keyword);
//        List<GroupedWord> groupedWordList = searchService.getGroupedWordList(newsIdList);
//        return new ResponseEntity<Object>(gson.toJson(groupedWordList),HttpStatus.OK);
//    }
}