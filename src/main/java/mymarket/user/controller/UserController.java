package mymarket.user.controller;

import com.amazonaws.xray.spring.aop.XRayEnabled;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import mymarket.user.model.User;
import mymarket.user.service.UserService;

@RequestMapping("/users")
@RestController
@Slf4j
@XRayEnabled
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public User save(@RequestBody User user) {
        return userService.save(user);
    }

    @DeleteMapping("{id}")
    public void deleteById(@PathVariable Long id) {
        userService.deleteById(id);
    }

    @GetMapping("{id}")
    public User getById(@PathVariable Long id) { return userService.getById(id); }

    @GetMapping
    public User getByEmail(@RequestParam("email") String email){ return userService.getByEmail(email); }
}
