package com.example.shiyan4.controller;

import com.example.shiyan4.bean.Person;
import com.example.shiyan4.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PersonController {
    @Autowired
    private PersonService personService;

    @RequestMapping(value = "person/findall", method = RequestMethod.GET)
    public List<Person> getPersonAll(){
        return personService.findAll();
    }
    @RequestMapping(value = "person/add4", method = RequestMethod.POST)
    public void addPerson(@RequestParam String username,@RequestParam String name,@RequestParam int age,@RequestParam String teleno){
        Person person = new Person(username,name,age,teleno);
        personService.addOrUpdatePerson(person);
    }
    @RequestMapping(value = "person/add3_teleno",method = RequestMethod.POST)
    public void addPerson(@RequestParam String username,@RequestParam String name,@RequestParam String teleno){
        Person person = new Person(username,name,-1,teleno);
        personService.addOrUpdatePerson(person);
    }
    @RequestMapping(value = "person/add3_age",method = RequestMethod.POST)
    public void addPerson(@RequestParam String username,@RequestParam String name,@RequestParam int age){
        Person person = new Person(username,name,age,"");
        personService.addOrUpdatePerson(person);
    }
    @RequestMapping(value = "person/add2",method = RequestMethod.POST)
    public void addPerson(@RequestParam String username,@RequestParam String name){
        Person person = new Person(username,name,-1,"");
        personService.addOrUpdatePerson(person);
    }
    @RequestMapping(value = "person/delete",method = RequestMethod.POST)
    public void deletePerson(@RequestParam String username){
        personService.deletePerson(username);
    }
    @RequestMapping(value = "person/find",method = RequestMethod.GET)
    public Person getPerson(@RequestParam String username){
        return personService.findByUsername(username);
    }


}
