# Hero tussenversie, zonder formulier (1a en 1b)

Tussenstap vóór de postcodevariant: de hero heeft **geen invoerveld**. Er staat alleen een knop die doorlinkt naar de bestaande formulierpagina. Alles verder volgens de overdracht (teksten, kleuren, typografie, afstanden, responsief).

- `preview.html`: beide varianten, openen in een browser
- `screenshot-desktop.png` / `screenshot-mobiel.png`: weergave op 1440px en 390px

## Wat verschilt van de overdracht

| Onderdeel | Overdracht | Tussenversie |
|---|---|---|
| Insteek | Besparen ("tot 50%", prijzen vergelijken) | Gemak, eenvoud, snelheid |
| 1a formulierkaart | Label + postcodeveld + knop | Alleen knop `Start uw aanvraag →` (58px) + trust-rij |
| 1a offertekaart | 4 fictieve prijzen | Kaart "Zo eenvoudig werkt het" in dezelfde stijl (3 stappen) |
| 1b formulierkaart | Titel + postcodeveld + knop + regel | Titel + korte zin + knop `Ontvang gratis 4 offertes →` (60px) + regel |
| Formidable | `[get param="postcode"]` instellen | **Niets aanpassen**, het formulier blijft zoals het is |
| HTML-widget | Nodig voor postcodeveld | Niet nodig, gewone Elementor **Button**-widget |
| Event | `hero_postcode_submit` | `hero_cta_click`, parameter `variant` = `1a` / `1b` |

Nieuwe tekst in 1b (vervangt het postcodeveld, graag bevestigen):
> Beantwoord een paar korte vragen over de woning en ontvang tot 4 offertes van ontruimers bij u in de buurt.

## Teksten (gewijzigd t.o.v. de overdracht)

**H1 (beide):** Uw woningontruiming eenvoudig geregeld: binnen 24 uur 4 offertes

**Intro 1a:** Eén korte aanvraag en u ontvangt binnen 24 uur offertes van gecontroleerde ontruimers uit uw regio. Geen gedoe, geen rondbellen.

**Intro 1b:** Eén aanvraag, binnen 24 uur tot 4 offertes van gecontroleerde ontruimers uit uw regio. U kiest zelf — of niet.

**USP 1 in 1b:** Eén aanvraag in plaats van zelf rondbellen (de andere twee blijven)

**Stappenkaart 1a** (vervangt de offertekaart, zelfde opmaak: rij 1 groen omrand, nummer in het grijze vierkant, rechts vet de timing):
- Kop: `Zo eenvoudig werkt het` + rechts `3 stappen`
- 1 · Vul uw aanvraag in · Een paar korte vragen over de woning · **Vandaag**
- 2 · Ontvang tot 4 offertes · Van ontruimers uit uw regio · **Binnen 24 uur**
- 3 · U kiest de ontruimer · Of niet, u zit nergens aan vast · **Vrijblijvend**
- Voetnoot: `Eén aanvraag, wij zetten hem uit bij ontruimers in uw regio.` / `Gratis, en u hoeft zelf niemand te zoeken.`

## Bouwen in Elementor

1. Dupliceer de homepage twee keer: `Homepage – test hero 1a` (`test-hero-1a`) en `Homepage – test hero 1b` (`test-hero-1b`). Status **Concept**, `noindex`.
2. Vervang alleen de hero-sectie. Header, footer, de secties eronder en het formulier blijven ongemoeid.
3. Knop = **Button**-widget:
   - Link: `/offerte-formulier/`
   - Achtergrond `#00C75D`, tekst `#1C2426` (niet wit), Poppins 700 17px, radius 10px, volle breedte
   - Hoogte: 58px (1a) / 60px (1b), mobiel minimaal 56px
   - Hover: iets donkerder (`#00B354`)
   - Geavanceerd → CSS-classes: `ov-hero-cta ov-hero-cta-1a` resp. `ov-hero-cta ov-hero-cta-1b`
4. Trust-items (1a) en de regel onder de knop (1b): Text Editor-widget, 500 14px `#4A4F59`, groene `✓` (800, `#00C75D`).
5. De overige onderdelen (badge, foto, balk, proof-rij) bouw je exact zoals in de overdracht; H1, intro's, USP's en de stappenkaart met de teksten hierboven.

## Meten (GTM)

Trigger: *Klik – Alleen links*, voorwaarde `Click Classes` bevat `ov-hero-cta`.
GA4-event `hero_cta_click` met parameter `variant`: `1a` als `Click Classes` `ov-hero-cta-1a` bevat, anders `1b` (lookup-variabele).
Zonder GTM kan ook dit script in een HTML-widget onderaan de hero:

```html
<script>
document.querySelectorAll('.ov-hero-cta a, a.ov-hero-cta').forEach(function(a){
  a.addEventListener('click',function(){
    var v=a.closest('.ov-hero-cta-1a')||a.classList.contains('ov-hero-cta-1a')?'1a':'1b';
    window.dataLayer=window.dataLayer||[];
    window.dataLayer.push({event:'hero_cta_click',variant:v});
  });
});
</script>
```

## Open punten

- 1b mobiel: de formulierkaart staat nu ónder de USP's en proof-rij. Wil je de knop hoger hebben, zet de kaart in Elementor dan op mobiel op volgorde 2 (direct na de intro).
