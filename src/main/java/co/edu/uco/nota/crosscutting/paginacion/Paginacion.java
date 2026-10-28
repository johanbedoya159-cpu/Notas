package co.edu.uco.nota.crosscutting.paginacion;

public record Paginacion(int pagina, int tamano) {

	public static final int PAGINA_INICIAL = 0;
	public static final int TAMANO_POR_DEFECTO = 20;
	public static final int TAMANO_MAXIMO = 50;

	public static Paginacion porDefecto() {
		return new Paginacion(PAGINA_INICIAL, TAMANO_POR_DEFECTO);
	}

	public static Paginacion unico() {
		return new Paginacion(PAGINA_INICIAL, 1);
	}

}
