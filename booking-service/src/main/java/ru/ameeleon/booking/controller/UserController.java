package ru.ameeleon.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.ameeleon.booking.dto.UserDto;
import ru.ameeleon.booking.entity.User;
import ru.ameeleon.booking.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
  private final UserService userService;

  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<List<UserDto>> getAllUsers() {
    List<User> users = userService.getAllUsers();
    List<UserDto> dtos = users.stream()
        .map(this::convertToDto)
        .collect(Collectors.toList());
    return ResponseEntity.ok(dtos);
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
    User user = userService.getUserById(id);
    return ResponseEntity.ok(convertToDto(user));
  }

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<UserDto> createUser(@RequestBody UserDto userDto) {
    User user = convertToEntity(userDto);
    User created = userService.createUser(user);
    return ResponseEntity.ok(convertToDto(created));
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @RequestBody UserDto userDto) {
    User user = convertToEntity(userDto);
    User updated = userService.updateUser(id, user);
    return ResponseEntity.ok(convertToDto(updated));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
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