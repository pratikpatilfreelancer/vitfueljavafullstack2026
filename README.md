# VANGUARD — Disaster Response & Resource Management System (Core Java Edition)

A console-based disaster response and resource allocation system, built to
demonstrate the full Core Java syllabus (OOP, exceptions, multithreading,
collections/generics, streams, file I/O, design patterns, and SQL/JDBC) in
one coherent, working project — not isolated exercises.

## What it does

You report an incident by typing a free-text description (e.g. "building
fire, people trapped, heavy smoke"). A rule-based classifier reads it and
assigns a type and severity. You then run allocation, which matches each
pending incident to the nearest available, most suitable resource (fire
truck, ambulance, rescue team, flood boat, or volunteer unit), dispatches
it on a background thread that simulates travel time, and resolves the
incident automatically on arrival.

## How to run it

There are now **three** front ends over the exact same backend — pick one.
None of them required changing any model/service/exception/concurrency
code; each is just a different way of calling into `DispatchCenter`,
`AllocationService`, and the classifiers.

**REST API (Spring Boot) — needs Maven and internet access the first time
it builds, to download dependencies:**
```bash
mvn spring-boot:run
```
Then hit endpoints like `GET http://localhost:8080/api/incidents` — see
the "REST API" section below for the full list.

**Graphical (Swing):**
```bash
javac -d out $(find src/main/java -name "*.java" -not -path "*/web/*")
cd out
java vanguard.SwingMain
```
(The `-not -path "*/web/*"` skips the Spring Boot package, since compiling
it with plain `javac` won't work — it needs Spring's classes, which only
Maven pulls in.)

**Console (text menu):**
```bash
java vanguard.Main
```

In VS Code, open the Run and Debug panel — "Launch VANGUARD REST API
(Spring Boot)", "Launch VANGUARD GUI (SwingMain)", and "Launch VANGUARD
Console (Main)" are all pre-configured in `.vscode/launch.json`.

## API docs (Swagger / OpenAPI)

Once the REST API is running, open **http://localhost:8080/swagger-ui.html**
in a browser. This is an auto-generated, interactive page listing every
endpoint below — you can expand any of them, fill in example values, and
click "Try it out" to actually call the API right from the browser, no
`curl` needed. It's built automatically from the `@RestController` classes
in `vanguard.web` — nothing to write or keep in sync by hand.

The raw machine-readable spec (if you ever need it, e.g. to import into
Postman) is at **http://localhost:8080/v3/api-docs**.

## REST API endpoints (Spring Boot)

| Method | Path | Does |
|---|---|---|
| GET | `/api/incidents` | List all incidents, sorted by severity |
| GET | `/api/incidents/{id}` | Get one incident (404 if not found) |
| POST | `/api/incidents` | Report a new incident — body: `{"description": "...", "x": 100, "y": 80}` |
| GET | `/api/resources` | List all resources |
| POST | `/api/allocate` | Run the allocation batch |
| GET | `/api/stats` | Summary stats as plain text |
| GET | `/api/audit-log` | Audit log contents as plain text |
| POST | `/api/persist/save` | Save current state to MySQL |
| POST | `/api/persist/load` | Load state from MySQL |

Try it with curl:
```bash
curl -X POST http://localhost:8080/api/incidents \
  -H "Content-Type: application/json" \
  -d '{"description":"building fire, people trapped","x":70,"y":65}'

curl http://localhost:8080/api/incidents
curl -X POST http://localhost:8080/api/allocate
```

No external dependencies needed for the core app — it runs entirely
in-memory. The optional MySQL/JDBC layer (see below) needs the MySQL
Connector/J driver on your classpath and a running MySQL server, but
everything else works without either.

## Where each syllabus topic lives

| Syllabus topic | Where it's demonstrated |
|---|---|
| Object, state, behavior, encapsulation | `model/Resource.java` — private fields, controlled access via getters and `markBusy()`/`markAvailable()` |
| Abstraction, abstract class/method | `model/Resource.java` (abstract class, two abstract methods) |
| 'IS A' — Inheritance, method overriding | `model/FireTruck.java`, `Ambulance.java`, `RescueTeam.java`, `FloodBoat.java`, `VolunteerUnit.java` all extend `Resource` |
| 'HAS A' — Containment | `util/DispatchCenter.java` contains `GenericRepository` instances |
| Polymorphism, super class reference to subclass object | `service/AllocationService.java` operates on `List<Resource>` holding mixed subclasses |
| Interfaces, multiple role-based inheritance | `model/Locatable.java` implemented by both `Resource` and `Incident` |
| Comparable vs Comparator | `Incident implements Comparable<Incident>` (natural severity ordering); `Comparator.comparingDouble` used in `AllocationService` |
| final keyword | Enums (`Severity`, etc.) are implicitly final classes — see comment in `Severity.java` |
| Custom annotations, meta-annotations | `util/Loggable.java` (`@Retention`, `@Target`), applied to `Main.runAllocation()`, read via reflection in `util/AnnotationScanner.java` |
| Functional interfaces, lambdas, method references | `service/IncidentClassifier.java` (`@FunctionalInterface`); lambda example in `Main.main()`; method references (`Resource::isAvailable`) in `AllocationService` |
| Wrapper classes, String/StringBuilder | `io/AuditLogger.java` builds log lines with `StringBuilder` |
| Static nested class vs inner class | `repository/GenericRepository.java` (static, no outer instance needed) vs `util/DispatchCenter.StatsView` (non-static inner class, reads outer fields directly) |
| Exception handling, checked vs unchecked, custom hierarchy | `exceptions/` package: `VanguardException` (checked base), `IncidentNotFoundException`/`NoAvailableResourceException` (checked subtypes), `InvalidReportException` (unchecked) |
| try/catch/finally, try-with-resources | `Main.main()` main loop (try/catch/finally); every DAO method and `AuditLogger`/`StateSerializer` (try-with-resources) |
| Multithreading — extending Thread | `concurrent/DispatchThread.java` |
| Multithreading — implementing Runnable | `concurrent/HeartbeatMonitor.java`, started in `Main.startHeartbeat()` |
| Thread synchronization, synchronized method/block | `util/DispatchCenter.java` (synchronized methods); `HeartbeatMonitor.stop()` (synchronized block) |
| Inter-thread communication | `HeartbeatMonitor` uses `wait()`/`notifyAll()` on a shared lock object |
| Collections — List, Set, Map, generics | `repository/GenericRepository.java` (hand-written generic class); `TreeSet` in `DispatchCenter.pendingIncidentsBySeverity()`; `HashMap`-backed storage inside the repository |
| Stream API | `AllocationService.allocate()` (filter/sorted/findFirst); `DispatchCenter.countBySeverity()` (`Collectors.groupingBy`) |
| File I/O | `io/AuditLogger.java` — `BufferedWriter`/`BufferedReader`, `Files.exists()` |
| Object serialization | `io/StateSerializer.java` — `ObjectOutputStream`/`ObjectInputStream`; every model class implements `Serializable` |
| Design patterns — Singleton | `util/DispatchCenter.getInstance()` |
| Design patterns — Factory | `util/ResourceFactory.create()` |
| SQL / JDBC | `db/` package — `DatabaseConnection`, `IncidentDAO`, `ResourceDAO`; `sql/schema.sql`, `sql/queries.sql` |

## Optional: connecting the SQL layer

The app runs entirely in-memory by default (via `DispatchCenter`), so you
don't need MySQL to use or demo it — every core feature (reporting,
classification, allocation, dispatch, the GUI) works with zero setup.
MySQL is wired in as a genuinely working **additional** persistence option,
not just unused example code:

1. Install MySQL and run `sql/schema.sql` to create `vanguard_db` and its
   tables.
2. Download the MySQL Connector/J `.jar` and add it to your classpath when
   compiling/running (in VS Code: right-click the project > "Add Folder
   to Java Source Path" won't work for a jar — instead add it via the
   Java Projects panel > Referenced Libraries > Add Jar, or add
   `-cp mysql-connector-j-x.x.x.jar` to your `javac`/`java` commands).
3. Edit the three constants at the top of `db/DatabaseConnection.java`
   (URL, username, password) to match your setup.
4. In the GUI, click **Save to MySQL** to write every current incident and
   resource into the database (safe to click repeatedly — it's an
   upsert, not a plain insert), or **Load from MySQL** to pull them back
   in. Both call `IncidentDAO`/`ResourceDAO` directly.
5. `sql/queries.sql` has extra example queries (WHERE, UPDATE, DELETE,
   INNER/LEFT/SELF/CROSS JOIN) you can run directly in MySQL to show
   off that part of the syllabus even beyond what the GUI buttons exercise.

## Project structure

```
pom.xml                      Maven build file (Spring Boot dependency)
src/main/java/vanguard/
  Main.java                 console entry point
  SwingMain.java             GUI entry point
  web/                      Spring Boot REST API (VanguardApplication, controllers)
  ui/                       Swing front end (MainFrame, MapPanel, dialog, renderers)
  model/                    Resource hierarchy, Incident, enums, Locatable
  exceptions/                custom checked/unchecked exceptions
  service/                  classification + allocation logic
  concurrent/                Thread/Runnable dispatch simulation
  repository/                generic in-memory repository
  util/                     Singleton, Factory, annotations, geometry
  io/                       file logging + object serialization
  db/                       JDBC DAOs (needs MySQL for real persistence)
sql/
  schema.sql                 CREATE DATABASE/TABLE statements + incident_patterns case base
  queries.sql                 example SELECT/UPDATE/DELETE/JOIN queries
```

## The classifier: three layers, all falling back gracefully

The active classifier in all three front ends (`DatabaseBackedClassifier`)
tries, in order:

1. **Case-based match** — compares the new description against
   `incident_patterns` in MySQL (past incidents with known-correct
   classifications). If a close enough match is found, reuses it.
2. **SQL keyword dataset** — if no close case match, scores the
   description against `keyword_rules` in MySQL: individual words like
   "fire" or "collapse" mapped to an incident type and a severity weight
   (see `sql/schema.sql`). **Adding or re-tuning a keyword is just an
   INSERT/UPDATE in SQL — no Java code changes or recompiling needed.**
   `SqlKeywordClassifier` also carries a small built-in copy of the same
   keyword list so it still works if MySQL isn't connected.
3. **Hardcoded regex (last resort)** — `RuleBasedClassifier`, only used
   if something goes wrong with both database-backed steps.

This is deliberately not a claim of real machine learning — it's
transparent, inspectable reference data plus a scoring rule, not a
trained model. Worth being upfront about that distinction if asked, while
still being a genuine demonstration of SQL driving application behavior
rather than sitting unused on the side.

## Using the GUI

- Dark, themed interface (`ui/Theme.java`) — data shown in proper tables
  (`JTable`), not plain lists, with severity/status color-coded per row.
- **Report Incident** opens a form for the description and coordinates.
  For a more visual flow: click "Pin Location Mode" first, then click
  anywhere on the tactical map to set the incident's location before
  opening the report form — it'll pre-fill with your pinned spot.
- **Run Allocation** matches every pending incident to the nearest
  suitable resource and shows the result; assigned resources are
  dispatched on a background thread that "arrives" after a simulated
  delay and marks the incident resolved automatically — watch the map
  and tables update live as it happens.
- **Stats**, **Audit Log**, **Save File** / **Load File** (object
  serialization) all call straight into the same backend classes the
  console version uses.
- **Save to MySQL** / **Load from MySQL** call `IncidentDAO`/`ResourceDAO`
  directly — real JDBC writes/reads against your database (see the SQL
  setup section above). If MySQL isn't running or configured, these fail
  gracefully with an error dialog instead of crashing the app; everything
  else keeps working normally.
