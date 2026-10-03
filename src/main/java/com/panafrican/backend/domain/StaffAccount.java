package com.panafrican.backend.domain;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "staff_accounts", uniqueConstraints = @UniqueConstraint(columnNames = "username"))
public class StaffAccount implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 80)
    private String username;

    @Column(nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private UserRole role;

    private String regionSlug;
    private String countrySlug;

    @Column(nullable = false)
    private boolean enabled = true;

    protected StaffAccount() {}

    public StaffAccount(String username, String passwordHash, UserRole role, String regionSlug, String countrySlug) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.regionSlug = regionSlug;
        this.countrySlug = countrySlug;
    }

    public Long getId() { return id; }
    public UserRole getRole() { return role; }
    public String getRegionSlug() { return regionSlug; }
    public String getCountrySlug() { return countrySlug; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() { return passwordHash; }

    @Override
    public String getUsername() { return username; }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return enabled; }
}