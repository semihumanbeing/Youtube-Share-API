package com.youtubeshareapi.user.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequest {
  @NotBlank(message = "username cannot be empty")
  private String username;
  @NotBlank(message = "password cannot be empty")
  private String password;
  @NotBlank(message = "email cannot be empty")
  private String email;

}
