"""Crea il video per le storie Instagram di un evento.

Uso (dalla cartella Eventi):
    python video.py "Capodanno Cinese"
    python video.py "Capodanno Cinese" --anteprima 1 3.5 6 9.5   (solo fotogrammi PNG, per controllare)

Legge <evento>/sorgente/video.html, lo registra fotogramma per fotogramma con Edge
e salva <evento>/Instagram video storia.mp4 (1080x1920, 30 fotogrammi al secondo).

Serve una volta sola:  pip install playwright imageio-ffmpeg
"""
import argparse
import subprocess
import sys
from pathlib import Path

import imageio_ffmpeg
from playwright.sync_api import sync_playwright

FPS = 30
DURATA = 10  # secondi


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("evento")
    ap.add_argument("--anteprima", nargs="*", type=float)
    ap.add_argument("--uscita", help="cartella per le anteprime")
    args = ap.parse_args()

    cartella = Path(__file__).resolve().parent / args.evento
    pagina_html = cartella / "sorgente" / "video.html"
    if not pagina_html.exists():
        sys.exit(f"Non trovo {pagina_html}")

    with sync_playwright() as p:
        browser = p.chromium.launch(channel="msedge")
        pagina = browser.new_page(viewport={"width": 1080, "height": 1920}, device_scale_factor=1)
        pagina.goto(pagina_html.as_uri())
        pagina.evaluate("document.fonts.ready")
        pagina.wait_for_timeout(300)
        tela = pagina.locator(".tela")

        if args.anteprima is not None:
            uscita = Path(args.uscita) if args.uscita else cartella
            for t in args.anteprima:
                pagina.evaluate(f"vai({t})")
                file = uscita / f"anteprima-{t:04.1f}.png"
                tela.screenshot(path=str(file))
                print(file)
            browser.close()
            return

        mp4 = cartella / "Instagram video storia.mp4"
        ffmpeg = subprocess.Popen(
            [imageio_ffmpeg.get_ffmpeg_exe(), "-y", "-loglevel", "error",
             "-f", "image2pipe", "-framerate", str(FPS), "-c:v", "png", "-i", "-",
             "-c:v", "libx264", "-preset", "slow", "-crf", "17", "-pix_fmt", "yuv420p",
             "-movflags", "+faststart", str(mp4)],
            stdin=subprocess.PIPE,
        )
        totale = FPS * DURATA
        for i in range(totale):
            pagina.evaluate(f"vai({i / FPS})")
            ffmpeg.stdin.write(tela.screenshot(type="png"))
            if i % FPS == 0:
                print(f"  secondo {i // FPS}/{DURATA}", flush=True)
        ffmpeg.stdin.close()
        ffmpeg.wait()
        browser.close()
        print(mp4)


if __name__ == "__main__":
    main()
