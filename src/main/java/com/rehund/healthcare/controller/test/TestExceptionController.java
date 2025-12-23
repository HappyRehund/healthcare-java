package com.rehund.healthcare.controller.test;

import com.rehund.healthcare.common.exception.BadRequestException;
import com.rehund.healthcare.common.exception.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestExceptionController {

    @GetMapping("/generic-error")
    public ResponseEntity<String> genericError(){
        throw new RuntimeException("This is a generic exception for testing purposes");
    }

    @GetMapping("/bad-request")
    public ResponseEntity<String> badRequestError(){
        throw new BadRequestException("This is a bad request exception for testing purposes");
    }

    @GetMapping("not-found")
    public ResponseEntity<String> notFoundError(){
        throw new ResourceNotFoundException("This is a resource not found exception for testing purposes");
    }
}
