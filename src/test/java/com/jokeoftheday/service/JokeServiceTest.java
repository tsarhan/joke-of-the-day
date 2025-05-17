package com.jokeoftheday.service;

import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.util.ReflectionUtils;

import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jokeoftheday.JokeRepository;
import com.jokeoftheday.model.Joke;
import com.jokeoftheday.model.dto.JokeDTO;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
public class JokeServiceTest {
    
    @Mock
    JokeRepository jokeRepository;

    JokeService jokeService;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void initService() {
        jokeService = new JokeServiceImpl(jokeRepository);
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    public void test_createValidJokeOfTheDay_Success() throws DatabindException, IOException {
        JokeDTO jokeDTO = new JokeDTO(null, "This is how the debug goes",LocalDate.now(), null);
        Joke joke = objectMapper.convertValue(jokeDTO, Joke.class);
        Field field = ReflectionUtils.findRequiredField(Joke.class, "id");
        ReflectionUtils.setField(field, joke, UUID.randomUUID()); 
        when(jokeRepository.existsByJoke(eq(jokeDTO.joke()))).thenReturn(false);
        when(jokeRepository.save(isA(Joke.class))).thenReturn(joke);
        JokeDTO savedJoke = jokeService.createJokeOfTheDay(jokeDTO);
        assertEquals(joke.getId(), savedJoke.id());
        verify(jokeRepository).existsByJoke(eq(jokeDTO.joke()));
        verify(jokeRepository).save(isA(Joke.class));
    }

    @Test
    public void test_createExistingJokeOfTheDay_fail() throws DatabindException, IOException {
        UUID id = UUID.randomUUID();
        JokeDTO jokeDTO = new JokeDTO(id, "This is how the debug goes",LocalDate.now(), null);
        when(jokeRepository.existsByJoke(eq(jokeDTO.joke()))).thenReturn(true);

        assertThrows(EntityExistsException.class, () -> jokeService.createJokeOfTheDay(jokeDTO));
        
        verify(jokeRepository).existsByJoke(eq(jokeDTO.joke()));
        verifyNoMoreInteractions(jokeRepository);
    }

    @Test
    public void test_updateJokeOfTheDay_Success() throws DatabindException, IOException {
        UUID id = UUID.randomUUID();
        JokeDTO jokeDTO = new JokeDTO(id, "This is how the debug goes",LocalDate.now(), null);
        Joke joke = objectMapper.convertValue(jokeDTO, Joke.class);
        when(jokeRepository.getReferenceById(eq(id))).thenReturn(joke);
        jokeService.updateJokeOftheDay(id, jokeDTO);
        verify(jokeRepository).getReferenceById(eq(id));
        verify(jokeRepository).save(isA(Joke.class));
    }

    @Test
    public void test_updateJokeNotFound_Failure() throws DatabindException, IOException {
        UUID id = UUID.randomUUID();
        JokeDTO jokeDTO = new JokeDTO(id, "This is how the debug goes",LocalDate.now(), null);
        when(jokeRepository.getReferenceById(eq(jokeDTO.id()))).thenThrow(EntityNotFoundException.class);

        try {
            jokeService.updateJokeOftheDay(id, jokeDTO);
            fail("Exception should be thrown");
        }catch(Exception e) {
            assertTrue(e instanceof EntityNotFoundException);
        }

        verify(jokeRepository).getReferenceById(eq(id));
        verifyNoMoreInteractions(jokeRepository);
    }

    @Test
    public void test_updateJokePayloadIdParameterIdMismatch_Failure() throws DatabindException, IOException {
        UUID payloadId = UUID.randomUUID();
        UUID parameterId = UUID.randomUUID();
        JokeDTO jokeDTO = new JokeDTO(payloadId, "This is how the debug goes",LocalDate.now(), null);
        
        assertThrows(IllegalArgumentException.class,() -> jokeService.updateJokeOftheDay(parameterId, jokeDTO));
        
        verifyNoInteractions(jokeRepository);
    }

    @Test
    public void test_deleteJokeOfTheDay_Success() throws DatabindException, IOException {
        UUID id = UUID.randomUUID();
        jokeService.deleteJokeById(id);
        verify(jokeRepository).deleteById(eq(id));
    }

     @Test
    public void test_getJokeById_Success() throws DatabindException, IOException {
        UUID id = UUID.randomUUID();
        JokeDTO jokeDTO = new JokeDTO(id, "This is how the debug goes",LocalDate.now(), null);
        Joke joke = objectMapper.convertValue(jokeDTO, Joke.class);
        when(jokeRepository.getReferenceById(eq(id))).thenReturn(joke);
        JokeDTO jokeResult = jokeService.getJokeById(id);
        assertEquals(jokeDTO, jokeResult);
    }

     @Test
    public void test_getJokeByIdNotFound_fail() throws DatabindException, IOException {
        UUID id = UUID.randomUUID();
        when(jokeRepository.getReferenceById(eq(id))).thenThrow(EntityNotFoundException.class);
        assertThrows(EntityNotFoundException.class, () -> jokeService.getJokeById(id));
    }

}
