package com.pagoDocente.dto;

import lombok.Data;

@Data
public class PagoRequest {
    private String tipoContrato;
    private int horasDiurnas;
    private int horasNocturnas;
    private int horasDominicales;
    private int horasFestivas;

    public PagoRequest(String tipoContrato, int horasDiurnas, int horasNocturnas, int horasDominicales, int horasFestivas) {
        this.tipoContrato = tipoContrato;
        this.horasDiurnas = horasDiurnas;
        this.horasNocturnas = horasNocturnas;
        this.horasDominicales = horasDominicales;
        this.horasFestivas = horasFestivas;
    }
}