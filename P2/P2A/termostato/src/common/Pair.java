package common;
import api.Control;

//Usarlo en sensores y realizar accion
//Es generico porque en termo nos da numero y en grid nos da string
public class Pair<T> {
    public Control control;
    public T particular;
    
    public Pair(Control control, T particular) {
        this.control = control;
        this.particular = particular;
    }
}
