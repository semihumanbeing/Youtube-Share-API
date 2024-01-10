package com.youtubeshareapi.user.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
  @NotBlank(message = "email cannot be empty")
  String email;
  @NotBlank(message = "password cannot be empty")
  String password;

}
