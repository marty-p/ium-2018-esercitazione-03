package com.example.davide.piechart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.List;

/**
 * Classe che rappresenta un grafico a torta
 */
public class PieChartView extends View {


    /**
     * Centro del grafico, ricalcolato a ogni disegno
     */
    private PointF center = new PointF();
    /**
     * Rettangolo che contiene il grafico a torta
     */
    private RectF enclosing = new RectF();
    /**
     * Variabile di appoggio per il disegno dell'elemento selezionato
     */
    private float selectedStartAngle = 0.0f;

    /**
     * Colore di sfondo del controllo
     */
    private int backgroundColor = Color.WHITE;
    /**
     * Lista delle percentuali (float) da disegnare come fette della torta
     */
    private List<Float> percent;
    /**
     * Lista dei colori per ogni fetta della torta
     */
    private List<Integer> segmentColor;
    /**
     * Colore del bordo delle fette non selezionate
     */
    private int strokeColor;
    /**
     * Spessore del bordo delle fette non selezionate
     */
    private int strokeWidth;
    /**
     * Colore del bordo della fetta selezionata
     */
    private int selectedColor;
    /**
     * Spessore del bordo della fetta selezionata
     */
    private int selectedWidth = 8;
    /**
     * Indice della fetta selezionata nella lista delle percentuali
     */
    private int selectedIndex = 2;
    /**
     * Raggio della torta
     */
    private int radius = 100;

    /**
     * Il fattore di scala
     */
    private float zoom = 1.0f;

    /**
     * il punto in alto a sinistra del viewport rispetto al sistema di riferimento del controllo
     */
    private PointF translate = new PointF(-200,-300);

    /**
     * Posizione precedente del tocco per implementare il pan della vista
     */
    private PointF previousTouch = new PointF(0,0);

    /**
     * vero se sto eseguendo un'interazione multitouch, falso altrimenti
     */
    private boolean multitouch = false;

    /**
     * distanza fra due tocchi durante l'interazione multitouch
     */
    private double oldDistance = 0.0;


    /**
     * Costruttore del controllo
     * @param context Il contesto grafico su cui disegnare
     */
    public PieChartView(Context context) {
        super(context);
    }

    /**
     * Costrutture del controllo
     * @param context Il contesto grafico su cui disegnare
     * @param attrs Gli attributi ricevuti dall'activity
     */
    public PieChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Costrutture del controllo
     * @param context Il contesto grafico su cui disegnare
     * @param attrs Gli attributi ricevuti dall'activity
     * @param defStyle Stili di disegno di default
     */
    public PieChartView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Restituisce il colore di sfondo
     * @return Il colore di sfondo
     */
    public int getBackgroundColor() {
        return backgroundColor;
    }

    /**
     * Imposta il colore di sfondo
     * @param backgroundColor Il colore di sfondo
     */
    @Override
    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    /**
     * Restituisce la lista delle percentuali da disegnare nel grafico,
     * @return La lista delle percentuali
     */
    public List<Float> getPercent() {
        return percent;
    }

    /**
     * Imposta la lista delle percentuali da disegnare nel grafico
     * @param percent La lista delle percentuali
     */
    public void setPercent(List<Float> percent) {
        this.percent = percent;
    }

    /**
     * Restituisce la lista dei colori con cui si disegnano le fette. L'indice dell'i-esimo colore
     * corrisponde alla i-esima percentuale nella lista delle percentuali.
     * @return
     */
    public List<Integer> getSegmentColor() {
        return segmentColor;
    }

    /**
     * Imposta la lista dei colori con cui si disegnano le fette. La lista deve avere
     * la stessa dimensione di quella delle percentuali
     * @param segmentColor la lista dei colori
     * @throws IllegalArgumentException se la lista dei colori e delle percentuali hanno dimensione diversa
     */
    public void setSegmentColor(List<Integer> segmentColor) {
        if(segmentColor.size() != percent.size()){
            throw  new IllegalArgumentException(
                    "La lista dei colori e delle percentuali devono avere la stessa dimensione");
        }
        this.segmentColor = segmentColor;
    }

    /**
     * Restituisce il raggio della torta
     * @return Il raggio della torta
     */
    public int getRadius() {
        return radius;
    }

    /**
     * Imposta il raggio della torta
     * @param radius Il raggio della torta
     */
    public void setRadius(int radius) {
        this.radius = radius;
    }

    /**
     * Restituisce il colore del bordo delle fette
     * @return il colore del bordo delle fette
     */
    public int getStrokeColor() {
        return strokeColor;
    }

    /**
     * Imposta il colore del bordo delle fette
     * @param strokeColor il colore del bordo delle fette
     */
    public void setStrokeColor(int strokeColor) {
        this.strokeColor = strokeColor;
    }

    /**
     * Restituisce la dimensione del bordo delle fette
     * @return La dimensione del bordo delle fette
     */
    public int getStrokeWidth() {
        return strokeWidth;
    }

    /**
     * Imposta la dimensione del bordo delle fette
     * @param strokeWidth la dimensione del bordo delle fette
     */
    public void setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    /**
     * Restituisce il colore del bordo selezionato
     * @return Il colore del bordo selezionato
     */
    public int getSelectedColor() {
        return selectedColor;
    }

    /**
     * Imposta il colore del bordo selezionato
     * @param selectedColor Il colore del bordo selezionato
     */
    public void setSelectedColor(int selectedColor) {
        this.selectedColor = selectedColor;
    }

    /**
     * Restituisce la dimensione del bordo dell'elemento selezionato
     * @return la dimensione del bordo dell'elemento selezionato
     */
    public int getSelectedWidth() {
        return selectedWidth;
    }

    /**
     * Imposta la dimensione del bordo dell'elemento selezionato
     * @param selectedWidth la dimensione del bordo dell'elemento selezionato
     */
    public void setSelectedWidth(int selectedWidth) {
        this.selectedWidth = selectedWidth;
    }

    /**
     * Procedura di disegno del controllo
     * @param canvas Il contesto grafico su cui disegnare
     */
    @Override
    protected void onDraw(Canvas canvas) {

        // fase 1- disegno lo sfondo

        // utilizziamo questo oggetto per definire colori, font, tipi di linee ecc
        Paint paint = new Paint();

        // impostiamo l'antialiasing
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);

        // cancelliamo lo sfondo
        paint.setColor(this.getBackgroundColor());
        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), paint);

        // salvo le trasformazioni correnti
        canvas.save();

        // fase 2 - disegno la torta
        // applico la scalatura, usiamo un fattore omogeneo (uguale su x e y)
        canvas.scale(this.getZoom(), this.getZoom());

        // applico la traslazione del punto di vista
        canvas.translate(getTranslate().x, getTranslate().y);

        // angolo dal quale si inizia a disegnare
        float alpha = -90.0f;

        // 1% in radianti
        float p2a = 360.0f / 100.0f;

        // calcolo il centro del cerchio e le dimensioni del quadrato in cui inscriverlo
        center.x = canvas.getWidth() / 2;
        center.y = canvas.getHeight() / 2;
        enclosing.top = center.y - radius;
        enclosing.bottom = center.y + radius;
        enclosing.left = center.x - radius;
        enclosing.right = center.x + radius;

        float p;
        int c;

        // disegno la parte colorata (il fill)
        for (int i = 0; i < percent.size(); i++) {
            // la percentuale da rappresentare
            p = percent.get(i);
            // il colore da usare
            c = segmentColor.get(i);
            paint.setColor(c);
            paint.setStyle(Paint.Style.FILL);

            // il disegno parte dall'angolo alpha e disegna un segmento circolare di ampiezza
            // p * p2a. Disegna in senso orario (al contrario dell'andamento degli angoli
            // nella circonferenza unitaria usuale).
            canvas.drawArc(
                    enclosing,
                    alpha,
                    p * p2a,
                    true,
                    paint);

            alpha += p * p2a;
        }

        // disegno il contorno (lo stroke)
        alpha = -90.0f;
        for (int i = 0; i < percent.size(); i++) {
            // la percentuale da rappresentare
            p = percent.get(i);
            // il colore da usare
            c = segmentColor.get(i);
            paint.setColor(strokeColor);
            paint.setStrokeWidth(strokeWidth);
            paint.setStyle(Paint.Style.STROKE);

            // salvo l'angolo dell'elemento selezionato per il passo successivo
            if(i == selectedIndex){
                selectedStartAngle = alpha;
            }

            canvas.drawArc(
                    enclosing,
                    alpha,
                    p * p2a,
                    true,
                    paint);

            alpha += p * p2a;
        }

        // disegno il contorno dell'item selezionato
        if(selectedIndex >= 0 && selectedIndex < percent.size()) {
            // il valore selezionato e' valido
            paint.setColor(selectedColor);
            paint.setStrokeWidth(selectedWidth);
            paint.setStyle(Paint.Style.STROKE);

            canvas.drawArc(
                    enclosing,
                    selectedStartAngle,
                    percent.get(selectedIndex) * p2a,
                    true,
                    paint);
        }

        // ripristino la situazione iniziale del cavas (non è strettamente necessario in
        // questo caso, ma ogni volta che si fa la save, si fa la restore
        canvas.restore();


    }

    @Override
    public boolean onTouchEvent(MotionEvent event){

        // ottento le coordinate del tocco dal descrittore dell'evento
        float tx = event.getX();
        float ty = event.getY();

        // riporto le coordinate del tocco dal sistema di riferimento dello schermo
        // a quello del controllo. In pratica, applico le trasformazioni
        // (scalatura e traslazione) in ordine inverso
        float x = (tx / getZoom()) - getTranslate().x;
        float y = (ty / getZoom()) - getTranslate().y;


        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                // prima posizione del primo tocco
                if (event.getPointerCount() == 1) {
                    // l'indice selezionato è quello relativo alla fetta che contiene il
                    // il punto del tocco.
                    selectedIndex = this.pickCorrelation(x, y);
                    // richiedo di aggiornare il disegno
                    this.invalidate();

                    // salvo la posizione corrente del tocco in caso di pan
                    this.previousTouch.x = tx;
                    this.previousTouch.y = ty;
                    return true;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                switch(event.getPointerCount()){
                    case 1:
                        if(multitouch){
                            // usciamo immediatamente se l'utente ha sollevato un dito dopo
                            // l'interazione multitouch
                            return true;
                        }
                        // recuperiamo il delta fra la posizione corrente e quella
                        // precedente. Dobbiamo dividere per il fattore di scala
                        // per avere la distanza nel sistema di riferimento
                        // originario
                        float dx = (tx - this.previousTouch.x) / this.zoom;
                        float dy = (ty - this.previousTouch.y) / this.zoom;
                        this.previousTouch.x = tx;
                        this.previousTouch.y = ty;

                        // aggiorniamo la traslazione spostandola di dx sulle x
                        // e di dy sulle y
                        this.translate.set(
                                this.translate.x + dx,
                                this.translate.y + dy
                        );
                        this.invalidate();
                        return true;

                    case 2:
                        // qui gestiamo il pinch

                        // teniamo traccia del fatto che l'utente abbia iniziato un pinch
                        // (vedi sopra)
                        multitouch = true;

                        // recuperiamo la posizione corrente del tocco 1 e del tocco 2
                        MotionEvent.PointerCoords touch1 = new MotionEvent.PointerCoords();
                        MotionEvent.PointerCoords touch2 = new MotionEvent.PointerCoords();

                        event.getPointerCoords(0, touch1);
                        event.getPointerCoords(1, touch2);

                        // calcoliamo la distanza corrente
                        double distance = Math.sqrt(
                                Math.pow(touch2.x - touch1.x, 2) +
                                        Math.pow(touch2.y - touch1.y, 2));

                        // confrontiamo con la precedente
                        if (distance - oldDistance > 0) {
                            // ingrandisco la vista
                            zoom += 0.03;
                            this.invalidate();
                        }

                        if (distance - oldDistance < 0) {
                            // rimpicciolisco la vista
                            zoom -= 0.03;
                            this.invalidate();
                        }

                        oldDistance = distance;

                        return true;

                }

            case MotionEvent.ACTION_UP:
                // reset delle variabili di stato
                this.previousTouch.x = 0.0f;
                this.previousTouch.y = 0.0f;
                multitouch = false;
                oldDistance = 0.0f;
                return true;
        }

        return false;
    }

    /**
     * Restituisce l'indice della fetta di torta che contiene il punto di coordinate (x,y)
     * @param x L'ascissa del punto
     * @param y L'ordinata del punto
     * @return l'indice della fetta di torta
     */
    private int pickCorrelation(float x, float y) {

        if (enclosing.contains(x, y)) {
            // sottraggo alla x e alla y le coordinate del centro
            float dx = x - center.x;
            float dy = y - center.y;

            // ottengo la distanza dal centro
            float r = (float) Math.sqrt(dx * dx + dy * dy);

            float cos = dx / r;
            float sin = -dy / r;

            // l'angolo varia tra -180 e 180
            double angle = Math.toDegrees(Math.atan2(sin, cos));

            Log.d("ANGLE", "angle: " + angle + " cos " + cos + " sin " + sin);


            // faccio in modo che l'angolo vari fra 90 e -270. Spazzando gli angoli
            // in senso orario, i valori degli angoli sono decrescenti, cosa che ci torna
            // utile per il for. Considerando per esempio i punti in cui la circonferenza
            // goniometrica incontra gli assi a partire da 90 gradi, la successione di
            // angoli è la seguente:
            // 90, 0, -90, -180, -270
            if (angle > 90 && angle < 180) {
                angle = angle - 360;
            }

            float alpha = 90.0f;
            float alpha1;

            // 1% in gradi
            float p2a = 360.0f / 100.0f;
            float p;
            for (int i = 0; i < percent.size(); i++) {
                p = percent.get(i);
                alpha1 = alpha - p * p2a;
                if (angle > alpha1 && angle < alpha) {
                    return i;
                }
                alpha = alpha1;

            }

        } else {
            return -1;
        }
        return -1;
    }


    public float getZoom() {
        return zoom;
    }

    public void setZoom(float zoom) {
        this.zoom = zoom;
    }

    public PointF getTranslate() {
        return translate;
    }

    public void setTranslate(PointF translate) {
        this.translate = translate;
    }
}
