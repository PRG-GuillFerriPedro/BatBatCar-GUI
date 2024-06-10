package es.batbatcar.v2p4.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import es.batbatcar.v2p4.exceptions.ReservaAlreadyExistsException;
import es.batbatcar.v2p4.exceptions.ReservaNotFoundException;
import es.batbatcar.v2p4.modelo.dto.Reserva;
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

	@GetMapping("reserva")
	public String getReservaAction(@RequestParam String codReserva, @RequestParam int codViaje, Model model) {
		Viaje v = new Viaje(codViaje);
		model.addAttribute("viaje", v);
		model.addAttribute("reserva", viajesRepository.findReservaByID(codReserva));
		model.addAttribute("titulo", "Vista de la Reserva " + codReserva);
		return "reserva/reserva_detalle";
	}

	@GetMapping("reserva-form")
	public String addReservaFormAction(@RequestParam int codViaje, Model model) {
		Viaje v = new Viaje(codViaje);
		model.addAttribute("viaje", v);
		model.addAttribute("reservas", viajesRepository.findReservasByViaje(v));
		model.addAttribute("titulo", "Listado de Reservas del viaje " + codViaje);
		return "reserva/reserva_form";
	}

	@PostMapping(value = "/reserva-cancel")
	public String postAddAction(@RequestParam String codReserva, RedirectAttributes redirectAttributes) {
		try {
			viajesRepository.remove(viajesRepository.findReservaByID(codReserva));
		} catch (ReservaNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "redirect:/viajes";
	}

	@PostMapping(value = "/reserva-add")
	public String postAddAction(@RequestParam Map<String, String> params, RedirectAttributes redirectAttributes) {
		String codViaje = params.get("codViaje");
		String usuario = params.get("user");
		int cantidad = Integer.parseInt(params.get("codViaje"));
		HashMap<String, String> errores = new HashMap<>();
		Viaje v = findViaje(Integer.parseInt(codViaje));

		if (usuario.isBlank()) {
			errores.put("usuario", "Debes de introducir un usuario");
		}
		if (usuario.contentEquals(v.getPropietario())) {
			errores.put("usuario", "El usuario debe ser diferente al propietario del viaje");
		}
		if (!v.estaDisponible()) {
			errores.put("viaje", "En este viaje no se pueden hacer reservas");
		}
		if (getNumPlazasReservadas(v) + cantidad > v.getPlazasOfertadas()) {
			errores.put("cantidad", "Has excedido el numero de plazas disponibles");
		}
		if (usuarioYaRealizoReservas(v, usuario)) {
			errores.put("reservas", "Ya has realizado una reserva en este viaje.(Solo lo puedes hacer una vez)");
		}
		if (errores.size() > 0) {
			redirectAttributes.addFlashAttribute("errores", errores);
			return "redirect:/reserva-form";
		}
		try {
			viajesRepository.save(new Reserva(codViaje + "-" + viajesRepository.findReservasByViaje(v).size() + 1,
					usuario, cantidad, v));
		} catch (ReservaAlreadyExistsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ReservaNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		redirectAttributes.addFlashAttribute("infoMessage", "Reserva añadida con éxito");
		return "redirect:/viajes";
	}

	private int getNumPlazasReservadas(Viaje v) {
		List<Reserva> reservas = viajesRepository.findReservasByViaje(v);
		int numPlazas = 0;
		for (Reserva reserva : reservas) {
			numPlazas = +reserva.getPlazasSolicitadas();
		}
		return numPlazas;
	}

	private Viaje findViaje(int codViaje) {
		Set<Viaje> viajes = viajesRepository.findAll();
		Viaje viaje = new Viaje(codViaje);
		for (Viaje viaje2 : viajes) {
			if (viaje2.equals(viaje)) {
				viaje = viaje2;
			}
		}
		return viaje;
	}

	private boolean usuarioYaRealizoReservas(Viaje v, String usuario) {
		List<Reserva> reservas = viajesRepository.findReservasByViaje(v);

		for (Reserva reserva : reservas) {
			if (reserva.getUsuario().contentEquals(usuario)) {
				return true;
			}
		}
		return false;
	}
}
