package org.atlas.rest.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.atlas.exception.Message;
import org.atlas.exception.InvalidTypeOfPerformedException;
import org.atlas.model.ResumeViewStats;
import org.atlas.model.TypeOfPerformed;
import org.atlas.model.dto.ResumeDto;
import org.atlas.rest.dto.CreateResumeRequest;
import org.atlas.rest.dto.SpecialistResumeUpdateRequest;
import org.atlas.service.ResumeService;
import org.atlas.service.ResumeViewStatsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeService;

    private final ResumeViewStatsService viewStatsService;

    @GetMapping("/{category}/all")
    public ResponseEntity<List<ResumeDto>> getAllResume(@PathVariable String category) {
        TypeOfPerformed typeOfPerformed;
        try {
            typeOfPerformed = TypeOfPerformed.valueOf(category);
        } catch (Exception ex) {
            throw new InvalidTypeOfPerformedException(Message.INVALID_TYPE_OF_PERFORMED.getName());
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(resumeService.getAllResumeByCategory(typeOfPerformed));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResumeDto> getResumeById(@PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(resumeService.getResumeById(id));

    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updateResume(@PathVariable Long id,
            @RequestBody @Valid SpecialistResumeUpdateRequest request) {
        resumeService.updateResume(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteResume(@PathVariable Long id) {
        resumeService.deleteResumeById(id);
    }

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public void resumeResume(@RequestBody @Valid CreateResumeRequest request) {
        resumeService.createResume(request);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<List<ResumeDto>> getResumeByUserId(@PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(resumeService.getResumeListByUserId(id));
    }

    @GetMapping("/{id}/stats")
    public ResponseEntity<List<ResumeViewStats>> getViewStats(@PathVariable Long id) {
        List<ResumeViewStats> stats = viewStatsService.getOrderViewStats(id);
        return ResponseEntity.status(HttpStatus.OK).body(stats);
    }

    @GetMapping("/api/v1/resume/categories_info")
    public ResponseEntity<List<String>> getResumeCategoriesInfo(@RequestParam(value = "userId") Long userId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(resumeService.getTypesOfPerformed(userId));
    }

    @GetMapping("/api/v1/resume/info")
    public ResponseEntity<List<Long>> getResumeInfo(@RequestParam(value = "category") String category) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(resumeService.getUsersByCategory(category));
    }
}
