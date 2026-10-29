package com.prisma.view.component;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

/** TableModel generik: isi tabel selalu berasal dari hasil query database. */
public class EntityTableModel<T> extends AbstractTableModel {
    private static final long serialVersionUID = 1L;

    private final String[] columns;
    private final BiFunction<T, Integer, Object> cellGetter;
    private List<T> rows = new ArrayList<>();

    public EntityTableModel(String[] columns, BiFunction<T, Integer, Object> cellGetter) {
        this.columns = columns;
        this.cellGetter = cellGetter;
    }

    public void setRows(List<T> data) {
        this.rows = new ArrayList<>(data);
        fireTableDataChanged();
    }

    public T getItem(int modelRow) { return rows.get(modelRow); }

    @Override public int getRowCount() { return rows.size(); }
    @Override public int getColumnCount() { return columns.length; }
    @Override public String getColumnName(int c) { return columns[c]; }
    @Override public Object getValueAt(int r, int c) { return cellGetter.apply(rows.get(r), c); }
    @Override public boolean isCellEditable(int r, int c) { return false; }
    @Override public Class<?> getColumnClass(int c) { return c == 0 ? Integer.class : String.class; }
}
