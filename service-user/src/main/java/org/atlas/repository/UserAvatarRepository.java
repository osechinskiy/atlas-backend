package org.atlas.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.atlas.model.UserAvatar;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAvatarRepository extends JpaRepository<UserAvatar, Long> {

    @EntityGraph("user-avatar-graph")
    Optional<UserAvatar> findByUserId(Long userId);

    @EntityGraph("user-avatar-graph")
    List<UserAvatar> getAllByUserIdIn(Collection<Long> userIds);
}
