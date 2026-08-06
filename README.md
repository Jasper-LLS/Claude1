# Eredivisie Ranking

Een simpele Android-app om samen (bijv. met je zoon) een ranking van de 18
Eredivisie-clubs te maken en na elke speelronde de tussenstand te zien.

## Hoe het werkt

1. **Spelers** — beheer de deelnemers (standaard "Jasper" en "Zoon").
2. **Teams beheren** — de app start met een standaardlijst van 18 clubs.
   Controleer en corrigeer deze lijst vóór het seizoen begint, want de
   samenstelling van de Eredivisie kan wijzigen door promotie/degradatie.
3. **Voorspellingen** — elke speler zet de 18 teams in de gewenste volgorde
   (positie 1 = kampioen, positie 18 = laatste) met de pijltjes-knoppen.
4. **Speelronde invoeren** — na elke speelronde vul je de *echte* actuele
   Eredivisie-ranglijst in (zelfde pijltjes-interface).
5. **Tussenstand** — de app berekent per speler en per ronde de score:
   - Team op de exact juiste positie voorspeld: **3 punten**
   - Team één plek ernaast voorspeld: **1 punt**
   - Anders: 0 punten

   Alle rondes worden opgeteld tot een lopend klassement.

Alle data wordt lokaal op het toestel opgeslagen (geen internet of account
nodig).

## Zelf bouwen

```bash
export ANDROID_HOME=/pad/naar/android-sdk
gradle assembleDebug
```

De APK komt in `app/build/outputs/apk/debug/app-debug.apk`.

## Installeren op Android

1. Zet de APK op de telefoon (bijv. via een gedeelde link, e-mail of USB).
2. Tik de APK aan om te installeren. Android vraagt mogelijk om installatie
   vanuit "onbekende bron" toe te staan — dat is nodig omdat de app niet via
   de Play Store gaat.
3. Open de app en begin met het instellen van teams, spelers en
   voorspellingen.
