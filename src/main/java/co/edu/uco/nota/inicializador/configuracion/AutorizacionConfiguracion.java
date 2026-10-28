package co.edu.uco.nota.inicializador.configuracion;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import co.edu.uco.nota.infraestructura.input.autorizacion.AutorizacionInterceptor;
import co.edu.uco.nota.infraestructura.input.autorizacion.LectorPrincipalAzure;
import tools.jackson.databind.json.JsonMapper;

@Configuration
public class AutorizacionConfiguracion implements WebMvcConfigurer {

	private final AutorizacionInterceptor interceptor;

	public AutorizacionConfiguracion(@Value("${nota.autorizacion.habilitada:false}") final boolean habilitada,
			final JsonMapper json) {
		this.interceptor = new AutorizacionInterceptor(habilitada, new LectorPrincipalAzure(json));
	}

	@Override
	public void addInterceptors(final InterceptorRegistry registro) {
		registro.addInterceptor(interceptor).addPathPatterns("/api/**");
	}

}
