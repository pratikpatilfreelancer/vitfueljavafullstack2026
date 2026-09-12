package com.capgemini.apartment_maintenance.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "staff")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Staff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "staff_id")
    private Integer staffId;

    @NotBlank
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @NotBlank
    @Column(name = "designation", nullable = false, length = 50)
    private String designation; // e.g. Technician, Supervisor

    @NotBlank
    @Column(name = "specialization", nullable = false, length = 50)
    private String specialization; // e.g. Plumbing, Electrical

    @OneToMany(mappedBy = "staff")
    @Builder.Default
    private List<Complaint> assignedComplaints = new ArrayList<>();
}