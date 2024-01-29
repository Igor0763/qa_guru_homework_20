package models;

import lombok.Data;

@Data
public class CreateUserBody {

    String email;
    String password;
}
