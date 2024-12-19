package org.atlas.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.atlas.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @EntityGraph("user-graph")
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Override
    @EntityGraph("user-graph")
    Optional<User> findById(Long aLong);

    @EntityGraph("user-graph")
    List<User> getAllByIdIn(Collection<Long> userIds);
}
