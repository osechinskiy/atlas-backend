package org.atlas.repository;

import java.util.List;
import org.atlas.model.Resume;
import org.atlas.model.TypeOfPerformed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long> {

    @Query("select r from Resume r where r.category = :category")
    List<Resume> findByCategory(@Param("category") TypeOfPerformed category);

    @Query("select r from Resume r where r.userId = :userId")
    List<Resume> getResumeListByUserId(@Param("userId") Long userId);

    @Query("select r.category from Resume r where r.userId = :userId")
    List<TypeOfPerformed> getTypeOfPerformedByUserId(@Param("userId") Long userId);

    @Query("select r.userId from Resume r where r.category = :category")
    List<Long> getUserIdsByCategory(@Param("category") TypeOfPerformed category);
}
