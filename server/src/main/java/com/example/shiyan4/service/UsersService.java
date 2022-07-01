package com.example.shiyan4.service;

import com.example.shiyan4.bean.Users;
import com.example.shiyan4.mapper.UsersMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class UsersService {
    @Autowired
    private UsersMapper usersMapper;

    /**
     * 查找全部
     * @return
     */
    public List<Users> findAll(){
        return usersMapper.findAll();
    }

    /**
     * 增
     * @param users
     */
    public void addUsers(Users users){
        usersMapper.addUsers(users);
    }

    /**
     * 改
     * @param users
     */
    public void updateUsers(Users users){
        usersMapper.updateUsers(users);
    }

    /**
     * 删
     * @param username
     */
    public void deleteUsers(String username){
        System.out.println("数据库：users表删除数据  "+username);
        usersMapper.deleteUsers(username);
    }

    /**
     * 查
     * @param username
     * @return
     */
    public Users findByUsername(String username){
        return usersMapper.findByUsername(username);
    }
}
