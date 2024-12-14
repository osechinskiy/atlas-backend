package org.atlas.mapper;

import java.util.Collection;
import java.util.List;
import org.atlas.model.dto.UserinfoDto;
import org.atlas.rest.dto.AuthInfo;
import org.atlas.model.dto.UserDto;
import org.atlas.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "phoneNumbers", source = "user.phoneNumbers")
    @Mapping(target = "avatar", source = "user.avatar.avatar")
    UserDto map(User user);

    @Mapping(target = "userId", source = "user.id")
    AuthInfo mapToAuthInfo(User user);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userName", expression = "java(user.getFirstName() + \" \" + user.getLastName())")
    @Mapping(target = "avatar", source = "user.avatar.avatar")
    UserinfoDto userToUserinfoDto(User user);

    List<UserinfoDto> mapToUserinfoDto(Collection<User> users);
}
