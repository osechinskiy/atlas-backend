package org.atlas.repository;

import java.util.List;
import org.atlas.model.ResumeViewStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResumeViewStatsRepository extends JpaRepository<ResumeViewStats, Long> {

    List<ResumeViewStats> getAllByResumeId(Long resumeId);

}
