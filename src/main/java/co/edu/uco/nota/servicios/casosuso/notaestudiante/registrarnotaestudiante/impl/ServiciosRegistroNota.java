package co.edu.uco.nota.servicios.casosuso.notaestudiante.registrarnotaestudiante.impl;

import co.edu.uco.nota.servicios.output.ports.serviciosexternos.catalogos.CatalogoParametrosServicio;
import co.edu.uco.nota.servicios.output.ports.serviciosexternos.notificaciones.NotificacionServicio;

public record ServiciosRegistroNota(CatalogoParametrosServicio parametros, NotificacionServicio notificaciones) {

}
