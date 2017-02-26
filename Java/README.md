** File JAVA<br />

NB: Il progetto funziona solo con NetBeans (causa problemi di librerie con Eclipse).<br /><br />

Funzionamento:<br />
1) Client legge valori rilevazione da Arduino.<br />
2) Client serializza ArrayList di rilevazioni.<br />
3) Client spedisce ArrayList serializzata a Server tramite Socket.<br />
4) Server riceve ArrayList serializzata tramite Socket.<br />
5) Server deserializza ArrayList.<br />
6) Server scrive valori ricevuti su file .txt.<br />
