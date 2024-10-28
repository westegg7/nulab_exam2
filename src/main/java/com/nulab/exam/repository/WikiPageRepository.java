package com.nulab.exam.repository;

import com.nulab.exam.dto.WikiPageDTO;
import com.nulab.exam.entity.WikiPage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WikiPageRepository extends JpaRepository<WikiPage, Integer>, WikiPageRepositoryCustom {

    // get id list from wiki_page table
    @Query("SELECT w.wikiId FROM WikiPage w")
    List<Integer> findAllWikiIds();

    // general search in name
    List<WikiPage> findByNameContaining(String keyword);

    @Query("SELECT new com.nulab.exam.dto.WikiPageDTO(w.id, w.name, w.content) FROM WikiPage w WHERE w.wikiId = :id")
    WikiPageDTO findWikiPageDTOById(@Param("id") Integer id);

}
