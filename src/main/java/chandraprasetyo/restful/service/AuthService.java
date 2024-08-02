package chandraprasetyo.restful.service;


import chandraprasetyo.restful.entity.User;
import chandraprasetyo.restful.model.LoginUserRequest;
import chandraprasetyo.restful.model.TokenResponse;

public interface AuthService {

    public TokenResponse login(LoginUserRequest request);

    public void logout(User user);

}
