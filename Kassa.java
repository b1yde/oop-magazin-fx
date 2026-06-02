import java.util.ArrayList;

// касса, за шаг один человек
public class Kassa {
    private String nazvanie;
    private ArrayList<Pokupatel> ochered = new ArrayList<Pokupatel>();
    private double vyruchkaKassy;

    Kassa(String nazvanie) { this.nazvanie = nazvanie; }
    public String getNazvanie() { return nazvanie; }
    public int dlinaOcheredi() { return ochered.size(); }
    public double getVyruchka() { return vyruchkaKassy; }
    public ArrayList<Pokupatel> getOchered() { return ochered; }

    public void postavit(Pokupatel p) { ochered.add(p); }

    // обслужили первого в очереди
    public double obsluzhit() {
        if (ochered.size() == 0) return 0;
        Pokupatel p = ochered.remove(0);
        double summa = p.getKorzina().summaItogo();
        vyruchkaKassy = vyruchkaKassy + summa;
        return summa;
    }
}
