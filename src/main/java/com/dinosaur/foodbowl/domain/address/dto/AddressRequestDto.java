package com.dinosaur.foodbowl.domain.address.dto;

import static com.dinosaur.foodbowl.domain.address.entity.Address.MAX_ADDRESS_NAME_LENGTH;
import static com.dinosaur.foodbowl.domain.address.entity.Address.MAX_BUILDING_NO_LENGTH;
import static com.dinosaur.foodbowl.domain.address.entity.Address.MAX_REGION_DEPTH_NAME_LENGTH;
import static com.dinosaur.foodbowl.domain.address.entity.Address.MAX_ROAD_NAME_LENGTH;

import com.dinosaur.foodbowl.domain.address.entity.Address;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class AddressRequestDto {

  @NotNull
  @Length(max = MAX_ADDRESS_NAME_LENGTH)
  private String addressName;
  @NotNull
  @Length(max = MAX_REGION_DEPTH_NAME_LENGTH)
  private String region1depthName;
  @NotNull
  @Length(max = MAX_REGION_DEPTH_NAME_LENGTH)
  private String region2depthName;
  @NotNull
  @Length(max = MAX_REGION_DEPTH_NAME_LENGTH)
  private String region3depthName;
  @NotNull
  @Length(max = MAX_ROAD_NAME_LENGTH)
  private String roadName;
  @NotNull
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
