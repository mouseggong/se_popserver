package com.se.apiserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("classpath:/application.properties")
public class Application {
    // 스프링 부트 실행
    // 이 프로젝트는 전체 프로그램의 컨트롤러의 역할을 담당한다
    // DB에 접근하여 뷰가 요청하는 데이터를 JSON이나 CSV형식으로 포트에 뿌려준다
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}