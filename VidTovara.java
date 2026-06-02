// вид товара - фрукты хлеб и тд, в одном отделе один вид
public class VidTovara {
    private String nazvanie;

    VidTovara(String nazvanie) {
        this.nazvanie = nazvanie;
    }

    public String getNazvanie() {
        return nazvanie;
    }
}
