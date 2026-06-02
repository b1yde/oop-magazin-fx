// покупатель
public class Pokupatel {
    private int nomer;
    private VidTovara celevoyVid;
    private int kolNuzhno;
    private double summaDeneg;
    private int porogOcheredi;   // если в очереди больше - уходит
    private Korzina korzina = new Korzina();

    Pokupatel(int nomer, VidTovara vid, int kol, double summa, int porog) {
        this.nomer = nomer;
        this.celevoyVid = vid;
        this.kolNuzhno = kol;
        this.summaDeneg = summa;
        this.porogOcheredi = porog;
    }

    public int getNomer() { return nomer; }
    public VidTovara getVid() { return celevoyVid; }
    public int getKolNuzhno() { return kolNuzhno; }
    public double getSummaDeneg() { return summaDeneg; }
    public int getPorogOcheredi() { return porogOcheredi; }
    public Korzina getKorzina() { return korzina; }
}
