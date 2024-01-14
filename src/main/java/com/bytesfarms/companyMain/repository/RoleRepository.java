package com.bytesfarms.companyMain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


import com.bytesfarms.companyMain.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

	Optional<Role> findByRoleName(String roleName);

}
