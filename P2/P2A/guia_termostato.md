# Retos de termostato en DBA Lab

**Borrador para revisión docente. No publicar todavía.** Esta guía debe leerse junto con la [guía de conexión con DBA Lab](guia-api-dba-lab.md), que explica el ciclo común, las peticiones HTTP y ejemplos en cURL y Java.

Los retos con `environmentType: "THERMOSTAT"` se descubren mediante `GET /api/v1/challenges`. La respuesta indica las capacidades concretas disponibles. Una vez iniciado un `run`, el agente consulta los sensores que necesite, solicita una acción y comprueba el nuevo estado de control.

## Percepciones

Cada lectura se solicita por separado mediante `POST /api/v1/runs/{runId}/perceptions/{sensorId}`. La respuesta contiene `sensorId`, `value` y `control`. Los valores siguientes describen la forma de `value`:

| `sensorId` | Campos de `value` |
| --- | --- |
| `thermostat.temperature` | `celsius`: temperatura actual. |
| `thermostat.limits` | `lower` y `upper`: límites permitidos, ambos incluidos. |
| `thermostat.target` | `celsius`: temperatura orientativa. |

Leer sensores no avanza la simulación. La lectura de temperatura es la que comunica el estado físico oficial; las respuestas de acción y de control no contienen una temperatura nueva.

## Acciones y resultado

Las capacidades de acción del termostato son `thermostat.refrigeration.on` y `thermostat.refrigeration.off`, si figuran en `actionCodes` del reto. Cada acción aplicada fija el estado de la refrigeración y avanza un paso, durante el que puede intervenir una perturbación externa. El termostato no usa energía.

El objetivo es mantener la temperatura dentro de los límites durante los pasos que exige el reto. La temperatura orientativa ayuda a decidir, pero no es por sí misma la condición de éxito. No hace falta conocer de antemano cuántos pasos faltan: el agente continúa mientras `control.runStatus` sea `ACTIVE` y se detiene ante el resultado terminal que indique DBA Lab. `control.remainingObjectives` no cuenta los pasos del termostato.

Una acción puede quedar `APPLIED` y cerrar el `run` por temperatura insegura; consulta también `control.runStatus` y `control.reasonCode`. Si el `run` continúa y necesitas conocer la nueva temperatura, vuelve a solicitar `thermostat.temperature`.