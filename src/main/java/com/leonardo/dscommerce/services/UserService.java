package com.leonardo.dscommerce.services;

import com.leonardo.dscommerce.DTO.UserDTO;
import com.leonardo.dscommerce.Util.CustomUserUtil;
import com.leonardo.dscommerce.entities.Role;
import com.leonardo.dscommerce.entities.User;
import com.leonardo.dscommerce.projections.UserDetailsProjection;
import com.leonardo.dscommerce.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository repository;

    @Autowired
    private CustomUserUtil userUtil;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<UserDetailsProjection> list = repository.searchUserAndRolesByEmail(username);

        if(list.isEmpty()){
            throw new UsernameNotFoundException("Email not found");
        }

        User user = new User();
        user.setEmail(list.getFirst().getUsername());
        user.setPassword(list.getFirst().getPassword());

        for(UserDetailsProjection projection : list){
            user.addRoles(new Role(projection.getRoleId(), projection.getAuthority()));
        }

        return user;
    }


    protected User authenticated() {
        try {
            String username = userUtil.getLoggedUsername();
            return repository.findByEmail(username).get();
        } catch (Exception e) {
            throw new UsernameNotFoundException("Email not found");
        }
    }

    @Transactional(readOnly = true)
    public UserDTO getMe(){
        User user = authenticated();
        return new UserDTO(user);
    }
}
