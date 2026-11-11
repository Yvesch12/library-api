package com.example.library.repository;
import com.example.library.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    boolean existsByBookIdAndMemberIdAndStatus(Long bookId, Long memberId, ReservationStatus status);
    List<Reservation> findByMemberIdOrderByIdAsc(Long memberId);
}
