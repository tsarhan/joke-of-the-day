package com.jokeoftheday.controller;

import java.time.LocalDate;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jokeoftheday.model.dto.JokeDTO;
import com.jokeoftheday.service.JokeService;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(SpringExtension.class)
@WebMvcTest(JokeController.class)
public class JokeControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    JokeService service;

    ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    public void init() {
        mapper.registerModule(new JavaTimeModule());
    }

    @Test
    public void test_createValidJokeOfTheDay_Success() throws Exception {
        JokeDTO joke = new JokeDTO(null, "This is how the debug goes", LocalDate.now(), null);
        JokeDTO createdJoke = new JokeDTO(UUID.randomUUID(), joke.joke(), joke.date(), null);

        given(service.createJokeOfTheDay(eq(joke))).willReturn(createdJoke);
        mvc.perform(post("/jokeoftheday").content(mapper.writeValueAsBytes(joke))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location", containsString("/"+createdJoke.id())));
    }

    @Test
    public void test_createJokeWithId_fail() throws Exception {
        JokeDTO joke = new JokeDTO(UUID.randomUUID(), "This is how the debug goes", LocalDate.now(), null);
        
        mvc.perform(post("/jokeoftheday").content(mapper.writeValueAsBytes(joke))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void test_createJokeMissingJoke_fail() throws Exception {
        JokeDTO joke = new JokeDTO(null, null, LocalDate.now(), null);
        mvc.perform(post("/jokeoftheday").content(mapper.writeValueAsBytes(joke))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void test_createJokeMissingDate_fail() throws Exception {
        JokeDTO joke = new JokeDTO(null, "This is how the debug goes",null, null);
        mvc.perform(post("/jokeoftheday").content(mapper.writeValueAsBytes(joke))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void test_getJoke_success() throws Exception {
        UUID id = UUID.randomUUID();
        JokeDTO jokeDTO = new JokeDTO(id, "Why did the Software Engineer Cross the road? To see if a car accident can be reproduced",LocalDate.now(), null);
        given(service.getJokeOfTheDay()).willReturn(jokeDTO);
        mvc.perform(get("/jokeoftheday"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Is.is(id.toString())))
                .andExpect(jsonPath("$.joke", Is.is("Why did the Software Engineer Cross the road? To see if a car accident can be reproduced")));
    }

    @Test
    public void test_deleteJoke_success() throws Exception {
        mvc.perform(delete("/jokeoftheday/{id}", "2bcb772c-64d3-433e-9c32-0229a36f78d8"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void test_updateJoke_success() throws Exception {
        JokeDTO joke = new JokeDTO(UUID.fromString("2bcb772c-64d3-433e-9c32-0229a36f78d8"), "This is how the debug goes",LocalDate.now(), null);
        mvc.perform(put("/jokeoftheday/{id}", "2bcb772c-64d3-433e-9c32-0229a36f78d8")
                .content(mapper.writeValueAsBytes(joke))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    public void test_updateJokeMissingIdInPayload_fail() throws Exception {
        JokeDTO joke = new JokeDTO(null, "This is how the debug goes",LocalDate.now(), null);
        mvc.perform(put("/jokeoftheday/{id}", "2bcb772c-64d3-433e-9c32-0229a36f78d8")
                .content(mapper.writeValueAsBytes(joke))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void test_updateJokeNotFound_fail() throws Exception {
        UUID id = UUID.randomUUID();
        JokeDTO joke = new JokeDTO(id, "This is how the debug goes",LocalDate.now(), null);
        willThrow(EntityNotFoundException.class).given(service).updateJokeOftheDay(eq(id), eq(joke));
        mvc.perform(put("/jokeoftheday/{id}", id.toString())
                .content(mapper.writeValueAsBytes(joke))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

     @Test
    public void test_updateJokeIdMismatch_fail() throws Exception {
        UUID id = UUID.randomUUID();
        JokeDTO joke = new JokeDTO(UUID.randomUUID(), "This is how the debug goes",LocalDate.now(), null);
        willThrow(IllegalArgumentException.class).given(service).updateJokeOftheDay(eq(id), eq(joke));
        mvc.perform(put("/jokeoftheday/{id}", id.toString())
                .content(mapper.writeValueAsBytes(joke))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void test_getJokeById_success() throws Exception {
        UUID id = UUID.randomUUID();
        JokeDTO jokeDTO = new JokeDTO(id, "Why did the Software Engineer Cross the road? To see if a car accident can be reproduced",LocalDate.now(), null);
        given(service.getJokeById(eq(id))).willReturn(jokeDTO);

        mvc.perform(get("/jokeoftheday/{id}", id.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Is.is(id.toString())))
                .andExpect(jsonPath("$.joke", Is.is("Why did the Software Engineer Cross the road? To see if a car accident can be reproduced")));
    }

    @Test
    public void test_getJokeByNonExistantId_fail() throws Exception {
        UUID id = UUID.randomUUID();
    
        willThrow(EntityNotFoundException.class).given(service).getJokeById(eq(id));
        
        mvc.perform(get("/jokeoftheday/{id}", id.toString()))
                .andExpect(status().isNotFound());
    }
}
