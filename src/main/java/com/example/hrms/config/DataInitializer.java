package com.example.hrms.config;

import com.example.hrms.entity.Role;
import com.example.hrms.entity.User;
import com.example.hrms.repository.RoleRepository;
import com.example.hrms.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            List<String> roles = List.of(
                    "ROLE_ADMIN",
                    "ROLE_HR",
                    "ROLE_MANAGER",
                    "ROLE_EMPLOYEE"
            );

            for (String roleName : roles) {
                roleRepository.findByRoleName(roleName)
                        .orElseGet(() -> {
                            Role role = new Role();
                            role.setRoleName(roleName);
                            return roleRepository.save(role);
                        });
            }

            if (userRepository.findByEmail("admin@hrms.com").isEmpty()) {

//                Role adminRole = roleRepository.findByRoleName("ROLE_ADMIN")
//                        .orElseThrow(() ->
//                                new IllegalStateException("ROLE_ADMIN not found after initialization"));

                Role adminRole = roleRepository.findByRoleName("ROLE_ADMIN")
                        .orElseThrow(() ->
                                new IllegalStateException("ROLE_ADMIN not found"));

                User admin = new User();
                admin.setEmail("admin@hrms.com");
                admin.setPassword(passwordEncoder.encode("Admin@123"));
                admin.setEnabled(true);
                admin.setRoles(new HashSet<>());
                admin.getRoles().add(
                        roleRepository.findByRoleName("ROLE_ADMIN").orElseThrow()
                );

                userRepository.save(admin);

                System.out.println("Default Admin user created");
            }
        };
    }
}
