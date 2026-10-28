package co.edu.uco.nota.crosscutting.parametros;

public enum ClaveParametro {

	NOTA_VALOR_MINIMO("nota.valor.minimo", "0"),
	NOTA_VALOR_MAXIMO("nota.valor.maximo", "5"),
	NOTA_VALOR_APROBATORIO("nota.valor.aprobatorio", "3");

	private final String clave;
	private final String valorPorDefecto;

	ClaveParametro(final String clave, final String valorPorDefecto) {
		this.clave = clave;
		this.valorPorDefecto = valorPorDefecto;
	}

	public String getClave() {
		return clave;
	}

	public String getValorPorDefecto() {
		return valorPorDefecto;
	}

}
