package com.example.shiyan4.controller;

import com.example.shiyan4.bean.Users;
import com.example.shiyan4.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UsersController {
    @Autowired
    private UsersService usersService;

    @RequestMapping(value = "users/findall", method = RequestMethod.GET)
    public List<Users> getUsersAll(){
        return usersService.findAll();
    }
    @RequestMapping(value = "users/add",method = RequestMethod.POST)
    public void addUsers(@RequestParam String username,@RequestParam String pass){
        Users users = new Users(username,pass);
        usersService.addUsers(users);
    }
    @RequestMapping(value = "users/update",method = RequestMethod.POST)
    public void updateUsers(@RequestParam String username,@RequestParam String pass){
        Users users = new Users(username,pass);
        usersService.updateUsers(users);
    }
    @RequestMapping(value = "users/delete",method = RequestMethod.POST)
    public void deleteUsers(@RequestParam String username){
        usersService.deleteUsers(username);
    }
    @RequestMapping(value = "users/find",method = RequestMethod.GET)
    public Users getUsers(@RequestParam String username){
        return usersService.findByUsername(username);
    }
}
