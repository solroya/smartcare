package com.nado.smartcare.patient.repository;

import com.nado.smartcare.member.domain.Member;
import com.nado.smartcare.patient.domain.PatientRecordCard;
import com.nado.smartcare.reservation.domain.PatientRecordCardWithCount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface PatientRecordCardRepository extends JpaRepository<PatientRecordCard, Long> {

    List<PatientRecordCard> findByEmployee_EmployeeNoAndClinicDateBetween(Long employeeNo, LocalDate startDate, LocalDate endDate);

    Page<PatientRecordCard> findByClinicDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);

    @Query("SELECT p FROM PatientRecordCard p WHERE p.member.memberNo = :memberNo")
    List<PatientRecordCard> findByMember_MemberNo(Long memberNo);

    @Query(value = """
    SELECT prc.* FROM (
        SELECT a.*, ROWNUM rnum 
        FROM (
            SELECT p.*, count(*) over() as total_count 
            FROM patient_record_card p
            WHERE p.member_id = :memberNo 
            ORDER BY p.clinic_date DESC
        ) a WHERE ROWNUM <= :limit + :offset
    ) prc WHERE rnum > :offset
""", nativeQuery = true)
    List<PatientRecordCard> findByMemberNoOrderByClinicDateDesc(
            @Param("memberNo") Long memberNo,
            @Param("offset") int offset,
            @Param("limit") int limit);

    // 전체 카운트를 위한 별도 쿼리
    @Query(value = "SELECT count(*) FROM patient_record_card WHERE member_id = :memberNo",
            nativeQuery = true)
    long countByMemberNo(@Param("memberNo") Long memberNo);


    @Query(value = """
    SELECT * FROM (
        SELECT p.*, ROW_NUMBER() OVER (ORDER BY p.clinic_date DESC) AS rn
        FROM patient_record_card p
        WHERE p.member_id = :memberNo
    ) WHERE rn > :offset AND rn <= :offset + :limit
""", nativeQuery = true)
    List<PatientRecordCard> findRecordsWithPagination(
            @Param("memberNo") Long memberNo,
            @Param("offset") int offset,
            @Param("limit") int limit);

    @Query(value = """
    SELECT p FROM PatientRecordCard p
    WHERE p.clinicDate BETWEEN :startDate AND :endDate
    AND (p.member.memberName LIKE %:searchTerm% OR p.clinicName LIKE %:searchTerm%)
""")
    Page<PatientRecordCard> findByDateRangeAndSearchTerm(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("searchTerm") String searchTerm,
            Pageable pageable
    );

    PatientRecordCard findByMember(Member member);

}
