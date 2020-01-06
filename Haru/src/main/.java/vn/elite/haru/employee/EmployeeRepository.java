package vn.elite.haru.employee;

import org.springframework.data.repository.PagingAndSortingRepository;

// @RepositoryRestResource(exported = false)
public interface EmployeeRepository extends PagingAndSortingRepository<Employee, Long> {}
