package com.pinkproject.admin;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class AdminRepositoryTest {

   @Autowired
    private AdminRepository adminRepository;

   @Test
   public void findByUsername_Test() {

      //when
      Admin findAdmin = adminRepository.findByUsername("admin");

      //then
      assertEquals("admin", findAdmin.getUsername());

      //eye
      System.out.println("관리자 아이디: " + findAdmin.getUsername());
      System.out.println("관리자 비밀번호: " + findAdmin.getPassword());
      System.out.println("계정 생성 시간: " + findAdmin.getCreatedAt());
   }
}
