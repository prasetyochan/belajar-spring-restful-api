package chandraprasetyo.restful.resolver;

import chandraprasetyo.restful.entity.User;
import chandraprasetyo.restful.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.server.ResponseStatusException;

@Component
@Slf4j
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return User.class.equals(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        //Check if token exist in Header
        HttpServletRequest servletRequest = (HttpServletRequest) webRequest.getNativeRequest();
        String token = servletRequest.getHeader("X-API-TOKEN");
        log.info("TOKEN {}", token);
        if (token == null){
            //if token == null then throw Unauthorized
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
            //if token not null/exist then query check if token exist in Database
            //if no token in Database then throw Unauthorized
        User user = userRepository.findFirstByToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));
        log.info("USER {}", user);
        if (user.getTokenExpiredAt() < System.currentTimeMillis()){
           throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        //if the token exist in database then return User
        return user;
    }
}
