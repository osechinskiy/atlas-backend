package org.atlas.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import com.netflix.appinfo.InstanceInfo;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.atlas.exception.ResumeNotFound;
import org.atlas.exception.TypeOfPerformedNotFoundException;
import org.atlas.mapper.ResumeMapper;
import org.atlas.model.Resume;
import org.atlas.model.TypeOfPerformed;
import org.atlas.model.dto.ResumeDto;
import org.atlas.repository.ResumeRepository;
import org.atlas.rest.dto.CreateResumeRequest;
import org.atlas.rest.dto.SpecialistResumeUpdateRequest;
import org.atlas.security.UserService;
import org.atlas.service.ResumeViewStatsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;

import com.netflix.discovery.EurekaClient;


@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ResumeServiceImplTest {

    @Mock
    private ResumeRepository repository;

    @Mock
    private EurekaClient discoveryClient;

    @Mock
    private RestClient restClient;

    @Mock
    private ResumeMapper resumeMapper;

    @Mock
    private ResumeViewStatsService viewStatsService;

    @Mock
    private UserService userService;

    @Mock
    private InstanceInfo instanceInfo;

    @InjectMocks
    private ResumeServiceImpl resumeService;

    @Test
    @Order(1)
    @DisplayName("Должен вернуть пустой список, если тип выполнения null")
    public void shouldReturnEmptyListWhenTypeOfPerformedIsNull() {
        List<ResumeDto> result = resumeService.getAllResumeByCategory(null);
        assertEquals(Collections.emptyList(), result);
    }


    @Test
    @Order(3)
    @DisplayName("Должен вернуть пустой список, если резюме не найдены по категории")
    public void shouldReturnEmptyListWhenNoResumesFoundByCategory() {
        TypeOfPerformed type = TypeOfPerformed.CLEAN_UP;
        when(repository.findByCategory(type)).thenReturn(Collections.emptyList());

        List<ResumeDto> result = resumeService.getAllResumeByCategory(type);

        assertEquals(0, result.size());
        verify(repository).findByCategory(type);
    }

    @Test
    @Order(4)
    @DisplayName("Должен бросить ResumeNotFound, если резюме не найдено")
    public void shouldThrowResumeNotFoundWhenResumeNotFound() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResumeNotFound.class, () -> resumeService.getResumeById(1L));
    }




    @Test
    @Order(7)
    @DisplayName("Должен создать резюме с текущей датой и временем")
    public void shouldCreateResumeWithCurrentTimestamp() {
        CreateResumeRequest request = new CreateResumeRequest();
        request.setUserId(1L);
        request.setPhoneId(2L);
        request.setTitle("Title");
        request.setDescription("Description");
        request.setCategory("CLEAN_UP");
        request.setExperience(1);

        resumeService.createResume(request);

        verify(repository).save(argThat(resume -> resume.getCreationTimestamp() != null));
    }

    @Test
    @Order(8)
    @DisplayName("Должен обновить резюме")
    public void shouldUpdateResume() {
        Resume resume = new Resume();
        when(repository.findById(anyLong())).thenReturn(Optional.of(resume));

        SpecialistResumeUpdateRequest request = new SpecialistResumeUpdateRequest();
        request.setUserPhoneId(2L);
        request.setCategory("CLEAN_UP");
        request.setTitle("Updated Title");
        request.setDescription("Updated Description");
        request.setExperience(2);

        resumeService.updateResume(1L, request);

        verify(repository).save(any(Resume.class));
    }

    @Test
    @Order(9)
    @DisplayName("Должен бросить ResumeNotFound, если резюме для обновления не найдено")
    public void shouldThrowResumeNotFoundWhenUpdatingNonExistentResume() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        SpecialistResumeUpdateRequest request = new SpecialistResumeUpdateRequest();

        assertThrows(ResumeNotFound.class, () -> resumeService.updateResume(1L, request));
    }

    @Test
    @Order(10)
    @DisplayName("Должен удалить резюме по ID")
    public void shouldDeleteResumeById() {
        resumeService.deleteResumeById(1L);
        verify(repository).deleteById(1L);
    }

    @Test
    @Order(11)
    @DisplayName("Должен бросить исключение при удалении несуществующего резюме")
    public void shouldThrowExceptionWhenDeletingNonExistentResume() {
        doThrow(new RuntimeException("Resume not found")).when(repository).deleteById(anyLong());

        assertThrows(RuntimeException.class, () -> resumeService.deleteResumeById(1L));
    }

    @Test
    @Order(12)
    @DisplayName("Должен бросить TypeOfPerformedNotFoundException, если типы выполнения не найдены")
    public void shouldThrowTypeOfPerformedNotFoundExceptionWhenTypesNotFound() {
        when(repository.getTypeOfPerformedByUserId(anyLong())).thenReturn(Collections.emptyList());

        assertThrows(TypeOfPerformedNotFoundException.class, () -> resumeService.getTypesOfPerformed(1L));
    }

    @Test
    @Order(13)
    @DisplayName("Должен вернуть список типов выполнения по ID пользователя")
    public void shouldReturnTypesOfPerformedByUserId() {
        TypeOfPerformed type = TypeOfPerformed.CLEAN_UP;
        when(repository.getTypeOfPerformedByUserId(anyLong())).thenReturn(List.of(type));

        List<String> result = resumeService.getTypesOfPerformed(1L);

        assertEquals(1, result.size());
        assertEquals(type.name(), result.get(0));
    }


    @Test
    @Order(15)
    @DisplayName("Должен вернуть пустой список, если резюме не найдены по ID пользователя")
    public void shouldReturnEmptyListWhenNoResumesFoundByUserId() {
        when(repository.getResumeListByUserId(anyLong())).thenReturn(Collections.emptyList());

        List<ResumeDto> result = resumeService.getResumeListByUserId(1L);

        assertEquals(0, result.size());
        verify(repository).getResumeListByUserId(1L);
    }


    @Test
    @Order(16)
    @DisplayName("Должен бросить исключение при ошибке получения списка резюме по ID пользователя")
    public void shouldThrowExceptionWhenErrorOccursWhileGettingResumeListByUserId() {
        when(repository.getResumeListByUserId(anyLong())).thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> resumeService.getResumeListByUserId(1L));
    }
}