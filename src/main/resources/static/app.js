"use strict";

const API = "api/v1";
const TAMANO = 50;
const APRUEBA = 3;
const formatoNota = new Intl.NumberFormat("es-CO", { minimumFractionDigits: 1, maximumFractionDigits: 2 });
const formatoFecha = new Intl.DateTimeFormat("es-CO", { day: "numeric", month: "long", year: "numeric" });

const estado = {
	grupos: [],
	paginaGrupos: 0,
	totalPaginasGrupos: 0,
	grupo: null,
	evaluaciones: [],
	evaluacion: null,
	estudiantes: [],
	paginaEstudiantes: 0,
	totalPaginasEstudiantes: 0,
	registradas: new Map(),
	resultados: [],
	paginaResultados: 0,
	totalPaginasResultados: 0,
};

const $ = (id) => document.getElementById(id);

function el(etiqueta, atributos = {}, ...hijos) {
	const nodo = document.createElement(etiqueta);
	for (const [clave, valor] of Object.entries(atributos)) {
		if (valor === false || valor === null || valor === undefined) continue;
		if (clave === "class") nodo.className = valor;
		else if (clave === "style") nodo.setAttribute("style", valor);
		else if (clave.startsWith("on")) nodo.addEventListener(clave.slice(2), valor);
		else if (clave in nodo && typeof valor !== "string") nodo[clave] = valor;
		else nodo.setAttribute(clave, valor === true ? "" : valor);
	}
	for (const hijo of hijos.flat()) {
		if (hijo !== null && hijo !== undefined && hijo !== false) nodo.append(hijo);
	}
	return nodo;
}

class ErrorApi extends Error {
	constructor(mensaje, estadoHttp, trazabilidad) {
		super(mensaje);
		this.estadoHttp = estadoHttp;
		this.trazabilidad = trazabilidad;
	}
}

async function api(ruta, opciones = {}) {
	let respuesta;
	try {
		respuesta = await fetch(`${API}${ruta}`, {
			...opciones,
			headers: { Accept: "application/json", ...(opciones.body ? { "Content-Type": "application/json" } : {}) },
		});
	} catch {
		throw new ErrorApi("No hay conexión con el servidor. Revise su internet e intente de nuevo.", 0, "");
	}
	const cuerpo = await respuesta.json().catch(() => ({}));
	if (!respuesta.ok || cuerpo.exito === false) {
		const mensaje = cuerpo.mensajes?.[0] ?? "Ocurrió un problema inesperado. Intente de nuevo más tarde.";
		throw new ErrorApi(mensaje, respuesta.status, cuerpo.trazabilidad ?? "");
	}
	return cuerpo;
}

function consulta(parametros) {
	const texto = new URLSearchParams();
	for (const [clave, valor] of Object.entries(parametros)) {
		if (valor !== "" && valor !== null && valor !== undefined) texto.set(clave, valor);
	}
	return texto.toString();
}

function retrasar(funcion, espera = 250) {
	let temporizador;
	return (...argumentos) => {
		clearTimeout(temporizador);
		temporizador = setTimeout(() => funcion(...argumentos), espera);
	};
}

let temporizadorAviso;
function avisar(mensaje, esError = false) {
	const aviso = $("aviso-flotante");
	aviso.textContent = mensaje;
	aviso.classList.toggle("error", esError);
	aviso.classList.add("visible");
	clearTimeout(temporizadorAviso);
	temporizadorAviso = setTimeout(() => aviso.classList.remove("visible"), 4000);
}

function mensajeError(error) {
	return error.trazabilidad ? `${error.message} (código de seguimiento ${error.trazabilidad.slice(-8)})` : error.message;
}

function estadoEvaluacion(evaluacion, ahora = new Date()) {
	const inicio = new Date(evaluacion.fechaInicioRegistro);
	const fin = new Date(evaluacion.fechaFinRegistro);
	if (ahora < inicio) return { abierta: false, texto: `Abre el ${formatoFecha.format(inicio)}` };
	if (ahora >= fin) return { abierta: false, texto: `Cerró el ${formatoFecha.format(fin)}` };
	return { abierta: true, texto: `Abierta hasta el ${formatoFecha.format(fin)}` };
}

function leerNota(texto) {
	const limpio = texto.trim().replace(",", ".");
	if (limpio === "") return { error: "Escriba la nota." };
	if (!/^\d+(\.\d*)?$/.test(limpio)) return { error: "Escriba un número, por ejemplo 4,35." };
	const valor = Number(limpio);
	if (valor < 0 || valor > 5) return { error: "La nota debe estar entre 0 y 5." };
	const decimales = (limpio.split(".")[1] ?? "").replace(/0+$/, "");
	if (decimales.length > 2) return { error: "La nota puede tener máximo dos decimales." };
	return { valor };
}

function pintarEscala(escala, valor, vistaPrevia = false) {
	const tieneValor = typeof valor === "number" && !Number.isNaN(valor);
	escala.classList.toggle("sin-nota", !tieneValor);
	escala.classList.toggle("vista-previa", vistaPrevia);
	escala.classList.toggle("reprueba", tieneValor && valor < APRUEBA);
	if (tieneValor) escala.style.setProperty("--posicion", `${(Math.min(Math.max(valor, 0), 5) / 5) * 100}%`);
}

function crearEscala(valor) {
	const escala = el("div", { class: "escala", "aria-hidden": "true" }, el("span", { class: "escala-relleno" }),
		el("span", { class: "escala-punto" }));
	pintarEscala(escala, valor);
	return escala;
}

async function cargarGrupos(reiniciar = true) {
	const lista = $("lista-grupos");
	if (reiniciar) {
		estado.paginaGrupos = 0;
		estado.grupos = [];
		lista.replaceChildren(el("li", { class: "estado-texto" }, "Buscando grupos…"));
	}
	try {
		const { datos } = await api(`/grupos?${consulta({ texto: $("buscar-grupo").value.trim(), pagina: estado.paginaGrupos, tamano: TAMANO })}`);
		estado.grupos = reiniciar ? datos.contenido : estado.grupos.concat(datos.contenido);
		estado.totalPaginasGrupos = datos.totalPaginas;
		pintarGrupos();
	} catch (error) {
		lista.replaceChildren(el("li", { class: "estado-texto" }, mensajeError(error)));
	}
}

function pintarGrupos() {
	const lista = $("lista-grupos");
	if (estado.grupos.length === 0) {
		lista.replaceChildren(el("li", { class: "estado-texto" }, "No hay grupos que coincidan con la búsqueda."));
	} else {
		lista.replaceChildren(...estado.grupos.map((grupo) => el("li", {},
			el("button", {
				type: "button",
				class: "grupo",
				"aria-pressed": String(estado.grupo?.id === grupo.id),
				onclick: () => abrirGrupo(grupo),
			},
			el("span", { class: "grupo-codigo" }, grupo.codigo, " ",
				el("span", { class: `etiqueta${grupo.estado === "VIGENTE" ? " vigente" : ""}` }, grupo.estado === "VIGENTE" ? "Vigente" : "Cerrado")),
			el("span", { class: "grupo-asignatura" }, grupo.nombre),
			el("span", { class: "grupo-docente" }, grupo.nombreDocente)))));
	}
	$("mas-grupos").hidden = estado.paginaGrupos + 1 >= estado.totalPaginasGrupos;
}

async function abrirGrupo(grupo) {
	estado.grupo = grupo;
	estado.evaluacion = null;
	estado.registradas = new Map();
	pintarGrupos();
	$("vista-planilla").classList.add("con-grupo");
	$("planilla-vacia").hidden = true;
	$("planilla-grupo").hidden = false;
	$("grupo-nombre").textContent = `${grupo.codigo}: ${grupo.nombre}`;
	$("grupo-detalle").textContent = `Docente ${grupo.nombreDocente}. ${grupo.estado === "VIGENTE" ? "Grupo vigente." : "Grupo cerrado: ya no recibe notas."}`;
	$("evaluaciones").replaceChildren(el("p", { class: "estado-texto" }, "Cargando notas del grupo…"));
	$("estudiantes").replaceChildren(el("li", { class: "cargando" }, "Cargando estudiantes…"));
	$("evaluacion-aviso").hidden = true;
	$("planilla").focus({ preventScroll: true });
	if (window.matchMedia("(max-width: 52rem)").matches) window.scrollTo({ top: 0 });

	try {
		const [notas] = await Promise.all([
			api(`/grupos/${grupo.id}/notas?${consulta({ tamano: TAMANO })}`),
			cargarEstudiantes(true),
		]);
		if (estado.grupo?.id !== grupo.id) return;
		estado.evaluaciones = notas.datos.contenido;
		const abierta = estado.evaluaciones.find((evaluacion) => estadoEvaluacion(evaluacion).abierta);
		await escogerEvaluacion(abierta ?? estado.evaluaciones[0] ?? null);
	} catch (error) {
		$("evaluaciones").replaceChildren(el("p", { class: "aviso" }, mensajeError(error)));
	}
}

async function cargarEstudiantes(reiniciar) {
	const grupo = estado.grupo;
	if (reiniciar) {
		estado.paginaEstudiantes = 0;
		estado.estudiantes = [];
	}
	const { datos } = await api(`/grupos/${grupo.id}/estudiantes?${consulta({ pagina: estado.paginaEstudiantes, tamano: TAMANO })}`);
	if (estado.grupo?.id !== grupo.id) return;
	estado.estudiantes = estado.estudiantes.concat(datos.contenido);
	estado.totalPaginasEstudiantes = datos.totalPaginas;
}

async function cargarRegistradas(evaluacion) {
	const registradas = new Map();
	for (let pagina = 0; pagina < 20; pagina += 1) {
		const { datos } = await api(`/notas/${evaluacion.id}/estudiantes?${consulta({ pagina, tamano: TAMANO })}`);
		datos.contenido.forEach((registro) => registradas.set(registro.estudiante, registro));
		if (pagina + 1 >= datos.totalPaginas) break;
	}
	return registradas;
}

function pintarEvaluaciones() {
	const contenedor = $("evaluaciones");
	if (estado.evaluaciones.length === 0) {
		contenedor.replaceChildren(el("p", { class: "estado-texto" }, "Este grupo todavía no tiene notas para registrar."));
		return;
	}
	contenedor.replaceChildren(...estado.evaluaciones.map((evaluacion) => {
		const { abierta, texto } = estadoEvaluacion(evaluacion);
		const seleccionada = estado.evaluacion?.id === evaluacion.id;
		return el("button", {
			type: "button",
			role: "radio",
			class: "evaluacion",
			"aria-checked": String(seleccionada),
			tabindex: seleccionada ? "0" : "-1",
			onclick: () => escogerEvaluacion(evaluacion),
			onkeydown: moverEntreEvaluaciones,
		},
		el("span", { class: "evaluacion-nombre" }, evaluacion.nombre),
		el("span", { class: `evaluacion-estado${abierta ? " abierta" : ""}` }, texto));
	}));
}

function moverEntreEvaluaciones(evento) {
	const pasos = { ArrowRight: 1, ArrowDown: 1, ArrowLeft: -1, ArrowUp: -1 };
	if (!(evento.key in pasos)) return;
	evento.preventDefault();
	const actual = estado.evaluaciones.findIndex((evaluacion) => evaluacion.id === estado.evaluacion?.id);
	const siguiente = (actual + pasos[evento.key] + estado.evaluaciones.length) % estado.evaluaciones.length;
	escogerEvaluacion(estado.evaluaciones[siguiente]).then(() => {
		$("evaluaciones").querySelector('[aria-checked="true"]')?.focus();
	});
}

async function escogerEvaluacion(evaluacion) {
	estado.evaluacion = evaluacion;
	pintarEvaluaciones();
	const aviso = $("evaluacion-aviso");
	if (!evaluacion) {
		aviso.hidden = true;
		pintarEstudiantes();
		return;
	}
	const { abierta, texto } = estadoEvaluacion(evaluacion);
	const grupoCerrado = estado.grupo.estado !== "VIGENTE";
	aviso.hidden = abierta && !grupoCerrado;
	const yaCerro = new Date() >= new Date(evaluacion.fechaFinRegistro);
	aviso.textContent = grupoCerrado
		? "El grupo está cerrado, así que no se pueden registrar notas nuevas. Puede consultar las que ya están."
		: yaCerro
			? `${texto}. Puede consultar las notas registradas, pero ya no registrar nuevas.`
			: `${texto}. Por ahora solo puede consultar las notas.`;
	try {
		const registradas = await cargarRegistradas(evaluacion);
		if (estado.evaluacion?.id !== evaluacion.id) return;
		estado.registradas = registradas;
	} catch (error) {
		avisar(mensajeError(error), true);
	}
	pintarEstudiantes();
}

function sePuedeRegistrar() {
	return Boolean(estado.evaluacion) && estadoEvaluacion(estado.evaluacion).abierta && estado.grupo?.estado === "VIGENTE";
}

function pintarEstudiantes() {
	const lista = $("estudiantes");
	if (estado.estudiantes.length === 0) {
		lista.replaceChildren(el("li", { class: "cargando" }, "Este grupo todavía no tiene estudiantes inscritos."));
	} else {
		lista.replaceChildren(...estado.estudiantes.map(crearRenglon));
	}
	$("mas-estudiantes").hidden = estado.paginaEstudiantes + 1 >= estado.totalPaginasEstudiantes;
}

function crearRenglon(estudiante) {
	const registro = estado.registradas.get(estudiante.id);
	const activo = estudiante.estado === "ACTIVO";
	const valor = registro ? Number(registro.valor) : undefined;
	const escala = crearEscala(valor);
	const renglon = el("li", { class: `renglon${activo ? "" : " inactivo"}`, "data-estudiante": estudiante.id },
		el("div", { class: "estudiante-nombre" }, estudiante.nombreCompleto,
			el("span", { class: "estudiante-id" }, activo ? `Identificación ${estudiante.identificacion}` : `Identificación ${estudiante.identificacion}. Estudiante inactivo`)),
		escala,
		crearCeldaNota(estudiante, registro, escala, activo));
	if (registro && valor < APRUEBA) renglon.querySelector(".nota-valor")?.classList.add("reprueba");
	return renglon;
}

function crearCeldaNota(estudiante, registro, escala, activo) {
	if (registro) {
		return el("div", { class: "nota" },
			el("span", { class: `nota-valor${Number(registro.valor) < APRUEBA ? " reprueba" : ""}` }, formatoNota.format(Number(registro.valor))),
			el("span", { class: "nota-fecha" }, `Registrada el ${formatoFecha.format(new Date(registro.fechaRegistro))}`));
	}
	if (!activo || !sePuedeRegistrar()) {
		return el("div", { class: "nota" }, el("span", { class: "estado-texto" }, "Sin nota"));
	}
	const idCampo = `nota-${estudiante.id}`;
	const error = el("p", { class: "nota-error", id: `${idCampo}-error`, hidden: true });
	const campo = el("input", {
		id: idCampo,
		inputmode: "decimal",
		autocomplete: "off",
		placeholder: "0,0",
		"aria-label": `Nota de ${estudiante.nombreCompleto}`,
		"aria-describedby": `${idCampo}-error`,
	});
	const boton = el("button", { type: "button", class: "boton" }, "Registrar");
	const intentar = () => confirmarRegistro(estudiante, campo, error, boton);
	campo.addEventListener("input", () => {
		const { valor } = leerNota(campo.value);
		pintarEscala(escala, valor, true);
		campo.removeAttribute("aria-invalid");
		error.hidden = true;
	});
	campo.addEventListener("keydown", (evento) => {
		if (evento.key !== "Enter") return;
		evento.preventDefault();
		intentar();
	});
	boton.addEventListener("click", intentar);
	return el("div", { class: "nota" }, campo, boton, error);
}

function mostrarErrorCampo(campo, error, mensaje) {
	error.textContent = mensaje;
	error.hidden = false;
	campo.setAttribute("aria-invalid", "true");
	campo.focus();
}

function confirmarRegistro(estudiante, campo, error, boton) {
	const lectura = leerNota(campo.value);
	if (lectura.error) {
		mostrarErrorCampo(campo, error, lectura.error);
		return;
	}
	const dialogo = $("confirmacion");
	const texto = $("confirmacion-texto");
	texto.replaceChildren("Va a registrar ",
		el("span", { class: `grande${lectura.valor < APRUEBA ? " reprueba" : ""}` }, formatoNota.format(lectura.valor)),
		` a ${estudiante.nombreCompleto} en ${estado.evaluacion.nombre}.`);
	dialogo.returnValue = "";
	dialogo.onclose = async () => {
		if (dialogo.returnValue !== "confirmar") {
			campo.focus();
			return;
		}
		await registrar(estudiante, lectura.valor, campo, error, boton);
	};
	dialogo.showModal();
}

async function registrar(estudiante, valor, campo, error, boton) {
	boton.disabled = true;
	campo.disabled = true;
	boton.textContent = "Registrando…";
	try {
		await api("/notas-estudiantes", {
			method: "POST",
			body: JSON.stringify({
				docente: estado.grupo.docente,
				estudiante: estudiante.id,
				nota: estado.evaluacion.id,
				valor,
			}),
		});
		avisar(`Nota registrada para ${estudiante.nombreCompleto}.`);
		estado.registradas = await cargarRegistradas(estado.evaluacion);
		reemplazarRenglon(estudiante);
	} catch (problema) {
		boton.disabled = false;
		campo.disabled = false;
		boton.textContent = "Registrar";
		if (problema.estadoHttp === 409) {
			estado.registradas = await cargarRegistradas(estado.evaluacion).catch(() => estado.registradas);
			reemplazarRenglon(estudiante);
			avisar(problema.message, true);
			return;
		}
		mostrarErrorCampo(campo, error, mensajeError(problema));
	}
}

function reemplazarRenglon(estudiante) {
	const actual = $("estudiantes").querySelector(`[data-estudiante="${estudiante.id}"]`);
	const nuevo = crearRenglon(estudiante);
	actual?.replaceWith(nuevo);
	const siguiente = nuevo.nextElementSibling?.querySelector("input:not(:disabled)");
	siguiente?.focus();
}

async function buscarEstudiantes(reiniciar = true) {
	const lista = $("resultados-estudiantes");
	if (reiniciar) {
		estado.paginaResultados = 0;
		estado.resultados = [];
		lista.replaceChildren(el("li", { class: "mensaje" }, "Buscando…"));
	}
	try {
		const { datos } = await api(`/estudiantes?${consulta({ texto: $("buscar-estudiante").value.trim(), pagina: estado.paginaResultados, tamano: TAMANO })}`);
		estado.resultados = estado.resultados.concat(datos.contenido);
		estado.totalPaginasResultados = datos.totalPaginas;
		if (estado.resultados.length === 0) {
			lista.replaceChildren(el("li", { class: "mensaje" }, "No encontramos estudiantes con ese nombre o identificación."));
		} else {
			lista.replaceChildren(...estado.resultados.map((estudiante) => el("li", { class: "resultado" },
				el("div", { class: "estudiante-nombre" }, estudiante.nombreCompleto,
					el("span", { class: "estudiante-id" }, `Identificación ${estudiante.identificacion}`)),
				el("span", { class: `etiqueta${estudiante.estado === "ACTIVO" ? " vigente" : ""}` }, estudiante.estado === "ACTIVO" ? "Activo" : "Inactivo"))));
		}
		$("mas-resultados").hidden = estado.paginaResultados + 1 >= estado.totalPaginasResultados;
	} catch (error) {
		lista.replaceChildren(el("li", { class: "mensaje" }, mensajeError(error)));
	}
}

function cambiarVista(vista) {
	document.querySelectorAll(".vista").forEach((boton) => {
		if (boton.dataset.vista === vista) boton.setAttribute("aria-current", "page");
		else boton.removeAttribute("aria-current");
	});
	$("vista-planilla").hidden = vista !== "planilla";
	$("vista-estudiantes").hidden = vista !== "estudiantes";
	if (vista === "estudiantes") {
		$("buscar-estudiante").focus();
		if (estado.resultados.length === 0) buscarEstudiantes(true);
	}
}

function iniciar() {
	$("buscar-grupo").addEventListener("input", retrasar(() => cargarGrupos(true)));
	$("mas-grupos").addEventListener("click", () => {
		estado.paginaGrupos += 1;
		cargarGrupos(false);
	});
	$("mas-estudiantes").addEventListener("click", async () => {
		estado.paginaEstudiantes += 1;
		await cargarEstudiantes(false).catch((error) => avisar(mensajeError(error), true));
		pintarEstudiantes();
	});
	$("volver-grupos").addEventListener("click", () => {
		$("vista-planilla").classList.remove("con-grupo");
		document.querySelector('.grupo[aria-pressed="true"]')?.focus();
	});
	$("buscar-estudiante").addEventListener("input", retrasar(() => buscarEstudiantes(true)));
	$("mas-resultados").addEventListener("click", () => {
		estado.paginaResultados += 1;
		buscarEstudiantes(false);
	});
	document.querySelectorAll(".vista").forEach((boton) => boton.addEventListener("click", () => cambiarVista(boton.dataset.vista)));
	cargarGrupos(true);
}

iniciar();
