package com.nulab.exam.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    @Autowired
    private WikiPageService wikiPageService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // Get access token
        String accessToken = userRequest.getAccessToken().getTokenValue();
        System.out.println(accessToken);

        List<Integer> wikiIdLIst = fetchWikiIdList(accessToken);
        wikiPageService.syncWikiPages(wikiIdLIst, accessToken);

        return new DefaultOAuth2User(
                Collections.singleton(() -> "ROLE_USER"),  // set permission
                Collections.singletonMap("name", "default"),  // set user info as default
                "name"
        );
    }

    private List<Integer> fetchWikiIdList(String accessToken) {

        // TODO: get properties from configuration file (application.properties?)
        String url = "https://nulab-exam.backlog.jp";
        String apiUrl = url + "/api/v2/wikis?projectIdOrKey=HAN";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, String.class);

        // JSON parsing
        ObjectMapper objectMapper = new ObjectMapper();
        List<Map<String, Object>> wikiList;
        List<Integer> idList = new ArrayList<>();
        try {
            wikiList = objectMapper.readValue(response.getBody(), new TypeReference<List<Map<String, Object>>>() {});
            for (Map<String, Object> wiki : wikiList) {
                Integer id = (Integer) wiki.get("id");
                idList.add(id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return idList;
    }
}
