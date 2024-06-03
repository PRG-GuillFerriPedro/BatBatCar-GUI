package es.batbatcar.v2p4.modelo.dao.sqldao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import es.batbatcar.v2p4.exceptions.ReservaAlreadyExistsException;
import es.batbatcar.v2p4.exceptions.ReservaNotFoundException;
import es.batbatcar.v2p4.modelo.dao.interfaces.ReservaDAO;
import es.batbatcar.v2p4.modelo.dto.Reserva;
import es.batbatcar.v2p4.modelo.dto.viaje.Viaje;
import es.batbatcar.v2p4.modelo.services.MySQLConnection;

@Repository
public class SQLReservaDAO implements ReservaDAO {

	@Autowired
	private MySQLConnection mySQLConnection;
	private static final String SQL_TABLE = "reservas";

	private static final String SQL_CODRESERVA = "codigoReserva";
	private static final String SQL_USUARIO = "usuario";
	private static final String SQL_PLAZAS = "plazasSolicitadas";
	private static final String SQL_FECHAREALIZACION = "fechaRealizacion";
	private static final String SQL_CODVIAJE = "viaje";

	@Override
	public Set<Reserva> findAll() {
		Set<Reserva> reservas = new HashSet<>();

		try {
			Connection con = mySQLConnection.getConnection();
			Statement statement = con.createStatement();
			ResultSet rs = statement.executeQuery("SELECT * FROM reservas");
			while (rs.next()) {
				reservas.add(new Reserva(rs.getString(SQL_CODRESERVA), rs.getString(SQL_USUARIO), rs.getInt(SQL_PLAZAS),
						rs.getTimestamp(SQL_FECHAREALIZACION).toLocalDateTime(), new Viaje(rs.getInt(SQL_CODVIAJE))));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return reservas;
	}

	@Override
	public Reserva findById(String id) {
		String sql = "SELECT * FROM reservas WHERE codigoReserva=?";
		Connection con = mySQLConnection.getConnection();
		try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
			preparedStatement.setString(1, id);

			ResultSet rs = preparedStatement.executeQuery(sql);
			if (rs.next()) {
				return new Reserva(rs.getString(SQL_CODRESERVA), rs.getString(SQL_USUARIO), rs.getInt(SQL_PLAZAS),
						rs.getTimestamp(SQL_FECHAREALIZACION).toLocalDateTime(), new Viaje(rs.getInt(SQL_CODVIAJE)));
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
	public ArrayList<Reserva> findAllByUser(String user) {
		Set<Reserva> reservas = new HashSet<>();
		String sql = "SELECT * FROM reservas WHERE usuario=?";
		Connection con = mySQLConnection.getConnection();
		try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
			preparedStatement.setString(1, user);

			ResultSet rs = preparedStatement.executeQuery(sql);
			while (rs.next()) {
				reservas.add(new Reserva(rs.getString(SQL_CODRESERVA), rs.getString(SQL_USUARIO), rs.getInt(SQL_PLAZAS),
						rs.getTimestamp(SQL_FECHAREALIZACION).toLocalDateTime(), new Viaje(rs.getInt(SQL_CODVIAJE))));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return (ArrayList<Reserva>) reservas;
	}

	@Override
	public ArrayList<Reserva> findAllByTravel(Viaje viaje) {
		Set<Reserva> reservas = new HashSet<>();
		String sql = "SELECT * FROM reservas WHERE viaje=?";
		Connection con = mySQLConnection.getConnection();
		try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
			preparedStatement.setInt(1, viaje.getCodViaje());

			ResultSet rs = preparedStatement.executeQuery(sql);
			while (rs.next()) {
				reservas.add(new Reserva(rs.getString(SQL_CODRESERVA), rs.getString(SQL_USUARIO), rs.getInt(SQL_PLAZAS),
						rs.getTimestamp(SQL_FECHAREALIZACION).toLocalDateTime(), new Viaje(rs.getInt(SQL_CODVIAJE))));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return (ArrayList<Reserva>) reservas;
	}

	@Override
	public Reserva getById(String id) throws ReservaNotFoundException {
		Reserva reserva = findById(id);
		if (reserva == null) {
			throw new ReservaNotFoundException(id);
		}

		return reserva;
	}

	@Override
	public List<Reserva> findAllBySearchParams(Viaje viaje, String searchParams) {
		Set<Reserva> reservas = new HashSet<>();
		String sql = "SELECT * FROM reservas WHERE viaje=? AND codigoReserva LIKE '%%?%%' OR usuario LIKE '%%?%%' ";
		Connection con = mySQLConnection.getConnection();
		try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
			preparedStatement.setInt(1, viaje.getCodViaje());
			preparedStatement.setString(2, searchParams);
			preparedStatement.setString(3, searchParams);
			ResultSet rs = preparedStatement.executeQuery(sql);
			while (rs.next()) {
				reservas.add(new Reserva(rs.getString(SQL_CODRESERVA), rs.getString(SQL_USUARIO), rs.getInt(SQL_PLAZAS),
						rs.getTimestamp(SQL_FECHAREALIZACION).toLocalDateTime(), new Viaje(rs.getInt(SQL_CODVIAJE))));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return (ArrayList<Reserva>) reservas;
	}

	@Override
	public void add(Reserva reserva) throws ReservaAlreadyExistsException {
		Set<Reserva> reservas = findAll();
		if (reservas.contains(reserva)) {
			throw new ReservaAlreadyExistsException(reserva);
		} else {
			String sql = "INSERT INTO reservas(codigoReserva,usuario,plazasSolicitadas,fechaRealizacion,viaje) VALUES (?,?,?,?,?)";
			Connection con = mySQLConnection.getConnection();
			try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
				preparedStatement.setString(1, reserva.getCodigoReserva());
				preparedStatement.setString(2, reserva.getUsuario());
				preparedStatement.setInt(3, reserva.getPlazasSolicitadas());
				preparedStatement.setTimestamp(4, Timestamp.valueOf(reserva.getFechaRealizacion()));
				preparedStatement.setInt(5, reserva.getCodigoViaje());
				preparedStatement.executeUpdate(sql);

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Override
	public void update(Reserva reserva) throws ReservaNotFoundException {
		Set<Reserva> reservas = findAll();
		if (!reservas.contains(reserva)) {
			throw new ReservaNotFoundException(reserva.getCodigoReserva());
		} else {
			String sql = "UPDATE reservas SET usuario='?',plazasSolicitadas=?,fechaRealizacion='?',viaje=? WHERE codigoReserva='?'";
			Connection con = mySQLConnection.getConnection();
			try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
				preparedStatement.setString(1, reserva.getUsuario());
				preparedStatement.setInt(2, reserva.getPlazasSolicitadas());
				preparedStatement.setTimestamp(3, Timestamp.valueOf(reserva.getFechaRealizacion()));
				preparedStatement.setInt(4, reserva.getCodigoViaje());
				preparedStatement.setString(5, reserva.getCodigoReserva());
				preparedStatement.executeUpdate(sql);

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Override
	public void remove(Reserva reserva) throws ReservaNotFoundException {
		Set<Reserva> reservas = findAll();
		if (!reservas.contains(reserva)) {
			throw new ReservaNotFoundException(reserva.getCodigoReserva());
		} else {
			String sql = "DELETE FROM reservas WHERE codigoReserva='?'";
			Connection con = mySQLConnection.getConnection();
			try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {

				preparedStatement.setString(1, reserva.getCodigoReserva());
				preparedStatement.executeUpdate(sql);

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Override
	public int getNumPlazasReservadasEnViaje(Viaje viaje) {
		/*
		 * List<Reserva> reservas = findAllByTravel(viaje); int numPlazas = 0; for
		 * (Reserva reserva : reservas) { numPlazas = +reserva.getPlazasSolicitadas(); }
		 * return numPlazas;
		 */
		String sql = "SELECT SUM(reservas.plazasSolicitadas) FROM reservas WHERE viaje=? ";
		Connection con = mySQLConnection.getConnection();
		try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
			preparedStatement.setInt(1, viaje.getCodViaje());

			ResultSet rs = preparedStatement.executeQuery(sql);

			return rs.getInt(0);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public Reserva findByUserInTravel(String usuario, Viaje viaje) {
		/*
		 * List<Reserva> reservas = findAllByTravel(viaje);
		 * 
		 * for (Reserva reserva : reservas) { if
		 * (reserva.getUsuario().contentEquals(usuario)) { return reserva; } } return
		 * null;
		 */

		String sql = "SELECT * FROM reservas WHERE viaje=? AND usuario = '?' ";
		Connection con = mySQLConnection.getConnection();
		try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
			preparedStatement.setInt(1, viaje.getCodViaje());
			preparedStatement.setString(2, usuario);

			ResultSet rs = preparedStatement.executeQuery(sql);
			if (rs.next()) {
				return new Reserva(rs.getString(SQL_CODRESERVA), rs.getString(SQL_USUARIO), rs.getInt(SQL_PLAZAS),
						rs.getTimestamp(SQL_FECHAREALIZACION).toLocalDateTime(), new Viaje(rs.getInt(SQL_CODVIAJE)));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
