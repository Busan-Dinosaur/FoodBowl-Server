package com.dinosaur.foodbowl.domain.store;

import static com.dinosaur.foodbowl.domain.store.entity.Store.MAX_STORE_NAME_LENGTH;

import com.dinosaur.foodbowl.domain.address.AddressTestHelper;
import com.dinosaur.foodbowl.domain.address.entity.Address;
import com.dinosaur.foodbowl.domain.category.CategoryTestHelper;
import com.dinosaur.foodbowl.domain.store.dao.StoreRepository;
import com.dinosaur.foodbowl.domain.store.entity.Store;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StoreTestHelper {

  @Autowired
  StoreRepository storeRepository;

  @Autowired
  AddressTestHelper addressTestHelper;

  @Autowired
  CategoryTestHelper categoryTestHelper;

  private String getRandomUUIDLengthWith(int length) {
    String randomString = UUID.randomUUID()
        .toString();
    length = Math.min(length, randomString.length());
    return randomString.substring(0, length);
  }

  public StoreBuilder builder() {
    return new StoreBuilder();
  }

  public final class StoreBuilder {

    private Address address;
    private String storeName;

    private StoreBuilder() {
    }

    public StoreBuilder address(Address address) {
      this.address = address;
      return this;
    }

    public StoreBuilder storeName(String storeName) {
      this.storeName = storeName;
      return this;
    }

    public Store build() {
      return storeRepository.save(Store.builder()
          .address(address != null ? address : addressTestHelper.builder().build())
          .storeName(storeName != null ? storeName : getRandomUUIDLengthWith(MAX_STORE_NAME_LENGTH))
          .build());
    }
  }
}
