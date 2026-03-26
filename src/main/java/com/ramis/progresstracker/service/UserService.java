package com.ramis.progresstracker.service;

import com.ramis.progresstracker.dto.UserDTO;
import com.ramis.progresstracker.entity.User;
import com.ramis.progresstracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {
    private final UserRepository userRepository;

    /**
     * Создать нового пользователя
     */
    public UserDTO createUser(String email, String name) {
        // Проверь, нет ли уже такого пользователя
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("User with email " + email + " already exists");
        }

        User user = new User();
        user.setEmail(email);
        user.setName(name);
        user.setLevel(1);
        user.setTotalXP(0);
        user.setMotivationScore(50.0);

        User saved = userRepository.save(user);
        log.info("User created: {}", email);

        return convertToDTO(saved);
    }

    /**
     * Получить пользователя по ID
     */
    public UserDTO getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        return convertToDTO(user);
    }

    /**
     * Получить пользователя по email
     */
    public UserDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));

        return convertToDTO(user);
    }

    /**
     * Обнови профиль пользователя
     */
    public UserDTO updateUser(Long userId, String name) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setName(name);
        User updated = userRepository.save(user);

        return convertToDTO(updated);
    }

    /**
     * Конвертируй Entity в DTO
     */
    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        dto.setLevel(user.getLevel());
        dto.setTotalXP(user.getTotalXP());
        dto.setMotivationScore(user.getMotivationScore());
        dto.setProgressToNextLevel(user.getProgressToNextLevel());
        dto.setXpToNextLevel(user.getXPToNextLevel());

        return dto;
    }
}
