package com.nulab.exam.repository;

import com.nulab.exam.entity.WikiPage;
import com.nulab.exam.repository.projection.WikiPageProjection;

import java.util.List;

public interface WikiPageRepositoryCustom {
    List<WikiPage> searchByDynamicCriteria(String query, String field, List<String> keywordList);
}
