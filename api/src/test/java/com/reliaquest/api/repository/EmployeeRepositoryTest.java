package com.reliaquest.api.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reliaquest.api.dto.CreateEmployeeRequest;
import com.reliaquest.api.dto.EmployeeDto;
import com.reliaquest.api.repository.EmployeeRepository.EmployeeListResponse;
import com.reliaquest.api.repository.EmployeeRepository.EmployeeResponse;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
public class EmployeeRepositoryTest {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private MockRestServiceServer mockServer;
    private EmployeeRepository employeeRepository;

    @BeforeEach
    void setUp() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
        employeeRepository = new EmployeeRepository(restTemplate);
    }

    @Test
    void testFetchAllEmployees() throws Exception {
        EmployeeDto employee = new EmployeeDto("1", "John Doe", 50000, 30, "Engineer", "john.doe@example.com");
        EmployeeListResponse response = new EmployeeListResponse();
        response.data = List.of(employee);

        mockServer.expect(requestTo("http://localhost:8112/api/v1/employee"))
                .andRespond(withSuccess(objectMapper.writeValueAsString(response), MediaType.APPLICATION_JSON));

        List<EmployeeDto> employees = employeeRepository.fetchAllEmployees();
        assertFalse(employees.isEmpty());
        assertEquals(1, employees.size());
        assertEquals("John Doe", employees.get(0).getEmployeeName());
    }

    @Test
    void testFetchEmployeeById() throws Exception {
        EmployeeDto employee = new EmployeeDto("1", "John Doe", 50000, 30, "Engineer", "john.doe@example.com");
        EmployeeResponse response = new EmployeeResponse();
        response.data = employee;

        mockServer.expect(requestTo("http://localhost:8112/api/v1/employee/1"))
                .andRespond(withSuccess(objectMapper.writeValueAsString(response), MediaType.APPLICATION_JSON));

        Optional<EmployeeDto> result = employeeRepository.fetchEmployeeById("1");
        assertTrue(result.isPresent());
        assertEquals("John Doe", result.get().getEmployeeName());
    }

    @Test
    void testCreateEmployee() throws Exception {
        CreateEmployeeRequest request = new CreateEmployeeRequest("John Doe", 50000, 30, "Engineer");
        EmployeeDto employee = new EmployeeDto("1", "John Doe", 50000, 30, "Engineer", "john.doe@example.com");
        EmployeeResponse response = new EmployeeResponse();
        response.data = employee;

        mockServer.expect(requestTo("http://localhost:8112/api/v1/employee"))
                .andExpect(method(org.springframework.http.HttpMethod.POST))
                .andRespond(withSuccess(objectMapper.writeValueAsString(response), MediaType.APPLICATION_JSON));

        Optional<EmployeeDto> result = employeeRepository.createEmployee(request);
        assertTrue(result.isPresent());
        assertEquals("John Doe", result.get().getEmployeeName());
    }

    @Test
    void testDeleteEmployeeById() throws Exception {
        EmployeeDto employee = new EmployeeDto("1", "John Doe", 50000, 30, "Engineer", "john.doe@example.com");
        EmployeeResponse response = new EmployeeResponse();
        response.data = employee;

        // Expect a GET request to fetch the employee
        mockServer.expect(requestTo("http://localhost:8112/api/v1/employee/1"))
                .andExpect(method(org.springframework.http.HttpMethod.GET))
                .andRespond(withSuccess(objectMapper.writeValueAsString(response), MediaType.APPLICATION_JSON));

        // Expect a DELETE request to remove the employee
        mockServer.expect(requestTo("http://localhost:8112/api/v1/employee"))
                .andExpect(method(org.springframework.http.HttpMethod.DELETE))
                .andRespond(withNoContent());

        boolean result = employeeRepository.deleteEmployeeById("1");
        assertTrue(result);
    }

    @Test
    void testFetchHighestSalary() throws Exception {
        EmployeeListResponse response = new EmployeeListResponse();
        response.data = List.of(
                new EmployeeDto("1", "John Doe", 70000, 30, "Engineer", "john.doe@example.com"),
                new EmployeeDto("2", "Jane Doe", 90000, 28, "Manager", "jane.doe@example.com"),
                new EmployeeDto("3", "Jim Beam", 85000, 35, "Analyst", "jim.beam@example.com")
        );

        mockServer.expect(requestTo("http://localhost:8112/api/v1/employee"))
                .andRespond(withSuccess(objectMapper.writeValueAsString(response), MediaType.APPLICATION_JSON));

        int highestSalary = employeeRepository.fetchHighestSalary();
        assertEquals(90000, highestSalary);
    }

    @Test
    void testFetchTopTenHighestEarningEmployeeNames() throws Exception {
        EmployeeListResponse response = new EmployeeListResponse();
        response.data = List.of(
                new EmployeeDto("1", "John Doe", 70000, 30, "Engineer", "john.doe@example.com"),
                new EmployeeDto("2", "Jane Doe", 90000, 28, "Manager", "jane.doe@example.com"),
                new EmployeeDto("3", "Jim Beam", 85000, 35, "Analyst", "jim.beam@example.com")
        );

        mockServer.expect(requestTo("http://localhost:8112/api/v1/employee"))
                .andRespond(withSuccess(objectMapper.writeValueAsString(response), MediaType.APPLICATION_JSON));

        List<String> topEarnerNames = employeeRepository.fetchTopTenHighestEarningEmployeeNames();
        assertEquals(List.of("Jane Doe", "Jim Beam", "John Doe"), topEarnerNames);
    }

    @Test
    void testFetchEmployeesByName() throws Exception {
        EmployeeListResponse response = new EmployeeListResponse();
        response.data = List.of(
                new EmployeeDto("1", "John Doe", 70000, 30, "Engineer", "john.doe@example.com"),
                new EmployeeDto("2", "Jane Doe", 90000, 28, "Manager", "jane.doe@example.com"),
                new EmployeeDto("3", "Jim Beam", 85000, 35, "Analyst", "jim.beam@example.com")
        );

        mockServer.expect(requestTo("http://localhost:8112/api/v1/employee"))
                .andRespond(withSuccess(objectMapper.writeValueAsString(response), MediaType.APPLICATION_JSON));

        List<EmployeeDto> employees = employeeRepository.fetchEmployeesByName("Doe");
        assertEquals(2, employees.size());
        assertTrue(employees.stream().allMatch(emp -> emp.getEmployeeName().contains("Doe")));
    }
}
