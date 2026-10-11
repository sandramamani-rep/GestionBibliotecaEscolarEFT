![Duoc UC](https://www.duoc.cl/wp-content/uploads/2022/09/logo-0.png)

# 📚 Evaluación Final Transversal – Desarrollo Orientado a Objetos II

## 👤 Autor del proyecto

- **Nombre completo:** Sandra Mamani
- **Carrera:** Analista programador computacional
- **Sede:** Online

---

## 📘 Descripción general del sistema

**Gestión de Biblioteca Escolar** es una aplicación de escritorio desarrollada en Java para administrar el catálogo, los estudiantes y los préstamos de una biblioteca escolar. Corresponde a la Evaluación Final Transversal de la asignatura **Desarrollo Orientado a Objetos II**.

El sistema utiliza una interfaz gráfica construida con Swing y formularios de IntelliJ IDEA. Los datos se almacenan en MySQL mediante JDBC. Su estructura aplica encapsulamiento, herencia, polimorfismo, una clase abstracta e interfaces, junto con los patrones MVC, DAO y Singleton.

### Funcionalidades

- Inicio de sesión mediante RUT y contraseña.
- Gestión de usuarios, estudiantes, libros y categorías para el bibliotecario.
- Consulta del catálogo y disponibilidad de libros.
- Registro de préstamos y devoluciones con actualización del stock.
- Historial de préstamos y visualización de atrasos.
- Reportes de libros más prestados, historial por estudiante y préstamos pendientes.

### Roles y permisos

| Rol | Funciones |
|---|---|
| **Bibliotecario** | Gestionar usuarios, estudiantes, libros y categorías; administrar préstamos y devoluciones; consultar reportes generales. |
| **Estudiante** | Consultar el catálogo, registrar préstamos propios, devolver sus materiales y consultar su historial. |

Para ingresar como estudiante, el RUT de la cuenta en `usuarios` debe coincidir con un registro de `estudiantes`. Los identificadores de ambas tablas pueden ser diferentes. Si no existe esa asociación, el sistema rechaza el acceso.

### Plazos y estados de los préstamos

El plazo de devolución es de **siete días desde la fecha del préstamo**. Al registrar un préstamo, `fecha_devolucion` guarda el vencimiento calculado. Al devolverlo, ese campo se actualiza con la fecha real de entrega y `devuelto` pasa a `true`.

El vencimiento se reconstruye desde `fecha_prestamo + 7 días`. Entregar el mismo día del vencimiento se considera una entrega a tiempo.

| Situación | Estado mostrado |
|---|---|
| Préstamo activo dentro del plazo | Pendiente |
| Préstamo activo con plazo vencido | Atrasado |
| Entregado hasta el vencimiento | Devuelto a tiempo |
| Entregado después del vencimiento | Devuelto con atraso |

### Diseño y concurrencia

- **Herencia y abstracción:** `Persona` reúne los atributos comunes de `Usuario` y `Estudiante`.
- **Polimorfismo:** ambas subclases implementan `getDescripcion()`; la representación mediante `toString()` utiliza esa implementación.
- **MVC:** las vistas presentan los datos, los controladores aplican las reglas y los modelos representan las entidades.
- **DAO:** las interfaces y sus implementaciones separan las consultas SQL del resto de la aplicación.
- **Singleton:** `DatabaseConnection` mantiene una única instancia del administrador. Cada llamada a `conectar()` abre una conexión JDBC que se cierra al finalizar la operación.
- **SwingWorker:** ejecuta las operaciones de préstamos y sus recargas en segundo plano; los componentes se actualizan en el hilo de eventos de Swing.
- **Sincronización y transacciones:** un bloqueo compartido con `synchronized` protege los procesos de stock dentro de la aplicación. Las transacciones confirman o revierten conjuntamente el préstamo y el cambio de stock.

---

## 🧱 Estructura general del proyecto

```text
GestionBibliotecaEscolarEFT/
├── README.md
├── GestionBibliotecaEscolarEFT.iml
└── src/
    ├── app.Main.java                 # Punto de entrada de la aplicación
    ├── app/
    │   └── PruebaSincronizacion.java
    ├── controlador/             # Validaciones, permisos y reglas del sistema
    ├── dao/                     # Interfaces de persistencia y reportes
    │   └── impl/                # Implementaciones JDBC
    ├── modelo/                  # Persona, Usuario, Estudiante, Libro y otros modelos
    ├── util/
    │   └── DatabaseConnection.java      # Administrador Singleton de conexiones
    ├── vista/                   # Login, ventana principal y vistas independientes
    │                            # Incluye los formularios .form de IntelliJ
    ├── lib/
    │   └── mysql-connector-j-26.7.0.jar
    └── sql/
        ├── biblioteca.sql                 # Creación de la base de datos y sus tablas
        └── poblamiento_biblioteca.sql      # Carga de datos iniciales
```

Cada pestaña tiene su propia vista: usuarios, estudiantes, libros, préstamos, categorías e informes. `VentanaBiblioteca` organiza las pestañas y sus permisos.

---

## ⚙️ Instrucciones para clonar y ejecutar el proyecto

### 1. Requisitos

- IntelliJ IDEA con soporte para los formularios Swing `.form` utilizados por el proyecto.
- **JDK 25**, utilizado para verificar la compilación local.
- MySQL Server; las pruebas locales se realizaron con **MySQL 8.4.11**.
- MySQL Connector/J, incluido en `src/lib`.
- Git, si se descarga el proyecto mediante clonación.

### 2. Clonar el repositorio

```bash
git clone https://github.com/sandramamani-rep/GestionBibliotecaEscolarEFT.git
cd GestionBibliotecaEscolarEFT
```

También se puede descargar el repositorio como ZIP y extraerlo antes de abrirlo en IntelliJ IDEA.

### 3. Preparar MySQL

Inicia el servidor MySQL y ejecuta los scripts **en este orden**, desde la carpeta raíz del proyecto:

```bash
mysql -u root -p < src/sql/biblioteca.sql
mysql -u root -p < src/sql/poblamiento_biblioteca.sql
```

También puedes abrir ambos archivos en MySQL Workbench y ejecutarlos en el mismo orden.

1. **`biblioteca.sql`** crea la base `biblioteca` y las tablas `usuarios`, `estudiantes`, `categorias`, `libros` y `prestamos`.
2. **`poblamiento_biblioteca.sql`** carga 10 usuarios (un bibliotecario y nueve estudiantes), 10 estudiantes, cinco categorías, 10 libros y 10 préstamos de ejemplo.

Realiza la carga inicial sobre tablas vacías con sus identificadores autoincrementales comenzando en 1: el poblamiento utiliza identificadores numéricos para relacionar categorías, libros y estudiantes. Los scripts no están preparados para repetirse sobre una base ya poblada; podrían producir duplicados o relaciones incorrectas. No es necesario volver a ejecutarlos en cada inicio de la aplicación.

Los nueve usuarios con rol estudiante tienen un registro asociado por RUT. El estudiante Luis Gómez está registrado sin una cuenta de acceso; el bibliotecario puede gestionar sus préstamos, pero necesitaría crearle un usuario con el mismo RUT para que inicie sesión.

Los préstamos de ejemplo tienen fechas de junio de 2025. Los pendientes aparecerán como **Atrasado** al ejecutar el sistema después de sus vencimientos. Para los registros marcados como devueltos, la aplicación interpreta `fecha_devolucion` como entrega real; en estos ejemplos coincide con el vencimiento y se muestran como **Devuelto a tiempo**.

El script inserta los préstamos directamente y no ejecuta el descuento de stock del controlador Java. Los valores de stock cargados se utilizan como disponibilidad inicial de la demostración; al probar un préstamo nuevo desde la aplicación, verifica que se descuente una unidad y que se reponga al devolverlo.

### 4. Configurar la conexión

En `src/util/DatabaseConnection.java`, ajusta los valores de conexión a tu instalación:

```java
private static final String URL = "jdbc:mysql://localhost:3306/biblioteca";
private static final String USER = "TU_USUARIO_MYSQL";
private static final String PASSWORD = "TU_CLAVE_MYSQL";
```

El usuario MySQL debe tener permisos de consulta, inserción, actualización y eliminación sobre las tablas utilizadas. La creación inicial del esquema requiere los permisos correspondientes.

### 5. Configurar IntelliJ IDEA

1. Abre la carpeta raíz del proyecto.
2. En **Project Structure**, selecciona el JDK instalado para el proyecto y el módulo. Ajusta el nivel de lenguaje para que coincida con ese JDK. Los archivos de configuración actuales contienen referencias a `ms-21` y a nivel de lenguaje 25; deben unificarse en el equipo donde se ejecute.
3. Comprueba que `src` esté marcada como carpeta de fuentes.
4. Verifica que `src/lib/mysql-connector-j-26.7.0.jar` figure como dependencia del módulo.
5. Conserva los archivos `.form` junto a sus clases Java y compila mediante IntelliJ para que se genere la inicialización de los controles del diseñador.

### 6. Ejecutar

Ejecuta **`src/app.Main.java`**, que está en el paquete predeterminado. Se abrirá la ventana de inicio de sesión.

Después de ejecutar `poblamiento_biblioteca.sql`, puedes ingresar con estas cuentas incluidas en el script:

| Rol | Nombre | RUT | Contraseña de demostración |
|---|---|---|---|
| Bibliotecario | Antonia Pérez | `12345678-9` | `clave123` |
| Estudiante | Carlos Ruiz | `98765432-1` | `clave123` |
| Estudiante | Tomás Vidal | `88990011-2` | `clave123` |

Todos los usuarios de este poblamiento usan la contraseña de demostración `clave123`.

Utiliza el mismo formato de RUT registrado en la base de datos.

---

## 🧪 Pruebas del sistema

Las pruebas locales verificaron CRUD, autenticación, asociación por RUT, cálculo de vencimiento, devoluciones, stock, reportes y operaciones concurrentes sobre una base MySQL separada.

Para comprobar el funcionamiento desde la interfaz:

1. Ingresa como bibliotecario y registra una categoría, un estudiante y un libro con stock.
2. Comprueba las operaciones de consulta y modificación de los CRUD.
3. Registra un préstamo y verifica que el stock disminuya una unidad.
4. Registra la devolución y verifica la fecha real, el estado y la reposición del stock.
5. Comprueba que un préstamo devuelto no permita otra devolución y que se conserve en el historial.
6. Ingresa como estudiante y verifica que solo puedas operar sobre tus préstamos y consultar el catálogo.
7. Consulta los tres reportes.

`src/app/PruebaSincronizacion.java` permite ejecutar dos solicitudes en paralelo. Antes de usarla, configura identificadores existentes de prueba y un libro con una sola unidad disponible. Debe registrarse un único préstamo y el stock no debe quedar negativo. Esta prueba escribe en la base configurada; utiliza una base destinada a pruebas.

La validación local no sustituye la revisión visual completa de las ventanas y los diálogos ni la prueba de errores de conexión desde la interfaz.

---

**Repositorio GitHub:** [GestionBibliotecaEscolarEFT](https://github.com/sandramamani-rep/GestionBibliotecaEscolarEFT)

**Fecha de entrega:** 11/10/2026

---

© Duoc UC | Escuela de Informática y Telecomunicaciones | Evaluación Final Transversal EFT
