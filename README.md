# ChefGPT
ChefGPT e' un applicazione Android che permette di riconoscere una vasta gamma di cibi americani e di generarne una ricetta semplificata. L'app utilizza due diversi modelli di machine learning per effettuare i task:

### Aiy - Classificazione del cibo
Aiy e' un modello di classificazione di immagini, permette di riconoscere 2023 diverse pietanze da immagini in input.

### Gemma3 - Natural Language Processing
Figlio della classe di modelli Gemini, gemma3 e' utilizzato per l'inferenza on-device grazie alle modeste dimensioni e alla sua efficienza. In ChefGPT viene utilizzato in combinazione con il classificatore di cibo per generare la ricetta rispettiva.

Entrambi i modelli sono disponibili su kaggle: [Aiy](https://www.kaggle.com/models/google/aiy), [Gemma3](https://www.kaggle.com/models/google/gemma-3).

Per la gestione dei modelli di intelligenza artificiale sono state utilizzate le seguenti librerie esterne:
- MediaPipe 0.10.22
- LiteRT (TensorFlow Lite) 2.12.0
- Markwon 4.6.2
- Glide 4.16.0

# Prima di partire

Per poter utilizzare la generazione della ricetta occorre disporre del modello di Natural Language Processing "gemma3" all'interno del vostro dispositivo, mentre il classificatore di pietanze e' gia presente nel pacchetto apk. 
Di seguito trovate le istruzioni per installare il modello di generazione testuale.

## Note

Per poter installare modelli in locale su Android e' necessario disporre di Android-Sdk, solitamente installata tramite Android Studio nel path di default: `C:\Users\USERNAME\AppData\Local\Android\Sdk\platform-tools`.

La sintassi dei comandi di adb potrebbe variare in base alla shell utilizzata; ad esempio per Windows PowerShell occorre utilizzare `./adb`, per CMD utilizzare `adb`.

## Istruzioni

1) Scaricare il modello dal seguente [link](https://drive.google.com/file/d/1lVjhUcZFg2ivmgW8k0xpSLDt2dSgfso5/view?usp=drive_link) (occorre accedere con il proprio account istituzionale unipd)
2) Copiare il percorso del file scaricato
3) Collegare il vostro device tramite cavo al PC
4) Recarsi nel percorso della Android-Sdk (vedere sopra)
5) In uno spazio vuoto nella cartella -> Tasto destro del mouse -> "Apri da terminale"
6) Per verificare che il vostro device sia visibile -> `./adb devices` 
7) Creare una cartella con il comando per il modello con `./adb shell mkdir /data/local/tmp/llm`
8) Digitare `./adb push PERCORSO_DOWNLOAD_MODELLO /data/local/tmp/llm/gemma3-1B-it-int4.task`, e' molto importante che il secondo percorso (device) rispetti questa sintassi (i caratteri di percorso devono essere "/", vanno rimosse le virgolette).


## Al termine dell'utilizzo

E' possibile rimuovere il modello locale tramite i seguenti passi:

1) Collegare il vostro device tramite cavo al PC
2) Recarsi nel percorso della Android-Sdk (vedere sopra)
3) In uno spazio vuoto nella cartella -> Tasto destro del mouse -> "Apri da terminale"
4) Per verificare che il vostro device sia visibile -> `./adb devices` 
5) Digitare `./adb shell rm -r /data/local/tmp/llm`, questo eliminera' sia la cartella llm che il modello stesso.

---
### Autori

- Mirco Zavarise
- Daniele Riolmi Rossetto
- Luca Tonin
- Leonardo Joao Fabbro
