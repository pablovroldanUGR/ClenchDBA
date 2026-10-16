# Retos Grid en DBA Lab

**Borrador para revisión docente. No publicar todavía.** Antes de distribuir esta guía hay que validar los retos Grid con energía en el backend. La [guía de conexión con DBA Lab](guia-api-dba-lab.md) explica el ciclo común y muestra peticiones en cURL y Java.

Los retos con `environmentType: "GRID_2D"` se descubren mediante `GET /api/v1/challenges`. Cada uno anuncia sus `sensorCodes` y `actionCodes`. El agente utiliza las capacidades disponibles en el `run` que ha iniciado.

## Percepciones

Los sensores se consultan individualmente con `POST /api/v1/runs/{runId}/perceptions/{sensorId}`. La respuesta contiene `sensorId`, `value` y `control`.

| `sensorId` | Campos de `value` |
| --- | --- |
| `grid.position` | `x` e `y`: posición actual. `x` es la columna desde la izquierda e `y` la fila desde arriba. |
| `grid.bumpers` | `north`, `east`, `south` y `west`: `true` si la celda adyacente está bloqueada por un obstáculo o por el límite del mundo. |
| `grid.target` | `x` e `y`: posición del objetivo. |
| `grid.energy` | En un reto con energía finita, `mode` vale `FINITE` y `remaining` indica la energía disponible. |

Las lecturas no mueven al agente. El laboratorio ofrece estas percepciones, no una descarga del terreno completo. Las imágenes de los primeros mapas en el guión ayudan a reconocer el mundo, mientras que el agente interactúa mediante los sensores de su `run`.

## Movimientos y resultado

El agente solicita un movimiento mediante `POST /api/v1/runs/{runId}/actions` con uno de los códigos anunciados: `grid.move.north`, `grid.move.east`, `grid.move.south` o `grid.move.west`. Por ejemplo, `{"actionCode":"grid.move.east"}` solicita avanzar una celda a la derecha.

La respuesta incluye `actionStatus`, `reasonCode` y `control`. `APPLIED` indica que el movimiento se ha procesado; comprueba también `control.runStatus`, porque puede haber alcanzado el objetivo, agotado la energía o provocado una colisión. Una colisión con obstáculo o límite termina el `run`. La respuesta de acción no incluye la posición ni el saldo de energía nuevos, por lo que una lectura posterior puede ser necesaria si la ejecución sigue activa.

Los retos Grid de esta práctica se han configurado con energía finita. Su presupuesto concreto pertenece a la configuración de cada reto. Alcanzar el objetivo antes de agotarlo forma parte de resolver el reto; la longitud del recorrido no tiene una puntuación independiente.