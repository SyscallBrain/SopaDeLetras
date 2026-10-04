#!/usr/bin/env python3
"""Validates assets/words/<lang>/<category>.json.

Fails the build on any violation: format, A-Z 3-12 letters, duplicates,
minimum counts, mystery/normal disjointness, length mix and blacklists
(brasileirismos/AO1990 in PT-PT, British spellings in EN-US).
"""
import json
import re
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent / "app" / "src" / "main" / "assets" / "words"
WORD_RE = re.compile(r"^[A-Z]{3,12}$")
LANGS = ["pt-PT", "en-US"]

BR_BLACKLIST = {
    "ONIBUS", "TREM", "CELULAR", "TELA", "SUCO", "TIME", "GOL", "MOCA",
    "GELADEIRA", "JACARE",
}
AO_BLACKLIST = {
    "ACAO", "ACCAO", "ATOR", "ACTOR", "FACTO", "FATO", "CONTACTO",
    "CONTATO", "OTIMO", "OPTIMO", "OTICA", "OPTICA",
}
EN_BLACKLIST = {
    "COLOUR", "HARBOUR", "GREY", "THEATRE", "FLAVOUR", "LORRY", "PAVEMENT",
}

errors: list[str] = []


def fail(message: str) -> None:
    errors.append(message)
    print(f"FAIL {message}")


def check_file(lang: str, path: Path) -> None:
    try:
        data = json.loads(path.read_text(encoding="utf-8"))
    except (OSError, json.JSONDecodeError) as exc:
        fail(f"{lang}/{path.name}: unreadable JSON ({exc})")
        return
    if data.get("id") != path.stem:
        fail(f"{lang}/{path.name}: id {data.get('id')!r} != filename")
    if not data.get("name"):
        fail(f"{lang}/{path.name}: missing display name")
    words = data.get("words", [])
    if len(words) < 80:
        fail(f"{lang}/{path.name}: {len(words)} normal words, need >= 80")
    seen: set[str] = set()
    for word in words:
        if not isinstance(word, str) or not WORD_RE.match(word):
            fail(f"{lang}/{path.name}: bad word {word!r} (want ^[A-Z]{{3,12}}$)")
            continue
        if word in seen:
            fail(f"{lang}/{path.name}: duplicate {word}")
        seen.add(word)
    short = sum(1 for w in words if isinstance(w, str) and 3 <= len(w) <= 5)
    mid = sum(1 for w in words if isinstance(w, str) and 6 <= len(w) <= 8)
    long = sum(1 for w in words if isinstance(w, str) and 9 <= len(w) <= 12)
    total = max(len(words), 1)
    if short / total < 0.20:
        fail(f"{lang}/{path.name}: short words {short}/{len(words)} < 20%")
    if mid / total < 0.40:
        fail(f"{lang}/{path.name}: mid words {mid}/{len(words)} < 40%")
    if long / total < 0.20:
        fail(f"{lang}/{path.name}: long words {long}/{len(words)} < 20%")
    blacklist = (BR_BLACKLIST | AO_BLACKLIST) if lang == "pt-PT" else EN_BLACKLIST
    for word in words:
        if word in blacklist:
            fail(f"{lang}/{path.name}: blacklisted {word}")
    mysteries = data.get("mystery", [])
    if len(mysteries) < 15:
        fail(f"{lang}/{path.name}: {len(mysteries)} mysteries, need >= 15")
    by_length: dict[int, int] = {}
    mystery_words: set[str] = set()
    for entry in mysteries:
        word = entry.get("word", "")
        clue = entry.get("clue", "")
        if not isinstance(word, str) or not re.match(r"^[A-Z]+$", word):
            fail(f"{lang}/{path.name}: bad mystery word {word!r}")
            continue
        if word in mystery_words:
            fail(f"{lang}/{path.name}: duplicate mystery {word}")
        mystery_words.add(word)
        by_length[len(word)] = by_length.get(len(word), 0) + 1
        if word in seen:
            fail(f"{lang}/{path.name}: mystery {word} also a normal word")
        if not clue or not clue.strip():
            fail(f"{lang}/{path.name}: mystery {word} has no clue")
    for length in (4, 5, 6, 7, 8):
        if by_length.get(length, 0) < 3:
            fail(f"{lang}/{path.name}: mysteries of length {length}: {by_length.get(length, 0)}, need >= 3")


def main() -> int:
    for lang in LANGS:
        directory = ROOT / lang
        if not directory.is_dir():
            fail(f"{lang}: missing directory")
            continue
        files = sorted(directory.glob("*.json"))
        if len(files) < 10:
            fail(f"{lang}: {len(files)} categories, need 10")
        for path in files:
            check_file(lang, path)
    if errors:
        print(f"validate_words: {len(errors)} problems")
        return 1
    print("validate_words: ok")
    return 0


if __name__ == "__main__":
    sys.exit(main())
