package com.nulab.exam.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "wiki_page")
@Getter @Setter
public class WikiPage {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer wikiId;

    @Column(name = "project_id", nullable = false)
    private Integer projectId;

    @Column(name = "name")
    private String name;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "updated_date", nullable = false)
    private LocalDateTime updatedDate;

    @Column(name = "created_user", nullable = false)
    private Integer createdUser;

    @Column(name = "updated_user", nullable = false)
    private Integer updatedUser;

    public WikiPage(Integer wikiId, Integer projectId, String name, String content, LocalDateTime createdDate, LocalDateTime updatedDate, Integer createdUser, Integer updatedUser) {
        this.wikiId = wikiId;
        this.projectId = projectId;
        this.name = name;
        this.content = content;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        this.createdUser = createdUser;
        this.updatedUser = updatedUser;
    }

    public WikiPage() {

    }

    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
        updatedDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedDate = LocalDateTime.now();
    }

    public static WikiPage sample() {
        return new WikiPage(1, 1, "sample", "contentA", LocalDateTime.now(), LocalDateTime.now(), 2, 2);
    }

}
