package com.nulab.exam.repository.impl;

import com.nulab.exam.entity.WikiPage;
import com.nulab.exam.repository.WikiPageRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class WikiPageRepositoryImplTest {

    @Autowired
    private WikiPageRepository wikiPageRepository;
    @Autowired
    private WikiPageRepositoryImpl wikiPageRepositoryImpl;

    @Transactional
    @Test
    public void testExactMatch() {
        // exact text condition case
        String query = "\"exact text condition case\"";

        WikiPage record = new WikiPage();
        record.setName("Test");
        record.setContent("sample content : this content for exact text condition case test.");
        record.setProjectId(1);
        record.setCreatedUser(1);
        record.setUpdatedUser(1);
        wikiPageRepository.save(record);

        List<WikiPage> results = wikiPageRepositoryImpl.searchByDynamicCriteria(query, "content", new ArrayList<>());
        assertTrue(results.size() == 1);
        assertTrue(results.get(0).getContent().contains("exact text condition case"));
    }

    @Transactional
    @Test
    public void testOrCondition() {
        // OR condition case
        String query = "kiwi OR strawberry";

        WikiPage record1 = new WikiPage();
        record1.setName("Test1");
        record1.setContent("sample content1 : this content for OR condition case test. this content has the word kiwi.");
        record1.setProjectId(1);
        record1.setCreatedUser(1);
        record1.setUpdatedUser(1);

        WikiPage record2 = new WikiPage();
        record2.setName("Test2");
        record2.setContent("sample content2 : this content for OR condition case test. this content has the word strawberry.");
        record2.setProjectId(1);
        record2.setCreatedUser(1);
        record2.setUpdatedUser(1);

        wikiPageRepository.save(record1);
        wikiPageRepository.save(record2);

        List<WikiPage> results = wikiPageRepositoryImpl.searchByDynamicCriteria(query, "content", new ArrayList<>());
        assertTrue(results.size() == 2);
        for (WikiPage page : results) {
            assertTrue(
                    page.getContent().contains("kiwi") || page.getContent().contains("strawberry"),
                    "Each result should contain either 'kiwi' or 'strawberry'."
            );
        }
    }

    @Transactional
    @Test
    public void testExcludeCondition() {
        // exclude condition case
        String query = "findIt -exceptThis";

        WikiPage record1 = new WikiPage();
        record1.setName("Test");
        record1.setContent("sample content : this content for exclude condition case test. this content has the word findIt.");
        record1.setProjectId(1);
        record1.setCreatedUser(1);
        record1.setUpdatedUser(1);
        wikiPageRepository.save(record1);

        List<WikiPage> results1 = wikiPageRepositoryImpl.searchByDynamicCriteria(query, "content", new ArrayList<>());
        assertTrue(results1.get(0).getContent().contains("findIt") && !results1.get(0).getContent().contains("exceptThis"));
        wikiPageRepository.delete(record1);

        WikiPage record2 = new WikiPage();
        record2.setName("Test");
        record2.setContent("sample content : this content for exclude condition case test. this content has the word findIt. exceptThis");
        record2.setProjectId(2);
        record2.setCreatedUser(2);
        record2.setUpdatedUser(2);
        wikiPageRepository.save(record2);
        wikiPageRepository.delete(record2);

        List<WikiPage> results2 = wikiPageRepositoryImpl.searchByDynamicCriteria(query, "content", new ArrayList<>());
        assertTrue(results2.isEmpty());
        wikiPageRepository.delete(record2);

    }

    @Transactional
    @Test
    public void testComplexCondition() {
        // TODO: 
    }

}