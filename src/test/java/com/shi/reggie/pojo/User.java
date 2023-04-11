package com.shi.reggie.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements Comparable<User> {
    private String name;
    private Character sex;
    private Integer age;
    private Double salary;

    @Override
    public int compareTo(User o) {
        return this.salary.compareTo(o.salary);
    }

    public static List<User> getUserList(){
        List<User> userList=new ArrayList<>();
        User user = new User("张三",'男',24,8000.0);
        User user1 = new User("李四",'男',20,12000.0);
        User user2 = new User("王五",'女',21,9000.0);
        User user3 = new User("小刚",'男',21,11000.0);
        User user4 = new User("小丽",'女',29,15000.0);
        User user5 = new User("小明",'男',23,9000.0);
        userList.add(user);
        userList.add(user1);
        userList.add(user2);
        userList.add(user3);
        userList.add(user4);
        userList.add(user5);
        return userList;
    }
}
