package com.example.shiyan4.service;

import com.example.shiyan4.bean.Person;
import com.example.shiyan4.bean.Users;
import com.example.shiyan4.mapper.PersonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class PersonService {
    @Autowired
    private PersonMapper personMapper;
    @Autowired
    private UsersService usersService;

    /**
     * 查找全部
     * @return
     */
    public List<Person> findAll(){
        return personMapper.findAll();
    }

    /**
     * 增（如果有则修改）
     * Users表中一并增加（如果有则修改）
     * @param person
     */
    public void addOrUpdatePerson(Person person){
        Person person1 = personMapper.findByUsername(person.getUsername());
        if (person1 == null){
            // person表中没有这个username，在person和users表中都执行添加
            System.out.println("数据库：Person表新增数据");
            System.out.printf("username: %s\t",person.getUsername());
            System.out.printf("name: %s\t",person.getName());
            if (person.getAge() == -1){
                System.out.print("age: \t");
            }else{
                System.out.printf("age: %d\t",person.getAge());
            }
            System.out.printf("teleno: %s\n",person.getTeleno());

            // 针对不同数据调用不同的函数
            if (!person.getTeleno().equals("") && person.getAge() != -1){
                personMapper.addPerson_Four(person);
            }
            else if(!person.getTeleno().equals("") && person.getAge() == -1){
                personMapper.addPerson_Three_teleno(person);
            }
            else if(person.getTeleno().equals("") && person.getAge() != -1){
                personMapper.addPerson_Three_age(person);
            }
            else{
                personMapper.addPerson_Two(person);
            }
            //处理User表
            System.out.println("数据库：Users表新增数据");
            System.out.printf("username: %s\t",person.getUsername());
            System.out.println("pass: 88888888");
            Users users = new Users(person.getUsername(),"88888888");
            usersService.addUsers(users);
        } else{
            // person表中有这个username，执行修改
            System.out.println("数据库：Person表修改数据");
            System.out.printf("username: %s\t",person.getUsername());
            System.out.printf("name: %s\t",person.getName());
            if (person.getAge() == -1){
                System.out.print("age: \t");
            }else{
                System.out.printf("age: %d\t",person.getAge());
            }
            System.out.printf("teleno: %s\n",person.getTeleno());
            personMapper.updatePerson(person);
        }
    }

    /**
     * 改
     * @param person
     */
    public void updatePerson(Person person){
        personMapper.updatePerson(person);
    }

    /**
     * 删
     * @param username
     */
    public void deletePerson(String username){
        personMapper.deletePerson(username);
    }

    /**
     * 查
     * @param username
     * @return
     */
    public Person findByUsername(String username){
        return personMapper.findByUsername(username);
    }

}
