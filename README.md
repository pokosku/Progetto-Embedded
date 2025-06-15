# Prima di partire

Per poter utilizzare la nostra applicazione occorre disporre del modello di Natural Language Processing "gemma3" all'interno del vostro dispositivo. 
Di seguito trovate le istruzioni per installare il modello.

## Note

Per poter installare modelli in locale su Android e' necessario disporre di Android-Sdk, solitamente installata tramite Android Studio nel path di default: `C:\Users\USERNAME\AppData\Local\Android\Sdk\platform-tools`

La sintassi dei comandi di adb potrebbe variare in base alla shell utilizzata; ad esempio per Windows PowerShell occorre utilizzare `./adb`, per CMD utilizzare `adb`.

## Istruzioni

1) Scaricare il modello dal seguente [link](https://drive.google.com/file/d/1lVjhUcZFg2ivmgW8k0xpSLDt2dSgfso5/view?usp=drive_link).
2) Copiare il percorso del file scaricato
3) Collegare il vostro device tramite cavo al PC
4) Recarsi nel percorso della Android-Sdk (vedere sopra)
5) In uno spazio vuoto nella cartella -> Tasto destro del mouse > "Apri da terminale"
6) Per verificare che il vostro device sia visibile -> `./adb devices` 
7) Creare una cartella con il comando per il modello con `./adb shell mkdir /data/local/tmp/llm`
8) Digitare `./adb push PERCORSO_DOWNLOAD_MODELLO /data/local/tmp/llm/gemma3-1B-it-int4.task`, e' molto importante che il secondo percorso (device) rispetti questa sintassi (i caratteri di percorso devono essere "/", vanno rimosse le virgolette).


# Al termine dell'utilizzo

E' possibile rimuovere il modello locale tramite i seguenti passi:

1) Collegare il vostro device tramite cavo al PC
2) Recarsi nel percorso della Android-Sdk (vedere sopra)
3) In uno spazio vuoto nella cartella -> Tasto destro del mouse > "Apri da terminale"
4) Per verificare che il vostro device sia visibile -> `./adb devices` 
5) Digitare `./adb shell rm -r /data/local/tmp/llm`, questo eliminera' sia la cartella llm che il modello stesso.