# sb-taller-git-2026

Servicio Spring Boot (Java 21, Maven) para el dominio de Minecraft. Implementa
el CRUD REST de entidades, la persistencia en H2 con Spring Data JPA, una
simulacion de explosiones con dos reglas de negocio, una interfaz de dominio y
una jerarquia de excepciones con un handler HTTP por tipo.

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

La consola de H2 esta disponible en `http://localhost:8080/h2-console` con:

| Dato | Valor |
| --- | --- |
| JDBC URL | `jdbc:h2:file:./data/entidades` |
| Usuario | `sa` |
| Password | vacio |

El archivo de la base se crea en `data/entidades.mv.db` y esta ignorado por
Git. Si se detiene la aplicacion y se borra ese archivo, la proxima ejecucion
recrea el esquema vacio.

## Propiedades de H2

| Propiedad | Para que esta |
| --- | --- |
| `spring.application.name=sb-taller-git-2026` | Nombre de la aplicacion en logs y actuator. |
| `spring.datasource.url=jdbc:h2:file:./data/entidades` | H2 en modo archivo, dentro del proyecto. |
| `spring.datasource.username=sa` | Usuario de desarrollo de H2. |
| `spring.datasource.password=` | Password vacio, solo para desarrollo local. |
| `spring.jpa.hibernate.ddl-auto=update` | Hibernate crea o actualiza las tablas al arrancar. |
| `spring.jpa.open-in-view=false` | Evita que la sesion JPA quede abierta durante la serializacion HTTP. |
| `spring.h2.console.enabled=true` | Habilita la consola web de H2 en desarrollo. |

## Modelo persistido

Las tres clases concretas que se persisten son:

- `Jugador`: entidad que recibe dano en la simulacion.
- `Creeper`: hostil que origina la explosion.
- `Bloque`: objeto del mundo que puede quedar destruido.

`Entidad` es la clase base abstracta de la familia y `Mob`/`Hostil` son clases
intermedias. `Jugador` y `Creeper` son dos especializaciones independientes de
`Entidad`; `Creeper` llega por `Mob -> Hostil` y `Jugador` hereda directo. El
metodo abstracto `atacar(Entidad)` de POO-06 sigue existiendo y se sobreescribe
en `Jugador`, `Hostil` y `Pacifico`.

### Herencia y discriminador

Se eligio `InheritanceType.SINGLE_TABLE`:

- `Entidad` es una `@Entity` abstracta.
- `Mob` y `Hostil` son `@MappedSuperclass`.
- `Jugador` y `Creeper` son `@Entity` con `@DiscriminatorValue`.
- La tabla `entidades` tiene una columna `tipo` que distingue `jugador` de
  `creeper`.

La eleccion evita joins y deja una sola tabla para consultar la familia. Como
contrapartida, las columnas propias de una hija quedan nulas en las filas de la
otra; por eso `experiencia` es opcional a nivel de tabla y solo se usa en
`Jugador`.

`Vector` es `@Embeddable` y se guarda en las columnas `posicion_x`,
`posicion_y`, `posicion_z` de `Entidad`. `Bloque` usa una tabla separada,
`bloques`, con `id` autogenerado por la base, `tipo`, `destruido` y las
coordenadas `x`, `y`, `z`.

JPA necesita un constructor sin argumentos. En todas las entidades ese
constructor es `protected`: el marco puede instanciarlas, pero el resto del
codigo no puede crear objetos incompletos. Los constructores publicos de
negocio validan y dejan el objeto en estado legal.

### Identidad

La clave es `id` (`String`). El JSON de creacion puede traerlo o omitirlo:

- Si lo trae, se usa como clave natural; es util para probar con ids legibles
  como `steve` o `creeper-poo08`.
- Si lo omite, el servicio genera un `UUID` antes de guardar.

En los dos casos la URL usa el mismo `id` que quedo persistido. El id no es un
indice de una lista del controller.

### Discriminacion del subtipo en JSON

El request y el response usan DTOs (`EntidadRequest` y `EntidadResponse`) con
un campo explicito `tipo` (`jugador` o `creeper`). No se usaron anotaciones
polimorficas de Jackson sobre las entidades.

Esa eleccion mantiene a Jackson fuera de la construccion del dominio: el
service valida los datos y llama al constructor de negocio correspondiente. Si
Jackson instanciara las entidades directamente, necesitaria setters o
constructores vacios publicos y seria mucho mas facil dejar un objeto en un
estado imposible. El DTO tambien desacopla el contrato HTTP de las columnas
internas de JPA.

## Repositorios

- `EntidadRepository extends JpaRepository<Entidad, String>`: CRUD de la familia
  y la consulta derivada `findBySaludGreaterThan(int)`, usada por
  `GET /entidades/vivos`.
- `BloqueRepository extends JpaRepository<Bloque, Long>`: persistencia de los
  bloques de la simulacion.

Los mensajes de consulta se declaran en la interfaz; Spring Data genera la
implementacion. El controller depende de `EntidadService`, el service depende
de `EntidadRepository`, y ninguno implementa a mano el acceso a datos.

## API REST

| Metodo | URL | Uso | Exito |
| --- | --- | --- | --- |
| `GET` | `/` | Confirmar que el servicio vive. | `200` |
| `GET` | `/entidades` | Listar lo persistido. | `200` |
| `GET` | `/entidades/{id}` | Buscar una entidad. | `200` o `404` |
| `GET` | `/entidades/vivos` | Listar entidades con salud mayor a 0. | `200` |
| `POST` | `/entidades` | Crear jugador o creeper. | `201` |
| `PUT` | `/entidades/{id}` | Reemplazar el recurso completo. | `200` |
| `DELETE` | `/entidades/{id}` | Borrar una entidad. | `204` |
| `POST` | `/simulacion/explosiones` | Ejecutar y persistir la explosion. | `200` |
| `GET` | `/bloques` | Ver los bloques persistidos. | `200` |
| `GET` | `/mobs/comportamiento` | Endpoint que usa `ComportamientoIA`. | `200` |

Ejemplo de creacion de un jugador:

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

Ejemplo de creacion de un creeper:

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

Las dos reglas viven en clases del dominio y las dispara el controller de
simulacion a traves de `ExplosionService`. Participan `Creeper`, `Jugador` y
`Bloque`.

1. **El dano de la explosion decae con la distancia.** En
   `Creeper.danioDeExplosion(distancia, radio)`, un objetivo dentro del radio
   recibe hasta 20 puntos de dano; la formula es
   `max(1, round(20 * (1 - distancia / radio)))`. Fuera del radio el dano es 0.
2. **Todo bloque alcanzado por el radio queda destruido.** En
   `Bloque.destruidoPorExplosion(distancia, radio)`, si la distancia es menor o
   igual al radio, el bloque se marca como `destruido`.

La simulacion persiste el creeper, el jugador con su salud actualizada y los
bloques con su estado. Un `GET /entidades/{id}` o `GET /bloques` posterior
muestra el estado ya guardado.

Ejemplo de simulacion:

```json
{
  "creeperId": "creeper-poo08",
  "saludMaximaCreeper": 20,
  "radio": 5,
  "origen": { "x": 0, "y": 0, "z": 0 },
  "jugadores": [
    {
      "id": "steve",
      "salud": 20,
      "saludMaxima": 20,
      "posicion": { "x": 3, "y": 0, "z": 0 }
    }
  ],
  "bloques": [
    {
      "tipo": "tierra",
      "posicion": { "x": 2, "y": 0, "z": 0 }
    }
  ]
}
```

En este caso `steve` esta a distancia 3 de un radio 5, asi que recibe 8 de
dano y queda con 12 de salud. El bloque a distancia 2 queda destruido.

## Interfaz de dominio

`ComportamientoIA` declara:

```java
void actuar(Mob mob);
String describir();
```

La implementan `Zombie` (familia `Hostil`) y `Aldeano` (familia `Pacifico`).
El endpoint `GET /mobs/comportamiento` arma una `List<ComportamientoIA>` y llama
`describir()` sobre el tipo de la interfaz, sin preguntar por la clase concreta.
Eso permite hablar con objetos de familias distintas a traves del mismo
protocolo.

## Excepciones y handlers

La jerarquia es:

- `DominioException`: clase base abstracta, no comprobada, extiende
  `RuntimeException`.
- `EntidadNoEncontradaException`: hija para un id que no existe. Responde
  `404`.
- `ReglaDeJuegoException`: hija para una regla de dominio incumplida. Responde
  `422`.

La jerarquia es no comprobada porque un id inexistente o una regla de juego
rota no se resuelven localmente: son errores de la solicitud que el handler
HTTP traduce. Si fueran comprobadas, cada capa tendria que declarar `throws` y
el controller quedaria acoplado a los detalles del dominio.

El JSON de error siempre lleva `tipo` y `mensaje`, de modo que las hermanas se
distinguen aunque el cliente no mire solo el codigo HTTP:

```json
{
  "tipo": "entidad_no_encontrada",
  "mensaje": "No existe una entidad con id no-existe."
}
```

```json
{
  "tipo": "regla_de_juego",
  "mensaje": "La salud no puede superar la salud maxima."
}
```

`HttpMessageNotReadableException` responde `400` con `tipo: "json_invalido"`.
`IllegalArgumentException` responde `400` con `tipo: "argumento_invalido"`.
Se eligio `422` para las reglas de juego porque el JSON esta bien formado pero
el contenido viola una regla del dominio; el `400` queda para cuerpos
malformados o argumentos invalidos.

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
        +atacar(Entidad objetivo)*
        +moverse()
        +recibirDano(int cantidad)
        +curar(int cantidad)
        +estaVivo() boolean
    }

    class Mob {
        <<abstract>>
        -ComportamientoIA ia
        +generarBotin()
        +deambular()
    }

    class Hostil {
        <<abstract>>
        -AtacaJugador agresivo
        +atacar()
        +atacar(int distancia)
        +atacar(Entidad objetivo)
    }

    class Pacifico {
        <<abstract>>
        +atacar(Entidad objetivo)
    }

    class Jugador {
        -Inventario inventario
        -int experiencia
        +atacar(Entidad objetivo)
        +construir(Bloque bloque)
    }

    class Creeper {
        +danioDeExplosion(double distancia, double radio) int
    }

    class Zombie {
        +actuar(Mob mob)
        +describir() String
    }

    class Aldeano {
        -Profesion profesion
        +actuar(Mob mob)
        +describir() String
    }

    class Bloque {
        -Long id
        -String tipo
        -Vector posicion
        -boolean destruido
        +destruidoPorExplosion(double distancia, double radio) boolean
    }

    class Vector {
        -double x
        -double y
        -double z
    }

    class ComportamientoIA {
        <<interface>>
        +actuar(Mob mob)
        +describir() String
    }

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
    }

    class EntidadController {
        +GET /entidades
        +GET /entidades/{id}
        +POST /entidades
        +PUT /entidades/{id}
        +DELETE /entidades/{id}
    }

    class ExplosionService {
        <<interface>>
        +simular(ExplosionRequest request)
    }

    class ExplosionController {
        +POST /simulacion/explosiones
    }

    class BloqueService {
        <<interface>>
        +listar()
    }

    class BloqueController {
        +GET /bloques
    }

    class MobController {
        +GET /mobs/comportamiento
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
    ComportamientoIA <|.. Zombie
    ComportamientoIA <|.. Aldeano
    Entidad *-- Vector : posicion
    Bloque *-- Vector : posicion
    EntidadRepository ..> Entidad : persiste
    BloqueRepository ..> Bloque : persiste
    EntidadService ..> EntidadRepository : usa
    EntidadController ..> EntidadService : usa
    ExplosionService ..> Creeper : regla 1
    ExplosionService ..> Jugador : persiste dano
    ExplosionService ..> Bloque : regla 2
    ExplosionService ..> EntidadRepository : persiste
    ExplosionService ..> BloqueRepository : persiste
    ExplosionController ..> ExplosionService : usa
    BloqueService ..> BloqueRepository : usa
    BloqueController ..> BloqueService : usa
    MobController ..> ComportamientoIA : usa
    DominioException <|-- EntidadNoEncontradaException
    DominioException <|-- ReglaDeJuegoException
    RestExceptionHandler ..> EntidadNoEncontradaException : 404
    RestExceptionHandler ..> ReglaDeJuegoException : 422
```

En el diagrama se distinguen:

- **Herencia:** lineas con `<|--` entre `Entidad`, `Mob`, `Hostil`, `Pacifico`,
  `Jugador`, `Creeper`, `Zombie` y `Aldeano`.
- **Implementacion de interfaz:** lineas con `<|..` entre
  `ComportamientoIA` y `Zombie`/`Aldeano`.
- **Uso del repositorio:** dependencias con `..>` desde el service/controller
  hacia `EntidadRepository` y `BloqueRepository`.
- **Jerarquia de excepciones:** `DominioException` como base y sus dos hijas,
  mas las dependencias del handler.

## Coleccion Insomnia

El archivo versionado es `insomnia/POO-08-insomnia.json`.

Para importarlo:

1. Abrir Insomnia.
2. Elegir `Import` y luego `From File`.
3. Seleccionar `insomnia/POO-08-insomnia.json`.
4. Verificar que el entorno incluido se llame `Local` y que `base_url` sea
   `http://localhost:8080`.

Orden recomendado:

1. `00 - Servicio vivo`.
2. `01 - Crear jugador` y `02 - Crear creeper`.
3. `03` a `08` para recorrer listar, consultar, actualizar y borrar.
4. `09 - Simular explosion`.
5. `10 - Ver jugador despues de simular` y `11 - Ver bloques persistidos`.
6. `12 - ComportamientoIA`.
7. `13 - Error ReglaDeJuego (422)`, `14 - Error EntidadNoEncontrada (404)` y
   `15 - Error JSON invalido (400)`.

Si se vuelve a ejecutar la coleccion sobre la misma base, el `POST` de `steve`
puede fallar por id duplicado. Para reiniciar: detener la aplicacion, borrar
`data/entidades.mv.db` y arrancar de nuevo. Tambien se pueden borrar
`steve`/`creeper-poo08` con `DELETE` antes de repetir.

La prueba de que un `POST` ilegal no ensucia la base es el request
`13 - Error ReglaDeJuego (422)`: responde error y el request `03 - Listar
entidades` sigue mostrando las mismas filas, sin `ilegal`.

## Preguntas de "Que hay que pensar"

**Recurso y verbo.** El sustantivo es `entidades`; las URL nombran el recurso y
el verbo HTTP dice la operacion: `GET` lee, `POST` crea, `PUT` actualiza y
`DELETE` borra. La simulacion es un recurso propio, `explosiones`, bajo
`/simulacion`.

**Identidad.** La clave es `id`. El JSON puede traerlo o no; si no viene, el
servicio genera un UUID. La URL usa el id persistido, no una posicion de lista.

**Herencia y tablas.** Se uso una sola tabla con discriminador `tipo` para la
familia de `Entidad`, y una tabla aparte para `Bloque`. El subtipo se
discrimina en el JSON con el campo `tipo` del DTO.

**Interfaz y repositorio.** El controller depende de `EntidadService`, que
depende de `EntidadRepository`, una interfaz. Spring Data genera la
implementacion. Igual que en el protocolo de POO-07, el controller trabaja
contra el contrato y no contra una implementacion concreta: puede cambiar el
acceso a datos sin tocar la capa HTTP.

**Protocolo del dominio.** La interfaz es `ComportamientoIA`. La usa
`GET /mobs/comportamiento`. Si, puede hablar con instancias de familias
distintas: `Zombie` viene de `Hostil` y `Aldeano` de `Pacifico`.

**Ocultamiento.** Las reglas numericas viven en `Creeper` y `Bloque`, no en el
controller. JPA usa acceso por campo, y las actualizaciones de estado pasan por
`Entidad.actualizarEstado`, que valida salud, salud maxima y posicion; los
setters que quedan tambien rechazan valores invalidos. El request `13` de
Insomnia muestra que un `POST` ilegal responde `422` y la base queda igual
porque la validacion ocurre antes de guardar y la transaccion hace rollback
ante cualquier error.

**PUT y PATCH.** Se implemento `PUT`, que reemplaza el recurso completo. Los
campos `salud` y `saludMaxima` son obligatorios; `experiencia` vale 0 si se
omite y las coordenadas valen 0 si se omiten. No se implemento `PATCH`.

**DELETE.** Borra fisicamente la fila. Antes de borrar se busca la entidad; si
no existe, el dominio lanza `EntidadNoEncontradaException` y el handler
responde `404`. El verbo HTTP no ejecuta una regla de juego adicional.

**Dos reglas.** La primera es el dano decreciente de la explosion; la segunda
es la destruccion de bloques dentro del radio. El recurso HTTP es
`POST /simulacion/explosiones`. Un `GET /entidades/{id}` y un `GET /bloques`
posteriores muestran el estado ya persistido.

**Excepciones.** `EntidadNoEncontradaException` nombra el recurso ausente y
`ReglaDeJuegoException` nombra una regla incumplida. Son tipos distintos: la
primera responde `404` y la segunda `422`. El JSON de error lleva `tipo` y
`mensaje`. Se usan excepciones no comprobadas porque el handler centraliza la
traduccion a HTTP y las capas no quedan obligadas a declarar `throws`.

## Bitacora

El detalle de asistentes, modelos y prompts esta en `BITACORA.md`.
