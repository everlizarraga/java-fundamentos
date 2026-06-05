# ☕ Apunte — Java esencial desde JavaScript (referencia rápida)

> **Qué es esto:** un compendio compacto de fundamentos prácticos de Java, pensado para **repasar**, no para aprender de cero (eso ya lo hiciste). Organizado por tema, con el puente a JS marcado donde ayuda. El hilo conductor: casi todo son **diferencias con JavaScript que muerden**.
>
> **Las 3 trampas que más muerden viniendo de JS** (si te olvidás de todo lo demás, acordate de estas):
> 1. Comparar Strings con `==` → usá `.equals()`.
> 2. `List.of(...)` es inmutable → `.add()` explota en ejecución.
> 3. Existen `int` vs `long` vs `double` (tipos numéricos separados) → en JS había un solo `number`.

---

## 1. 🔤 Strings

**`==` vs `.equals()` — LA trampa #1:**
```java
"rock" == "rock"                 // ❌ compara identidad de objeto, no contenido. NUNCA confíes en esto
"rock".equals("rock")            // ✅ compara contenido
"Rock".equalsIgnoreCase("rock")  // ✅ compara ignorando mayúsculas/minúsculas
```
> JS: `"rock" === "rock"` comparaba contenido y andaba. En Java, `==` con Strings = bug silencioso. Para contenido, **siempre** `.equals()`.

**Mayúsculas / minúsculas:**
```java
String m = s.toUpperCase();   // devuelve un String NUEVO (los String son inmutables) → hay que asignarlo
String n = s.toLowerCase();
```
Comparar sin importar may/min: `.equalsIgnoreCase(...)` (lo limpio), o convertir ambos y `.equals()`.

**`substring` (índices desde 0; incluye el inicio, EXCLUYE el fin):**
```java
s.substring(0, 4);            // primeras 4 letras (índices 0,1,2,3)
s.substring(s.length() - 4);  // últimas 4
int i = (s.length() - 4) / 2; s.substring(i, i + 4);  // 4 del medio
```
> ⚠️ Índice fuera de rango → `StringIndexOutOfBoundsException`. En JS, `"ab".slice(0,4)` devolvía `"ab"` sin quejarse; Java explota. Chequeá `s.length()` si no estás seguro.

**Juntar una lista en un solo String:**
```java
String.join(", ", listaDeStrings);   // lista YA de Strings — ⚠️ el separador va PRIMERO
objetos.stream().map(O::getCampo).collect(Collectors.joining(", "));  // lista de objetos: extraer campo primero
```
> JS: `arr.join(", ")`. En Java es `String.join(sep, lista)` (separador adelante), o `Collectors.joining` si venís de un stream.

---

## 2. 🔢 Números y tipos

**Convertir `long` / `Long` → `int`:**

| Tenés | Convertís con |
|---|---|
| `long` primitivo (ej. `.count()`) | `(int) valor` — el cast |
| `Long` (objeto wrapper) | `valor.intValue()` |
| Un `long` que podría ser enorme | `Math.toIntExact(valor)` — falla si no entra, en vez de corromper |

> ⚠️ El cast `(int)` **trunca en silencio** si el número no entra en un `int` (~2.100 millones). Para `.count()` de una lista normal nunca pasa, así que el `(int)` está perfecto ahí. JS no tenía esto: un solo `number`.
> 💡 Consejo: muchas veces **no necesitás convertir** — dejá el `long` si solo lo imprimís o comparás.

**`mapToInt/Long/Double` vs `map` (para sumar):**
```java
productos.stream().map(Producto::getPrecio).sum();         // ❌ map deja un Stream<Objeto>, no tiene .sum()
productos.stream().mapToDouble(Producto::getPrecio).sum(); // ✅ stream numérico → tiene sum/average/max/min
```
- `map` → seguís con `Stream<Objeto>` (para transformar y quedarte con una colección).
- `mapToInt/Long/Double` → stream numérico especializado (para hacer **cuentas**). Elegí el que coincida con el tipo del campo; `sum()` devuelve ese mismo tipo.
> Equivale al `map(...).reduce((a,b)=>a+b, 0)` de JS, pero `mapToXxx.sum()` es lo directo y eficiente.

---

## 3. 🌊 Streams: terminales, intermedias y pereza

**Intermedia vs terminal — la regla es QUÉ devuelve:**
- Devuelve **otro stream** → **intermedia** (se encadena): `filter`, `map`, `sorted`, `limit`, `distinct`, `skip`, `mapToInt/Long/Double`, `peek`.
- Devuelve **otra cosa** (lista, número, boolean, nada) → **terminal** (dispara la ejecución): `toList`, `collect`, `count`, `anyMatch`/`allMatch`/`noneMatch`, `findFirst`/`findAny`, `forEach`, `sum`/`average`/`max`/`min`, `reduce`.

> ⚠️ Sin operación terminal, el stream **no corre** (armaste la cinta pero no la prendiste). Es la diferencia más rara viniendo de JS, donde `.filter().map()` ejecutaba al toque.

**El instinto del "salí y volví a entrar" (`.toList()...stream()` en el medio):**
Es mala señal — casi siempre se hace en un solo pipeline. La terminal intermedia **materializa todo** y corta la **pereza** (los streams son *lazy*: no ejecutan hasta la terminal).
- Para descendente, `.reversed()` va en el **comparator**, no dando vuelta la lista:
  ```java
  .sorted(Comparator.comparing(Cancion::getReproducciones).reversed())  // ✅ descendente directo
  ```
- `sorted().limit(n)`: Java es vivo — **no** ordena todo para tirar casi todo; mantiene solo el "podio de n" recorriendo una vez. Importa con datos grandes; con pocos elementos da igual y lo hacés por **claridad**.

---

## 4. 🏛️ Clases, estáticos y enums

**Miembros estáticos dentro de la misma clase:**
```java
canciones                    // ✅ adentro de la clase: directo
NombreClase.canciones        // desde OTRA clase: con el prefijo
```
> Como una variable de módulo en JS: adentro la usás directo, desde afuera la importás/prefijás.
- Buenas prácticas: arrancá todo `private` (abrí solo lo necesario). Si es un dato fijo de referencia: `private static final` + nombre en `MAYÚSCULAS`.

**Enums — el `"MODO_1" | "MODO_2"` de JS, pero garantizado por el compilador:**
```java
public enum Genero { ROCK, POP, JAZZ }

void filtrar(Genero g) { ... }
filtrar(Genero.ROCK);   // ✅ IntelliJ autocompleta los valores válidos
filtrar(Genero.ROK);    // ❌ NO COMPILA — el typo lo caza el compilador (no es solo "sugerencia" como JSDoc)
```
> En JS, `"MODO_1" | "MODO_2"` era una sugerencia: nada impedía pasar un typo. El enum es una **regla**: si no está en la lista, no compila.

**Dónde declarar el enum:**

| Situación | Dónde |
|---|---|
| Lo usan varias clases (lo común) | su propio archivo `Genero.java`, en el package del dominio |
| Solo lo usa una clase puntual | anidado dentro de esa clase |
| "Junto todos los enums porque son enums" | ❌ evitá — se agrupa por **dominio**, no por tipo de archivo |

**Javadoc (= JSDoc, para DESCRIBIR, no para restringir):**
```java
/** Filtra por género. @param g el género a buscar. @return la lista filtrada. */
```
> Distinción clave: el **enum** hace cumplir *qué* valores se aceptan (compilador). El **Javadoc** *describe* (tooltip en el IDE). En JS, el JSDoc hacía las dos cosas; en Java se separan.

---

## 5. 🛠️ Tooling (Maven / IntelliJ)

**`mvn clean`:** borra `target/` (todo lo compilado). Maven es **incremental** → no hace falta siempre.
- Día a día: `mvn compile exec:java`.
- Si algo huele raro o tocaste el `pom.xml`: `mvn clean compile exec:java` (botón de reset).
- Con la **flechita verde de IntelliJ** es automático (el equivalente al clean es `Build → Rebuild Project`).
> JS: `clean` es como borrar `node_modules` + `dist` — no lo hacés en cada run, solo cuando algo se rompió raro.

**¿"Scripts" estilo `package.json`?** Maven no tiene scripts con nombres libres; tiene **fases fijas**: `compile`, `test`, `package`, `clean`. Para tener un atajo cómodo:
- **Run Configuration de IntelliJ** (lo más parecido a `npm start`): guardás `compile exec:java` como un botón ▶️ reutilizable. ← recomendado.
- Alias de terminal: `alias run='mvn compile exec:java'` (vive en tu máquina, no en el proyecto).

**Plugin de Lombok vs la dependencia** (resumen; el detalle completo está en el apunte de Lombok):
- **Dependencia** (en el `pom.xml`) → para que el **código funcione** (genera los getters en la compilación). Sin ella, no compila.
- **Plugin de IntelliJ** → para que el **editor te entienda** (autocompletado, no marcar rojo falso). Sin él, compila y corre igual, pero el IDE te miente con errores que no son.

---

## 📎 Mini-cheatsheet JS ↔ Java (lo más consultado)

| Necesito | JS | Java |
|---|---|---|
| Comparar contenido de Strings | `a === b` | `a.equals(b)` (¡no `==`!) |
| Ignorar may/min al comparar | `a.toLowerCase() === ...` | `a.equalsIgnoreCase(b)` |
| Pedazo de String | `s.slice(0, 4)` | `s.substring(0, 4)` |
| Últimas N letras | `s.slice(-4)` | `s.substring(s.length() - 4)` |
| Juntar lista en String | `arr.join(", ")` | `String.join(", ", lista)` |
| Sumar un campo | `arr.reduce((a,b)=>a+b.x, 0)` | `lista.stream().mapToDouble(O::getX).sum()` |
| Valores fijos de un parámetro | JSDoc `"A" \| "B"` (sugerencia) | `enum` (lo exige el compilador) |
| Documentar una función | JSDoc | Javadoc (`/** @param @return */`) |
| Rebuild total | borrar `node_modules`+`dist` | `mvn clean ...` / Rebuild Project |
| Comando rápido reutilizable | script en `package.json` | Run Configuration de IntelliJ |
