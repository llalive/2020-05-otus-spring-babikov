package dev.lochness.library.controller;

import dev.lochness.library.domain.Author;
import dev.lochness.library.service.LibraryService;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthorControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private LibraryService libraryService;

    @Test
    @WithMockUser(
            username = "guest",
            authorities = {"GUEST"}
    )
    public void shouldReturnAllAuthors() throws Exception {
        given(libraryService.getAuthors()).willReturn(List.of(new Author(1L, "Alexander", "Pushkin"),
                new Author(2L, "Chuck", "Palahniuk")));
        this.mvc.perform(MockMvcRequestBuilders.get("/api/authors"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(new BaseMatcher<>() {
                    @Override
                    public boolean matches(Object o) {
                        String s = (String) o;
                        return s.contains("Pushkin") && s.contains("Palahniuk");
                    }

                    @Override
                    public void describeTo(Description description) {
                    }
                }));
    }

    @Test
    @WithAnonymousUser
    public void shouldDenyAccessToAuthors() throws Exception {
        this.mvc.perform(MockMvcRequestBuilders.get("/api/authors"))
                .andExpect(status().isFound());
    }
}