package com.leonardo.dscommerce.services;

import com.leonardo.dscommerce.DTO.UserDTO;
import com.leonardo.dscommerce.Factory;
import com.leonardo.dscommerce.Util.CustomUserUtil;
import com.leonardo.dscommerce.entities.Role;
import com.leonardo.dscommerce.entities.User;
import com.leonardo.dscommerce.projections.UserDetailsProjection;
import com.leonardo.dscommerce.repositories.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
public class UserServiceTests {

    @InjectMocks
    private UserService service;

    @Mock
    private UserRepository repository;

    @Mock
    private CustomUserUtil userUtil;

    private String existingUsername, nonExistingUsername;
    private User user;
    private List<UserDetailsProjection> userDetails;

    @BeforeEach
    void setUp() throws Exception{
        existingUsername = "alex@gmail.com";
        nonExistingUsername = "robert@gmail.com";
        user = Factory.createClient();
        userDetails = Factory.createCustomClientUserDetails(existingUsername);

        Mockito.when(repository.searchUserAndRolesByEmail(existingUsername)).thenReturn(userDetails);
        Mockito.when(repository.searchUserAndRolesByEmail(nonExistingUsername)).thenReturn(new ArrayList<>());

        Mockito.when(repository.findByEmail(existingUsername)).thenReturn(Optional.of(user));
        Mockito.when(repository.findByEmail(nonExistingUsername)).thenReturn(Optional.empty());
    }

    @Test
    public void loadUserByUsernameShouldReturnUserWhenUsernameExists(){
        UserDetails result = service.loadUserByUsername(existingUsername);

        Mockito.verify(repository).searchUserAndRolesByEmail(existingUsername);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(existingUsername, result.getUsername());
        Assertions.assertEquals("$2a$10$XZHsZ0yuw1IkMbwcTKQHiet8JQB3bAdV0KjcU9Bg6zAHbrAmOFlEO", result.getPassword());
    }

    @Test
    public void loadUserByUsernameShouldThrowUsernameNotFoundWhenUsernameDoesNotExists(){
        Assertions.assertThrows(UsernameNotFoundException.class, () -> {
            service.loadUserByUsername(nonExistingUsername);
        });
    }

    @Test
    public void authenticatedShouldReturnUserWhenUserExists(){
        Mockito.when(userUtil.getLoggedUsername()).thenReturn(existingUsername);

        User result = service.authenticated();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(existingUsername, result.getUsername());
    }

    @Test
    public void authenticatedShouldThrowUsernameNotFoundWhenUserDoesNotExists(){
        Mockito.doThrow(ClassCastException.class).when(userUtil).getLoggedUsername();

        Assertions.assertThrows(UsernameNotFoundException.class, () -> {
            service.authenticated();
        });

    }

    @Test
    public void getMeShouldReturnUserDTOWhenUserIsAuthenticated(){
        UserService serviceSpy = Mockito.spy(service);
        Mockito.when(userUtil.getLoggedUsername()).thenReturn(existingUsername);
        Mockito.doReturn(user).when(serviceSpy).authenticated();
        UserDTO result = serviceSpy.getMe();

        Assertions.assertNotNull(result);
        Assertions.assertEquals("Alex", result.getName());
        Assertions.assertEquals("alex@gmail.com", result.getEmail());
        Assertions.assertEquals("ROLE_OPERATOR", result.getRoles().getFirst());
    }


    @Test
    public void getMeShouldThrowUsernameNotFoundWhenUserIsNotAuthenticated(){
        UserService serviceSpy = Mockito.spy(service);
        Mockito.doThrow(UsernameNotFoundException.class).when(serviceSpy).authenticated();

        Assertions.assertThrows(UsernameNotFoundException.class, serviceSpy::getMe);

    }

}
