package com.pagoDocente.controller;
import com.pagoDocente.dto.PagoRequest;
import com.pagoDocente.dto.PagoResponse;
import com.pagoDocente.service.PagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pago")
@CrossOrigin(origins = "*")
public class PagoController {

    @Autowired
    private PagoService pagoService;

    @PostMapping
    public PagoResponse calcularPago(@RequestBody PagoRequest request) {
        return pagoService.calcularPago(request);
    }
}