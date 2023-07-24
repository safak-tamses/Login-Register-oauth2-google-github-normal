package com.oauth.implementation.service;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.oauth.implementation.dao.RoleRepository;
import com.oauth.implementation.dao.UserRepository;
import com.oauth.implementation.dto.UserRegisteredDTO;
import com.oauth.implementation.model.Role;
import com.oauth.implementation.model.User;


@Service
public class DefaultUserServiceImpl implements DefaultUserService{
   @Autowired
	private UserRepository userRepo;
	
   @Autowired
  	private RoleRepository roleRepo;


   private JavaMailSender javaMailSender;
  	
   
	private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	
	
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
	
		User user = userRepo.findByEmail(email);
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
	
	private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Set<Role> roles){
		return roles.stream().map(role -> new SimpleGrantedAuthority(role.getRole())).collect(Collectors.toList());
	}

	@Override
	public User save(UserRegisteredDTO userRegisteredDTO) {
		Role role = roleRepo.findByRole("USER");
		
		User user = new User();
		user.setEmail(userRegisteredDTO.getEmail_id());
		user.setName(userRegisteredDTO.getName());
		user.setPassword(passwordEncoder.encode(userRegisteredDTO.getPassword()));
		user.setRole(role);

		SimpleMailMessage mail = new SimpleMailMessage();
		mail.setTo(userRegisteredDTO.getEmail_id());
		mail.setSubject("Kayıt başarılı hoşgeldiniz");
		mail.setText("Merhaba " + userRegisteredDTO.getName() + " web sitemize hoşgeldin :)");
		
		return userRepo.save(user);
	}
}
