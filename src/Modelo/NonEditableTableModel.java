
package Modelo;

import java.util.Vector;
import javax.swing.table.DefaultTableModel;

/**
 * Las tablas construídas usando este tipo de modelo tiene deshabilitada la edición de sus celdas.
 * @author Mauri
 */
public class NonEditableTableModel extends DefaultTableModel
{
    NonEditableTableModel(Vector datos, Vector columnas) {
        super(datos, columnas);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
}
