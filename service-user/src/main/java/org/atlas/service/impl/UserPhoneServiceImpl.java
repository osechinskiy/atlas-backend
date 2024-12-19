package org.atlas.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atlas.model.User;
import org.atlas.model.UserPhoneNumber;
import org.atlas.repository.UserPhoneRepository;
import org.atlas.rest.dto.CreateUserPhoneRequest;
import org.atlas.rest.dto.UpdateUserPhoneRequest;
import org.atlas.service.UserPhoneService;
import org.atlas.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserPhoneServiceImpl implements UserPhoneService {

    private final UserPhoneRepository repository;

    private final UserService userService;

    @Override
    public void deleteByPhoneId(long userId, long phoneId) {
        User user = userService.getById(userId);
        user.getPhoneNumbers().removeIf(number -> number.getId().equals(phoneId));
        userService.updateCash(user);
        repository.deleteById(phoneId);
    }

    @Override
    @Transactional
    public void updatePhoneNumber(UpdateUserPhoneRequest request) {
        UserPhoneNumber phoneNumber = repository.findById(request.getId()).orElseThrow();
        phoneNumber.setPhone(request.getPhone());
        save(phoneNumber);
        User user = userService.getById(phoneNumber.getUserId());
        user.getPhoneNumbers().stream()
                .filter(existingPhoneNumber -> existingPhoneNumber.equals(phoneNumber))
                .findFirst()
                .ifPresent(existingPhoneNumber -> existingPhoneNumber.setPhone(phoneNumber.getPhone()));
        userService.updateCash(user);
    }

    @Override
    public void createPhoneNumber(CreateUserPhoneRequest request) {
        UserPhoneNumber phoneNumber = new UserPhoneNumber();
        phoneNumber.setUserId(request.getUserId());
        phoneNumber.setPhone(request.getPhone());
        save(phoneNumber);
        User user = userService.getById(phoneNumber.getUserId());
        user.getPhoneNumbers().add(phoneNumber);
        userService.updateCash(user);
    }

    private UserPhoneNumber save(UserPhoneNumber userPhoneNumber) {
        return repository.save(userPhoneNumber);
    }
}
