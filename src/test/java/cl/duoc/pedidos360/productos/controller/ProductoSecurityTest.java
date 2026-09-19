package cl.duoc.pedidos360.productos.controller;

import cl.duoc.pedidos360.productos.model.Producto;
import cl.duoc.pedidos360.productos.repository.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas de la matriz 200 / 201 / 204 / 400 / 401 / 403 del microservicio de productos.
 * jwt() simula un token ya validado, así que aquí se prueba la AUTORIZACIÓN;
 * la validación de firma/issuer/client_id se cubre en CognitoAccessTokenValidatorTest
 * y en la prueba manual con un token real de Cognito.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
class ProductoSecurityTest {

	private static final GrantedAuthority READ = new SimpleGrantedAuthority("SCOPE_rs-api-productos/productos-read");
	private static final GrantedAuthority WRITE = new SimpleGrantedAuthority("SCOPE_rs-api-productos/productos-write");
	private static final GrantedAuthority ADMIN = new SimpleGrantedAuthority("ROLE_ADMIN");
	private static final String BODY_VALIDO = "{\"nombre\":\"Notebook\",\"precio\":499990,\"stock\":5}";

	@Autowired
	private MockMvc mvc;

	@Autowired
	private ProductoRepository repository;

	@Test
	void sinToken_devuelve401ConCuerpoJson() throws Exception {
		mvc.perform(get("/api/productos"))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.status").value(401));
	}

	@Test
	void conScopeRead_listaProductos200() throws Exception {
		mvc.perform(get("/api/productos").with(jwt().authorities(READ)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$[0].nombre").exists())
				.andExpect(jsonPath("$[0].precio").exists());
	}

	@Test
	void tokenValidoSinScope_devuelve403() throws Exception {
		mvc.perform(get("/api/productos").with(jwt()))
				.andExpect(status().isForbidden())
				.andExpect(jsonPath("$.status").value(403));
	}

	@Test
	void postConSoloScopeRead_devuelve403() throws Exception {
		mvc.perform(post("/api/productos").with(jwt().authorities(READ))
						.contentType(MediaType.APPLICATION_JSON).content(BODY_VALIDO))
				.andExpect(status().isForbidden());
	}

	@Test
	void postConScopeWrite_crea201() throws Exception {
		mvc.perform(post("/api/productos").with(jwt().authorities(WRITE))
						.contentType(MediaType.APPLICATION_JSON).content(BODY_VALIDO))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.nombre").value("Notebook"))
				.andExpect(jsonPath("$.precio").value(499990))
				.andExpect(jsonPath("$.stock").value(5));
	}

	@Test
	void postConPrecioInvalido_devuelve400() throws Exception {
		mvc.perform(post("/api/productos").with(jwt().authorities(WRITE))
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"nombre\":\"Notebook\",\"precio\":0,\"stock\":5}"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void deleteSinRolAdmin_devuelve403() throws Exception {
		Producto producto = repository.save(new Producto("Cable HDMI", 4990, 100));

		mvc.perform(delete("/api/productos/" + producto.getId()).with(jwt().authorities(READ, WRITE)))
				.andExpect(status().isForbidden());
	}

	@Test
	void deleteConRolAdmin_devuelve204() throws Exception {
		Producto producto = repository.save(new Producto("Cable USB", 2990, 100));

		mvc.perform(delete("/api/productos/" + producto.getId()).with(jwt().authorities(ADMIN)))
				.andExpect(status().isNoContent());
	}

	@Test
	void healthEsPublico() throws Exception {
		mvc.perform(get("/actuator/health")).andExpect(status().isOk());
	}
}
