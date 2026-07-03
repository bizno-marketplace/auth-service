package com.biznopay.authservice.infra.persistence.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "T_SELLER_REJECTIONS")
public class SellerRejectionJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_seller_id", nullable = false)
    private UserJpaEntity seller;

    @ElementCollection
    @CollectionTable(
            name = "t_seller_rejection_reasons",
            joinColumns = @JoinColumn(name = "approval_history_id")
    )
    @Column(name = "reason")
    private List<String> reasonsForRejections;

    private int numberOfRejections;
}
