Gruppo composto da 3 componenti.Gruppo n° 19

L'idea del progetto è stata la realizzazione di un contatore intelligente, che decide quale dispositivo si possa accendere a seconda della quantità di potenza disponibile.
Nel nostro caso abbiamo preso come riferimento una casa domestica e inserito alcuni dei vari elettrodomestici possibili, con le loro rispettive potenze prendendone i riferimenti online.
Il contatore tiene traccia dei dispositivi connessi e li considera disconnessi se non inviano il messaggio di ALIVE per più di 10 secondi.
Il server invia periodicamente il messaggio di HELLO in multicast, per capire quali dispositivi vogliono connettersi alla rete.
Nello stesso messaggio di HELLO, il server invia anche la sua porta di connessione e la potenza massima disponibile.
Il client, una volta ricevuto il messaggio di HELLO e aver controllato che la sua potenza di accensione sia minore di quella disponibile,si connette e invia ogni secondo il messaggio di ALIVE unicast al server. 
In caso di conflitti tra client che cercano di accedere, ma simultaneamente causerebbero un blackout (perchè la potenza consumata diventerebbe maggiore di quella massima consentita), si è pensato di risolvere questa possibile situazione utilizzando il protocollo "CSMA".
Il protocollo "CSMA" è stato applicato in modo che se si verificasse la situazione precedente, al successivo messaggio di HELLO i device in questione verranno messi in uno stato wait e riavviati dopo un tempo che cresce esponenzialmente ai fallimenti e ai client che hanno causato il sovraccarico.
Nel frattempo nuovi dispositivi possono connettersi, sempre se la sua potenza non causa un sovraccarico.
Il codice è corredato di Javadoc ed è possibile modificare la porta del server e la potenza massima consentita modificando i valori nella classe "Constants" nel package "Commons".

Come prima operazione, avviare la ServerGUI. In seguito avviare la ClientGUI ogni qual volta si desidera instanziare un nuovo dispositivo.

Al momento dell'apertura della ClientGUI, si richiede di scegliere il dispositivo IOT che desidera collegarsi alla rete.
Una volta scelto, tramite la selezione del jRadioButton "ON", il dispositivo cerca di connettersi, ma dovrà rispettare le condizioni prima citate.
Sull'interfaccia lato server, si possono vedere i dispositivi connessi, e in caso del mancato invio da parte del client dei messaggi ALIVE, il server farà partire un contatore.
Se il contatore arriva a 10(=10 secondi) prima di ricevere un ALIVE da quello stesso dispositivo, il server lo considererà DISCONNECTED, e in caso in cui lo stesso client volesse riconnettersi, dovrà attendere un nuovo messaggio di HELLO da parte del server.
Nel caso in cui invece arrivasse il messaggio di ALIVE prima dello scadere dei 10 secondi del contatore, quest'ultimo verrà resettato.
Il client potrà anche spegnersi selezionando il jRadioButton "OFF" non inviando più i messaggi di ALIVE al server.
Come spiegato nel funzionamento del server, il client potrà ricollegarsi prima dei 10 secondi dall'ultimo ALIVE inviato, oppure dovrà aspettare un nuovo messaggio di HELLO da parte del server.
Nel caso in cui i client siano accesi, e il server si spegne, il client riconoscerà la mancanza del server dopo 20 secondi e si spegnerà anch'esso.

Nell'interfaccia del Client, una volta selezionato il dispositivo IOT da connettere, è possibile visualizzare le seguenti informazioni:
- indirizzo e porta del server, una volta ricevuto il messaggio di HELLO; 
- il tipo di dispositivo IOT scelto;
- lo stato del dispositivo;
- la conferma dell'invio dell'ALIVE message, una volta iniziata la comunicazione col server.

Nell'interfaccia del Server è possibile visualizzare le seguenti informazioni:
-potenza consumata e potenza totale;
-tipo di dispositivo e tempo dall'ultimo alive ricevuto, eventualmente "DISCONNECTED";
-il button "Refresh" che aggiorna asincronamente gli stati dei devices.


