package com.nado.smartcare.patient.service.impl;

import com.nado.smartcare.employee.domain.Employee;
import com.nado.smartcare.employee.repository.EmployeeRepository;
import com.nado.smartcare.member.domain.Member;
import com.nado.smartcare.member.repository.MemberRepository;
import com.nado.smartcare.patient.dto.AppointmentDto;
import com.nado.smartcare.patient.dto.type.AppointmentStatus;
import com.nado.smartcare.patient.entity.Appointment;
import com.nado.smartcare.patient.repository.AppointmentRepository;
import com.nado.smartcare.patient.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;

    private final MemberRepository memberRepository;

    private final EmployeeRepository employeeRepository;

    @Override
    public List<AppointmentDto> getAllAppointments() {
        return appointmentRepository.findAll().stream()
                .map(AppointmentDto::from)
                .collect(Collectors.toList());
    }

    @Override
    public AppointmentDto createAppointment(Long memberNo, Long employeeNo, LocalDateTime appointmentTime) {

        Member member = memberRepository.findById(memberNo)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with memberNo: " + memberNo));

        Employee employee = employeeRepository.findByEmployeeNo(employeeNo)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with employeeNo: " + employeeNo));

        Appointment appointment = new Appointment();
        appointment.setMember(member);
        appointment.setDoctor(employee);
        appointment.setAppointmentTime(appointmentTime);
        appointment.setStatus(AppointmentStatus.PENDING);
        appointment.setCreatedAt(LocalDateTime.now());
        appointment.setUpdatedAt(LocalDateTime.now());
        Appointment savedAppointment = appointmentRepository.save(appointment);
        return AppointmentDto.from(savedAppointment);
    }

    @Transactional
    public void updateAppointmentStatus(Long appointmentId, AppointmentStatus status) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found with ID: " + appointmentId));
        appointment.setStatus(status);
        appointmentRepository.save(appointment); // 상태 업데이트
    }

    @Transactional
    public void deleteAppointment(Long appointmentId) {
        appointmentRepository.deleteById(appointmentId); // 예약 삭제
    }

    @Override
    public Optional<AppointmentDto> findById(Long appointmentId) {
        Appointment result = appointmentRepository.findById(appointmentId).orElseThrow(() -> new IllegalArgumentException("Appointment not found with ID: " + appointmentId));
        return Optional.of(AppointmentDto.from(result));
    }
}
