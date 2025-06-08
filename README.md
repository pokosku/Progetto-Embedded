# Prima di partire

Per poter utilizzare la nostra applicazione occorre disporre del modello di Natural Language Processing "gemma3" all'interno del vostro dispositivo. 
Di seguito trovate le istruzioni per installare il modello.

## Nota

Per poter installare modelli in locale su Android e' necessario disporre di Android-Sdk, solitamente installata tramite Android Studio nel path di default: `C:\Users\USERNAME\AppData\Local\Android\Sdk\platform-tools`
## Istruzioni

1) Scaricare il modello dal seguente [link](https://drive.google.com/file/d/1lVjhUcZFg2ivmgW8k0xpSLDt2dSgfso5/view?usp=drive_link).
2) Estrarre il file .task dall' archivio tar.gz
3) Collegare il vostro device tramite cavo al PC
4) Recarsi nel percorso della Android-Sdk (vedere sopra)
5) In uno spazio vuoto nella cartella -> Tasto destro del mouse > "Apri da terminale"
6) Per verificare che il vostro device sia visibile -> `./adb devices` 
7) Digitare `./adb push C:\Users\USERNAME\Downloads\gemma-3-tflite-gemma3-1b-it-int4-v1\gemma3-1B-it-int4.task /data/local/tmp/llm/gemma3-1B-it-int4.task`, e' molto importante che il secondo percorso (device) rispetti questa sintassi (i caratteri di percorso devono essere "/").

