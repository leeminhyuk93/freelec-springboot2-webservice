package com.jojoldu.book.springboot.web;


import com.jojoldu.book.springboot.domain.posts.Posts;
import com.jojoldu.book.springboot.domain.posts.PostsRepository;
import com.jojoldu.book.springboot.web.dto.PostsSaveRequestDto;
import com.jojoldu.book.springboot.web.dto.PostsUpdateReqeustDto;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PostsApiControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private PostsRepository postsRepository;

    @After
    public void tearDown() throws Exception {
        postsRepository.deleteAll();
    }

    @Test
    public void Posts_등록된다() throws Exception {
        //given
        String title = "title";
        String content = "content";
        PostsSaveRequestDto requestDto = PostsSaveRequestDto.builder()
                .title(title)
                .content(content)
                .author("author")
                .build();

        String url = "http://localhost:" + port + "/api/v1/posts";

        //when
        ResponseEntity<Long> responseEntity = restTemplate.postForEntity(url, requestDto, Long.class);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isGreaterThan(0L);

        List<Posts> all = postsRepository.findAll();
        assertThat(all.get(0).getTitle()).isEqualTo(title);
        assertThat(all.get(0).getContent()).isEqualTo(content);

        /**
         *
         *
         * PostSaveRequestDto의 인스턴스를 생성하여 dto의 필드를 초기화한다.
         * url 문자열로 해당 api 요청 url을 저장한다.
         * TestRestTemplate의 인스턴스를 생성한다. 해당 클래스는 url 테스트를 실행할 수 있다.
         *
         * restTemplate.postForEntity(url, requestDto, Long.class);
         * 요청 url, 요청 데이터를 받을 dto, 요청 body(결과)의 반환값 타입을 설정하면 ResponseEntity 객체를 반환한다.
         */
    }

    @Test
    public void Posts_수정된다() throws Exception {
        //given
        Posts savedPosts = postsRepository.save(Posts.builder()
                .title("title")
                .content("content")
                .author("author")
                .build());

        Long updateId = savedPosts.getId();
        String expectedTitle = "title2";
        String expectedContent = "content2";

        PostsUpdateReqeustDto requestDto = PostsUpdateReqeustDto.builder()
                .title(expectedTitle)
                .content(expectedContent)
                .build();

        String url = "http://localhost:" + port + "/api/v1/posts/" + updateId;

        HttpEntity<PostsUpdateReqeustDto> requestEntity = new HttpEntity<>(requestDto);

        /**
         * given
         * postRepository.save 로 데이터베이스에 저장할 때 엔티티를 반환하게 된다.
         * savedPosts에는 방금 저장한 객체(Entity)가 저장된다.
         *
         * updateId에는 저장된 데이터의 자동으로 생성된 Primary Key(PK) 숫자를 저장하게 된다.
         * expectedTitle, expectedContent는 새로 저장될 내용들이다.
         *
         * 업데이트를 요청할 때 전달할 데이터를 PostsUpdateRequestDto 객체로 생성한다.
         * HttpEntity<T> 타입은 Http 요청을 할 때 전달할 데이터를 담는 객체이다.
         * requestEntity 몸체에 requestDto를 전달하고 있다. 이는 Controller의 파라미터 @RequestBody로 전달될 수 있다.
         */

        //when
        ResponseEntity<Long> responseEntity = restTemplate.exchange(url, HttpMethod.PUT, requestEntity, Long.class);

        /**
         * when
         * ResponseEntity<T> 타입은 요청한 결과 값을 HttpHeader와 함께 전달 받을 수 있는 객체이다.
         * responseEntity에 restTemplate.exchange 함수로 url 요청을 한다. 요청에 전달될 데이터로 requestEntity를 전달한다.
         * url : 요청 url
         * HttpMethod : 메소드 전달 방식(Get, Post, Put...)
         * requestEntity : @RequestBody로 전달될 객체
         * Long.class : 반환 타입
         */

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isGreaterThan(0L);

        List<Posts> all = postsRepository.findAll();
        assertThat(all.get(0).getTitle()).isEqualTo(expectedTitle);
        assertThat(all.get(0).getContent()).isEqualTo(expectedContent);
    }
}
