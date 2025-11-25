package com.server.sellernexus.model.sellurNexus;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "seller")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Seller {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;  // updated from sellerName → name

    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Relationship with PlatformCredential
    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<PlatformCredential> credentials;
}
