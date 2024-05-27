package com.example.api.model.database;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.api.model.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * User Entity
 * <p>
 * Represents a user entity in the database.
 */
@Data
@Table(name = "user")
@Entity
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler", "tokens", "refreshTokens", "friendships", "authorities", "messages" })
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "image")
    private String image;

    @Column(name = "email", unique = true, nullable = false)
    @NotBlank(message = "email is mandatory")
    @Email(message = "must be a valid email")
    private String email;

    @Column(name = "username", unique = true, nullable = false)
    @NotBlank(message = "username is mandatory")
    private String username;

    @Column(name = "logged", unique = false, nullable = false)
    @NotNull(message = "logged is mandatory")
    private Boolean logged;

    @Column(name = "last_connection")
    private Date lastConnection;

    @Column(name = "password")
    @NotBlank(message = "password is mandatory")
    private String password;

    @Enumerated(value = EnumType.STRING)
    @NotNull(message = "role is mandatory")
    Role role;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Token> tokens;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<RefreshToken> refreshTokens;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Code> codes;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Friendship> friendships;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Code> messages;

    /**
     * Retrieves the authorities granted to the user.
     *
     * @return the authorities granted to the user
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    /**
     * Indicates whether the user's account has expired.
     *
     * @return true if the user's account is valid, false otherwise
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user is locked or unlocked.
     *
     * @return true if the user is not locked, false otherwise
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Indicates whether the user's credentials (password) has expired.
     *
     * @return true if the user's credentials are valid, false otherwise
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user is enabled or disabled.
     *
     * @return true if the user is enabled, false otherwise
     */
    @Override
    public boolean isEnabled() {
        return role != Role.REGISTER;
    }

    /**
     * override the method toString() of the class
     * 
     * @return each field of the object seperate by a space
     */
    @Override
    public String toString() {
        return username + " role: " + role + " logged: " + logged;
    }

}
