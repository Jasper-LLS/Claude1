# Claude1

## `/transcribeer` skill

Haalt de kennis van een YouTube-kanaal binnen als tekst: pakt de laatste video's van een
kanaal of playlist en haalt van elke video de ondertitels op (snel, gratis, geen download),
en kan er optioneel een kennis-skill van bouwen die werkt volgens de principes van die expert.

Zie [`transcribeer/SKILL.md`](transcribeer/SKILL.md) voor de volledige documentatie.

**Installeren:** kopieer de map `transcribeer/` naar `~/.claude/skills/transcribeer/` en
draai `uv run ~/.claude/skills/transcribeer/transcribeer.py "<YOUTUBE-URL>"` (vereist alleen
Python 3 en [`uv`](https://astral.sh/uv), die de rest van de afhankelijkheden zelf regelt).