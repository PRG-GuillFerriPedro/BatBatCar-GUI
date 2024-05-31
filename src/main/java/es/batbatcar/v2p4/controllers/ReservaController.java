package es.batbatcar.v2p4.controllers;

import java.util.HashMap;
import java.util.Map;

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

	@GetMapping("reserva-form")
	public String addReservaFormAction(@RequestParam int codViaje, Model model) {
		Viaje v = new Viaje(codViaje);
		model.addAttribute("viaje", v);
		model.addAttribute("reservas", viajesRepository.findReservasByViaje(v));
		model.addAttribute("titulo", "Listado de Reservas del viaje " + codViaje);
		return "reserva/reserva_form";
	}

	@PostMapping(value = "/reserva-add")
	public String postAddAction(@RequestParam Map<String, String> params, RedirectAttributes redirectAttributes) {
		String codViaje = params.get("codViaje");
		String usuario = params.get("user");
		int cantidad = Integer.parseInt(params.get("codViaje"));
		HashMap<String, String> errores = new HashMap<>();
		Viaje v = new Viaje(Integer.parseInt(codViaje));
		if (usuario.isBlank()) {
			errores.put("usuario", "Deves de introducir un usuario");
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
}
