package com.seoulhackerton.mycaps.controller;


import com.seoulhackerton.mycaps.domain.Employee;
import com.seoulhackerton.mycaps.exception.EmployeeNotFoundException;
import com.seoulhackerton.mycaps.payroll.EmployeeRepository;
import com.seoulhackerton.mycaps.service.telegram.CoreTelegramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.util.List;

@RestController
class EmployeeController {

    private final EmployeeRepository repository;

    EmployeeController(EmployeeRepository repository) {
        this.repository = repository;
    }

    @Autowired
    private CoreTelegramService mainService;

    // Aggregate root
    @GetMapping("/employees")
    List<Employee> all() {
        String text = "Are you on Way to go Daiso ???????????";
        sendTelegram(text);
        return repository.findAll();
    }

    private void sendTelegram(String text) {
        String url = "https://api.telegram.org/bot818348795:AAE3-dC2J1POYDmss1JZHURDgP_R5wqx4m0/sendMessage?chat_id=727848241&text=";
        String sb = url + URLEncoder.encode(text);
        mainService.sendMsg(sb);
    }

    @PostMapping("/employees")
    Employee newEmployee(@RequestBody Employee newEmployee) {
        return repository.save(newEmployee);
    }

    // Single item
    @GetMapping("/employees/{id}")
    Employee one(@PathVariable Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));
    }

    @PutMapping("/employees/{id}")
    Employee replaceEmployee(@RequestBody Employee newEmployee, @PathVariable Long id) {
        return repository.findById(id)
                .map(employee -> {
                    employee.setName(newEmployee.getName());
                    employee.setRole(newEmployee.getRole());
                    return repository.save(employee);
                })
                .orElseGet(() -> {
                    newEmployee.setId(id);
                    return repository.save(newEmployee);
                });
    }

    @DeleteMapping("/employees/{id}")
    void deleteEmployee(@PathVariable Long id) {
        repository.deleteById(id);
    }

    void test() {
    }
}
