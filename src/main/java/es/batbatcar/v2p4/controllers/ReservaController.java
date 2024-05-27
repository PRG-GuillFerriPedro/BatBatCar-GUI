package es.batbatcar.v2p4.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.batbatcar.v2p4.modelo.dto.viaje.Viaje;
import es.batbatcar.v2p4.modelo.repositories.ViajesRepository;

@Controller
public class ReservaController {

	@Autowired
	private ViajesRepository viajesRepository;

	@GetMapping("reservas")
	public String getReservasAction(@RequestParam int codViaje, Model model) {
		Viaje v = new Viaje(codViaje);
		model.addAttribute("viaje", v);
		model.addAttribute("reservas", viajesRepository.findReservasByViaje(v));
		model.addAttribute("titulo", "Listado de Reservas del viaje " + codViaje);
		return "reserva/listado";
	}

	@GetMapping("reserva-add")
	public String addReservaAction(@RequestParam int codViaje, Model model) {
		Viaje v = new Viaje(codViaje);
		model.addAttribute("viaje", v);
		model.addAttribute("reservas", viajesRepository.findReservasByViaje(v));
		model.addAttribute("titulo", "Listado de Reservas del viaje " + codViaje);
		return "reserva/listado";
	}
}
