Eure Beteiligungen sind willkommen.

Mit euren Beiträgen überlasst ihr mir, Stefan Prelle, das Recht, diesen Code 
und Daten im Kontext des RPGFramework Projekts zu nutzen. Dies beinhaltet 
explizit auch die kommerzielle Nutzung, z.B. durch die Genesis-Lizenzen.

Euer Recht ist es, dass diese Daten weiterhin unter dem für Fanprojekte 
vorgegebenen Rahmen unentgeltlich zur Verfügung stehen, so daß eure Beiträge
auch der Community nutzen.


## Ablauf

* Erzeuge Dir einen Fork dieses Projekts in dein persönlichen Github Account
* In meinem Projekt erzeugst Du ein Issue Ticket (z.B. "Unterstützung für 'Hinter dem Schleier'")
  Das Ticket bekommt dann eine Nummer wie z.B. "#5".
* In deinem Projekt erzeugst Du dann einen Branch speziell für das Ticket.
  (Wenn Du lokal an deinem Rechner arbeitest musst Du den Branch durch ein Checkout/Pull synchronisieren)
* Du wechselst in den Branch, machst deine Änderung und wenn Du committest, sieh zu dass die
  Commit-Nachricht in der ersten Zeile mit der Ticketnummer beginnt (also z.B. "#5 Zauber aus HdS eingegeben")
* Diese Commits kannst Du per Push **in dein Repository** hochladen.
* Wenn Du irgendwann fertig bist, besuchst Du die wieder Github und erzeugst einen
  Pull Request - wichtig: **für deinen Branch** und gegen den Original `master`
  Auch beim Pull Request schreib bitte als erstes nochmal die Ticketnummer (z.B. '#5' rein)

Theoretisch kannst Du dann ein neues Ticket erzeugen, einen neuen Branch und das nächste Thema angehen.
Aber: Du kannst nur bis zu 3 offene Pull Requests haben

## Was gehört in ein Ticket

Die Faustregel ist hier: alles was eine handliche Zusammengehörige Aufgabe ist, 
über die man ggf. einzeln entscheiden möchte. Wenn Du z.B. alle Daten aus Mondstahlklingen eingeben würdest, 
wäre ein Ticket z.B. eine Waffengattung. Wenn Du aber nur Daten aus einem kleinen Buch eingibst, 
wo es insgesamt nur 10 Ausrüstungsstücke, kannst Du auch ein Ticket "Ausrüstung aus XY" machen.

Schlecht sind Tickets bei denen Du mischt: z.B. "Bugfix zum Regionalband X und fehlende Daten aus Band Y"

Faustregel: Mische keine Tickets für Code-Änderungen mit Daten-Änderungen