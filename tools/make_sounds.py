#!/usr/bin/env python3
"""Generates the app's own soft sounds.

Sine waves with exponential decay, 44.1 kHz mono 16-bit WAV:
  tick.wav  single low blip, ~35 ms, quiet
  found.wav two ascending notes, ~250 ms
  win.wav   three-note chord, ~700 ms
No licensed material: everything is synthesized here.
"""
import math
import struct
import wave
from pathlib import Path

RATE = 44100
OUT = Path(__file__).resolve().parent.parent / "app" / "src" / "main" / "res" / "raw"


def tone(freq: float, seconds: float, volume: float = 0.5, delay: float = 0.0) -> list[float]:
    start = int(RATE * delay)
    count = int(RATE * seconds)
    out = [0.0] * (start + count)
    for i in range(count):
        t = i / RATE
        env = math.exp(-4.0 * i / count)
        out[start + i] += volume * env * math.sin(2 * math.pi * freq * t)
    return out


def mix(*parts: list[float]) -> list[float]:
    length = max(len(part) for part in parts)
    out = [0.0] * length
    for part in parts:
        for i, sample in enumerate(part):
            out[i] += sample
    peak = max(1e-6, max(abs(sample) for sample in out))
    return [sample / peak * 0.9 for sample in out]


def write(name: str, samples: list[float]) -> None:
    OUT.mkdir(parents=True, exist_ok=True)
    path = OUT / name
    with wave.open(str(path), "wb") as wav:
        wav.setnchannels(1)
        wav.setsampwidth(2)
        wav.setframerate(RATE)
        wav.writeframes(b"".join(struct.pack("<h", int(max(-1.0, min(1.0, s)) * 32767)) for s in samples))
    seconds = len(samples) / RATE
    print(f"{name}: {seconds * 1000:.0f} ms, {path.stat().st_size} bytes")


def main() -> None:
    write("tick.wav", mix(tone(330.0, 0.035, volume=0.35)))
    write("found.wav", mix(tone(392.0, 0.16), tone(523.25, 0.16, delay=0.09)))
    write("win.wav", mix(tone(261.63, 0.7), tone(329.63, 0.7), tone(392.0, 0.7)))


if __name__ == "__main__":
    main()
