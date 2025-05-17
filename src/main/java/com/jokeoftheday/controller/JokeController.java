package com.jokeoftheday.controller;

import java.net.URI;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.jokeoftheday.model.dto.JokeDTO;
import com.jokeoftheday.service.JokeService;
import com.jokeoftheday.validation.OnCreate;
import com.jokeoftheday.validation.OnUpdate;

@RestController
@RequestMapping("/jokeoftheday")
public class JokeController {

    private final JokeService jokeService;
    
    @Autowired
    public JokeController(JokeService jokeService) {
        this.jokeService = jokeService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> createJokeOfTheDay(@RequestBody  @Validated(OnCreate.class)  JokeDTO joke) {
        JokeDTO jokeDTO = jokeService.createJokeOfTheDay(joke);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(jokeDTO.id().toString())
                .toUri();
        return ResponseEntity.created(location).body(jokeDTO);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public JokeDTO getJokeOfTheDay() {
        return jokeService.getJokeOfTheDay();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public JokeDTO getJokeById(@PathVariable UUID id) {
        return jokeService.getJokeById(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public void updateJokeOftheDay(@PathVariable UUID id, @RequestBody @Validated(OnUpdate.class) JokeDTO jokeDTO) {
        jokeService.updateJokeOftheDay(id, jokeDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteJoke(@PathVariable UUID id) {
        jokeService.deleteJokeById(id);
    }
}
