// строка в корзине, цену запомнили когда взял
public class StrokaKorziny {
    private Tovar tovar;
    private int kolichestvo;
    private double cenaZaShtuku;

    StrokaKorziny(Tovar t, int k, double c) {
        this.tovar = t;
        this.kolichestvo = k;
        this.cenaZaShtuku = c;
    }

    public Tovar getTovar() { return tovar; }
    public int getKolichestvo() { return kolichestvo; }
    public double itogo() { return kolichestvo * cenaZaShtuku; }
}
