package com.pagoDocente.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PagoResponse {
    private double salarioBruto;
    private double descuentoParafiscales;
    private double salarioNeto;
    private double recargo;       // ← Campo nuevo para test 6 a 10 y 18
    private String detalle;       // ← Campo nuevo para test 15 (desglose)
}