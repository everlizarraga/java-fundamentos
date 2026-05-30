# 🧭 Apunte-maestro: Fundamentos de estructura Java · Maven · IntelliJ

> **Material extra** (no oficial, transversal — no es de una clase puntual de DSI).
>
> **Para quién es:** para alguien que recién entra al mundo Java y mira un proyecto Maven/Spring sintiendo que todo es ajeno (`pom.xml`, `groupId`, packages, módulos, iconos raros). Este apunte es el mapa que ojalá hubiera tenido antes de empezar: al terminarlo vas a saber **qué estás construyendo, dónde lo construís y cuál es tu alcance**, sin las idas y vueltas.
>
> **Cómo nació:** de explorar un proyecto real (un TP multi-módulo de microservicios) y un repo de cátedra hecho con Spring, preguntándole "¿por qué?" a cada cosa.

---

## 1. Cómo leer este apunte (la leyenda)

A lo largo del texto vas a ver estas marcas. No miden "importancia para un examen": te dicen **dónde pararte** según en qué etapa estás.

- 🟢 **Lo usás hoy.** Esto lo necesitás sí o sí para tu primer proyecto.
- 🟡 **Bueno entenderlo.** Te saca la sensación de magia; te hace entender el *por qué*.
- 🔵 **Para más adelante.** Existe, es real, pero no lo tocás todavía. Saber que está te da el mapa completo sin obligarte a usarlo.

> **La idea central que ordena todo lo demás:** lo que parece un montón de conceptos sueltos (módulo, artefacto, package, sources root, carpeta) en realidad son **capas distintas** que conviven. La mayoría de la confusión viene de mirar dos capas y creer que son la misma. Tené esta frase a mano todo el apunte.

---

## 2. El mapa mental: todo son capas 🟢

Antes de cualquier detalle, grabate este dibujo. Cada caja vive **dentro** de la anterior; cada nivel hacia adentro es una capa distinta:

```
┌──────────────────────────────────────────────────────────────────┐
│ MÓDULO  ·  1 pom.xml  →  produce 1 ARTEFACTO (.jar)                │
│ identidad: groupId : artifactId : version   (capa de BUILD)        │
│ ┌──────────────────────────────────────────────────────────────┐ │
│ │ SOURCES ROOT  ·  src/main/java                                 │ │
│ │ (acá EMPIEZA el código; IntelliJ marca esta carpeta)           │ │
│ │ ┌──────────────────────────────────────────────────────────┐ │ │
│ │ │ PACKAGE  ·  com.miproyecto.fundamentos                     │ │ │
│ │ │ (una carpeta, pero con SIGNIFICADO para Java)              │ │ │
│ │ │    ┌─────────┐   ┌───────────┐   ┌──────────┐             │ │ │
│ │ │    │ Persona │   │ Saludador │   │   Main   │   ← clases  │ │ │
│ │ │    └─────────┘   └───────────┘   └──────────┘             │ │ │
│ │ └──────────────────────────────────────────────────────────┘ │ │
│ └──────────────────────────────────────────────────────────────┘ │
└──────────────────────────────────────────────────────────────────┘
   El artifactId nombra el MÓDULO · el package UBICA la clase
```

Cada concepto del apunte cae en una de estas capas. Cuando algo te confunda, preguntate: *"¿esto es capa de build (Maven) o capa de código (Java)?"*.

---

## 3. Maven en 2 minutos 🟢🟡

**Maven** es un gestor de *build* + dependencias para Java. Reemplaza el flujo manual de "bajá los `.jar` a mano y compilá con `javac` armando el classpath". Vos *declarás* qué necesitás y Maven lo resuelve.

**`pom.xml`** (Project Object Model) es el **manifiesto** del proyecto: declarás dependencias, versión de Java y plugins, y Maven hace el resto.

### Las 3 coordenadas que identifican TODO 🟡

Todo proyecto/librería del universo Maven se identifica con tres datos:

```
groupId : artifactId : version
```

- **`groupId`** → la familia / el namespace. Estilo dominio al revés. Se comparte entre proyectos relacionados.
- **`artifactId`** → el nombre de ESTE proyecto puntual dentro de esa familia.
- **`version`** → la versión (ej. `1.0-SNAPSHOT`; el sufijo `-SNAPSHOT` = "en desarrollo").

> **Puente si venís de JavaScript:** es lo mismo que un paquete npm.
> ```
> Maven:   groupId            : artifactId        : version
> npm:     @scope             / nombre-paquete    @ version
> ej.:     @miorg/mi-libreria @ 1.0.0
> ```
> El `groupId` es como el *scope* `@miorg`, el `artifactId` como el nombre del paquete, y la version es la version.

### El lifecycle (qué pasa al correr un comando) 🟢

```
mvn compile   → compila src/main/java
mvn test      → compila + corre los tests de src/test/java
mvn package   → todo lo anterior + arma el .jar en target/
mvn clean     → borra target/ (empezás de cero)
```

Cada comando corre **contra UN solo `pom.xml`**: el de la carpeta donde estás parado.

---

## 4. Los 3 campos al crear el proyecto: Name vs groupId vs artifactId 🟢

Cuando creás un proyecto Maven (en IntelliJ: *New Project → Maven*), te pide tres cosas que parecen lo mismo y NO lo son. De los tres, **solo dos son de Maven**; el otro es IntelliJ siendo amable.

| Campo | ¿Qué es? | ¿Maven lo usa? |
|---|---|---|
| **`Name`** | El nombre de la **carpeta** en disco (y la etiqueta en el IDE). Cosmético. | ❌ No. Es un campo solo de IntelliJ. |
| **`GroupId`** | La **familia / namespace** del artefacto. | ✅ Sí |
| **`ArtifactId`** | El **nombre puntual** de este artefacto. | ✅ Sí |

**Por qué hay un `Name` Y un `ArtifactId` que parecen iguales:** porque podés (y conviene) hacerlos distintos. En la **carpeta** querés un número para ordenar (`01-colecciones-streams`), pero la **identidad Maven** la querés limpia (`colecciones-streams`). IntelliJ rellena el `ArtifactId` igual al `Name` por defecto, pero vos lo editás.

Ejemplo concreto (tres ejercicios de práctica en un repo `java-fundamentos`):

| `Name` (carpeta) | `GroupId` | `ArtifactId` |
|---|---|---|
| `01-colecciones-streams` | `com.miproyecto.fundamentos` | `colecciones-streams` |
| `02-optional` | `com.miproyecto.fundamentos` | `optional` |
| `03-optional-aplicado` | `com.miproyecto.fundamentos` | `optional-aplicado` |

Fijate: el `groupId` es **el mismo** (misma familia/repo); lo que cambia es el `artifactId`.

> 🟢 **Tips del wizard:** dejá **destildado** "Add sample code" (no querés un `Main` de relleno) y **destildado** "Create Git repository" (el repo git es uno solo en la raíz del repo temático, no uno por proyecto).

---

## 5. ⭐ El corazón del apunte: groupId vs package 🟡

Esta es **la** confusión que separa entender Maven de copiarlo. La mayoría mira las carpetas del código, ve algo como `ar.edu.utn.frba.ddsi.incentivos`, y cree que ESO es el groupId. No lo es.

Son **dos capas distintas con dos propósitos distintos**:

```
                  ┌─ lo que ve MAVEN (en el pom.xml)
groupId           ar.edu.utn.frba.ddsi
artifactId        incentivos-service
                  ↳ identifican el .JAR · capa de BUILD / dependencias

                  ┌─ lo que ve JAVA (en las carpetas / la línea `package`)
package           ar.edu.utn.frba.ddsi.incentivos
                                       └──────────┘
                  ↳ el tramo extra (.incentivos) namespacia las CLASES · capa de CÓDIGO
```

- **`groupId : artifactId`** vive en el `pom.xml`. Es cómo **Maven** identifica y encuentra el `.jar`. Importa en tiempo de *build*.
- **`package`** vive arriba de cada clase (`package ...;`) y se refleja como carpetas. Es cómo **Java** organiza las clases adentro. Importa en tiempo de *compilación y ejecución*.

### La pregunta filosa: si el artifactId ya separa los módulos, ¿para qué ADEMÁS el package?

Porque actúan en momentos distintos y **no se cubren mutuamente**:

- `groupId:artifactId` separa los **`.jar`** (qué jar bajar/enlazar).
- Pero una vez que el `.jar` de `common-lib` está en el *classpath* de `notificaciones`, el compilador y la JVM **ya no ven fronteras de artifactId**. Ven un único saco donde cada clase se identifica por su **nombre completo = package + nombre de clase**. El `artifactId` NO es parte de la identidad de una clase en ejecución.

Conclusión: si dos módulos tuvieran `Persona` en el **mismo** package, chocarían en el classpath **aunque sean artefactos distintos**. Lo único que los desambigua ahí es el package. Por eso:

> **El artifactId separa los JARs. El package separa las CLASES. En el momento que conviven, solo manda el package.**

### ¿Por qué el package base se parece al groupId?

Pura **convención** de unicidad (la misma idea del dominio invertido, pero aplicada a las clases). No es obligatorio: Java aceptaría groupId `com.acme` con package `loquesea.pepito`. Nadie lo hace porque sería desprolijo.

> 🟢 **Para tus prácticas (un solo módulo):** no hay otro `Persona` con quien chocar, así que `package = groupId` y listo. El tramo extra solo tiene sentido cuando conviven varios módulos.

---

## 6. El dominio invertido y tu namespace propio 🟡

La convención del `groupId` es **"dominio invertido"**: para que sea único en todo el mundo Maven, usás **un dominio que controlás, escrito al revés**. Esto NO es preferencia ni algo hardcodeado: es una regla para garantizar unicidad global el día que un artefacto se publique.

```
Dominio que controlás        →   groupId (al revés)
────────────────────────────────────────────────────
miproyecto.com               →   com.miproyecto
lizarraga.com.ar             →   ar.com.lizarraga
acme.org                     →   org.acme
utn.edu.ar  (una facultad)   →   ar.edu.utn   (+ .frba.ddsi por regional/materia)
```

¿No tenés un dominio propio pagado? Hay una alternativa estándar y legítima: si tenés GitHub, podés usar tu GitHub Pages como "dominio":

```
tuusuario.github.io          →   io.github.tuusuario
```

> El punto en todos los casos es el mismo: **es un dominio que vos controlás, dado vuelta.** Por eso un profe usa `ar.edu.utn.frba.ddsi` (sale del dominio real de la facultad) — para el TP es correcto, porque ese artefacto ES de la facultad. Para **tu** portfolio personal, usá un namespace tuyo, no el de la facu.

> 🔵 **¿Problemas si en práctica usás un groupId "inventado" tipo `com.miproyecto`?** Ninguno funcional: nada se valida en tu máquina, y un proyecto autocontenido que nunca se publica corre igual con cualquier groupId. La convención larga importa recién cuando publicás.

---

## 7. Anatomía de un proyecto: layout, sources roots, main vs test 🟢

Todo proyecto Maven tiene la **misma estructura estándar**. Maven es "convención sobre configuración": si ponés cada archivo donde lo espera, no le configurás nada (por eso el `pom` no dice "el código está en tal carpeta": lo asume).

```
01-colecciones-streams/              ← el proyecto (un módulo)
├── pom.xml                          ← el manifiesto
├── .gitignore
└── src/
    ├── main/
    │   ├── java/                    ← CÓDIGO productivo  (sources root)
    │   │   └── com/miproyecto/fundamentos/
    │   │       └── Main.java        → package com.miproyecto.fundamentos;
    │   └── resources/               ← archivos no-código que tu programa necesita
    └── test/
        └── java/                    ← TESTS  (test sources root)
            └── com/miproyecto/fundamentos/
                └── MainTest.java    → package com.miproyecto.fundamentos;  (idéntico al de main)
```

### main vs test 🟡

- **`src/main/java`** y **`src/test/java`** son **dos sources roots separados**. El código productivo y los tests viven aparte.
- **`src/test/` es OPCIONAL.** Un proyecto sin un solo test compila igual. (Por eso es normal ver un módulo con `main` pero sin `test`: simplemente todavía no le escribieron pruebas.)
- Si creás tests, **espejás la estructura del main**: misma ruta de carpetas, **mismo `package`**.

¿Por qué se espeja el package entre main y test? Para que el test pueda usar la clase **sin escribir `import`** (están en el mismo espacio de nombres) y pueda ver los miembros *package-private*:

```
src/main/java/com/miproyecto/fundamentos/Persona.java       → package com.miproyecto.fundamentos;
src/test/java/com/miproyecto/fundamentos/PersonaTest.java   → package com.miproyecto.fundamentos;  (igual)
```

> 🟢 En IntelliJ casi nunca lo hacés a mano: parado sobre la clase, `Ctrl+Shift+T` → *Create New Test* y te crea el test en el lugar correcto, con el package espejado.

### ¿Quién crea `main` y `test`?

Las crea **el IDE como cortesía** al generar el proyecto, no Maven. Si destildaste "Add sample code", `src/main/java` te queda **vacío** (sin package): es lo esperado. Lo armás así:

```
click derecho en  src/main/java  →  New → Package  →  escribís: com.miproyecto.fundamentos
luego:  click derecho en el package  →  New → Java Class  →  Main
```

---

## 8. Módulos: qué son y las 3 formas de organizar código 🔵

Un **módulo** es una *unidad-proyecto* dentro de un build multi-módulo: una carpeta con su propio `pom.xml`. Cada módulo produce **su propio artefacto** (su `.jar`).

> **¿Módulo = artefacto?** Casi. El **módulo** es la unidad-proyecto; el **artefacto** es lo que produce al compilar. Relación 1 a 1. Por eso cada módulo tiene su `artifactId`: es el nombre del artefacto que escupe.

### La clave que destraba todo: reutilizar código NO requiere módulos

Hay **tres** maneras de organizar y compartir código, y elegir está bien — no hay una "correcta":

| Forma | Qué es | Cuándo |
|---|---|---|
| **Proyectos independientes (monorepo de islas)** | Un repo, varias carpetas; cada una un proyecto autónomo que NO depende de los otros. | Prácticas, katas, ejercicios sueltos. **Es tu caso hoy.** |
| **Multi-módulo** | Varias piezas en UN solo build; se referencian localmente sin publicar; comparten config. | Piezas muy acopladas, co-desarrolladas por un mismo equipo. **Es tu TP.** |
| **Artefacto publicado** | Un proyecto publica su `.jar` en un repositorio; otro lo consume por `groupId:artifactId:version`. | Reusar librerías. **Es lo que hacés con JUnit, AssertJ, Spring.** |

> Cuando ponés JUnit en tu `pom`, estás usando la 3ª forma: JUnit es un proyecto separado, de otra gente, publicado, y lo enchufás por sus coordenadas. **Reusar código de otro proyecto es normal y no necesita módulos.** Los módulos son solo la forma de tener piezas *juntas y co-desarrolladas*.

### ¿Submódulos dentro de un módulo?

Técnicamente sí (un módulo puede ser "padre" de otros; por eso IntelliJ te ofrece *New → Module*). En la práctica casi nadie anida más de un nivel. 🔵 Ignoralo por ahora.

### Monolito vs microservicios (el panorama real)

- **Monolito modular:** una sola app, se despliega entera, dividida en módulos para ordenar. Corre como un proceso.
- **Microservicios:** muchos servicios, cada uno su proyecto/despliegue, en procesos o máquinas separadas, hablándose por red (HTTP/REST, mensajería).

Tu TP es de estilo microservicios: cada servicio está como módulo *ahora* (para vivir en un repo y revisarlo junto), pero está diseñado para correr separado después.

---

## 9. Cómo "levantar una sola pieza" 🟡

Una duda típica: *"en el TP levantamos un solo servicio sin tocar los otros, ¿cómo?"*.

Porque cada módulo produce su propio artefacto con su propio `main` (cada servicio Spring tiene su `@SpringBootApplication`). El build puede construirlos a todos, pero vos apuntás a uno:

```bash
# Multi-módulo (TP): correr SOLO un módulo
mvn spring-boot:run -pl notificaciones-service      # o correr su main desde IntelliJ

# Monorepo de islas (tus prácticas): aún más simple, no hay reactor
cd 02-optional && mvn test                          # o el ▶ del main en IntelliJ
```

| | Multi-módulo (TP) | Monorepo de islas (prácticas) |
|---|---|---|
| ¿Hay un "compilar todo" desde la raíz? | Sí (el *reactor* del pom padre) | No (no hay pom padre) |
| Correr una pieza | `-pl <modulo>` o su main | `cd <carpeta>` o su main |

---

## 10. Leyendo IntelliJ: los iconos 🟢

No son "tipos de contenedor al azar": IntelliJ te marca el **rol** de cada carpeta con un badge.

| Lo que ves | Qué es |
|---|---|
| Carpeta común (gris) | Un directorio sin rol especial (`.idea`, `.mvn`). |
| Carpeta con una marca al pie | **Sources Root** = `src/main/java`, "acá va el código". (También hay *resources root* y *test sources root*.) |
| Carpeta con icono de paquete | Un **package**: carpeta bajo el sources root que Java trata como espacio de nombres. |
| Nodo raíz con `[nombre]` al lado | El **módulo** (y el `[nombre]` es su `artifactId`). |

> 🔵 **Detectar un proyecto hecho con Spring Initializr** (`start.spring.io`, "la página web de Spring"): trae `mvnw` y `mvnw.cmd` (el *Maven wrapper*), `HELP.md`, `application.yml`, un `<parent>spring-boot-starter-parent` en el pom y bloques vacíos `<licenses>/<developers>/<scm>`. Un proyecto Maven "pelado" hecho desde IntelliJ sale más minimalista.

---

## 11. Las 3 estructuras, lado a lado (autocontenido) 🟡

Para que no necesites abrir ningún repo, acá están reproducidas las tres formas que vas a cruzarte.

### (A) Tu proyecto de práctica — un solo módulo

```
01-colecciones-streams/
├── pom.xml                      ← groupId:artifactId:version, Java 21, JUnit/AssertJ
├── .gitignore
└── src/
    ├── main/java/com/miproyecto/fundamentos/Main.java
    └── test/java/com/miproyecto/fundamentos/MainTest.java
```

### (B) El TP — proyecto multi-módulo (microservicios)

```
ddsi-tp-template/                ← pom RAÍZ (packaging=pom): coordina, no tiene código
├── pom.xml                      ← groupId ar.edu.utn.frba.ddsi · centraliza versiones · <modules>
├── common-lib/                  ← módulo · artifactId common-lib · CÓDIGO COMPARTIDO
│   ├── pom.xml                  ← <parent> apunta a la raíz
│   └── src/main/java/ar/edu/utn/frba/ddsi/common/
│       ├── Persona.java
│       └── Saludador.java
├── incentivos-service/          ← módulo · artifactId incentivos-service · su propio main
│   └── src/main/java/ar/edu/utn/frba/ddsi/incentivos/IncentivosServiceApplication.java
├── notificaciones-service/      ← módulo · artifactId notificaciones-service · su propio main
└── donaciones-service/          ← módulo · artifactId donaciones-service · su propio main
```

Coordenadas Maven (lo que ve Maven) vs packages (lo que ve Java):

```
ar.edu.utn.frba.ddsi : common-lib              package ar.edu.utn.frba.ddsi.common
ar.edu.utn.frba.ddsi : incentivos-service      package ar.edu.utn.frba.ddsi.incentivos
ar.edu.utn.frba.ddsi : notificaciones-service  package ar.edu.utn.frba.ddsi.notificaciones
└──── mismo groupId ────┘ └ artifactId distinto ┘        └ package con tramo extra por módulo ┘
```

### (C) Repo de cátedra — un módulo, generado por Spring Initializr

```
rest-paises/                     ← un solo módulo
├── pom.xml                      ← <parent> spring-boot-starter-parent
├── mvnw, mvnw.cmd               ← Maven wrapper (marca de Initializr)
├── HELP.md
└── src/
    ├── main/
    │   ├── java/ar/edu/utn/ba/ddsi/countries/   ← package = groupId + .countries
    │   │   ├── CountriesApplication.java
    │   │   ├── config/
    │   │   └── services/
    │   └── resources/application.yml
    └── test/
```

Coordenadas: `ar.edu.utn.ba.ddsi : countries`, package `ar.edu.utn.ba.ddsi.countries`. (Aunque es un solo módulo, le sumaron `.countries` al package por costumbre; no es obligatorio.)

---

## 12. Git esencial + `.gitignore` 🟢

Lo mínimo que va sí o sí en un proyecto Java/IntelliJ versionado: un `.gitignore`, el `pom.xml`, `src/main/java`, `src/test/java` y (recomendado) un `README.md`.

### El `.gitignore`

```gitignore
# Build de Maven: se regenera, NUNCA se sube.
target/

# Config del IDE IntelliJ (tuya, no del proyecto).
.idea/
*.iml

# Basura del sistema.
.DS_Store
```

### Cómo se propaga (importante) 🟡

Un patrón en el `.gitignore` de la raíz aplica a **todo el árbol por debajo, a cualquier profundidad**, salvo que el patrón tenga una restricción de ubicación. La clave es si lleva o no una barra `/` **al inicio o en el medio**:

```
target/            → SIN barra interna: matchea "target" en CUALQUIER nivel
                      (raíz, 01-streams/target/, 02-x/target/, etc.)  ✅ lo que querés
*.iml  .DS_Store   → igual: matchean en cualquier subcarpeta
/target/           → barra al INICIO: solo la target/ de la raíz
01-streams/target/ → barra en el MEDIO: solo esa, anclada a esa ruta
```

Por eso **un solo `.gitignore` en la raíz** cubre todos los proyectos del repo.

> ⚠️ El `.gitignore` solo afecta archivos que Git **todavía no trackea**. Si por error hiciste `git add` de `target/` antes de tener el ignore, sacalo del seguimiento con:
> ```bash
> git rm -r --cached target/
> ```
> y commiteá. Si armás el `.gitignore` antes del primer `add`, nunca llegás a ese problema.

---

## 13. Empaquetar un proyecto para compartir o debuggear 🟡

Cuando algo falle y quieras pasarle UN proyecto a alguien (o a un asistente), no mandes el repo entero: mandá solo la carpeta del proyecto, sin `target/` ni `.idea/`. Dos formas:

```bash
# Forma 1 — zip (sirve SIEMPRE, incluso con cambios sin commitear)
zip -r 02-optional.zip 02-optional -x "*/target/*" "*/.idea/*" "*.iml"
#   -r                recursivo
#   02-optional.zip   archivo a crear
#   02-optional       carpeta a empaquetar
#   -x "..."          patrones a EXCLUIR ( */target/* matchea a cualquier profundidad )

# Forma 2 — git archive (más limpio; excluye solo lo gitignoreado, pero toma lo COMMITEADO)
git archive --format=zip --prefix=02-optional/ --output=02-optional.zip HEAD:02-optional
#   --prefix=02-optional/   hace que dentro del zip todo cuelgue de esa carpeta (se autodescribe)
#   HEAD:02-optional        la versión commiteada de esa carpeta
```

> ⚠️ **Diferencia clave:** `git archive` empaqueta lo **commiteado**, no tus cambios sin guardar. Si estás debuggeando algo a medio escribir y sin commitear, usá la **Forma 1 (`zip`)**, que toma lo que hay en disco tal cual.

---

## 14. Cheat-sheet 🟢

### Git — arranque de un proyecto (una vez)

```bash
git init                                  # convierte la carpeta en repo git
cat > .gitignore << 'EOF'                 # crea el .gitignore con su contenido
target/
.idea/
*.iml
.DS_Store
EOF
git add -A                                # marca TODO para el próximo commit
git commit -m "Setup inicial"             # primer commit
git remote add origin <URL-del-repo>      # engancha tu repo de GitHub
git remote -v                             # LISTA los remotos (verificar que quedó bien)
git push -u origin main                   # primer push ( -u recuerda el destino )
```

### Git — día a día

```bash
git status                                # qué cambió
git add -A && git commit -m "mensaje"     # guardar cambios
git push                                  # subir (ya recuerda el destino por el -u)
git log --oneline                         # historial compacto
git remote -v                             # ver a qué remoto apunta
```

### Maven — dentro de un proyecto

```bash
mvn clean        # borra target/
mvn compile      # compila
mvn test         # compila + corre tests
mvn package      # + arma el .jar en target/
```

---

## 15. Glosario rápido

| Término | En una frase |
|---|---|
| **Maven** | Gestor de build y dependencias: vos declarás, él resuelve. |
| **pom.xml** | El manifiesto del proyecto (dependencias, Java, plugins). |
| **groupId** | La familia/namespace del artefacto (dominio invertido). Capa de build. |
| **artifactId** | El nombre puntual de este artefacto. Capa de build. |
| **artefacto** | El `.jar` que produce un módulo al compilar. |
| **package** | Carpeta con significado para Java: namespacia las clases. Capa de código. |
| **sources root** | La carpeta donde empieza el código (`src/main/java`). |
| **módulo** | Una unidad-proyecto (carpeta + pom) dentro de un build multi-módulo. |
| **reactor** | El build que coordina varios módulos desde un pom padre. |
| **classpath** | El "saco" donde conviven todas las clases en ejecución, por nombre completo. |
| **SNAPSHOT** | Sufijo de versión que indica "en desarrollo, todavía cambia". |

---

## 16. Lo único que necesitás VOS, hoy 🟢

Si te abrumó algo de lo de arriba, quedate con esto y nada más:

- Tu ejercicio es **un proyecto independiente, de un solo módulo**. No tocás módulos, ni multi-módulo, ni `<parent>`.
- `package = groupId`. Sin tramos extra.
- `src/main/java` para el código, `src/test/java` para los tests (opcionales pero buen hábito).
- Un `.gitignore` con `target/`, `.idea/`, `*.iml`.
- **No podés romper nada importante.** Ese es tu alcance, y es seguro.

Todo lo demás —módulos, microservicios, artefactos publicados— es el mapa del territorio para que sepas **dónde estás parado**. Saber que existe te da contexto; no tenés que usarlo todavía.

> El entendimiento que falta a partir de acá no sale de leer más: sale de **escribir Java y chocarse con cosas reales.** Este apunte es el piso firme; ahora se construye encima.
