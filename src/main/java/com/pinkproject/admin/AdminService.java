package com.pinkproject.admin;

import com.pinkproject.admin.AdminRequest._LoginAdminRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AdminService {

    private final AdminRepository adminRepository;

    public boolean authenticate(_LoginAdminRecord loginRequestDTO) {
        Admin admin = adminRepository.findByUsername(loginRequestDTO.username());
        if (admin != null && admin.getPassword().equals(loginRequestDTO.password())) {
            return true;
        }
        return false;
    }
}
