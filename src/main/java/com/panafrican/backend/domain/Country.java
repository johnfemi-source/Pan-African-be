package com.panafrican.backend.domain;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "countries", uniqueConstraints = @UniqueConstraint(columnNames = "slug"))
public class Country {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 2)
    private String iso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id", nullable = false)
    private Region region;

    @OneToMany(mappedBy = "country", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    protected Country() {
    }

    public Country(String slug, String name, String iso, Region region) {
        this.slug = slug;
        this.name = name;
        this.iso = iso;
        this.region = region;
    }

    public Long getId() {
        return id;
    }

    public String getSlug() {
        return slug;
    }

    public String getName() {
        return name;
    }

    public String getIso() {
        return iso;
    }

    public Region getRegion() {
        return region;
    }

    public List<Post> getPosts() {
        return posts;
    }
}
