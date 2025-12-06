# Documentación Completa del Proyecto PaginaPizza



## 📋 Tabla de Contenidos
1. [OPEN API] (#Link del OpenApi)
2. [Descripción General](#descripción-general)
3. [Arquitectura del Proyecto](#arquitectura-del-proyecto)
4. [Entidades (Models)](#entidades-models)
5. [Repositorios (Repositories)](#repositorios-repositories)
6. [Servicios (Services)](#servicios-services)
7. [Controladores (Controllers)](#controladores-controllers)
8. [Configuración y Recursos](#configuración-y-recursos)

---
## Link
   (http://localhost:8080/swagger-ui/index.html?url=/api-docs)

   (http://localhost:8080/swagger-ui.html)
## 📱 Descripción General

**PaginaPizza** es una aplicación web de e-commerce especializada en la venta de pizzas. Permite a los usuarios:
- Navegar por el catálogo de pizzas disponibles
- Registrarse e iniciar sesión
- Agregar pizzas al carrito con diferentes tamaños
- Gestionar su carrito (actualizar cantidades, eliminar items, vaciar)
- Procesar pedidos con métodos de pago
- Consultar información sobre misión y visión del negocio

**Stack Tecnológico:**
- Backend: Spring Boot 3.5.6
- Java: 17 LTS
- Base de Datos: JPA/Hibernate
- Frontend: Thymeleaf (Templates HTML)
- Seguridad: BCrypt para encriptación de contraseñas

---

## 🏗️ Arquitectura del Proyecto

```
PaginaPizza/
├── src/
│   ├── main/
│   │   ├── java/com/prueba3/pagina3/
│   │   │   ├── Pagina3Application.java          (Punto de entrada)
│   │   │   ├── Controller/                      (Capa de presentación)
│   │   │   ├── Service/                         (Lógica de negocio)
│   │   │   ├── Model/                           (Entidades)
│   │   │   └── Repository/                      (Acceso a datos)
│   │   └── resources/
│   │       ├── application.properties           (Configuración)
│   │       ├── templates/                       (Vistas Thymeleaf)
│   │       └── static/                          (CSS e imágenes)
│   └── test/
└── pom.xml                                       (Configuración Maven)
```

### Capas de la Aplicación:

1. **Capa de Presentación (Controller)**: Maneja las solicitudes HTTP
2. **Capa de Lógica de Negocio (Service)**: Implementa la lógica del carrito y usuarios
3. **Capa de Datos (Repository)**: Acceso a la base de datos
4. **Capa de Modelos (Model)**: Entidades JPA

---

## 💾 Entidades (Models)

### 1. **Opcion.java** - Modelo de Pizza

```java
@Entity
public class Opcion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                    // ID único de la pizza
    private String nombre;              // Nombre de la pizza
    private String descripcion;         // Descripción de la pizza
    private double precioPequena;       // Precio tamaño pequeño
    private double precioMediana;       // Precio tamaño mediano
    private double precioGrande;        // Precio tamaño grande
    private String imagen;              // Ruta de la imagen
}
```

#### Atributos:
- `id`: Identificador único generado automáticamente por la BD
- `nombre`: Nombre de la pizza (ej: "Margarita", "Pepperoni")
- `descripcion`: Detalles sobre la composición de la pizza
- `precioPequena`: Precio de la pizza en tamaño pequeño
- `precioMediana`: Precio de la pizza en tamaño mediano
- `precioGrande`: Precio de la pizza en tamaño grande
- `imagen`: Ruta o nombre del archivo de imagen

#### Métodos Importantes:

**`getPrecioPorTamano(String tamano)`**
```java
// Objetivo: Obtener el precio según el tamaño seleccionado
// Parámetros: tamano - "pequeña", "mediana" o "grande"
// Retorna: double - el precio correspondiente al tamaño
// Lógica: Usa switch para mapear tamaño a precio
```

---

### 2. **ItemCarrito.java** - Modelo de Item en Carrito

```java
@Entity
@Table(name = "item_carrito")
@Data                           // Lombok: genera getters/setters
@NoArgsConstructor              // Constructor sin parámetros
@AllArgsConstructor             // Constructor con todos los parámetros
public class ItemCarrito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                    // ID único del item
    
    @ManyToOne
    @JoinColumn(name = "opcion_id", nullable = false)
    private Opcion opcion;              // Referencia a la pizza
    
    private String nombre;              // Nombre de la pizza
    private String tamano;              // Tamaño seleccionado
    private int cantidad;               // Cantidad de pizzas
    private double precioUnitario;      // Precio por unidad
    private double subtotal;            // Precio total (cantidad × precio)
    private String usuarioEmail;        // Email del propietario del carrito
}
```

#### Atributos:
- `id`: Identificador único del item en el carrito
- `opcion`: Relación ManyToOne con la tabla Opcion
- `nombre`: Nombre de la pizza (cacheado para rendimiento)
- `tamano`: Tamaño elegido (pequeña, mediana, grande)
- `cantidad`: Número de pizzas de este tipo
- `precioUnitario`: Precio por pizza de este tamaño
- `subtotal`: Total para este item (cantidad × precio)
- `usuarioEmail`: Asociación con el usuario propietario

#### Métodos Importantes:

**`setOpcion(Opcion opcion)`**
```java
// Objetivo: Establecer la pizza y actualizar datos relacionados
// Parámetros: opcion - la pizza a agregar
// Efectos: Actualiza nombre y recalcula subtotal
// Bloque: Verifica que opcion no sea null antes de acceder
```

**`setTamano(String tamano)`**
```java
// Objetivo: Cambiar el tamaño y recalcular precio unitario
// Parámetros: tamano - nuevo tamaño
// Efectos: Obtiene precio de la pizza para el nuevo tamaño
// Lógica: Si opcion existe, usa getPrecioPorTamano()
```

**`setCantidad(int cantidad)`**
```java
// Objetivo: Actualizar la cantidad de pizzas
// Parámetros: cantidad - nueva cantidad
// Efectos: Recalcula el subtotal automáticamente
```

**`getSubtotal()`**
```java
// Objetivo: Obtener el subtotal actualizado del item
// Retorna: double - precioUnitario × cantidad
// Importancia: Se recalcula cada acceso para garantizar consistencia
```

**`actualizarSubtotal()` (privado)**
```java
// Objetivo: Actualizar el campo subtotal internamente
// Bloque privado: Solo usado internamente por los setters
// Cálculo: this.subtotal = this.precioUnitario * this.cantidad
```

---

### 3. **Usermodel.java** - Modelo de Usuario

```java
@Entity
public class Usermodel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                    // ID único del usuario
    
    private String nombre;              // Nombre del usuario
    private String apellidos;           // Apellidos del usuario
    private String telefono;            // Teléfono de contacto
    private String direccion;           // Dirección de envío
    
    @Column(nullable = false, unique = true)
    private String email;               // Email único del usuario
    
    @Column(nullable = false)
    private String password;            // Contraseña encriptada
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;    // Fecha de creación
}
```

#### Atributos:
- `id`: Identificador único de la BD
- `nombre`: Primer nombre del usuario
- `apellidos`: Apellidos del usuario
- `telefono`: Número de teléfono
- `direccion`: Dirección para envíos
- `email`: Email único (restricción UNIQUE)
- `password`: Contraseña encriptada con BCrypt
- `createdAt`: Fecha y hora de registro

#### Métodos Especiales:

**`@PrePersist - prePersist()`**
```java
// Objetivo: Hook ejecutado antes de guardar en BD
// Bloque: Se ejecuta automáticamente cuando se guarda
// Efectos: Establece createdAt a la fecha/hora actual
// Importancia: Garantiza que todo usuario registrado tenga timestamp
```

---

## 🔄 Repositorios (Repositories)

Los repositorios son interfaces que extienden `JpaRepository` y permiten acceso a la base de datos.

### 1. **ItemCarritoRepository.java**

```java
public interface ItemCarritoRepository extends JpaRepository<ItemCarrito, Long> {
    
    @Query("SELECT i FROM ItemCarrito i JOIN FETCH i.opcion WHERE i.usuarioEmail = :usuarioEmail")
    List<ItemCarrito> findByUsuarioEmail(@Param("usuarioEmail") String usuarioEmail);
    
    Optional<ItemCarrito> findByUsuarioEmailAndOpcionIdAndTamano(
        String usuarioEmail, Long opcionId, String tamano);
    
    void deleteByUsuarioEmail(String usuarioEmail);
}
```

#### Métodos:

**`findByUsuarioEmail(String usuarioEmail)` - Consulta Personalizada**
```java
// Objetivo: Obtener todos los items del carrito de un usuario
// Parámetros: usuarioEmail - email del usuario
// Retorna: List<ItemCarrito> - todos los items del usuario
// Bloque: Usa JOIN FETCH para cargar optimamente la relación con Opcion
// Ventaja: Evita el problema N+1 de Hibernate
```

**`findByUsuarioEmailAndOpcionIdAndTamano(String, Long, String)`**
```java
// Objetivo: Buscar un item específico por usuario, pizza y tamaño
// Parámetros: usuarioEmail, opcionId, tamano
// Retorna: Optional<ItemCarrito> - item si existe
// Uso: Detectar duplicados al agregar items
```

**`deleteByUsuarioEmail(String usuarioEmail)` - Eliminación Masiva**
```java
// Objetivo: Eliminar todos los items de un usuario
// Parámetros: usuarioEmail - email del usuario
// Efecto: Vacía el carrito del usuario
// Uso: Al finalizar compra o vaciar carrito
```

---

### 2. **OpcionRepository.java**

```java
public interface OpcionRepository extends JpaRepository<Opcion, Long> {
}
```

#### Métodos Heredados:
- `findAll()`: Obtiene todas las pizzas
- `findById(Long)`: Obtiene una pizza por ID
- `save(Opcion)`: Guarda o actualiza una pizza
- `delete(Opcion)`: Elimina una pizza

---

### 3. **UsuarioRepository.java**

```java
public interface UsuarioRepository extends JpaRepository<Usermodel, Long> {
    
    Optional<Usermodel> findByEmail(String email);
    
    boolean existsByEmail(String email);
}
```

#### Métodos Personalizados:

**`findByEmail(String email)`**
```java
// Objetivo: Buscar usuario por su email
// Parámetros: email - email del usuario
// Retorna: Optional<Usermodel> - usuario si existe
// Uso: En login para validar credenciales
```

**`existsByEmail(String email)`**
```java
// Objetivo: Verificar si un email ya está registrado
// Parámetros: email - email a verificar
// Retorna: boolean - true si existe, false si no
// Uso: Validación en registro para evitar duplicados
```

---

## 🎯 Servicios (Services)

Los servicios contienen la lógica de negocio de la aplicación.

### 1. **CarritoService.java** - Gestión del Carrito

```java
@Service
public class CarritoService {
    private final ItemCarritoRepository itemCarritoRepository;
    private final List<ItemCarrito> itemsCarrito = new ArrayList<>();
}
```

#### Método: `agregarItem(String usuarioEmail, Opcion opcion, String tamano, int cantidad)`

```java
/**
 * Objetivo: Agregar una pizza al carrito o aumentar cantidad si ya existe
 * 
 * Parámetros:
 *   - usuarioEmail: Email del usuario (null para invitados)
 *   - opcion: Pizza a agregar
 *   - tamano: Tamaño seleccionado (pequeña, mediana, grande)
 *   - cantidad: Cantidad de pizzas a agregar
 * 
 * Lógica:
 *   1. Verifica si ya existe un item con la misma pizza y tamaño
 *   2. Si existe: aumenta la cantidad del item existente
 *   3. Si no existe: crea un nuevo item en el carrito
 *   4. Asegura que el precioUnitario esté siempre actualizado
 *   5. Guarda en BD si es usuario registrado, en lista si es invitado
 * 
 * Bloques:
 *   - Bloque if (itemExistente != null): Manejo de duplicados
 *   - Bloque else: Creación de nuevo item
 *   - Validación if (opcion != null && cantidad > 0): Seguridad
 */
```

#### Método: `obtenerItems(String usuarioEmail)`

```java
/**
 * Objetivo: Obtener todos los items del carrito del usuario
 * 
 * Parámetros:
 *   - usuarioEmail: Email del usuario (null para invitados)
 * 
 * Retorna: List<ItemCarrito> - lista de items en el carrito
 * 
 * Lógica:
 *   1. Si es usuario registrado: obtiene de BD
 *   2. Si es invitado: obtiene de lista en memoria
 *   3. Valida que todos los items tengan precio unitario
 *   4. Si falta precio, lo recalcula desde la pizza
 *   5. Persiste cambios si es necesario
 * 
 * Bloques:
 *   - Bloque if (usuarioEmail != null): Distinguir usuario registrado
 *   - Bloque for: Validación de precios unitarios
 *   - Bloque if (item.getPrecioUnitario() == 0): Recálculo de precios
 */
```

#### Método: `eliminarItem(Long itemId, String usuarioEmail)`

```java
/**
 * Objetivo: Eliminar un item específico del carrito
 * 
 * Parámetros:
 *   - itemId: ID del item a eliminar
 *   - usuarioEmail: Email del usuario
 * 
 * Retorna: boolean - true si se eliminó, false si falló
 * 
 * Lógica:
 *   1. Si es usuario registrado:
 *      - Verifica que el item exista en BD
 *      - Lo elimina de la BD
 *   2. Si es invitado:
 *      - Busca en lista en memoria
 *      - Lo elimina con removeIf
 * 
 * Bloques:
 *   - Bloque if (usuarioEmail != null): Eliminación en BD
 *   - Bloque else: Eliminación en lista en memoria
 */
```

#### Método: `vaciarCarrito(String usuarioEmail)` - Transaccional

```java
/**
 * Objetivo: Eliminar todos los items del carrito
 * 
 * Parámetros:
 *   - usuarioEmail: Email del usuario
 * 
 * Efectos:
 *   1. Si es usuario: elimina todos sus items de BD
 *   2. Si es invitado: limpia la lista en memoria
 * 
 * @Transactional: Asegura atomicidad de la operación
 * 
 * Bloques:
 *   - Bloque if (usuarioEmail != null): BD
 *   - Bloque else: Lista en memoria
 */
```

#### Método: `calcularTotal(String usuarioEmail)`

```java
/**
 * Objetivo: Calcular el total del carrito
 * 
 * Parámetros:
 *   - usuarioEmail: Email del usuario
 * 
 * Retorna: double - suma de subtotales de todos los items
 * 
 * Lógica:
 *   1. Obtiene todos los items del usuario
 *   2. Suma los subtotales usando stream().mapToDouble()
 * 
 * Ejemplo: 
 *   - Item 1: 2 pizzas × $8 = $16
 *   - Item 2: 1 pizza × $10 = $10
 *   - Total: $26
 */
```

#### Método: `obtenerCantidadItems(String usuarioEmail)`

```java
/**
 * Objetivo: Obtener cantidad total de pizzas en el carrito
 * 
 * Parámetros:
 *   - usuarioEmail: Email del usuario
 * 
 * Retorna: int - suma de cantidades de todos los items
 * 
 * Lógica:
 *   Suma el campo 'cantidad' de cada item usando stream
 * 
 * Ejemplo:
 *   - Item 1: cantidad = 2
 *   - Item 2: cantidad = 1
 *   - Total = 3
 */
```

#### Método: `estaVacio(String usuarioEmail)`

```java
/**
 * Objetivo: Verificar si el carrito está vacío
 * 
 * Parámetros:
 *   - usuarioEmail: Email del usuario
 * 
 * Retorna: boolean - true si no hay items, false si hay
 */
```

#### Método: `obtenerItemPorId(Long itemId, String usuarioEmail)`

```java
/**
 * Objetivo: Buscar un item específico por ID
 * 
 * Parámetros:
 *   - itemId: ID del item
 *   - usuarioEmail: Email del usuario
 * 
 * Retorna: ItemCarrito - el item si existe, null si no
 * 
 * Lógica:
 *   1. Si es usuario: busca en BD
 *   2. Si es invitado: busca en lista en memoria
 */
```

#### Método: `actualizarCantidad(Long itemId, int nuevaCantidad, String usuarioEmail)` - Transaccional

```java
/**
 * Objetivo: Cambiar la cantidad de un item
 * 
 * Parámetros:
 *   - itemId: ID del item
 *   - nuevaCantidad: Nueva cantidad
 *   - usuarioEmail: Email del usuario
 * 
 * Retorna: boolean - true si se actualizó, false si falló
 * 
 * Lógica:
 *   1. Obtiene el item por ID
 *   2. Si cantidad > 0: actualiza la cantidad
 *   3. Si cantidad <= 0: elimina el item
 *   4. Guarda cambios en BD si es usuario registrado
 * 
 * Bloques:
 *   - Bloque if (item != null && nuevaCantidad > 0): Actualización
 *   - Bloque else if (item != null && nuevaCantidad <= 0): Eliminación
 */
```

#### Método: `migrarCarritoInvitado(String usuarioEmail)` - Transaccional

```java
/**
 * Objetivo: Transferir carrito de invitado a usuario registrado
 * 
 * Parámetros:
 *   - usuarioEmail: Email del nuevo usuario registrado
 * 
 * Lógica:
 *   1. Itera sobre items en memoria (carrito invitado)
 *   2. Asigna usuarioEmail a cada item
 *   3. Persiste cada item en la BD
 *   4. Limpia la lista en memoria
 * 
 * Bloque for:
 *   - Recorre copia de itemsCarrito (evita ConcurrentModificationException)
 *   - Establece usuarioEmail
 *   - Guarda en BD
 * 
 * Importancia: Mantiene el carrito cuando el invitado se registra
 */
```

#### Método privado: `buscarItemExistente(Long opcionId, String tamano)`

```java
/**
 * Objetivo: Buscar un item en la lista en memoria por pizza y tamaño
 * 
 * Parámetros:
 *   - opcionId: ID de la pizza
 *   - tamano: Tamaño de la pizza
 * 
 * Retorna: ItemCarrito - item si existe, null si no
 * 
 * Lógica:
 *   Usa stream().filter() para encontrar el primer item que coincida
 */
```

---

### 2. **OpcionService.java** - Gestión de Pizzas y Usuarios

```java
@Service
public class OpcionService {
    private final OpcionRepository opcionRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
}
```

#### Métodos de Pizzas:

**`listarOpciones()`**
```java
/**
 * Objetivo: Obtener todas las pizzas disponibles
 * Retorna: List<Opcion> - lista de todas las pizzas de BD
 * Uso: Para mostrar catálogo en la página principal
 */
```

**`obtenerPorId(Long id)`**
```java
/**
 * Objetivo: Obtener una pizza específica por ID
 * Parámetros: id - ID de la pizza
 * Retorna: Opcion - la pizza si existe, null si no
 * Uso: Al agregar al carrito
 */
```

#### Métodos de Usuarios:

**`emailExiste(String email)`**
```java
/**
 * Objetivo: Verificar si un email ya está registrado
 * Parámetros: email - email a verificar
 * Retorna: boolean - true si existe, false si no
 * Uso: Validación en formulario de registro
 */
```

**`registrarUsuario(String nombre, String apellidos, String telefono, String direccion, String email, String password)`**
```java
/**
 * Objetivo: Crear nuevo usuario en la BD
 * 
 * Parámetros:
 *   - nombre: Nombre del usuario
 *   - apellidos: Apellidos del usuario
 *   - telefono: Teléfono de contacto
 *   - direccion: Dirección de envío
 *   - email: Email único
 *   - password: Contraseña en texto plano
 * 
 * Lógica:
 *   1. Crea instancia nueva de Usermodel
 *   2. Asigna todos los datos
 *   3. Encripta la contraseña con BCrypt
 *   4. Guarda en BD
 * 
 * Bloque de encriptación:
 *   - passwordEncoder.encode(password)
 *   - Genera hash irreversible con salt
 */
```

**`validarLogin(String email, String password)`**
```java
/**
 * Objetivo: Autenticar usuario con email y contraseña
 * 
 * Parámetros:
 *   - email: Email del usuario
 *   - password: Contraseña en texto plano
 * 
 * Retorna: Map<String, String> - info del usuario si es válido, null si no
 *   - Clave "nombre": nombre del usuario
 *   - Clave "email": email del usuario
 * 
 * Lógica:
 *   1. Busca usuario por email
 *   2. Si existe:
 *      a. Valida contraseña encriptada con BCrypt
 *      b. Soporta contraseñas antiguas en texto plano (compatibilidad)
 *      c. Retorna mapa con datos del usuario
 *   3. Si no existe o contraseña incorrecta: retorna null
 * 
 * Bloques:
 *   - Bloque if (opt.isPresent()): Usuario encontrado
 *   - Condición OR: Compatibilidad con contraseñas antiguas
 *   - Bloque if (passwordEncoder.matches(...)): Validación segura
 */
```

**`obtenerTodosLosUsuarios()`**
```java
/**
 * Objetivo: Obtener lista de todos los usuarios registrados
 * 
 * Retorna: List<Map<String, String>> - lista de mapas con datos de usuarios
 * 
 * Lógica:
 *   1. Obtiene todos los usuarios de BD
 *   2. Para cada usuario:
 *      a. Crea LinkedHashMap (mantiene orden)
 *      b. Agrega datos del usuario
 *   3. Retorna lista de mapas
 * 
 * Estructura del Map:
 *   - "nombre": Nombre del usuario
 *   - "apellidos": Apellidos
 *   - "telefono": Teléfono
 *   - "direccion": Dirección
 *   - "email": Email
 */
```

---

## 🎮 Controladores (Controllers)

Los controladores manejan las solicitudes HTTP y retornan vistas o datos.

### 1. **InfoController.java** - Información de la Empresa

```java
@Controller
public class InfoController {
    
    @GetMapping("/misionvision")
    public String misionVision() {
        return "misionvision"; // busca misionvision.html en templates
    }
}
```

#### Métodos:

**`misionVision()` - GET /misionvision**
```java
/**
 * Objetivo: Mostrar página de misión y visión de la empresa
 * 
 * Anotaciones:
 *   @GetMapping: Maneja peticiones GET a /misionvision
 *   @Controller: Indica que es controlador MVC (no REST)
 * 
 * Retorna: String - nombre de la plantilla "misionvision"
 *   Spring buscará misionvision.html en carpeta templates/
 * 
 * Uso: Información corporativa de la empresa
 */
```

---

### 2. **OpcionController.java** - Gestión de Pizzas, Carrito y Usuarios

```java
@Controller
public class OpcionController {
    private final OpcionService opcionService;
    private final CarritoService carritoService;
}
```

#### Métodos del Catálogo y Carrito:

**`mostrarOpciones()` - GET /**
```java
/**
 * Objetivo: Mostrar página principal con catálogo de pizzas
 * 
 * Anotaciones:
 *   @GetMapping("/"): Mapea a la URL raíz
 *   
 * Parámetros:
 *   - Model model: Objeto para pasar datos a la vista
 *   - HttpSession session: Sesión del usuario
 * 
 * Lógica:
 *   1. Obtiene todas las pizzas del servicio
 *   2. Las agrega al modelo ("opciones")
 *   3. Obtiene el email de la sesión
 *   4. Calcula cantidad de items en carrito
 *   5. Agrega cantidad al modelo
 * 
 * Bloques:
 *   - Línea de obtención de opciones
 *   - Línea de obtención de cantidad
 * 
 * Retorna: String - "seleccion" (plantilla seleccion.html)
 */
```

**`agregarAlCarrito()` - POST /agregar**
```java
/**
 * Objetivo: Agregar pizza al carrito
 * 
 * Anotaciones:
 *   @PostMapping("/agregar"): Maneja POST a /agregar
 * 
 * Parámetros:
 *   @RequestParam Long opcionId: ID de la pizza
 *   @RequestParam String tamano: Tamaño elegido
 *   @RequestParam int cantidad: Cantidad a agregar
 *   RedirectAttributes redirectAttributes: Para mensajes post-redireccionamiento
 *   HttpSession session: Sesión para obtener email del usuario
 * 
 * Lógica:
 *   1. Obtiene la pizza por ID
 *   2. Valida que pizza exista y cantidad > 0
 *   3. Obtiene email de la sesión
 *   4. Agrega item al carrito mediante servicio
 *   5. Agrega mensaje de éxito o error
 *   6. Redirige a página principal
 * 
 * Bloques:
 *   - Bloque if (opcion != null && cantidad > 0): Validación
 *   - Mensaje de éxito en bloque then
 *   - Mensaje de error en bloque else
 * 
 * Retorna: String - "redirect:/" (redirección)
 */
```

**`mostrarCarrito()` - GET /carrito**
```java
/**
 * Objetivo: Mostrar página del carrito de compras
 * 
 * Parámetros:
 *   - Model model: Para pasar datos a la vista
 *   - HttpSession session: Para obtener email del usuario
 * 
 * Lógica:
 *   1. Obtiene email de la sesión
 *   2. Obtiene todos los items del carrito
 *   3. Calcula total del carrito
 *   4. Calcula cantidad total de items
 *   5. Verifica si carrito está vacío
 *   6. Agrega todo al modelo
 * 
 * Modelo:
 *   - itemsCarrito: List<ItemCarrito>
 *   - totalCarrito: double
 *   - cantidadItems: int
 *   - carritoVacio: boolean
 * 
 * Retorna: String - "carrito" (plantilla carrito.html)
 */
```

**`eliminarDelCarrito()` - POST /carrito/eliminar**
```java
/**
 * Objetivo: Eliminar un item del carrito
 * 
 * Parámetros:
 *   @RequestParam Long itemId: ID del item a eliminar
 *   RedirectAttributes redirectAttributes
 *   HttpSession session
 * 
 * Lógica:
 *   1. Obtiene email de sesión
 *   2. Intenta eliminar item mediante servicio
 *   3. Si éxito: agrega mensaje "Item eliminado"
 *   4. Si error: agrega mensaje de error
 *   5. Redirige a /carrito
 * 
 * Bloques:
 *   - if (carritoService.eliminarItem(...)): Éxito/Error
 * 
 * Retorna: String - "redirect:/carrito"
 */
```

**`vaciarCarrito()` - POST /carrito/vaciar**
```java
/**
 * Objetivo: Vaciar todo el carrito
 * 
 * Lógica:
 *   1. Obtiene email de sesión
 *   2. Vacía todos los items mediante servicio
 *   3. Agrega mensaje de confirmación
 *   4. Redirige a /carrito
 * 
 * Retorna: String - "redirect:/carrito"
 */
```

**`actualizarCantidadCarrito()` - POST /carrito/actualizar**
```java
/**
 * Objetivo: Cambiar cantidad de un item en el carrito
 * 
 * Parámetros:
 *   @RequestParam Long itemId: ID del item
 *   @RequestParam int cantidad: Nueva cantidad
 * 
 * Lógica:
 *   1. Obtiene email de sesión
 *   2. Actualiza cantidad mediante servicio
 *   3. Si éxito: mensaje "Cantidad actualizada"
 *   4. Si error: mensaje de error
 *   5. Redirige a /carrito
 * 
 * Bloques:
 *   - if (carritoService.actualizarCantidad(...)): Resultado
 * 
 * Retorna: String - "redirect:/carrito"
 */
```

#### Métodos de Registro e Inicio de Sesión:

**`mostrarFormularioRegistro()` - GET /registro**
```java
/**
 * Objetivo: Mostrar formulario de registro
 * 
 * Retorna: String - "registro" (plantilla registro.html)
 */
```

**`procesarRegistro()` - POST /registro**
```java
/**
 * Objetivo: Procesar registro de nuevo usuario
 * 
 * Parámetros:
 *   @RequestParam String nombre
 *   @RequestParam String apellidos
 *   @RequestParam String telefono
 *   @RequestParam String direccion
 *   @RequestParam String email
 *   @RequestParam String password
 *   HttpSession session
 *   Model model
 * 
 * Lógica:
 *   1. Verifica que email no esté registrado
 *      - Si existe: retorna a formulario con error
 *   2. Registra nuevo usuario en BD
 *   3. Guarda email y nombre en sesión
 *   4. Migra carrito de invitado a usuario
 *   5. Si hay pizzaSeleccionada: redirige a /carrito
 *   6. Si no: muestra mensaje de éxito
 * 
 * Bloques:
 *   - Bloque if (opcionService.emailExiste(email)): Validación email
 *   - Bloque if (pizzaId != null): Redirección condicional
 * 
 * Flujo alternativo:
 *   - Usuario invitado sin carrito → "redirect:/carrito"
 *   - Usuario sin pizza preseleccionada → Mostrar mensaje
 * 
 * Retorna: String - Depende del flujo
 */
```

**`mostrarLogin()` - GET /login**
```java
/**
 * Objetivo: Mostrar formulario de inicio de sesión
 * 
 * Retorna: String - "login" (plantilla login.html)
 */
```

**`procesarLogin()` - POST /login**
```java
/**
 * Objetivo: Autenticar usuario y crear sesión
 * 
 * Parámetros:
 *   @RequestParam String email
 *   @RequestParam String password
 *   HttpSession session
 *   Model model
 *   RedirectAttributes redirectAttributes
 * 
 * Lógica:
 *   1. Valida que campos no estén vacíos
 *      - Si vacío: retorna a login con error
 *   2. Valida credenciales mediante servicio
 *      - Si inválidas: retorna a login con error
 *   3. Login exitoso:
 *      a. Guarda email y nombre en sesión
 *      b. Migra carrito de invitado al usuario
 *      c. Busca redirección pendiente en sesión
 *      d. Si existe: redirige a ese URL
 *      e. Si hay pizza preseleccionada: redirige a /carrito
 *      f. Si no: redirige a inicio con mensaje
 * 
 * Bloques:
 *   - Bloque if (email.isEmpty() || password.isEmpty()): Validación
 *   - Bloque if (usuario == null): Autenticación fallida
 *   - Bloque if (redirectAfterLogin != null): Redirección pendiente
 *   - Bloque if (pizzaId != null): Pizza preseleccionada
 * 
 * Retorna: String - Depende del flujo
 */
```

#### Métodos de Pago y Confirmación:

**`confirmarPedido()` - POST /confirmar**
```java
/**
 * Objetivo: Validar antes de proceder al pago
 * 
 * Lógica:
 *   1. Verifica que usuario esté logueado
 *      - Si no: guarda URL en sesión y redirige a login
 *   2. Verifica que carrito no esté vacío
 *      - Si vacío: error y redirige a /carrito
 *   3. Si todo ok: redirige a /pago
 * 
 * Bloques:
 *   - Bloque if (usuarioEmail == null): Usuario requerido
 *   - Bloque if (carritoService.estaVacio(...)): Carrito requerido
 * 
 * Retorna: String - Redirección
 */
```

**`mostrarPago()` - GET /pago**
```java
/**
 * Objetivo: Mostrar formulario de pago
 * 
 * Lógica:
 *   1. Valida que usuario esté logueado
 *   2. Valida que carrito no esté vacío
 *   3. Si validaciones ok: prepara datos
 *      - Items del carrito
 *      - Total del carrito
 *      - Email del usuario
 * 
 * Modelo:
 *   - itemsCarrito: List<ItemCarrito>
 *   - totalCarrito: double
 *   - usuarioEmail: String
 * 
 * Retorna: String - "pago" (plantilla pago.html)
 */
```

**`procesarPago()` - POST /pago**
```java
/**
 * Objetivo: Procesar pago y generar recibo
 * 
 * Parámetros:
 *   @RequestParam String metodo: Método de pago
 *   @RequestParam(required=false) String numeroTarjeta: Número de tarjeta
 *   @RequestParam(required=false) String nombreTarjeta: Nombre en tarjeta
 *   @RequestParam(required=false) String fechaTarjeta: Fecha de vencimiento
 *   HttpSession session
 *   Model model
 *   RedirectAttributes redirectAttributes
 * 
 * Lógica:
 *   1. Valida que usuario esté logueado
 *   2. Valida que carrito no esté vacío
 *   3. Prepara datos para recibo:
 *      - Items del carrito
 *      - Total del pedido
 *      - Email del usuario
 *      - Método de pago
 *   4. Si método es Tarjeta: extrae últimos 4 dígitos
 *   5. Vacía el carrito (orden completada)
 *   6. Redirige a página de resultado
 * 
 * Bloques:
 *   - if (usuarioEmail == null): Validación usuario
 *   - if (carritoService.estaVacio(...)): Validación carrito
 *   - if (metodo != null && metodo.equalsIgnoreCase(...)):
 *     Extrae últimos dígitos de tarjeta
 * 
 * Seguridad:
 *   - Nunca muestra número completo de tarjeta
 *   - Solo muestra últimos 4 dígitos
 * 
 * Retorna: String - "resultado" (página de recibo)
 */
```

#### Métodos Administrativos:

**`verUsuarios()` - GET /usuarios**
```java
/**
 * Objetivo: Mostrar lista de todos los usuarios registrados
 * 
 * Lógica:
 *   1. Obtiene lista de todos los usuarios
 *   2. Calcula cantidad total
 *   3. Agrega al modelo
 * 
 * Modelo:
 *   - usuarios: List<Map<String, String>>
 *   - total: int
 * 
 * Retorna: String - "usuarios" (plantilla usuarios.html)
 */
```

**`cerrarSesion()` - GET /logout**
```java
/**
 * Objetivo: Cerrar sesión del usuario
 * 
 * Lógica:
 *   1. Invalida la sesión actual
 *   2. Agrega mensaje de despedida
 *   3. Redirige a inicio
 * 
 * Importancia:
 *   - session.invalidate() limpia todos los datos de sesión
 *   - Borra email, nombre y otros atributos
 * 
 * Retorna: String - "redirect:/" (redirección a inicio)
 */
```

---

## ⚙️ Configuración y Recursos

### 1. **Pagina3Application.java** - Punto de Entrada

```java
@SpringBootApplication
public class Pagina3Application {
    
    public static void main(String[] args) {
        SpringApplication.run(Pagina3Application.class, args);
        System.out.println("Aplicación iniciada correctamente.");
    }
}
```

#### Descripción:

**`@SpringBootApplication`**
```java
/**
 * Anotación compuesta que incluye:
 *   - @Configuration: Indica que es clase de configuración
 *   - @ComponentScan: Escanea componentes en el paquete
 *   - @EnableAutoConfiguration: Activa autoconfiguración
 * 
 * Efecto: Inicia la aplicación Spring Boot automáticamente
 */
```

**`main(String[] args)`**
```java
/**
 * Objetivo: Punto de entrada de la aplicación
 * 
 * Línea: SpringApplication.run(Pagina3Application.class, args)
 *   - Inicia el contexto de Spring
 *   - Carga todas las configuraciones
 *   - Inicia el servidor embedded Tomcat
 * 
 * Línea: System.out.println(...)
 *   - Confirma en consola que app inició correctamente
 */
```

---

### 2. **application.properties** - Configuración de la Aplicación

```properties
# Configuración típica (completar según tu setup)
spring.datasource.url=jdbc:mysql://localhost:3306/pizza_db
spring.datasource.username=root
spring.datasource.password=tu_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
server.port=8080
```

#### Configuraciones Comunes:

```
# Base de datos MySQL
spring.datasource.url: URL de conexión a BD
spring.datasource.username: Usuario de BD
spring.datasource.password: Contraseña de BD
spring.datasource.driver-class-name: Controlador JDBC

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto: create/create-drop/update/validate
spring.jpa.show-sql: Mostrar SQL en logs (true/false)
spring.jpa.properties.hibernate.dialect: Dialecto SQL (MySQL57Dialect)

# Servidor
server.port: Puerto de ejecución (8080)
server.servlet.context-path: Ruta base de la app (/app)

# Logging
logging.level.root: Nivel global (INFO)
logging.level.com.prueba3.pagina3: Nivel específico del paquete
```

---

### 3. **Estructura de Carpetas de Recursos**

```
resources/
├── static/
│   ├── img/              (Imágenes del catálogo de pizzas)
│   └── style/
│       ├── carrito.css   (Estilos del carrito)
│       ├── login.css     (Estilos del login)
│       ├── registro.css  (Estilos del registro)
│       ├── seleccion.css (Estilos del catálogo)
│       └── ... (otros CSS)
│
└── templates/
    ├── carrito.html      (Página del carrito)
    ├── Index.html        (Página principal)
    ├── login.html        (Formulario login)
    ├── registro.html     (Formulario registro)
    ├── pago.html         (Formulario pago)
    ├── resultado.html    (Recibo del pedido)
    ├── misionvision.html (Información empresa)
    │
    └── fragment/
        ├── navbar.html   (Barra de navegación)
        └── footer.html   (Pie de página)
```

---

## 🔐 Flujos Principales de Negocio

### Flujo de Compra:

```
1. Visitante accede a "/" 
   ↓
2. Ve catálogo de pizzas (mostrarOpciones)
   ↓
3. Selecciona pizza y tamaño, agrega al carrito (agregarAlCarrito)
   ↓
4. Ver carrito (mostrarCarrito)
   ↓
5. Puede:
   a) Actualizar cantidades (actualizarCantidadCarrito)
   b) Eliminar items (eliminarDelCarrito)
   c) Vaciar carrito (vaciarCarrito)
   d) Proceder a pago (confirmarPedido)
   ↓
6. Si no está logueado: redirige a login/registro
   ↓
7. Formulario de pago (mostrarPago)
   ↓
8. Selecciona método de pago
   ↓
9. Procesa pago (procesarPago)
   ↓
10. Muestra recibo (resultado.html)
    ↓
11. Carrito se vacía automáticamente
```

### Flujo de Registro:

```
1. Usuario accede a "/registro"
   ↓
2. Completa formulario (mostrarFormularioRegistro)
   ↓
3. Envía datos (procesarRegistro)
   ↓
4. Validaciones:
   - Email no debe existir
   ↓
5. Si válido:
   - Crea usuario en BD (encripta contraseña)
   - Crea sesión
   - Migra carrito de invitado
   ↓
6. Redirige a carrito o inicio
```

### Flujo de Login:

```
1. Usuario accede a "/login"
   ↓
2. Ingresa email y contraseña (mostrarLogin)
   ↓
3. Envía datos (procesarLogin)
   ↓
4. Validaciones:
   - Campos no vacíos
   - Email existe
   - Contraseña coincide
   ↓
5. Si válido:
   - Crea sesión con email
   - Migra carrito de invitado
   ↓
6. Redirige a URL solicitada o inicio
```

---

## 📊 Relaciones de Base de Datos

```
┌─────────────────────┐
│      Opcion         │
│ (Pizzas)            │
├─────────────────────┤
│ id (PK)             │
│ nombre              │
│ descripcion         │
│ precioPequena       │
│ precioMediana       │
│ precioGrande        │
│ imagen              │
└──────────┬──────────┘
           │ 1:N
           ↓
┌──────────────────────────┐
│    ItemCarrito           │
│ (Items en carrito)       │
├──────────────────────────┤
│ id (PK)                  │
│ opcion_id (FK)           │
│ nombre                   │
│ tamano                   │
│ cantidad                 │
│ precioUnitario           │
│ subtotal                 │
│ usuario_email            │
└──────────────────────────┘

┌──────────────────────────┐
│      Usermodel           │
│ (Usuarios registrados)   │
├──────────────────────────┤
│ id (PK)                  │
│ nombre                   │
│ apellidos                │
│ telefono                 │
│ direccion                │
│ email (UNIQUE)           │
│ password (encriptada)    │
│ created_at               │
└──────────────────────────┘
```

---
## Diagrama ER
(https://img.plantuml.biz/plantuml/png/fLF1IiD04BtlLoov249-eAU25H4KYzApJBAZBcnsucOMqQR_xYGsoKPhAVGIaZTltxmtiras385ZvPH2pvO_zQQ942pfhTBwqXgzq7VaN-KZp-NXgIe22lZ4mVOmLc1T2jJGD1yKp8JjHl67khJaJzQk0mf_ZUyHFKZ1K2mSZeLxD1OE5Mu2U8CJFXcuPQok80JBfoTagC1JYfGWQWQwezRps7RZBVUpImmriM1YS2aMVqPyFP_SP78jkSn__bwmVEiYhu0xhJ_YA1irT4XsXK9rU5Jzh6JIwa8_eeDkoTYeuIgrRPxJTeYrq5b0SFOBChM7zeIvGU6L96xGOwRcfHtbUTiEjqc8rByzMvEfTCF7UB_CfSU4k6-hbkYD_1tV)

## 🔍 Consideraciones Técnicas

### Seguridad:
- ✅ Contraseñas encriptadas con BCrypt
- ✅ Validación de emails únicos
- ✅ Sesiones HTTP para usuario autenticado
- ⚠️ No hay HTTPS configurado (considerar para producción)
- ⚠️ No hay CSRF protection explícita (verificar en config)

### Rendimiento:
- ✅ JOIN FETCH en ItemCarritoRepository evita N+1
- ✅ Cálculos en memoria para invitados
- ⚠️ Sin paginación para lista de usuarios
- ⚠️ Sin caché implementado

### Datos:
- ✅ Transacciones en operaciones críticas
- ✅ Relaciones adecuadamente mapeadas
- ✅ Validación de datos en servicios
- ⚠️ Sin validación de entrada en controladores (bean validation)

### Funcionalidades Especiales:
- ✅ Carrito dual: memoria para invitados, BD para usuarios
- ✅ Migración automática de carrito en registro/login
- ✅ Protección de rutas (login requerido para pago)
- ✅ Flash attributes para mensajes post-redireccionamiento

---

**Fin de la Documentación**
