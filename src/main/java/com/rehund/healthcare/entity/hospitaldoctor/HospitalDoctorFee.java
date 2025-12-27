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
@Table(name = "hospital_doctor_fees")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class HospitalDoctorFee {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hospital_doctor_fee_id")
    @Id
    private Long hospitalDoctorFeeId;

    @Column(name = "hospital_id", nullable = false)
    private Long hospitalId;

    @Column(name = "doctor_specialization_id", nullable = false)
    private Long doctorSpecializationId;

    @Column(
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal fee;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
