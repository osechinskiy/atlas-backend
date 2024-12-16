package org.atlas.rest.controller;

import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atlas.model.dto.UserinfoDto;
import org.atlas.rest.dto.AuthInfo;
import org.atlas.rest.dto.ChangePasswordRequest;
import org.atlas.rest.dto.CreateUserPhoneRequest;
import org.atlas.rest.dto.SignInRequest;
import org.atlas.rest.dto.SignUpRequest;
import org.atlas.rest.dto.SimpleUserInfoRequest;
import org.atlas.rest.dto.SimpleUserInfoResponse;
import org.atlas.rest.dto.UpdateUserPhoneRequest;
import org.atlas.model.dto.UserDto;
import org.atlas.mapper.UserMapper;
import org.atlas.rest.dto.UserInfoResponse;
import org.atlas.rest.dto.UserMainInfoRequest;
import org.atlas.service.UserAvatarService;
import org.atlas.service.UserPhoneService;
import org.atlas.service.UserService;
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
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    private final UserAvatarService userAvatarService;

    private final UserPhoneService userPhoneService;

    private final UserMapper mapper;

    @GetMapping("/account")
    public ResponseEntity<AuthInfo> getUser(@RequestBody @Valid SignInRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(mapper.mapToAuthInfo(userService.getByEmail(request)));
    }

    @PostMapping("/account")
    public ResponseEntity<AuthInfo> signUp(@RequestBody @Valid SignUpRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.mapToAuthInfo(userService.create(request)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(mapper.map(userService.getById(id)));
    }

    @GetMapping("/api/v1/user/info")
    public ResponseEntity<SimpleUserInfoResponse> getSimpleUserInfo(
            @RequestParam(value = "userId") long userId,
            @RequestParam(value = "phoneId") long phoneId) {
        SimpleUserInfoRequest request = new SimpleUserInfoRequest(userId, phoneId);
        return ResponseEntity.status(HttpStatus.OK).body(userService.getSimpleUserInfoById(request));
    }

    @PatchMapping("/api/v1/user")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updateUser(@RequestBody @Valid UserMainInfoRequest request) {
        userService.updateUserMainInfo(request);
    }

    @PostMapping("/upload-avatar")
    public ResponseEntity<String> uploadAvatar(@RequestParam("userId") long userId,
            @RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Нет файла для загрузки.");
        }
        userAvatarService.setUserAvatar(userId, file.getBytes());
        return ResponseEntity.ok().body("Аватар успешно загружен.");
    }

    @DeleteMapping("/{userId}/phone/{phoneId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePhone(@PathVariable Long userId, @PathVariable Long phoneId) {
        userPhoneService.deleteByPhoneId(userId, phoneId);
    }

    @PatchMapping("/phones")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updatePhone(@RequestBody @Valid UpdateUserPhoneRequest request) {
        userPhoneService.updatePhoneNumber(request);
    }

    @PostMapping("/phones")
    @ResponseStatus(HttpStatus.CREATED)
    public void createPhone(@RequestBody @Valid CreateUserPhoneRequest request) {
        userPhoneService.createPhoneNumber(request);
    }

    @PatchMapping("/{id}/password")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void changePassword(@PathVariable Long id,
            @RequestBody @Valid ChangePasswordRequest request) {
        userService.changePassword(id, request);
    }

    @PostMapping("/specialist/avatar")
    public ResponseEntity<List<UserinfoDto>> getUserAvatars(@RequestBody List<Long> request) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(mapper.mapToUserinfoDto(userService.getUserByIds(request)));
    }

    @GetMapping("/api/v1/user/information")
    public ResponseEntity<UserInfoResponse> getUserInformation(
            @RequestParam(value = "userId") Long userId,
            @RequestParam(value = "phoneId") Long phoneId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.getUserInfoById(userId, phoneId));
    }

}
