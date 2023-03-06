package com.dinosaur.foodbowl.domain.post.dao;

import com.dinosaur.foodbowl.domain.post.entity.Post;
import com.dinosaur.foodbowl.domain.store.entity.Store;
import com.dinosaur.foodbowl.domain.user.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends Repository<Post, Long> {

    Post save(Post post);

    void delete(Post post);

    boolean existsById(Long id);

    Optional<Post> findById(Long id);

    Post getReferenceById(Long id);

    @Query("select p from Post p left join fetch p.thumbnail where p.user = :user")
    List<Post> findThumbnailsByUser(@Param("user") User user, Pageable pageable);

    @Query("select p from Post p"
            + " left join fetch p.user"
            + " left join fetch p.store"
            + " left join fetch p.store.address"
            + " where p.user in (select f.following from Follow f where f.follower = :user)"
            + " or p.user = :user")
    List<Post> findFeed(@Param("user") User user, Pageable pageable);

    @EntityGraph(attributePaths = {"thumbnail"}, type = EntityGraphType.LOAD)
    List<Post> findAllByUserNot(User user, Pageable pageable);

    long countByStore(Store store);
}
