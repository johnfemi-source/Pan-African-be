package com.panafrican.backend.domain;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "contact_messages")
public class ContactMessage {

    @Id
    @Column(nullable = false, unique = true, length = 36)
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false, length = 5000)
    private String body;

    @Column(nullable = false)
    private Instant createdAt;

    protected ContactMessage() {
    }

    public ContactMessage(String id, String name, String email, String body, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.body = body;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getBody() {
        return body;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
