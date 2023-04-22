package com.group.libraryapp.controller.calculator;

import com.group.libraryapp.dto.calculator.request.CalculatorAddrequest;
import com.group.libraryapp.dto.calculator.request.CalculatorMultiplyrequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CalculatorController {

//    @GetMapping("/add")
//    public int addTwoNumbers(
//            @RequestParam int number1,
//            @RequestParam int number2
//    ) {
//        return number1 + number2;
//    }

    @GetMapping("/add")
    public int addTwoNumbers(CalculatorAddrequest request) {
        return request.getNumber1() + request.getNumber2();
    }

    @PostMapping("/multiply")
    public int multiplyTwoNumbers(@RequestBody CalculatorMultiplyrequest request) {
        return request.getNumber1() * request.getNumber2();
    }
}
