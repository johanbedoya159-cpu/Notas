package co.edu.uco.nota.servicios.input.adapters;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import co.edu.uco.nota.crosscutting.helper.ObjetoHelper;
import co.edu.uco.nota.dominio.estudiante.consultarmisnotas.ConsultarMisNotasDominio;
import co.edu.uco.nota.servicios.casosuso.estudiante.consultarmisnotas.ConsultarMisNotasCasoUso;
import co.edu.uco.nota.servicios.casosuso.estudiante.consultarmisnotas.NotasDeGrupo;
import co.edu.uco.nota.servicios.input.dto.request.ConsultarMisNotasRequestDTO;
import co.edu.uco.nota.servicios.input.dto.response.MiGrupoResponseDTO;
import co.edu.uco.nota.servicios.input.dto.response.MiNotaResponseDTO;
import co.edu.uco.nota.servicios.input.ports.ConsultarMisNotasInputPort;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.GrupoEntidad;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.NotaEstudianteEntidad;

public final class ConsultarMisNotasInteractor implements ConsultarMisNotasInputPort {

	private final ConsultarMisNotasCasoUso casoUso;

	public ConsultarMisNotasInteractor(final ConsultarMisNotasCasoUso casoUso) {
		this.casoUso = ObjetoHelper.requerirDependencia(casoUso, "el caso de uso de consultar mis notas");
	}

	@Override
	public List<MiGrupoResponseDTO> ejecutar(final ConsultarMisNotasRequestDTO solicitud) {
		final ConsultarMisNotasRequestDTO datos = ObjetoHelper.obtenerValorPorDefecto(solicitud,
				new ConsultarMisNotasRequestDTO(null));
		return casoUso.ejecutar(new ConsultarMisNotasDominio(datos.correo())).stream()
				.map(ConsultarMisNotasInteractor::convertir).toList();
	}

	private static MiGrupoResponseDTO convertir(final NotasDeGrupo notasDeGrupo) {
		final Map<UUID, NotaEstudianteEntidad> registradas = notasDeGrupo.registradas().stream()
				.collect(Collectors.toMap(registro -> registro.nota().id(), Function.identity(), (uno, otro) -> uno));
		final GrupoEntidad grupo = notasDeGrupo.inscripcion().grupo();
		return new MiGrupoResponseDTO(grupo.id(), grupo.codigo(), grupo.nombre(), grupo.docente().nombre(),
				grupo.estado(),
				notasDeGrupo.notas().stream()
						.map(nota -> registradas.containsKey(nota.id())
								? MiNotaResponseDTO.registrada(nota, registradas.get(nota.id()))
								: MiNotaResponseDTO.pendiente(nota))
						.toList());
	}

}
