package com.udacity.jdnd.course3.critter.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<EmployeeEntity, Long> {
    List<EmployeeEntity> findByDaysAvailableContaining(DayOfWeek dayOfWeek);
    List<EmployeeEntity> findEmployeeEntitiesBySchedules_Id(Long scheduleId);
}
