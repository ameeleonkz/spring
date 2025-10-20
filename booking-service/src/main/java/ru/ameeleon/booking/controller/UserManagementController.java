package ru.ameeleon.booking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.ameeleon.booking.dto.UserDto;
import ru.ameeleon.booking.entity.User;
import ru.ameeleon.booking.service.UserService;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserManagementController {
    private final UserService userService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto userDto) {
        User user = convertToEntity(userDto);
        User created = userService.createUser(user);
        return ResponseEntity.ok(convertToDto(created));
    }

    @PatchMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> updateUser(@Valid @RequestBody UserDto userDto) {
        if (userDto.getId() == null) {
            throw new RuntimeException("User ID is required for update");
        }
        User user = convertToEntity(userDto);
        User updated = userService.updateUser(userDto.getId(), user);
        return ResponseEntity.ok(convertToDto(updated));
    }

    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@RequestParam Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    private UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setRole(user.getRole().name());
        return dto;
    }

    private User convertToEntity(UserDto dto) {
        User user = new User();
        user.setId(dto.getId());
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        if (dto.getRole() != null) {
            user.setRole(User.Role.valueOf(dto.getRole()));
        }
        return user;
    }
}