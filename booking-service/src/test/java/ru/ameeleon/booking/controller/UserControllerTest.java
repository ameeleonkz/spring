package ru.ameeleon.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.ameeleon.booking.dto.UserDto;
import ru.ameeleon.booking.entity.User;
import ru.ameeleon.booking.security.JwtAuthenticationFilter;
import ru.ameeleon.booking.security.JwtUtil;
import ru.ameeleon.booking.service.UserService;
import java.util.Arrays;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private JwtUtil jwtUtil;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private UserService userService;

  @MockBean
  private JwtAuthenticationFilter jwtAuthenticationFilter;

  @Test
  @WithMockUser(username = "admin", roles = {"ADMIN"})
  void getAllUsers_ShouldReturnListOfUsers() throws Exception {
    User user1 = createUser(1L, "user1", "USER");
    User user2 = createUser(2L, "user2", "ADMIN");
    List<User> users = Arrays.asList(user1, user2);

    when(userService.getAllUsers()).thenReturn(users);

    mockMvc.perform(get("/api/users"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].username").value("user1"))
        .andExpect(jsonPath("$[0].role").value("USER"))
        .andExpect(jsonPath("$[1].id").value(2))
        .andExpect(jsonPath("$[1].username").value("user2"))
        .andExpect(jsonPath("$[1].role").value("ADMIN"));

    verify(userService).getAllUsers();
  }

  @Test
  @WithMockUser(username = "admin", roles = {"ADMIN"})
  void getUserById_ShouldReturnUser_WhenUserExists() throws Exception {
    User user = createUser(1L, "testuser", "USER");

    when(userService.getUserById(1L)).thenReturn(user);

    mockMvc.perform(get("/api/users/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.username").value("testuser"))
        .andExpect(jsonPath("$.role").value("USER"));

    verify(userService).getUserById(1L);
  }

  @Test
  @WithMockUser(username = "admin", roles = {"ADMIN"})
  void createUser_ShouldReturnCreatedUser() throws Exception {
    UserDto userDto = new UserDto();
    userDto.setUsername("newuser");
    userDto.setPassword("password123");
    userDto.setRole("USER");

    User createdUser = createUser(1L, "newuser", "USER");

    when(userService.createUser(any(User.class))).thenReturn(createdUser);

    mockMvc.perform(post("/api/users")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(userDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.username").value("newuser"))
        .andExpect(jsonPath("$.role").value("USER"));

    verify(userService).createUser(any(User.class));
  }

  @Test
  @WithMockUser(username = "admin", roles = {"ADMIN"})
  void updateUser_ShouldReturnUpdatedUser() throws Exception {
    UserDto userDto = new UserDto();
    userDto.setId(1L);
    userDto.setUsername("updateduser");
    userDto.setPassword("newpassword");
    userDto.setRole("ADMIN");

    User updatedUser = createUser(1L, "updateduser", "ADMIN");

    when(userService.updateUser(eq(1L), any(User.class))).thenReturn(updatedUser);

    mockMvc.perform(put("/api/users/1")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(userDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.username").value("updateduser"))
        .andExpect(jsonPath("$.role").value("ADMIN"));

    verify(userService).updateUser(eq(1L), any(User.class));
  }

  @Test
  @WithMockUser(username = "admin", roles = {"ADMIN"})
  void deleteUser_ShouldReturnOk_WhenUserDeleted() throws Exception {
    doNothing().when(userService).deleteUser(1L);

    mockMvc.perform(delete("/api/users/1"))
        .andExpect(status().isOk());

    verify(userService).deleteUser(1L);
  }


  private User createUser(Long id, String username, String role) {
    User user = new User();
    user.setId(id);
    user.setUsername(username);
    user.setPassword("password");
    user.setRole(User.Role.valueOf(role));
    return user;
  }
}