package com.jokeoftheday.service;

import java.util.UUID;

import com.jokeoftheday.model.dto.JokeDTO;

public interface JokeService {
    void createJokeOfTheDay(JokeDTO joke);
    JokeDTO getJokeOfTheDay();
    void updateJokeOftheDay(UUID id, JokeDTO joke);
    void deleteJokeById(UUID id);
}
