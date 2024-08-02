package chandraprasetyo.restful.service;

import chandraprasetyo.restful.entity.User;
import chandraprasetyo.restful.model.RegisterUserRequest;
import chandraprasetyo.restful.model.UpdateUserRequest;
import chandraprasetyo.restful.model.UserResponse;
import org.springframework.stereotype.Service;

public interface UserService {

    public void register(RegisterUserRequest request);

    public UserResponse get(User user);

    public UserResponse update(User user, UpdateUserRequest request);



}
