import java.util.ArrayList;
import java.util.Random;

// тут вся симуляция крутится
public class Simulyaciya {
    private ArrayList<Otdel> otdely = new ArrayList<Otdel>();
    private ArrayList<Tovar> vseTovary = new ArrayList<Tovar>();
    private ArrayList<VidTovara> vseVidy = new ArrayList<VidTovara>();
    private Random rnd = new Random(42);    // 42 чтоб каждый раз одно и то же было

    private int tekuschiyShag;
    private int sleduyuschiyPokupatel = 5;  // первый чел на 5 шаге
    private int sledNomer = 1;
    private int ushliBezObsluzhivaniya;     // кто не дождался кассы

    private int vsegoPokupateleyObsluzheno; // для средних
    private double vsegoSummaPokupok;

    // продажи считаю массивами, hashmap нельзя
    private int[] prodanoKol;
    private double[] prodanoSumma;

    // стартовые товары отделы кассы
    public void zapolnitDemo() {
        VidTovara frukty = new VidTovara("Фрукты");
        VidTovara hleb = new VidTovara("Хлеб");
        VidTovara moloko = new VidTovara("Молочка");
        VidTovara napitki = new VidTovara("Напитки");
        vseVidy.add(frukty);
        vseVidy.add(hleb);
        vseVidy.add(moloko);
        vseVidy.add(napitki);

        Tovar yabloko = new Tovar("Яблоко", frukty, 80, false, 0, true, 10);
        Tovar persik = new Tovar("Персик", frukty, 150, true, 6, true, 20);
        Tovar baton = new Tovar("Батон", hleb, 45, true, 3, false, 0);
        Tovar bulka = new Tovar("Булка", hleb, 30, true, 2, false, 0);
        Tovar molokoT = new Tovar("Молоко", moloko, 90, true, 5, false, 0);
        Tovar tvorog = new Tovar("Творог", moloko, 130, true, 4, false, 0);
        Tovar sok = new Tovar("Сок", napitki, 110, false, 0, true, 15);
        Tovar voda = new Tovar("Вода", napitki, 50, false, 0, false, 0);
        vseTovary.add(yabloko); vseTovary.add(persik); vseTovary.add(baton);
        vseTovary.add(bulka); vseTovary.add(molokoT); vseTovary.add(tvorog);
        vseTovary.add(sok); vseTovary.add(voda);

        Otdel otdelFr = new Otdel("Отдел фруктов", frukty, 5, 10, 20, 40, rnd);
        Otdel otdelHl = new Otdel("Отдел хлеба", hleb, 4, 8, 15, 30, rnd);
        Otdel otdelMl = new Otdel("Молочный отдел", moloko, 6, 12, 20, 35, rnd);
        Otdel otdelNp = new Otdel("Отдел напитков", napitki, 7, 14, 25, 50, rnd);

        otdelFr.dobavitKassu(new Kassa("Касса Ф1"));
        otdelFr.dobavitKassu(new Kassa("Касса Ф2"));
        otdelHl.dobavitKassu(new Kassa("Касса Х1"));
        otdelMl.dobavitKassu(new Kassa("Касса М1"));
        otdelMl.dobavitKassu(new Kassa("Касса М2"));
        otdelNp.dobavitKassu(new Kassa("Касса Н1"));

        otdelFr.zavezti(yabloko, 30, 0);
        otdelFr.zavezti(persik, 15, 0);
        otdelHl.zavezti(baton, 20, 0);
        otdelHl.zavezti(bulka, 25, 0);
        otdelMl.zavezti(molokoT, 30, 0);
        otdelMl.zavezti(tvorog, 15, 0);
        otdelNp.zavezti(sok, 25, 0);
        otdelNp.zavezti(voda, 40, 0);

        otdely.add(otdelFr);
        otdely.add(otdelHl);
        otdely.add(otdelMl);
        otdely.add(otdelNp);

        prodanoKol = new int[vseTovary.size()];
        prodanoSumma = new double[vseTovary.size()];
    }

    // один шаг времени
    public void shag() {
        tekuschiyShag = tekuschiyShag + 1;

        // просрочку убрать
        for (int i = 0; i < otdely.size(); i++) {
            otdely.get(i).spisatProsrochku(tekuschiyShag);
        }

        // сезонные цены иногда меняются
        for (int i = 0; i < vseTovary.size(); i++) {
            Tovar t = vseTovary.get(i);
            if (t.isSezonniy() && rnd.nextInt(10) == 0) {
                t.smenitCenu(rnd);
            }
        }

        // завоз в отделы
        for (int i = 0; i < otdely.size(); i++) {
            otdely.get(i).mozhetZavestiTovary(tekuschiyShag, vseTovary, rnd);
        }

        // новый покупатель раз в 1-3 шага
        if (tekuschiyShag >= sleduyuschiyPokupatel) {
            sozdatPokupatelya();
            sleduyuschiyPokupatel = tekuschiyShag + 1 + rnd.nextInt(3);
        }

        // кассы работают
        for (int i = 0; i < otdely.size(); i++) {
            Otdel o = otdely.get(i);
            for (int j = 0; j < o.getKassy().size(); j++) {
                Kassa k = o.getKassy().get(j);
                if (k.dlinaOcheredi() > 0) {
                    // сначала в статистику, потом из очереди
                    Pokupatel pk = k.getOchered().get(0);
                    for (int s = 0; s < pk.getKorzina().getStroki().size(); s++) {
                        StrokaKorziny sk = pk.getKorzina().getStroki().get(s);
                        int idx = indeksTovara(sk.getTovar());
                        prodanoKol[idx] = prodanoKol[idx] + sk.getKolichestvo();
                        prodanoSumma[idx] = prodanoSumma[idx] + sk.itogo();
                    }
                    double summa = k.obsluzhit();
                    if (summa > 0) {
                        o.dobavitVyruchku(summa);
                        vsegoPokupateleyObsluzheno = vsegoPokupateleyObsluzheno + 1;
                        vsegoSummaPokupok = vsegoSummaPokupok + summa;
                    }
                }
            }
        }
    }

    // один покупатель зашел
    private void sozdatPokupatelya() {
        VidTovara v = vseVidy.get(rnd.nextInt(vseVidy.size()));
        int kol = 1 + rnd.nextInt(5);
        double dengi = 100 + rnd.nextInt(900);
        int porog = 2 + rnd.nextInt(4);   // очередь длиннее - сваливает
        Pokupatel p = new Pokupatel(sledNomer, v, kol, dengi, porog);
        sledNomer = sledNomer + 1;

        Otdel otd = null;
        for (int i = 0; i < otdely.size(); i++) {
            if (otdely.get(i).getVid() == v) {
                otd = otdely.get(i);
            }
        }
        if (otd == null) return;

        // что есть на полке этого вида
        ArrayList<Tovar> dostupnye = new ArrayList<Tovar>();
        for (int i = 0; i < vseTovary.size(); i++) {
            Tovar t = vseTovary.get(i);
            if (t.getVid() == v && otd.kolichestvoTovara(t) > 0) {
                dostupnye.add(t);
            }
        }
        if (dostupnye.size() == 0) return;
        Tovar t = dostupnye.get(rnd.nextInt(dostupnye.size()));

        // сколько реально возьмет - деньги склад и сколько хотел
        int maxPoDengam = (int)(dengi / t.getCena());
        int hochet = kol;
        if (hochet > maxPoDengam) hochet = maxPoDengam;
        if (hochet <= 0) return;
        int fakt = otd.vzyat(t, hochet);
        if (fakt <= 0) return;
        p.getKorzina().dobavit(t, fakt, t.getCena());

        Kassa k = otd.naimeneeZagruzhennaya();
        if (k == null) {
            otd.vernut(t, fakt, tekuschiyShag);
            return;
        }
        // очередь длинная - ушел, товар назад
        if (k.dlinaOcheredi() > p.getPorogOcheredi()) {
            otd.vernut(t, fakt, tekuschiyShag);
            ushliBezObsluzhivaniya = ushliBezObsluzhivaniya + 1;
            return;
        }
        k.postavit(p);
    }

    public int getTekuschiyShag() { return tekuschiyShag; }
    public int getUshliBezObsluzhivaniya() { return ushliBezObsluzhivaniya; }

    public String[] getImenaTovarov() {
        String[] a = new String[vseTovary.size()];
        for (int i = 0; i < vseTovary.size(); i++) a[i] = vseTovary.get(i).getImya();
        return a;
    }

    public String[] getImenaVidov() {
        String[] a = new String[vseVidy.size()];
        for (int i = 0; i < vseVidy.size(); i++) a[i] = vseVidy.get(i).getNazvanie();
        return a;
    }

    public String prodazhiTovara(String imya) {
        Tovar t = naytiTovar(imya);
        if (t == null) return "Нет такого товара";
        int idx = indeksTovara(t);
        return "Продано: " + prodanoKol[idx] + " шт на сумму " + prodanoSumma[idx];
    }

    public String ostatkiTovara(String imya) {
        Tovar t = naytiTovar(imya);
        if (t == null) return "Нет такого товара";
        int kol = 0;
        for (int i = 0; i < otdely.size(); i++) kol = kol + otdely.get(i).kolichestvoTovara(t);
        double s = kol * t.getCena();
        return "Остаток: " + kol + " шт на сумму " + s;
    }

    public String prodazhiVida(String imya) {
        VidTovara v = naytiVid(imya);
        if (v == null) return "Нет такого вида";
        int kol = 0;
        double summa = 0;
        for (int i = 0; i < vseTovary.size(); i++) {
            if (vseTovary.get(i).getVid() == v) {
                kol = kol + prodanoKol[i];
                summa = summa + prodanoSumma[i];
            }
        }
        return "Продано вида: " + kol + " шт на сумму " + summa;
    }

    public String ostatkiVida(String imya) {
        VidTovara v = naytiVid(imya);
        if (v == null) return "Нет такого вида";
        int kol = 0;
        double summa = 0;
        for (int i = 0; i < otdely.size(); i++) {
            Otdel o = otdely.get(i);
            if (o.getVid() != v) continue;
            for (int j = 0; j < o.getSklad().size(); j++) {
                Partiya p = o.getSklad().get(j);
                kol = kol + p.getKolichestvo();
                summa = summa + p.getKolichestvo() * p.getTovar().getCena();
            }
        }
        return "Остаток вида: " + kol + " шт на сумму " + summa;
    }

    // скоропорт у которого срок кончается скоро
    public String skoroportyaschiesya(int porog) {
        int kol = 0;
        double summa = 0;
        for (int i = 0; i < otdely.size(); i++) {
            Otdel o = otdely.get(i);
            for (int j = 0; j < o.getSklad().size(); j++) {
                Partiya p = o.getSklad().get(j);
                if (p.getTovar().isSkoroporta() && p.getShagSpisaniya() > 0) {
                    int ostatok = p.getShagSpisaniya() - tekuschiyShag;
                    if (ostatok >= 0 && ostatok <= porog) {
                        kol = kol + p.getKolichestvo();
                        summa = summa + p.getKolichestvo() * p.getTovar().getCena();
                    }
                }
            }
        }
        return "Скоропортящихся: " + kol + " шт на сумму " + summa;
    }

    public String sezonnyeTekst() {
        StringBuilder sb = new StringBuilder();
        boolean naydeno = false;
        for (int i = 0; i < vseTovary.size(); i++) {
            Tovar t = vseTovary.get(i);
            if (t.isSezonniy() && t.cenaNestandartnaya()) {
                sb.append(t.getImya()).append(" : цена ").append(t.getCena())
                  .append(" (базовая ").append(t.getBazovayaCena()).append(")\n");
                naydeno = true;
            }
        }
        if (!naydeno) return "Нет сезонных товаров с нестандартной ценой";
        return sb.toString().trim();
    }

    public String srednieTekst() {
        if (tekuschiyShag == 0) return "Шагов еще не было";
        double srKol = (double) vsegoPokupateleyObsluzheno / tekuschiyShag;
        double srSum = vsegoSummaPokupok / tekuschiyShag;
        return "Среднее количество покупателей за шаг: " + srKol + "\n"
             + "Средняя сумма покупок за шаг: " + srSum;
    }

    public String ushliTekst() {
        return "Ушло без обслуживания: " + ushliBezObsluzhivaniya;
    }

    public String vyruchkaTekst() {
        StringBuilder sb = new StringBuilder();
        double itog = 0;
        for (int i = 0; i < otdely.size(); i++) {
            Otdel o = otdely.get(i);
            sb.append("Отдел ").append(o.getNazvanie()).append(" : выручка ").append(o.getVyruchka()).append("\n");
            for (int j = 0; j < o.getKassy().size(); j++) {
                Kassa k = o.getKassy().get(j);
                sb.append("   ").append(k.getNazvanie()).append(" : ").append(k.getVyruchka()).append("\n");
            }
            itog = itog + o.getVyruchka();
        }
        sb.append("Итого по магазину: ").append(itog);
        return sb.toString();
    }

    // для графиков в javafx - просто массивы

    public String[] getImenaKass() {
        int n = 0;
        int i = 0;
        while (i < otdely.size()) {
            n = n + otdely.get(i).getKassy().size();
            i = i + 1;
        }
        String[] a = new String[n];
        int k = 0;
        i = 0;
        while (i < otdely.size()) {
            Otdel o = otdely.get(i);
            int j = 0;
            while (j < o.getKassy().size()) {
                a[k] = o.getKassy().get(j).getNazvanie();
                k = k + 1;
                j = j + 1;
            }
            i = i + 1;
        }
        return a;
    }

    public int[] getOcherediKass() {
        String[] imena = getImenaKass();
        int[] a = new int[imena.length];
        int k = 0;
        int i = 0;
        while (i < otdely.size()) {
            Otdel o = otdely.get(i);
            int j = 0;
            while (j < o.getKassy().size()) {
                a[k] = o.getKassy().get(j).dlinaOcheredi();
                k = k + 1;
                j = j + 1;
            }
            i = i + 1;
        }
        return a;
    }

    public String[] getImenaOtdelov() {
        String[] a = new String[otdely.size()];
        int i = 0;
        while (i < otdely.size()) {
            a[i] = otdely.get(i).getNazvanie();
            i = i + 1;
        }
        return a;
    }

    public double[] getVyruchkaOtdelov() {
        double[] a = new double[otdely.size()];
        int i = 0;
        while (i < otdely.size()) {
            a[i] = otdely.get(i).getVyruchka();
            i = i + 1;
        }
        return a;
    }

    public double[] getProdanoSummaPoTovaram() {
        double[] a = new double[prodanoSumma.length];
        int i = 0;
        while (i < prodanoSumma.length) {
            a[i] = prodanoSumma[i];
            i = i + 1;
        }
        return a;
    }

    public double[] getProdanoSummaPoVidam() {
        double[] a = new double[vseVidy.size()];
        int i = 0;
        while (i < vseTovary.size()) {
            int idx = indeksVida(vseTovary.get(i).getVid());
            if (idx >= 0) {
                a[idx] = a[idx] + prodanoSumma[i];
            }
            i = i + 1;
        }
        return a;
    }

    private int indeksVida(VidTovara v) {
        int i = 0;
        while (i < vseVidy.size()) {
            if (vseVidy.get(i) == v) return i;
            i = i + 1;
        }
        return -1;
    }

    private Tovar naytiTovar(String imya) {
        for (int i = 0; i < vseTovary.size(); i++) {
            if (vseTovary.get(i).getImya().equalsIgnoreCase(imya)) return vseTovary.get(i);
        }
        return null;
    }

    private VidTovara naytiVid(String imya) {
        for (int i = 0; i < vseVidy.size(); i++) {
            if (vseVidy.get(i).getNazvanie().equalsIgnoreCase(imya)) return vseVidy.get(i);
        }
        return null;
    }

    private int indeksTovara(Tovar t) {
        for (int i = 0; i < vseTovary.size(); i++) if (vseTovary.get(i) == t) return i;
        return -1;
    }
}
