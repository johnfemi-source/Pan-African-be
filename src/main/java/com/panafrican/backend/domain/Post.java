package com.panafrican.backend.domain;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "posts")
public class Post {

    @Id
    @Column(nullable = false, unique = true, length = 36)
    private String id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 10000)
    private String body;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PostCategory category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private Instant createdAt;

    protected Post() {
    }

    public Post(String id, String title, String body, PostCategory category, Country country, String author, Instant createdAt) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.category = category;
        this.country = country;
        this.author = author;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public PostCategory getCategory() {
        return category;
    }

    public Country getCountry() {
        return country;
    }

    public String getAuthor() {
        return author;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
