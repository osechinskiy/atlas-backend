package org.atlas.service.impl;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.atlas.exception.Message;
import org.atlas.exception.ResumeNotFound;
import org.atlas.exception.TypeOfPerformedNotFoundException;
import org.atlas.mapper.ResumeMapper;
import org.atlas.model.Resume;
import org.atlas.model.TypeOfPerformed;
import org.atlas.model.dto.ResumeDto;
import org.atlas.repository.ResumeRepository;
import org.atlas.rest.dto.CreateResumeRequest;
import org.atlas.rest.dto.SpecialistResumeUpdateRequest;
import org.atlas.rest.dto.UserAvatarResponse;
import org.atlas.rest.dto.UserInfoRequest;
import org.atlas.rest.dto.UserInfoResponse;
import org.atlas.security.UserService;
import org.atlas.service.ResumeService;
import org.atlas.service.ResumeViewStatsService;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@Slf4j
@RequiredArgsConstructor
public class ResumeServiceImpl implements ResumeService {

    private final ResumeRepository repository;

    private final EurekaClient discoveryClient;

    private final RestClient restClient;

    private final ResumeMapper resumeMapper;

    private final ResumeViewStatsService viewStatsService;

    private final UserService userService;

    @Override
    @Transactional
    public List<ResumeDto> getAllResumeByCategory(TypeOfPerformed typeOfPerformed) {
        if (typeOfPerformed == null) {
            log.warn("typeOfPerformed is null");
            return Collections.emptyList();
        }

        List<Resume> resumes = repository.findByCategory(typeOfPerformed);
        if (CollectionUtils.isEmpty(resumes)) {
            return Collections.emptyList();
        }
        return process(resumes);

    }

    @Override
    @Transactional
    public ResumeDto getResumeById(Long id) {
        Resume resume = repository.findById(id)
                .orElseThrow(() -> new ResumeNotFound(Message.RESUME_NOT_FOUND.getName()));
        UserInfoRequest request = new UserInfoRequest(resume.getUserId(), resume.getPhoneId());
        UserInfoResponse response = getUserInfo(request);

        ResumeDto resumeDto = resumeMapper.mapToDto(resume);
        resumeDto.setUserName(response.getUserName());
        resumeDto.setUserPhone(response.getUserPhoneNumber());
        resumeDto.setUserAvatar(response.getUserAvatar());
        resumeDto.setAge(response.getAge());
        resumeDto.setGender(response.getGender());
        resumeDto.setEditable(userService.canEdit(resumeDto.getUserId()));

        viewStatsService.recordResumeView(resume.getId());

        return resumeDto;
    }

    @Override
    @Transactional
    public void createResume(CreateResumeRequest request) {
        repository.save(Resume.builder()
                .userId(request.getUserId())
                .phoneId(request.getPhoneId())
                .title(request.getTitle())
                .description(request.getDescription())
                .category(TypeOfPerformed.valueOf(request.getCategory()))
                .creationTimestamp(LocalDateTime.now())
                .experience(request.getExperience())
                .build());
        log.info("create new specialist resume : {}", request);
    }

    @Override
    @Transactional
    public void updateResume(Long resumeId, SpecialistResumeUpdateRequest request) {
        Resume resume = repository.findById(resumeId)
                .orElseThrow(() -> new ResumeNotFound(Message.RESUME_NOT_FOUND.getName()));

        resume.setPhoneId(request.getUserPhoneId());
        resume.setCategory(TypeOfPerformed.valueOf(request.getCategory()));
        resume.setTitle(request.getTitle());
        resume.setDescription(request.getDescription());
        resume.setExperience(request.getExperience());

        repository.save(resume);
        log.info("update specialist resume : {}", request);
    }

    @Override
    @Transactional
    public void deleteResumeById(Long id) {
        repository.deleteById(id);
        log.info("delete specialist : {}", id);
    }

    @Override
    public List<String> getTypesOfPerformed(Long userId) {
        List<TypeOfPerformed> typeOfPerformedList = repository.getTypeOfPerformedByUserId(userId);

        if (CollectionUtils.isEmpty(typeOfPerformedList)) {
            throw new TypeOfPerformedNotFoundException(Message.TYPE_OF_PERFORMED_NOT_FOUND.getName());
        }

        return typeOfPerformedList.stream().map(TypeOfPerformed::name).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<ResumeDto> getResumeListByUserId(Long userId) {
        List<Resume> resumeList = repository.getResumeListByUserId(userId);
        if (CollectionUtils.isEmpty(resumeList)) {
            return Collections.emptyList();
        }
        return process(resumeList);
    }

    @Override
    public List<Long> getUsersByCategory(String category) {
        TypeOfPerformed typeOfPerformed = TypeOfPerformed.valueOf(category);
        return repository.getUserIdsByCategory(typeOfPerformed);
    }

    /**
     * Получает информацию о пользователе в упрощенном формате. Этот метод извлекает информацию о пользователе,
     * отправляя запрос к сервису пользователя, используя его URL, полученный из Eureka.
     *
     * @param request Объект запроса, содержащий информацию о пользователе, включая идентификатор пользователя
     * идентификатор телефона.
     * @return Объект SimpleUserInfoResponse, содержащий информацию о пользователе.
     * @throws RestClientException Если произошла ошибка при выполнении запроса к сервису.
     */
    private List<UserAvatarResponse> getUserAvatar(List<Long> request) {
        InstanceInfo instanceInfo = discoveryClient.getNextServerFromEureka("SERVICE-USER", false);
        return restClient.post()
                .uri(instanceInfo.getHomePageUrl() + "/specialist/avatar")
                .accept(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

    private UserInfoResponse getUserInfo(UserInfoRequest request) {
        InstanceInfo instanceInfo = discoveryClient.getNextServerFromEureka("SERVICE-USER", false);
        String url = UriComponentsBuilder.fromHttpUrl(instanceInfo.getHomePageUrl())
                .path("/api/v1/user/information")
                .queryParam("userId", request.getUserId())
                .queryParam("phoneId", request.getUserPhoneId())
                .toUriString();
        return restClient.get()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(UserInfoResponse.class);
    }


    private List<ResumeDto> process(Collection<Resume> resumes) {
        List<Long> requests = new ArrayList<>();
        resumes.forEach(resume -> requests.add(resume.getUserId()));
        List<UserAvatarResponse> response = getUserAvatar(requests);
        Map<Long, UserAvatarResponse> responseMap = response.stream()
                .collect(Collectors.toMap(UserAvatarResponse::getUserId, Function.identity()));
        List<ResumeDto> resumeDtos = resumeMapper.mapToDtoList(resumes);
        resumeDtos.forEach(specialist -> {
            UserAvatarResponse res = responseMap.get(specialist.getUserId());
            if (res != null) {
                specialist.setUserName(res.getUserName());
                specialist.setUserAvatar(res.getAvatar());
            }
        });
        return resumeDtos;
    }
}
