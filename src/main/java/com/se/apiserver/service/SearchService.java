package com.se.apiserver.service;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.se.apiserver.entity.*;
import com.se.apiserver.repository.IndexedWordRepository;
import com.se.apiserver.repository.NewsContentRepository;
import com.se.apiserver.repository.NewsWordRelationRepository;
import com.twitter.penguin.korean.KoreanPosJava;
import com.twitter.penguin.korean.KoreanTokenJava;
import com.twitter.penguin.korean.TwitterKoreanProcessorJava;
import com.twitter.penguin.korean.tokenizer.KoreanTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scala.collection.Seq;
import scala.collection.mutable.StringBuilder;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Created by LeeHyungRae on 2016. 11. 25..
 */
@Service
public class SearchService {
    //@Autowired를 이용 상위 패키지가 같으면 get,set 없어도 사용 가능하도록 묶어줌
    @Autowired
    IndexedWordRepository indexedWordRepository;

    @Autowired
    NewsWordRelationRepository newsWordRelationRepository;

    @Autowired
    NewsContentRepository newsContentRepository;

    @Autowired
    NewsService newsService;
    // 메인 페이지의 버블 키워드를 구성하기 위해 그룹된 단어를 CSV로 바꾼 String으로 구성
    public String groupedWordBubbleToCSV(List<GroupedWord> groupedWordList){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("id,value\n");
        for(GroupedWord word : groupedWordList){
            stringBuilder.append(word.getWid()%100+"."+word.getWord()+","+word.getTfidf()+"\n");
        }
        return stringBuilder.toString();
    }
    // Third 페이지의 워드 클라우드를 구성하기 위해 그룹된 단어를 CSV로 바꾼 String으로 구성 (위의 함수와는 필요한 원소가 달라서 다른 함수로 구현)
    public String groupedWordCloudToCSV(List<GroupedWord> groupedWordList){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("password,category\n");
        for(GroupedWord word : groupedWordList){
            stringBuilder.append(word.getWord()+","+word.getTfidf()+"\n");
        }
        return stringBuilder.toString();
    }
    //NewsWordRelation 엔티티와 IndexedWord를 이용하여 그룹된 문서들에서 전체 단어의 TF-IDF값을 구함 - TF-IDF크기 순으로 나열
    public List<GroupedWord> getGroupedWordList(List<Integer> newsIdList){
        List<NewsWordRelation> newsWordRelationList = getNewsWordRelationByNewsIdList(newsIdList);
        List<IndexedWord> indexedWordList = getIndexedWordListByNewsWordRelation(newsWordRelationList);
        Map<Integer,Double> idfMap = getIdfMap(indexedWordList);


        for(NewsWordRelation newsWordRelation : newsWordRelationList){
            double tf = newsWordRelation.getTf();
            double idf = Math.log10(idfMap.get(newsWordRelation.getWid()));
            double tfidf = tf*idf;
            newsWordRelation.setTfIdf(tfidf);
        }

        Collections.sort(newsWordRelationList, new Comparator<NewsWordRelation>() {
            @Override
            public int compare(NewsWordRelation o1, NewsWordRelation o2) {
                if(o1.getTfidf()<o2.getTfidf())
                    return 1;
                else
                    return -1;
            }
        });


        Map<Integer,GroupedWord> groupedWordMap = Maps.newHashMap();

        for(NewsWordRelation newsWordRelation : newsWordRelationList){
            if(groupedWordMap.containsKey(newsWordRelation.getWid())){
                GroupedWord groupedWord = groupedWordMap.get(newsWordRelation.getWid());
                groupedWord.setTfidf(groupedWord.getTfidf()+newsWordRelation.getTfidf());
            }
            else{
                GroupedWord groupedWord = new GroupedWord();
                groupedWord.setWid(newsWordRelation.getWid());
                groupedWord.setTfidf(newsWordRelation.getTfidf());
                groupedWordMap.put(groupedWord.getWid(),groupedWord);
            }
        }

        Map<Integer,String> indexedWordMap = Maps.newHashMap();

        for(IndexedWord indexedWord : indexedWordList){
            if(!indexedWordMap.containsKey(indexedWord.getWid()))
                indexedWordMap.put(indexedWord.getWid(),indexedWord.getWord());
        }

        List<GroupedWord> groupedWordList = Lists.newArrayList(groupedWordMap.values());

        for(GroupedWord groupedWord : groupedWordList){
            groupedWord.setWord(indexedWordMap.get(groupedWord.getWid()));
        }

        Collections.sort(groupedWordList, new Comparator<GroupedWord>() {
            @Override
            public int compare(GroupedWord o1, GroupedWord o2) {
                if(o1.getTfidf()>o2.getTfidf())
                    return -1;
                else if(o1.getTfidf()==o2.getTfidf())
                    return 0;
                else
                    return 1;
            }
        });
        return groupedWordList;
    }
    //List의 정수값에 대응하는 Nid를 가진 뉴스를 가져온다
    public List<NewsContent> searchNewsContent(List<Integer> newsContentIdList){
        return newsContentRepository.findAll(newsContentIdList);
    }
    //검색어를 형태소 분석하여 전체 TF-IDF값을 구하고 그에 따라 정렬 시킨다
    public List<Integer> searchNewsContentIdList(String keyword){
        //검색어를 Stemming
        List<String> wordList = stemmingKeyword(keyword);
        //Stemming된 단어들을 데이터베이스에서 조회
        List<IndexedWord> indexedWordList = getindexedWordByWordList(wordList);
        //IDF 계산
        Map<Integer,Double> idfMap = getIdfMap(indexedWordList);
        //Stemming된 단어 리스트들을 포함하고 있는 뉴스 기사 - 단어 Row들을 조회
        List<NewsWordRelation> newsWordRelationList = getNewsWordRelationListByIdxedWordList(indexedWordList);
        //TFIDF 계산
        List<NewsWordRelation> tfidfCalcedNewsWordRelationList = calcTfIdf(newsWordRelationList,idfMap);
        //문서 번호를 기준으로 뉴스-단어를 조합
        List<GroupedNewsWordRelation> groupedNewsWordRelations = groupingNewsWordRelation(tfidfCalcedNewsWordRelationList);

        //TFIDF에 따라 정렬
        Collections.sort(groupedNewsWordRelations, new Comparator<GroupedNewsWordRelation>() {
            @Override
            public int compare(GroupedNewsWordRelation o1, GroupedNewsWordRelation o2) {
                if(o1.calcTfIdfSum()>o2.calcTfIdfSum())
                    return 1;
                else if(o1.calcTfIdfSum()==o2.calcTfIdfSum())
                    return 0;
                else
                    return -1;
            }
        });

        return Lists.transform(groupedNewsWordRelations, new Function<GroupedNewsWordRelation, Integer>() {
            @Override
            public Integer apply(GroupedNewsWordRelation input) {
                return input.getNid();
            }
        });
    }
    //NewsWordRelation을 받아 Map을 사용하여 매칭시켜 GroupedNewsWordRelation을 반환한다
    public List<GroupedNewsWordRelation> groupingNewsWordRelation(List<NewsWordRelation> newsWordRelationList){
        Map<Integer,GroupedNewsWordRelation> groupedNewsWordRelationMap = Maps.newHashMap();

        for(NewsWordRelation newsWordRelation : newsWordRelationList){
            if(groupedNewsWordRelationMap.containsKey(newsWordRelation.getNid())) {
                GroupedNewsWordRelation grpedNewsWordRelation = groupedNewsWordRelationMap.get(newsWordRelation.getNid());
                grpedNewsWordRelation.addNewsWordRelation(newsWordRelation);
            }
            else{
                GroupedNewsWordRelation rel = new GroupedNewsWordRelation();
                rel.setNid(newsWordRelation.getNid());
                rel.addNewsWordRelation(newsWordRelation);
                groupedNewsWordRelationMap.put(newsWordRelation.getNid(),rel);
            }
        }
        return Lists.newArrayList(groupedNewsWordRelationMap.values());
    }
    //해당 단어가 포함된 뉴스와 전체뉴스와 검색된 해당 뉴스의 전체 단어수와 해당 기사의 해당 단어의 단어수를 이용하여 TF_IDF를 구한다
    private List<NewsWordRelation> calcTfIdf(List<NewsWordRelation> newsWordRelationList,Map<Integer,Double> idfMap){
        List<NewsWordRelation> calcedNewsWordRelation = Lists.newArrayList();

        for(NewsWordRelation newsWordRelation : newsWordRelationList){
            double tf = newsWordRelation.getTf();
            double idf = Math.log10(idfMap.get(newsWordRelation.getWid()));
            double tfidf = tf*idf;
            newsWordRelation.setTfIdf(tfidf);
            calcedNewsWordRelation.add(newsWordRelation);
        }
        return calcedNewsWordRelation;
    }
    //뽑혀나온 기사들에서 다시한번 뉴스와 단어의 관계를 찾는다
    private List<NewsWordRelation> getNewsWordRelationByNewsIdList(List<Integer> newsIdList){
        return newsWordRelationRepository.getNewsWordRelationListByNewsIdList(newsIdList);
    }
    //뉴스와 단어의 관계를 이용하여 IndexedWord를 구한다 - 이를통해 단어를 포함하는 기사를 찾을 수 있다
    private List<IndexedWord> getIndexedWordListByNewsWordRelation(List<NewsWordRelation> newsWordRelationList){
        List<Integer> wordIdList = Lists.transform(newsWordRelationList, new Function<NewsWordRelation, Integer>() {
            @Override
            public Integer apply(NewsWordRelation input) {
                return input.getWid();
            }
        });
        return indexedWordRepository.findAll(wordIdList);
    }

    /**
     * 데이터베이스에서 검색한 단어에 대한 정보를 가져온다
     * @param wordList 사용자가 검색한 단어를 Stemming 한 List
     * @return 데이터베이스에 저장된 단어 리스트
     */
    private List<IndexedWord> getindexedWordByWordList(List<String> wordList){
        List<IndexedWord> indexedWordList = indexedWordRepository.getAllNewsContentByWord(wordList);
        return indexedWordList.size()==0?Lists.<IndexedWord>newArrayList():indexedWordList;
    }

    /**
     * 주어진 문서들에 대한 IDF Map 을 생성한다.
     * @param indexedWordList IDF값을 생성할 단어 리스트
     * @return 단어에 대한 IDF Map
     */
    public Map<Integer,Double> getIdfMap(List<IndexedWord> indexedWordList){
        double allDocCnt = newsContentRepository.count();

        Map<Integer,Double> wordIdfMap = Maps.newHashMap();

        for(IndexedWord indexedWord : indexedWordList)
            wordIdfMap.put(indexedWord.getWid(),(double)(allDocCnt/(double)indexedWord.getAllcount()));
        return wordIdfMap;
    }

    /**
     * 단어 Index List를 이용하여 News-Word Relation 값을 가져온다
     * @param idxedWordList 인덱싱 된 단어 리스트
     * @return 인덱싱 된 단어가 포함된 뉴스 리스트
     */
    private List<NewsWordRelation> getNewsWordRelationListByIdxedWordList(List<IndexedWord> idxedWordList){
        List<Integer> idxWordList = Lists.transform(idxedWordList, new Function<IndexedWord, Integer>() {
            @Override
            public Integer apply(IndexedWord input) {
                return input.getWid();
            }
        });
        return newsWordRelationRepository.getNewsWordRelationListByWordIdxList(idxWordList);
    }


    /**
     * 검색할 단어를 Stemming한다.
     * @param keyword 검색할 단어
     * @return Stemming된 단어 리스트
     */
    public List<String> stemmingKeyword(String keyword){
        //단어를 Stemming한 결과를 담을 Arraylist를 생성한다.
        List<String> wordList = Lists.newArrayList();

        //Twitter 형태소 분석기를 이용하여 Stemming을 진행한다.
        CharSequence normalized = TwitterKoreanProcessorJava.normalize(keyword);
        Seq<KoreanTokenizer.KoreanToken> tokens = TwitterKoreanProcessorJava.tokenize(normalized);
        Seq<KoreanTokenizer.KoreanToken> stemmed = TwitterKoreanProcessorJava.stem(tokens);
        List<KoreanTokenJava> list = TwitterKoreanProcessorJava.tokensToJavaKoreanTokenList(stemmed);

        //Stemming된 List에서 고유명사를 분리해낸다.(추후 변경 -> 동사나 이런것도 처리가 가능하도록)
        for (KoreanTokenJava koreanTokenJava : list) {
            //고유명사와 명사만을 걸러낸다.
            if ((koreanTokenJava.getPos().equals(KoreanPosJava.ProperNoun) || koreanTokenJava.getPos().equals(KoreanPosJava.Noun)) && koreanTokenJava.getLength() > 1){
                String word = koreanTokenJava.getText();
                wordList.add(word);
            }
        }

        //중복된 단어들은 제외한 리스트를 생성하여 리턴한다.
        return ImmutableSet.copyOf(wordList).asList();
    }
}
