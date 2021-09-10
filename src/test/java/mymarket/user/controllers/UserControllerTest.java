package mymarket.user.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import mymarket.user.exceptions.UserNotFoundException;
import mymarket.user.models.User;
import mymarket.user.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private User user;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new ExceptionController())
                .build();

        user = User.builder().id(1L).role("user").email("fake@gmail.com").build();
    }

    @Test
    public void save_ExpectedValues_Ok() throws Exception {
        //given
        given(userService.save(any())).willReturn(user);

        //when
        MockHttpServletResponse response = mockMvc.perform(post("/users/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(user))
                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        //then
        then(userService).should().save(user);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON.toString());
        assertThat(response.getContentAsString()).isEqualTo(asJsonString(user));
    }

    @Test
    public void save_MissingValues_DataIntegrityViolationException() throws Exception {
        //given
        given(userController.save(any())).willThrow(new DataIntegrityViolationException(""));

        //when
        MockHttpServletResponse response = mockMvc.perform(post("/users/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(user))
                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        //then
        then(userService).should().save(user);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void deleteById_ExpectedValues_Ok() throws Exception {
        //given
        willDoNothing().given(userService).deleteById(anyLong());

        //when
        MockHttpServletResponse response = mockMvc.perform(delete("/users/4")
                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        //then
        then(userService).should().deleteById(4L);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    public void deleteById_NonexistentId_EmptyResultDataAccessException() throws Exception {
        //given
        willThrow(new EmptyResultDataAccessException(0)).given(userService).deleteById(anyLong());

        //when
        MockHttpServletResponse response = mockMvc.perform(delete("/users/150")
                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        //then
        then(userService).should().deleteById(150L);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void getById_ExpectedValues_Ok() throws Exception {
        //given
        given(userService.getById(anyLong())).willReturn(user);

        //when
        MockHttpServletResponse response = mockMvc.perform(get("/users/4")
                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        //then
        then(userService).should().getById(4L);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(asJsonString(user));
    }

    @Test
    public void getById_NonexistentId_UserNotFoundException() throws Exception {
        //given
        BDDMockito.willThrow(new UserNotFoundException("")).given(userService).getById(anyLong());

        //when
        MockHttpServletResponse response = mockMvc.perform(get("/users/150")
                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        //then
        then(userService).should().getById(150L);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void getByEmail_ExpectedValues_Ok() throws Exception {
        //given
        given(userService.getByEmail(anyString())).willReturn(user);

        //when
        MockHttpServletResponse response = mockMvc.perform(get("/users?email=fake@email.com")
                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        //then
        then(userService).should().getByEmail("fake@email.com");
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(asJsonString(user));
    }

    @Test
    public void getByEmail_NonexistentEmail_UserNotFoundException() throws Exception {
        //given
        willThrow(new UserNotFoundException("")).given(userService).getByEmail(anyString());

        //when
        MockHttpServletResponse response = mockMvc.perform(get("/users?email=random@email.com")
                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        //then
        then(userService).should().getByEmail("random@email.com");
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
