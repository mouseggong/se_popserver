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
 * Created by LeeHyungRae on 2016. 11. 29..
 */
@Service
public class SearchService {

    @Autowired
    IndexedWordRepository indexedWordRepository;

    @Autowired
    NewsWordRelationRepository newsWordRelationRepository;

    @Autowired
    NewsContentRepository newsContentRepository;

    @Autowired
    NewsService newsService;

    public String groupedWordListToCSV(List<GroupedWord> groupedWordList){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("id,value\n");
        for(GroupedWord word : groupedWordList){
            stringBuilder.append(word.getWid()%100+"."+word.getWord()+","+word.getTfidf()+"\n");
        }
        return stringBuilder.toString();
    }

    public List<GroupedWord> getGroupedWordList(List<Integer> newsIdList){
        long startTime = System.currentTimeMillis();
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

    public List<NewsContent> searchNewsContent(List<Integer> newsContentIdList){
        return newsContentRepository.findAll(newsContentIdList);
    }

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

    private List<NewsWordRelation> getNewsWordRelationByNewsIdList(List<Integer> newsIdList){
        return newsWordRelationRepository.getNewsWordRelationListByNewsIdList(newsIdList);
    }

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
