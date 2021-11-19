package mymarket.user.controller;

import com.amazonaws.xray.spring.aop.XRayEnabled;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import mymarket.user.model.User;
import mymarket.user.service.UserService;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RequestMapping("/users")
@RestController
@Slf4j
@XRayEnabled
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<User> save(@RequestBody User user) {
        return ResponseEntity.created(getLocation(userService.save(user))).build();
    }

    @DeleteMapping("{id}")
    public void deleteById(@PathVariable Long id) {
        userService.deleteById(id);
    }

    @GetMapping("{id}")
    public User getById(@PathVariable Long id) { return userService.getById(id); }

    @GetMapping
    public User getByEmail(@RequestParam("email") String email){ return userService.getByEmail(email); }

    private URI getLocation(User user) {

        return ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(user.getId())
                .toUri();
    }
}
