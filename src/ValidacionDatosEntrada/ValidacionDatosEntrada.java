
package ValidacionDatosEntrada;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class ValidacionDatosEntrada implements KeyListener
{
    @Override
    /**
     * Validación para datos de entrada númericos.
     */
    public void keyTyped(KeyEvent ke)
    {
        char c = ke.getKeyChar();                   // Obtiene el caracter tipeado
        if (!Character.isDigit(c))  ke.consume();   // Si el caracter no es un dígito, el evento tipeo se consume y no se procesa el caracter
    }

    @Override
    public void keyPressed(KeyEvent ke) {}
    
    @Override
    public void keyReleased(KeyEvent ke) {}
    
}
