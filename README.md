# Déjà Vu — Menu clienti & App staff

Due web-app che funzionano **offline** sull'iPad (dopo il primo caricamento) e salvano i dati nel browser.

| Cartella   | Cosa fa |
|------------|---------|
| `clienti/` | Menu per i clienti: foto/icona, descrizione e ingredienti (senza dosi) |
| `staff/`   | Ricette complete (dosi, bicchiere, metodo, passaggi) + gestione **tavoli** |
| `shared/`  | `drinks.js` (l'elenco drink, **unico file da modificare**), logo, colori, font |

## Modificare i drink
Apri `shared/drinks.js`: ogni drink ha nome, categoria, prezzo, descrizione, ingredienti con dosi e passaggi.
Per usare una foto: mettila in `shared/img/` e aggiungi al drink `img: "../shared/img/nome-foto.jpg"`.

## Indirizzi
| App | Indirizzo |
|-----|-----------|
| Menu clienti | https://4kumiho.github.io/Dejavu/clienti/ |
| Staff | https://4kumiho.github.io/Dejavu/staff/ |

Attenzione: la **D** di `Dejavu` è maiuscola e serve la `/` finale.
Non confonderli con `github.com/4Kumiho/Dejavu`: quella è la pagina del codice, non l'app.

## Attivare GitHub Pages (una volta sola)
1. Vai su https://github.com/4Kumiho/Dejavu/settings/pages
2. In **Build and deployment**: Source **Deploy from a branch**, Branch **`main`**, cartella **`/ (root)`**, poi **Save**.
3. Dopo 1–2 minuti in alto compare "Your site is live at https://4kumiho.github.io/Dejavu/".

Ogni modifica pubblicata su `main` va online da sola in 1–2 minuti. Gli iPad prendono la nuova versione
alla successiva apertura con internet (la vedono dalla seconda apertura).

## Installare sull'iPad
1. Apri in **Safari** l'indirizzo dell'app (es. https://4kumiho.github.io/Dejavu/staff/).
2. Aspetta che compaia l'app Déjà Vu (sfondo petrolio), poi tasto Condividi → **Aggiungi alla schermata Home**.
3. L'icona è il logo (quella dello staff ha la scritta "STAFF").

Da quel momento l'app funziona **anche senza internet**.
Usa **sempre l'icona** sulla schermata Home: i tavoli sono salvati dentro l'app installata
(Safari "normale" ha una memoria separata).

Se l'icona apre GitHub invece dell'app: eliminala (pressione lunga → Rimuovi app) e rifai i passaggi
con l'indirizzo `github.io` giusto.

## Aprire l'app sul PC (per provarla)
- **Doppio clic** su `staff/index.html` (o `clienti/index.html`) e aprilo con Chrome o Edge: funziona tutto, tavoli compresi.
- Oppure, più simile all'iPad, dal terminale nella cartella del progetto:
  ```
  python -m http.server 8000
  ```
  e apri http://localhost:8000/staff/ (`Ctrl+C` per fermarlo).

I tavoli salvati con il doppio clic e quelli con `localhost` sono separati (per il browser sono due siti diversi).

## Note
- I tavoli restano salvati anche chiudendo l'app o spegnendo l'iPad; non si sincronizzano tra iPad diversi.
- Cancellare i dati di Safari / disinstallare l'icona cancella i tavoli aperti.
