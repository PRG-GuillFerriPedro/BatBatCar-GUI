package es.batbatcar.v2p4.modelo.dao.sqldao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import es.batbatcar.v2p4.exceptions.ViajeNotFoundException;
import es.batbatcar.v2p4.modelo.dao.interfaces.ViajeDAO;
import es.batbatcar.v2p4.modelo.dto.viaje.EstadoViaje;
import es.batbatcar.v2p4.modelo.dto.viaje.Viaje;
import es.batbatcar.v2p4.modelo.services.MySQLConnection;

@Repository
public class SQLViajeDAO implements ViajeDAO {

	@Autowired
	private MySQLConnection mySQLConnection;
	/***
	 * Variables de los nombres de las filas para aumentar la acessibilidad
	 */
	private static final String SQL_TABLE = "viajes";
	private static final String SQL_CODVIAJE = "codViaje";
	private static final String SQL_PROPIETARIO = "propietario";
	private static final String SQL_RUTA = "ruta";
	private static final String SQL_FECHASALIDA = "fechaSalida";
	private static final String SQL_DURACION = "duracion";
	private static final String SQL_PRECIO = "precio";
	private static final String SQL_PLAZAS = "plazasOfertadas";
	private static final String SQL_ESTADOVIAJE = "estadoViaje";

	@Override
	public Set<Viaje> findAll() {
		Set<Viaje> viajes = new HashSet<>();

		try {
			Connection con = mySQLConnection.getConnection();
			Statement statement = con.createStatement();
			ResultSet rs = statement.executeQuery("SELECT * FROM viajes");
			while (rs.next()) {
				viajes.add(new Viaje(rs.getInt(SQL_CODVIAJE), rs.getString(SQL_PROPIETARIO), rs.getString(SQL_RUTA),
						rs.getTimestamp(SQL_FECHASALIDA).toLocalDateTime(), rs.getInt(SQL_DURACION),
						rs.getFloat(SQL_PRECIO), rs.getInt(SQL_PLAZAS),
						EstadoViaje.valueOf(rs.getString(SQL_ESTADOVIAJE))));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return viajes;
	}

	@Override
	public Set<Viaje> findAll(String city) {
		throw new RuntimeException("Not yet implemented");
	}

	@Override
	public Set<Viaje> findAll(EstadoViaje estadoViaje) {
		throw new RuntimeException("Not yet implemented");
	}

	@Override
	public Set<Viaje> findAll(Class<? extends Viaje> viajeClass) {
		throw new RuntimeException("Not yet implemented");
	}

	@Override
	public Viaje findById(int codViaje) {
		throw new RuntimeException("Not yet implemented");
	}

	@Override
	public Viaje getById(int codViaje) throws ViajeNotFoundException {
		throw new RuntimeException("Not yet implemented");
	}

	@Override
	public void add(Viaje viaje) {
		throw new RuntimeException("Not yet implemented");
	}

	@Override
	public void update(Viaje viaje) throws ViajeNotFoundException {
		throw new RuntimeException("Not yet implemented");
	}

	@Override
	public void remove(Viaje viaje) throws ViajeNotFoundException {
		throw new RuntimeException("Not yet implemented");
	}
}
