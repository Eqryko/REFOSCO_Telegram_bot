# 🏈 MantisNFL – Telegram Bot NFL

MantisNFL è un bot Telegram sviluppato in **Java** che fornisce informazioni
sul campionato **NFL (National Football League)** tramite una API esterna.
Il bot permette di consultare risultati, salvare dati, tracciare statistiche
di utilizzo e mantenere uno stato persistente grazie a un database SQLite.

Il progetto è stato realizzato come esercizio didattico per la materia **TPSIT**
e rispetta i requisiti di utilizzo di API REST, database e gestione sicura
delle configurazioni.

---

## Funzionalità principali

- Consultazione risultati NFL aggiornati
- Memorizzazione utenti Telegram
- Salvataggio partite nel database
- Statistiche di utilizzo dei comandi
- Stato persistente (SQLite)
- Output pulito e leggibile (no JSON grezzo)
- Configurazione sicura tramite file esterno

---

## API utilizzata

### Highlightly – American Football API

- **Base URL**:  
  https://american-football.highlightly.net

- **Documentazione ufficiale**:  
  https://highlightly.net/documentation/american-football/

### Autenticazione
Le richieste API richiedono il seguente header:
x-rapidapi-key: YOUR_API_KEY


⚠️ L’API key **NON è inclusa nel repository** per motivi di sicurezza.

---

## 🛠 Setup del progetto

### 🔹 Requisiti
- Java JDK 21
- Maven
- IntelliJ IDEA (consigliato)
- Account Telegram
- API Key Highlightly

---

### 🔹 Installazione dipendenze
Le dipendenze vengono gestite automaticamente tramite **Maven**.

Assicurarsi che IntelliJ carichi correttamente il file `pom.xml`.

---

### 🔹 Configurazione API Key

1. Creare un file `config.properties` nella root del progetto
2. Inserire al suo interno:

```properties
highlightly.api.key=INSERISCI_LA_TUA_API_KEY
```
3. Il file è escluso dal versionamento tramite .gitignore

File di esempio fornito

config.properties.example

highlightly.api.key=YOUR_API_KEY_HERE

🔹 Avvio del bot

Avviare la classe Main

Aprire Telegram

Cercare il bot tramite username

Inviare il comando /start

Comandi disponibili
Comando	Descrizione
/start	Avvia il bot e registra l’utente
/results	Mostra le ultime partite NFL
/lastgames	Mostra le ultime partite salvate
/stats	Statistiche di utilizzo del bot
/help	Lista comandi disponibili

Database SQLite

Il progetto utilizza SQLite per garantire persistenza dei dati.




## Configurazione

1. Rinominare `config.properties.example` in `config.properties`
2. Inserire la propria API key Highlightly
3. Avviare il progetto
