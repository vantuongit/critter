package com.udacity.jdnd.course3.critter.pet;

import com.udacity.jdnd.course3.critter.user.CustomerEntity;
import com.udacity.jdnd.course3.critter.user.CustomerService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles web requests related to Pets.
 */
@RestController
@RequestMapping("/pet")
public class PetController {

    @Autowired
    private PetService petService;

    @Autowired
    private CustomerService customerService;
    @Autowired
    private ModelMapper modelMapper;

    @PostMapping
    public PetDTO savePet(@RequestBody PetDTO petDTO) {
        PetEntity petEntity = modelMapper.map(petDTO, PetEntity.class);
        CustomerEntity customerEntity = customerService.findById(petDTO.getOwnerId());
        if (customerEntity != null) {
            petEntity.setCustomer(customerEntity);
        }
        petEntity = petService.save(petEntity);
        PetDTO petDTO1 = modelMapper.map(petEntity, PetDTO.class);
        if (petEntity.getCustomer() != null) {
            petDTO.setOwnerId(petEntity.getCustomer().getId());
        }
        return petDTO1;
    }

    @GetMapping("/{petId}")
    public PetDTO getPet(@PathVariable long petId) {
        PetEntity entity = petService.findById(petId);
        PetDTO petDTO = modelMapper.map(entity, PetDTO.class);
        if (entity.getCustomer() != null) {
            petDTO.setOwnerId(entity.getCustomer().getId());
        }
        return petDTO;
    }

    @GetMapping
    public List<PetDTO> getPets(){
        List<PetEntity> petEntities = petService.findAll();
        return petEntities.stream().map(petEntity -> {
            PetDTO petDTO = modelMapper.map(petEntity, PetDTO.class);
            if (petEntity.getCustomer() != null) {
                petDTO.setOwnerId(petEntity.getCustomer().getId());
            }
            return petDTO;
        }).collect(Collectors.toList());
    }

    @GetMapping("/owner/{ownerId}")
    public List<PetDTO> getPetsByOwner(@PathVariable long ownerId) {
        List<PetEntity> petEntities = petService.findAllByCustomer(ownerId);
        return petEntities.stream().map(petEntity -> {
            PetDTO petDTO = modelMapper.map(petEntity, PetDTO.class);
            if (petEntity.getCustomer() != null) {
                petDTO.setOwnerId(petEntity.getCustomer().getId());
            }
            return petDTO;
        }).collect(Collectors.toList());
    }
}
