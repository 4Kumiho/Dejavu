// Rende l'app utilizzabile offline: mostra subito la copia salvata
// e in background scarica l'eventuale versione aggiornata.
const CACHE = "dejavu-staff-v7";
const FILES = [
  "./", "./index.html", "./manifest.json", "./icon-180.png", "./icon-192.png",
  "../shared/drinks.js", "../shared/brand.css", "../shared/logo-white.png", "../shared/img/bar.jpg",
  "../shared/img/locale-1.jpg", "../shared/img/locale-2.jpg", "../shared/img/locale-3.jpg",
  "../shared/fonts/montserrat.woff2", "../shared/fonts/cormorant-500.woff2", "../shared/fonts/cormorant-600.woff2",
];

self.addEventListener("install", (e) => {
  e.waitUntil(caches.open(CACHE).then((c) => c.addAll(FILES)).then(() => self.skipWaiting()));
});

self.addEventListener("activate", (e) => {
  e.waitUntil(
    caches.keys()
      .then((keys) => Promise.all(keys.filter((k) => k.startsWith("dejavu-staff-") && k !== CACHE).map((k) => caches.delete(k))))
      .then(() => self.clients.claim())
  );
});

self.addEventListener("fetch", (e) => {
  if (e.request.method !== "GET" || new URL(e.request.url).origin !== location.origin) return;
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
