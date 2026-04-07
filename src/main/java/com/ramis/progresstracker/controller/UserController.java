package com.ramis.progresstracker.controller;

import com.ramis.progresstracker.dto.CreateUserRequest;
import com.ramis.progresstracker.dto.UpdateUserRequest;
import com.ramis.progresstracker.dto.UserDTO;
import com.ramis.progresstracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin("*")
public class UserController {

    private final UserService userService;

    /**
     * Создать нового пользователя
     * POST /api/users
     */
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody CreateUserRequest request) {
        UserDTO user = userService.createUser(request.email(), request.name());
        return ResponseEntity.ok(user);
    }

    /**
     * Получить пользователя по ID
     * GET /api/users/{userId}
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long userId) {
        UserDTO user = userService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    /**
     * Получить пользователя по email
     * GET /api/users/email/{email}
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<UserDTO> getUserByEmail(@PathVariable String email) {
        UserDTO user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    /**
     * Обновить профиль
     * PUT /api/users/{userId}
     */
    @PutMapping("/{userId}")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable Long userId,
            @RequestBody UpdateUserRequest request) {
        UserDTO user = userService.updateUser(userId, request.name());
        return ResponseEntity.ok(user);
    }
}
