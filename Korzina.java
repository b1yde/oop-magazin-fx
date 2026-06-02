import java.util.ArrayList;

// корзина
public class Korzina {
    private ArrayList<StrokaKorziny> stroki = new ArrayList<StrokaKorziny>();

    public void dobavit(Tovar t, int k, double c) {
        if (k > 0) stroki.add(new StrokaKorziny(t, k, c));
    }
    public ArrayList<StrokaKorziny> getStroki() { return stroki; }

    public double summaItogo() {
        double s = 0;
        for (int i = 0; i < stroki.size(); i++) s = s + stroki.get(i).itogo();
        return s;
    }
}
