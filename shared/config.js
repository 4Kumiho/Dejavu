/*
 * Impostazioni dell'app Staff.
 * Il MENU originale (voci, prezzi, varianti, ricette) è in shared/menu.json: si modifica dall'app (sezione Menu)
 * e le modifiche restano salvate sul dispositivo.
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

  // «Visualizza menu»: le pagine del menu cartaceo, in ordine (originali in Foto/Menu/)
  menuCartaceo: [
    "../shared/img/menu-cartaceo/pagina-01.jpg",
    "../shared/img/menu-cartaceo/pagina-02.jpg",
    "../shared/img/menu-cartaceo/pagina-03.jpg",
    "../shared/img/menu-cartaceo/pagina-04.jpg",
    "../shared/img/menu-cartaceo/pagina-05.jpg",
    "../shared/img/menu-cartaceo/pagina-06.jpg",
    "../shared/img/menu-cartaceo/pagina-07.jpg",
    "../shared/img/menu-cartaceo/pagina-08.jpg",
    "../shared/img/menu-cartaceo/pagina-09.jpg",
    "../shared/img/menu-cartaceo/pagina-10.jpg",
    "../shared/img/menu-cartaceo/pagina-11.jpg",
    "../shared/img/menu-cartaceo/pagina-12.jpg",
  ],

  // Indirizzo pubblico (GitHub Pages): l'app Android ci controlla se c'è un APK nuovo da scaricare
  online: "https://4kumiho.github.io/Dejavu/",
};
