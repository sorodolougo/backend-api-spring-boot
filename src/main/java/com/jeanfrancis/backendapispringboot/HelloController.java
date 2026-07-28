package com.jeanfrancis.backendapispringboot;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "http://localhost:4200") // Pour autoriser Angular plus tard
public class HelloController {

    @GetMapping("/api/hello")
    public String hello() {
        return "Backend OK, prêt pour Angular !";
    }
}