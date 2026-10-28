package co.edu.uco.nota.inicializador.configuracion;

import java.time.Clock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import co.edu.uco.nota.servicios.casosuso.notaestudiante.consultarnotasregistradas.ConsultarNotasRegistradasCasoUso;
import co.edu.uco.nota.servicios.casosuso.notaestudiante.consultarnotasregistradas.impl.ConsultarNotasRegistradasCasoUsoImpl;
import co.edu.uco.nota.servicios.casosuso.notaestudiante.registrarnotaestudiante.RegistrarNotaEstudianteCasoUso;
import co.edu.uco.nota.servicios.casosuso.notaestudiante.registrarnotaestudiante.impl.RegistrarNotaEstudianteCasoUsoImpl;
import co.edu.uco.nota.servicios.casosuso.notaestudiante.registrarnotaestudiante.impl.RepositoriosRegistroNota;
import co.edu.uco.nota.servicios.casosuso.notaestudiante.registrarnotaestudiante.impl.ServiciosRegistroNota;
import co.edu.uco.nota.servicios.input.adapters.ConsultarNotasRegistradasInteractor;
import co.edu.uco.nota.servicios.input.adapters.RegistrarNotaEstudianteInteractor;
import co.edu.uco.nota.servicios.input.ports.ConsultarNotasRegistradasInputPort;
import co.edu.uco.nota.servicios.input.ports.RegistrarNotaEstudianteInputPort;
import co.edu.uco.nota.servicios.output.ports.repositorios.DocenteRepositorio;
import co.edu.uco.nota.servicios.output.ports.repositorios.EstudianteGrupoRepositorio;
import co.edu.uco.nota.servicios.output.ports.repositorios.EstudianteRepositorio;
import co.edu.uco.nota.servicios.output.ports.repositorios.GrupoRepositorio;
import co.edu.uco.nota.servicios.output.ports.repositorios.NotaEstudianteRepositorio;
import co.edu.uco.nota.servicios.output.ports.repositorios.NotaRepositorio;
import co.edu.uco.nota.servicios.output.ports.serviciosexternos.catalogos.CatalogoParametrosServicio;
import co.edu.uco.nota.servicios.output.ports.serviciosexternos.notificaciones.NotificacionServicio;

@Configuration
public class NotaEstudianteConfiguracion {

	@Bean
	RegistrarNotaEstudianteCasoUso registrarNotaEstudianteCasoUso(final DocenteRepositorio docenteRepositorio,
			final EstudianteRepositorio estudianteRepositorio, final NotaRepositorio notaRepositorio,
			final GrupoRepositorio grupoRepositorio, final EstudianteGrupoRepositorio estudianteGrupoRepositorio,
			final NotaEstudianteRepositorio notaEstudianteRepositorio, final CatalogoParametrosServicio parametros,
			final NotificacionServicio notificaciones, final Clock reloj) {
		return new RegistrarNotaEstudianteCasoUsoImpl(
				new RepositoriosRegistroNota(docenteRepositorio, estudianteRepositorio, notaRepositorio,
						grupoRepositorio, estudianteGrupoRepositorio, notaEstudianteRepositorio),
				new ServiciosRegistroNota(parametros, notificaciones), reloj);
	}

	@Bean
	RegistrarNotaEstudianteInputPort registrarNotaEstudianteInputPort(final RegistrarNotaEstudianteCasoUso casoUso) {
		return new RegistrarNotaEstudianteInteractor(casoUso);
	}

	@Bean
	ConsultarNotasRegistradasCasoUso consultarNotasRegistradasCasoUso(final NotaRepositorio notaRepositorio,
			final NotaEstudianteRepositorio notaEstudianteRepositorio) {
		return new ConsultarNotasRegistradasCasoUsoImpl(notaRepositorio, notaEstudianteRepositorio);
	}

	@Bean
	ConsultarNotasRegistradasInputPort consultarNotasRegistradasInputPort(
			final ConsultarNotasRegistradasCasoUso casoUso) {
		return new ConsultarNotasRegistradasInteractor(casoUso);
	}

}
