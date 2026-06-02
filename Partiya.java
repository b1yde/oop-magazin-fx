// партия на складе, у скоропорта есть шаг когда выкинуть
// если не скоропорт то shagSpisaniya = -1
public class Partiya {
    private Tovar tovar;
    private int kolichestvo;
    private int shagSpisaniya;

    Partiya(Tovar tovar, int kolichestvo, int shagSpisaniya) {
        this.tovar = tovar;
        this.kolichestvo = kolichestvo;
        this.shagSpisaniya = shagSpisaniya;
    }

    public Tovar getTovar() { return tovar; }
    public int getKolichestvo() { return kolichestvo; }
    public int getShagSpisaniya() { return shagSpisaniya; }
    public void otnyatKol(int k) { kolichestvo = kolichestvo - k; }
}
