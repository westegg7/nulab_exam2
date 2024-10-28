package com.nulab.exam.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nulab.exam.dto.SearchResultDTO;
import com.nulab.exam.dto.WikiPageDTO;
import com.nulab.exam.entity.WikiPage;
import com.nulab.exam.repository.WikiPageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class WikiPageService {

    public static final int SNIPPET_LENGTH = 140;

    @Autowired
    private WikiPageRepository wikiPageRepository;

    public List<WikiPage> getAllWikiPages() {
        return wikiPageRepository.findAll();
    }

    public WikiPage getWikiPageById(Integer id) {
        return wikiPageRepository.findById(id).orElse(null);
    }

    public WikiPageDTO getWikiPageDTOById(Integer id) {
        return wikiPageRepository.findWikiPageDTOById(id);
    }

    public WikiPage saveWikiPage(WikiPage wikiPage) {
        return wikiPageRepository.save(wikiPage);
    }

    public void deleteWikiPage(Integer id) {
        wikiPageRepository.deleteById(id);
    }

    // Sync fetchedWikiIds with dbWikiIds
    public void syncWikiPages(List<Integer> fetchedWikiIds, String accessToken) {

        // get list of Ids from database
        List<Integer> dbWikiIds = wikiPageRepository.findAllWikiIds();
        
        List<Integer> idsToAdd = fetchedWikiIds.stream()
                .filter(id -> !dbWikiIds.contains(id))
                .collect(Collectors.toList());

        for (Integer wikiId : idsToAdd) {
            WikiPage newPage = fetchAndSaveWikiPage(wikiId, accessToken);
            wikiPageRepository.save(newPage);
        }

        // Delete the data that exists only in dbWikiIds
        List<Integer> idsToDelete = dbWikiIds.stream()
                .filter(id -> !fetchedWikiIds.contains(id))
                .collect(Collectors.toList());

        wikiPageRepository.deleteAllById(idsToDelete);
    }

    public WikiPage fetchAndSaveWikiPage(Integer wikiId, String accessToken) {

        String url = "https://nulab-exam.backlog.jp";
        String apiUrl = url + "/api/v2/wikis/" + wikiId;
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> wiki = new HashMap<>();
        try {
            wiki = objectMapper.readValue(response.getBody(), new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            e.printStackTrace();
        }

        WikiPage wikiPage = new WikiPage();
        wikiPage.setWikiId(wikiId);
        wikiPage.setName((String) wiki.get("name"));
        wikiPage.setProjectId((Integer) wiki.get("projectId"));
        wikiPage.setContent((String) wiki.get("content"));

        Map<String, Object> createdUserMap = (Map<String, Object>) wiki.get("createdUser");
        wikiPage.setCreatedUser((Integer) createdUserMap.get("id"));
        Map<String, Object> updatedUserMap = (Map<String, Object>) wiki.get("updatedUser");
        wikiPage.setUpdatedUser((Integer) updatedUserMap.get("id"));

        return wikiPage;
    }

    public List<SearchResultDTO> getSearchResult(String keyword, boolean isTitleOnly) {

        assert keyword != null && !"".equals(keyword);

        List<WikiPage> wikiPages;
        List<String> keywordList = new ArrayList<>();

        // For the Title Only option, it finds matches only in the title, and the snippet consists of the first maxLength characters from the content.
        if (isTitleOnly) {
            wikiPages = wikiPageRepository.searchByDynamicCriteria(keyword, "name", keywordList);
        } else {
            wikiPages = wikiPageRepository.searchByDynamicCriteria(keyword, "content", keywordList);
        }

        return wikiPages.stream()
                .map(p -> new SearchResultDTO(keywordList, p.getWikiId().longValue(), p.getName(), getSnippet(p.getContent(), keywordList, SNIPPET_LENGTH, isTitleOnly)))
                .collect(Collectors.toList());
    }

    private List<String> getSnippet(String content, List<String> keywordList, int maxLength, boolean isTitleOnly) {

        assert content != null && keywordList != null && !keywordList.isEmpty();

        List<String> snippets = new ArrayList<>();

        if (isTitleOnly) {
            snippets.add(content.substring(0, Math.min(maxLength, content.length())) + "...");
            return snippets;
        }

        String[] lines = content.split("\\r?\\n");

        for (String keyword : keywordList) {
            Pattern pattern = Pattern.compile(Pattern.quote(keyword), Pattern.CASE_INSENSITIVE);

            for (String line : lines) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    String snippet = line;
                    if (line.length() > maxLength) {
                        int keywordIndex = matcher.start();
                        int start;
                        int end;

                        // 키워드가 문자열의 시작부터 maxLength 범위 내에 있을 경우
                        if (keywordIndex + keyword.length() <= maxLength) {
                            start = 0;
                            end = maxLength;
                        }
                        // 키워드가 문자열의 끝에서 maxLength 범위 내에 있을 경우
                        else if (keywordIndex + keyword.length() >= line.length() - maxLength) {
                            start = line.length() - maxLength;
                            end = line.length();
                        }
                        // 키워드가 가운데 있을 경우
                        else {
                            start = keywordIndex - (maxLength - keyword.length()) / 2;
                            end = start + maxLength;
                        }

                        snippet = line.substring(start, end).trim();

                        // 잘린 부분에 '...' 추가
                        if (start > 0) {
                            snippet = "..." + snippet;
                        }
                        if (end < line.length()) {
                            snippet = snippet + "...";
                        }
                    }

                    // 중복되지 않도록 스니펫을 추가
                    if (!snippets.contains(snippet)) {
                        snippets.add(snippet);
                    }
                }
            }
        }

        return snippets;
    }
}
