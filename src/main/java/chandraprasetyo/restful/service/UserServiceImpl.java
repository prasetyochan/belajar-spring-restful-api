package chandraprasetyo.restful.service;

import chandraprasetyo.restful.entity.User;
import chandraprasetyo.restful.model.RegisterUserRequest;
import chandraprasetyo.restful.model.UpdateUserRequest;
import chandraprasetyo.restful.model.UserResponse;
import chandraprasetyo.restful.repository.UserRepository;
import chandraprasetyo.restful.security.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ValidationServiceImpl validationService;

    @Override
    @Transactional
    public void register(RegisterUserRequest request) {
        validationService.validate(request);

        // checking if username already exist
        if (userRepository.existsById(request.getUsername())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already registered");
        }

        // register or insert into
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
        user.setName(request.getName());
        userRepository.save(user);

    }

    @Override
    public UserResponse get(User user) {
        return UserResponse.builder()
                .username(user.getUsername())
                .name(user.getName())
                .build();
    }

    @Override
    public UserResponse update(User user, UpdateUserRequest request) {
        validationService.validate(request);

        if (Objects.nonNull(request.getName())){
            if (request.getName().trim().isEmpty()){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name cannot be null");
            }
            user.setName(request.getName());
        }

        if (Objects.nonNull(request.getPassword())){
            if (request.getPassword().trim().isEmpty()){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password cannot be null");
            }
            user.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
        }

        userRepository.save(user);
        return UserResponse.builder()
                .name(user.getName())
                .username(user.getUsername())
                .build();
    }

}
