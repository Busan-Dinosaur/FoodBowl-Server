package com.dinosaur.foodbowl.domain.address.dto;

import static com.dinosaur.foodbowl.domain.address.entity.Address.MAX_ADDRESS_NAME_LENGTH;
import static com.dinosaur.foodbowl.domain.address.entity.Address.MAX_BUILDING_NO_LENGTH;
import static com.dinosaur.foodbowl.domain.address.entity.Address.MAX_REGION_DEPTH_NAME_LENGTH;
import static com.dinosaur.foodbowl.domain.address.entity.Address.MAX_ROAD_NAME_LENGTH;

import com.dinosaur.foodbowl.domain.address.entity.Address;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class AddressDto {

  @Length(max = MAX_ADDRESS_NAME_LENGTH)
  private String addressName;

  @Length(max = MAX_REGION_DEPTH_NAME_LENGTH)
  private String region1depthName;

  @Length(max = MAX_REGION_DEPTH_NAME_LENGTH)
  private String region2depthName;

  @Length(max = MAX_REGION_DEPTH_NAME_LENGTH)
  private String region3depthName;

  @Length(max = MAX_ROAD_NAME_LENGTH)
  private String roadName;

  @Length(max = MAX_BUILDING_NO_LENGTH)
  private String mainBuildingNo;

  @Length(max = MAX_BUILDING_NO_LENGTH)
  private String subBuildingNo;

  private BigDecimal longitude;

  private BigDecimal latitude;

  public Address toEntity() {
    return Address.builder()
        .addressName(addressName)
        .region1depthName(region1depthName)
        .region2depthName(region2depthName)
        .region3depthName(region3depthName)
        .roadName(roadName)
        .mainBuildingNo(mainBuildingNo)
        .subBuildingNo(subBuildingNo)
        .longitude(longitude)
        .latitude(latitude)
        .build();
  }
}
