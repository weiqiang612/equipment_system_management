package com.weiqiang;

import com.weiqiang.utils.DBUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Connection;

@SpringBootTest
class DatabaseAssignmentApplicationTests {

    @Test
    void contextLoads() {
        Connection connection = DBUtils.getConnection();
        System.out.println(connection);
    }

    @Test
    void testGit(){
        System.out.println("Hello GiT!");
    }

}
