# e-procurement-WS2Vigilanza
Servizio (web services) di intermediazione tra Appalti e VigilanzaComunicazioni.

Permette l'acquisizione dei dati degli eventi dei contratti da Appalti (o altre applicazioni esterne alla piattaforma).
Il set dei dati corrisponde a quelli richiesti da ANAC per ogni evento, ma limita i controlli al mininmo per consentire alle "applicazioni client" di riversare i dati disponibili, con il minimo dei vincoli.

Principali operazioni esposte dal servizio:
- Metodo IstanziaGara
- Metodo IstanziaAggiudicazione
- Metodo IstanziaEsito
- Metodo IstanziaContratto
- Metodo IstanziaInizio
- Metodo IstanziaAvanzamento
- Metodo IstanziaSospensione
- Metodo IstanziaVariante
- Metodo IstanziaSubappalto
- Metodo IstanziaConclusione
- Metodo IstanziaCollaudo
- Metodo IstanziaAvviso
- Metodo IstanziaPubblicazioneDocumenti
- Metodo IstanziaElencoImpreseInvitate