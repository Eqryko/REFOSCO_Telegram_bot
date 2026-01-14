# 🏈 MantisNFL – Telegram Bot NFL

MantisNFL è un bot Telegram sviluppato in **Java** che fornisce informazioni
sul campionato **NFL (National Football League)** tramite una API esterna.
Il bot permette di consultare risultati, salvare dati, tracciare statistiche
di utilizzo e mantenere uno stato persistente grazie a un database SQLite.

Il progetto è stato realizzato come esercizio didattico per la materia **TPSIT**
e rispetta i requisiti di utilizzo di API, database e gestione sicura
delle configurazioni.

---

## Funzionalità principali

- Consultazione risultati NFL aggiornati
- Consultazione giocatori squadre NFL
- Memorizzazione utenti Telegram
- Salvataggio partite nel database
- Statistiche di utilizzo dei comandi
- Stato persistente (SQLite)
- Output pulito e leggibile (no JSON grezzo)

---

## API utilizzata

### ESPN – American Football API

- **Base URL**:  
  https://site.api.espn.com/apis/site/v2/sports/football/nfl

- **Documentazione **:  
  https://github.com/pseudo-r/Public-ESPN-API

### Autenticazione
Non è richiesta una API key.

---

## 🛠 Setup del progetto

### 🔹 Requisiti
- Java JDK 21
- Maven
- IntelliJ IDEA (consigliato)
- Account Telegram

---

### 🔹 Installazione dipendenze
Le dipendenze vengono gestite automaticamente tramite **Maven**.

Assicurarsi che IntelliJ carichi correttamente il file `pom.xml`.

---

### 🔹 Configurazione Bot
Avviare la classe Main

Aprire Telegram

Cercare il bot tramite username

Inviare il comando /start

Comandi disponibili
/start	Avvia il bot e registra l’utente
/results	Mostra le ultime partite NFL
/lastgames	Mostra le ultime partite salvate
/stats	Statistiche di utilizzo del bot
/roster
/teams
/team
/help	Lista comandi disponibili

Database SQLite

Il progetto utilizza SQLite per garantire persistenza dei dati.




## Configurazione

1. Rinominare `config.properties.example` in `config.properties`
2. Inserire la propria API key Highlightly
3. Avviare il progetto
