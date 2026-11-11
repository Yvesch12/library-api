package com.example.library.repository;

import com.example.library.entity.Borrowing;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BorrowingRepository extends JpaRepository<Borrowing, Long> {
    java.util.List<Borrowing> findByMemberIdAndReturnDateIsNull(Long memberId);
}
