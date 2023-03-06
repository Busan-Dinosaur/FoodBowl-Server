package com.dinosaur.foodbowl.domain.store.application;

import com.dinosaur.foodbowl.domain.post.dao.PostRepository;
import com.dinosaur.foodbowl.domain.store.dao.StoreRepository;
import com.dinosaur.foodbowl.domain.store.dto.response.StoreSearchResponse;
import com.dinosaur.foodbowl.domain.store.entity.Store;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StoreService {

    private final PostRepository postRepository;
    private final StoreRepository storeRepository;

    public List<StoreSearchResponse> searchStoresByName(
            final String storeName, final Pageable pageable
    ) {
        final List<Store> stores = storeRepository.findStoresByStoreNameContaining(
                storeName, pageable
        );

        return stores.stream()
                .map(store -> StoreSearchResponse.from(store, postRepository.countByStore(store)))
                .toList();
    }
}
