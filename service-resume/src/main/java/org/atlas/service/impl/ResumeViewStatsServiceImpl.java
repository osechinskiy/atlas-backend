package org.atlas.service.impl;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atlas.model.ResumeViewStats;
import org.atlas.repository.ResumeViewStatsRepository;
import org.atlas.service.ResumeViewStatsService;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ResumeViewStatsServiceImpl implements ResumeViewStatsService {

    private final ResumeViewStatsRepository repository;

    @Override
    public void recordResumeView(Long resumeId) {
        // Найдите существующую запись статистики или создайте новую
        ResumeViewStats stats = repository.getAllByResumeId(resumeId)
                .stream()
                .filter(s -> s.getDate().isEqual(LocalDate.now()))
                .findFirst()
                .orElse(ResumeViewStats.builder()
                        .resumeId(resumeId)
                        .date(LocalDate.now())
                        .viewCount(0)
                        .build());

        // Увеличьте счетчик просмотров
        stats.setViewCount(stats.getViewCount() + 1);
        repository.save(stats);
    }

    @Override
    public List<ResumeViewStats> getOrderViewStats(Long resumeId) {
        return repository.getAllByResumeId(resumeId).stream()
                .sorted(Comparator.comparing(ResumeViewStats::getDate))
                .toList();
    }

    @Override
    public void deleteBySpecialistId(Long resumeId) {
        repository.deleteById(resumeId);
    }
}
