package com.nulab.exam.controller;

import com.nulab.exam.dto.SearchResultDTO;
import com.nulab.exam.dto.WikiPageDTO;
import com.nulab.exam.entity.WikiPage;
import com.nulab.exam.service.WikiPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@CrossOrigin(origins = "http://localhost:5173")
public class WikiPageController {

    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    @Autowired
    private WikiPageService wikiPageService;

    @GetMapping("/")
    public String home(Model model, @RegisteredOAuth2AuthorizedClient("nulab") OAuth2AuthorizedClient authorizedClient) {

        return "index";
    }

    @GetMapping("/api/search")
    @ResponseBody
    public List<SearchResultDTO> getSearchResult(
            @RequestParam String keyword,
            @RequestParam boolean isTitleOnly
    ) {

        return wikiPageService.getSearchResult(keyword, isTitleOnly);
    }

    @GetMapping("/api/wikis")
    @ResponseBody
    public WikiPageDTO getWikiResult(@RequestParam Integer wikiId) {
        return wikiPageService.getWikiPageDTOById(wikiId);
    }

    @GetMapping("/pages")
    public List<WikiPage> getAllPages() {
        return wikiPageService.getAllWikiPages();
    }

    @GetMapping("/pages/{id}")
    public WikiPage getPageById(@PathVariable Integer id) {
        return wikiPageService.getWikiPageById(id);
    }

    @PostMapping("/pages")
    public WikiPage createPage(@RequestBody WikiPage wikiPage) {
        return wikiPageService.saveWikiPage(wikiPage);
    }

}

