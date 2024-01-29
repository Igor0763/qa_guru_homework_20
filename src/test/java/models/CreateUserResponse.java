package models;

import lombok.Data;

@Data
public class CreateUserResponse {
    String token;
    Integer id;
}
