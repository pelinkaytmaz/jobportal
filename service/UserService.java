package com.dauphine.jobportal.service;

import com.dauphine.jobportal.model.User;
import com.dauphine.jobportal.repository.UserRepository;
import com.dauphine.jobportal.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

        private final UserRepository userRepository;

        @Override
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                User user = userRepository.findByUsername(username)
                                .orElseThrow(() -> new UsernameNotFoundException(
                                                "Utilisateur non trouvé avec le nom : " + username));

                return UserDetailsImpl.build(user);
        }
}
