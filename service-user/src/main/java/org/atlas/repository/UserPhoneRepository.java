package org.atlas.repository;

import org.atlas.model.UserPhoneNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPhoneRepository extends JpaRepository<UserPhoneNumber, Long> {

}
