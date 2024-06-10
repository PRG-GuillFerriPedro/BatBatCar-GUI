package es.batbatcar.v2p4.modelo.dao.sqldao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import es.batbatcar.v2p4.exceptions.ViajeAlreadyExistsException;
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
		String sql = String.format("SELECT * FROM %s ", SQL_TABLE);
		try {
			Connection con = mySQLConnection.getConnection();
			Statement statement = con.createStatement();
			ResultSet rs = statement.executeQuery(sql);
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
		Set<Viaje> viajes = new HashSet<>();
		String delimitador = "-";
		String sql = String.format("SELECT * FROM %s WHERE SUBSTRING_INDEX(%s,'%s',-1)LIKE '%%?%%'", SQL_TABLE,
				SQL_RUTA, delimitador);
		Connection con = mySQLConnection.getConnection();
		try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
			preparedStatement.setString(1, city);
			ResultSet rs = preparedStatement.executeQuery();
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
	public Set<Viaje> findAll(EstadoViaje estadoViaje) {
		Set<Viaje> viajes = new HashSet<>();
		String sql = String.format("SELECT * FROM %s WHERE %s=?", SQL_TABLE, SQL_ESTADOVIAJE);
		Connection con = mySQLConnection.getConnection();
		try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
			preparedStatement.setString(1, estadoViaje.name());
			ResultSet rs = preparedStatement.executeQuery();
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
	public Set<Viaje> findAll(Class<? extends Viaje> viajeClass) {
		Set<Viaje> viajes = findAll();
		TreeSet<Viaje> viajesDePaso = new TreeSet<>();
		for (Viaje viaje : viajes) {
			if (viaje.getClass() == viajeClass) {
				viajesDePaso.add(viaje);
			}
		}
		return viajesDePaso;
	}

	@Override
	public Viaje findById(int codViaje) {
		String sql = String.format("SELECT * FROM %s WHERE %s=?", SQL_TABLE, SQL_CODVIAJE);
		Connection con = mySQLConnection.getConnection();
		try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
			preparedStatement.setInt(1, codViaje);

			ResultSet rs = preparedStatement.executeQuery();
			if (rs.next()) {
				return new Viaje(rs.getInt(SQL_CODVIAJE), rs.getString(SQL_PROPIETARIO), rs.getString(SQL_RUTA),
						rs.getTimestamp(SQL_FECHASALIDA).toLocalDateTime(), rs.getInt(SQL_DURACION),
						rs.getFloat(SQL_PRECIO), rs.getInt(SQL_PLAZAS),
						EstadoViaje.valueOf(rs.getString(SQL_ESTADOVIAJE)));
			} else {
				return null;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Viaje getById(int codViaje) throws ViajeNotFoundException {
		Viaje viaje = findById(codViaje);
		if (viaje == null) {
			throw new ViajeNotFoundException("El viaje seleccionado no existe");
		}

		return viaje;
	}

	@Override
	public void add(Viaje viaje) throws ViajeAlreadyExistsException {
		Set<Viaje> viajes = findAll();
		if (viajes.contains(viaje)) {
			throw new ViajeAlreadyExistsException(viaje.getCodViaje());
		} else {
			String sql = String.format("INSERT INTO %s(%s,%s,%s,%s,%s,%s,%s,%s) VALUES (?,?,?,?,?,?,?,?)", SQL_TABLE,
					SQL_CODVIAJE, SQL_PROPIETARIO, SQL_RUTA, SQL_FECHASALIDA, SQL_DURACION, SQL_PRECIO, SQL_PLAZAS,
					SQL_ESTADOVIAJE);
			Connection con = mySQLConnection.getConnection();
			try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
				preparedStatement.setInt(1, viaje.getCodViaje());
				preparedStatement.setString(2, viaje.getPropietario());
				preparedStatement.setString(3, viaje.getRuta());
				preparedStatement.setTimestamp(4, Timestamp.valueOf(viaje.getFechaSalida()));
				preparedStatement.setInt(5, (int) viaje.getDuracion());
				preparedStatement.setFloat(6, viaje.getPrecio());
				preparedStatement.setInt(7, viaje.getPlazasOfertadas());
				preparedStatement.setString(8, viaje.getEstado().name());
				preparedStatement.executeUpdate();

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Override
	public void update(Viaje viaje) throws ViajeNotFoundException {
		Viaje viajeAActualizar = findById(viaje.getCodViaje());
		if (viajeAActualizar != null) {
			String sql = String.format("UPDATE %s SET %s='?',%s=?,%s='?',%s=?,%s=?,%s=?,%s=? WHERE %s='?'", SQL_TABLE,
					SQL_PROPIETARIO, SQL_RUTA, SQL_FECHASALIDA, SQL_DURACION, SQL_PRECIO, SQL_PLAZAS, SQL_ESTADOVIAJE,
					SQL_CODVIAJE);
			Connection con = mySQLConnection.getConnection();
			try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
				preparedStatement.setString(1, viaje.getPropietario());
				preparedStatement.setString(2, viaje.getRuta());
				preparedStatement.setTimestamp(3, Timestamp.valueOf(viaje.getFechaSalida()));
				preparedStatement.setInt(4, (int) viaje.getDuracion());
				preparedStatement.setFloat(5, viaje.getPrecio());
				preparedStatement.setInt(6, viaje.getPlazasOfertadas());
				preparedStatement.setString(7, viaje.getEstado().name());
				preparedStatement.setInt(8, viaje.getCodViaje());
				preparedStatement.executeUpdate();

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void remove(Viaje viaje) throws ViajeNotFoundException {
		if (findById(viaje.getCodViaje()) != null) {
			String sql = String.format("DELETE FROM %s WHERE %s='?'", SQL_TABLE, SQL_CODVIAJE);
			Connection con = mySQLConnection.getConnection();
			try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {

				preparedStatement.setInt(1, viaje.getCodViaje());
				preparedStatement.executeUpdate();

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
}
