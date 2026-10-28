package co.edu.uco.nota.datosprueba;

import java.time.Clock;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import co.edu.uco.nota.crosscutting.parametros.ClaveParametro;
import co.edu.uco.nota.infraestructura.output.serviciosexternos.azure.IdentidadAzure;
import co.edu.uco.nota.infraestructura.output.serviciosexternos.catalogos.CatalogoAppConfigurationAdapter;
import co.edu.uco.nota.servicios.output.ports.serviciosexternos.catalogos.CatalogoParametrosServicio;
import co.edu.uco.nota.servicios.output.ports.serviciosexternos.notificaciones.NotificacionCorreo;
import co.edu.uco.nota.servicios.output.ports.serviciosexternos.notificaciones.NotificacionServicio;

public final class ServiciosExternosEnMemoria {

	public final Map<ClaveParametro, String> valoresParametros = new EnumMap<>(ClaveParametro.class);
	public final List<NotificacionCorreo> notificaciones = new ArrayList<>();

	public final CatalogoParametrosServicio parametros = clave -> valoresParametros.getOrDefault(clave,
			clave.getValorPorDefecto());

	public final NotificacionServicio notificador = notificaciones::add;

	public static CatalogoAppConfigurationAdapter catalogoLocal() {
		return new CatalogoAppConfigurationAdapter(
				new IdentidadAzure(null, null, "", "", Clock.systemUTC()), null, null, "", 300,
				Clock.systemUTC());
	}

}
