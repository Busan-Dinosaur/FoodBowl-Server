package com.dinosaur.foodbowl.domain.address.entity;

import com.dinosaur.foodbowl.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "address")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = {"id"}, callSuper = false)
public class Address extends BaseEntity {

  public static final int MAX_ADDRESS_NAME_LENGTH = 512;
  public static final int MAX_REGION_DEPTH_NAME_LENGTH = 45;
  public static final int MAX_ROAD_NAME_LENGTH = 45;
  public static final int MAX_BUILDING_NO_LENGTH = 45;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, updatable = false)
  private Long id;

  @Column(name = "address_name", nullable = false, length = MAX_ADDRESS_NAME_LENGTH)
  private String addressName;

  @Column(name = "region_1depth_name", nullable = false, length = MAX_REGION_DEPTH_NAME_LENGTH)
  private String region1depthName;

  @Column(name = "region_2depth_name", nullable = false, length = MAX_REGION_DEPTH_NAME_LENGTH)
  private String region2depthName;

  @Column(name = "region_3depth_name", nullable = false, length = MAX_REGION_DEPTH_NAME_LENGTH)
  private String region3depthName;

  @Column(name = "road_name", nullable = false, length = MAX_ROAD_NAME_LENGTH)
  private String roadName;

  @Column(name = "main_building_no", nullable = false, length = MAX_BUILDING_NO_LENGTH)
  private String mainBuildingNo;

  @Column(name = "sub_building_no", length = MAX_BUILDING_NO_LENGTH)
  private String subBuildingNo;

  @Column(name = "longitude")
  private BigDecimal longitude;

  @Column(name = "latitude")
  private BigDecimal latitude;

  @Builder
  private Address(
      String addressName, String region1depthName, String region2depthName,
      String region3depthName, String roadName, String mainBuildingNo, String subBuildingNo,
      BigDecimal longitude, BigDecimal latitude
  ) {
    this.addressName = addressName;
    this.region1depthName = region1depthName;
    this.region2depthName = region2depthName;
    this.region3depthName = region3depthName;
    this.roadName = roadName;
    this.mainBuildingNo = mainBuildingNo;
    this.subBuildingNo = subBuildingNo;
    this.longitude = longitude;
    this.latitude = latitude;
  }
}
