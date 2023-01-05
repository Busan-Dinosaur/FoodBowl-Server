package com.dinosaur.foodbowl.domain.store.entity;

import com.dinosaur.foodbowl.domain.address.entity.Address;
import com.dinosaur.foodbowl.domain.category.entity.Category;
import com.dinosaur.foodbowl.global.entity.BaseEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "store")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Store extends BaseEntity {

  private static final int MAX_STORE_NAME_LENGTH = 100;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, updatable = false)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name="address_id", nullable = false)
  private Address address;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name="category_id", nullable = false)
  private Category category;

  @Column(name = "store_name", length = MAX_STORE_NAME_LENGTH, nullable = false)
  private String storeName;

  @Builder
  private Store(Address address, Category category, String storeName) {
    this.address = address;
    this.category = category;
    this.storeName = storeName;
  }

}
