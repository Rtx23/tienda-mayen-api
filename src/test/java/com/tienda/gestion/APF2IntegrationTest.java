// Pruebas de integración para comprobar la API, la seguridad y la base de datos.
@SpringBootTest
@AutoConfigureMockMvc
class APF2IntegrationTest {

    @Autowired MockMvc mvc; // Permite probar los endpoints HTTP.
    @Autowired JdbcTemplate jdbc; // Permite consultar y limpiar la base de datos.
    @Autowired JwtEncoder encoder; // Genera tokens para las pruebas.
    @Autowired JwtDecoder decoder; // Comprueba el contenido de los tokens.
    String admin; // Guarda el token del administrador.

    // Limpia los datos antes de cada prueba e inicia sesión como administrador.
    @BeforeEach
    void preparar() throws Exception {
        // Elimina primero los detalles y las ventas para respetar las relaciones.
        jdbc.update("delete from detalles_venta");
        jdbc.update("delete from ventas");
        jdbc.update("delete from productos");
        admin = login("admin");
    }

    // Inicia sesión y devuelve el token recibido.
    String login(String username) throws Exception {
        // Envía las credenciales y comprueba que el acceso sea correcto.
        String body = mvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"" + username + "\",\"password\":\"" + username + "123\"}"))
                .andExpect(status().isOk())
                .andExpect(header().string("Cache-Control", "no-store"))
                .andReturn().getResponse().getContentAsString();

        return JsonPath.read(body, "$.accessToken");
    }

    // Prepara los datos de un producto en formato JSON.
    String producto(String nombre, int stock, int minimo) {
        return "{\"nombre\":\"" + nombre + "\",\"precio\":3.50,\"stock\":" + stock
                + ",\"stockMinimo\":" + minimo + ",\"fechaVencimiento\":\"2027-12-15\"}";
    }

    // Crea un producto y devuelve el identificador generado.
    long crear(String nombre, int stock, int minimo) throws Exception {
        // Envía la petición con el token del administrador.
        String body = mvc.perform(post("/api/productos")
                .header("Authorization", "Bearer " + admin)
                .contentType(MediaType.APPLICATION_JSON)
                .content(producto(nombre, stock, minimo)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andReturn().getResponse().getContentAsString();

        return ((Number) JsonPath.read(body, "$.id")).longValue();
    }

    // Prepara los datos de una venta en formato JSON.
    String venta(long id, int cantidad) {
        return "{\"items\":[{\"productoId\":" + id + ",\"cantidad\":" + cantidad + "}]}";
    }

    // Consulta el stock actual directamente en la base de datos.
    int stock(long id) {
        return jdbc.queryForObject("select stock from productos where id=?", Integer.class, id);
    }

    // Comprueba el acceso público y que los endpoints protegidos exijan autenticación.
    @Test
    void pingPublicoYSinToken401() throws Exception {
        // ...
    }

    // Verifica que las credenciales incorrectas no permitan iniciar sesión.
    @Test
    void loginIncorrectoYUsuarioInexistente401() throws Exception {
        // ...
    }

    // Comprueba los datos del token y que las contraseñas estén cifradas.
    @Test
    void jwtContieneClaimsYUsuariosUsanBcrypt() {
        // ...
    }

    // Comprueba qué operaciones puede realizar cada rol.
    @ParameterizedTest
    @CsvSource({"lector,403,403", "editor,201,403", "admin,201,204"})
    void permisosPorRol(String usuario, int creacion, int borrado) throws Exception {
        // ...
    }

    // Verifica que crear, consultar, actualizar y eliminar productos funcione con la base de datos.
    @Test
    void crudEscribeFilasYActualizaSinInsertarOtroId() throws Exception {
        // ...
    }

    // Comprueba las consultas de productos con stock bajo y sin existencias.
    @Test
    void jpqlEncuentraSoloStockBajoYAgotados() throws Exception {
        // ...
    }

    // Verifica que una venta guarde sus detalles y descuente el stock.
    @Test
    void ventaGuardaDetalleTotalYDescuentaStock() throws Exception {
        // ...
    }

    // Comprueba que un error en la venta revierta los cambios anteriores.
    @Test
    void rollbackReviertePrimerDescuentoSiSegundoProductoNoTieneStock() throws Exception {
        // ...
    }

    // Comprueba que un producto inexistente no deje cambios parciales.
    @Test
    void rollbackConProductoInexistente() throws Exception {
        // ...
    }

    // Verifica que dos ventas simultáneas no dejen el stock negativo.
    @Test
    void ventasConcurrentesNoProducenStockNegativo() throws Exception {
        // ...
    }

    // Comprueba que los tokens inválidos, vencidos o de otro emisor sean rechazados.
    @Test
    void tokensManipuladosExpiradosYOtroEmisor401() throws Exception {
        // ...
    }

    // Genera un token para probar distintos emisores y fechas de expiración.
    String token(String issuer, Instant inicio, Instant fin) {
        // ...
    }

    // Comprueba que CORS permita el origen autorizado y rechace uno distinto.
    @Test
    void corsPermiteAngularYRechazaOtroOrigen() throws Exception {
        // ...
    }

    // Verifica que los datos inválidos sean rechazados sin guardar registros.
    @Test
    void validacionJsonYCantidadNoDejanDatos() throws Exception {
        // ...
    }

    // Comprueba que una venta rechace productos duplicados y cantidades inválidas.
    @Test
    void ventaNoPermiteDuplicadosOCantidadNula() throws Exception {
        // ...
    }
}