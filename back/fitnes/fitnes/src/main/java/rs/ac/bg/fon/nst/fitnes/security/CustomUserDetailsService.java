/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rs.ac.bg.fon.nst.fitnes.security;

import java.util.Collections;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import rs.ac.bg.fon.nst.fitnes.domain.User;
import rs.ac.bg.fon.nst.fitnes.repo.UserRepository;


@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Korisnik sa emailom " + email + " nije pronađen."));

        
         GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().getRole().toUpperCase());
     

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                 Collections.singletonList(authority)
        );
    }
}
