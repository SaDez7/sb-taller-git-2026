# Entrega POO-08 — CRUD REST, H2, reglas de juego y excepciones

**Alumno:** Santiago Baez (GitHub: `SaDez7`)
**Asignatura:** Lenguaje de Programación 3 (CYT646)
**Dominio:** Minecraft
**Repositorio:** https://github.com/SaDez7/sb-taller-git-2026
**Commit de la solución:** https://github.com/SaDez7/sb-taller-git-2026/commit/a0f327ebb4b45e717f4aaea2fcf2b7ad2b0a05a9
**Rango completo de POO-08:** https://github.com/SaDez7/sb-taller-git-2026/compare/3b68d11f1bd6717791efd8686578479c4218d45b...a0f327ebb4b45e717f4aaea2fcf2b7ad2b0a05a9

## Objetivo

Implementar un servicio Spring Boot con Java 21 y Maven que exponga un CRUD REST
sobre entidades del dominio Minecraft, persista los datos en H2 mediante Spring
Data JPA, ejecute una simulación de explosiones con dos reglas de negocio y
traduzca las excepciones del dominio a respuestas HTTP diferenciadas.

El proyecto incluye tres clases persistidas (`Jugador`, `Creeper` y `Bloque`),
una interfaz de dominio (`ComportamientoIA`) implementada por clases de familias
distintas, un repositorio Spring Data para la familia principal, una colección
de Insomnia versionada y una bitácora de uso de IA.

## Arranque

```bash
./mvnw spring-boot:run
```

El servicio queda disponible en `http://localhost:8080`.

Para compilar y ejecutar las pruebas:

```bash
./mvnw -q compile
./mvnw test
```

La base H2 se guarda en `data/entidades.mv.db` y está ignorada por Git. La
consola web está en `http://localhost:8080/h2-console` con:

| Dato | Valor |
| --- | --- |
| JDBC URL | `jdbc:h2:file:./data/entidades` |
| Usuario | `sa` |
| Password | vacío |

## URLs de la API

| Método | URL | Uso | Éxito |
| --- | --- | --- | --- |
| `GET` | `/` | Confirmar que el servicio está vivo. | `200` |
| `GET` | `/entidades` | Listar las entidades persistidas. | `200` |
| `GET` | `/entidades/vivos` | Listar entidades con salud mayor a 0. | `200` |
| `GET` | `/entidades/{id}` | Obtener una entidad por su id persistido. | `200` o `404` |
| `POST` | `/entidades` | Crear un jugador o un creeper. | `201` |
| `PUT` | `/entidades/{id}` | Reemplazar una entidad existente. | `200` |
| `DELETE` | `/entidades/{id}` | Eliminar una entidad. | `204` |
| `POST` | `/simulacion/explosiones` | Ejecutar y persistir una explosión. | `200` |
| `GET` | `/bloques` | Listar los bloques persistidos. | `200` |
| `GET` | `/mobs/comportamiento` | Consultar los objetos a través de `ComportamientoIA`. | `200` |

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

## Invariantes

- El `id` es la clave persistida. El JSON puede enviarlo o omitirlo; si se
  omite, el servicio genera un `UUID`. Las URLs usan ese id persistido, no un
  índice de una lista del controller.
- La salud siempre cumple `0 <= salud <= saludMaxima`. Los constructores de
  negocio y el service rechazan valores fuera de rango.
- El subtipo se discrimina explícitamente con el campo `tipo`
  (`jugador` o `creeper`). El DTO de entrada no permite construir entidades
  incompletas ni saltear las validaciones del dominio.
- JPA utiliza un constructor sin argumentos `protected`; el resto del código
  solo puede crear objetos mediante los constructores públicos de negocio.
- Regla 1: el daño de la explosión decae con la distancia. En
  `Creeper.danioDeExplosion(distancia, radio)` la fórmula es
  `max(1, round(20 * (1 - distancia / radio)))` dentro del radio; fuera del
  radio el daño es 0.
- Regla 2: todo bloque alcanzado por el radio queda destruido. En
  `Bloque.destruidoPorExplosion(distancia, radio)` el bloque se marca como
  `destruido` cuando la distancia es menor o igual al radio.
- La simulación persiste el creeper, el jugador con su salud actualizada y los
  bloques con su estado. Un `GET` posterior muestra el estado guardado.
- Las reglas de juego producen `422`; un id inexistente produce `404`; un JSON
  malformado o un argumento inválido producen `400`. El cuerpo de error incluye
  `tipo` y `mensaje` para distinguir las excepciones hermanas.

## Cómo probar

1. Abrir el commit indicado arriba o clonar el repositorio y ubicarse en `main`.
2. Ejecutar `./mvnw spring-boot:run` y esperar a que el servicio escuche en el
   puerto `8080`.
3. Importar en Insomnia el archivo `insomnia/sb-taller-git-2026.json`.
4. Verificar que el entorno `Local` tenga `base_url = http://localhost:8080`.
5. Ejecutar los pedidos en este orden:
   - `00` a `05`: servicio vivo, creación de jugador y creeper, listados y
     consulta por id.
   - `06` a `08`: actualización, creación y eliminación de una entidad temporal.
   - `09` a `11`: simulación de la explosión y consulta del jugador y los
     bloques persistidos.
   - `12`: endpoint que usa la interfaz `ComportamientoIA`.
   - `13` a `15`: errores esperados `422`, `404` y `400`.
6. Resultados esperados: `201` al crear, `200` al leer o actualizar, `204` al
   eliminar, `422` al violar una regla, `404` para un id inexistente y `400`
   para un JSON inválido.
7. Opcional: abrir `http://localhost:8080/h2-console`, conectarse con los datos
   indicados y verificar las tablas `entidades` y `bloques`.

En la simulación de ejemplo, `steve` está a distancia 3 de un radio 5, recibe 8
de daño y queda con 12 de salud; el bloque a distancia 2 queda destruido.

## Entregables incluidos

- `README.md`: arranque, propiedades de H2, modelo, API, reglas, excepciones,
  diagrama Mermaid y respuestas a las preguntas de «Qué hay que pensar».
- `BITACORA.md`: asistentes, modelos de LLM y resumen de prompts.
- `insomnia/sb-taller-git-2026.json`: colección con entorno y pedidos de CRUD,
  simulación, interfaz y errores.
- `ENTREGA-CLASSROOM.md`: este documento.
