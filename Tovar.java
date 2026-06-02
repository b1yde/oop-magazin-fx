import java.util.Random;

// сам товар
public class Tovar {
    private String imya;
    private VidTovara vid;
    private double bazovayaCena;   // с чего начинали, для отчета про сезон
    private double cena;           // текущая, у сезонных прыгает
    private boolean skoroporta;
    private int srokHraneniya;     // сколько шагов живет, 0 если норм товар
    private boolean sezonniy;
    private double sezonDelta;     // на сколько цену двигаем

    Tovar(String imya, VidTovara vid, double cena, boolean skoroporta, int srok, boolean sezonniy, double sezonDelta) {
        this.imya = imya;
        this.vid = vid;
        this.bazovayaCena = cena;
        this.cena = cena;
        this.skoroporta = skoroporta;
        this.srokHraneniya = srok;
        this.sezonniy = sezonniy;
        this.sezonDelta = sezonDelta;
    }

    public String getImya() { return imya; }
    public VidTovara getVid() { return vid; }
    public double getCena() { return cena; }
    public double getBazovayaCena() { return bazovayaCena; }
    public boolean isSkoroporta() { return skoroporta; }
    public int getSrokHraneniya() { return srokHraneniya; }
    public boolean isSezonniy() { return sezonniy; }

    // цена уехала от базовой - для пункта про сезонные
    public boolean cenaNestandartnaya() {
        double r = cena - bazovayaCena;
        if (r < 0) r = -r;
        return r > 0.001;
    }

    // сезонный - рандом + или - delta
    public void smenitCenu(Random rnd) {
        if (!sezonniy) return;
        if (rnd.nextBoolean()) {
            cena = cena + sezonDelta;
        } else {
            cena = cena - sezonDelta;
        }
        if (cena < 1) cena = 1;
    }
}
