package com.example.shiyan4.mapper;

import com.example.shiyan4.bean.Users;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
@Mapper
public interface UsersMapper {
    /**
     * 全部用户查询
     * @return
     */
    List<Users> findAll();

    /**
     * 新增用户
     * @param users
     */
    void addUsers(Users users);

    /**
     * 修改用户
     * @param users
     */
    void updateUsers(Users users);

    /**
     * 删除用户
     * @param username
     */
    void deleteUsers(String username);

    /**
     * 指定username用户查询
     * @param username
     * @return
     */
    Users findByUsername(String username);


}
