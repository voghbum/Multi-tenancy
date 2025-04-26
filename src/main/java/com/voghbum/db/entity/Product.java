package com.voghbum.db.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "PRODUCT")
@Data
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PRODUCT_ID_SEQUENCE_GENERATOR")
    @SequenceGenerator(name = "PRODUCT_ID_SEQUENCE_GENERATOR", sequenceName = "PRODUCT_ID_SEQUENCE", allocationSize = 1)
    @Column(name = "PRODUCT_ID")
    private Long id;

    @Column(name = "PRODUCT_NAME")
    private String name;
}
