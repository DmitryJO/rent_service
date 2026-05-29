package ru.dmsmirnov.rent.api.controller.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "Регистрация и аутентификация")
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Operation(summary = "Регистрация пользователя")
    @ApiResponse(responseCode = "200", description = "Успешная регистрация")
    @PostMapping("/register")
    public ResponseEntity<Void> register() {
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Вход в систему")
    @ApiResponse(responseCode = "200", description = "Успешная аутентификация")
    @PostMapping("/login")
    public ResponseEntity<Void> login() {
        return ResponseEntity.ok().build();
    }

}
