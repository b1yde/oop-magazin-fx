import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

// окно javafx - кнопки журнал графики
public class MagazinFxApp extends Application {

    // номера кнопок
    private static final int KOD_ODIN_SHAG = 1;
    private static final int KOD_N_SHAGOV = 2;
    private static final int KOD_PRODAZHI_TOVAR = 3;
    private static final int KOD_OSTATKI_TOVAR = 4;
    private static final int KOD_PRODAZHI_VID = 5;
    private static final int KOD_OSTATKI_VID = 6;
    private static final int KOD_SKOROPORT = 7;
    private static final int KOD_SEZONNYE = 8;
    private static final int KOD_SREDNIE = 9;
    private static final int KOD_USHLI = 10;
    private static final int KOD_VYRUCHKA = 11;
    private static final int KOD_OCHISTIT = 12;

    private Simulyaciya sim;
    private Label shagLabel;
    private TextArea vyvod;
    private TextField poleShagov;
    private TextField poleSrok;
    private ComboBox<String> tovary;
    private ComboBox<String> vidy;

    private BarChart<String, Number> diagrammaOcheredi;
    private BarChart<String, Number> diagrammaVyruchka;
    private BarChart<String, Number> diagrammaProdazhi;
    private PieChart diagrammaVidy;

    // точки графиков храню чтоб не очищалр каждый раз (мигало и цвета прыгали)
    private XYChart.Series<String, Number> seriyaOcheredi;
    private XYChart.Data<String, Number>[] tochkiOcheredi;
    private XYChart.Series<String, Number> seriyaVyruchka;
    private XYChart.Data<String, Number>[] tochkiVyruchka;
    private XYChart.Series<String, Number> seriyaProdazhi;
    private XYChart.Data<String, Number>[] tochkiProdazhi;
    private PieChart.Data[] tochkiVidy;

    @Override
    public void start(Stage stage) {
        sim = new Simulyaciya();
        sim.zapolnitDemo();

        shagLabel = new Label("Текущий шаг: 0");
        shagLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        shagLabel.setWrapText(true);

        vyvod = new TextArea();
        vyvod.setEditable(false);
        vyvod.setWrapText(true);
        vyvod.setText("Симуляция магазина. Слева — управление, по центру — журнал, справа — диаграммы.");

        poleShagov = new TextField("10");
        poleShagov.setPrefWidth(70);
        poleSrok = new TextField("2");
        poleSrok.setPrefWidth(70);

        tovary = new ComboBox<String>();
        tovary.setMaxWidth(Double.MAX_VALUE);
        String[] imenaT = sim.getImenaTovarov();
        int it = 0;
        while (it < imenaT.length) {
            tovary.getItems().add(imenaT[it]);
            it = it + 1;
        }
        tovary.getSelectionModel().selectFirst();

        vidy = new ComboBox<String>();
        vidy.setMaxWidth(Double.MAX_VALUE);
        String[] imenaV = sim.getImenaVidov();
        int iv = 0;
        while (iv < imenaV.length) {
            vidy.getItems().add(imenaV[iv]);
            iv = iv + 1;
        }
        vidy.getSelectionModel().selectFirst();

        diagrammaOcheredi = sdelatStolbchatuyu("Очереди на кассах", "чел.");
        diagrammaVyruchka = sdelatStolbchatuyu("Выручка по отделам", "руб.");
        diagrammaProdazhi = sdelatStolbchatuyu("Продажи по товарам", "руб.");
        diagrammaVidy = new PieChart();
        diagrammaVidy.setTitle("Доля продаж по видам");
        diagrammaVidy.setLegendVisible(true);
        diagrammaVidy.setPrefHeight(220);
        diagrammaVidy.setAnimated(false);
        // цвета круга зафиксировал
        diagrammaVidy.setStyle(
            "-fx-pie-color0: #4e79a7; -fx-pie-color1: #f28e2b; "
          + "-fx-pie-color2: #59a14f; -fx-pie-color3: #e15759;");

        seriyaOcheredi = zagruzitStolbchatuyu(diagrammaOcheredi, sim.getImenaKass());
        tochkiOcheredi = vzyatTochki(seriyaOcheredi);
        seriyaVyruchka = zagruzitStolbchatuyu(diagrammaVyruchka, sim.getImenaOtdelov());
        tochkiVyruchka = vzyatTochki(seriyaVyruchka);
        seriyaProdazhi = zagruzitStolbchatuyu(diagrammaProdazhi, sim.getImenaTovarov());
        tochkiProdazhi = vzyatTochki(seriyaProdazhi);
        tochkiVidy = zagruzitKrugovuyu(diagrammaVidy, sim.getImenaVidov());

        VBox panelDiagramm = new VBox(10);
        panelDiagramm.setPadding(new Insets(8));
        panelDiagramm.getChildren().add(diagrammaOcheredi);
        panelDiagramm.getChildren().add(diagrammaVyruchka);
        panelDiagramm.getChildren().add(diagrammaProdazhi);
        panelDiagramm.getChildren().add(diagrammaVidy);
        ScrollPane prokrutkaDiag = new ScrollPane(panelDiagramm);
        prokrutkaDiag.setFitToWidth(true);

        SplitPane centr = new SplitPane();
        centr.getItems().add(vyvod);
        centr.getItems().add(prokrutkaDiag);
        centr.setDividerPositions(0.42);

        // слева кнопки в столбик + скролл а то не влезает
        VBox levo = new VBox(8);
        levo.setPadding(new Insets(10));
        levo.setAlignment(Pos.TOP_LEFT);
        levo.setMinWidth(300);
        levo.setPrefWidth(300);

        Button b1 = new Button("1 шаг");
        b1.setMaxWidth(Double.MAX_VALUE);
        b1.setOnAction(new ObrabotchikKnopki(this, KOD_ODIN_SHAG));
        levo.getChildren().add(b1);

        Button bN = new Button("N шагов");
        bN.setMaxWidth(Double.MAX_VALUE);
        bN.setOnAction(new ObrabotchikKnopki(this, KOD_N_SHAGOV));
        levo.getChildren().add(new Label("Число шагов:"));
        levo.getChildren().add(poleShagov);
        levo.getChildren().add(bN);

        levo.getChildren().add(new Label("--- товар ---"));
        levo.getChildren().add(new Label("Выбор:"));
        levo.getChildren().add(tovary);
        Button bPt = new Button("Продажи");
        bPt.setMaxWidth(Double.MAX_VALUE);
        bPt.setOnAction(new ObrabotchikKnopki(this, KOD_PRODAZHI_TOVAR));
        Button bOt = new Button("Остатки");
        bOt.setMaxWidth(Double.MAX_VALUE);
        bOt.setOnAction(new ObrabotchikKnopki(this, KOD_OSTATKI_TOVAR));
        levo.getChildren().add(bPt);
        levo.getChildren().add(bOt);

        levo.getChildren().add(new Label("--- вид ---"));
        levo.getChildren().add(new Label("Выбор:"));
        levo.getChildren().add(vidy);
        Button bPv = new Button("Продажи");
        bPv.setMaxWidth(Double.MAX_VALUE);
        bPv.setOnAction(new ObrabotchikKnopki(this, KOD_PRODAZHI_VID));
        Button bOv = new Button("Остатки");
        bOv.setMaxWidth(Double.MAX_VALUE);
        bOv.setOnAction(new ObrabotchikKnopki(this, KOD_OSTATKI_VID));
        levo.getChildren().add(bPv);
        levo.getChildren().add(bOv);

        levo.getChildren().add(new Label("Срок (шагов):"));
        levo.getChildren().add(poleSrok);
        Button bSk = new Button("Скоропорт.");
        bSk.setMaxWidth(Double.MAX_VALUE);
        bSk.setOnAction(new ObrabotchikKnopki(this, KOD_SKOROPORT));
        levo.getChildren().add(bSk);

        levo.getChildren().add(sdelatKnopku("Сезонные цены", KOD_SEZONNYE));
        levo.getChildren().add(sdelatKnopku("Средние за шаг", KOD_SREDNIE));
        levo.getChildren().add(sdelatKnopku("Ушли без кассы", KOD_USHLI));
        levo.getChildren().add(sdelatKnopku("Выручка", KOD_VYRUCHKA));
        levo.getChildren().add(sdelatKnopku("Очистить журнал", KOD_OCHISTIT));

        ScrollPane prokrutkaLevo = new ScrollPane(levo);
        prokrutkaLevo.setFitToWidth(true);
        prokrutkaLevo.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        BorderPane koren = new BorderPane();
        HBox verh = new HBox(shagLabel);
        verh.setPadding(new Insets(10, 10, 0, 10));
        koren.setTop(verh);
        koren.setLeft(prokrutkaLevo);
        BorderPane.setAlignment(prokrutkaLevo, Pos.TOP_LEFT);
        koren.setCenter(centr);
        BorderPane.setMargin(centr, new Insets(10));
        BorderPane.setMargin(prokrutkaLevo, new Insets(0, 0, 10, 0));

        obnovitShag();

        Scene scena = new Scene(koren, 1200, 640);
        stage.setTitle("Магазин — симуляция (JavaFX)");
        stage.setScene(scena);
        stage.setMinWidth(900);
        stage.show();
    }

    // столбчатый график, без анимации
    private BarChart<String, Number> sdelatStolbchatuyu(String zagolovok, String edinica) {
        CategoryAxis osX = new CategoryAxis();
        NumberAxis osY = new NumberAxis();
        osY.setLabel(edinica);
        osY.setLowerBound(0);
        osY.setForceZeroInRange(true);
        BarChart<String, Number> d = new BarChart<String, Number>(osX, osY);
        d.setTitle(zagolovok);
        d.setLegendVisible(false);
        d.setAnimated(false);
        d.setPrefHeight(180);
        d.setCategoryGap(20);
        d.setBarGap(4);
        return d;
    }

    // столбики один раз создал, потом только цифры меняю
    private XYChart.Series<String, Number> zagruzitStolbchatuyu(BarChart<String, Number> diagramma, String[] podpisi) {
        XYChart.Series<String, Number> seriya = new XYChart.Series<String, Number>();
        int i = 0;
        while (i < podpisi.length) {
            XYChart.Data<String, Number> tochka = new XYChart.Data<String, Number>(podpisi[i], 0);
            seriya.getData().add(tochka);
            i = i + 1;
        }
        diagramma.getData().add(seriya);
        return seriya;
    }

    // вытащить точки из серии в массив
    private XYChart.Data<String, Number>[] vzyatTochki(XYChart.Series<String, Number> seriya) {
        int n = seriya.getData().size();
        XYChart.Data<String, Number>[] mass = new XYChart.Data[n];
        int i = 0;
        while (i < n) {
            mass[i] = seriya.getData().get(i);
            i = i + 1;
        }
        return mass;
    }

    // сектора круга сразу все, даже нули
    private PieChart.Data[] zagruzitKrugovuyu(PieChart diagramma, String[] podpisi) {
        PieChart.Data[] mass = new PieChart.Data[podpisi.length];
        int i = 0;
        while (i < podpisi.length) {
            mass[i] = new PieChart.Data(podpisi[i], 0);
            diagramma.getData().add(mass[i]);
            i = i + 1;
        }
        return mass;
    }

    private Button sdelatKnopku(String tekst, int kod) {
        Button b = new Button(tekst);
        b.setMaxWidth(Double.MAX_VALUE);
        b.setWrapText(true);
        b.setOnAction(new ObrabotchikKnopki(this, kod));
        return b;
    }

    // что нажали на кнопку
    void vypolnitKod(int kod) {
        if (kod == KOD_ODIN_SHAG) {
            sim.shag();
            obnovitShag();
            pokazat("Выполнен 1 шаг моделирования.");
        } else if (kod == KOD_N_SHAGOV) {
            int n = razobrat(poleShagov.getText(), 1);
            int i = 0;
            while (i < n) {
                sim.shag();
                i = i + 1;
            }
            obnovitShag();
            pokazat("Выполнено шагов: " + n);
        } else if (kod == KOD_PRODAZHI_TOVAR) {
            pokazat(sim.prodazhiTovara(tovary.getValue()));
        } else if (kod == KOD_OSTATKI_TOVAR) {
            pokazat(sim.ostatkiTovara(tovary.getValue()));
        } else if (kod == KOD_PRODAZHI_VID) {
            pokazat(sim.prodazhiVida(vidy.getValue()));
        } else if (kod == KOD_OSTATKI_VID) {
            pokazat(sim.ostatkiVida(vidy.getValue()));
        } else if (kod == KOD_SKOROPORT) {
            pokazat(sim.skoroportyaschiesya(razobrat(poleSrok.getText(), 0)));
        } else if (kod == KOD_SEZONNYE) {
            pokazat(sim.sezonnyeTekst());
        } else if (kod == KOD_SREDNIE) {
            pokazat(sim.srednieTekst());
        } else if (kod == KOD_USHLI) {
            pokazat(sim.ushliTekst());
        } else if (kod == KOD_VYRUCHKA) {
            pokazat(sim.vyruchkaTekst());
        } else if (kod == KOD_OCHISTIT) {
            vyvod.clear();
            obnovitShag();
        }
    }

    private void obnovitShag() {
        shagLabel.setText("Шаг: " + sim.getTekuschiyShag()
            + "  |  ушли без кассы: " + sim.getUshliBezObsluzhivaniya());
        obnovitDiagrammy();
    }

    private void obnovitDiagrammy() {
        obnovitTochkiInt(tochkiOcheredi, sim.getOcherediKass());
        obnovitTochkiDouble(tochkiVyruchka, sim.getVyruchkaOtdelov());
        obnovitTochkiDouble(tochkiProdazhi, sim.getProdanoSummaPoTovaram());
        obnovitKrugovuyu(tochkiVidy, sim.getProdanoSummaPoVidam());
    }

    private void obnovitTochkiInt(XYChart.Data<String, Number>[] tochki, int[] znacheniya) {
        int i = 0;
        while (i < tochki.length) {
            tochki[i].setYValue(znacheniya[i]);
            i = i + 1;
        }
    }

    private void obnovitTochkiDouble(XYChart.Data<String, Number>[] tochki, double[] znacheniya) {
        int i = 0;
        while (i < tochki.length) {
            tochki[i].setYValue(znacheniya[i]);
            i = i + 1;
        }
    }

    private void obnovitKrugovuyu(PieChart.Data[] tochki, double[] znacheniya) {
        int i = 0;
        while (i < tochki.length) {
            tochki[i].setPieValue(znacheniya[i]);
            i = i + 1;
        }
    }

    private void pokazat(String tekst) {
        obnovitShag();
        vyvod.appendText("\n---\n" + tekst + "\n");
    }

    private int razobrat(String s, int defolt) {
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return defolt;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
