Juganaru Stefan  Madalin 
332 CA

COMMUNICATION CHANNEL :
- Am folosit un BlockingQueue pentru a evita exceptia OutOfMemory , deoarece coada e 
capabila sa scoata elemente la fel de repede cum le primeste 

- Pentru a pune mesaje pe canalul minerului m-am folosit pur si simplu de functia put() , iar
pentru a le lua de pe canalul minerului, de functia take()

-In functia de putMessageWizzard m-am folosit de un Reetrant lock , deoarece acest lock 
lasa threadul sa intre de mai mult ori in block-ul de instructiuni , iar de fiecare data cand
threadul face acest lucru , lock-ul incrementeaza un contor . Dupa ce thread-ul termina , 
se da unlock pana contorul ajunge la 0 . In asa fel , mesajele de la wizard vin in ordine 

-In functia de get pentru wizzard , se ia mesajul cu functia take()

Miner :

- Am avut nevoie de un semafor 
- Am luat functiile de encrypt din functia main a scheletului 
-In functia run , am intializat 2 mesaje de tip Message si am deschis un loop in care merg
lucrurile in felul urmator : 

== intr-un block try , am pus un semafor inainte de primul get si am dat release dupa al 2 lea 
pentru a nu avea 2 wizard care sa intre in acelasi timp in acest block 

== in synchronized(solved) testez daca currentRoom-ul din al 2 lea mesaj este in solved . Daca da 
se face continue , daca nu , se adauga in solved camera 

- Iar la final se creaza noul mesaj cu ajutorul functiei "encryptMultipleTimes" si se pune pe canalul minerului 
