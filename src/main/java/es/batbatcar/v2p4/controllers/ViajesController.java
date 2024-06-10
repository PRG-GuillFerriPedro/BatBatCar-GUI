package es.batbatcar.v2p4.controllers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import es.batbatcar.v2p4.exceptions.ViajeAlreadyExistsException;
import es.batbatcar.v2p4.exceptions.ViajeNotFoundException;
import es.batbatcar.v2p4.modelo.dto.viaje.Viaje;
import es.batbatcar.v2p4.modelo.repositories.ViajesRepository;
import es.batbatcar.v2p4.utils.Validator;

@Controller
public class ViajesController {

	@Autowired
	private ViajesRepository viajesRepository;

	/**
	 * Endpoint que muestra el listado de todos los viajes disponibles
	 *
	 */
	@GetMapping("viajes")
	public String getViajesAction(Model model) {
		model.addAttribute("viajes", viajesRepository.findAll());
		model.addAttribute("titulo", "Listado de viajes");
		return "viaje/listado";
	}

	@GetMapping("viaje")
	public String viewViajeAction(@RequestParam int codViaje, Model model) {
		Viaje viaje = findViaje(codViaje);

		model.addAttribute("viaje", viaje);
		model.addAttribute("reservas", viajesRepository.findReservasByViaje(viaje));
		return "viaje/viaje_detalle";
	}

	@GetMapping("viaje-form")
	public String addViajesAction(Model model) {
		model.addAttribute("viajes", viajesRepository.findAll());
		model.addAttribute("titulo", "Listado de viajes");
		return "viaje/viaje_form";
	}

	@PostMapping(value = "/viaje-add")
	public String postAddAction(@RequestParam Map<String, String> params, RedirectAttributes redirectAttributes) {
		String propietario = params.get("propietario");
		String ruta = params.get("ruta");
		int plazasOfertadas = Integer.parseInt(params.get("cantidadofertada"));
		float precio = Float.parseFloat(params.get("precio"));
		int duracionAproximadaEnMin = Integer.parseInt(params.get("durMin"));
		String diaSalidaString = params.get("diaSalida");
		String horaSalidaString = params.get("horaSalida");
		// Fin declaracion vars

		HashMap<String, String> errores = new HashMap<>();
		if (!Validator.isValidPropietario(propietario)) {
			errores.put("usuario", "Debe empezar por mayuscula, tener como minimo 5 caracteres y no estar vacío");
		}
		if (!Validator.isValidRuta(ruta)) {
			errores.put("ruta", "La ruta debe ser Origen-Destino");
		}
		if (!Validator.isValidPlazasOfertadas(plazasOfertadas)) {
			errores.put("cantidadofertada", "La cantidad de plazas ofertadas debe ser mayor a 0 y menor o igual a 6");
		}
		if (!Validator.isValidPrecio(precio)) {
			errores.put("precio", "El precio debe ser mayor a 0 y debe contener decimales");
		}
		if (!Validator.isValidDur(duracionAproximadaEnMin)) {
			errores.put("durMin", "La duracion debe ser mayor a 0");
		}
		if (!Validator.isValidDate(diaSalidaString)) {
			errores.put("diaSalida", "El dia de salida debe ser introducido");
		}
		if (!Validator.isValidTime(horaSalidaString)) {
			errores.put("horaSalida", "La hora de salida debe ser introducido");
		}
		if (errores.size() > 0) {
			redirectAttributes.addFlashAttribute("errores", errores);
			return "redirect:/viaje-form";
		}
		LocalDateTime t = LocalDateTime.parse(diaSalidaString + " " + horaSalidaString,
				DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
		Viaje v = new Viaje(viajesRepository.getNextCodViaje(), propietario, ruta, t, duracionAproximadaEnMin, precio,
				plazasOfertadas);
		if (!v.estaDisponible()) {
			v.cerrarViaje();
		}
		try {
			viajesRepository.save(v);
		} catch (ViajeAlreadyExistsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ViajeNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		redirectAttributes.addFlashAttribute("infoMessage", "Viaje añadido con éxito");
		return "redirect:/viajes";
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
}
