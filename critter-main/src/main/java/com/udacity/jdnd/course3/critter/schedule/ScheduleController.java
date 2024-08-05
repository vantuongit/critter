package com.udacity.jdnd.course3.critter.schedule;

import com.udacity.jdnd.course3.critter.pet.PetEntity;
import com.udacity.jdnd.course3.critter.pet.PetService;
import com.udacity.jdnd.course3.critter.user.EmployeeEntity;
import com.udacity.jdnd.course3.critter.user.EmployeeRepository;
import com.udacity.jdnd.course3.critter.user.EmployeeService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles web requests related to Schedules.
 */
@RestController
@RequestMapping("/schedule")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private PetService petService;
    @Autowired
    private ModelMapper modelMapper;

    @PostMapping
    public ScheduleDTO createSchedule(@RequestBody ScheduleDTO scheduleDTO) {
        ScheduleEntity scheduleEntity = scheduleService.save(convertToEntity(scheduleDTO));
        ScheduleDTO scheduleDTO1 = convertToDTO(scheduleEntity);
        return scheduleDTO1;
    }

    @GetMapping
    public List<ScheduleDTO> getAllSchedules() {
        List<ScheduleEntity> scheduleEntities = scheduleService.findAll();
        return scheduleEntities.stream().map(scheduleEntity ->convertToDTO(scheduleEntity)).collect(Collectors.toList());
    }

    @GetMapping("/pet/{petId}")
    public List<ScheduleDTO> getScheduleForPet(@PathVariable long petId) {
        List<ScheduleEntity> scheduleEntities = scheduleService.findByPetId(petId);
        return scheduleEntities.stream().map(scheduleEntity ->convertToDTO(scheduleEntity)).collect(Collectors.toList());
    }

    @GetMapping("/employee/{employeeId}")
    public List<ScheduleDTO> getScheduleForEmployee(@PathVariable long employeeId) {
        List<ScheduleEntity> scheduleEntities = scheduleService.findByEmployeeId(employeeId);
        return scheduleEntities.stream().map(scheduleEntity ->convertToDTO(scheduleEntity)).collect(Collectors.toList());
    }

    @GetMapping("/customer/{customerId}")
    public List<ScheduleDTO> getScheduleForCustomer(@PathVariable long customerId) {
        List<ScheduleEntity> scheduleEntities = scheduleService.findByCustomerId(customerId);
        return scheduleEntities.stream().map(scheduleEntity ->convertToDTO(scheduleEntity)).collect(Collectors.toList());
    }

    private ScheduleEntity convertToEntity(ScheduleDTO schedule) {
        ScheduleEntity scheduleEntity = modelMapper.map(schedule, ScheduleEntity.class);
        if (schedule.getEmployeeIds() != null) {
            List<EmployeeEntity> employees = schedule.getEmployeeIds().stream().map(employeeId -> employeeService.findById(employeeId)).collect(Collectors.toList());
            scheduleEntity.setEmployees(employees);
        }
        //
        if (schedule.getPetIds() != null) {
            List<PetEntity> pets = schedule.getPetIds().stream().map(petId -> petService.findById(petId)).collect(Collectors.toList());
            scheduleEntity.setPets(pets);
        }
        return scheduleEntity;
    }
    private ScheduleDTO convertToDTO(ScheduleEntity schedule) {
        ScheduleDTO scheduleDTO = modelMapper.map(schedule, ScheduleDTO.class);
        if (schedule.getEmployees() != null) {
            List<Long> employeeIds =  schedule.getEmployees().stream().map(EmployeeEntity-> EmployeeEntity.getId()).collect(Collectors.toList());
            scheduleDTO.setEmployeeIds(employeeIds);
        }
        //
        if (schedule.getPets() != null) {
            List<Long> petIds =  schedule.getPets().stream().map(PetEntity -> PetEntity.getId()).collect(Collectors.toList());
            scheduleDTO.setPetIds(petIds);
        }
        return scheduleDTO;
    }
}
