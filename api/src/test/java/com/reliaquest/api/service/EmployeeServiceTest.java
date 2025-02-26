package com.reliaquest.api.service;

import com.reliaquest.api.dto.CreateEmployeeRequest;
import com.reliaquest.api.dto.EmployeeDto;
import com.reliaquest.api.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {
    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeService employeeService;

    private EmployeeDto employeeDto;
    private CreateEmployeeRequest createRequest;

    @BeforeEach
    void setUp() {
        employeeDto = new EmployeeDto("1", "John Doe", 100000, 30, "Software Engineer", "john.doe@example.com");
        createRequest = new CreateEmployeeRequest("John Doe", 100000, 30, "Software Engineer");
    }

    @Test
    void getAllEmployees_shouldReturnList() {
        when(employeeRepository.fetchAllEmployees()).thenReturn(List.of(employeeDto));
        List<EmployeeDto> result = employeeService.getAllEmployees();
        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).getEmployeeName());
    }

    @Test
    void getEmployeesByNameSearch_shouldReturnFilteredList() {
        when(employeeRepository.fetchEmployeesByName("John"))
                .thenReturn(List.of(employeeDto));
        List<EmployeeDto> result = employeeService.getEmployeesByNameSearch("John");
        assertFalse(result.isEmpty());
        assertEquals("John Doe", result.get(0).getEmployeeName());
    }

    @Test
    void getEmployeeById_shouldReturnEmployee() {
        when(employeeRepository.fetchEmployeeById("1"))
                .thenReturn(Optional.of(employeeDto));
        Optional<EmployeeDto> result = employeeService.getEmployeeById("1");
        assertTrue(result.isPresent());
        assertEquals("John Doe", result.get().getEmployeeName());
    }

    @Test
    void getHighestSalary_shouldReturnSalary() {
        when(employeeRepository.fetchHighestSalary()).thenReturn(100000);
        int salary = employeeService.getHighestSalary();
        assertEquals(100000, salary);
    }

    @Test
    void getTopTenHighestEarningEmployeeNames_shouldReturnList() {
        List<String> names = List.of("John Doe", "Jane Doe");
        when(employeeRepository.fetchTopTenHighestEarningEmployeeNames()).thenReturn(names);
        List<String> result = employeeService.getTopTenHighestEarningEmployeeNames();
        assertEquals(2, result.size());
        assertEquals("John Doe", result.get(0));
    }

    @Test
    void createEmployee_shouldReturnEmployee() {
        when(employeeRepository.createEmployee(createRequest)).thenReturn(Optional.of(employeeDto));
        Optional<EmployeeDto> result = employeeService.createEmployee(createRequest);
        assertTrue(result.isPresent());
        assertEquals("John Doe", result.get().getEmployeeName());
    }

    @Test
    void createEmployee_shouldReturnEmptyIfCreationFails() {
        when(employeeRepository.createEmployee(createRequest)).thenReturn(Optional.empty());
        Optional<EmployeeDto> result = employeeService.createEmployee(createRequest);
        assertFalse(result.isPresent());
    }

    @Test
    void deleteEmployeeById_shouldReturnTrueOnSuccess() {
        when(employeeRepository.deleteEmployeeById("1")).thenReturn(true);
        boolean result = employeeService.deleteEmployeeById("1");
        assertTrue(result);
    }

    @Test
    void deleteEmployeeById_shouldReturnFalseIfNotFound() {
        doThrow(HttpClientErrorException.NotFound.class)
                .when(employeeRepository).deleteEmployeeById("1");
        boolean result = employeeService.deleteEmployeeById("1");
        assertFalse(result);
    }

}
