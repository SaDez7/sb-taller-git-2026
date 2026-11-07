# Bitácora de asistencia de inteligencia artificial

**Ejercicios:** POO-06 — Revisión, paquetes, constructores y sobrecarga; POO-08 — CRUD REST, H2, reglas de juego y excepciones
**Asignatura:** Lenguaje de Programación 3 (CYT646)
**Alumno:** Santiago Baez (GitHub: `SaDez7`)
**Repositorio:** `sb-taller-git-2026`
**Dominio:** Minecraft

---

## Asistentes y modelos

| Herramienta | Modelo exacto | Para qué la usé |
|---|---|---|
| Claude (Anthropic), en Claude Code | `claude-sonnet-5` y `claude-sonnet-5-5` (cambié de modelo a mitad de la sesión) | Guía paso a paso del taller de Git, lectura de los enunciados y la rúbrica, revisión de lo que hizo el agente, redacción de este documento y de las especificaciones |
| OpenCode (agente en la terminal de la VM) | `opencode/big-pickle` | Ejecutar los cambios sobre el código del repositorio en POO-06 y POO-08 |

---

## Resumen de prompts

**Taller de Git (Partes A a C)**
- Pedí que me explicara qué dependencias activar en start.spring.io según el enunciado del taller (solo Spring Web) y cómo fusionar el `.gitignore` del starter con el del repo.
- Pedí ayuda con el ciclo `status`, `add`, `commit`, `push` y con la autenticación por token cuando GitHub rechazó la contraseña.
- Copié el modelado de Minecraft del repo anterior y corregí los `package`. Con ayuda detecté que se había colado una segunda clase `@SpringBootApplication` (`MinecraftApplication`) que impedía arrancar, y la eliminé.
- En la colaboración con un compañero agregué la interfaz `AtacaJugador` en una rama aparte y abrí el pull request.

**Ejercicio POO-06**
- Pedí que se revisara el template `lp3-template-tp` para decidir qué carpetas imitar. Me quedé con `domain/` y `rest/controller/`, y descarté `repository/` y `service/` porque son de un ejemplo con base de datos que no aplica acá.
- Pedí un prompt con restricciones para OpenCode
- Ante la pregunta de OpenCode sobre qué hacer con `IllegalArgumentException`, elegí mapearla a 400 en el controller y dejar la validación completa en la clase.
- Pedí los prompts para generar el diagrama Mermaid del README, el apartado de sobrecarga y sobreescritura, y esta bitácora.

**Prompt enviado a OpenCode para el ejercicio POO-06 (texto literal):**

```
Estoy en ~/sb-taller-git-2026 (repo Spring Boot, Java 21, Maven, Spring Web).
Mi dominio es Minecraft. Mis clases están en el paquete
py.edu.uc.lp3.sb_taller_git_2026.minecraft (ej: Creeper, Zombie — confirmame los nombres reales con find/grep antes de tocar nada).

Necesito reorganizar y completar el proyecto según este enunciado:

1. Reorganizar paquetes siguiendo este template:
   https://github.com/alefq/lp3-template-tp/tree/main/src/main/java/py/edu/uc/lp3
   El dominio va en un paquete "domain" y los controllers REST en
   "rest.controller", dentro de mi paquete base. La clase *Application
   no se mueve ni se toca.

2. En la clase base de mi jerarquía, agregar UN método abstracto que
   describa un comportamiento común con implementación distinta por
   tipo (ej: cómo ataca o se mueve cada mob). Dos clases hijas
   independientes lo sobreescriben, misma firma, cada una a su modo.
   Estado privado, sin getters que expongan todo.

3. Agregar constructores simples y sobrecargados en al menos una
   clase del dominio (como el ejemplo de Persona.java del template:
   varias firmas, una llamando a otra con this(...)). Cada firma
   debe dejar el objeto en estado válido.

4. Sobrecargar al menos un mensaje del dominio (mismo nombre, otra
   lista de argumentos — ej: atacar() sin argumentos y atacar(distancia)).

5. Crear dos controllers REST:
   - IndexController en GET / que confirme que el servicio está
     vivo e indique el dominio (Minecraft).
   - Otro controller con @RequestParam que construya una instancia
     del dominio a partir de parámetros de la URL, delegando la
     validación a la clase (si el valor es inválido, la clase lo
     rechaza, el controller no "arregla" nada).
   Un tercer endpoint (puede ser el mismo controller u otro) debe
   responder JSON pidiéndole el método abstracto a cada una de las
   dos hijas, tratándolas como el tipo padre — sin if/switch por tipo.

Restricciones:
- No abrir campos public ni agregar setters que rompan el
  ocultamiento.
- No usar JPA, @Entity, ni capas de service/repository — eso es
  del ejemplo viejo del template, no aplica acá.
- Diff mínimo: no reescribas clases mías que ya funcionan.
- Después de cada cambio, compilá con ./mvnw -q compile y probá con
  ./mvnw spring-boot:run antes de seguir.
- Hacé commits separados por parte (controllers, método abstracto,
  constructores/sobrecarga), con mensajes descriptivos.

Al final, explicame en tres frases qué quedó encapsulado y por qué
el padre no puede implementar el método abstracto él mismo.
```

---

## Qué verifiqué yo

- Compilé con `./mvnw -q compile`, corrí `./mvnw test` y levanté el servicio con `./mvnw spring-boot:run`.
- Revisé el código de `JugadorController` y comprobé que construye con el constructor completo, sin setters.
- Probé con `curl` los casos `GET /jugador`, `GET /jugador?id=` y `GET /jugador?id=%20`. Los dos primeros dan 200 con el valor por defecto (`Steve`) y el tercero da 400 con el mensaje del dominio.

---

## POO-08 — CRUD REST, H2, reglas de juego y excepciones

**Alcance:** persistir tres clases del dominio en H2, publicar el CRUD REST,
implementar dos reglas de negocio con una simulación persistida, agregar una
jerarquía de excepciones con handlers por tipo, usar la interfaz
`ComportamientoIA` en un endpoint y versionar la colección Insomnia.

**Asistentes y modelos:** usé OpenCode en la terminal con el modelo
`opencode/big-pickle`. La lectura del enunciado, la revisión de los resultados
y la decisión de qué aceptar quedaron de mi lado.

**Resumen de prompts:**
- Pedí un CRUD REST de `Entidad` en tres capas, con validaciones en el service
  y persistencia en H2 con Spring Data JPA.
- Pedí que la base H2 quedara en archivo, dentro de `data/`, y que se ignoraran
  los archivos generados.
- Pedí una regla de negocio de explosiones con `Creeper`, `Jugador` y `Bloque`,
  expuesta por REST y con validaciones.
- Pedí leer el enunciado de POO-08 y comparar lo implementado contra la rúbrica.
- Pedí continuar con la refactorización para que las clases reales del dominio
  fueran las entidades JPA, con `Entidad` como base y `Jugador`/`Creeper` como
  subtipos persistidos.
- Pedí mover las dos reglas de la explosión al dominio, persistir la simulación
  y agregar la jerarquía `DominioException` con dos hijas y handlers distintos.
- Pedí el endpoint de `ComportamientoIA` usando `Zombie` y `Aldeano`, la
  colección Insomnia y la actualización del README.

**Qué verifiqué en esta etapa:**
- `./mvnw -q compile` y `./mvnw test` terminan sin errores.
- El arranque con `./mvnw spring-boot:run` crea el esquema en
  `data/entidades.mv.db`.
- Con `curl` probé crear un jugador y un creeper, listar, consultar por id,
  actualizar, borrar, listar vivos y consultar el endpoint de la interfaz.
- La simulación devolvió daño 8 para un jugador a distancia 3 con radio 5,
  persistió la salud resultante y marcó como destruido el bloque dentro del
  radio. Un `GET` posterior mostró esos cambios.
- Los errores se distinguen: `POST` con salud mayor a la máxima devuelve `422`
  con `tipo: "regla_de_juego"`; un id inexistente devuelve `404` con
  `tipo: "entidad_no_encontrada"`; un JSON roto devuelve `400` con
  `tipo: "json_invalido"`.
- La colección quedó en `insomnia/POO-08-insomnia.json`; el entorno `Local`
  apunta a `http://localhost:8080`.
