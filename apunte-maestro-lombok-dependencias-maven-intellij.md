# 🧩 Apunte Maestro — Lombok, dependencias, Maven e IntelliJ (de cero, sin sufrir)

> **Para quién es esto:** para alguien que viene de JavaScript / npm, recién aterriza en Java con Maven e IntelliJ, y está enredado con por qué Lombok "a veces funciona y a veces no", qué va en el `pom.xml`, qué es ese plugin que te hicieron instalar, y cómo correr el programa. Si Lombok te funcionó en un proyecto y en otro no, y sentís que fue *de suerte* — no fue suerte, y acá vas a entender por qué.
>
> **Spoiler del final feliz:** no es magia, no es azar, y no vas a tener que pelearte con esto cada vez. Es setup de **una sola vez por proyecto**. Una vez que entendés las piezas, lo resolvés en 2 minutos y no lo tocás más.

---

## 0. 🎯 TL;DR (el resumen de una frita)

Para que Lombok funcione hacen falta **tres piezas**, pero **no siempre las tres a la vez**. Depende de *cómo corrés el código*:

| Pieza | Qué es | ¿Cuándo la necesito? |
|---|---|---|
| **1. La dependencia** | Un bloque `<dependency>` en el `pom.xml` | **SIEMPRE.** Sin esto, Lombok no existe en tu proyecto. |
| **2. Plugin IntelliJ + "annotation processing"** | Un plugin del IDE + un checkbox en Settings | Si corrés con la **flechita verde** de IntelliJ. |
| **3. El `<build>` con `annotationProcessorPaths`** | Otro bloque en el `pom.xml` | Solo si compilás/corrés por **terminal con `mvn`**. |

**Los dos escenarios:**

- **Corrés con la flechita verde de IntelliJ** (lo más común al practicar) → necesitás **Pieza 1 + Pieza 2**. *No* necesitás el `<build>`.
- **Corrés por terminal con `mvn`** → necesitás **Pieza 1 + Pieza 3**.

La dependencia (Pieza 1) está en **los dos** casos. Eso es lo que más confunde: parece opcional, pero no lo es nunca.

Si entendés solo esta tabla, ya estás salvado. El resto del apunte es el *por qué*, para que no dependas de memorizarlo.

---

## 1. 🔄 Cambio de chip: venías de npm, ahora estás en Maven

Si tu modelo mental es npm, este es el diccionario de traducción. Te va a ordenar medio mundo:

| En el mundo de npm (JS) | En el mundo de Maven (Java) |
|---|---|
| `package.json` | `pom.xml` |
| `node_modules/` (carpeta con las libs) | `~/.m2/repository/` (carpeta cacheada, fuera del proyecto) |
| `npm install libreria` | Agregar un bloque `<dependency>` al `pom.xml` |
| npm te edita el `package.json` solo | Maven NO: el `pom.xml` lo editás vos (o con un atajo del IDE, ver sección 7) |
| `npm run start` | `mvn compile exec:java` (o la flechita verde) |
| Versión: `"libreria": "^1.2.3"` | Versión: `<version>1.2.3</version>` |

**Honestidad sin vueltas:** sí, la experiencia de npm es más cómoda para agregar dependencias. `npm install x` te toca el `package.json` solo; Maven históricamente no tiene ese comando y editás el `pom.xml` a mano. **No lo estás imaginando, y no es un déficit tuyo** — es una diferencia real entre ecosistemas. La buena noticia es que el IDE te da un atajo parecido a `npm install` (sección 7), así que no estás condenado a escribir XML a mano para siempre.

---

## 2. 🧠 Qué es Lombok y por qué da más trabajo que una librería normal

**Lombok** es una librería que te **genera código repetitivo automáticamente**: getters, setters, `toString()`, `equals()`, constructores, etc. Vos escribís una annotation (`@Data`) y Lombok genera 50 líneas por vos.

Acá está el dato que explica TODO el lío:

> **Lombok genera ese código durante la compilación.** Es lo que se llama un *annotation processor* (procesador de anotaciones). No es una librería "normal" que simplemente se usa en tiempo de ejecución — interviene en el momento en que tu código se compila.

¿Por qué importa? Porque **quien compile tu código tiene que saber que Lombok existe y ejecutarlo**. Y resulta que hay dos cosas distintas que pueden compilar tu código:

- **IntelliJ**, cuando apretás la flechita verde (usa su propio compilador).
- **Maven**, cuando corrés `mvn` por terminal (usa el suyo).

Cada uno necesita que le avisen "che, pasá por Lombok antes de terminar". Por eso hay dos configuraciones distintas (Pieza 2 y Pieza 3): una por cada compilador. **Ese es el origen del "a veces anda y a veces no"** — andaba cuando la pieza que correspondía a tu forma de correr estaba puesta.

> 💡 **Que esto te tranquilice:** Lombok es un caso **especialmente** quisquilloso *justamente* porque genera código en compilación. **La gran mayoría de las dependencias NO son así.** El 90% de las veces, agregar una librería es *solo* pegar el bloque `<dependency>` y nada más — sin plugin, sin annotation processing, sin `<build>`. Lombok es la excepción molesta, no la regla.

---

## 3. 🔧 Las tres piezas en detalle

### Pieza 1 — La dependencia (OBLIGATORIA, SIEMPRE)

Esto le dice a Maven "bajá la librería Lombok y ponela disponible para mi proyecto". Sin esto, ni siquiera podés escribir `import lombok.Data;` — el código no compila porque Lombok literalmente no está.

```xml
<dependencies>
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <version>1.18.46</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

- `groupId` + `artifactId` = la "dirección" única de la librería (como el nombre de un paquete en npm).
- `version` = qué versión querés. **1.18.46** es la estable al momento de escribir esto; sirve para Java 21. (Cómo saber la última: sección 7.)
- `<scope>provided</scope>` = "la necesito para *compilar*, pero no hace falta empaquetarla para *ejecutar*". Tiene sentido para Lombok porque, una vez que generó los getters en la compilación, el código generado ya quedó adentro y la librería en sí no se necesita más.

### Pieza 2 — Plugin de IntelliJ + "annotation processing" (para la flechita verde)

Cuando corrés con la flechita verde ▶️, **el que compila es IntelliJ**. Para que IntelliJ sepa procesar Lombok necesita dos cosas, y normalmente ya vienen resueltas:

1. **El plugin de Lombok instalado.** En IntelliJ moderno **ya viene incluido**. (Es el plugin que tu profe quizás te hizo verificar.) Para chequearlo: `Settings → Plugins → Installed → buscar "Lombok"` → tiene que estar tildado.
2. **"Enable annotation processing" tildado.** Acá: `Settings → Build, Execution, Deployment → Compiler → Annotation Processors → ☑ Enable annotation processing`.

Si esto falta, IntelliJ no "ve" los getters que genera Lombok y te marca todo en rojo (`cannot find symbol: method getX()`) aunque el `pom.xml` esté perfecto.

### Pieza 3 — El `<build>` con `annotationProcessorPaths` (para la terminal)

Cuando corrés por terminal con `mvn`, **el que compila es Maven**, no IntelliJ — así que el plugin del IDE no juega. Hay que decirle a Maven, en el propio `pom.xml`, que use Lombok como procesador de anotaciones:

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.13.0</version>
            <configuration>
                <annotationProcessorPaths>
                    <path>
                        <groupId>org.projectlombok</groupId>
                        <artifactId>lombok</artifactId>
                        <version>1.18.46</version>
                    </path>
                </annotationProcessorPaths>
            </configuration>
        </plugin>
    </plugins>
</build>
```

`annotationProcessorPaths` es exactamente eso: la lista de procesadores de anotaciones que Maven le pasa al compilador. Acá adentro va Lombok.

### La tabla de decisión (clavá esto en la pared)

| Cómo corro el código | Pieza 1 (dependencia) | Pieza 2 (IntelliJ) | Pieza 3 (`<build>`) |
|---|:---:|:---:|:---:|
| **Flechita verde de IntelliJ** | ✅ | ✅ | ❌ no hace falta |
| **Terminal con `mvn`** | ✅ | (no aplica) | ✅ |

---

## 4. 📋 Los `pom.xml` listos para copiar

Reemplazá `groupId`, `artifactId` y (si usás terminal) `mainClass` por los tuyos. Lo que está marcado como ejemplo cambialo; el resto dejalo igual.

### 4.1 — Versión MÍNIMA (para correr con la flechita verde de IntelliJ)

Esta es la que vas a usar el 99% del tiempo practicando. **Una dependencia, cero `<build>`.**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <!-- 👇 CAMBIÁ estos tres por los tuyos -->
    <groupId>io.github.tu_usuario.fundamentos</groupId>
    <artifactId>nombre-del-proyecto</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.46</version>
            <scope>provided</scope>
        </dependency>
    </dependencies>

</project>
```

### 4.2 — Versión COMPLETA (para correr también por terminal con `mvn`)

Igual a la anterior, pero agrega el `<build>` con dos plugins: `maven-compiler-plugin` (para que Lombok compile vía Maven) y `exec-maven-plugin` (para poder *ejecutar* tu `Main` desde la terminal).

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <!-- 👇 CAMBIÁ estos tres por los tuyos -->
    <groupId>io.github.tu_usuario.fundamentos</groupId>
    <artifactId>nombre-del-proyecto</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.46</version>
            <scope>provided</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <!-- Compila tu código y le pasa Lombok al compilador de Maven -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.13.0</version>
                <configuration>
                    <annotationProcessorPaths>
                        <path>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                            <version>1.18.46</version>
                        </path>
                    </annotationProcessorPaths>
                </configuration>
            </plugin>
            <!-- Permite ejecutar tu Main desde la terminal con: mvn exec:java -->
            <plugin>
                <groupId>org.codehaus.mojo</groupId>
                <artifactId>exec-maven-plugin</artifactId>
                <version>3.5.0</version>
                <configuration>
                    <!-- 👇 CAMBIÁ esto: package completo + nombre de tu clase Main -->
                    <mainClass>io.github.tu_usuario.fundamentos.Main</mainClass>
                </configuration>
            </plugin>
        </plugins>
    </build>

</project>
```

> **Sobre `<mainClass>`:** va la ruta completa del package + el nombre de la clase que tiene tu `main`. Si tu `Main.java` está en `src/main/java/io/github/tu_usuario/fundamentos/practica/Main.java`, entonces es `io.github.tu_usuario.fundamentos.practica.Main`. Es la "dirección" de tu clase, con puntos en vez de barras.

---

## 5. ❓ Qué es eso de `${algo.version}`

A veces vas a ver la versión escrita así: `<version>${lombok.version}</version>`. Eso **no lo completás a mano cada vez** — es una **variable**.

En el `pom.xml` podés declarar valores arriba, en `<properties>`, y reusarlos abajo con `${nombre}`. Es idéntico a un `const` en JS:

```javascript
const LOMBOK_VERSION = "1.18.46";   // lo declaro una vez
// ...y lo reuso donde quiera
```

```xml
<properties>
    <lombok.version>1.18.46</lombok.version>   <!-- lo declaro una vez -->
</properties>
...
<version>${lombok.version}</version>           <!-- lo reuso -->
```

Sirve cuando la misma versión aparece en varios lugares (como en la 4.2, donde Lombok figura dos veces). **No es obligatorio:** podés poner el número directo (`1.18.46`) y funciona igual. Si recién empezás, poné el número directo y olvidate de las variables hasta que las necesites.

---

## 6. ▶️ Cómo ejecutar tu programa (los dos modos)

### 6.1 — Con la flechita verde de IntelliJ (cómodo, recomendado para practicar)

Abrís tu clase con el `main`, hacés clic en la **▶️ verde** que aparece al lado, "Run". IntelliJ compila (procesando Lombok gracias a la Pieza 2) y corre. El output aparece abajo. Listo. Para esto te alcanza el pom **4.1**.

### 6.2 — Con la terminal y Maven

Necesitás el pom **4.2** (con el `<build>`). Abrí una terminal **en la raíz del proyecto** (donde está el `pom.xml`). En IntelliJ tenés una terminal integrada con `Alt+F12` que ya abre en la carpeta correcta.

Comando para compilar y ejecutar de una:

```bash
mvn compile exec:java
```

Hace dos cosas en orden: `compile` (compila tu código — acá Lombok genera los getters/setters) y `exec:java` (corre tu `Main`).

**Dos cosas que vas a notar viniendo de npm, para que no te asusten:**

- Maven escupe **muchísimo texto** (fases, descargas, etc.). Es normal, no es error. Tu `System.out.println` está en el medio de todo eso. Al final tiene que decir `BUILD SUCCESS`.
- La **primera vez** puede tardar y bajar plugins de internet. Después queda cacheado y va más rápido.

> En Windows, si el proyecto trae un wrapper, el comando puede ser `mvnw compile exec:java` (o `.\mvnw` en PowerShell). Si `mvn` te funciona directo, usá `mvn`.

---

## 7. 🛠️ Cómo agregar CUALQUIER dependencia (tu autonomía, sin depender de nadie)

Esto es lo que te devuelve la sensación de `npm install`. Cinco formas, de la más cómoda a la más manual:

1. **Atajo del IDE (lo más parecido a `npm install`):** abrí el `pom.xml`, poné el cursor dentro de `<dependencies>`, apretá **`Alt+Insert`** → "Add dependency" → buscás el nombre (ej. "lombok") → IntelliJ te inserta el bloque solo. No escribís XML a mano.

2. **Maven Central / Sonatype:** entrá a `mvnrepository.com` o `central.sonatype.com`, buscás la librería, elegís la versión, y te da el bloque `<dependency>` **listo para copiar**. Esta es la fuente canónica de *cualquier* librería Java, y también es donde mirás **cuál es la última versión**.

3. **La página oficial de la librería:** muchas (Lombok incluida) tienen una sección "setup → Maven" con el XML exacto, incluido cualquier paso especial (como el annotation processing de Lombok).

4. **Copiar de un proyecto que ya te funciona:** totalmente válido. Es como hiciste hasta ahora. Si ya tenés Lombok andando en un proyecto, copiá el bloque al nuevo.

5. **Spring Initializr (`start.spring.io`):** cuando llegues a proyectos Spring, este sitio te genera el `pom.xml` completo con las dependencias elegidas. No tenés que armarlo a mano.

> **Punto importante para tu tranquilidad:** antes de que existiera la IA, la gente resolvía esto exactamente con los métodos 1 a 5. No lo adivinaban de memoria. Vos también podés. No es que "tengas que pedirle a una IA" — es que aún no conocías estas fuentes. Ahora sí.

---

## 8. 🚨 Cuando algo falla (troubleshooting)

| Síntoma | Causa probable | Solución |
|---|---|---|
| `cannot find symbol: method getX()` corriendo con la flechita verde | Falta "Enable annotation processing", o el plugin de Lombok | Tildá annotation processing (Pieza 2) y verificá el plugin |
| `cannot find symbol: method getX()` corriendo con `mvn` por terminal | Falta el `<build>` con `annotationProcessorPaths` (Pieza 3) | Usá el pom 4.2 |
| `package lombok does not exist` / no reconoce `import lombok.Data` | Falta la dependencia (Pieza 1), o no recargaste Maven | Agregá el `<dependency>` y recargá Maven |
| Cambié el `pom.xml` y no pasa nada | IntelliJ no recargó los cambios | Apretá el ícono de **recarga de Maven** (refresh, arriba a la derecha del panel Maven) |
| `mvn exec:java` dice que no encuentra la main class | El `<mainClass>` está mal escrito | Revisá que sea package completo + nombre de clase, con puntos |
| Todo en rojo justo después de crear el proyecto | Maven todavía no bajó las dependencias | Recargá Maven y esperá a que termine de bajar |

> **El reflejo de oro ante cualquier rojo de Lombok:** (1) ¿está la dependencia? (2) ¿está annotation processing tildado? (3) ¿recargaste Maven? Con esos tres chequeos resolvés casi todo.

---

## 9. ✅ Checklist para un proyecto NUEVO desde cero

Cada vez que arranques un proyecto nuevo con Lombok, esto es mecánico. No más drama:

1. Creás el proyecto Maven en IntelliJ (Java 21).
2. Abrís el `pom.xml` y pegás la dependencia de Lombok (pom 4.1). *Solo* si vas a usar terminal, usás el 4.2.
3. **Recargás Maven** (ícono de refresh).
4. Verificás que **"Enable annotation processing"** esté tildado (Settings → Compiler → Annotation Processors). Esto suele quedar guardado entre proyectos, pero confirmalo.
5. **Smoke test:** creás una clase con `@Data` y un campo, y desde el `main` hacés `obj.getCampo()`. Si autocompleta y corre → ✅ ganaste.
6. No tocás el `pom.xml` nunca más (salvo que agregues otra librería).

---

## 🌅 Lo que tenés que recordar (si te olvidás de todo lo demás)

- **La dependencia va siempre.** No es opcional.
- **No es suerte:** Lombok andaba cuando estaba puesta la pieza que correspondía a tu forma de correr (IntelliJ o terminal).
- **Lombok es quisquilloso porque genera código en la compilación.** La mayoría de las librerías son solo el bloque `<dependency>` y nada más.
- **Es setup de una sola vez por proyecto.** Una vez que anda, te olvidás.
- **Tenés autonomía:** `Alt+Insert` en el pom, o `mvnrepository.com`, te dan cualquier dependencia sin depender de nadie.

Si llegaste hasta acá entendiendo, ya saliste del pozo. El resto es escribir Java.
