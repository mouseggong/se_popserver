package com.se.apiserver.service;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.twitter.penguin.korean.KoreanPosJava;
import com.twitter.penguin.korean.KoreanTokenJava;
import com.twitter.penguin.korean.TwitterKoreanProcessorJava;
import com.twitter.penguin.korean.tokenizer.KoreanTokenizer;
import org.springframework.stereotype.Service;
import scala.collection.Seq;

import java.util.List;

/**
 * Created by LeeHyungRae on 2016. 11. 29..
 */
@Service
public class SearchService {
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
