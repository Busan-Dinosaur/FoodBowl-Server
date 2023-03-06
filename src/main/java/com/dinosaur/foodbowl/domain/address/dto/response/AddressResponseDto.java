package com.dinosaur.foodbowl.domain.address.dto.response;

import com.dinosaur.foodbowl.domain.address.entity.Address;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AddressResponseDto {

    private String addressName;
    private String region1depthName;
    private String region2depthName;
    private String region3depthName;
    private String roadName;
    private String mainBuildingNo;
    private String subBuildingNo;
    private BigDecimal longitude;
    private BigDecimal latitude;

    public static AddressResponseDto toDto(Address address) {
        return AddressResponseDto.builder()
                .addressName(address.getAddressName())
                .region1depthName(address.getRegion1depthName())
                .region2depthName(address.getRegion2depthName())
                .region3depthName(address.getRegion3depthName())
                .roadName(address.getRoadName())
                .mainBuildingNo(address.getMainBuildingNo())
                .subBuildingNo(address.getSubBuildingNo())
                .longitude(address.getLongitude())
                .latitude(address.getLatitude())
                .build();
    }
}
