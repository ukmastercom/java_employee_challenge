package com.reliaquest.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reliaquest.api.dto.CreateEmployeeRequest;
import com.reliaquest.api.dto.EmployeeDto;
import com.reliaquest.api.service.IEmployeeService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/employee")
public class EmployeeController implements IEmployeeController {
    @Autowired
    private IEmployeeService employeeService;

    @GetMapping
    public ResponseEntity<List<EmployeeDto>> getAllEmployees() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    @GetMapping("/search/{searchString}")
    public ResponseEntity<List<EmployeeDto>> getEmployeesByNameSearch(@PathVariable String searchString) {
        return ResponseEntity.ok(employeeService.getEmployeesByNameSearch(searchString));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDto> getEmployeeById(@PathVariable String id) {
        return employeeService
                .getEmployeeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/highestSalary")
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        return ResponseEntity.ok(employeeService.getHighestSalary());
    }

    @GetMapping("/topTenHighestEarningEmployeeNames")
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        return ResponseEntity.ok(employeeService.getTopTenHighestEarningEmployeeNames());
    }

    @PostMapping
    @Override
    public ResponseEntity<EmployeeDto> createEmployee(@RequestBody Object request) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            CreateEmployeeRequest input = objectMapper.convertValue(request, CreateEmployeeRequest.class);
            return employeeService
                    .createEmployee(input)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.badRequest().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmployeeById(@PathVariable String id) {
        return employeeService.deleteEmployeeById(id)
                ? ResponseEntity.ok("Employee deleted successfully")
                : ResponseEntity.notFound().build();
    }
}
