package com.springProject.controller;

import com.springProject.repository.PrefersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PrefersController {

    private final PrefersRepository prefersRepository;


}
