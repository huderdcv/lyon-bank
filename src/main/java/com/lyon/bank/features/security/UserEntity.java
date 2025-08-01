package com.lyon.bank.features.security;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String username; // Acts as ID/DNI

  @Column(nullable = false)
  private String password; // BCrypt Hash

  @Column(nullable = false)
  private String email;

  @Builder.Default
  private boolean enabled = true;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
    name = "users_roles",
    joinColumns = @JoinColumn(name = "user_id"),
    inverseJoinColumns = @JoinColumn(name = "role_id")
  )
  private Set<RoleEntity> roles = new HashSet<>();

  @CreationTimestamp
  private LocalDateTime createdAt;

  @UpdateTimestamp
  private LocalDateTime updatedAt;
}
