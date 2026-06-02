import java.util.ArrayList;
import java.util.Random;

// отдел - склад кассы завозы
public class Otdel {
    private String nazvanie;
    private VidTovara vid;                    // какой вид тут продают
    private ArrayList<Partiya> sklad = new ArrayList<Partiya>();
    private ArrayList<Kassa> kassy = new ArrayList<Kassa>();
    private int minPauza, maxPauza;           // через сколько шагов снова завоз
    private int minPartiya, maxPartiya;       // сколько штук везут
    private int sleduyuschiyZavoz;            // когда следующий завоз
    private double vyruchkaOtdela;

    Otdel(String nazvanie, VidTovara vid, int minPauza, int maxPauza, int minP, int maxP, Random rnd) {
        this.nazvanie = nazvanie;
        this.vid = vid;
        this.minPauza = minPauza;
        this.maxPauza = maxPauza;
        this.minPartiya = minP;
        this.maxPartiya = maxP;
        this.sleduyuschiyZavoz = sluchaynoeChislo(minPauza, maxPauza, rnd);
    }

    private int sluchaynoeChislo(int a, int b, Random rnd) {
        return a + rnd.nextInt(b - a + 1);
    }

    public String getNazvanie() { return nazvanie; }
    public VidTovara getVid() { return vid; }
    public ArrayList<Partiya> getSklad() { return sklad; }
    public ArrayList<Kassa> getKassy() { return kassy; }
    public double getVyruchka() { return vyruchkaOtdela; }

    public void dobavitKassu(Kassa k) { kassy.add(k); }

    // завезли на склад, скоропорту считаем когда протухнет
    public void zavezti(Tovar t, int kol, int tekuschiyShag) {
        int srok = -1;
        if (t.isSkoroporta()) {
            srok = tekuschiyShag + t.getSrokHraneniya();
        }
        sklad.add(new Partiya(t, kol, srok));
    }

    // сколько штук t лежит в отделе
    public int kolichestvoTovara(Tovar t) {
        int summa = 0;
        for (int i = 0; i < sklad.size(); i++) {
            Partiya p = sklad.get(i);
            if (p.getTovar() == t) summa = summa + p.getKolichestvo();
        }
        return summa;
    }

    // взять со склада, сначала старые партии (они в начале списка)
    public int vzyat(Tovar t, int kol) {
        int vzyato = 0;
        int i = 0;
        while (i < sklad.size() && vzyato < kol) {
            Partiya p = sklad.get(i);
            if (p.getTovar() == t && p.getKolichestvo() > 0) {
                int nuzhno = kol - vzyato;
                int est = p.getKolichestvo();
                int beryom;
                if (nuzhno < est) beryom = nuzhno; else beryom = est;
                p.otnyatKol(beryom);
                vzyato = vzyato + beryom;
            }
            i = i + 1;
        }
        ubratPustyePartii();
        return vzyato;
    }

    // не дождался на кассе - вернули в отдел
    public void vernut(Tovar t, int kol, int tekuschiyShag) {
        if (kol <= 0) return;
        zavezti(t, kol, tekuschiyShag);
    }

    private void ubratPustyePartii() {
        int i = 0;
        while (i < sklad.size()) {
            if (sklad.get(i).getKolichestvo() <= 0) {
                sklad.remove(i);
            } else {
                i = i + 1;
            }
        }
    }

    // выкинуть просрочку
    public int spisatProsrochku(int tekuschiyShag) {
        int spisano = 0;
        int i = 0;
        while (i < sklad.size()) {
            Partiya p = sklad.get(i);
            if (p.getShagSpisaniya() > 0 && p.getShagSpisaniya() <= tekuschiyShag) {
                spisano = spisano + p.getKolichestvo();
                sklad.remove(i);
            } else {
                i = i + 1;
            }
        }
        return spisano;
    }

    // пора завоз - случайный товар этого отдела
    public void mozhetZavestiTovary(int tekuschiyShag, ArrayList<Tovar> vseTovary, Random rnd) {
        if (tekuschiyShag < sleduyuschiyZavoz) return;
        ArrayList<Tovar> nashi = new ArrayList<Tovar>();
        for (int i = 0; i < vseTovary.size(); i++) {
            Tovar tv = vseTovary.get(i);
            if (tv.getVid() == vid) nashi.add(tv);
        }
        if (nashi.size() > 0) {
            Tovar t = nashi.get(rnd.nextInt(nashi.size()));
            int kol = sluchaynoeChislo(minPartiya, maxPartiya, rnd);
            zavezti(t, kol, tekuschiyShag);
        }
        sleduyuschiyZavoz = tekuschiyShag + sluchaynoeChislo(minPauza, maxPauza, rnd);
    }

    // касса где очередь короче
    public Kassa naimeneeZagruzhennaya() {
        if (kassy.size() == 0) return null;
        Kassa luchshaya = kassy.get(0);
        for (int i = 1; i < kassy.size(); i++) {
            Kassa k = kassy.get(i);
            if (k.dlinaOcheredi() < luchshaya.dlinaOcheredi()) luchshaya = k;
        }
        return luchshaya;
    }

    public void dobavitVyruchku(double s) { vyruchkaOtdela = vyruchkaOtdela + s; }
}
