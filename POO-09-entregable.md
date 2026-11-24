# POO-09 — Enunciado aplicado a esta entrega

**Commit de la solución:** https://github.com/SaDez7/sb-taller-git-2026/commit/0b244776d8d8c08d7457d59c40f5c09e63c3530a

## Quiénes son A1, A2 y A3

| Rol | Alumno | Servicio | URL base | OpenAPI | Regla que publica |
| --- | --- | --- | --- | --- | --- |
| A1 | Santiago Baez (`SaDez7`) | `sb-taller-git-2026` (este repo) | `http://25.29.140.0:8080` | `http://25.29.140.0:8080/v3/api-docs` | El daño de una explosión decae con la distancia hasta 20 puntos y todo bloque dentro del radio queda destruido. |
| A2 | fadul | API Minecraft de fadul | `http://25.30.124.10:8080` | `http://25.30.124.10:8080/v3/api-docs` | Una TNT detona una sola vez y aplica hasta 8 puntos de daño a entidades dentro de 4 bloques. |
| A3 | mateo | API Minecraft de mateo | `http://25.28.15.25:8080` | `http://25.28.15.25:8080/v3/api-docs` | Un creeper vivo explota una sola vez y daña hasta 10 puntos a objetivos a 3 bloques o menos. |

A1 es el servicio propio y el único que orquesta el escenario. A2 y A3 son los dos consumos ajenos; sus contratos se tomaron de los OpenAPI indicados, sin clonar su código ni copiar sus clases.

## Escenario en una frase

Una explosión cruzada es legal cuando una TNT de A2 puede detonar contra una entidad de A2 y un creeper vivo de A3 puede explotar contra otro objetivo, con A1 validando primero que el jugador local exista y esté vivo, guardando la jugada en H2 recién después de que ambos compañeros acepten.

## Cómo probarlo

1. Abrir los Swagger: A1 en `http://25.29.140.0:8080/swagger-ui.html`, A2 en `http://25.30.124.10:8080/swagger-ui/index.html` y A3 en `http://25.28.15.25:8080/swagger-ui.html`.
2. En A1 ejecutar **Salud → `GET /`** y **Escenario cruzado → `GET /escenarios/explosion-cruzada`** para verificar el servicio y anotar las filas previas.
3. En A2 ejecutar **Entidades → `GET /api/entidades`** y elegir un objetivo vivo. En A3 ejecutar **Entidades → `GET /entidades/creepers/listos`** y **Entidades → `GET /entidades`** para elegir un creeper vivo y un objetivo vivo.
4. En A2 ejecutar **Simulaciones → `POST /api/tnts`** con un id nuevo, por ejemplo:

```json
{"id":"t5","posicion":{"x":0,"y":0,"z":0}}
```

5. En A1 ejecutar **Escenario cruzado → `POST /escenarios/explosion-cruzada`** con ese mismo id de TNT:

```json
{
  "jugadorId": "steve",
  "tntId": "t5",
  "objetivoA2Id": "jugador1",
  "creeperA3Id": 5,
  "objetivoA3Id": 3,
  "distanciaA3": 2
}
```

La respuesta esperada es `201` con `danoA2`, `saludA2Despues`, `danoA3` y `vidaA3Despues`. Después se verifica `GET /escenarios/explosion-cruzada` y `GET /escenarios/explosion-cruzada/{id}`.

6. Para el rechazo de A2, repetir el mismo `POST` con la misma TNT: debe devolver `422` con `tipo: "validacion_companero"`, `api: "A2"` y detalle `La TNT ya fue detonada`.
7. Para el rechazo de A3, crear otra TNT en A2 y repetir el `POST` con el creeper ya explotado: debe devolver `422` con `api: "A3"` y detalle `El creeper ya explotó`.
8. Ejecutar `GET /escenarios/explosion-cruzada` otra vez: la lista no debe crecer después de los rechazos.

Si un compañero no responde, A1 devuelve `502` con `tipo: "companero_no_disponible"` y el campo `api`. Los ids usados pueden cambiar; antes de repetir hay que elegir otros con los `GET` del paso 3.

La colección `insomnia/sb-taller-git-2026.json` deja armados el caso feliz (`20`, `21`, `22`) y ambos rechazos (`23`, `24`, `25`, `26`), con variables para no repetir ids manualmente.
