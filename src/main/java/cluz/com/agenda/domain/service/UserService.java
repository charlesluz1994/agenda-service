package cluz.com.agenda.domain.service;

import cluz.com.agenda.domain.entity.User;
import cluz.com.agenda.domain.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

	private final UserRepository repository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public UserDetails loadUserByUsername(String user) throws UsernameNotFoundException {
		Optional<User> optUser = repository.findByUser(user);

		if (optUser.isEmpty()) {
			throw new UsernameNotFoundException("User not found");
		}

		User userSaved = optUser.get();
		return new org.springframework.security.core.userdetails.User(userSaved.getUser(), userSaved.getPassword(), new ArrayList<>());
	}

	public List<User> findAll() {
		return repository.findAll();
	}

	public User save(User user) {
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		return repository.save(user);
	}
}
