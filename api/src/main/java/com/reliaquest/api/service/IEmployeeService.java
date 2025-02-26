package com.reliaquest.api.service;

import com.reliaquest.api.dto.CreateEmployeeRequest;
import com.reliaquest.api.dto.EmployeeDto;
import java.util.List;
import java.util.Optional;

public interface IEmployeeService {
    List<EmployeeDto> getAllEmployees();

    List<EmployeeDto> getEmployeesByNameSearch(String searchString);

    Optional<EmployeeDto> getEmployeeById(String id);

    int getHighestSalary();

    List<String> getTopTenHighestEarningEmployeeNames();

    Optional<EmployeeDto> createEmployee(CreateEmployeeRequest request);

    boolean deleteEmployeeById(String id);
}
