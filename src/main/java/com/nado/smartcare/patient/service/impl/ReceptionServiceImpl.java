package com.nado.smartcare.patient.service.impl;

import com.nado.smartcare.employee.domain.Employee;
import com.nado.smartcare.employee.repository.EmployeeRepository;
import com.nado.smartcare.member.domain.Member;
import com.nado.smartcare.member.repository.MemberRepository;
import com.nado.smartcare.patient.domain.Reception;
import com.nado.smartcare.patient.domain.type.ReceptionStatus;
import com.nado.smartcare.patient.domain.dto.ReceptionDto;
import com.nado.smartcare.patient.repository.ReceptionRepository;
import com.nado.smartcare.patient.service.ReceptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReceptionServiceImpl implements ReceptionService {

    private final MemberRepository memberRepository;

    private final EmployeeRepository employeeRepository;

    private final ReceptionRepository receptionRepository;

    @Override
    public void registerReception(Long memberNo, Long employeeNo, LocalDateTime appointmentTime) {
        Member member = memberRepository.findById(memberNo)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with memberNo: " + memberNo));

        Employee employee = employeeRepository.findByEmployeeNo(employeeNo)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with employeeNo: " + employeeNo));

        ReceptionDto receptionDto = new ReceptionDto(
                null, // receptionNo는 생성될 때 자동으로 설정
                member,
                employee,
                null, // patientRecordCard는 null로 설정
                appointmentTime,
                ReceptionStatus.PENDING, // 기본 상태를 PENDING으로 설정
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        Reception reception = Reception.of(receptionDto, member, employee);

        receptionRepository.save(reception);
    }

    @Override
    public List<ReceptionDto> getAllReceptions() {
        return receptionRepository.findAll().stream()
                .map(ReceptionDto::from)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ReceptionDto> getReceptionById(Long receptionId) {
        Reception reception = receptionRepository.findById(receptionId)
                .orElseThrow(() -> new IllegalArgumentException("Reception Not found with receptionNo"));

        ReceptionDto dto = ReceptionDto.from(reception);
        return Optional.of(dto);
    }

    @Override
    public void updateReceptionStatus(ReceptionDto receptionDto, ReceptionStatus receptionStatus) {
        Reception reception = receptionRepository.findById(receptionDto.receptionNo())
                .orElseThrow(() -> new IllegalArgumentException("Reception Not found with receptionNo"));

        reception.setStatus(receptionStatus);
        receptionRepository.save(reception);
    }

    @Override
    public void deleteReceptionById(Long receptionId) {
        receptionRepository.deleteById(receptionId);
    }
}
