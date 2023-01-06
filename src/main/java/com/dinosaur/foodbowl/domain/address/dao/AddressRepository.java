package com.dinosaur.foodbowl.domain.address.dao;

import com.dinosaur.foodbowl.domain.address.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {

}
