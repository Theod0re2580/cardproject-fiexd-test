package com.example.cardtest.repository;

import com.example.cardtest.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByLoginId(String loginId);

    Optional<Member> findByEmail(String email);

    @Query(value = "SELECT * FROM MEMBER ORDER BY MEMBER_ID DESC FETCH FIRST :limit ROWS ONLY", nativeQuery = true)
    List<Member> findLatest(@Param("limit") int limit);
}
