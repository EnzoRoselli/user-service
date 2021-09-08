package tesis.user.controllers;

import com.amazonaws.xray.spring.aop.XRayEnabled;
import com.sun.istack.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tesis.user.models.User;
import tesis.user.repositories.UserRepository;

@RequestMapping("users")
@RestController
@Slf4j
@XRayEnabled
@RequiredArgsConstructor
public class UserController {

    private final UserRepository repo;

    @PostMapping
    public User save(@RequestBody @NotNull User user) {
        return repo.save(user);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable("id") Long id) {
        repo.deleteById(id);
    }

    @GetMapping("{id}")
    public User getById(@PathVariable("id") Long id) {
        return repo.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }

    @GetMapping("email/{email}")
    public Long getIdByEmail(@PathVariable String email){
        return repo.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found")).getId();
    }
}
