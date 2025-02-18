const CACHE_NAME = "lifeai-cache-v1";
const urlsToCache = ["/", "error.html", "info.html", "home.html", "/limfedem.html", "/css/style.css", "/js/script.js"];

self.addEventListener("install", (event) => {
  event.waitUntil(
    caches.open(CACHE_NAME).then((cache) => {
      return cache.addAll(urlsToCache);
    })
  );
});

self.addEventListener("fetch", (event) => {
  event.respondWith(
    caches.match(event.request).then((response) => {
      return response || fetch(event.request);
    })
  );
});
