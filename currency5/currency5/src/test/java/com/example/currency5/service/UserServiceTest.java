package com.example.currency5.service;

import com.example.currency5.dto.UserBulkDTO;
import com.example.currency5.entity.User;
import com.example.currency5.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User mockUser1;
    private User mockUser2;

    @BeforeEach
    void setUp() {
        mockUser1 = mock(User.class);
        when(mockUser1.getId()).thenReturn(1L);
        when(mockUser1.getUsername()).thenReturn("user1");

        mockUser2 = mock(User.class);
        when(mockUser2.getId()).thenReturn(2L);
        when(mockUser2.getUsername()).thenReturn("user2");
    }

    @Test
    void getAllUsers_ShouldReturnAllUsers() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(mockUser1, mockUser2));
        List<User> result = userService.getAllUsers();
        assertEquals(2, result.size());
        assertTrue(result.contains(mockUser1));
        assertTrue(result.contains(mockUser2));
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getUserById_ShouldReturnUserWhenExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser1));
        Optional<User> result = userService.getUserById(1L);
        assertTrue(result.isPresent());
        assertEquals(mockUser1, result.get());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getUserById_ShouldReturnEmptyWhenNotExists() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        Optional<User> result = userService.getUserById(999L);
        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findById(999L);
    }

    @Test
    void createUser_ShouldSaveAndReturnUser() {
        when(userRepository.save(mockUser1)).thenReturn(mockUser1);
        User result = userService.createUser(mockUser1);
        assertEquals(mockUser1, result);
        verify(userRepository, times(1)).save(mockUser1);
    }

    @Test
    void createBulkUsers_ShouldSaveAllUsers() {
        UserBulkDTO mockBulkDTO = mock(UserBulkDTO.class);
        UserBulkDTO.UserDTO mockUserDTO1 = mock(UserBulkDTO.UserDTO.class);
        when(mockUserDTO1.getUsername()).thenReturn("user1");
        UserBulkDTO.UserDTO mockUserDTO2 = mock(UserBulkDTO.UserDTO.class);
        when(mockUserDTO2.getUsername()).thenReturn("user2");
        when(mockBulkDTO.getUsers()).thenReturn(Arrays.asList(mockUserDTO1, mockUserDTO2));
        when(userRepository.saveAll(anyList())).thenReturn(Arrays.asList(mockUser1, mockUser2));

        List<User> result = userService.createBulkUsers(mockBulkDTO);
        assertEquals(2, result.size());
        assertTrue(result.contains(mockUser1));
        assertTrue(result.contains(mockUser2));
        verify(userRepository, times(1)).saveAll(anyList());
    }

    @Test
    void updateUser_ShouldUpdateAndReturnUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser1));
        when(userRepository.save(mockUser1)).thenReturn(mockUser1);
        User mockUpdatedUser = mock(User.class);
        when(mockUpdatedUser.getId()).thenReturn(1L);
        when(mockUpdatedUser.getUsername()).thenReturn("updated_user");
        User result = userService.updateUser(1L, mockUpdatedUser);
        assertEquals("updated_user", result.getUsername());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(mockUser1);
    }

    @Test
    void updateUser_ShouldThrowExceptionWhenNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> userService.updateUser(999L, mock(User.class)));
        verify(userRepository, times(1)).findById(999L);
        verify(userRepository, never()).save(any());
    }

    @Test
    void deleteUser_ShouldDeleteUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser1));
        userService.deleteUser(1L);
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).delete(mockUser1);
    }

    @Test
    void deleteUser_ShouldThrowExceptionWhenNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> userService.deleteUser(999L));
        verify(userRepository, times(1)).findById(999L);
        verify(userRepository, never()).delete(any());
    }
}