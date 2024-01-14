package com.bytesfarms.companyMain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bytesfarms.companyMain.entity.UserProfile;

public interface UserProfileRepository  extends JpaRepository<UserProfile, Long> {

}
