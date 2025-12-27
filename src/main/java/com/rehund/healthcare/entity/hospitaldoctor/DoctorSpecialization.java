package com.rehund.healthcare.entity.hospitaldoctor;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "doctor_specializations")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class DoctorSpecialization {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "doctor_specialization_id")
    private Long doctorSpecializationId;

    @Column(name = "doctor_id", nullable = false)
    private Long doctorId;

    @Column(name = "specialization_id", nullable = false)
    private Long specializationId;

    @Column(
            name = "base_fee",
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal baseFee;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
