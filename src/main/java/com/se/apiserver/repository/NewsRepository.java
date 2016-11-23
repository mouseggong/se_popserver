package com.se.apiserver.repository;

import com.se.apiserver.entity.NewsContent;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by LeeHyungRae on 2016. 11. 22..
 */
public interface NewsRepository extends CrudRepository<NewsContent,Integer>{ //호찬이 한테 물어보기

}
