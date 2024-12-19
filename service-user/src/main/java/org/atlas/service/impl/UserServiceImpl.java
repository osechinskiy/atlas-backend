package org.atlas.service.impl;

import java.time.LocalDate;
import java.time.Period;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.atlas.exception.ChangePasswordException;
import org.atlas.exception.InvalidPasswordException;
import org.atlas.exception.UserPhoneNotFound;
import org.atlas.model.Gender;
import org.atlas.model.Role;
import org.atlas.model.UserPhoneNumber;
import org.atlas.rest.dto.ChangePasswordRequest;
import org.atlas.rest.dto.SignInRequest;
import org.atlas.rest.dto.SignUpRequest;
import org.atlas.exception.Message;
import org.atlas.exception.UserAlreadyExistException;
import org.atlas.exception.UserNotFoundException;
import org.atlas.model.User;
import org.atlas.repository.UserRepository;
import org.atlas.rest.dto.SimpleUserInfoRequest;
import org.atlas.rest.dto.SimpleUserInfoResponse;
import org.atlas.rest.dto.UserInfoResponse;
import org.atlas.rest.dto.UserMainInfoRequest;
import org.atlas.service.UserService;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    private final PasswordEncoder passwordEncoder;

    private final UserService self;

    @Override
    public User create(SignUpRequest request) {
        if (repository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistException(
                    String.format(Message.USER_EMAIL_ALREADY_EXIST.getTitle(), request.getEmail()));
        }
        return repository.save(User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .authority(Role.NO_CATEGORY)
                .isActive(true)
                .build());
    }

    @Override
    @Transactional
    public User getByEmail(SignInRequest request) {
        User user = repository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException(
                        String.format(Message.USER_BY_EMAIL_NOT_FOUND.getTitle(), request.getEmail())));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidPasswordException(Message.INVALID_PASSWORD.getTitle());
        }
        return user;
    }

    @Override
    @Cacheable("userCache")
    public User getById(Long id) {
        return repository.findById(id).orElseThrow(() -> new UserNotFoundException(
                String.format(Message.USER_BY_USERNAME_NOT_FOUND.getTitle(), id)));
    }

    @Override
    @Transactional
    @CachePut(value = "userCache", key = "#request.id")
    public User updateUserMainInfo(UserMainInfoRequest request) {
        User user = self.getById(request.getId());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setBirthday(request.getBirthday());
        user.setGender(Gender.valueOf(request.getGender()));

        return repository.save(user);
    }

    @Override
    @Transactional
    @CachePut(value = "userCache", key = "#userId")
    public User changePassword(Long userId, ChangePasswordRequest request) {
        User user = self.getById(userId);
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new ChangePasswordException(Message.INCORRECT_OLD_PASSWORD.getTitle());
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        return repository.save(user);
    }

    @Override
    @Transactional
    public SimpleUserInfoResponse getSimpleUserInfoById(SimpleUserInfoRequest request) {
        User user = self.getById(request.getUserId());
        UserPhoneNumber phoneNumber = getUserPhoneByUserAndPhoneId(request.getPhoneId(), user);

        return new SimpleUserInfoResponse(user.getFirstName(), phoneNumber.getPhone());
    }

    @Override
    public UserInfoResponse getUserInfoById(Long userId, Long phoneId) {
        User user = self.getById(userId);
        UserPhoneNumber phoneNumber = getUserPhoneByUserAndPhoneId(phoneId, user);

        return UserInfoResponse.builder()
                .userName(String.format("%s %s", user.getFirstName(), user.getLastName()))
                .userPhoneNumber(phoneNumber.getPhone())
                .age(Period.between(user.getBirthday(), LocalDate.now()).getYears())
                .userAvatar(user.getAvatar().getAvatar())
                .gender(user.getGender().name())
                .build();
    }

    @Override
    @Transactional
    public List<User> getUserByIds(Collection<Long> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return Collections.emptyList();
        }
        return repository.getAllByIdIn(userIds);
    }

    @Override
    @CachePut(value = "userCache", key = "#user.id")
    public User updateCash(User user) {
        return user;
    }


    private UserPhoneNumber getUserPhoneByUserAndPhoneId(Long phoneId, User user) {
        return user.getPhoneNumbers().stream()
                .filter(phone -> phone.getId().equals(phoneId))
                .findFirst()
                .orElseThrow(() -> new UserPhoneNotFound(Message.PHONE_NOT_FOUND.getTitle()));
    }
}
