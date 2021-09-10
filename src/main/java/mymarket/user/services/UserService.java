package mymarket.user.services;

import lombok.RequiredArgsConstructor;
import mymarket.user.repositories.UserRepository;
import org.springframework.stereotype.Service;
import mymarket.user.exceptions.UserNotFoundException;
import mymarket.user.models.User;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User save(User user){ return userRepository.save(user); }

    public void deleteById(Long id){ userRepository.deleteById(id); }

    public User getById(Long id){
        return userRepository.findById(id).orElseThrow(() ->
                new UserNotFoundException("User with id " + id + " not found."));
    }

    public User getByEmail(String email){
        return userRepository.findByEmail(email).orElseThrow(() ->
                new UserNotFoundException("User with email " + email + " not found."));
    }
}
