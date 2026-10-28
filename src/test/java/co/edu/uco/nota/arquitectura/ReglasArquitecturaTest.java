package co.edu.uco.nota.arquitectura;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;

class ReglasArquitecturaTest {

	private static final String RAIZ = "co.edu.uco.nota";
	private static JavaClasses clases;

	@BeforeAll
	static void importar() {
		clases = new ClassFileImporter().withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
				.importPackages(RAIZ);
	}

	@Test
	@DisplayName("El dominio no conoce la infraestructura, el inicializador ni frameworks")
	void dominioLimpio() {
		noClasses().that().resideInAPackage("..dominio..").should().dependOnClassesThat()
				.resideInAnyPackage("..infraestructura..", "..inicializador..", "..servicios.input..",
						"..servicios.casosuso..", "org.springframework..", "jakarta..")
				.check(clases);
	}

	@Test
	@DisplayName("Los servicios no dependen de la infraestructura ni de frameworks")
	void serviciosLimpios() {
		noClasses().that().resideInAPackage("..servicios..").should().dependOnClassesThat()
				.resideInAnyPackage("..infraestructura..", "..inicializador..", "org.springframework..", "jakarta..")
				.check(clases);
	}

	@Test
	@DisplayName("Lo transversal no depende de ninguna capa")
	void transversalIndependiente() {
		noClasses().that().resideInAPackage("..crosscutting..").should().dependOnClassesThat()
				.resideInAnyPackage("..dominio..", "..servicios..", "..infraestructura..", "..inicializador..",
						"org.springframework..")
				.check(clases);
	}

	@Test
	@DisplayName("Los controladores solo hablan con puertos de entrada, no con casos de uso ni repositorios")
	void controladoresUsanPuertos() {
		noClasses().that().resideInAPackage("..infraestructura.input..").should().dependOnClassesThat()
				.resideInAnyPackage("..servicios.casosuso..", "..servicios.output..", "..servicios.input.adapters..",
						"..dominio..", "..infraestructura.output..")
				.check(clases);
	}

	@Test
	@DisplayName("Los adaptadores de salida no dependen de la entrada")
	void salidaNoConoceEntrada() {
		noClasses().that().resideInAPackage("..infraestructura.output..").should().dependOnClassesThat()
				.resideInAnyPackage("..infraestructura.input..", "..servicios.input..", "..servicios.casosuso..")
				.check(clases);
	}

	@Test
	@DisplayName("Los casos de uso se implementan en el paquete impl y no se anotan con Spring")
	void casosUsoEnImpl() {
		classes().that().resideInAPackage("..servicios.casosuso..").and().haveSimpleNameEndingWith("CasoUsoImpl")
				.should().resideInAPackage("..impl").andShould().notBeAnnotatedWith("org.springframework.stereotype.Service")
				.check(clases);
	}

	@Test
	@DisplayName("Las entidades JPA solo viven en infraestructura")
	void entidadesJpaEnInfraestructura() {
		classes().that().areAnnotatedWith("jakarta.persistence.Entity").should()
				.resideInAPackage("..infraestructura.output.repositorios.estructurados.jpa.entidades..").check(clases);
	}

}
