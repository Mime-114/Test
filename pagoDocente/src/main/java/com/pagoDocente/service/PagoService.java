package com.pagoDocente.service;

import com.pagoDocente.dto.PagoRequest;
import com.pagoDocente.dto.PagoResponse;

public interface PagoService {
    PagoResponse calcularPago(PagoRequest request);
}