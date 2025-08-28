package co.com.loan.consumer;

import co.com.loan.model.user.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

  User toDomain(UserResponse data);
}
