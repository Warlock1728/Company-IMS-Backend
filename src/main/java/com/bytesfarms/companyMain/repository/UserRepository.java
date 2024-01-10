package com.bytesfarms.companyMain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bytesfarms.companyMain.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByUsername(String username);

	Optional<User> findByEmail(String email);

	boolean existsByEmail(String email);

	boolean existsByUsername(String username);

	List<User> findByRole_Id(int roleId);

	@Query("SELECT u FROM User u WHERE u.role.id IN :roleIds")
	List<User> findByRoleIdIn(@Param("roleIds") List<Long> roleIds);

	
	//For forgot password
	
	User findUserByEmail(String email);

	User findByUuid(String uuid);

	//List<User> findByRoleIn(List<Integer> of);
	
	
	
}