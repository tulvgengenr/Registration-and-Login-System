package com.example.shiyan4.mapper;

import com.example.shiyan4.bean.Person;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
@Mapper
public interface PersonMapper {
    List<Person> findAll();
    void addPerson_Four(Person person);
    void addPerson_Three_age(Person person);
    void addPerson_Three_teleno(Person person);
    void addPerson_Two(Person person);
    void updatePerson(Person person);
    void deletePerson(String username);
    Person findByUsername(String username);
}
