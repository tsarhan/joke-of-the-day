package com.jokeoftheday.model.dto;

import java.time.LocalDate;
import java.util.UUID;

import com.jokeoftheday.validation.OnCreate;
import com.jokeoftheday.validation.OnUpdate;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;

public record JokeDTO(@NotNull(groups={OnUpdate.class}) @Null(groups={OnCreate.class})UUID id, @NotBlank(groups={OnCreate.class, OnUpdate.class}) String joke, @NotNull(groups={OnCreate.class, OnUpdate.class}) @FutureOrPresent(groups={OnCreate.class, OnUpdate.class}) LocalDate date, String description){}