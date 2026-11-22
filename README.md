# sb-taller-git-2026

Servicio Spring Boot (Java 21, Maven) para el dominio de Minecraft. Implementa
el CRUD REST de entidades, la persistencia en H2 con Spring Data JPA, una
simulación de explosiones con dos reglas de negocio, una capa service que valida
y lanza excepciones, y un handler HTTP por tipo de error.

## Arranque

```bash
./mvnw spring-boot:run
```

El servicio queda en `http://localhost:8080`. Para compilar y correr las
pruebas:

```bash
./mvnw -q compile
./mvnw test
```

La consola de H2 está disponible en `http://localhost:8080/h2-console` con:

| Dato | Valor |
| --- | --- |
| JDBC URL | `jdbc:h2:file:./data/entidades` |
| Usuario | `sa` |
| Password | vacío |

El archivo de la base se crea en `data/entidades.mv.db` y está ignorado por
Git. Si se detiene la aplicación y se borra ese archivo, la próxima ejecución
recrea el esquema vacío.

## Propiedades de H2

| Propiedad | Para qué sirve |
| --- | --- |
| `spring.application.name=sb-taller-git-2026` | Nombre de la aplicación en logs y actuator. |
| `spring.datasource.url=jdbc:h2:file:./data/entidades` | H2 en modo archivo, dentro del proyecto. |
| `spring.datasource.username=sa` | Usuario de desarrollo de H2. |
| `spring.datasource.password=` | Password vacío, solo para desarrollo local. |
| `spring.jpa.hibernate.ddl-auto=update` | Hibernate crea o actualiza las tablas al arrancar. |
| `spring.jpa.open-in-view=false` | Evita que la sesión JPA quede abierta durante la serialización HTTP. |
| `spring.h2.console.enabled=true` | Habilita la consola web de H2 en desarrollo. |
| `api.restful-api.base-url=https://api.restful-api.dev` | Base de la API REST externa consultada con WebClient. |

## Modelo persistido

Las tres clases concretas que se persisten son:

- `Jugador`: entidad que recibe daño en la simulación.
- `Creeper`: hostil que origina la explosión.
- `Bloque`: objeto del mundo que puede quedar destruido.

`Entidad` es la clase base abstracta de la familia y `Mob`/`Hostil` son clases
intermedias. `Jugador` y `Creeper` son dos especializaciones independientes de
`Entidad`; `Creeper` llega por `Mob -> Hostil` y `Jugador` hereda directo. La
jerarquía se conserva como modelo de estado: las decisiones de ataque,
comportamiento y reglas de juego viven en la capa service.

### Herencia y discriminador

Se eligió `InheritanceType.SINGLE_TABLE`:

- `Entidad` es una `@Entity` abstracta.
- `Mob` y `Hostil` son `@MappedSuperclass`.
- `Jugador` y `Creeper` son `@Entity` con `@DiscriminatorValue`.
- La tabla `entidades` tiene una columna `tipo` que distingue `jugador` de
  `creeper`.

La elección evita joins y deja una sola tabla para consultar la familia. Como
contrapartida, las columnas propias de una hija quedan nulas en las filas de la
otra; por eso `experiencia` es opcional a nivel de tabla y solo se usa en
`Jugador`.

`Vector` es `@Embeddable` y se guarda en las columnas `posicion_x`,
`posicion_y`, `posicion_z` de `Entidad`. `Bloque` usa una tabla separada,
`bloques`, con `id` autogenerado por la base, `tipo`, `destruido` y las
coordenadas `x`, `y`, `z`.

JPA necesita un constructor sin argumentos. En todas las entidades ese
constructor es `protected`: el marco puede instanciarlas, pero el resto del
código no puede crear objetos incompletos. Los constructores públicos solo
inicializan estado; los service validan antes de llamarlos.

### Identidad

La clave es `id` (`String`). El JSON de creación puede traerlo o omitirlo:

- Si lo trae, se usa como clave natural; es util para probar con ids legibles
  como `steve` o `creeper-poo08`.
- Si lo omite, el servicio genera un `UUID` antes de guardar.

En los dos casos la URL usa el mismo `id` que quedó persistido. El id no es un
índice de una lista del controller.

### Discriminación del subtipo en JSON

El request y el response usan DTOs (`EntidadRequest` y `EntidadResponse`) con
un campo explícito `tipo` (`jugador` o `creeper`). No se usaron anotaciones
polimórficas de Jackson sobre las entidades.

Esa elección mantiene a Jackson fuera de la construcción del dominio: el
service valida los datos y llama al constructor correspondiente. Si
Jackson instanciara las entidades directamente, necesitaría setters o
constructores vacios públicos y sería mucho más fácil dejar un objeto en un
estado imposible. El DTO también desacopla el contrato HTTP de las columnas
internas de JPA.

## Sobrecarga y sobreescritura

### Sobrecarga

La sobrecarga aparece cuando dos métodos de la misma clase tienen el mismo
nombre y distinta lista de parámetros. Se resuelve en compilación según los
argumentos de la llamada.

- `Bloque(String)` y `Bloque(String, Vector)`: la misma construcción con y sin
  posición explicita; el service siempre pasa una posición validada.
- `Jugador` tiene cuatro constructores: `Jugador(String)`,
  `Jugador(String, int)`, `Jugador(String, int, int)` y
  `Jugador(String, int, int, Vector)`. Los tres primeros delegan directamente en
  el completo y dejan el objeto en un estado legal.
- `Vector()` y `Vector(double, double, double)` son dos constructores con
  distinta lista de parámetros.

### Sobreescritura

La sobreescritura aparece cuando una clase redefine un método con la misma
firma que el padre. Después de esta corrección el dominio no tiene métodos de
negocio sobreescritos: las clases hijas solo heredan estado. La unica
sobreescritura presente es `Vector.toString()`, que cambia la representación
textual heredada de `Object`.

`Mob` y `Hostil` conservan la relación de herencia y los constructores llaman a
`super` cuando corresponde. Las decisiones que antes estaban en métodos
sobreescritos ahora están en `MobServiceImpl` y `ExplosionServiceImpl`, donde se
resuelven según el tipo concreto con `instanceof`.

La diferencia práctica sigue siendo la firma y el momento de resolución: la
sobrecarga repite el nombre en la misma clase con parámetros distintos y se
elige al compilar; la sobreescritura repite la firma completa y se elige al
ejecutar mediante polimorfismo.

## Repositorios

- `EntidadRepository extends JpaRepository<Entidad, String>`: CRUD de la familia
  y la consulta derivada `findBySaludGreaterThan(int)`, usada por
  `GET /entidades/vivos`.
- `BloqueRepository extends JpaRepository<Bloque, Long>`: persistencia de los
  bloques de la simulación.

Los mensajes de consulta se declaran en la interfaz; Spring Data genera la
implementación. El controller depende de `EntidadService`, el service depende
de `EntidadRepository`, y ninguno implementa a mano el acceso a datos.

## API REST

| Método | URL | Uso | Éxito |
| --- | --- | --- | --- |
| `GET` | `/` | Confirmar que el servicio vive. | `200` |
| `GET` | `/jugador` | Construir un jugador con parámetros de consulta (POO-06). | `200` o `400` |
| `GET` | `/entidades` | Listar lo persistido. | `200` |
| `GET` | `/entidades/{id}` | Buscar una entidad. | `200` o `404` |
| `GET` | `/entidades/vivos` | Listar entidades con salud mayor a 0. | `200` |
| `POST` | `/entidades` | Crear jugador o creeper. | `201` |
| `PUT` | `/entidades/{id}` | Reemplazar el recurso completo. | `200` |
| `DELETE` | `/entidades/{id}` | Borrar una entidad. | `204` |
| `POST` | `/simulacion/explosiones` | Ejecutar sobre entidades persistidas y guardar el resultado. | `200` |
| `POST` | `/bloques` | Crear un bloque persistido para referenciarlo en la simulación. | `201` |
| `GET` | `/bloques` | Ver los bloques persistidos. | `200` |
| `GET` | `/mobs/ataque` | Calcular ataques con `Zombie` y `Aldeano` desde el service. | `200` |
| `GET` | `/mobs/comportamiento` | Comportamientos de entidades persistidas leídas de H2, expuestos por `ComportamientoIA`. | `200` |
| `GET` | `/objetos-externos` | Consultar la API REST externa con WebClient. | `200` |
| `GET` | `/v3/api-docs` | Especificación OpenAPI generada por springdoc. | `200` |
| `GET` | `/swagger-ui.html` | Interfaz web de Swagger. | `200` |

### Consulta a la API externa

`GET /objetos-externos` usa `WebClient` y la propiedad
`api.restful-api.base-url=https://api.restful-api.dev` para consultar
`/objects`. La respuesta es una lista de `ObjetoExternoResponse`; si la API
externa falla, el error se propaga al cliente HTTP.

### Documentación OpenAPI

springdoc publica `/v3/api-docs` y `/swagger-ui.html`. Los controladores y DTOs
llevan anotaciones `@Tag`, `@Operation` y `@Schema`, por lo que la documentación
de la API se genera desde el código.

Ejemplo de creación de un jugador:

```json
{
  "tipo": "jugador",
  "id": "steve",
  "salud": 20,
  "saludMaxima": 20,
  "experiencia": 7,
  "x": 3,
  "y": 0,
  "z": 0
}
```

Ejemplo de creación de un creeper:

```json
{
  "tipo": "creeper",
  "id": "creeper-poo08",
  "salud": 20,
  "saludMaxima": 20,
  "x": 0,
  "y": 0,
  "z": 0
}
```

## Las dos reglas de negocio

Las dos reglas viven en la capa service y las dispara el controller de
simulación a través de `ExplosionService`. Las clases del dominio solo guardan
estado; no validan, no lanzan excepciones y no calculan reglas de juego.

1. **El daño de la explosión decae con la distancia.** En
   `ExplosionServiceImpl.danioDeExplosion(distancia, radio)`, un objetivo dentro
   del radio recibe hasta 20 puntos de daño; la fórmula es
   `max(1, round(20 * (1 - distancia / radio)))`. Fuera del radio el daño es 0.
2. **Todo bloque alcanzado por el radio queda destruido.** En
   `BloqueServiceImpl.aplicarExplosion(bloque, distancia, radio)`, si la
   distancia es menor o igual al radio, el bloque se marca como `destruido`.

La simulación carga el creeper, los jugadores y los bloques desde H2 usando
solo los ids del cuerpo; no crea entidades nuevas ni reescribe su estado con
datos del pedido. Si falta algún id, responde `404`. Después de aplicar las
reglas guarda el jugador con su salud actualizada y el bloque con su estado.

Ejemplo de simulación:

```json
{
  "creeperId": "creeper-poo08",
  "radio": 5,
  "jugadores": [
    { "id": "steve" }
  ],
  "bloques": [
    { "id": 1 }
  ]
}
```

El creeper y `steve` deben existir por `POST`/`PUT /entidades`, y el bloque `1`
por `POST /bloques`. La posición y la salud salen de H2: el `origen`, la salud
máxima del creeper y la salud/posición de cada jugador ya no van en el pedido.
En este caso `steve` está a distancia 3 de un radio 5, recibe 8 de daño y pasa
de 15 a 7; el bloque a distancia 2 queda destruido. Repetir el pedido parte de 7
y acumula el daño en vez de curarlo. Un `GET /entidades/steve` o `GET /bloques`
posterior muestra el estado guardado.

`Bloque` no expone `setDestruido`. El único camino para marcar un bloque es
`BloqueServiceImpl.aplicarExplosion`, que valida radio y distancia y devuelve una
nueva instancia con el estado calculado.

## Comportamiento en la capa service

`MobService` expone `simularAtaques()` y `listarComportamientos()`. En
`listarComportamientos()` el service ya no arma mobs temporales: lee las filas
de H2 con `EntidadRepository.findAll()`, las ordena por id y devuelve una lista
de `ComportamientoIA`. Cada fila persistida se adapta a un
`ComportamientoResponse`, que implementa esa interfaz; las descripciones se
calculan en `MobServiceImpl` según el tipo concreto (`Jugador`, `Creeper`).
`MobController` consume la interfaz y construye el DTO de respuesta.

Las clases del dominio siguen sin métodos de comportamiento: `Zombie` y
`Aldeano` solo conservan el estado de las familias `Hostil` y `Pacifico`, usadas
por `simularAtaques()`. La lógica que antes estaba en `ComportamientoIA` y
`AtacaJugador` vive en el service, y la interfaz `ComportamientoIA` volvió como
contrato de salida del endpoint, no como responsabilidad de la entidad.

## Excepciones y handlers

La jerarquía es:

- `DominioException`: clase base abstracta, no comprobada, extiende
  `RuntimeException`.
- `EntidadNoEncontradaException`: hija para un id que no existe. Responde
  `404`.
- `ReglaDeJuegoException`: hija para una regla de negocio incumplida. Responde
  `422`.

La jerarquía es no comprobada porque un id inexistente o una regla de juego
rota no se resuelven localmente: son errores de la solicitud que el handler
HTTP traduce. Si fueran comprobadas, cada capa tendría que declarar `throws` y
el controller quedaría acoplado a los detalles del dominio.

El JSON de error siempre lleva `tipo` y `mensaje`, de modo que las hermanas se
distinguen aunque el cliente no mire solo el código HTTP:

```json
{
  "tipo": "entidad_no_encontrada",
  "mensaje": "No existe una entidad con id no-existe."
}
```

```json
{
  "tipo": "regla_de_juego",
  "mensaje": "La salud no puede superar la salud máxima."
}
```

`HttpMessageNotReadableException` responde `400` con `tipo: "json_invalido"`.
`IllegalArgumentException` responde `400` con `tipo: "argumento_invalido"`.
Se eligió `422` para las reglas de juego porque el JSON está bien formado pero
el contenido viola una regla de negocio validada en el service; el `400` queda
para cuerpos malformados o argumentos inválidos.

## Diagrama de clases

```mermaid
classDiagram
    direction TB

    class Entidad {
        <<abstract>>
        -String id
        -Vector posicion
        -int salud
        -int saludMaxima
    }

    class Mob {
        <<abstract>>
    }

    class Hostil {
        <<abstract>>
    }

    class Pacifico {
        <<abstract>>
        -boolean pacifico
    }

    class Jugador {
        -Inventario inventario
        -int experiencia
    }

    class Creeper

    class Zombie

    class Aldeano {
        -Profesion profesion
    }

    class Bloque {
        -Long id
        -String tipo
        -Vector posicion
        -boolean destruido
    }

    class Vector {
        -double x
        -double y
        -double z
    }

    class Inventario

    class EntidadRepository {
        <<interface>>
        +findAll()
        +findById(String id)
        +save(Entidad entidad)
        +findBySaludGreaterThan(int salud)
    }

    class BloqueRepository {
        <<interface>>
        +findAll()
        +findById(Long id)
        +save(Bloque bloque)
    }

    class EntidadService {
        <<interface>>
        +listar()
        +listarVivos()
        +obtener(String id)
        +crear(EntidadRequest request)
        +actualizar(String id, EntidadRequest request)
        +eliminar(String id)
        +construirJugador(String id, int saludMaxima, int experiencia, double x, double y, double z)
    }

    class EntidadController {
        +listar()
        +listarVivos()
        +obtener(String id)
        +crear(EntidadRequest request)
        +actualizar(String id, EntidadRequest request)
        +eliminar(String id)
    }

    class ExplosionService {
        <<interface>>
        +simular(ExplosionRequest request)
    }

    class MobService {
        <<interface>>
        +simularAtaques()
        +listarComportamientos()
    }

    class AtaqueResponse
    class ComportamientoResponse

    class ExplosionController {
        +simular(ExplosionRequest request)
    }

    class BloqueService {
        <<interface>>
        +listar()
        +crear(String tipo, Vector posicion)
        +actualizar(Bloque bloque, String tipo, Vector posicion)
        +aplicarExplosion(Bloque bloque, double distancia, double radio) Bloque
    }

    class BloqueController {
        +listar()
    }

    class MobController {
        +simularAtaques()
        +listarComportamientos()
    }

    class DominioException {
        <<abstract>>
    }

    class EntidadNoEncontradaException
    class ReglaDeJuegoException
    class RestExceptionHandler

    Entidad <|-- Mob
    Entidad <|-- Jugador
    Mob <|-- Hostil
    Mob <|-- Pacifico
    Hostil <|-- Creeper
    Hostil <|-- Zombie
    Pacifico <|-- Aldeano
    Entidad *-- Vector : posicion
    Bloque *-- Vector : posicion
    Jugador *-- Inventario : inventario
    EntidadRepository ..> Entidad : persiste
    BloqueRepository ..> Bloque : persiste
    EntidadService ..> EntidadRepository : usa
    EntidadController ..> EntidadService : usa
    ExplosionService ..> Creeper : regla 1
    ExplosionService ..> Jugador : aplica daño
    ExplosionService ..> BloqueService : regla 2
    ExplosionService ..> EntidadRepository : lee y persiste
    ExplosionService ..> BloqueRepository : lee y persiste
    ExplosionController ..> ExplosionService : usa
    BloqueService ..> Bloque : valida y devuelve estado
    BloqueService ..> BloqueRepository : usa
    BloqueController ..> BloqueService : usa
    BloqueController ..> BloqueRequest : recibe
    MobService ..> Zombie : calcula
    MobService ..> Aldeano : calcula
    MobService ..> EntidadRepository : lee filas
    MobService ..> ComportamientoIA : devuelve
    ComportamientoIA <|.. ComportamientoResponse
    MobService ..> AtaqueResponse : devuelve
    MobController ..> MobService : usa
    DominioException <|-- EntidadNoEncontradaException
    DominioException <|-- ReglaDeJuegoException
    RestExceptionHandler ..> EntidadNoEncontradaException : 404
    RestExceptionHandler ..> ReglaDeJuegoException : 422
```

En el diagrama se distinguen:

- **Herencia:** líneas con `<|--` entre `Entidad`, `Mob`, `Hostil`, `Pacifico`,
  `Jugador`, `Creeper`, `Zombie` y `Aldeano`.
- **Estado del dominio:** `Entidad`, `Mob`, `Hostil`, `Pacifico`, `Jugador`,
  `Creeper`, `Zombie`, `Aldeano` y `Bloque` solo guardan datos.
- **Uso del repositorio:** dependencias con `..>` desde el service/controller
  hacia `EntidadRepository` y `BloqueRepository`.
- **Jerarquía de excepciones:** `DominioException` como base y sus dos hijas,
  más las dependencias del handler.

## Colección Insomnia

El archivo versionado es `insomnia/sb-taller-git-2026.json`.

Para importarlo:

1. Abrir Insomnia.
2. Elegir `Import` y luego `From File`.
3. Seleccionar `insomnia/sb-taller-git-2026.json`.
4. Verificar que el entorno incluido se llame `Local` y que `base_url` sea
   `http://localhost:8080`.

Orden recomendado:

1. `00 - Servicio vivo`.
2. `01 - Crear jugador` y `02 - Crear creeper`.
3. `03` a `08` para recorrer listar, consultar, actualizar y borrar.
4. `09 - Crear bloque`.
5. `10 - Simular explosión`.
6. `11 - Ver jugador después de simular` y `12 - Ver bloques persistidos`.
7. `13 - Comportamiento`.
8. `14 - Error ReglaDeJuego (422)`, `15 - Error EntidadNoEncontrada (404)` y
   `16 - Error JSON inválido (400)`.

Si se vuelve a ejecutar la colección sobre la misma base, el `POST` de `steve`
puede fallar por id duplicado. Para reiniciar: detener la aplicación, borrar
`data/entidades.mv.db` y arrancar de nuevo. También se pueden borrar
`steve`/`creeper-poo08` con `DELETE` antes de repetir.

La prueba de que un `POST` ilegal no ensucia la base es el request
`14 - Error ReglaDeJuego (422)`: responde error y el request `03 - Listar
entidades` sigue mostrando las mismas filas, sin `ilegal`.

## Preguntas de «Qué hay que pensar»

**Recurso y verbo.** El sustantivo es `entidades`; las URL nombran el recurso y
el verbo HTTP dice la operación: `GET` lee, `POST` crea, `PUT` actualiza y
`DELETE` borra. La simulación es un recurso propio, `explosiones`, bajo
`/simulacion`.

**Identidad.** La clave es `id`. El JSON puede traerlo o no; si no viene, el
servicio genera un UUID. La URL usa el id persistido, no una posición de lista.

**Herencia y tablas.** Se usó una sola tabla con discriminador `tipo` para la
familia de `Entidad`, y una tabla aparte para `Bloque`. El subtipo se
discrimina en el JSON con el campo `tipo` del DTO.

**Interfaz y repositorio.** El controller depende de `EntidadService`, que
depende de `EntidadRepository`, una interfaz. Spring Data genera la
implementación. El controller trabaja contra el contrato y no contra una
implementación concreta: puede cambiar el acceso a datos sin tocar la capa HTTP.

**Comportamiento en el service.** `GET /mobs/ataque` pasa por `MobService`, que
usa el estado de `Zombie` (rama `Hostil`) y `Aldeano` (rama `Pacifico`) para
calcular el daño. `GET /mobs/comportamiento` lee entidades persistidas de H2,
las expone como `ComportamientoIA` desde el service y el controller las adapta
a `ComportamientoResponse`; las entidades del dominio no deciden nada.

**Ocultamiento y validación.** Las reglas numéricas viven en
`ExplosionServiceImpl` y `BloqueServiceImpl`, no en el controller ni en el
dominio. JPA usa acceso por campo y los service validan salud, salud máxima,
posición, tipo, radio y distancia antes de mutar el estado con los setters. El
request `14` de Insomnia muestra que un `POST` ilegal responde `422` y la base
queda igual porque la validación ocurre antes de guardar y la transacción hace
rollback ante cualquier error.

**PUT y PATCH.** Se implementó `PUT`, que reemplaza el recurso completo. Los
campos `salud` y `saludMaxima` son obligatorios; `experiencia` vale 0 si se
omite y las coordenadas valen 0 si se omiten. No se implementó `PATCH`.

**DELETE.** Borra físicamente la fila. Antes de borrar se busca la entidad; si
no existe, `EntidadServiceImpl` lanza `EntidadNoEncontradaException` y el handler
responde `404`. El verbo HTTP no ejecuta una regla de juego adicional.

**Dos reglas.** La primera es el daño decreciente de la explosión; la segunda
es la destrucción de bloques dentro del radio. El recurso HTTP es
`POST /simulacion/explosiones`. Un `GET /entidades/{id}` y un `GET /bloques`
posteriores muestran el estado ya persistido.

**Excepciones.** `EntidadNoEncontradaException` nombra el recurso ausente y
`ReglaDeJuegoException` nombra una regla incumplida. Son tipos distintos: la
primera responde `404` y la segunda `422`. El JSON de error lleva `tipo` y
`mensaje`. Se usan excepciones no comprobadas porque el handler centraliza la
traducción a HTTP y las capas no quedan obligadas a declarar `throws`.

## Bitácora

El detalle de asistentes, modelos y prompts está en `BITACORA.md`.
