package com.reliaquest.api.service;

import com.reliaquest.api.dto.CreateEmployeeRequest;
import com.reliaquest.api.dto.EmployeeDto;
import com.reliaquest.api.repository.EmployeeRepository;
import java.util.List;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

@Service
@Slf4j
public class EmployeeService implements IEmployeeService {
    private final EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public List<EmployeeDto> getAllEmployees() {
        return employeeRepository.fetchAllEmployees();
    }

    public List<EmployeeDto> getEmployeesByNameSearch(String searchString) {
        return employeeRepository.fetchEmployeesByName(searchString);
    }

    public Optional<EmployeeDto> getEmployeeById(String id) {
        return employeeRepository.fetchEmployeeById(id);
    }

    public int getHighestSalary() {
        return employeeRepository.fetchHighestSalary();
    }

    public List<String> getTopTenHighestEarningEmployeeNames() {
        return employeeRepository.fetchTopTenHighestEarningEmployeeNames();
    }

    public Optional<EmployeeDto> createEmployee(CreateEmployeeRequest request) {
        Optional<EmployeeDto> result = employeeRepository.createEmployee(request);
        if(result.isPresent()){
            log.info("Created employee with id: {}",result.get().getId());
        }
        else{
            log.warn("Unable to create employee with given details: {}",request.getName());
        }
        return result;
    }

    public boolean deleteEmployeeById(String id) {
        try {
            log.info("Deleting employee with id: {}", id);
            employeeRepository.deleteEmployeeById(id);
            log.info("Successfully deleted employee with id: {}", id);
            return true;
        } catch (HttpClientErrorException.NotFound e){
            log.error("Employee with id: {} not found",id);
            return false;
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("Error in deleteEmployee API: {}", e.getResponseBodyAsString());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error in deleteEmployee", e);
            throw new RuntimeException("Failed to delete employee. Please try again.");
        }
    }
}
