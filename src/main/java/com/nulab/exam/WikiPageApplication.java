package com.nulab.exam;

import com.nulab.exam.entity.WikiPage;
import com.nulab.exam.repository.WikiPageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WikiPageApplication {

	@Autowired
	private WikiPageRepository wikiPageRepository;

	public static void main(String[] args) {
		SpringApplication.run(WikiPageApplication.class, args);
	}

//	// 클래스에 implements CommandLineRunner 추가 필요.
//	@Override
//	public void run(String... args) throws Exception {
//		// 데이터베이스에 간단한 데이터 추가
//		WikiPage newPage = new WikiPage();
//		newPage.setProjectId(1);
//		newPage.setName("Spring Boot JPA Test");
//		newPage.setContent("This is a test wiki page.");
//		newPage.setCreateUser(1);
//		newPage.setUpdateUser(1);
//
//		wikiPageRepository.save(newPage);
//
//		System.out.println("New WikiPage saved with ID: " + newPage.getWikiId());
//	}
}