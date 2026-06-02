import javafx.event.ActionEvent;
import javafx.event.EventHandler;

// обработчик кнопки отдельным классом
public class ObrabotchikKnopki implements EventHandler<ActionEvent> {
    private MagazinFxApp prilozhenie;
    private int kod;

    ObrabotchikKnopki(MagazinFxApp prilozhenie, int kod) {
        this.prilozhenie = prilozhenie;
        this.kod = kod;
    }

    public void handle(ActionEvent sobytie) {
        prilozhenie.vypolnitKod(kod);
    }
}
