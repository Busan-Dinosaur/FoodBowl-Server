package com.dinosaur.foodbowl.domain.store.entity;

import com.dinosaur.foodbowl.domain.address.entity.Address;
import com.dinosaur.foodbowl.domain.category.entity.Category;
import com.dinosaur.foodbowl.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "store")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = {"id"}, callSuper = false)
public class Store extends BaseEntity {

  public static final int MAX_STORE_NAME_LENGTH = 100;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, updatable = false)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "address_id", nullable = false)
  private Address address;

  @Column(name = "store_name", nullable = false, unique = true, length = MAX_STORE_NAME_LENGTH)
  private String storeName;

  @Builder
  private Store(Address address, Category category, String storeName) {
    this.address = address;
    this.storeName = storeName;
  }
}
