package com.nulab.exam.repository.impl;

import com.nulab.exam.entity.WikiPage;
import com.nulab.exam.repository.WikiPageRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Repository
public class WikiPageRepositoryImpl implements WikiPageRepositoryCustom {

    private final EntityManager entityManager;

    public WikiPageRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<WikiPage> searchByDynamicCriteria(String query, String field, List<String> keywordList) {

        assert query != null && !"".equals(query);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<WikiPage> criteriaQuery = cb.createQuery(WikiPage.class);
        Root<WikiPage> root = criteriaQuery.from(WikiPage.class);

        List<Predicate> orPredicates = new ArrayList<>();

        // Since the "OR" condition is at the highest level of the query, each clause should first be separated based on "OR".
        String[] orSections = query.split("(?i)\\s+OR\\s+");

        for (String section : orSections) {
            List<Predicate> andPredicates = new ArrayList<>();

            // Extract conditions(exact text, exclude) from each sentence using regular expressions.
            Pattern pattern = Pattern.compile("\"([^\"]*)\"|-?\\S+");
            Matcher matcher = pattern.matcher(section);

            List<String> includeTerms = new ArrayList<>();
            List<String> exactPhrases = new ArrayList<>();
            List<String> excludeTerms = new ArrayList<>();

            while (matcher.find()) {
                String term = matcher.group();
                if (term.startsWith("-")) {
                    excludeTerms.add(term.substring(1));
                } else if (term.startsWith("\"") && term.endsWith("\"")) {
                    exactPhrases.add(term.substring(1, term.length() - 1));
                } else {
                    includeTerms.add(term); // default condition(AND)
                }
            }

            // 'AND' condition
            for (String term : includeTerms) {
                Predicate includePredicate = cb.like(cb.lower(root.get(field)), "%" + term.toLowerCase() + "%");
                andPredicates.add(includePredicate);
                keywordList.add(term);
            }

            // Exact match phrases.
            for (String phrase : exactPhrases) {
                Predicate exactMatchPredicate = cb.like(cb.lower(root.get(field)), "%" + phrase.toLowerCase() + "%");
                andPredicates.add(exactMatchPredicate);
                keywordList.add(phrase);
            }

            // Exclude condition
            for (String term : excludeTerms) {
                Predicate excludePredicate = cb.notLike(cb.lower(root.get(field)), "%" + term.toLowerCase() + "%");
                andPredicates.add(excludePredicate);
            }

            // For each clause, create conditions combined with "AND" and then combine them with "OR"
            orPredicates.add(cb.and(andPredicates.toArray(new Predicate[0])));
        }

        criteriaQuery.where(cb.or(orPredicates.toArray(new Predicate[0])));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }


}