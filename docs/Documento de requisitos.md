# Documento de requisitos y Análisis del sistema

![JavaScript](https://img.shields.io/badge/JavaScript-F7DF1E?style=for-the-badge&logo=javascript&logoColor=black)
![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![React](https://img.shields.io/badge/React-20232A?style=for-the-badge&logo=react&logoColor=61DAFB)

# Los Mapas del Reino 
**Repositorio:**

[![GitHub](https://img.shields.io/badge/GitHub-000?style=for-the-badge&logo=github&logoColor=white)](https://github.com/gii-is-DP1/DP1--2023-2024-l3-3/)

**Video:** 

**Miembros del equipo**:
- [Alberto Carmona Sicre](https://github.com/albcarsic)
- [Ramón Gavira Sánchez](https://github.com/rgavira123)
- [Marco Padilla Gómez](https://github.com/maarcoopg)
- [Rafael Pulido Cifuentes](https://github.com/rafpulcif)
- [Daniel Ruiz López](https://github.com/Danielruizlopezcc)

**Tutor**: Manuel Resinas Arias de Reyna

## Historial de versiones

| Fecha | Versión | Descripción |
| - | - | - |
| 10/02/2024 | 0.1 | Primera versión del documento |
| 12/02/2024 | 0.2 | Historias de usuario H1-H17. Creación del glosario de términos |
| 12/02/2024 | 0.2.1 | Revisión de la descripción del proyecto y extender el glosario de términos |
| 23/02/2024 | 0.2.2 | Revisión del idioma y añadida Historia de usuario H18 |
| 08/03/2024 | 0.2.3 | Añadir historia de usuario H19 | 

## Descrición del proyecto
Con este proyecto, cuyo propósito es principalmente educativo, se busca la implementación del juego *Los Mapas del Reino* en una aplicación web. El objetivo es que se pueda jugar a dicho juego de manera online, con la posibilidad de jugar en tiempo real con otros jugadores. De igual manera, también es una oportunidad para mejorar nuestras habilidades tecnológicas. Centrándonos en el uso de tecnologías como Java, JS y React.

Los Mapas del Reino es un juego de 1 a 4 jugadores, de estrategia *roll & write* en el que en cada partida habrá unos objetivos diferentes. Los jugadores deberán perseguir estos objetivos y conseguir el mapa que mas puntos les otorgue.   
La partida comienza tirando 4 dados para elegir los objetivos de la partida. Tras esto, los jugadores irán tirando dados y eligiendo que desean añadir al mapa del reino, siempre teniendo en cuenta los objetivos para maximizar sus puntos. Dependiendo del número de jugadores, habrá un número de elementos mínimos que se pueden elegir añadir al mapa. Sin embargo, no hay número máximo de rondas, por lo que la partida termina el mismo turno en el que alguno de los jugadores no pueda añadir más elementos al mapa.
En base a los objetivos de la partida y casillas que otorgarán **poderes**, se hará el recuento de puntos, tras la finalización de la partida, el jugador con más puntos será el ganador.

### Conceptos Generales

En los mapas del Reino encontramos **6** tipos de territorios para construir:
- Castillo
- Aldea
- Ciudad
- Bosque
- Río
- Montaña
---

A lo largo de la partida, los jugadores irán colocando los diferentes territorios en su *mapa* (cada jugador tiene su propio mapa) para poder maximizar los puntos conseguidos en base a los 4 **criterios de puntuación/objetivos** que se establecen al inicio de la partida. Estos se dividen en 2 sectores.
>Los 4 dados que se tiran al inicio de la partida determinan los 2 **criterios del sector A** (2 primeros dados) y los 2 **criterios del sector B** (2 últimos dados)
 El número resultante en la tirada determina qué criterio (numerados del 1 al 6) serán validos durante la partida.

**Criterios del sector A**
1. **2 PV** por cada **castillo** rodeado por **6 territorios cualquiera**
2. **3 PV** por cada **aldea** que conecte con **montaña y río** + **1 PV** extra si también conecta **bosque**
3. **2 PV** por cada **bosque** en tu **grupo más pequeño de bosques** (al menos **dos grupos**)
4. **2 PV** por cada **línea** en el *mapa* donde aparezcan **aldea y río**
5. **5 PV** por cada grupo de **ciudades** (una sola también se considera grupo)
6. **1 PV** por cada **montaña** en los **bordes del mapa**

**Criterios del sector B** 
1. **1 PV** por cada **montaña** en tu grupo más grande de **montañas** (al menos **dos grupos**)
2. **1 PV** por cada **aldea** que conecte al menos una **ciudad** + **3 PV** extra si también conecta con **dos castillos**
3. **10 PV** por conectar **dos caras opuestas del mapa** mediante **bosques**
4. **12 PV** por cada **castillo** que conecte con un territorio de cada tipo
5. **2 PV** por cada **río** que conecte al menos con **dos bosques**
6. **8 PV** por cada **ciudad** que conecte con **río,bosque y montaña** y ninguna otra **ciudad**
----
En el transcurso de la partida nos encontraremos en el *mapa* con casillas que nos otorgarán **poderes** en la partida.
- Casilla **+1/-1**: Una vez el jugador coloca un territorio en esta casilla decide sí se añade un **poder** que otorgará +1 ó -1 a cualquier dado.
- Casilla **?**: Cuando un jugador coloque un territorio en esta casilla, el jugador sumará a su puntuación final su puntuación en **ese momento** de cualquiera de los 4 criterios establecidos al inicio de la partida


### Fases del juego

1. El **Jugador activo** lanza los dados (Siempre se lanzan N+1 dados, siendo N el número de jugadores en la partida)
2. Tras esto el jugador **anuncia** que territorio va a jugar
3. El **jugador activo** escoge uno de los dados disponibles y lo aparta (este dado representa la cantidad de territorios que construye del tipo elegido)
4. Ahora los **jugadores pasivos** deben escoger un número de los dados sobrantes de la mesa (pueden elegir varios el mismo), y construyen dicha cantidad del territorio **anunciado** por el **jugador activo**

El turno pasa ahora al siguiente jugador, y tira los dados restantes, así hasta que sólamente quede **un dado en juego**. Para comenzar la siguiente ronda se toman nuevamente todos los dados (*N+1 dados*)

### Fin del juego

El juego finaliza cuando uno o más jugadores no pueden construir la cantidad de territorios requerida. En este momento el jugador no construye nada, se termina ese turno, es decir los jugadores que sí pueden construir si que construyen.

Se contabilizan los puntos otorgados y el que sume más puntos será el **GANADOR**. En caso de empate, se compartirá la victoria.



## Glosario de términos (en orden alfabético TODO)
| Término | Definición |
| - | - | 
| Jugador pasivo  | Jugador que, en el momento de tirar dados en una partida, no realiza dicha acción |
| Grupo de territorios | Consideraremos un grupo en el mapa cuando haya más de dos territorios iguales juntos (excepto en el criterio 5A, **ver más arriba**) |
| Mapa  | El mapa es equivalente al tablero, se usa de manera indistinta
| Jugador activo | Jugador que, en el momento de tirar los dados en una partida, es el que **anuncia** el territorio que se construye|
| Anunciar | El verbo anunciar es la decisión del **jugador activo** de construir uno de los territorios disponibles |
| Criterios/Objetivos | | Normas de puntuación que son válidas para toda la partida en la que nos encontramos, y que determinan la forma de puntuar|

## Tipos de Usuario/ Roles
Consideraremos como **usuarios del sistema** a los siguientes roles:
- **Jugador**
- **Administrador**

## Historias de usuario

### H1. Registro
Como usuario, quiero poder registrarme en el juego para tener una cuenta personal con sus respectivos datos y permisos.

### H2. Inicio de sesión
Como usuario, quiero poder iniciar sesión en el juego si ya me he registrado para tardar menos en entrar al juego y poder acceder a mis datos. 

### H3. Editar perfil
Como usuario, quiero poder editar los parámetros de mi perfil que no afecten a datos de las partidas anteriores para tener la opción de personalizar mi perfil a lo largo del tiempo.

### H4. Cierre de sesión
Como usuario, quiero poder cerrar mi sesión en el juego si ya he iniciado sesión anteriormente para que nadie más pueda acceder a mi sesión.

### H5. Crear partida
Como jugador, quiero poder crear una partida para así jugar.

### H6. Visualizar mis partidas
Como jugador quiero poder visualizar las partidas que he jugado anteriormente para poder llevar el recuento de cuantas partidas he jugado y con quien las he jugado.

### H7. Visualizar todas las partidas
Como adminsitrador quiero poder visualizar todas las partidas que se han creado por todos los jugadores para llevar un seguimiento de las partidas y el crecimiento del juego.

### H8. Visualizar jugadores registrados
Como administrador, quiero poder visualizar todos los jugadores que se han registrado para llevar un seguimiento del volumen de jugadores y sus datos.

### H9. Crear usuario
Como administrador, quiero poder crear usuarios para tener usuarios de prueba y por posibles necesidades futuras.

### H10. Actualizar usuarios
Como administrador, quiero poder actualizar usuarios para mantener la información de la cuenta actualizada y reflejar cambios relevantes en los roles, permisos o datos personales de los usuarios.

### H11. Eliminar usuarios
Como administrador, quiero poder eliminar usuarios para que pueda mantener la integridad y seguridad de la plataforma.

### H12. Consultar usuarios
Como administrador, quiero poder acceder a los usuarios para revisar y gestionar su información relevante, como nombres, correos electrónicos, roles de acceso. 

### H13. Colocar losetas
Como jugador, quiero poder colocar losetas para completar el mapa y con estas conseguir el mayor número de puntos posibles.

### H14. Tirar dados
Como jugador, quiero poder tirar los dados para tomar decisiones durante la partida

#### H14.1 Determinación de objetivos de partida
Como jugador quiero poder tirar los dados para determinar cuales van a ser los objetivos de la partida. 

#### H14.2 Determinar número de losetas
Como jugador quiero poder tirar los dados para determinar el número de losetas que voy a jugar en mi turno

### H15. Elegir dados
Como jugador, quiero poder elegir, una vez se han tirado los dados, el que más me convenga para poder construir losetas en el mapa

### H16. Elegir loseta
Como jugador quiero poder elegir, una vez tirados los dados, el tipo de loseta que quiero colocar en el mapa. 

### H17. Casillas especiales
Como jugador quiero poder hacer uso de casillas especiales para disfrutar de ventajas que puedan ayudar a ganar la partida.

### H18. Pantalla de inicio
Como usuario quiero poder visualizar una pantalla de inicio para poder acceder a las diferentes opciones y que me sirva de guía para saber que puedo hacer en el juego.
### H19. Unirse a paartida
Como jugador quiero poder unirme a las partidas creadas por otros jugadores para poder jugar con ellos.
