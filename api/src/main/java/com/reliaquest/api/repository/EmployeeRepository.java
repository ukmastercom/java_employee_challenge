package com.reliaquest.api.repository;

import com.reliaquest.api.dto.CreateEmployeeRequest;
import com.reliaquest.api.dto.EmployeeDto;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.reliaquest.api.model.DeleteMockEmployeeInput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Repository
@Slf4j
public class EmployeeRepository {
    private static final String BASE_URL = "http://localhost:8112/api/v1/employee";
    private final RestTemplate restTemplate;

    public EmployeeRepository(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<EmployeeDto> fetchAllEmployees() {
        ResponseEntity<EmployeeListResponse> response = restTemplate.getForEntity(BASE_URL, EmployeeListResponse.class);
        return response.getBody() != null ? response.getBody().getData() : Collections.emptyList();
    }

    public List<EmployeeDto> fetchEmployeesByName(String searchString) {
        return fetchAllEmployees().stream()
                .filter(emp -> emp.getEmployeeName().toLowerCase().contains(searchString.toLowerCase()))
                .collect(Collectors.toList());
    }

    public Optional<EmployeeDto> fetchEmployeeById(String id) {
        try {
            ResponseEntity<EmployeeResponse> response =
                    restTemplate.getForEntity(BASE_URL + "/" + id, EmployeeResponse.class);
            return Optional.ofNullable(response.getBody()).map(EmployeeResponse::getData);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public int fetchHighestSalary() {
        return fetchAllEmployees().stream()
                .mapToInt(EmployeeDto::getEmployeeSalary)
                .max()
                .orElse(0);
    }

    public List<String> fetchTopTenHighestEarningEmployeeNames() {
        return fetchAllEmployees().stream()
                .sorted(Comparator.comparingInt(EmployeeDto::getEmployeeSalary).reversed())
                .limit(10)
                .map(EmployeeDto::getEmployeeName)
                .collect(Collectors.toList());
    }

    public Optional<EmployeeDto> createEmployee(CreateEmployeeRequest request) {
        try {
            log.info("Creating employee with request: {}", request);
            ResponseEntity<EmployeeResponse> response =
                    restTemplate.postForEntity(BASE_URL, request, EmployeeResponse.class);
            log.info("Received response: {}", response);
            return Optional.ofNullable(response.getBody()).map(EmployeeResponse::getData);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("Error in createEmployee API: {}", e.getResponseBodyAsString());
            throw e; // Let GlobalExceptionHandler handle it
        } catch (Exception e) {
            log.error("Unexpected error in createEmployee", e);
            throw new RuntimeException("Failed to create employee. Please try again.");
        }
    }

    public boolean deleteEmployeeById(String id) {
        try {
            Optional<EmployeeDto> employee = fetchEmployeeById(id);

            if (employee.isEmpty() || employee.get().getEmployeeName() == null) {
                log.error("Employee not found or name is missing for id: {}", id);
                return false;
            }
            // Create request body with both fields
            DeleteMockEmployeeInput request = new DeleteMockEmployeeInput(id, employee.get().getEmployeeName());

            // Wrap it in HttpEntity
            HttpEntity<DeleteMockEmployeeInput> requestEntity = new HttpEntity<>(request);

            // Send DELETE request with JSON body
            ResponseEntity<Void> response = restTemplate.exchange(
                    BASE_URL,
                    HttpMethod.DELETE,
                    requestEntity,
                    Void.class
            );

            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            return false;
        }
    }

    // DTOs for API Responses
    static class EmployeeListResponse {
        List<EmployeeDto> data;

        public List<EmployeeDto> getData() {
            return data;
        }
    }

    static class EmployeeResponse {
        EmployeeDto data;

        public EmployeeDto getData() {
            return data;
        }
    }
}
