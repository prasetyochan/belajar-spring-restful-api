package chandraprasetyo.restful.service;

import chandraprasetyo.restful.entity.User;
import chandraprasetyo.restful.model.LoginUserRequest;
import chandraprasetyo.restful.model.TokenResponse;
import chandraprasetyo.restful.repository.UserRepository;
import chandraprasetyo.restful.security.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ValidationService validationService;


    @Override
    @Transactional
    public TokenResponse login(LoginUserRequest request) {
        validationService.validate(request);

        User user = userRepository.findById(request.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Username or Password is wrong"));

        //check if user input/request password == user password in database
        if(BCrypt.checkpw(request.getPassword(), user.getPassword())){
            //success login
            user.setToken(UUID.randomUUID().toString());
            user.setTokenExpiredAt(nextMonth());
            userRepository.save(user);

            //return Response Body
            return TokenResponse.builder()
                    .token(user.getToken())
                    .expiredAt(user.getTokenExpiredAt())
                    .build();
        }else {
            //failed login, throw exception
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Username or Password is wrong");
        }
    }

    private Long nextMonth(){
        return System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 30);
        // 1 seconds * 60 = 1 min * 60 = 1 hour * 24 = 1 day * 30 = 30 days
    }

    @Override
    public void logout(User user) {
        user.setToken(null);
        user.setTokenExpiredAt(null);

        userRepository.save(user);
    }

}
