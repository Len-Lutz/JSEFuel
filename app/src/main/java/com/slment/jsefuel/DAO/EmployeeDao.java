package com.slment.jsefuel.DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.slment.jsefuel.Entities.Employee;
import java.util.List;

@Dao
public interface EmployeeDao {
    @Query("SELECT * FROM employee ORDER BY last_name, first_name")
    List<Employee> getAllEmployees();

    @Query("SELECT * FROM employee WHERE employee_id = :employeeId")
    Employee getEmployee(int employeeId);

    @Query("SELECT * FROM employee WHERE :workCell = work_cell")
    Employee getEmployeeByWorkCell(String workCell);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertEmployee(Employee employee);

    @Update
    void updateEmployee(Employee employee);
}
