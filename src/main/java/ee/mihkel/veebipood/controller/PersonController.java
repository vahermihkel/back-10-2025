package ee.mihkel.veebipood.controller;

import ee.mihkel.veebipood.entity.Person;
import ee.mihkel.veebipood.entity.PersonRole;
import ee.mihkel.veebipood.model.AuthToken;
import ee.mihkel.veebipood.model.LoginData;
import ee.mihkel.veebipood.model.PersonDTO;
import ee.mihkel.veebipood.model.UpdatePassword;
import ee.mihkel.veebipood.repository.PersonRepository;
import ee.mihkel.veebipood.service.JwtService;
import ee.mihkel.veebipood.util.PersonValidator;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PersonController {

    @Autowired
    PersonRepository personRepository;

    @Autowired
    PersonValidator personValidator;

//    ModelMapper modelMapper = new ModelMapper();
    @Autowired
    ModelMapper modelMapper;

    @Autowired
    JwtService jwtService;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping("public-persons")
    public List<PersonDTO> getPublicPersons(){
//        return personRepository.findAll();
//        List<PersonDTO> persons = new ArrayList<>();
//        for(Person person : personRepository.findAll()){
//            PersonDTO personDTO = new PersonDTO();
//            personDTO.setFirstName(person.getFirstName());
//            personDTO.setLastName(person.getLastName());
//            persons.add(personDTO);
//        }
        System.out.println(modelMapper);
        return List.of(modelMapper.map(personRepository.findAll(), PersonDTO[].class));
    }

    @PostMapping("signup")
    public Person signup(@RequestBody Person person){
        if (person.getId() != null){
            throw new RuntimeException("Cannot signup with ID");
        }
//        PersonValidator personValidator = new PersonValidator();
        if (person.getEmail() == null || !personValidator.validateEmail(person.getEmail())) {
            throw new RuntimeException("Email is not correct");
        }
        personValidator.validatePassword(person.getPassword());
//        person.setRole(PersonRole.CUSTOMER);
        person.setPassword(bCryptPasswordEncoder.encode(person.getPassword()));
        return personRepository.save(person);
    }

    @PostMapping("login")
    public AuthToken login(@RequestBody LoginData loginData){
        Person person = personRepository.findByEmail(loginData.getEmail());
        if (person == null ){
            throw new RuntimeException("Invalid email");
        }
        if (!bCryptPasswordEncoder.matches(loginData.getPassword(), person.getPassword())){
            throw new RuntimeException("Invalid password");
        }
        return jwtService.generateToken(person, false);
    }

    @GetMapping("update-token")
    public AuthToken updateToken(){
        Long id = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        Person person = personRepository.findById(id).orElseThrow();
        return jwtService.generateToken(person, true);
    }


    @GetMapping("persons")
    public List<Person> getPersons(){
        return personRepository.findAllByOrderByIdAsc();
    }

    @PatchMapping("change-admin")
    public List<Person> changeAdmin(@RequestParam Long personId, boolean isAdmin){
        Person person = personRepository.findById(personId).orElseThrow();
        if (isAdmin){
            person.setRole(PersonRole.ADMIN);
        } else {
            person.setRole(PersonRole.CUSTOMER);
        }
        personRepository.save(person);
        return personRepository.findAllByOrderByIdAsc();
    }

    @GetMapping("person")
    public Person getPerson(){
        Long id = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        return personRepository.findById(id).orElseThrow();
    }

    @PutMapping("update-profile")
    public Person updateProfile(@RequestBody Person person){
        Long id = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        if (!person.getId().equals(id)) {
            throw new RuntimeException("Cannot edit someone else's profile");
        }
        Person dbPerson = personRepository.findById(id).orElseThrow();
        person.setRole(dbPerson.getRole());
        person.setPassword(dbPerson.getPassword());
        return personRepository.save(person);
    }

    @PatchMapping("update-password")
    public Person updatePassword(@RequestBody UpdatePassword updatePassword){
        Long id = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());

        personValidator.validatePassword(updatePassword.getNewPassword());

        if (!id.equals(updatePassword.getPersonId())) {
            throw new RuntimeException("Cannot edit someone else's password");
        }

        Person person = personRepository.findById(id).orElseThrow();

        if (!bCryptPasswordEncoder.matches(updatePassword.getOldPassword(), person.getPassword())){
            throw new RuntimeException("Password doesnt match old password");
        }
        person.setPassword(bCryptPasswordEncoder.encode(updatePassword.getNewPassword()));
        return personRepository.save(person);
    }


}
