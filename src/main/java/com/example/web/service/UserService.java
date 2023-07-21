package com.example.web.service;

import com.example.web.dao.RoleRepository;
import com.example.web.dao.UserRepository;
import com.example.web.dto.UserRegisteredDTO;
import com.example.web.model.Role;
import com.example.web.model.User;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {

     private UserRepository userRepository;

     private JavaMailSender javaMailSender;

     private RoleRepository roleRepository;

     private BCryptPasswordEncoder passwordEncoder;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if(user == null) {
            throw new UsernameNotFoundException("Invalid username or password.");
        }
        SimpleMailMessage mail = new SimpleMailMessage();
        System.out.println(user.getEmail());
        mail.setTo(user.getEmail());
        mail.setSubject("Kayıt başarılı hoşgeldiniz");
        mail.setText("Merhaba " + user.getName()  + " web sitemize hoşgeldin :)");

        javaMailSender.send(mail);
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), mapRolesToAuthorities(user.getRole()));
    }
    public User save(UserRegisteredDTO userRegisteredDTO){
        Role role = roleRepository.findByRole("USER");

        User user = new User();
        user.setEmail(userRegisteredDTO.getEmail_id());
        user.setName(userRegisteredDTO.getName());
        user.setPassword(passwordEncoder.encode(userRegisteredDTO.getPassword()));
        user.setRole(role);

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(userRegisteredDTO.getEmail_id());
        mail.setSubject("Kayıt başarılı hoşgeldiniz");
        mail.setText("Merhaba " + userRegisteredDTO.getName() + " web sitemize hoşgeldin :)");

        javaMailSender.send(mail);

        return userRepository.save(user);
    }
    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Set<Role> roles){
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getRole())).collect(Collectors.toList());
    }

}
