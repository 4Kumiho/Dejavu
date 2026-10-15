# Déjà Vu

## 👉 Installa l'app sull'iPad

1. Apri il link con **Safari**: **[https://4kumiho.github.io/Dejavu/staff/](https://4kumiho.github.io/Dejavu/staff/)**
2. Tocca il tasto **Condividi** (il quadrato con la freccia verso l'alto).
3. Tocca **Aggiungi alla schermata Home**.

Fatto. Da ora apri l'app **sempre dall'icona**: funziona anche senza internet e i tavoli restano salvati sull'iPad.

---

## Per chi gestisce il progetto

### Cosa c'è dentro
| Cartella   | Cosa fa |
|------------|---------|
| `staff/`   | L'app: pagina iniziale, gestione **tavoli**, ricette complete (dosi, bicchiere, metodo, passaggi) |
| `shared/`  | `drinks.js` (l'elenco drink, **unico file da modificare**), logo, foto del locale, colori, font |

### Modificare il menu
Apri `shared/drinks.js`: ogni voce ha nome, categoria, prezzo ed eventuali **varianti** (es. gusti dello spritz, gin, misure della birra).
I cocktail hanno anche ingredienti con dosi e passaggi e compaiono nella sezione Ricette.
Le foto del menu originale sono in `Foto/Menu/`.
Per usare una foto: mettila in `shared/img/` e aggiungi al drink `img: "../shared/img/nome-foto.jpg"`.
La foto della pagina iniziale è `shared/img/bar.jpg`: sostituiscila con un file con lo stesso nome per cambiarla.

Ogni modifica pubblicata su `main` va online da sola in 1–2 minuti. Gli iPad prendono la nuova versione
alla successiva apertura con internet (la vedono dalla seconda apertura).

### Attivare GitHub Pages (una volta sola, dal proprietario del repository)
Serve perché i link qui sopra funzionino.
1. Vai su https://github.com/4Kumiho/Dejavu/settings/pages
2. In **Build and deployment**: Source **Deploy from a branch**, Branch **`main`**, cartella **`/ (root)`**, poi **Save**.
3. Dopo 1–2 minuti in alto compare "Your site is live at https://4kumiho.github.io/Dejavu/".

Per avere una propria copia (es. con altri drink): **Fork** del repository e attivare GitHub Pages sul fork.

### Aprire l'app sul PC (per provarla)
- **Doppio clic** su `staff/index.html` e aprilo con Chrome o Edge.
- Oppure, più simile all'iPad: `python -m http.server 8000` nella cartella e apri http://localhost:8000/staff/.

### Note
- I tavoli non si sincronizzano tra iPad diversi.
- Rimuovere l'icona o cancellare i dati di Safari cancella i tavoli aperti.
- Se l'icona apre GitHub invece dell'app: rimuovila e rifai l'installazione dal link `github.io`.
