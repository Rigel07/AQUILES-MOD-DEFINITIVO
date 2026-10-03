# Achilles — Forge 1.20.1

Mod de supervivencia/protección inspirado en el guerrero Aquiles.

## Funciones

- Estatuas inmortales de Aquiles generadas de forma determinista por el Overworld.
- Separación amplia entre estatuas para que incluso con radio máximo no se solapen.
- Interactúa con una estatua sin dueño para reclamarla.
- Cada estatua descubierta da 1 punto personal al jugador.
- Cada estatua puede tener radio 0–100 y curación 0–100.
- Radio base: 12 bloques. Radio máximo: 90 bloques.
- Curación base: 0,5 corazones-equivalentes por segundo; aumenta con el nivel.
- El propietario y jugadores autorizados quedan protegidos del daño dentro del radio.
- Los monstruos hostiles son expulsados del radio y pierden su objetivo de jugadores protegidos.
- La construcción y rotura de bloques queda protegida dentro de la zona para jugadores no autorizados.
- Comandos: `/achilles points`, `/achilles info`, `/achilles upgrade radius`, `/achilles upgrade healing`, `/achilles trust <jugador>`, `/achilles untrust <jugador>`.
- Mob Aquiles/Aquilles chibi, con modelo, textura y sonidos propios.

## Compilar en GitHub

Sube el contenido del proyecto a un repositorio y el workflow `.github/workflows/build.yml` compilará el JAR con Java 17 y Gradle 8.8.

## Limitación de compatibilidad

Está hecho para Forge 1.20.1 / Forge 47.4.10. No es compatible directamente con mods Fabric o NeoForge.
