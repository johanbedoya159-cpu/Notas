package co.edu.uco.nota.infraestructura.output.serviciosexternos.notificaciones.dto;

public record CorreoAcsDTO(String senderAddress, ContenidoCorreoDTO content, DestinatariosCorreoDTO recipients) {

}
