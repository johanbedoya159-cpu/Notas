package co.edu.uco.nota.servicios.input.adapters;

import co.edu.uco.nota.crosscutting.helper.ObjetoHelper;
import co.edu.uco.nota.dominio.notaestudiante.registrarnotaestudiante.RegistrarNotaEstudianteDominio;
import co.edu.uco.nota.servicios.casosuso.notaestudiante.registrarnotaestudiante.RegistrarNotaEstudianteCasoUso;
import co.edu.uco.nota.servicios.input.dto.request.RegistrarNotaEstudianteRequestDTO;
import co.edu.uco.nota.servicios.input.ports.RegistrarNotaEstudianteInputPort;

public final class RegistrarNotaEstudianteInteractor implements RegistrarNotaEstudianteInputPort {

	private final RegistrarNotaEstudianteCasoUso casoUso;

	public RegistrarNotaEstudianteInteractor(final RegistrarNotaEstudianteCasoUso casoUso) {
		this.casoUso = ObjetoHelper.requerirDependencia(casoUso, "el caso de uso de registrar nota");
	}

	@Override
	public void ejecutar(final RegistrarNotaEstudianteRequestDTO solicitud) {
		final RegistrarNotaEstudianteRequestDTO datos = ObjetoHelper.obtenerValorPorDefecto(solicitud,
				new RegistrarNotaEstudianteRequestDTO(null, null, null, null));
		casoUso.ejecutar(RegistrarNotaEstudianteDominio.crear(datos.docente(), datos.estudiante(), datos.nota(),
				datos.valor()));
	}

}
