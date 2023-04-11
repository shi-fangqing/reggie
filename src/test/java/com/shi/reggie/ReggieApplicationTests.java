package com.shi.reggie;

import com.shi.reggie.common.Constant;
import com.shi.reggie.pojo.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootTest
class ReggieApplicationTests {

    @Test
    void testHasLength() {
        System.out.println(StringUtils.hasLength(" ")); //true
        System.out.println(StringUtils.hasLength(null)); //false
        System.out.println(StringUtils.hasLength(""));  //false
        System.out.println(StringUtils.hasLength("l")); //true

    }

    @Test
    void testArraysStream(){
        Arrays.asList(2,3,4,1,3,9).stream()
                .distinct()
                .sorted()
                .forEach(System.out::println);
        System.out.println("=======================");
        Arrays.asList("wad","asd","az","asg").stream()
                .sorted()
                .forEach(System.out::println);
        System.out.println("=======================");
        Arrays.asList("wad","asd","az","asg").stream()
                .sorted((a,b)->b.compareTo(a))
                .forEach(System.out::println);
    }

    @Test
    void testSortUser(){
        List<User> userList = User.getUserList();
        //比较方式：实现的compareTo方法(薪水升序)
        System.out.println("自然排序：");
        userList.stream().sorted().forEach(System.out::println);
        System.out.println("======================");
        //比较方式：Comparator内置的基本排序
        System.out.println("定制排序：");
        userList.stream().sorted(Comparator.comparing(User::getAge))
                .forEach(System.out::println);
        System.out.println("======================");
        //比较方式：创建Comparator的实现类
        System.out.println("多条件定制排序：");
        userList.stream().sorted(((o1, o2) -> {
            int compareValue = o1.getAge().compareTo(o2.getAge());
            if (compareValue!=0){
                return compareValue;
            }
            int compareValue1 = o1.getSalary().compareTo(o2.getSalary());
            return compareValue1;
        })).forEach(System.out::println);
        System.out.println("=====================");
    }

    @Test
    void testAverageAndSumSalary(){
        List<User> userList = User.getUserList();
        //薪水总和
        double sumSalary = userList.stream().mapToDouble(user -> user.getSalary())
                .sum();
        System.out.println(sumSalary);
        //平均薪水
        double avgSalary = userList.stream().mapToDouble(User::getSalary)
                .average().getAsDouble();
        System.out.println(avgSalary);
    }

    @Test
    void testMaxAndMinSalary(){
        List<User> userList = User.getUserList();
        User user = userList.stream().max(Comparator.comparing(User::getSalary))
                .get();
        System.out.println(user.getSalary());
        System.out.println("=============================");
        User user1 = userList.stream().min(Comparator.comparing(User::getSalary))
                .get();
        System.out.println(user1.getSalary());
    }

    @Test
    void testFilter(){
        List<User> userList = User.getUserList();
        //过滤出来 性别=男
        List<User> users = userList.stream().filter(user -> user.getSex().equals('男'))
                .collect(Collectors.toList());
        users.forEach(System.out::println);
    }

    @Test
    void testStream(){
        //从3开始，步长为2，得到10个数字
        Stream.iterate(3, n -> n += 2)
                .limit(10)
                .forEach(System.out::println);
        //生成3个随机数，并降序打印
        Stream.generate(Math::random).limit(3)
                .forEachOrdered(System.out::println);
    }

    @Test
    void testDoubleYinHao(){
        System.out.println("{\"name\":\"张三\",\"age\":\"24\"}");
        System.out.println("{'name':'张三','age':'24'}");
    }


    @Test
    void testFile1() throws IOException {
        File file = new File("D:" + File.separator + "dir1" + File.separator + "hello.txt");
        if (!file.exists()) {
            file.mkdirs();
            System.out.println(file.createNewFile());
        }
        System.out.println(file.getAbsolutePath());
    }

    @Test
    void testFile2(){
        File file = new File("D:" + File.separator + "dir1");
        if (!file.exists()) {
            file.mkdirs();
        }
        System.out.println(file.getAbsolutePath());
    }

    @Test
    void testCreateNewFile() throws IOException {
        String basePath="D:"+ File.separator+"dir1"+File.separator+"dir2"+File.separator;
        File file = new File(basePath+"hello1.txt");
        File file1 = new File(basePath+"hello2.txt");
        File file2 = new File(basePath+"hello3.txt");
        List<File> list=new ArrayList<>();
        list.add(file);
        list.add(file1);
        list.add(file2);
        File dir=new File(basePath);
        if (!dir.exists()){
            dir.mkdirs();
        }
        for (File f :list) {
            f.createNewFile();
        }
        System.out.println(dir.getPath());
        System.out.println(file1.getPath());
        System.out.println(file1.getAbsolutePath());
    }

    @Test
    void testAddress(){
        String tar="adw";
        String a=tar;
        String b=a;
        System.out.println("adw".hashCode());
        System.out.println(tar.hashCode());
        System.out.println(a.hashCode());
        System.out.println(b.hashCode());
    }

    @Test
    void testLinkedList(){
        LinkedList head=new LinkedList();
        head.add("23");
    }

    @Test
    void testFileAbsolutePath(){
        File file=new File(Constant.BASE_PATH);
        file.exists();
        System.out.println(file.getAbsolutePath());
    }
}
