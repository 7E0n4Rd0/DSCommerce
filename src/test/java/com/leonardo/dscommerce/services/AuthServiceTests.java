package com.leonardo.dscommerce.services;

import com.leonardo.dscommerce.Factory;
import com.leonardo.dscommerce.entities.User;
import com.leonardo.dscommerce.services.exceptions.ForbiddenException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.Assert;

@ExtendWith(SpringExtension.class)
public class AuthServiceTests {

    @InjectMocks
    private AuthService service;

    @Mock
    private UserService userService;

    private User selfClient, otherClient, admin;

    @BeforeEach
    void setUp() throws Exception{
        admin = Factory.createAdmin();
        selfClient = Factory.createCustomClient(3L, "Arthur Morgan");
        otherClient = Factory.createCustomClient(2L, "Master Chief");



    }

    @Test
    public void validateSelfOrAdminShouldDoNothingWhenAdminLogged(){
        Mockito.when(userService.authenticated()).thenReturn(admin);

        Long userId = admin.getId();

        Assertions.assertDoesNotThrow(() -> service.validateSelfOrAdmin(userId));

    }

    @Test
    public void validateSelfOrAdminShouldDoNothingWhenSelfClientLogged(){
        Mockito.when(userService.authenticated()).thenReturn(selfClient);

        Long userId = selfClient.getId();

        Assertions.assertDoesNotThrow(() -> service.validateSelfOrAdmin(userId));
    }

    @Test
    public void validateSelfOrAdminShouldThrowForbiddenWhenOtherClientLogged(){
        Mockito.when(userService.authenticated()).thenReturn(selfClient);

        Long userId = otherClient.getId();

        Assertions.assertThrows(ForbiddenException.class, () -> service.validateSelfOrAdmin(userId));
    }

}
