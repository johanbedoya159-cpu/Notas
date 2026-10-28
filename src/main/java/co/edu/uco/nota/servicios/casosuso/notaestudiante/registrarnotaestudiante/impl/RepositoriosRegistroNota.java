package co.edu.uco.nota.servicios.casosuso.notaestudiante.registrarnotaestudiante.impl;

import co.edu.uco.nota.servicios.output.ports.repositorios.DocenteRepositorio;
import co.edu.uco.nota.servicios.output.ports.repositorios.EstudianteGrupoRepositorio;
import co.edu.uco.nota.servicios.output.ports.repositorios.EstudianteRepositorio;
import co.edu.uco.nota.servicios.output.ports.repositorios.GrupoRepositorio;
import co.edu.uco.nota.servicios.output.ports.repositorios.NotaEstudianteRepositorio;
import co.edu.uco.nota.servicios.output.ports.repositorios.NotaRepositorio;

public record RepositoriosRegistroNota(DocenteRepositorio docente, EstudianteRepositorio estudiante,
		NotaRepositorio nota, GrupoRepositorio grupo, EstudianteGrupoRepositorio estudianteGrupo,
		NotaEstudianteRepositorio notaEstudiante) {

}
