using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Diagnostics;
using System.Windows.Forms;
using System.ComponentModel;

namespace SplittermondTool
{
    public class Kreatur
    {
        #region Parameters

        public string Name = "NoName";

        /* --- Parameters --- */
        //Attribute
        public int AUS = 0;
        public int BEW = 0;
        public int INT = 0;
        public int KON = 0;
        public int MYS = 0;
        public int STA = 0;
        public int VER = 0;
        public int WIL = 0;
        // 
        public int GK = 0;
        public int GSW = 0;
        public int GSWFliegend = 0;
        public int GSWSchwimmend = 0;
        public int LP = 0;
        public int LPGes = 0;
        public int FO = 0;
        public int VTD = 0;
        public int SR = 0;
        public int KW = 0;
        public int GW = 0;
        public int Ini = 0;
        public int Ang = 0;

        // Typus und Waffen
        public string Typus = "unbekannt";
        public bool isReittier = false;
        public bool isKoloss = false;
        public Waffe basisWaffe = null;
        public bool zusWaffe = false;
        public Waffe zusatzWaffe1 = null;
        public Waffe zusatzWaffe2 = null;
        public List<Waffe> Waffen = new List<Waffe>();

        // Fertigkeiten
        public int Akrobatik = 0;
        public int Athletik = 0;
        public int Entschlossenheit = 0;
        public int Heimlichkeit = 0;
        public int Wahrnehmung = 0;
        public int Zähigkeit = 0;

        public int Fingerfertigkeit = 0;
        public int Schwimmen = 0;
        public int Jagdkunst = 0;
        public int Darbietung = 0;
        public int Empathie = 0;

        public int FingerfertigkeitBase = 7;
        public int SchwimmenBase = 7;
        public int JagdkunstBase = 7;
        public int DarbietungBase = 7;
        public int EmpathieBase = 7;

        public bool hasFingerfertigkeit = false;
        public bool hasSchwimmen = false;
        public bool hasJagdkunst = false;
        public bool hasDarbietung = false;
        public bool hasEmpathie = false;

        // Merkmale
        public int Furchterregend = 0;

        // Zauberfertigkeiten

        public List<Zauberfertigkeit> Zauberfertigkeiten = new List<Zauberfertigkeit>();
        // Zauber, Meisterschaften, Merkmale
        public List<string> Zauber = new List<string>();
        public List<string> Meisterschaften = new List<string>();
        public List<string> Merkmale = new List<string>();
        public List<string> Besonderheiten = new List<string>();

        // Gesamtwerte
        public int AttributeGes = 0;
        public int BasisFertigkeitenGes = 0;
        public int ExtraFertigkeitenGes = 0;
        public int KreaturWert = 0;
        public int LPMod = 0;

        int minusMerkmale = 0;
        int minusMeisterschaften = 0;
        int minusZauberfertigkeiten = 0;
        int minusZauber = 0;
        string auswahl = "";
        List<string> auswahlItems = new List<string>();
        FormKreaturenAuswahl auswahlPopUp;
        DialogResult dialogresult;
        public string kombination = "";

        int GesStufe = 0;

        #endregion

        public Kreatur()
        {

        }

        public Kreatur(string korpus, string rolle, List<string> module)
        {
            switch (korpus)
            {
                case "Robust":
                    kombination = kombination + "Robust";
                    Debug.WriteLine("Robust");
                    Robust();
                    break;
                case "Agil":
                    kombination = kombination + "Agil";
                    Debug.WriteLine("Agil");
                    Agil();
                    break;
                case "Schnell":
                    kombination = kombination + "Schnell";
                    Debug.WriteLine("Schnell");
                    Schnell();
                    break;
                case "Stark":
                    kombination = kombination + "Stark";
                    Debug.WriteLine("Stark");
                    Stark();
                    break;
                default:
                    Debug.WriteLine("Fehler in Korpus Wahl!");
                    break;
            }

            switch (rolle)
            {
                case "Kampf":
                    kombination = kombination + "+ Kampf";
                    Debug.WriteLine("Kampf");
                    Kampf();
                    break;
                case "Lasttier":
                    kombination = kombination + "+ Lasttier";
                    Debug.WriteLine("Lasttier");
                    Lasttier();
                    break;
                case "Familiar":
                    kombination = kombination + "+ Familiar";
                    Debug.WriteLine("Familiar");
                    Familiar();
                    break;
                case "Kundschafter":
                    kombination = kombination + "+ Kundschafter";
                    Debug.WriteLine("Kundschafter");
                    Kundschafter();
                    break;
                case "Maskottchen":
                    kombination = kombination + " + Maskottchen";
                    Debug.WriteLine("Maskottchen");
                    Maskottchen();
                    break;
                default:
                    Debug.WriteLine("Fehler in Rollen Wahl!");
                    break;
            }

            if (module.Contains("Koloss"))
                isKoloss = true;

            foreach(string modul in module)
            {
                switch (modul)
                {
                    case "Gross":
                        kombination = kombination + "+ Groß";
                        Debug.WriteLine("Groß");
                        Groß();
                        break;
                    case "Klein":
                        kombination = kombination + "+ Klein";
                        Debug.WriteLine("Klein");
                        Klein();
                        break;
                    case "Riesig":
                        kombination = kombination + "+ Riesig";
                        Debug.WriteLine("Riesig");
                        Riesig();
                        break;
                    case "Schrumpfen":
                        kombination = kombination + "+ Schrumpfen";
                        Debug.WriteLine("Schrumpfen");
                        Schrumpfen();
                        break;
                    case "Winzig":
                        kombination = kombination + "+ Winzig";
                        Debug.WriteLine("Winzig");
                        Winzig();
                        break;
                    case "Angriffslustig":
                        kombination = kombination + "+ Angriffslustig";
                        Debug.WriteLine("Angriffslustig");
                        Angrifslustig();
                        break;
                    case "Beobachter":
                        kombination = kombination + "+ Beobachter";
                        Debug.WriteLine("Beobachter");
                        Beobachter();
                        break;
                    case "Helfer":
                        kombination = kombination + "+ Helfer";
                        Debug.WriteLine("Helfer");
                        Helfer();
                        break;
                    case "Jäger":
                        kombination = kombination + "+ Jäger";
                        Debug.WriteLine("Jäger");
                        Jäger();
                        break;
                    case "Magisch":
                        kombination = kombination + "+ Magisch";
                        Debug.WriteLine("Magisch");
                        Magisch();
                        break;
                    case "Prächtig":
                        kombination = kombination + "+ Prächtig";
                        Debug.WriteLine("Prächtig");
                        Prächtig();
                        break;
                    case "Reittier":
                        kombination = kombination + "+ Reittier";
                        Debug.WriteLine("Reittier");
                        Reittier();
                        break;
                    case "BesReittier":
                        kombination = kombination + "+ BesReittier";
                        Debug.WriteLine("BesReittier");
                        BesReittier();
                        break;
                    case "Verteidiger":
                        kombination = kombination + "+ Verteidiger";
                        Debug.WriteLine("Verteidiger");
                        Verteidiger();
                        break;
                    case "Willensstark":
                        kombination = kombination + "+ Willensstark";
                        Debug.WriteLine("Willensstark");
                        Willensstark();
                        break;
                    case "ZähneKlauen":
                        kombination = kombination + "+ Zähne und Klauen";
                        Debug.WriteLine("ZähneKlauen");
                        ZähneKlauen();
                        break;
                    case "Zirkustier":
                        kombination = kombination + "+ Zirkustier";
                        Debug.WriteLine("Zirkustier");
                        Zirkustier();
                        break;
                    case "ZusätzlicheZauber":
                        kombination = kombination + "+ Zusätzliche Zauber";
                        Debug.WriteLine("ZusätzlicheZauber");
                        ZusätzlicheZauber();
                        break;
                    case "Koloss":
                        kombination = kombination + "+ Koloss";
                        Debug.WriteLine("Koloss");
                        Koloss();
                        break;
                    case "Rudel":
                        kombination = kombination + "+ Rudel";
                        Debug.WriteLine("Rudel");
                        Rudel();
                        break;
                    case "Schwarmwesen":
                        kombination = kombination + "+ Schwarmwesen";
                        Debug.WriteLine("Schwarmwesen");
                        Schwarmwesen();
                        break;
                    case "Bedrohlich":
                        kombination = kombination + "+ Bedrohlich";
                        Debug.WriteLine("Bedrohlich");
                        Bedrohlich();
                        break;
                    case "Blutrausch":
                        kombination = kombination + "+ Blutrausch";
                        Debug.WriteLine("Blutrausch");
                        Blutrausch();
                        break;
                    case "Dämmerbote":
                        kombination = kombination + "+ Dämmerbote";
                        Debug.WriteLine("Dämmerbote");
                        Dämmerbote();
                        break;
                    case "Falle":
                        kombination = kombination + "+ Falle";
                        Debug.WriteLine("Falle");
                        Falle();
                        break;
                    case "Fliegend":
                        kombination = kombination + "+ Fliegend";
                        Debug.WriteLine("Fliegend");
                        Fliegend();
                        break;
                    case "Gestaltwandler":
                        kombination = kombination + "+ Gestaltwandler";
                        Debug.WriteLine("Gestaltwandler");
                        Gestaltwandler();
                        break;
                    case "Giftig":
                        kombination = kombination + "+ Giftig";
                        Debug.WriteLine("Giftig");
                        Giftig();
                        break;
                    case "Körperlos":
                        kombination = kombination + "+ Körperlos";
                        Debug.WriteLine("Körperlos");
                        Körperlos();
                        break;
                    case "Krankheitsträger":
                        kombination = kombination + "+ Krankheitsträger";
                        Debug.WriteLine("Krankheitsträger");
                        Krankheitsträger();
                        break;
                    case "Sänger":
                        kombination = kombination + "+ Sänger";
                        Debug.WriteLine("Sänger");
                        Sänger();
                        break;
                    case "Schwimmer":
                        kombination = kombination + "+ Schwimmer";
                        Debug.WriteLine("Schwimmer");
                        Schwimmer();
                        break;
                    case "SpezSensorik":
                        kombination = kombination + "+ Spezielle Sensorik";
                        Debug.WriteLine("SpezSensorik");
                        SpezSensorik();
                        break;
                    case "Sprachbegabt":
                        kombination = kombination + "+ Sprachbegabt";
                        Debug.WriteLine("Sprachbegabt");
                        Sprachbegabt();
                        break;
                    case "Teleportation":
                        kombination = kombination + "+ Teleportation";
                        Debug.WriteLine("Teleportation");
                        Teleporation();
                        break;
                    case "Unsichtbar":
                        kombination = kombination + "+ Unsichtbar";
                        Debug.WriteLine("Unsichtbar");
                        Unsichtbar();
                        break;
                    case "Vernunftbegabt":
                        kombination = kombination + "+ Vernunftbegabt";
                        Debug.WriteLine("Vernunftbegabt");
                        Vernunftbegabt();
                        break;
                    case "Fragil":
                        kombination = kombination + "+ Fragil";
                        Debug.WriteLine("Fragil");
                        Fragil();
                        break;
                    case "Immunitäten":
                        kombination = kombination + "+ Immunitäten";
                        Debug.WriteLine("Immunitäten");
                        Immunitäten();
                        break;
                    case "Nullsumme":
                        kombination = kombination + "+ Nullsumme";
                        Debug.WriteLine("Nullsumme");
                        Nullsumme();
                        break;
                    case "Resistenzen":
                        kombination = kombination + "+ Resistenzen";
                        Debug.WriteLine("Resistenzen");
                        Resitenzen();
                        break;
                    case "Verwundbar":
                        kombination = kombination + "+ Verwundbar";
                        Debug.WriteLine("Verwundbar");
                        Verwundbar();
                        break;
                    case "Zäh":
                        kombination = kombination + "+ Zäh";
                        Debug.WriteLine("Zäh");
                        Zäh();
                        break;
                    case "Feenwesen":
                        kombination = kombination + "+ Feenwesen";
                        Debug.WriteLine("Feenwesen");
                        Feenwesen();
                        break;
                    case "Geist":
                        kombination = kombination + "+ Geist";
                        Debug.WriteLine("Geist");
                        Geist();
                        break;
                    case "Unterwasserwesen":
                        kombination = kombination + "+ Unterwasserwesen";
                        Debug.WriteLine("Unterwasserwesen");
                        Unterwasserwesen();
                        break;
                    case "Untot":
                        kombination = kombination + "+ Untot";
                        Untot();
                        Debug.WriteLine("Untot");
                        break;
                    case "Empathisch":
                        kombination = kombination + "+ Empathisch";
                        Empathisch();
                        Debug.WriteLine("Empathisch");
                        break;
                    default:
                        Debug.WriteLine("Fehler in Modulzusammenstellung!");
                        break;
                }
            }
            Endabnahme();
        }

        #region Add Krempel
        public void AddKorpusModul(string korpus)
        {

        }

        public void AddRollenModul(string rolle)
        {

        }

        public void AddModule(List<string> module)
        {

        }

        public void AddWaffe(Waffe neueWaffe)
        {
            Waffen.Add(neueWaffe);
        }

        public void AddZauber(string neuerZauber)
        {
            Zauber.Add(neuerZauber);
        }

        public void AddMeisterschaft(string neueMeisterschaft)
        {
            Meisterschaften.Add(neueMeisterschaft);
        }

        public void AddMerkmal(string neuesMerkmal)
        {
            Merkmale.Add(neuesMerkmal);
        }

        public void AddZauberfertigkeit(string name, int value)
        {
            Zauberfertigkeit neueZauberfertigkeit = new Zauberfertigkeit(name, value);
            Zauberfertigkeit z;

            bool exists = false;

            foreach (Zauberfertigkeit zf in Zauberfertigkeiten)
            {
                if (zf.Name == name)
                {
                    //Debug.WriteLine("exists" + zf.Name);
                    exists = true;
                }
            }

            if (exists)
            {
                z = Zauberfertigkeiten.Find(x => x.Name == neueZauberfertigkeit.Name);
                //Debug.WriteLine("Update Value " + z.Name);
                z.Value += neueZauberfertigkeit.Value;
                //Debug.WriteLine("new Value: " + z.Value);
            }
            else
            {
                //Debug.WriteLine("Adding new Zauberfertigkeit: " + neueZauberfertigkeit.Name);
                Zauberfertigkeiten.Add(neueZauberfertigkeit);
            }

            //Debug.WriteLine("--------");
            //foreach (Zauberfertigkeit zf in Zauberfertigkeiten)
            //{
            //    Debug.WriteLine(zf.Name + ": " + zf.Value);
            //}
            //Debug.WriteLine("--------");
        }


        #endregion

        #region Endabnahme
        private void Endabnahme()
        {

            // Zusäzliche Fähigkeiten ermitteln
            EndabnahmeZusFähigkeiten();

            //Gesundheitstufen ermitteln
            EndabnahmeGesundheitstufen();
            // Debug.WriteLine("LPMod = " + LPMod);

            // Meisterschaften entfernen
            EndabnahmeMeisterschaften();

            // Zauber/fertigkeiten anpassen
            EndabnahmeZauberAnpassen();

            // Merkmale entfernen
            EndabnahmeMerkmale();

            // Basis Werte anpassen
            EndabnahmeBasisWerte();

            //Zusätzliche Waffe
            if(zusWaffe)
            EndabnahmeZusWaffe();

            //Waffe Stumpf
            // waffenMerkmale.Add("Stumpf");
            int i = 0;
            foreach (string element in basisWaffe.Merkmale)
            {
                if (element == "Stumpf")
                {
                    i++;
                }
            }
            Debug.WriteLine("Stumpf:" + i);
            // Hässliche Hacks
            if(basisWaffe.Merkmale.Contains("Stumpf") && basisWaffe.Merkmale.Contains("Durchdringung 1"))
            {
                basisWaffe.Merkmale.Remove("Stumpf");
                basisWaffe.Merkmale.Remove("Durchdringung 1");
            }

            if (basisWaffe.Merkmale.Contains("Stumpf") && basisWaffe.Merkmale.Contains("Kritisch 1"))
            {
                basisWaffe.Merkmale.Remove("Stumpf");
                basisWaffe.Merkmale.Remove("Kritisch 1");
            }
        }

        public void EndabnahmeGesundheitstufen()
        {

            Debug.WriteLine("Gesundheistufe " + GesStufe);
            if (GesStufe < -1)
            {
                Merkmale.Add("Zerbrechlich");
                //Debug.WriteLine("LP vorher: " + LP);
                //Debug.WriteLine("LP Abzüge Zerbrechlich: " + (GesStufe + 1));

                LP += GesStufe + 1;
                //Debug.WriteLine("LP nachher: " + LP);
            }
            else if (GesStufe == -1)
            {
                Merkmale.Add("Schwächlich");
            } else
            {
                LP += (GesStufe - 1);
            }

            if (Merkmale.Contains("Zerbrechlich"))
            {
                if (Merkmale.Contains("Schwächlich"))
                    Merkmale.Remove("Schwächlich");
                LPMod = 1;
            }
            else if (Merkmale.Contains("Schwächlich"))
            {
                LPMod = 3;
            }
            else
            {
                LPMod = 5;
            }
            Debug.WriteLine("LPMod = " + LPMod);
        }

        public void EndabnahmeBasisWerte()
        {

            Debug.WriteLine("LP Basis: " + LP);
            Debug.WriteLine("Kreatur Basis: " + KreaturWert);

            if (GK > 8)
                GK = 8;
            if (LP < 1)
                LP = 1;
            if (KreaturWert <= 0)
                KreaturWert = 1;
            if (SR < 0)
                SR = 0;

            // Für jedes Negative Attribut wird ein positives Merkmal, 1 Zauberfertigkeit und 1 Zauber gestrichen

            // Errechnete Werte
            Name = kombination;
            AttributeGes = AUS + BEW + INT + KON + MYS + STA + VER + WIL;
            BasisFertigkeitenGes = Akrobatik + Athletik + Entschlossenheit + Heimlichkeit + Wahrnehmung + Zähigkeit;
            ExtraFertigkeitenGes = Schwimmen + Jagdkunst + Darbietung + Fingerfertigkeit + Empathie;
            LPGes = LP * LPMod;
        }

        public void EndabnahmeMeisterschaften()
        {
            while (minusMeisterschaften >= 1)
            {
                Debug.WriteLine("Meisterschaften :" + Meisterschaften.Count + " minusMeisterschaften: " + minusMeisterschaften);
                if (minusMeisterschaften >= Meisterschaften.Count)
                {
                    Meisterschaften.Clear();
                    break;
                }
                if (Meisterschaften.Count > 1)
                {
                    foreach (string meisterschaft in Meisterschaften)
                    {
                        auswahlItems.Add(meisterschaft);
                    }
                    auswahlPopUp = new FormKreaturenAuswahl(this, auswahlItems, "Entfernen:");
                    dialogresult = auswahlPopUp.ShowDialog();
                    if (dialogresult == DialogResult.OK)
                    {
                        Meisterschaften.Remove(auswahl);
                    }
                    auswahlPopUp.Dispose();
                    auswahlItems.Clear();
                }
                else
                {
                    Meisterschaften.Clear();
                    break;
                }
                minusMeisterschaften--;
            }
        }

        public void EndabnahmeMerkmale()
        {
            while (minusMerkmale >= 1)
            {
                Debug.WriteLine("Merkmale :" + Merkmale.Count + " minusMerkmale: " + minusMerkmale);

                if (Merkmale.Count > 1)
                {
                    foreach (string merkmal in Merkmale)
                    {
                        if(merkmal != "Feigling" ^ merkmal != "Zerbrechlich" ^ merkmal != "Schwächlich")
                        auswahlItems.Add(merkmal);
                    }
                    if (auswahlItems.Count > 0)
                    {
                        //auswahlItems.Add("Kein positives zum entfernen");
                        auswahlPopUp = new FormKreaturenAuswahl(this, auswahlItems, "Entfernen:");
                        dialogresult = auswahlPopUp.ShowDialog();
                        if (dialogresult == DialogResult.OK)
                        {
                            if (!(auswahl == "Kein positives zum entfernen"))
                                Merkmale.Remove(auswahl);
                        }
                        auswahlPopUp.Dispose();
                        auswahlItems.Clear();
                    }
                }
                minusMerkmale--;
            }
        }

        public void EndabnahmeZauberAnpassen()
        {
            // Zauberfertigkeiten senken
            if (minusZauberfertigkeiten > 0 && Zauberfertigkeiten.Count > 0)
            {
                foreach (Zauberfertigkeit zauberfertigkeit in Zauberfertigkeiten)
                {
                    zauberfertigkeit.Value += -minusZauberfertigkeiten;
                }
            }



            // Zauber entfernen
            Debug.WriteLine("Minus Zauber: " + minusZauber);
            Debug.WriteLine("Zauber: " + Zauber.Count());
            int i = 0;
            if (minusZauber > 0 && Zauber.Count > 0)
            {
                if (minusZauber >= Zauber.Count)
                {
                    Zauber.Clear();
                }
                else
                {
                    while (minusZauber > 0)
                    {
                        if (i == 0)
                        {
                            if (Zauber.Contains("Grad 2"))
                            {
                                Zauber.Remove("Grad 2");
                            } else
                            {
                                if(Zauber.Contains("Grad 1"))
                                {
                                    Zauber.Remove("Grad 1");
                                }
                                else
                                {
                                    Zauber.Remove("Grad 0");
                                }
                            }
                            i++;
                        } else if (i == 1)
                        {
                            if (Zauber.Contains("Grad 1"))
                            {
                                Zauber.Remove("Grad 1");
                            }
                            else
                            {
                                Zauber.Remove("Grad 0");
                            }
                            i++;
                        }
                        else
                        {
                            if (Zauber.Contains("Grad 0"))
                                Zauber.Remove("Grad 0");
                            else if (Zauber.Contains("Grad 1"))
                                Zauber.Remove("Grad 1");
                            else if (Zauber.Contains("Grad 2"))
                                Zauber.Remove("Grad 2");
                        }

                        //foreach (string zauber in Zauber)
                        //{
                        //    auswahlItems.Add(zauber);
                        //}
                        //auswahlPopUp = new FormKreaturenAuswahl(this, auswahlItems, "Entfernen:");
                        //dialogresult = auswahlPopUp.ShowDialog();
                        //if (dialogresult == DialogResult.OK)
                        //{
                        //    Zauber.Remove(auswahl);
                        //}
                        auswahlPopUp.Dispose();
                        auswahlItems.Clear();
                        minusZauber--;
                    }
                }
            }
        }

        public void EndabnahmeZusFähigkeiten()
        {
            if (hasSchwimmen)
                Schwimmen += SchwimmenBase;
            else
                Schwimmen = 0;
            if (hasJagdkunst)
                Jagdkunst += JagdkunstBase;
            else
                Jagdkunst = 0;
            if (hasDarbietung)
                Darbietung += DarbietungBase;
            else
                Darbietung = 0;
            if (hasFingerfertigkeit)
                Fingerfertigkeit += FingerfertigkeitBase;
            else
                Fingerfertigkeit = 0;
            if (hasEmpathie)
                Empathie += EmpathieBase;
            else Empathie = 0;

            if (hasSchwimmen || Merkmale.Contains("Fliegend"))
            {
                GSW += -2;
                if (Merkmale.Contains("Fliegend"))
                    auswahlItems.Add("Fliegend");
                if (hasSchwimmen)
                    auswahlItems.Add("Schwimmend");
                auswahlItems.Add("Keines");
                auswahlPopUp = new FormKreaturenAuswahl(this, auswahlItems, "GSW Basis -3, Spezial +3:");
                dialogresult = auswahlPopUp.ShowDialog();
                if (dialogresult == DialogResult.OK)
                {
                    switch (auswahl)
                    {
                        case "Keines":
                            break;
                        case "Schwimmend":
                            GSW += -5;
                            GSWSchwimmend += 5;
                            break;
                        case "Fliegend":
                            GSW += -5;
                            GSWFliegend += 5;
                            break;
                        default:
                            Debug.WriteLine("Fehler, Endabnahme GSW Auswahl");
                            break;
                    }
                }
                auswahlPopUp.Dispose();
                auswahlItems.Clear();
            }

            if (Furchterregend > 0)
                Merkmale.Add("Furchterregend " + Furchterregend);

            if (GSW < 1)
                GSW = 1;
        }

        public void EndabnahmeZusWaffe()
        {
            if (KreaturWert == 1)
            {
                List<string> waffenMerkmale = new List<string>();
                waffenMerkmale.Add("Scharf 2");
                zusatzWaffe1 = new Waffe(1, 6, 0, 7, waffenMerkmale);
                Waffen.Add(zusatzWaffe1);
                Waffen.Remove(basisWaffe);
            }
            else if(KreaturWert == 2)
            {
                List<string> waffenMerkmale = new List<string>();
                waffenMerkmale.Add("Scharf 2");
                zusatzWaffe1 = new Waffe(1, 6, 1, 6, waffenMerkmale);
                Waffen.Add(zusatzWaffe1);
                Waffen.Remove(basisWaffe);
            }
            else if (KreaturWert == 3)
            {
                List<string> waffenMerkmale = new List<string>();
                waffenMerkmale.Add("Scharf 3");
                zusatzWaffe1 = new Waffe(1, 6, 2, 7, waffenMerkmale);
                Waffen.Add(zusatzWaffe1);
            }
            else if (KreaturWert == 4)
            {
                List<string> waffenMerkmale = new List<string>();
                waffenMerkmale.Add("Scharf 2");
                waffenMerkmale.Add("Durchdringung 1");
                zusatzWaffe1 = new Waffe(1, 6, 4, 8, waffenMerkmale);
                Waffen.Add(zusatzWaffe1);
            } else
            {
                List<string> waffenMerkmale = new List<string>();
                waffenMerkmale.Add("Scharf 3");
                waffenMerkmale.Add("Durchdringung 2");
                zusatzWaffe1 = new Waffe(2, 6, 2, 9, waffenMerkmale);
                Waffen.Add(zusatzWaffe1);
            }
        }
        #endregion

        public void setAuswahl(string aktuelleAuswahl)
        {
            auswahl = aktuelleAuswahl;
           // Debug.WriteLine("Auswahl: " + auswahl);
        }

        public void KreaturAusgabe()
        {

        }

        #region Module

        #region Korpus

        public void Robust()
        {
            AUS = 2;
            BEW = 2;
            INT = 2;
            KON = 3;
            MYS = 0;
            STA = 2;
            VER = 1;
            WIL = 1;
            // 
            GK = 3;
            GSW = 5;
            GSWFliegend = 5;
            GSWSchwimmend = 5;
            LP = 6;
            FO = 2;
            VTD = 15;
            SR = 0;
            KW = 15;
            GW = 13;
            Ini = 8;
            Ang = 4; 

            // Typus und Waffen
            Typus = "Tier";
            List<string> waffenMerkmale = new List<string>();
            waffenMerkmale.Add("Stumpf");
            basisWaffe = new Waffe(1, 6, 0, 7, waffenMerkmale);
            //Debug.WriteLine("WGS: " + robustWaffe.WGS);
            Waffen.Add(basisWaffe);


            // Fertigkeiten
            Akrobatik = 7;
            Athletik = 7;
            Entschlossenheit = 5;
            Heimlichkeit = 6;
            Wahrnehmung = 6;
            Zähigkeit = 9;
            Fingerfertigkeit = 0;
            Schwimmen = 0;
            Jagdkunst = 0;
            Darbietung = 0;

            Merkmale.Add("Feigling");
            //Merkmale.Add("Schwächlich");
            GesStufe--;

            // Gesamtwerte
            KreaturWert = 2;
            LPMod = 3;

            //UpdateBerechneteWerte();
    }

        public void Schnell()
        {
            AUS = 2;
            BEW = 3;
            INT = 3;
            KON = 1;
            MYS = 0;
            STA = 1;
            VER = 1;
            WIL = 1;
            // 
            GK = 3;
            GSW = 9;
            GSWFliegend = 9;
            GSWSchwimmend = 9;
            LP = 4;
            FO = 2;
            VTD = 15;
            SR = 0;
            KW = 13;
            GW = 14;
            Ini = 7;
            Ang = 4;

            // Typus und Waffen
            Typus = "Tier";
            List<string> waffenMerkmale = new List<string>();
            waffenMerkmale.Add("Stumpf");
            basisWaffe = new Waffe(1, 6, 0, 7, waffenMerkmale);
            //Debug.WriteLine("WGS: " + robustWaffe.WGS);
            Waffen.Add(basisWaffe);

            // Fertigkeiten
            Akrobatik = 8;
            Athletik = 7;
            Entschlossenheit = 4;
            Heimlichkeit = 8;
            Wahrnehmung = 7;
            Zähigkeit = 6;
            Fingerfertigkeit = 0;
            Schwimmen = 0;
            Jagdkunst = 0;
            Darbietung = 0;

            Merkmale.Add("Feigling");
            //Merkmale.Add("Schwächlich");
            GesStufe--;

            // Gesamtwerte
            KreaturWert = 2;
            LPMod = 3;

            //UpdateBerechneteWerte();
        }

        public void Agil()
        {
            AUS = 2;
            BEW = 4;
            INT = 2;
            KON = 2;
            MYS = 0;
            STA = 1;
            VER = 1;
            WIL = 1;
            // 
            GK = 3;
            GSW = 7;
            GSWFliegend = 7;
            GSWSchwimmend = 7;
            LP = 5;
            FO = 2;
            VTD = 15;
            SR = 0;
            KW = 14;
            GW = 14;
            Ini = 7;
            Ang = 4;

            // Typus und Waffen
            Typus = "Tier";
            List<string> waffenMerkmale = new List<string>();
            waffenMerkmale.Add("Stumpf");
            basisWaffe = new Waffe(1, 6, 0, 7, waffenMerkmale);
            //Debug.WriteLine("WGS: " + robustWaffe.WGS);
            Waffen.Add(basisWaffe);

            // Fertigkeiten
            Akrobatik = 9;
            Athletik = 8;
            Entschlossenheit = 4;
            Heimlichkeit = 8;
            Wahrnehmung = 6;
            Zähigkeit = 5;
            Fingerfertigkeit = 0;
            Schwimmen = 0;
            Jagdkunst = 0;
            Darbietung = 0;

            Merkmale.Add("Feigling");
            //Merkmale.Add("Schwächlich");
            GesStufe--;

            // Gesamtwerte
            KreaturWert = 2;
            LPMod = 3;

            //UpdateBerechneteWerte();
        }

        public void Stark()
        {
            AUS = 2;
            BEW = 2;
            INT = 2;
            KON = 2;
            MYS = 0;
            STA = 3;
            VER = 1;
            WIL = 1;
            // 
            GK = 3;
            GSW = 5;
            GSWFliegend = 5;
            GSWSchwimmend = 5;
            LP = 5;
            FO = 2;
            VTD = 14;
            SR = 0;
            KW = 16;
            GW = 14;
            Ini = 8;
            Ang = 4;

            // Typus und Waffen
            Typus = "Tier";
            List<string> waffenMerkmale = new List<string>();
            waffenMerkmale.Add("Stumpf");
            basisWaffe = new Waffe(1, 6, 0, 7, waffenMerkmale);
            //Debug.WriteLine("WGS: " + robustWaffe.WGS);
            Waffen.Add(basisWaffe);

            // Fertigkeiten
            Akrobatik = 7;
            Athletik = 8;
            Entschlossenheit = 6;
            Heimlichkeit = 6;
            Wahrnehmung = 5;
            Zähigkeit = 8;
            Fingerfertigkeit = 0;
            Schwimmen = 0;
            Jagdkunst = 0;
            Darbietung = 0;

            Merkmale.Add("Feigling");
            //Merkmale.Add("Schwächlich");
            GesStufe--;

            // Gesamtwerte
            KreaturWert = 2;
            LPMod = 3;

        }

        #endregion

        #region Rolle

        public void Kundschafter()
        {
            //Kopfwerte
            INT += 1;
            VTD += 2;
            KW += 1;
            GW += 1;
            GSW += 2;
            //LP += -1;

            //GesStufe--;

            //Waffe

            //Fertigkeiten
            //Athletik += 2;
            //Entschlossenheit += 2;
            Heimlichkeit += 3;
            Wahrnehmung += 5;
            //Jagdkunst += 9;
            hasJagdkunst = true;
            Jagdkunst += 2;

            //Meisterschaften
            Meisterschaften.Add("Heimlichkeit(I: Leisetreter)");

            // Merkmale
            Merkmale.Add("Dämmersicht");
            // Auswahl an Merkmalen
            List<string> auswahlItems = new List<string>();
            auswahlItems.Add("Fliegend");
            auswahlItems.Add("Taucher");
            auswahlItems.Add("Dunkelsicht");
            auswahlItems.Add("Taktiker");
            auswahlPopUp = new FormKreaturenAuswahl(this, auswahlItems);
            dialogresult = auswahlPopUp.ShowDialog();
            if (dialogresult == DialogResult.OK)
            {
                switch (auswahl)
                {
                    case "Fliegend":
                        Merkmale.Add("Fliegend");
                        break;
                    case "Taucher":
                        Merkmale.Add("Taucher");
                        break;
                    case "Taktiker":
                        Merkmale.Add("Taktiker");
                        break;
                    case "Dunkelsicht":
                        Merkmale.Add("Dunkelsicht");
                        if (Merkmale.Contains("Dämmersicht"))
                            Merkmale.Remove("Dämmersicht");
                        break;
                    default:
                        Debug.WriteLine("Fehler in Modul: " + System.Reflection.MethodBase.GetCurrentMethod().Name);
                        break;
                }
            }
            auswahlPopUp.Dispose();
            auswahlItems.Clear();
        }

        public void Kampf()
        {
            // Kopfwerte
            //KON += 1;
            //LP += 1;
            VTD += 3;   // 4
            KW += 2;    // 3
            GW += 2;
            GSW += 2;
            Ini += -2;
            FO += 3;
            Ang += 5;  // oder +5?

            // Waffe
            basisWaffe.TP += 1;
            basisWaffe.WGS += -1;
            basisWaffe.Merkmale.Remove("Stumpf");
            basisWaffe.Merkmale.Add("Scharf 2");
            //foreach (Waffe waffe in Waffen)
            //{
            //    waffe.TP += 1;
            //    //Debug.WriteLine("WGS: " + waffe.WGS);
            //    waffe.WGS = waffe.WGS -1;
            //    waffe.Merkmale.Remove("Stumpf");
            //}


            // Fertigkeiten
            Akrobatik += 1;
            Athletik += 1;
            Entschlossenheit += 2;
            Zähigkeit += 2;

            Merkmale.Remove("Feigling");

            //Meisterschaften
            Meisterschaften.Add("1x Kampf Schwelle 1");

        }

        public void Lasttier()
        {
            //Kopfwerte
            BEW += 2;
            KON += 3;
            STA += 3;

            GK += 3;
            GSW += 4;
            LP += 5; //3;    (Finalisierung)
            FO += 3;
            VTD += 3;
            KW += 4;
            GW += 2;
            Ang += 3;

            //Waffe
            //foreach (Waffe waffe in Waffen)
            //{
            //    waffe.W = 2;
            //}
            basisWaffe.W = 2;
            basisWaffe.WGS += 2;

            //Fertigkeiten
            Akrobatik += 3;
            Athletik += 8;
            Zähigkeit += 3;

            //Zauber

            //Meisterschaften
            Meisterschaften.Add("Atheletik(II: Muskelprotz)");

            // Merkmale
            //Merkmale.Remove("Schwächlich");
            GesStufe++;
            //LPMod = 5;
            basisWaffe.Merkmale.Add("Stumpf");

            //UpdateBerechneteWerte();
        }

        public void Familiar()
        {
            //Kopfwerte
            MYS += 2;
            VER += 1;
            WIL += 1;
            GSW += 1;
            FO += 6;

            VTD += 2;
            GW += 2;

            //Waffe


            //Fertigkeiten
            Entschlossenheit += 5;
            Heimlichkeit += 3;
            Wahrnehmung += 3;

            // Zauber
            AddZauberfertigkeit("Zauber1", 6);
            Zauber.Add("Grad 0");
            //Zauber.Add("Grad 1");

            //Meisterschaften


            // Merkmale

        }

        public void Maskottchen()
        {
            //Kopfwerte
            AUS += 1;
            BEW += 2;
            INT += 1;
            WIL += 1;
            GSW += 3;
            FO += 3;

            VTD += 1;
            KW += 1;
            GW += 2;
            //Waffe


            //Fertigkeiten
            Akrobatik += 4;
            Athletik += 3;
            Entschlossenheit += 2;
            Heimlichkeit += 4;
            Wahrnehmung += 3;

            auswahlItems.Add("Fingerfertigkeit");
            auswahlItems.Add("Darbietung");
            auswahlPopUp = new FormKreaturenAuswahl(this, auswahlItems);
            dialogresult = auswahlPopUp.ShowDialog();
            if (dialogresult == DialogResult.OK)
            {
                if (auswahl == "Fingerfertigkeit")
                {
                    Fingerfertigkeit += 3;
                    hasFingerfertigkeit = true;
                }
                else
                {
                    hasDarbietung = true;
                    Darbietung += 3;
                    Meisterschaften.Add("Darbietung(I: Geselle)");
                }
            }
            auswahlPopUp.Dispose();
            auswahlItems.Clear();

            hasEmpathie = true;
            Empathie += 1;

            // Zauber


            //Meisterschaften
            Meisterschaften.Add("Akrobatik(II: Meisterhafte Balance");
            Meisterschaften.Add("Athletik(I: Kletteraffe)");

            // Merkmale
        }
        #endregion

        #region Sonstige Module
        #region Größe

        public void Groß()
        {
            KON += 1;
            STA += 1;
            GK += 1;
            GSW += 2;
            LP += 2;

            Ang += 2;
            basisWaffe.TP += 2;
            basisWaffe.WGS += 1;
            // TODO Spezialwaffen!

            //Aktrobatik += 1;
            Athletik += 2;
            //Entschlossenheit += 1;
            //Heimlichkeit += 1;
            //Wahrnehmung += 1;
            Zähigkeit += 1;
            //Fingerfertigkeit += 1;
            //Schwimmen += 1;
            //Jagdkunst += 1;
            //Darbietung += 1;
            //Empathie += 1;

            // Weiteres Merkmal?
            Meisterschaften.Add("1x Meisterschaft");

            // if (Merkmale.Contains("Schwächlich"))
            auswahlItems.Add("Kein Schwächlich");
            if (Merkmale.Contains("Feigling"))
                auswahlItems.Add("Kein Feigling");
            if (!Merkmale.Contains("Dämmersicht"))
                auswahlItems.Add("Dämmersicht");
            else if (!Merkmale.Contains("Dunkelsicht"))
                auswahlItems.Add("Dunkelsicht");
            //auswahlItems.Add("LP +1");
            auswahlPopUp = new FormKreaturenAuswahl(this, auswahlItems);
            dialogresult = auswahlPopUp.ShowDialog();
            if (dialogresult == DialogResult.OK)
            {
                switch (auswahl)
                {
                    case "Kein Feigling":
                        Merkmale.Remove("Feigling");
                        break;
                    case "Dämmersicht":
                        Merkmale.Add("Dämmersicht");
                        break;
                    case "Dunkelsicht":
                        Merkmale.Add("Dunkelsicht");
                        if (Merkmale.Contains("Dämmersicht"))
                            Merkmale.Remove("Dämmersicht");
                        break;
                    case "Kein Schwächlich":
                        //GEsStufeSchwächlich--;
                        GesStufe++;
                        //Merkmale.Remove("Schwächlich");
                        break;
                    case "LP +1":
                        LP += 1;
                        break;
                    default:
                        Debug.WriteLine("Fehler in Modul: " + System.Reflection.MethodBase.GetCurrentMethod().Name);
                        break;
                }
            }
            auswahlPopUp.Dispose();
            auswahlItems.Clear();

            KreaturWert += 1;
            //UpdateBerechneteWerte();

        }

        public void Klein()
        {
            auswahlItems.Add("STÄ -1");
            auswahlItems.Add("BEW -1");
            auswahlPopUp = new FormKreaturenAuswahl(this, auswahlItems);
            dialogresult = auswahlPopUp.ShowDialog();
            if (dialogresult == DialogResult.OK)
            {
                if (auswahl == "STÄ -1")
                {
                    Debug.WriteLine(auswahl);
                    STA += -1;
                }
                else
                {
                    Debug.WriteLine(auswahl);
                    BEW += -1;
                }
            }
            auswahlPopUp.Dispose();
            auswahlItems.Clear();

            KON += -1;

            VTD += -1;
            KW += -1;
            GK += -1;
            GSW += -2;
            LP += -1;
            FO += -3; // 4

            Ang += -2;
            if(basisWaffe.W == 2)
            {
                basisWaffe.W = 1;
                basisWaffe.WGS += -2;
            }
            basisWaffe.WGS += 1;
            basisWaffe.Merkmale.Remove("Durchdringung 1");
            // TODO Spezialwaffen!

            Akrobatik += -1;
            Athletik += -2;
            Entschlossenheit += -1;
            Heimlichkeit += -1;
            Wahrnehmung += -1;
            Zähigkeit += -2;
            Fingerfertigkeit += -1;
            Schwimmen += -1;
            Jagdkunst += -1;
            Darbietung += -1;
            Empathie += -1;

            // entfernen
            minusMeisterschaften += 1;
            minusZauberfertigkeiten += 2; // (F) evtl. auf 1 runter,
            minusZauber += 2; // 3

            KreaturWert += -1;
            
        }

        public void Riesig()
        {
            KON += 2;   // 3        (Finalisierung)
            STA += 2;
            GK += 2;
            GSW += 4;
            LP += 3;    //5         (Finalisierung)

            VTD += 1;   //2         (Finalisierung)
            KW += 2;    //3         (Finalisierung)
            GW += 1;    //1         (Finalisierung)

            Ang += 4;
            basisWaffe.TP += 3;
            basisWaffe.WGS += 2;
            basisWaffe.Merkmale.Add("Wuchtig");
            // TODO Spezialwaffen!

            Akrobatik += 1;
            Athletik += 2;
            Entschlossenheit += 2;
            Heimlichkeit += 1;
            Wahrnehmung += 1;
            Zähigkeit += 2;
            Fingerfertigkeit += 1;
            Schwimmen += 1;
            Jagdkunst += 1;
            Darbietung += 1;
            Empathie += 1;

            // Gesundheistufen anpassung
            GesStufe++;

            auswahlItems.Add("SR +1");
            if (!Merkmale.Contains("Unbeherrschbar"))
                auswahlItems.Add("Unbeherrschbar");
            if (!Merkmale.Contains("Dämmersicht"))
                auswahlItems.Add("Dämmersicht");
            else if (!Merkmale.Contains("Dunkelsicht"))
                auswahlItems.Add("Dunkelsicht");
            if (!Merkmale.Contains("Schmerzresistenz"))
                auswahlItems.Add("Schmerzresistenz");
            else if (!Merkmale.Contains("Schmerzimmunität"))
                auswahlItems.Add("Schmerzimmunität");
            if (Merkmale.Contains("Feigling"))
                auswahlItems.Add("Kein Feigling");
            auswahlPopUp = new FormKreaturenAuswahl(this, auswahlItems);
            dialogresult = auswahlPopUp.ShowDialog();
            if (dialogresult == DialogResult.OK)
            {
                switch (auswahl)
                {
                    case "SR +1":
                        SR += 1;
                        break;
                    case "Unbeherrschbar":
                        Merkmale.Add("Unbeherrschbar");
                        break;
                    case "Dämmersicht":
                        Merkmale.Add("Dämmersicht");
                        break;
                    case "Dunkelsicht":
                        Merkmale.Add("Dunkelsicht");
                        if (Merkmale.Contains("Dämmersicht"))
                            Merkmale.Remove("Dämmersicht");
                        break;
                    case "Schmerzresistenz":
                        Merkmale.Add("Schmerzresistenz");
                        break;
                    case "Schmerzimmunität":
                        Merkmale.Add("Schmerzimmunität");
                        if (Merkmale.Contains("Schmerzresistenz"))
                            Merkmale.Remove("Schmerzresistenz");
                        break;
                    case "Kein Feigling":
                        Merkmale.Remove("Feigling");
                        break;
                    default:
                        Debug.WriteLine("Fehler in Modul: " + System.Reflection.MethodBase.GetCurrentMethod().Name);
                        break;
                }
            }
            auswahlPopUp.Dispose();
            auswahlItems.Clear();

            KreaturWert += 2;

        }

        public void Schrumpfen()
        {
            Besonderheiten.Add("Schrumpfen");
            KreaturWert += 1;
        }

        public void Winzig()
        {
            auswahlItems.Add("STÄ -1");
            auswahlItems.Add("BEW -1");
            auswahlPopUp = new FormKreaturenAuswahl(this, auswahlItems);
            dialogresult = auswahlPopUp.ShowDialog();
            if (dialogresult == DialogResult.OK)
            {
                if (auswahl == "STÄ -1")
                {
                    Debug.WriteLine(auswahl);
                    STA += -1;
                }
                else
                {
                    Debug.WriteLine(auswahl);
                    BEW += -1;
                }
            }
            auswahlPopUp.Dispose();
            auswahlItems.Clear();

            KON += -2;
            GK += -2;
            LP += -4;

            VTD += -4;
            KW += -5;

            FO += -6; // 7

            // Gesundheitstufenanpassung
            GesStufe--;

            Ang += -4;
            basisWaffe.TP += -3;
            basisWaffe.WGS += 2;

            basisWaffe.Merkmale.Remove("Durchdringung 1");            // oder TP -1;

            Akrobatik += -4;
            Athletik += -4;
            Entschlossenheit += -4;
            Heimlichkeit += -4;
            Wahrnehmung += -4;
            Zähigkeit += -5;
            Fingerfertigkeit += -4;
            Schwimmen += -4;
            Jagdkunst += -4;
            Darbietung += -4;
            Empathie += -4;


            minusMerkmale += 1;
            minusMeisterschaften += 3;
            minusZauberfertigkeiten += 4;
            minusZauber += 4;

            KreaturWert += -2;
        }

        #endregion

        #region Bestimmung
        public void Angrifslustig()
        {

            // Auswahl an Attributen
            auswahlItems.Add("STÄ +1");
            auswahlItems.Add("BEW +1");
            auswahlPopUp = new FormKreaturenAuswahl(this, auswahlItems);
            dialogresult = auswahlPopUp.ShowDialog();
            if (dialogresult == DialogResult.OK)
            {
                if (auswahl == "STÄ +1")
                {
                    STA += 1;
                }
                else
                {
                    BEW += +1;
                }
            }
            auswahlPopUp.Dispose();
            auswahlItems.Clear();

            INT += 1;

            VTD += 1;
            KW += 2;
            GW += 1;

            Ang += 2;
            Ini += -1;
            basisWaffe.TP += 1;
            basisWaffe.WGS += 1;

            Akrobatik += 1;
            Athletik += 1;
            //Entschlossenheit += 2;
            //Zähigkeit += 1;

            Meisterschaften.Add("Kampfmeisterschaft Schwelle 1");
            Meisterschaften.Add("Meisterschaft Schwelle 1");

            // Auswahl Stumpf entfernen oder Durchdringung 1
            // ''''''''''''''' HÄSSLICHSTER HACK ALLER ZEIGEN '''''''''''''''''''''''''''
            if (basisWaffe.Merkmale.Contains("Stumpf"))
            {
                basisWaffe.Merkmale.Remove("Stumpf");
            }
            else if (basisWaffe.Merkmale.Contains("Stumpf"))
            {
                basisWaffe.Merkmale.Remove("Stumpf");
            }
            else if (basisWaffe.Merkmale.Contains("Stumpf"))
            {
                basisWaffe.Merkmale.Remove("Stumpf");
            }else
            {
                basisWaffe.Merkmale.Add("Durchdringung 1");
            }

            #region Angriffslustig alt
            // Auswahl 2
            //auswahlItems.Add("SR +1");
            //auswahlItems.Add("Unbeherrschbar");
            //auswahlItems.Add("Taktiker");
            //if (Merkmale.Contains("Schwächlich"))
            //{
            //    auswahlItems.Add("Kein Schwächlich");
            //}
            //auswahlPopUp = new FormKreaturenAuswahl(this, auswahlItems);
            //dialogresult = auswahlPopUp.ShowDialog();
            //if (dialogresult == DialogResult.OK)
            //{
            //    switch (auswahl)
            //    {
            //        case "SR +1":
            //            SR += 1;
            //            break;
            //        case "Unbeherrschbar":
            //            Merkmale.Add("Unbeherrschbar");
            //            break;
            //        case "Taktiker":
            //            Merkmale.Add("Taktiker");
            //            break;
            //        case "Kein Schwächlich":
            //            Merkmale.Remove("Schwächlich");
            //            break;
            //        default:
            //            Debug.WriteLine("Fehler, Angriffslustig Auswahl");
            //            break;
            //    }
            //}
            //auswahlPopUp.Dispose();
            //auswahlItems.Clear();
            #endregion

            KreaturWert += 1;

        }

        public void Beobachter()
        {
            BEW += 1;
            WIL += 1;
            FO += 2;

            VTD += 2;
            KW += 2;
            GW += 2;

            Athletik += 1;
            Entschlossenheit += 1;
            Heimlichkeit += 2;
            Wahrnehmung += 4;
            Empathie += 2;

            Meisterschaften.Add("1x Meisterschaft Schwelle 1");
            Meisterschaften.Add("1x Wahrnehmung Schwelle 2");

            if (!Merkmale.Contains("Taktiker"))
                auswahlItems.Add("Taktiker");
            if (!Merkmale.Contains("Konzentrationsstärke"))
                auswahlItems.Add("Konzentrationsstärke");
            else if (!Merkmale.Contains("Unbeherrschbar"))
                auswahlItems.Add("Unbeherrschbar");
            if (Merkmale.Contains("Feigling"))
                auswahlItems.Add("Kein Feigling");
            else if (Merkmale.Contains("Furchtimmunität"))
                auswahlItems.Add("Furchtimmunität");
            if (!Merkmale.Contains("Dämmersicht"))
                auswahlItems.Add("Dämmersicht");
            else if (!Merkmale.Contains("Dunkelsicht"))
                auswahlItems.Add("Dunkelsicht");
            auswahlPopUp = new FormKreaturenAuswahl(this, auswahlItems);
            dialogresult = auswahlPopUp.ShowDialog();
            if (dialogresult == DialogResult.OK)
            {
                switch (auswahl)
                {
                    case "Furchtimmunität":
                        Merkmale.Add("Furchtimmunität");
                        break;
                    case "Dämmersicht":
                        Merkmale.Add("Dämmersicht");
                        break;
                    case "Dunkelsicht":
                        Merkmale.Add("Dunkelsicht");
                        Merkmale.Remove("Dämmersicht");
                        break;
                    case "Konzentrationsstärke":
                        Merkmale.Add("Konzentrationsstärke");
                        break;
                    case "Unbeherrschbar":
                        Merkmale.Add("Unbeherrschbar");
                        break;
                    case "Taktiker":
                        Merkmale.Add("Taktiker");
                        break;
                    case "Kein Feigling":
                        Merkmale.Remove("Feigling");
                        break;
                    default:
                        Debug.WriteLine("Fehler in Modul: " + System.Reflection.MethodBase.GetCurrentMethod().Name);
                        break;
                }
            }
            auswahlPopUp.Dispose();
            auswahlItems.Clear();

            KreaturWert += 1;
        }

        public void Helfer()
        {
            AUS += 1;
            INT += 1;

            VTD += 2;
            KW += 1;
            GW += 3;

            Ang += 1;

            Akrobatik += 3;
            Heimlichkeit += 2;
            Wahrnehmung += 3;
            Empathie += 1;

            if (!hasFingerfertigkeit)
            {
                hasFingerfertigkeit = true;
                //Fingerfertigkeit += 3;
            }
            else
            {
                Fingerfertigkeit += 3;
            }

            if (hasEmpathie)
                Empathie += 3;
            else
            {
                hasEmpathie = true;
            }
            KreaturWert += 1;

            Meisterschaften.Add("1x Meisterschaft Schwelle 1");
            Meisterschaften.Add("1x Fingerfertigkeit Schwelle 1");

            Merkmale.Add("Taktiker");
        }

        public void Jäger()
        {
            BEW += 1;
            INT += 1;

            VTD += 2;
            KW += 1; // 1 (Finaliesierung)
            GW += 1;

            Ang += 1;

            Meisterschaften.Add("Meisterschaft Schwelle 1");
            Meisterschaften.Add("Meisterschaft Schwelle 1");

            if (basisWaffe.Merkmale.Contains("Stumpf"))
                basisWaffe.Merkmale.Remove("Stumpf");
            else basisWaffe.Merkmale.Add("Kritisch 1");

            Athletik += 2;
            Entschlossenheit += 1;
            Heimlichkeit += 1;
            Wahrnehmung += 3;

            if(!hasJagdkunst)
            {
                hasJagdkunst = true;
                Jagdkunst += 3;
            }
            else
            {
                Jagdkunst += 6;
            }

            KreaturWert += 1;
        }

        public void Magisch()
        {
            auswahlItems.Add("AUS +1");
            auswahlItems.Add("VER +1");
            auswahlPopUp = new FormKreaturenAuswahl(this, auswahlItems);
            dialogresult = auswahlPopUp.ShowDialog();
            if (dialogresult == DialogResult.OK)
            {
                if (auswahl == "AUS +1")
                {
                    Debug.WriteLine(auswahl);
                    AUS += 1;
                }
                else
                {
                    Debug.WriteLine(auswahl);
                    VER += +1;
                }
            }
            auswahlPopUp.Dispose();
            auswahlItems.Clear();

            MYS += 1;
            FO += 6; // 6

            string zauberAuswahl;
            if (Zauberfertigkeiten.Count > 0)
            {
                auswahlItems.Add("Zauberfertigkeit 9");
                foreach (Zauberfertigkeit zauber in Zauberfertigkeiten)
                {
                    zauberAuswahl = zauber.Name + " +3";
                    auswahlItems.Add(zauberAuswahl);
                }
                auswahlPopUp = new FormKreaturenAuswahl(this, auswahlItems);
                dialogresult = auswahlPopUp.ShowDialog();
                if (dialogresult == DialogResult.OK)
                {
                    if (auswahl == "Zauberfertigkeit 9")
                    {
                        int count = Zauberfertigkeiten.Count + 1;
                        string newZauber = "Zauber" + count;
                        Zauberfertigkeiten.Add(new Zauberfertigkeit(newZauber, 9));
                    }
                    else
                    {
                        foreach (Zauberfertigkeit zauber in Zauberfertigkeiten)
                        {
                            zauberAuswahl = zauber.Name + " +3";
                            if (zauberAuswahl == auswahl)
                            {
                                zauber.Value += 3;
                            }
                        }
                    }
                }
                auswahlPopUp.Dispose();
                auswahlItems.Clear();
            }
            else
            {
                int count = Zauberfertigkeiten.Count + 1;
                string newZauber = "Zauber" + count;
                Zauberfertigkeiten.Add(new Zauberfertigkeit("Zauber", 9));
            }

            // Zweiter Zauber
            auswahlItems.Add("Zauberfertigkeit 6");
            foreach (Zauberfertigkeit zauber in Zauberfertigkeiten)
            {
                zauberAuswahl = zauber.Name + " +3";
                auswahlItems.Add(zauberAuswahl);
            }
            auswahlPopUp = new FormKreaturenAuswahl(this, auswahlItems);
            dialogresult = auswahlPopUp.ShowDialog();
            if (dialogresult == DialogResult.OK)
            {
                if (auswahl == "Zauberfertigkeit 6")
                {
                    int count = Zauberfertigkeiten.Count + 1;
                    string newZauber = "Zauber" + count;
                    Zauberfertigkeiten.Add(new Zauberfertigkeit(newZauber, 6));
                }
                else
                {
                    foreach (Zauberfertigkeit zauber in Zauberfertigkeiten)
                    {
                        zauberAuswahl = zauber.Name + " +3";
                        if (zauberAuswahl == auswahl)
                        {
                            zauber.Value += 3;
                        }
                    }
                }
            }
            auswahlPopUp.Dispose();
            auswahlItems.Clear();

            VTD += 1;
            GW += 3;
            KW += 2;
            Ang += 2;

            if (Merkmale.Contains("Unbeherrschbar") && Merkmale.Contains("Konzentrationsstärke"))
            {
                Debug.WriteLine("Es konnte keine Auswahl getroffen werden, Modul Magisch");
            }
            else
            {
                if (!Merkmale.Contains("Unbeherrschbar"))
                    auswahlItems.Add("Unbeherrschbar");
                if (!Merkmale.Contains("Konzentrationsstärke"))
                    auswahlItems.Add("Konzentrationsstärke");
                if (Merkmale.Contains("Feigling"))
                    auswahlItems.Add("Kein Feigling");
                auswahlPopUp = new FormKreaturenAuswahl(this, auswahlItems);
                dialogresult = auswahlPopUp.ShowDialog();
                if (dialogresult == DialogResult.OK)
                {
                    switch (auswahl)
                    {
                        case "Unbeherrschbar":
                            Merkmale.Add("Unbeherrschbar");
                            break;
                        case "Konzentrationsstärke":
                            Merkmale.Add("Konzentrationsstärke");
                            break;
                        case "Kein Feigling":
                            Merkmale.Remove("Feigling");
                            break;
                        default:
                            Debug.WriteLine("Fehler in Modul: " + System.Reflection.MethodBase.GetCurrentMethod().Name);
                            break;
                    }
                }
                auswahlPopUp.Dispose();
                auswahlItems.Clear();
            } 

            Meisterschaften.Add("Zaubermeisterschaft Schwelle 1");
            Zauber.Add("Grad 0");
            Zauber.Add("Grad 0"); // 0
            Zauber.Add("Grad 1"); // 1
            Zauber.Add("Grad 2"); // 1

            KreaturWert += 2;

        }

        // Evtl nach Zusätzliche Fähigkeiten verschieben
        public void Prächtig()
        {
            AUS += 1;
            WIL += 1;
            FO += 2;
            VTD += 1;
            KW += 1;
            GW += 2;

            Ang += 2;

            Akrobatik += 1;
            Athletik += 1;
            Entschlossenheit += 1;
            Heimlichkeit += 1;
            Wahrnehmung += 1;
            Zähigkeit += 1;
            Fingerfertigkeit += 1;
            Schwimmen += 1;
            Jagdkunst += 1;
            Darbietung += 1;
            Empathie += 1;

            Zauber.Add("Grad 0");

            Meisterschaften.Add("Meisterschaft Schwelle 1");
            Meisterschaften.Add("Meisterschaft Schwelle 2");

            if (!Merkmale.Contains("Taktiker"))
                auswahlItems.Add("Taktiker");
            if (!Merkmale.Contains("Konzentrationsstärke"))
                auswahlItems.Add("Konzentrationsstärke");
            else if (!Merkmale.Contains("Unbeherrschbar"))
                auswahlItems.Add("Unbeherrschbar");
            if (Merkmale.Contains("Feigling"))
                auswahlItems.Add("Kein Feigling");
            auswahlPopUp = new FormKreaturenAuswahl(this, auswahlItems);
            dialogresult = auswahlPopUp.ShowDialog();
            if (dialogresult == DialogResult.OK)
            {
                switch (auswahl)
                {
                    case "Konzentrationsstärke":
                        Merkmale.Add("Konzentrationsstärke");
                        break;
                    case "Unbeherrschbar":
                        Merkmale.Add("Unbeherrschbar");
                        break;
                    case "Taktiker":
                        Merkmale.Add("Taktiker");
                        break;
                    case "Kein Feigling":
                        Merkmale.Remove("Feigling");
                        break;
                    default:
                        Debug.WriteLine("Fehler in Modul: " + System.Reflection.MethodBase.GetCurrentMethod().Name);
                        break;
                }
            }
            auswahlPopUp.Dispose();
            auswahlItems.Clear();

            KreaturWert += 1;
        }

        public void Reittier()
        {
            // Darf nicht vorher schon Reittier sein

            KON += 1;
            STA += 1;
            GSW += 3;
            //LP += 2;

            VTD += 1;
            KW += 3;

            Athletik += 6;
            Entschlossenheit += 1;
            Zähigkeit += 1;
            Ang += 1;

            Meisterschaften.Add("Handgemenge (I: Vorstürmen)");
            Meisterschaften.Add("Athletik (II: Flinker Verfolger)");
            Merkmale.Add("Erschöpfungsresistenz 1");
            basisWaffe.Merkmale.Add("Stumpf");
            //basisWaffe.Merkmale.Add("Stumpf");

            Typus = Typus + ", Reittier";
            KreaturWert += 1;
        }

        public void BesReittier()
        {
            // Darf nicht vorher schon Reittier sein

            // Land + Wasser +1 Kreatur
            // Fliegend +2 Kreautr
            // nur wasser wie Land
            Reittier();
            KreaturWert += 2;
        }

        public void Verteidiger()
        {

            // Mindestens GK 4; oder auch nicht

            auswahlItems.Add("STÄ +1");
            auswahlItems.Add("KON +1");
            auswahlItems.Add("BEW +1");
            auswahlPopUp = new FormKreaturenAuswahl(this, auswahlItems);
            dialogresult = auswahlPopUp.ShowDialog();
            if (dialogresult == DialogResult.OK)
            {
                switch (auswahl)
                {
                    case "STÄ +1":
                        STA += 1;
                        break;
                    case "KON +1":
                        KON += 1;
                        break;
                    case "BEW +1":
                        BEW += 1;
                        break;
                    default:
                        Debug.WriteLine("Fehler in Modul: " + System.Reflection.MethodBase.GetCurrentMethod().Name);
                        break;
                }
            }
            auswahlPopUp.Dispose();
            auswahlItems.Clear();

            INT += 1;

            Ang += 1;

            VTD += 2;
            KW += 2;
            GW += 1;
            GK += 1;
            LP += 1;

            Wahrnehmung += 2;
            Akrobatik += 1;
            Athletik += 1;
            Entschlossenheit += 3;
            Zähigkeit += 2;

            //SR += 2;

            Meisterschaften.Add("Handgemenge(I: Verteidiger)");
            //Meisterschaften.Add("Kampfmeisterschaft Schwelle 1");

            if (!Merkmale.Contains("Taktiker"))
                auswahlItems.Add("Taktiker");
            if (!Merkmale.Contains("Dämmersicht"))
                auswahlItems.Add("Dämmersicht");
            else if (!Merkmale.Contains("Dunkelsicht"))
                auswahlItems.Add("Dunkelsicht");
            if (!Merkmale.Contains("Furchterregend"))
                auswahlItems.Add("Furchterregend");
            auswahlPopUp = new FormKreaturenAuswahl(this, auswahlItems);
            dialogresult = auswahlPopUp.ShowDialog();
            if (dialogresult == DialogResult.OK)
            {
                switch (auswahl)
                {
                    case "Taktiker":
                        Merkmale.Add("Taktiker");
                        break;
                    case "Dämmersicht":
                        Merkmale.Add("Dämmersicht");
                        break;
                    case "Dunkelsicht":
                        Merkmale.Add("Dunkelsicht");
                        if (Merkmale.Contains("Dämmersicht"))
                            Merkmale.Remove("Dämmersicht");
                        break;
                    case "Furchterregend":
                        if (Furchterregend == 0)
                        {
                            Furchterregend += 10;
                        }
                        else
                            Furchterregend += 6;
                        break;
                    default:
                        Debug.WriteLine("Fehler, Verteidiger Auswahl");
                        break;
                }
            }
            auswahlPopUp.Dispose();
            auswahlItems.Clear();

            KreaturWert += 1;
        }

        // Evtl nach Anfälligkeit verschieben
        public void Willensstark()
        {

            VER += 1;
            WIL += 1;
            FO += 4; // 2 (Finalisierung)

            VTD += 1;
            KW += 1;
            GW += 2;

            Ang += 1;

            Akrobatik += 1;
            Athletik += 1;
            Entschlossenheit += 4;
            Heimlichkeit += 1;
            Wahrnehmung += 1;
            Zähigkeit += 1;
            Fingerfertigkeit += 1;
            Schwimmen += 1;
            Jagdkunst += 1;
            Darbietung += 1;
            Empathie += 1;

            Zauber.Add("Grad 0");

            Meisterschaften.Add("Meisterschaft Schwelle 1");
            //Meisterschaften.Add("Meisterschaft Schwelle 1");

            if (!Merkmale.Contains("Taktiker"))
                auswahlItems.Add("Taktiker");
            if (!Merkmale.Contains("Konzentrationsstärke"))
                auswahlItems.Add("Konzentrationsstärke");
            else if (!Merkmale.Contains("Unbeherrschbar"))
                auswahlItems.Add("Unbeherrschbar");
            if (Merkmale.Contains("Feigling"))
                auswahlItems.Add("Kein Feigling");
            else auswahlItems.Add("Furchtimmunität");
            auswahlPopUp = new FormKreaturenAuswahl(this, auswahlItems);
            dialogresult = auswahlPopUp.ShowDialog();
            if (dialogresult == DialogResult.OK)
            {
                switch (auswahl)
                {
                    case "Furchtimmunität":
                        Merkmale.Add("Furchtimmunität");
                        break;
                    case "Konzentrationsstärke":
                        Merkmale.Add("Konzentrationsstärke");
                        break;
                    case "Unbeherrschbar":
                        Merkmale.Add("Unbeherrschbar");
                        break;
                    case "Taktiker":
                        Merkmale.Add("Taktiker");
                        break;
                    case "Kein Feigling":
                        Merkmale.Remove("Feigling");
                        break;
                    default:
                        Debug.WriteLine("Fehler in Modul: " + System.Reflection.MethodBase.GetCurrentMethod().Name);
                        break;
                }
            }
            auswahlPopUp.Dispose();
            auswahlItems.Clear();

            KreaturWert += 1;
        }

        // Evtl nach Zusätzliche Fähigkeiten verschieben
        public void ZähneKlauen()
        {


            /*** Zusäztliche Waffe abhängig von der Kreaturenstufe ***/

            auswahlItems.Add("STÄ +1");
            auswahlItems.Add("BEW +1");
            auswahlPopUp = new FormKreaturenAuswahl(this, auswahlItems);
            dialogresult = auswahlPopUp.ShowDialog();
            if (dialogresult == DialogResult.OK)
            {
                if (auswahl == "STÄ +1")
                {
                    STA += 1;
                }
                else
                {
                    BEW += +1;
                }
            }
            auswahlPopUp.Dispose();
            auswahlItems.Clear();

            auswahlItems.Add("AUS +1");
            auswahlItems.Add("KON +1");
            auswahlPopUp = new FormKreaturenAuswahl(this, auswahlItems);
            dialogresult = auswahlPopUp.ShowDialog();
            if (dialogresult == DialogResult.OK)
            {
                if (auswahl == "AUS +1")
                {
                    AUS += 1;
                }
                else
                {
                    KON += +1;
                }
            }
            auswahlPopUp.Dispose();
            auswahlItems.Clear();

            VTD += 2;
            KW += 3;
            GW += 2;
            GSW += 3;
            Ini += -2;
            Ang += 4;


            if (!isKoloss)
            {
                auswahlItems.Add("Zusätzliche Waffe");
                auswahlItems.Add("Verbesserte Basis Waffe");
                auswahlPopUp = new FormKreaturenAuswahl(this, auswahlItems);
                dialogresult = auswahlPopUp.ShowDialog();
                if (dialogresult == DialogResult.OK)
                {
                    if (auswahl == "Zusätzliche Waffe")
                    {
                        //List<string> waffenMerkmale = new List<string>();
                        //waffenMerkmale.Add("Scharf 2");
                        //waffenMerkmale.Add("Durchdringung 1");
                        //zusatzWaffe1 = new Waffe(1, 6, 5, 8, waffenMerkmale);
                        //Waffen.Add(zusatzWaffe1);
                        zusWaffe = true;
                    }
                    else
                    {
                        basisWaffe.TP += 1;
                        if (basisWaffe.Merkmale.Contains("Scharf 2"))
                        {
                            basisWaffe.Merkmale.Add("Scharf 3");
                            basisWaffe.Merkmale.Remove("Scharf 2");
                        }
                        else
                        {
                            basisWaffe.Merkmale.Add("Scharf 2");
                        }
                        //basisWaffe.Merkmale.Add("Durchdringung 1");
                        basisWaffe.Merkmale.Remove("Stumpf");
                    }
                }
                auswahlPopUp.Dispose();
                auswahlItems.Clear();
            }
            else
            {
                //List<string> waffenMerkmale = new List<string>();
                //waffenMerkmale.Add("Scharf 2");
                //waffenMerkmale.Add("Durchdringung 1");
                //zusatzWaffe1 = new Waffe(1, 6, 5, 8, waffenMerkmale);
                //Waffen.Add(zusatzWaffe1);
                zusWaffe = true;
            }

            Heimlichkeit += 1;
            Wahrnehmung += 1;
            Akrobatik += 1;
            Athletik += 1;
            Entschlossenheit += 1;
            Zähigkeit += 1;
            // Darbietung etc.

            Meisterschaften.Add("Kampfmeisterschaft Schwelle 1");
            Meisterschaften.Add("Meisterschaft Schwelle 1");

            KreaturWert += 2;
        }

        //TODO
        public void Zirkustier()
        {
            AUS += 1;
            WIL += 1;
            FO += 2;

            VTD += 2;
            KW += 1;
            GW += 2; // 3 (Finalisierung)

            Ang += 1;

            Akrobatik += 3;
            Heimlichkeit += 3;
            Wahrnehmung += 1;
            Empathie += 1;

            if (!hasDarbietung)
            {
                hasDarbietung = true;
                //Darbietung += 3;
            }
            else
            {
                Darbietung += 3;
            }

            if (hasEmpathie)
                Empathie += 3;
            else
            {
                hasEmpathie = true;
            }

            Meisterschaften.Add("Darbietung Schwelle 1");
            Meisterschaften.Add("Meisterschaft Schwelle 1");

            Merkmale.Add("Taktiker");

            KreaturWert += 1;

        }

        // Evtl nach Zusätzliche Fähigkeiten verschieben
        public void ZusätzlicheZauber()
        {
            MYS += 1;
            WIL += 1;
            FO += 4;

            string zauberAuswahl;
            //int count = Zauberfertigkeiten.Count + 1;
            string newZauber = "Zauber" + (Zauberfertigkeiten.Count + 1);
            if (Zauberfertigkeiten.Count > 0)
            {
                foreach (Zauberfertigkeit zauber in Zauberfertigkeiten)
                {
                    zauberAuswahl = zauber.Name + " +3";
                    auswahlItems.Add(zauberAuswahl);
                }
                auswahlItems.Add("Zauberfertigkeit 6");
                auswahlPopUp = new FormKreaturenAuswahl(this, auswahlItems);
                dialogresult = auswahlPopUp.ShowDialog();
                if (dialogresult == DialogResult.OK)
                {
                    if (auswahl == "Zauberfertigkeit 6")
                    {
                        Zauberfertigkeiten.Add(new Zauberfertigkeit(newZauber, 6));
                    }
                    else
                    {
                        foreach (Zauberfertigkeit zauber in Zauberfertigkeiten)
                        {
                            zauberAuswahl = zauber.Name + " +3";
                            if (zauberAuswahl == auswahl)
                            {
                                zauber.Value += 3;
                            }
                        }
                    }
                }
                auswahlPopUp.Dispose();
                auswahlItems.Clear();
            }
            else
            {
                Zauberfertigkeiten.Add(new Zauberfertigkeit(newZauber, 6));
            }

            Zauber.Add("Grad 1");
            Zauber.Add("Grad 1");
            Zauber.Add("Grad 2");

            KreaturWert += 1;
        }

        #endregion

        #region Anzahl
        public void Koloss()
        {
            VTD += 2;
            KW += 3;
            GW += 2;

            Ang += 2;

            GK += 3;

            Merkmale.Add("Koloss 2 (Basis Waffe, Zusäzliche Waffe)");
            KreaturWert += 3;
            Debug.WriteLine("TODO: ");
        }

        //TODO ?
        public void Rudel()
        {
            VTD += 1;
            KW += 1;
            GW += 1;
            Ang += 2;
            FO += 3;

            /*** merhfach erlernbar ***/


            Besonderheiten.Add("Schwarmwesen, Schwarmstufe 1+ Anzahl Schwarmwesenmodule.");
            KreaturWert += 1;
        }

        private bool firstSchwarmwesen = true;

        public void Schwarmwesen()
        {

            VTD += 1;
            KW += 1;
            GW += 1;

            FO += 2;

            Akrobatik += 1;
            Athletik += 1;
            Entschlossenheit += 1;
            Heimlichkeit += 1;
            Wahrnehmung += 1;
            Zähigkeit += 1;
            Fingerfertigkeit += 1;
            Schwimmen += 1;
            Jagdkunst += 1;
            Darbietung += 1;

            Meisterschaften.Add("Meisterschaft Schwelle 1");
            Zauber.Add("Grad 0");

            if (firstSchwarmwesen)
            {
                Besonderheiten.Add("Schwarmwesen, ein Wesen das aus vielen Besteht." +
                    "Benötigt nur die Schwarmalpha Spezialisierung um als Schwarm genutzt zu werden." +
                    "Schwarmstufe 1+ Anzahl Schwarmwesenmodule");
            }
            if (firstSchwarmwesen)
            {
                auswahlItems.Add("1x");
                auswahlItems.Add("2x");
                auswahlItems.Add("3x");
                auswahlItems.Add("4x");
                auswahlPopUp = new FormKreaturenAuswahl(this, auswahlItems);
                dialogresult = auswahlPopUp.ShowDialog();
                if (dialogresult == DialogResult.OK)
                {
                    switch (auswahl)
                    {
                        case "1x":
                            break;
                        case "2x":
                            firstSchwarmwesen = false;
                            Schwarmwesen();
                            break;
                        case "3x":
                            firstSchwarmwesen = false;
                            Schwarmwesen();
                            Schwarmwesen();
                            break;
                        case "4x":
                            firstSchwarmwesen = false;
                            Schwarmwesen();
                            Schwarmwesen();
                            Schwarmwesen();
                            break;
                        default:
                            Debug.WriteLine("Fehler in Modul: " + System.Reflection.MethodBase.GetCurrentMethod().Name);
                            break;
                    }
                }
                auswahlPopUp.Dispose();
                auswahlItems.Clear();
            }

            KreaturWert += 1;
        }
        #endregion

        #region Zusätzliche Fähigkeiten
        public void Bedrohlich()
        {
            STA += 1;

            VTD += 1;
            KW += 1;
            GW += 1;

            SR += 2;

            Ang += 2;
            basisWaffe.Merkmale.Add("Kritisch 1");

            Entschlossenheit += 1;
            Zähigkeit += 1;
            Heimlichkeit += 2;
            Wahrnehmung += 3;

            Meisterschaften.Add("Meisterschaft Schwelle 1");

            //UpdateFurchterregend();
            if (Furchterregend == 0)
            {
                Furchterregend += 10;
            }
            else
                Furchterregend += 6;
            //Furchterregend += 3;

            KreaturWert += 1;
        }

        public void Blutrausch()
        {
            INT += 1;

            VTD += 1;
            KW += 1;
            GW += 1;

            Ang += 2;
            Ini += -2;
            basisWaffe.Merkmale.Add("Kritisch 1");

            Akrobatik += 2;
            Athletik += 2;
            Wahrnehmung += 1;
            Zähigkeit += 1;

            Meisterschaften.Add("Kampf Schwelle 1");

            auswahlItems.Add("Blutrausch (3 / Verbündeter Bewusstlos/Tod)");
            auswahlItems.Add("Blutrausch (2 / Verletzt)");
            auswahlItems.Add("Blutrausch (1 / Sonnenschein)");
            auswahlPopUp = new FormKreaturenAuswahl(this, auswahlItems);
            dialogresult = auswahlPopUp.ShowDialog();
            if (dialogresult == DialogResult.OK)
            {
                switch (auswahl)
                {
                    case "Blutrausch (3 / Verbündeter Bewusstlos":
                        Merkmale.Add("Blutrausch (3 / Verbündeter Bewusstlos");
                        break;
                    case "Blutrausch (2 / Verletzt)":
                        Merkmale.Add("Butrausch (2 / Verletzt");
                        break;
                    case "Blutrausch (1 / Sonnenschein)":
                        Merkmale.Add("Blutrausch (1 / Sonnenschein)");
                        break;
                    default:
                        Debug.WriteLine("Fehler in Modul: " + System.Reflection.MethodBase.GetCurrentMethod().Name);
                        break;
                }
            }
            auswahlPopUp.Dispose();
            auswahlItems.Clear();

            if (Merkmale.Contains("Feigling"))
                auswahlItems.Add("Kein Feigling");
            else if (!Merkmale.Contains("Furchtimmunität"))
                auswahlItems.Add("Furchtimmunität");
            if (!Merkmale.Contains("Taktiker"))
                auswahlItems.Add("Taktiker");
            auswahlPopUp = new FormKreaturenAuswahl(this, auswahlItems);
            dialogresult = auswahlPopUp.ShowDialog();
            if (dialogresult == DialogResult.OK)
            {
                switch (auswahl)
                {
                    case "Kein Feigling":
                        Merkmale.Remove("Feigling");
                        break;
                    case "Taktiker":
                        Merkmale.Add("Taktiker");
                        break;
                    case "Furchtimmunität":
                        Merkmale.Add("Furchtimmunität");
                        break;
                    default:
                        Debug.WriteLine("Fehler in Modul: " + System.Reflection.MethodBase.GetCurrentMethod().Name);
                        break;
                }
            }
            auswahlPopUp.Dispose();
            auswahlItems.Clear();

            KreaturWert += 1;
        }

        public void Dämmerbote()
        {
            VTD += 2;
            KW += 1;
            GW += 1;

            GSW += 2;
            GSWFliegend += 2;
            GSWSchwimmend += 2;

            Athletik += 2;
            Heimlichkeit += 3;
            Wahrnehmung += 3;

            Ang += 1;

            Meisterschaften.Add("Meisterschaft Schwelle 1");
            Meisterschaften.Add("Meisterschaft Schwelle 1");

            if (Merkmale.Contains("Dämmersicht"))
            {
                Merkmale.Add("Dunkelsicht");
                Merkmale.Remove("Dämmersicht");
            }
            else
                Merkmale.Add("Dämmersicht");

            if (Merkmale.Contains("Feigling"))
                auswahlItems.Add("Kein Feigling");
            else if (!Merkmale.Contains("Furchtimmunität"))
                auswahlItems.Add("Furchtimmunität");
            if (!Merkmale.Contains("Taktiker"))
                auswahlItems.Add("Taktiker");
            auswahlPopUp = new FormKreaturenAuswahl(this, auswahlItems);
            dialogresult = auswahlPopUp.ShowDialog();
            if (dialogresult == DialogResult.OK)
            {
                switch (auswahl)
                {
                    case "Kein Feigling":
                        Merkmale.Remove("Feigling");
                        break;
                    case "Taktiker":
                        Merkmale.Add("Taktiker");
                        break;
                    case "Furchtimmunität":
                        Merkmale.Add("Furchtimmunität");
                        break;
                    default:
                        Debug.WriteLine("Fehler in Modul: " + System.Reflection.MethodBase.GetCurrentMethod().Name);
                        break;
                }
            }
            auswahlPopUp.Dispose();
            auswahlItems.Clear();

            KreaturWert += 1;
        }

        public void Falle()
        {
            VTD += 1;
            KW += 1;
            GW += 1;

            Ang += 1;

            Akrobatik += 1;
            Athletik += 1;
            Entschlossenheit += 1;
            Heimlichkeit += 3;
            Wahrnehmung += 1;
            Zähigkeit += 1;
            Fingerfertigkeit += 1;
            Schwimmen += 1;
            Jagdkunst += 1;
            Darbietung += 1;
            Empathie += 1;

            auswahlItems.Add("Falle (4 / Ringend)");
            auswahlItems.Add("Falle (5 / Lahm)");
            auswahlItems.Add("Falle (4 / Benommen 1)");
            auswahlPopUp = new FormKreaturenAuswahl(this, auswahlItems);
            dialogresult = auswahlPopUp.ShowDialog();
            if (dialogresult == DialogResult.OK)
            {
                switch (auswahl)
                {
                    case "Falle (4 / Ringend)":
                        Merkmale.Add("Falle (4 / Ringend)");
                        KreaturWert += 1;
                        break;
                    case "Falle (5 / Lahm)":
                        Merkmale.Add("Falle (5 / Lahm)");
                        KreaturWert += 2;
                        Ang += 1;
                        break;
                    case "Falle (4 / Benommen 1)":
                        Merkmale.Add("Falle (4 / Benommen 1)");
                        KreaturWert += 3;
                        Ang += 2;
                        break;
                    default:
                        Debug.WriteLine("Fehler in Modul: " + System.Reflection.MethodBase.GetCurrentMethod().Name);
                        break;
                }
            }
            auswahlPopUp.Dispose();
            auswahlItems.Clear();

            KreaturWert += 1;
        }

        public void Fliegend()
        {
            VTD += 1;
            GW += 1;

            Ang += 1;
            basisWaffe.TP += 1;

            Akrobatik += 1;
            Athletik += 1;
            Entschlossenheit += 1;
            Heimlichkeit += 1;
            Wahrnehmung += 3;
            Zähigkeit += 1;
            Fingerfertigkeit += 1;
            Schwimmen += 1;
            Jagdkunst += 1;
            Darbietung += 1;
            Empathie += 1;

            GSWFliegend += 5;
            if (!Merkmale.Contains("Fliegend"))
            Merkmale.Add("Fliegend");
            else
                Meisterschaften.Add("Meisterschaft Schwelle 2");

            //Meisterschaften.Add("Meisterschaft Schwelle 1");

            KreaturWert += 1;

        }

        public void Gestaltwandler()
        {

            auswahlItems.Add("1");
            auswahlItems.Add("2");
            auswahlItems.Add("3");
            auswahlItems.Add("4");
            auswahlItems.Add("5");
            auswahlItems.Add("6");
            auswahlItems.Add("7");
            auswahlItems.Add("8");
            auswahlPopUp = new FormKreaturenAuswahl(this, auswahlItems, "Kosten Zusätzliche Gestalt");
            dialogresult = auswahlPopUp.ShowDialog();
            if (dialogresult == DialogResult.OK)
            {
                switch (auswahl)
                {
                    case "1":
                        KreaturWert += 1;
                        break;
                    case "2":
                        KreaturWert += 2;
                        break;
                    case "3":
                        KreaturWert += 3;
                        break;
                    case "4":
                        KreaturWert += 4;
                        break;
                    case "5":
                        KreaturWert += 5;
                        break;
                    case "6":
                        KreaturWert += 6;
                        break;
                    case "7":
                        KreaturWert += 7;
                        break;
                    case "8":
                        KreaturWert += 8;
                        break;
                    default:
                        Debug.WriteLine("Fehler in Modul: " + System.Reflection.MethodBase.GetCurrentMethod().Name);
                        break;
                }
            }
            auswahlPopUp.Dispose();
            auswahlItems.Clear();

            Besonderheiten.Add("Zusätzliche Gestalt"+ "(" + auswahl + "), Geistige Fähigkeiten und Zauber der ersten Gestalt bleiben erhalten.");

            // evtl. Kreaturwert Kosten +1
        }

        public void Giftig()
        {
            VTD += 1;
            KW += 1;
            GW += 1;

            LP += -1;
            Ang += 1;

            // Gift auswahl in 3 Kathegorien: Schwach, Mittel, Stark

            auswahlItems.Add("Gift Schwach");
            auswahlItems.Add("Gift Mittel");
            auswahlItems.Add("Gift Stark");
            auswahlPopUp = new FormKreaturenAuswahl(this, auswahlItems);
            dialogresult = auswahlPopUp.ShowDialog();
            if (dialogresult == DialogResult.OK)
            {
                switch (auswahl)
                {
                    case "Gift Schwach":
                        Merkmale.Add("Gift (Blut 1 / Benommen / 60 Ticks)");
                        KreaturWert += 1;
                        break;
                    case "Gift Mittel":
                        Merkmale.Add("Gift (Blut 2 / Lahm / 1 Stunde)");
                        KreaturWert += 2;
                        Ang += 1;
                        break;
                    case "Gift Stark":
                        Merkmale.Add("Gift (Blut 3 / Erschöpft / 30 Ticks");
                        KreaturWert += 3;
                        Ang += 2;
                        break;
                    default:
                        Debug.WriteLine("Fehler in Modul: " + System.Reflection.MethodBase.GetCurrentMethod().Name);
                        break;
                }
            }
            auswahlPopUp.Dispose();
            auswahlItems.Clear();
        }

        //TODO
        public void Körperlos()
        {
            VTD += 1;
            KW += 2;
            GW += 1;

            FO += 2;

            SR += 4;

            Zauber.Add("Grad 0");

            Merkmale.Add("Körperlos");
            Besonderheiten.Add("Kann sich durch Ritzen und Schlitze zwängen, Immun gegen Fallschaden und Zustand Blutend, löst keine Gelegenheitsangriffe aus");

            KreaturWert += 1;
        }

        public void Krankheitsträger()
        {
            VTD += 1;
            KW += 2;
            GW += 1;

            Ang += 1;

            auswahlItems.Add("Schwache Krankheit");
            auswahlItems.Add("Mittlere Krankheit");
            auswahlItems.Add("Schwere Krankheit");

            auswahlPopUp = new FormKreaturenAuswahl(this, auswahlItems);
            dialogresult = auswahlPopUp.ShowDialog();
            if (dialogresult == DialogResult.OK)
            {
                switch (auswahl)
                {
                    case "Schwache Krankheit":
                        Merkmale.Add("Krankheit (Schwach)");
                        KreaturWert += 1;
                        break;
                    case "Mittlere Krankheit":
                        Merkmale.Add("Krankheit (Mittel)");
                        KreaturWert += 2;
                        Ang += 1;
                        break;
                    case "Schwere Krankheit":
                        Merkmale.Add("Krankheit (Schwer)");
                        KreaturWert += 3;
                        Ang += 2;
                        break;
                    default:
                        Debug.WriteLine("Fehler in Modul: " + System.Reflection.MethodBase.GetCurrentMethod().Name);
                        break;
                }
            }
            auswahlPopUp.Dispose();
            auswahlItems.Clear();
        }

        public void Sänger()
        {
            AUS += 1;
            WIL += 1;
            FO += 2; // 4 (Finalisierung)

            VTD += 1;
            KW += 1;
            GW += 2;

            Akrobatik += 2;
            Entschlossenheit += 1;
            Heimlichkeit += 3;
            Wahrnehmung += 3;
            Empathie += 3;

            if (hasDarbietung)
            {
                Darbietung += 3;
            }
            else
            {
                hasDarbietung = true;
                Darbietung += 2;
            }

            if (hasEmpathie)
            {
                Empathie += 3;
            }
            else
            {
                hasEmpathie = true;
                Empathie += 1;
            }
            Meisterschaften.Add("Darbietung Schwelle 1");
            Meisterschaften.Add("Darbietung Schwelle 2");

            Zauber.Add("Grad 0");

            Besonderheiten.Add("Kann Lieder aus Darbietung nutzen ohne Sprachbegabt zu sein.");

            KreaturWert += 1;
        }

        public void Schwimmer()
        {
            KON += 1;
            LP += 1;

            VTD += 1;
            KW += 2;
            GW += 1;

            GSWSchwimmend += 5;

            Ang += 2;

            if (hasSchwimmen)
            {
                Schwimmen += 5;
            }else
            {
                hasSchwimmen = true;
                Schwimmen += 3;
            }

            Meisterschaften.Add("Schwimmen Schwelle 1");
            Meisterschaften.Add("Schwimmen Schwelle 2");

            if (!Merkmale.Contains("Taucher"))
                Merkmale.Add("Taucher");
            else
                Meisterschaften.Add("Meisterschaft Schwelle 2");

            Meisterschaften.Add("Meisterschaft Schwelle 1");

            KreaturWert += 1;
        }

        public void Sprachbegabt()
        {
            WIL += 1; // Oder VER +1 ; muss noch implementiert werden
            FO += 2;

            VTD += 1;
            KW += 1;
            GW += 1;

            Akrobatik += 1;
            Athletik += 1;
            Entschlossenheit += 1;
            Heimlichkeit += 1;
            Wahrnehmung += 1;
            Zähigkeit += 1;
            Fingerfertigkeit += 1;
            Schwimmen += 1;
            Jagdkunst += 1;
            Darbietung += 1;

            Zauber.Add("Grad 0");

            Meisterschaften.Add("Meisterschaft Schwelle 1");

            Besonderheiten.Add("Kann im Rahmen seiner Tierichen Geistigen Fähigkeiten reden");

            KreaturWert += 1;
        }

        //TODO
        public void Teleporation()
        {
            VTD += 1;
            KW += 1;
            GW += 1;

            Merkmale.Add("Teleportation (2)");

            // Kostet Teleportation Auslösezeit?

            KreaturWert += 1;
        }

        //TODO
        public void Unsichtbar()
        {
            VTD += 1;
            KW += 1;
            GW += 1;

            Merkmale.Add("Unsichtbar");

            // Modul evtl ganz streichen, je nach umgang mit Spuk

            KreaturWert += 2;
        }

        public void Vernunftbegabt()
        {
            VER += 1;

            VTD += 1;
            KW += 1;
            GW += 1;

            Akrobatik += 1;
            Athletik += 1;
            Entschlossenheit += 1;
            Heimlichkeit += 1;
            Wahrnehmung += 1;
            Zähigkeit += 1;
            Fingerfertigkeit += 1;
            Schwimmen += 1;
            Jagdkunst += 1;
            Darbietung += 1;

            Meisterschaften.Add("Meisterschaft Schwelle 1");
            if (!Merkmale.Contains("Taktiker"))
                Merkmale.Add("Taktiker");
            Zauber.Add("Grad 0");

            Besonderheiten.Add("Ist Intelligent, kann nicht über Tierfühung ausgebildet werden.");
            KreaturWert += 1;
        }

        #region Zusätzliche Fähigkeiten alt

        public void Empathisch()
        {
            // veraltet
        }

        public void SpezSensorik()
        {
            // veraltet
        }

        #endregion

        #endregion

        #region Anfälligkeit

        public void Fragil()
        {
            //auswahlItems.Add("KON -1");
            auswahlItems.Add("BEW -1");
            auswahlItems.Add("STA -1");
            auswahlPopUp = new FormKreaturenAuswahl(this, auswahlItems);
            dialogresult = auswahlPopUp.ShowDialog();
            if (dialogresult == DialogResult.OK)
            {
                switch (auswahl)
                {
                    case "KON -1":
                        KON += -1;
                        //LP += -1;
                        break;
                    case "BEW -1":
                        BEW += -1;
                        //GSW += -1;
                        break;
                    case "STA -1":
                        STA += -1;
                        //VTD += -1;
                        break;
                    default:
                        Debug.WriteLine("Fehler in Modul: " + System.Reflection.MethodBase.GetCurrentMethod().Name);
                        break;
                }
            }
            auswahlPopUp.Dispose();
            auswahlItems.Clear();

            KON += - 1;

            VTD += -1;
            KW += -1;
            GW += -1;
            LP += -1;
            SR += -2;

            FO += -2;

            // Gesundheitstufe anpassen
            GesStufe--;

            Ang += -1;

            if (basisWaffe.W == 2)
            {
                basisWaffe.W = 1;
            }
            basisWaffe.WGS += 1;
            // TODO Spezialwaffen!

            Akrobatik += -1;
            Athletik += -1;
            Entschlossenheit += -1;
            Heimlichkeit += -1;
            Wahrnehmung += -1;
            Zähigkeit += -2;
            Fingerfertigkeit += -1;
            Schwimmen += -1;
            Jagdkunst += -1;
            Darbietung += -1;

            // Meisterschaft entfernen
            minusMeisterschaften++;
            minusZauberfertigkeiten += 1;
            minusZauber += 2;

            KreaturWert += -1;
        }

        public void Immunitäten()
        {

            auswahlItems.Add("KON +1");
            auswahlItems.Add("BEW +1");
            auswahlItems.Add("STA +1");
            auswahlPopUp = new FormKreaturenAuswahl(this, auswahlItems);
            dialogresult = auswahlPopUp.ShowDialog();
            if (dialogresult == DialogResult.OK)
            {
                switch (auswahl)
                {
                    case "KON +1":
                        KON += 1;
                        LP += 1;
                        break;
                    case "BEW +1":
                        BEW += 1;
                        GSW += 1;
                        break;
                    case "STA +1":
                        STA += 1;
                        VTD += 1;
                        break;
                    default:
                        Debug.WriteLine("Fehler in Modul: " + System.Reflection.MethodBase.GetCurrentMethod().Name);
                        break;
                }
            }
            auswahlPopUp.Dispose();
            auswahlItems.Clear();

            VTD += 1;
            KW += 1;
            GW += 1;

            WIL += 1;
            FO += 2;

            Ang += 1;

            Akrobatik += 1;
            Athletik += 1;
            Zähigkeit += 2;
            Wahrnehmung += 1;

            auswahlItems.Add("SR +1");
            if (!Merkmale.Contains("Betäubungsimmunität"))
                auswahlItems.Add("Betäubungsimmunität");
            if (!Merkmale.Contains("Schmerzresistenz"))
                auswahlItems.Add("Schmerzresistenz");
            else if (!Merkmale.Contains("Schmerzimmunität"))
                auswahlItems.Add("Schmerzimmunität");
            if (!Merkmale.Contains("Furchtimmunität"))
                auswahlItems.Add("Furchtimmunität");
            if (!Merkmale.Contains("Giftimmunität"))
                auswahlItems.Add("Giftimmunität");
            auswahlPopUp = new FormKreaturenAuswahl(this, auswahlItems);
            dialogresult = auswahlPopUp.ShowDialog();
            if (dialogresult == DialogResult.OK)
            {
                switch (auswahl)
                {
                    case "SR +1":
                        SR += 1;
                        break;
                    case "Betäubungsimmunität":
                        Merkmale.Add("Betäubungsimmunität");
                        break;
                    case "Schmerzresistenz":
                        Merkmale.Add("Schmerzresistenz");
                        break;
                    case "Schmerzimmunität":
                        Merkmale.Add("Schmerzimmunität");
                        if (Merkmale.Contains("Schmerzresistenz"))
                            Merkmale.Remove("Schmerzresistenz");
                        break;
                    case "Furchtimmunität":
                        Merkmale.Add("Furchtimmunität");
                        break;
                    case "Giftimmunität":
                        Merkmale.Add("Giftimmunität");
                        break;
                    default:
                        Debug.WriteLine("Fehler, Immunitäten Auswahl");
                        break;
                }
            }
            auswahlPopUp.Dispose();
            auswahlItems.Clear();

            auswahlItems.Add("SR +1");
            if (!Merkmale.Contains("Betäubungsimmunität"))
                auswahlItems.Add("Betäubungsimmunität");
            if (!Merkmale.Contains("Schmerzresistenz"))
                auswahlItems.Add("Schmerzresistenz");
            else if (!Merkmale.Contains("Schmerzimmunität"))
                auswahlItems.Add("Schmerzimmunität");
            if (!Merkmale.Contains("Furchtimmunität"))
                auswahlItems.Add("Furchtimmunität");
            if (!Merkmale.Contains("Giftimmunität"))
                auswahlItems.Add("Giftimmunität");
            auswahlPopUp = new FormKreaturenAuswahl(this, auswahlItems);
            dialogresult = auswahlPopUp.ShowDialog();
            if (dialogresult == DialogResult.OK)
            {
                switch (auswahl)
                {
                    case "SR +1":
                        SR += 1;
                        break;
                    case "Betäubungsimmunität":
                        Merkmale.Add("Betäubungsimmunität");
                        break;
                    case "Schmerzresistenz":
                        Merkmale.Add("Schmerzresistenz");
                        break;
                    case "Schmerzimmunität":
                        Merkmale.Add("Schmerzimmunität");
                        if (Merkmale.Contains("Schmerzresistenz"))
                            Merkmale.Remove("Schmerzresistenz");
                        break;
                    case "Furchtimmunität":
                        Merkmale.Add("Furchtimmunität");
                        break;
                    case "Giftimmunität":
                        Merkmale.Add("Giftimmunität");
                        break;
                    default:
                        Debug.WriteLine("Fehler, Immunitäten Auswahl");
                        break;
                }
            }
            auswahlPopUp.Dispose();
            auswahlItems.Clear();

            KreaturWert += 1;
        }

        public void Nullsumme()
        {
            Merkmale.Add("Resistenz gegen Schadensart 4");
            Merkmale.Add("Verwundbarkeit gegen Schadensart");

            // keine Kreaturwert Kosten
        }

        public void Resitenzen()
        {

            auswahlItems.Add("KON +1");
            auswahlItems.Add("BEW +1");
            auswahlItems.Add("STA +1");
            auswahlPopUp = new FormKreaturenAuswahl(this, auswahlItems);
            dialogresult = auswahlPopUp.ShowDialog();
            if (dialogresult == DialogResult.OK)
            {
                switch (auswahl)
                {
                    case "KON +1":
                        KON += 1;
                        break;
                    case "BEW +1":
                        BEW += 1;
                        break;
                    case "STA +1":
                        STA += 1;
                        break;
                    default:
                        Debug.WriteLine("Fehler in Modul: " + System.Reflection.MethodBase.GetCurrentMethod().Name);
                        break;
                }
            }
            auswahlPopUp.Dispose();
            auswahlItems.Clear();

            VTD += 1;
            KW += 1; // 1 (Finalisierung)
            GW += 1;

            LP += 1;
            WIL += 1;
            FO += 2;
            SR += 1;

            Ang += 1;

            Zähigkeit += 2;
            Athletik += 2;
            Wahrnehmung += 1;

            auswahlItems.Add("SR +1");
            auswahlItems.Add("Resistenz gegen Schadensart 3");
            auswahlPopUp = new FormKreaturenAuswahl(this, auswahlItems);
            dialogresult = auswahlPopUp.ShowDialog();
            if (dialogresult == DialogResult.OK)
            {
                switch (auswahl)
                {
                    case "SR +1":
                        SR += 1;
                        break;
                    case "Resistenz gegen Schadensart 3":
                        Merkmale.Add("Resistenz gegen Schadensart 3");
                        break;
                    default:
                        Debug.WriteLine("Fehler in Modul: " + System.Reflection.MethodBase.GetCurrentMethod().Name);
                        break;
                }
            }
            auswahlPopUp.Dispose();
            auswahlItems.Clear();

            auswahlItems.Add("SR +1");
            auswahlItems.Add("Hitzeresistenz 3");
            auswahlItems.Add("Kälteresistenz 3");
            auswahlPopUp = new FormKreaturenAuswahl(this, auswahlItems);
            dialogresult = auswahlPopUp.ShowDialog();
            if (dialogresult == DialogResult.OK)
            {
                switch (auswahl)
                {
                    case "Hitzeresistenz 3":
                        Merkmale.Add("Hitzeresistenz 3");
                        break;
                    case "Kälteresistenz 3":
                        Merkmale.Add("Kälteresistenz 3");
                        break;
                    case "SR +1":
                        SR += 1;
                        break;
                    default:
                        Debug.WriteLine("Fehler in Modul: " + System.Reflection.MethodBase.GetCurrentMethod().Name);
                        break;
                }
            }
            auswahlPopUp.Dispose();
            auswahlItems.Clear();

            KreaturWert += 1;

        }

        public void Verwundbar()
        {

            KON += -1;
            LP += -1;

            VTD += -1;
            KW += -1;
            GW += -1;

            FO += -2;

            Ang += -1;

            if (basisWaffe.W == 2)
            {
                basisWaffe.W = 1;
            }
            basisWaffe.WGS += 1;
            // TODO Spezialwaffen!

            Akrobatik += -1;
            Athletik += -1;
            Entschlossenheit += -1;
            Heimlichkeit += -1;
            Wahrnehmung += -1;
            Zähigkeit += -1;
            Fingerfertigkeit += -1;
            Schwimmen += -1;
            Jagdkunst += -1;
            Darbietung += -1;

            auswahlItems.Add("Verwundbarkeit gegen Schadensart");
            auswahlItems.Add("Lichtempfindlich 2");
            auswahlPopUp = new FormKreaturenAuswahl(this, auswahlItems);
            dialogresult = auswahlPopUp.ShowDialog();
            if (dialogresult == DialogResult.OK)
            {
                if (auswahl == "Verwundbarkeit gegen Schadensart")
                {
                    Merkmale.Add("Verwundbarkeit gegen Schadensart");
                }
                else
                {
                    Merkmale.Add("Lichtempfindlich 2");
                }
            }
            auswahlPopUp.Dispose();
            auswahlItems.Clear();

            // entfernen
            minusMeisterschaften++;
            minusZauberfertigkeiten += 1;
            minusZauber += 2;

            KreaturWert += -1;
        }

        public void Zäh()
        {
            KON += 1;
            STA += 1;

            VTD += 1;
            KW += 2;
            LP += 1;

            // Gesundheitstufen Modifikation
            GesStufe++;

            Ang += 1;
            Akrobatik += 2;
            Athletik += 2;
            Zähigkeit += 4;

            Meisterschaften.Add("Meisterschaft Schwelle 1");

            Merkmale.Add("Erschöpfungsresistenz 1");

            auswahlItems.Add("SR +2");
            if (!Merkmale.Contains("Betäubungsimmunität"))
                auswahlItems.Add("Betäubungsimmunität");
            if (!Merkmale.Contains("Schmerzresistenz"))
                auswahlItems.Add("Schmerzresistenz");
            else if (!Merkmale.Contains("Schmerzimmunität"))
                auswahlItems.Add("Schmerzimmunität");
            auswahlPopUp = new FormKreaturenAuswahl(this, auswahlItems);
            dialogresult = auswahlPopUp.ShowDialog();
            if (dialogresult == DialogResult.OK)
            {
                switch (auswahl)
                {
                    case "SR +2":
                        SR += 2;
                        break;
                    case "Betäubungsimmunität":
                        Merkmale.Add("Betäubungsimmunität");
                        break;
                    case "Schmerzresistenz":
                        Merkmale.Add("Schmerzresistenz");
                        break;
                    case "Schmerzimmunität":
                        Merkmale.Add("Schmerzimmunität");
                        if (Merkmale.Contains("Schmerzresistenz"))
                            Merkmale.Remove("Schmerzresistenz");
                        break;
                    default:
                        Debug.WriteLine("Fehler in Modul: " + System.Reflection.MethodBase.GetCurrentMethod().Name);
                        break;
                }
            }
            auswahlPopUp.Dispose();
            auswahlItems.Clear();

            KreaturWert += 1;

        }


        #endregion

        #region Typ
        public void Feenwesen()
        {
            AUS += 1;
            FO += 2;
            VTD += 1;
            KW += 1;
            GW += 1;

            Ang += 1;

            Akrobatik += 1;
            Athletik += 1;
            Entschlossenheit += 1;
            Heimlichkeit += 1;
            Wahrnehmung += 1;
            Zähigkeit += 1;
            Fingerfertigkeit += 1;
            Schwimmen += 1;
            Jagdkunst += 1;
            Darbietung += 1;

            string zauberAuswahl;
            if (Zauberfertigkeiten.Count > 0)
            {
                if (Zauberfertigkeiten.Count == 1)
                {
                    Zauberfertigkeiten[0].Value += 2;
                }
                else
                {
                    foreach (Zauberfertigkeit zauber in Zauberfertigkeiten)
                    {
                        zauberAuswahl = zauber.Name + " +2";
                        auswahlItems.Add(zauberAuswahl);
                    }
                    auswahlPopUp = new FormKreaturenAuswahl(this, auswahlItems);
                    dialogresult = auswahlPopUp.ShowDialog();
                    if (dialogresult == DialogResult.OK)
                    {
                        foreach (Zauberfertigkeit zauber in Zauberfertigkeiten)
                        {
                            zauberAuswahl = zauber.Name + " +2";
                            if (zauberAuswahl == auswahl)
                            {
                                zauber.Value += 2;
                            }
                        }
                    }
                    auswahlPopUp.Dispose();
                    auswahlItems.Clear();
                }
            }
            else
            {
                int count = Zauberfertigkeiten.Count + 1;
                string newZauber = "Zauber" + count;
                Zauberfertigkeiten.Add(new Zauberfertigkeit(newZauber, 3));
            }

            Zauber.Add("Grad 0");

            Merkmale.Add("Feenblut");
            Besonderheiten.Add("Keine Abzüge durch die Umgebung in Feenwelten");
            Typus = Typus + ", Feenwesen";
            KreaturWert += 1;
        }

        //TODO
        public void Geist()
        {
            AUS += -1;
            //VTD += 2;
            //KW += 1;
            //GW += 1;
            FO += 2;

            Merkmale.Add("Geist");
            //Merkmale.Add("Furcherregend 10");
            //if (Furchterregend == 0)
            //{
            //    Furchterregend += 10;
            //}
            //else
            //    Furchterregend += 3;

            string zauberAuswahl;
            if (Zauberfertigkeiten.Count > 0)
            {
                if (Zauberfertigkeiten.Count == 1)
                {
                    Zauberfertigkeiten[0].Value += 2;
                }
                else
                {
                    foreach (Zauberfertigkeit zauber in Zauberfertigkeiten)
                    {
                        zauberAuswahl = zauber.Name + " +2";
                        auswahlItems.Add(zauberAuswahl);
                    }
                    auswahlPopUp = new FormKreaturenAuswahl(this, auswahlItems);
                    dialogresult = auswahlPopUp.ShowDialog();
                    if (dialogresult == DialogResult.OK)
                    {
                        foreach (Zauberfertigkeit zauber in Zauberfertigkeiten)
                        {
                            zauberAuswahl = zauber.Name + " +2";
                            if (zauberAuswahl == auswahl)
                            {
                                zauber.Value += 2;
                            }
                        }
                    }
                    auswahlPopUp.Dispose();
                    auswahlItems.Clear();
                }
            }
            else
            {
                int count = Zauberfertigkeiten.Count + 1;
                string newZauber = "Zauber" + count;
                Zauberfertigkeiten.Add(new Zauberfertigkeit(newZauber, 3));
            }

            //SR += 2;
            Zauber.Add("Grad 0");

            if (!Merkmale.Contains("Dämmersicht"))
            {
                Merkmale.Add("Dämmersicht");
            }
            else
            {
                Merkmale.Remove("Dämmersicht");
                Merkmale.Add("Dunkelsicht");
            }

            Besonderheiten.Add("Immun gegen Ersticken, Krankheiten, Überansträngung, Verhungern und Zustand Sterbend");
            Typus = Typus + ", Geist";
            KreaturWert += 1;
        }
        
        public void Unterwasserwesen()
        {
            Merkmale.Add("Unterwasserwesen");
            if (!hasSchwimmen)
            {
                hasSchwimmen = true;
                Schwimmen += 5;
                GSWSchwimmend += 3;
                GSW = 0;
            }
            else
            {
                Schwimmen += 10;
                GSW = 0;
            }

            //KreaturWert += 1;
        }

        // TODO
        public void Untot()
        {
            AUS += -2;

            VTD += 2;
            KW += 2;
            GW += 2;

            Ang += 3;
            basisWaffe.TP += 2;
            basisWaffe.WGS += 1;

            Akrobatik += 1;
            Athletik += 1;
            Entschlossenheit += 1;
            Heimlichkeit += 1;
            Wahrnehmung += 1;
            Zähigkeit += 1;
            Fingerfertigkeit += 1;
            Schwimmen += 1;
            Jagdkunst += 1;
            Darbietung += 1;

            Merkmale.Add("Untot");

            if (Furchterregend == 0)
            {
                Furchterregend += 10;
            }
            else
                Furchterregend += 3;

            Merkmale.Add("Verwundbarkeit gegen Licht");
            Merkmale.Add("Verwundbarkeit gegen Feuer");

            Merkmale.Add("Betäubungsimmunität");
            Merkmale.Add("Schmerzimmunität");
            Merkmale.Add("Giftimmunität");

            if (!Merkmale.Contains("Dämmersicht"))
            {
                Merkmale.Add("Dämmersicht");
            }
            else
                Merkmale.Add("Dunkelsicht");

            Besonderheiten.Add("Immun gegen Ersticken, Krankheiten, Überansträngung, Verhungern und Zustände Blutend, Panisch, Sterbend");
            Typus = Typus + ", Untot";
            KreaturWert += 3; // +2 (Finalisierung)
        }

        #endregion

        #endregion
        #endregion

    }

    public class Zauberfertigkeit
    {
        public string Name;
        public int Value;

        public Zauberfertigkeit(string name, int value)
        {
            Name = name;
            Value = value;
        }

        public bool Exists(Zauberfertigkeit zf)
        {
            if (zf.Name == Name)
            {
                return true;
            }
            else
            {
                return false;
            }

        }
    }

}




