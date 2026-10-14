# Prompt del Lab 3

Scrivi qui il prompt per l'agent, seguendo la guida in `docs/PROMPT.md` e i requisiti in `docs/REQUISITI.md`.
Poi lo committi insieme al codice.

Autori: Michele Albanesi

## Contesto
Progetto Java con Quarkus 3, compilato per Java 17 (compatibile anche con Java 21 runtime), nel package `it.zucchetti.lab`. Il progetto include già le dipendenze REST (`quarkus-rest`), Jackson (`quarkus-rest-jackson`) e OpenAPI (`quarkus-smallrye-openapi`). L'applicazione verrà eseguita all'interno di un container Linux e successivamente orchestrata su Kubernetes (Giorno 2).

## Obiettivo
Implementare una REST web application con Quarkus che soddisfi integralmente tutti e 7 i requisiti funzionali e operativi definiti in `docs/REQUISITI.md`, pronta per essere pacchettizzata come singolo uber-jar e distribuita tramite Dockerfile.

## Requisiti
1. **GET /**: risponde con codice 200 e `Content-Type: text/html; charset=UTF-8`, restituendo una pagina HTML ben formattata che mostra a video il messaggio (`MESSAGE`), l'hostname del nodo/container e la versione (`APP_VERSION`).
2. **GET /info**: risponde con codice 200 e `Content-Type: application/json`, restituendo un JSON con i campi `hostname` (stringa con il nome dell'host/container), `version` (stringa versione corrente) e `startedAt` (data e ora di avvio dell'app in formato standard ISO-8601).
3. **GET /health**: risponde con codice HTTP 200 e `Content-Type: application/json` con payload `{"status":"ok"}` per fungere da liveness e readiness probe di Kubernetes.
4. **POST /crash**: risponde con codice HTTP 202 Accepted e `Content-Type: application/json` con payload `{"status":"crashing"}` e subito dopo (in modo asincrono, dopo aver inviato la risposta HTTP) provoca lo spegnimento dell'applicazione tramite `System.exit(1)`, condizionato al fatto che la proprietà `app.crash.exit` sia `true`.
5. **Configurazione messaggio e versione**: i valori del messaggio e della versione devono essere configurabili tramite le variabili d'ambiente `MESSAGE` e `APP_VERSION`, mappate in `src/main/resources/application.properties` con rispettivi valori di default (`MESSAGE` default "Ciao dalla mia app!" e `APP_VERSION` default "1.0").
6. **Porta e interfaccia di rete**: la porta HTTP di ascolto deve essere configurabile tramite la variabile d'ambiente `PORT` (con default `8080`) e l'applicazione deve ascoltare su `0.0.0.0` (`quarkus.http.host=0.0.0.0`), in modo da essere raggiungibile dall'esterno del container.
7. **Pacchettizzazione uber-jar e stateless**: la build deve produrre un unico uber-jar eseguibile (`target/*-runner.jar`), l'applicazione non deve utilizzare alcun database, non deve scrivere file su disco e non deve conservare stato mutabile in memoria (a parte il timestamp di avvio `startedAt`).

## Vincoli
- Utilizzare esclusivamente il package `it.zucchetti.lab`.
- Codice compatibile con Java 17 (nessuna feature successiva a Java 17).
- Nessuna dipendenza esterna o libreria aggiuntiva oltre a quelle già presenti in `pom.xml`.
- Nessun database, nessun file scritto su disco o stato persistente.
- Durante i test automatici `%test.app.crash.exit=false` deve disabilitare `System.exit` per evitare di interrompere la JVM durante la suite di test.
- Una sola classe risorsa REST (ad esempio `AppResource.java`) snella e chiara.
- Non includere password, token o credenziali nel codice o nella configurazione.

## Verifica
- Scrivere i test unitari con `@QuarkusTest` e RestAssured nella classe `AppResourceTest.java`:
  - Test per `GET /`: verifica stato 200, Content-Type HTML e presenza di messaggio, hostname e versione nel corpo della risposta.
  - Test per `GET /info`: verifica stato 200, Content-Type JSON e presenza dei campi `hostname`, `version` e `startedAt`.
  - Test per `GET /health`: verifica stato 200 e JSON `{"status":"ok"}`.
  - Test per `POST /crash`: verifica stato 202 e JSON `{"status":"crashing"}` senza provocare l'uscita dalla JVM.
- Eseguire `./mvnw test` e verificare che tutti i 4 test risultino verdi.
- Eseguire `./mvnw package` e verificare la presenza del file uber-jar `target/ufs12-devops-lab-agent-1.0.0-SNAPSHOT-runner.jar`.

## Formato della risposta
- Elenco puntato di tutti i file creati o modificati.
- Due o tre frasi di spiegazione per ogni file, descrivendone il ruolo e l'implementazione.
- Report dell'esito dell'esecuzione di `./mvnw test` e `./mvnw package`.

---

## Versioni successive

- **v1.0 (iniziale)**: Definito il prompt completo coprendo tutti e 7 i requisiti funzionali e operativi, i vincoli architetturali e la suite di test richiesta.
- **v1.1 (correzione template HTML)**: Risolto il problema di formattazione della pagina HTML per `GET /` sostituendo la formattazione a template `String.formatted` con `.replace()` per evitare conflitti con i simboli `%` presenti nelle regole CSS. Tutti i test ora passano al 100%.
