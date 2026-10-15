/*
 * Impostazioni dell'app Staff.
 * Il MENU (voci, prezzi, varianti, ricette) è in shared/menu.json e si modifica dall'app (sezione Menu).
 */
window.DEJAVU = {
  bar: {
    nome: "Déjà Vu",
    sottotitolo: "Draught Beer · Finest Cocktail",
    luogo: "Borgo Santa Caterina · Bergamo",
    logo: "../shared/logo-white.png",
    // foto che scorrono nella pagina iniziale
    foto: [
      "../shared/img/locale-1.jpg",
      "../shared/img/locale-2.jpg",
      "../shared/img/bar.jpg",
      "../shared/img/locale-3.jpg",
    ],
  },

  // Dove l'app pubblica le modifiche al menu (repository GitHub che ospita l'app)
  github: { owner: "4Kumiho", repo: "Dejavu", branch: "main", menuPath: "shared/menu.json" },
};
