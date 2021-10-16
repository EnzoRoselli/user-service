package mymarket.user.service;

import mymarket.user.exception.UserNotFoundException;
import mymarket.user.model.User;
import mymarket.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.googlecode.catchexception.apis.BDDCatchException.caughtException;
import static com.googlecode.catchexception.apis.BDDCatchException.when;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.times;
import org.assertj.core.api.BDDAssertions;



@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).role("user").email("fake@gmail.com").build();
    }

    @Test
    public void save_ExpectedValues_Ok(){
        //given
        given(userRepository.save(any())).willReturn(user);

        //when
        User userFromRepository = userService.save(user);

        //then
        then(userRepository).should().save(user);
        assertThat(userFromRepository).isNotNull();
        assertThat(userFromRepository).isEqualTo(user);
    }

    @Test
    public void deleteById_ExpectedValues_Ok(){
        //given
        willDoNothing().given(userRepository).deleteById(anyLong());

        //when
        userService.deleteById(1L);
        userService.deleteById(2L);
        userService.deleteById(3L);

        //then
        then(userRepository).should(times(3)).deleteById(anyLong());
    }

    @Test
    public void getById_ExpectedValues_Ok(){
        //given
        Optional<User> userOptional = Optional.of(user);
        given(userRepository.findById(anyLong())).willReturn(userOptional);

        //when
        User userFromRepository = userService.getById(1L);

        //then
        then(userRepository).should().findById(1L);
        assertThat(userFromRepository).isNotNull();
        assertThat(userFromRepository).isEqualTo(userOptional.get());
    }

    @Test
    public void getById_NonexistentId_UserNotFoundException(){
        //given
        BDDMockito.willThrow(new UserNotFoundException("User with id 1 not found.")).given(userRepository).findById(anyLong());

        //when
        when(() -> userService.getById(1L));

        //then
        BDDAssertions.then(caughtException())
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User with id 1 not found.")
                .hasNoCause();
    }

    @Test
    public void getByEmail_ExpectedValues_Ok(){
        //given
        Optional<User> userOptional = Optional.of(user);
        given(userRepository.findByEmail(anyString())).willReturn(userOptional);

        //when
        User userFromRepository = userService.getByEmail("fake@email.com");

        //then
        then(userRepository).should().findByEmail("fake@email.com");
        assertThat(userFromRepository).isNotNull();
        assertThat(userFromRepository).isEqualTo(userOptional.get());
    }

    @Test
    public void getByEmail_NonexistentId_UserNotFoundException(){
        //given
        willThrow(new UserNotFoundException("User with email fake@email.com not found."))
                .given(userRepository).findByEmail(anyString());

        //when
        when(() -> userService.getByEmail("fake@email.com"));

        //then
        BDDAssertions.then(caughtException())
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User with email fake@email.com not found.")
                .hasNoCause();
    }
}