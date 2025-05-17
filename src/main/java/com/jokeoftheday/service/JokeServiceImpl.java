package com.jokeoftheday.service;

import java.util.Objects;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jokeoftheday.JokeRepository;
import com.jokeoftheday.model.Joke;
import com.jokeoftheday.model.dto.JokeDTO;

import jakarta.persistence.EntityExistsException;

@Service
public class JokeServiceImpl implements JokeService {

    private final JokeRepository jokeRepository;

    private final ObjectMapper objectMapper;

    @Autowired
    public JokeServiceImpl(JokeRepository jokeRepository) {
        this.jokeRepository = jokeRepository;
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public JokeDTO createJokeOfTheDay(JokeDTO jokeDTO) {
        Joke joke = objectMapper.convertValue(jokeDTO, Joke.class);
        if (jokeRepository.existsByJoke(joke.getJoke())) {
            throw new EntityExistsException("Joke already exists");
        } 
        return  objectMapper.convertValue(jokeRepository.save(joke), JokeDTO.class);
    }

    @Override
    public void deleteJokeById(UUID id) {
        jokeRepository.deleteById(id);
    }

    @Override
    public JokeDTO getJokeOfTheDay() {
        return objectMapper.convertValue(jokeRepository.getJokeOfTheDay(), JokeDTO.class);
    }

    @Override
    public void updateJokeOftheDay(UUID id, JokeDTO joke) {
        if(!Objects.equals(id, joke.id())) throw new IllegalArgumentException("Provided Id and Id in payload don't match");
        Joke existingJoke = jokeRepository.getReferenceById(id);
        existingJoke.setDate(joke.date());
        existingJoke.setJoke(joke.description());
        existingJoke.setDescription(joke.description());
        jokeRepository.save(existingJoke);
    }

    @Override
    public JokeDTO getJokeById(UUID id) {
        Joke existingJoke = jokeRepository.getReferenceById(id);
        return objectMapper.convertValue(existingJoke, JokeDTO.class);
    }

}
