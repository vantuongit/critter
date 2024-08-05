package com.udacity.jdnd.course3.critter.user;

import com.udacity.jdnd.course3.critter.pet.PetEntity;
import com.udacity.jdnd.course3.critter.pet.PetService;
import com.udacity.jdnd.course3.critter.schedule.ScheduleDTO;
import com.udacity.jdnd.course3.critter.schedule.ScheduleEntity;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Handles web requests related to Users.
 * <p>
 * Includes requests for both customers and employees. Splitting this into separate user and customer controllers
 * would be fine too, though that is not part of the required scope for this class.
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private CustomerService customerService;
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private PetService petService;
    @Autowired
    private ModelMapper modelMapper;

    @PostMapping("/customer")
    public CustomerDTO saveCustomer(@RequestBody CustomerDTO customerDTO) {
        CustomerEntity customerEntity = modelMapper.map(customerDTO, CustomerEntity.class);
        List<PetEntity> petEntities = new ArrayList<>();
        if (customerDTO.getPetIds() != null) {
            petEntities = customerDTO.getPetIds().stream().map(petId -> petService.findById(petId)).collect(Collectors.toList());
        }
        customerEntity.setPets(petEntities);
        customerEntity = customerService.save(customerEntity);
        customerDTO.setId(customerEntity.getId());
        return customerDTO;
    }

    @GetMapping("/customer")
    public List<CustomerDTO> getAllCustomers() {
        List<CustomerEntity> customerEntities = customerService.findAll();
        return customerEntities.stream().map(
                cus -> {
                    CustomerDTO customerDTO = modelMapper.map(cus, CustomerDTO.class);
                    if (cus.getPets() != null) {
                        List<Long> petIds =  cus.getPets().stream().map(PetEntity-> PetEntity.getId()).collect(Collectors.toList());
                        customerDTO.setPetIds(petIds);
                    }
                    return customerDTO;
                }
        ).collect(Collectors.toList());
    }

    @GetMapping("/customer/pet/{petId}")
    public CustomerDTO getOwnerByPet(@PathVariable long petId) {
        CustomerEntity customerEntity = customerService.findByPetId(petId);
        CustomerDTO customerDTO = modelMapper.map(customerEntity, CustomerDTO.class);
        if (customerEntity.getPets() != null) {
            List<Long> petIds =  customerEntity.getPets().stream().map(PetEntity->PetEntity.getId()).collect(Collectors.toList());
            customerDTO.setPetIds(petIds);
        }
        return customerDTO;
    }

    @PostMapping("/employee")
    public EmployeeDTO saveEmployee(@RequestBody EmployeeDTO employeeDTO) {
        EmployeeEntity employeeEntity = modelMapper.map(employeeDTO, EmployeeEntity.class);
        employeeEntity = employeeService.save(employeeEntity);
        return modelMapper.map(employeeEntity, EmployeeDTO.class);
    }

    @PostMapping("/employee/{empId}")
    public EmployeeDTO getEmployee(@PathVariable long empId) {
        EmployeeEntity employeeEntity = employeeService.findById(empId);
        return modelMapper.map(employeeEntity, EmployeeDTO.class);
    }

    @PutMapping("/employee/{empId}")
    public void setAvailability(@RequestBody Set<DayOfWeek> daysAvailable, @PathVariable long empId) {
        EmployeeEntity employeeEntity = employeeService.findById(empId);
        employeeEntity.setDaysAvailable(daysAvailable);
        employeeService.save(employeeEntity);
    }

    @GetMapping("/employee/availability")
    public List<EmployeeDTO> findEmployeesForService(@RequestBody EmployeeRequestDTO employeeDTO) {
        List<EmployeeEntity> employeeEntities = employeeService.findByService(employeeDTO.getDate().getDayOfWeek(), employeeDTO.getSkills());
        return employeeEntities.stream().map(emp -> modelMapper.map(emp, EmployeeDTO.class)).collect(Collectors.toList());
    }
}
