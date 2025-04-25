package com.pagoDocente.service.servicieImpl;

import com.pagoDocente.dto.PagoRequest;
import com.pagoDocente.dto.PagoResponse;
import com.pagoDocente.entity.DocentePago;
import com.pagoDocente.repository.DocentePagoRepository;
import com.pagoDocente.service.PagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
@Service
public class PagoServiceImpl implements PagoService {

    private final DocentePagoRepository pagoRepository;

    public PagoServiceImpl(DocentePagoRepository pagoRepository) {
        this.pagoRepository = pagoRepository;
    }

    @Override
    public PagoResponse calcularPago(PagoRequest request) {
        if (request.getTipoContrato() == null || request.getTipoContrato().trim().isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar un tipo de contrato");
        }

        if (!"tiempo_completo".equalsIgnoreCase(request.getTipoContrato())
                && !"medio_tiempo".equalsIgnoreCase(request.getTipoContrato())) {
            throw new IllegalArgumentException("Tipo de contrato no válido");
        }

        if ("medio_tiempo".equalsIgnoreCase(request.getTipoContrato())
                && (request.getHorasDiurnas() + request.getHorasNocturnas()
                + request.getHorasDominicales() + request.getHorasFestivas()) > 100) {
            throw new IllegalArgumentException("Medio tiempo no puede superar las 100 horas");
        }

        int totalHoras = request.getHorasDiurnas() + request.getHorasNocturnas()
                + request.getHorasDominicales() + request.getHorasFestivas();
        if (totalHoras > 240) {
            throw new IllegalArgumentException("No se permiten más de 240 horas por mes");
        }

        if (request.getHorasDiurnas() < 0 || request.getHorasNocturnas() < 0 ||
                request.getHorasDominicales() < 0 || request.getHorasFestivas() < 0) {
            throw new IllegalArgumentException("Las horas trabajadas no pueden ser negativas");
        }

        double valorHoraDiurna;
        double valorHoraNocturna;
        double valorHoraDominical;
        double valorHoraFestiva;

        if ("tiempo_completo".equalsIgnoreCase(request.getTipoContrato())) {
            valorHoraDiurna = 20000;
            valorHoraNocturna = 30000;
            valorHoraDominical = 35000;
            valorHoraFestiva = 40000;
        } else {
            valorHoraDiurna = 12000;
            valorHoraNocturna = 18000;
            valorHoraDominical = 21000;
            valorHoraFestiva = 24000;
        }

        double pagoDiurno = request.getHorasDiurnas() * valorHoraDiurna;
        double pagoNocturno = request.getHorasNocturnas() * valorHoraNocturna;
        double pagoDominical = request.getHorasDominicales() * valorHoraDominical;
        double pagoFestivo = request.getHorasFestivas() * valorHoraFestiva;

        double salarioBruto = pagoDiurno + pagoNocturno + pagoDominical + pagoFestivo;
        double descuentoParafiscales = salarioBruto * 0.08;
        double salarioNeto = Math.round((salarioBruto - descuentoParafiscales) * 100.0) / 100.0;

        double recargo = pagoNocturno + pagoDominical + pagoFestivo;

        String detalle = String.format("Diurnas: %d (%.0f), Nocturnas: %d (%.0f), Dominicales: %d (%.0f), Festivas: %d (%.0f)",
                request.getHorasDiurnas(), pagoDiurno,
                request.getHorasNocturnas(), pagoNocturno,
                request.getHorasDominicales(), pagoDominical,
                request.getHorasFestivas(), pagoFestivo
        );

        DocentePago entity = DocentePago.builder()
                .tipoContrato(request.getTipoContrato())
                .horasDiurnas(request.getHorasDiurnas())
                .horasNocturnas(request.getHorasNocturnas())
                .horasDominicales(request.getHorasDominicales())
                .horasFestivas(request.getHorasFestivas())
                .salarioBruto(salarioBruto)
                .descuentoParafiscales(descuentoParafiscales)
                .salarioNeto(salarioNeto)
                .fechaRegistro(LocalDateTime.now())
                .build();

        pagoRepository.save(entity);

        return new PagoResponse(salarioBruto, descuentoParafiscales, salarioNeto, recargo, detalle);
    }
}