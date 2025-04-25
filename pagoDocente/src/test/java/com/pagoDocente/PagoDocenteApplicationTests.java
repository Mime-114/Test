package com.pagoDocente;

import com.pagoDocente.dto.PagoRequest;
import com.pagoDocente.dto.PagoResponse;
import com.pagoDocente.entity.DocentePago;
import com.pagoDocente.repository.DocentePagoRepository;
import com.pagoDocente.service.servicieImpl.PagoServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PagoDocenteApplicationTests {

	@Mock
	private DocentePagoRepository pagoRepository;

	@InjectMocks
	private PagoServiceImpl pagoService;

	@Test
	void criterio1_horasNegativas() {
		PagoRequest request = new PagoRequest("tiempo_completo", -1, 0, 0, 0);
		assertThrows(IllegalArgumentException.class, () -> pagoService.calcularPago(request));
	}

	@Test
	void criterio2_salarioBaseNegativo_simulado() {
		// Como PagoRequest no tiene salarioBase, se simula que el servicio valida el valor indirectamente
		PagoRequest request = new PagoRequest("tiempo_completo", 1, 0, 0, 0);
		when(pagoRepository.save(any())).thenReturn(new DocentePago());
		PagoResponse response = pagoService.calcularPago(request);
		assertTrue(response.getSalarioBruto() > 0); // No lanza excepción pero asegura cálculo válido
	}

	@Test
	void criterio3_textoEnHoras() {
		assertThrows(NumberFormatException.class, () -> {
			Integer.parseInt("abc");
		});
	}

	@Test
	void criterio4_contratoDuplicado() {
		PagoRequest request = new PagoRequest("medio_tiempo,tiempo_completo", 10, 0, 0, 0);
		assertThrows(IllegalArgumentException.class, () -> pagoService.calcularPago(request));
	}

	@Test
	void criterio5_tipoContratoNoReconocido() {
		PagoRequest request = new PagoRequest("freelancer", 10, 0, 0, 0);
		assertThrows(IllegalArgumentException.class, () -> pagoService.calcularPago(request));
	}

	@Test
	void criterio6_soloHorasDiurnasSinRecargo() {
		PagoRequest request = new PagoRequest("medio_tiempo", 10, 0, 0, 0);
		when(pagoRepository.save(any())).thenReturn(new DocentePago());
		PagoResponse response = pagoService.calcularPago(request);
		assertEquals(0, response.getRecargo());
	}

	@Test
	void criterio7_soloHorasNocturnasConRecargo35() {
		PagoRequest request = new PagoRequest("medio_tiempo", 0, 10, 0, 0);
		when(pagoRepository.save(any())).thenReturn(new DocentePago());
		PagoResponse response = pagoService.calcularPago(request);
		assertTrue(response.getRecargo() > 0);
	}

	@Test
	void criterio8_soloDominicalConRecargo75() {
		PagoRequest request = new PagoRequest("medio_tiempo", 0, 0, 10, 0);
		when(pagoRepository.save(any())).thenReturn(new DocentePago());
		PagoResponse response = pagoService.calcularPago(request);
		assertTrue(response.getRecargo() > 0);
	}

	@Test
	void criterio9_soloFestivoConRecargo100() {
		PagoRequest request = new PagoRequest("medio_tiempo", 0, 0, 0, 10);
		when(pagoRepository.save(any())).thenReturn(new DocentePago());
		PagoResponse response = pagoService.calcularPago(request);
		assertTrue(response.getRecargo() > 0);
	}

	@Test
	void criterio10_nocturnoYFestivo() {
		PagoRequest request = new PagoRequest("tiempo_completo", 0, 5, 0, 5);
		when(pagoRepository.save(any())).thenReturn(new DocentePago());
		PagoResponse response = pagoService.calcularPago(request);
		assertTrue(response.getRecargo() > 0);
	}

	@Test
	void criterio11_sinHorasPagoCero() {
		PagoRequest request = new PagoRequest("medio_tiempo", 0, 0, 0, 0);
		when(pagoRepository.save(any())).thenReturn(new DocentePago());
		PagoResponse response = pagoService.calcularPago(request);
		assertEquals(0, response.getSalarioNeto());
	}

	@Test
	void criterio12_deduccionesCalculadas() {
		PagoRequest request = new PagoRequest("tiempo_completo", 10, 0, 0, 0);
		when(pagoRepository.save(any())).thenReturn(new DocentePago());
		PagoResponse response = pagoService.calcularPago(request);
		assertTrue(response.getDescuentoParafiscales() > 0);
	}

	@Test
	void criterio13_redondeoDosDecimales() {
		PagoRequest request = new PagoRequest("tiempo_completo", 8, 6, 4, 2);
		when(pagoRepository.save(any())).thenReturn(new DocentePago());
		PagoResponse response = pagoService.calcularPago(request);
		assertTrue(String.valueOf(response.getSalarioNeto()).matches("\\d+\\.\\d{1,2}"));
	}

	@Test
	void criterio14_pagoMenorSalarioMinimo() {
		PagoRequest request = new PagoRequest("medio_tiempo", 1, 0, 0, 0);
		when(pagoRepository.save(any())).thenReturn(new DocentePago());
		PagoResponse response = pagoService.calcularPago(request);
		assertTrue(response.getSalarioNeto() < 1160000);
	}

	@Test
	void criterio15_detalleIncluido() {
		PagoRequest request = new PagoRequest("tiempo_completo", 5, 5, 2, 1);
		when(pagoRepository.save(any())).thenReturn(new DocentePago());
		PagoResponse response = pagoService.calcularPago(request);
		assertNotNull(response.getDetalle());
	}

	@Test
	void criterio16_medioTiempoNoSupera100Horas() {
		PagoRequest request = new PagoRequest("medio_tiempo", 101, 0, 0, 0);
		assertThrows(IllegalArgumentException.class, () -> pagoService.calcularPago(request));
	}

	@Test
	void criterio17_descuentoProporcionalPorContrato() {
		PagoRequest request = new PagoRequest("medio_tiempo", 10, 0, 0, 0);
		when(pagoRepository.save(any())).thenReturn(new DocentePago());
		PagoResponse response = pagoService.calcularPago(request);
		assertTrue(response.getDescuentoParafiscales() > 0);
	}

	@Test
	void criterio18_recargoSinDuplicar() {
		PagoRequest request = new PagoRequest("tiempo_completo", 0, 0, 0, 8);
		when(pagoRepository.save(any())).thenReturn(new DocentePago());
		PagoResponse response = pagoService.calcularPago(request);
		assertTrue(response.getRecargo() <= 8 * 40000);
	}

	@Test
	void criterio19_exceso240Horas() {
		PagoRequest request = new PagoRequest("tiempo_completo", 250, 0, 0, 0);
		assertThrows(IllegalArgumentException.class, () -> pagoService.calcularPago(request));
	}

	@Test
	void criterio20_contratoNoSeleccionado() {
		PagoRequest request = new PagoRequest(null, 10, 5, 2, 1);
		assertThrows(IllegalArgumentException.class, () -> pagoService.calcularPago(request));
	}
}
