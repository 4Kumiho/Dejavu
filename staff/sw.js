// Rende l'app utilizzabile offline.
// - menu.json: prima dalla rete (per avere sempre il menu più recente), se offline dalla copia salvata
// - tutto il resto: subito dalla copia salvata, e in background scarica l'eventuale versione aggiornata
// Cambiando CACHE, gli iPad scaricano la nuova versione dell'app e mostrano "Aggiorna".
const CACHE = "dejavu-staff-v20";
const FILES = [
  "./", "./index.html", "./manifest.json", "./icon-180.png", "./icon-192.png",
  "../shared/config.js", "../shared/menu.json", "../shared/brand.css", "../shared/logo-white.png",
  "../shared/img/bar.jpg", "../shared/img/locale-1.jpg", "../shared/img/locale-2.jpg", "../shared/img/locale-3.jpg",
  "../shared/fonts/montserrat.woff2", "../shared/fonts/cormorant-500.woff2", "../shared/fonts/cormorant-600.woff2",
  ...Array.from({ length: 12 }, (_, i) => `../shared/img/menu-cartaceo/pagina-${String(i + 1).padStart(2, "0")}.jpg`),
];

self.addEventListener("install", (e) => {
  e.waitUntil(
    caches.open(CACHE)
      .then((c) => c.addAll(FILES.map((u) => new Request(u, { cache: "reload" }))))
      .then(() => self.skipWaiting())
  );
});

self.addEventListener("activate", (e) => {
  e.waitUntil(
    caches.keys()
      .then((keys) => Promise.all(keys.filter((k) => k.startsWith("dejavu-staff-") && k !== CACHE).map((k) => caches.delete(k))))
      .then(() => self.clients.claim())
  );
});

self.addEventListener("fetch", (e) => {
  const url = new URL(e.request.url);
  if (e.request.method !== "GET" || url.origin !== location.origin) return;

  if (url.pathname.endsWith("/menu.json")) {
    const chiave = url.origin + url.pathname;
    e.respondWith(
      caches.open(CACHE).then((cache) =>
        fetch(e.request, { cache: "no-store" })
          .then((res) => { if (res.ok) cache.put(chiave, res.clone()); return res; })
          .catch(() => cache.match(chiave))
      )
    );
    return;
  }

  e.respondWith(
    caches.open(CACHE).then(async (cache) => {
      const cached = await cache.match(e.request, { ignoreSearch: true });
      const network = fetch(e.request)
        .then((res) => {
          if (res.ok) cache.put(e.request, res.clone());
          return res;
        })
        .catch(() => cached);
      return cached || network;
    })
  );
});
