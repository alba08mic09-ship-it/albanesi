# Consegna Esercizi UFS12 · DevOps & Containerizzazione

**Studente:** Michele Albanesi  
**Repository / Cartella:** `Albanesi_Michele_UFS12_LabDevOps`  
**Tecnologie:** Java 17/21, Quarkus 3, Maven Wrapper, Docker, RestAssured  

---

## 1. Riepilogo dei Requisiti e Soluzione Implementata

Tutti i **7 requisiti** specificati nel documento `docs/REQUISITI.md` sono stati implementati e verificati:

| # | Requisito | Stato | File / Dettagli implementativi |
|---|---|---|---|
| **1** | `GET /` restituisce HTML con messaggio, hostname e versione | ✅ Completato | `AppResource.java` restituisce pagina HTML stilizzata con card e badge contenenti i dati dinamici |
| **2** | `GET /info` restituisce JSON con `hostname`, `version` e `startedAt` | ✅ Completato | `AppResource.java` serializza il record `InfoResponse` con data ISO-8601 di avvio |
| **3** | `GET /health` restituisce HTTP 200 con `{"status":"ok"}` | ✅ Completato | Implementato in `AppResource.java` per probe Kubernetes |
| **4** | `POST /crash` risponde HTTP 202 con `{"status":"crashing"}` e spegne l'app | ✅ Completato | Risponde con 202 e avvia `System.exit(1)` su thread asincrono se `app.crash.exit=true` |
| **5** | Variabili d'ambiente `MESSAGE` e `APP_VERSION` con valori di default | ✅ Completato | Configurate in `application.properties` con sintassi `${MESSAGE:Ciao dalla mia app!}` e `${APP_VERSION:1.0}` |
| **6** | Porta da variabile `PORT` (default 8080) e ascolto su `0.0.0.0` | ✅ Completato | Configurate in `application.properties` (`quarkus.http.host=0.0.0.0` e `quarkus.http.port=${PORT:8080}`) |
| **7** | Singolo uber-jar, nessun DB, nessun file su disco, stateless | ✅ Completato | Configurato `quarkus.package.jar.type=uber-jar`, pacchetto generato in `target/*-runner.jar` |

---

## 2. File del Progetto Modificati e Creati

1. **`prompts/lab3.md`**: Compilato con il prompt completo seguendo le 6 sezioni di `docs/PROMPT.md` (Contesto, Obiettivo, Requisiti, Vincoli, Verifica, Formato della risposta, Versioni successive).
2. **`src/main/java/it/zucchetti/lab/AppResource.java`**: Risorsa REST JAX-RS / Quarkus che implementa tutti i 4 endpoint (`/`, `/info`, `/health`, `/crash`) e la risoluzione dinamica dell'hostname.
3. **`src/main/resources/application.properties`**: File di configurazione dell'applicazione che mappa le variabili d'ambiente con i relativi default, abilita Swagger UI e gestisce il flag `%test.app.crash.exit=false`.
4. **`src/test/java/it/zucchetti/lab/AppResourceTest.java`**: Suite di test con `@QuarkusTest` e RestAssured che testa tutti i 4 endpoint verificando status code, content-type e payload.
5. **`Dockerfile`**: Configurazione del container basato su `eclipse-temurin:21-jre`, utente non-root `1001`, esposizione porta `8080` e avvio dell'uber-jar.
6. **`.env`**: File con le variabili di default per ambiente locale e container (`MESSAGE`, `APP_VERSION`, `PORT`).
7. **`README.md`**: Aggiornato con il nome dello studente nella sezione collaboratori.

---

## 3. Verifica e Test

### Esecuzione Test Unitari
Eseguendo:
```bash
./mvnw test
```
Tutti i **4 test** sono superati con esito positivo:
```text
[INFO] Running it.zucchetti.lab.AppResourceTest
[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 5.209 s -- in it.zucchetti.lab.AppResourceTest
[INFO] Results:
[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

### Build del pacchetto Uber-JAR
Eseguendo:
```bash
./mvnw package
```
Viene generato il file eseguibile:
`target/ufs12-devops-lab-agent-1.0.0-SNAPSHOT-runner.jar`

### Build ed Esecuzione Container Docker
```bash
# 1. Build immagine Docker
docker build -t mia-app:1.0 .

# 2. Avvio del container
docker run -d --name app -p 8080:8080 --env-file .env mia-app:1.0

# 3. Verifica endpoint
curl http://localhost:8080/info
curl http://localhost:8080/health
curl http://localhost:8080/

# 4. Verifica crash
curl -X POST http://localhost:8080/crash
docker ps -a   # Il container risulta "Exited (1)" come previsto per Kubernetes
```

