package com.pagoDocente.entity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "docente_pago")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocentePago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tipoContrato;

    private int horasDiurnas;
    private int horasNocturnas;
    private int horasDominicales;
    private int horasFestivas;

    private double salarioBruto;
    private double descuentoParafiscales;
    private double salarioNeto;

    private LocalDateTime fechaRegistro;
}