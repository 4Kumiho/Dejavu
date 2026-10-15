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

## Metterle online (serve una volta sola)
Per funzionare offline le pagine devono essere aperte da un indirizzo `https://`. Il modo più semplice e gratuito:
1. vai su https://app.netlify.com/drop
2. trascina l'intera cartella `Dejavu`
3. ottieni un indirizzo tipo `https://nome.netlify.app`
   - menu clienti: `https://nome.netlify.app/clienti/`
   - staff: `https://nome.netlify.app/staff/`

(In alternativa GitHub Pages.) Dopo aver modificato i drink, ritrascina la cartella: gli iPad prendono
la nuova versione alla successiva apertura con internet (alla seconda apertura la vedono).

## Installare sull'iPad
Apri l'indirizzo in **Safari** → tasto Condividi → **Aggiungi alla schermata Home**.
Usa **sempre l'icona** sulla schermata Home: i tavoli sono salvati dentro l'app installata
(Safari "normale" ha una memoria separata).

## Note
- I tavoli restano salvati anche chiudendo l'app o spegnendo l'iPad; non si sincronizzano tra iPad diversi.
- Cancellare i dati di Safari / disinstallare l'icona cancella i tavoli aperti.
- Prova in locale sul PC: `python -m http.server` nella cartella e apri `http://localhost:8000/staff/`.
