package co.edu.uco.nota.inicializador.configuracion;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import co.edu.uco.nota.servicios.casosuso.estudiante.consultarestudiantes.ConsultarEstudiantesCasoUso;
import co.edu.uco.nota.servicios.casosuso.estudiante.consultarestudiantes.impl.ConsultarEstudiantesCasoUsoImpl;
import co.edu.uco.nota.servicios.casosuso.grupo.consultarestudiantesgrupo.ConsultarEstudiantesGrupoCasoUso;
import co.edu.uco.nota.servicios.casosuso.grupo.consultarestudiantesgrupo.impl.ConsultarEstudiantesGrupoCasoUsoImpl;
import co.edu.uco.nota.servicios.casosuso.grupo.consultargrupos.ConsultarGruposCasoUso;
import co.edu.uco.nota.servicios.casosuso.grupo.consultargrupos.impl.ConsultarGruposCasoUsoImpl;
import co.edu.uco.nota.servicios.casosuso.nota.consultarnotasgrupo.ConsultarNotasGrupoCasoUso;
import co.edu.uco.nota.servicios.casosuso.nota.consultarnotasgrupo.impl.ConsultarNotasGrupoCasoUsoImpl;
import co.edu.uco.nota.servicios.input.adapters.ConsultarEstudiantesGrupoInteractor;
import co.edu.uco.nota.servicios.input.adapters.ConsultarEstudiantesInteractor;
import co.edu.uco.nota.servicios.input.adapters.ConsultarGruposInteractor;
import co.edu.uco.nota.servicios.input.adapters.ConsultarNotasGrupoInteractor;
import co.edu.uco.nota.servicios.input.ports.ConsultarEstudiantesGrupoInputPort;
import co.edu.uco.nota.servicios.input.ports.ConsultarEstudiantesInputPort;
import co.edu.uco.nota.servicios.input.ports.ConsultarGruposInputPort;
import co.edu.uco.nota.servicios.input.ports.ConsultarNotasGrupoInputPort;
import co.edu.uco.nota.servicios.output.ports.repositorios.EstudianteGrupoRepositorio;
import co.edu.uco.nota.servicios.output.ports.repositorios.EstudianteRepositorio;
import co.edu.uco.nota.servicios.output.ports.repositorios.GrupoRepositorio;
import co.edu.uco.nota.servicios.output.ports.repositorios.NotaRepositorio;

@Configuration
public class ConsultasConfiguracion {

	@Bean
	ConsultarEstudiantesCasoUso consultarEstudiantesCasoUso(final EstudianteRepositorio repositorio) {
		return new ConsultarEstudiantesCasoUsoImpl(repositorio);
	}

	@Bean
	ConsultarEstudiantesInputPort consultarEstudiantesInputPort(final ConsultarEstudiantesCasoUso casoUso) {
		return new ConsultarEstudiantesInteractor(casoUso);
	}

	@Bean
	ConsultarGruposCasoUso consultarGruposCasoUso(final GrupoRepositorio repositorio) {
		return new ConsultarGruposCasoUsoImpl(repositorio);
	}

	@Bean
	ConsultarGruposInputPort consultarGruposInputPort(final ConsultarGruposCasoUso casoUso) {
		return new ConsultarGruposInteractor(casoUso);
	}

	@Bean
	ConsultarEstudiantesGrupoCasoUso consultarEstudiantesGrupoCasoUso(final GrupoRepositorio grupoRepositorio,
			final EstudianteGrupoRepositorio estudianteGrupoRepositorio) {
		return new ConsultarEstudiantesGrupoCasoUsoImpl(grupoRepositorio, estudianteGrupoRepositorio);
	}

	@Bean
	ConsultarEstudiantesGrupoInputPort consultarEstudiantesGrupoInputPort(
			final ConsultarEstudiantesGrupoCasoUso casoUso) {
		return new ConsultarEstudiantesGrupoInteractor(casoUso);
	}

	@Bean
	ConsultarNotasGrupoCasoUso consultarNotasGrupoCasoUso(final GrupoRepositorio grupoRepositorio,
			final NotaRepositorio notaRepositorio) {
		return new ConsultarNotasGrupoCasoUsoImpl(grupoRepositorio, notaRepositorio);
	}

	@Bean
	ConsultarNotasGrupoInputPort consultarNotasGrupoInputPort(final ConsultarNotasGrupoCasoUso casoUso) {
		return new ConsultarNotasGrupoInteractor(casoUso);
	}

}
