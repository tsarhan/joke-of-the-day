package com.jokeoftheday;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.jokeoftheday.model.Joke;

@Repository
public interface JokeRepository extends JpaRepository<Joke, UUID>{
    @Query(value = "SELECT * FROM joke ORDER BY RAND() limit 1", nativeQuery = true)
    Joke getJokeOfTheDay();
    boolean existsByJoke(String joke);
}
