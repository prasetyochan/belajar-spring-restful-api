package chandraprasetyo.restful.model;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateUserRequest {

    //no need validation cause the Update is optional between name or password

    @Size(max = 100)
    private String name;

    @Size(max = 100)
    private String password;

}
