# CODING SOCIALE

## Progetto realizzato da:

- Rayan2426
- YvingLore
- bernatScolastico

---
 ### *FUNZIONAMENTO DEL CLIENT*

1 Il **Client** come prima cosa prende l'*indirizzo IP* del **Server** (inserito dall'utente), se l'indirizzo esiste o è disponibile allora il **Client** si connette al **Server**.

2 *Dopo l'avvenuta connessione*, il **Server** invierà la lista dei Client presenti nella connessione.

3 Il **Client** prima di inviare i messaggi in privato o in broadcast dovrà prima *scegliere un nome utente valido*.

4 I **Client** possono comunicare ininterrottamente.

5 Quando il **Client** invierà una stringa del formato */close* esso verrà *disconnesso* dalla rete.

----
### *PROTOCOLLI*
*Tutti i comandi del protocollo devono essere utilizzati nel terminale solo e soltanto dopo aver instaurato una connessione con un server.

- Il Server utilizza il formato **“@USERNAME 'messaggio'”** per inviare un messaggio privato ad un terminale, altrimenti verrà inviato in broadcast a tutti i terminali connessi;
- Digitare **"/list"** per ottenere la lista di indirizzi ip disponibili connessi al Server.
- Digitare **"/help"** per visualizzare la documentazione di tutti i protocolli.
- dIGITARE **"/close"** per chiudere la connnessione, il Server invierà a tutti i Client il messaggio di disconnessione del Client con il formato *" 'NomeClient' si è disconnesso dalla chat"*. 
