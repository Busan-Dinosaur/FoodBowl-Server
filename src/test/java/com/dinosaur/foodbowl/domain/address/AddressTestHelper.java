package com.dinosaur.foodbowl.domain.address;

import static com.dinosaur.foodbowl.domain.address.entity.Address.MAX_ADDRESS_NAME_LENGTH;
import static com.dinosaur.foodbowl.domain.address.entity.Address.MAX_BUILDING_NO_LENGTH;
import static com.dinosaur.foodbowl.domain.address.entity.Address.MAX_REGION_DEPTH_NAME_LENGTH;
import static com.dinosaur.foodbowl.domain.address.entity.Address.MAX_ROAD_NAME_LENGTH;

import com.dinosaur.foodbowl.domain.address.dao.AddressRepository;
import com.dinosaur.foodbowl.domain.address.entity.Address;
import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AddressTestHelper {

  @Autowired
  private AddressRepository addressRepository;

  public AddressBuilder builder() {
    return new AddressBuilder();
  }

  private String getRandomUUIDLengthWith(int length) {
    String randomString = UUID.randomUUID()
        .toString();
    length = Math.min(length, randomString.length());
    return randomString.substring(0, length);
  }

  public final class AddressBuilder {

    private String addressName;
    private String region1depthName;
    private String region2depthName;
    private String region3depthName;
    private String roadName;
    private String mainBuildingNo;
    private String subBuildingNo;
    private BigDecimal longitude;
    private BigDecimal latitude;

    private AddressBuilder() {
    }

    public AddressBuilder addressName(String addressName) {
      this.addressName = addressName;
      return this;
    }

    public AddressBuilder region1depthName(String region1depthName) {
      this.region1depthName = region1depthName;
      return this;
    }

    public AddressBuilder region2depthName(String region2depthName) {
      this.region2depthName = region2depthName;
      return this;
    }

    public AddressBuilder region3depthName(String region3depthName) {
      this.region3depthName = region3depthName;
      return this;
    }

    public AddressBuilder roadName(String roadName) {
      this.roadName = roadName;
      return this;
    }

    public AddressBuilder mainBuildingNo(String mainBuildingNo) {
      this.mainBuildingNo = mainBuildingNo;
      return this;
    }

    public AddressBuilder subBuildingNo(String subBuildingNo) {
      this.subBuildingNo = subBuildingNo;
      return this;
    }

    public AddressBuilder longitude(BigDecimal longitude) {
      this.longitude = longitude;
      return this;
    }

    public AddressBuilder latitude(BigDecimal latitude) {
      this.latitude = latitude;
      return this;
    }

    public Address build() {
      return addressRepository.save(Address.builder()
          .addressName(addressName != null ? addressName :
              getRandomUUIDLengthWith(MAX_ADDRESS_NAME_LENGTH))
          .region1depthName(region1depthName != null ? region1depthName
              : getRandomUUIDLengthWith(MAX_REGION_DEPTH_NAME_LENGTH))
          .region2depthName(region2depthName != null ? region2depthName
              : getRandomUUIDLengthWith(MAX_REGION_DEPTH_NAME_LENGTH))
          .region3depthName(region3depthName != null ? region3depthName
              : getRandomUUIDLengthWith(MAX_REGION_DEPTH_NAME_LENGTH))
          .roadName(roadName != null ? roadName
              : getRandomUUIDLengthWith(MAX_ROAD_NAME_LENGTH))
          .mainBuildingNo(mainBuildingNo != null ? mainBuildingNo
              : getRandomUUIDLengthWith(MAX_BUILDING_NO_LENGTH))
          .subBuildingNo(subBuildingNo != null ? subBuildingNo
              : getRandomUUIDLengthWith(MAX_BUILDING_NO_LENGTH))
          .longitude(longitude != null ? longitude : BigDecimal.valueOf(Math.random()))
          .latitude(latitude != null ? latitude : BigDecimal.valueOf(Math.random()))
          .build());
    }
  }

}
