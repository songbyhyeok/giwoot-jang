package com.giwootjang.backend.member.domain.repository;

import com.giwootjang.backend.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

}
