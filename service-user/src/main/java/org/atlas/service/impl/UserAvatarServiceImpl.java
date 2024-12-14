package org.atlas.service.impl;

import java.util.Base64;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atlas.model.User;
import org.atlas.model.UserAvatar;
import org.atlas.repository.UserAvatarRepository;
import org.atlas.service.UserAvatarService;
import org.atlas.service.UserService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserAvatarServiceImpl implements UserAvatarService {

    private final UserAvatarRepository repository;

    private final UserService userService;

    @Override
    public void setUserAvatar(long userId, byte[] avatar) {
        User user = userService.getById(userId);
        UserAvatar userAvatar;
        String base64Encoded = Base64.getEncoder().encodeToString(avatar);
        if (user.getAvatar() != null) {
            userAvatar = user.getAvatar();
            userAvatar.setAvatar(base64Encoded);
        } else {
            userAvatar = UserAvatar.builder()
                    .user(user)
                    .avatar(base64Encoded)
                    .build();
        }
        user.setAvatar(save(userAvatar));
        userService.updateCash(user);
    }

    @Override
    public UserAvatar findByUserId(Long userId) {
        return repository.findByUserId(userId).orElse(null);
    }

    private UserAvatar save(UserAvatar userAvatar) {
        return repository.save(userAvatar);
    }
}
