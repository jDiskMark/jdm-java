
package jdiskmark;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.table.DefaultTableModel;

/**
 * @author James
 */
public class BenchmarkPanel extends javax.swing.JPanel {

    /**
     * Creates new form TestPanel
     */
    public BenchmarkPanel() {
        initComponents();
        Gui.runPanel = BenchmarkPanel.this;
        
        // auto scroll to bottom when a new record is added
        runTable.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                runTable.scrollRectToVisible(runTable.getCellRect(runTable.getRowCount()-1, 0, true));
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        runTable = new javax.swing.JTable();

        runTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Drive Information", "IO Mode", "Block Order", "Samples", "Blocks", "B. Size", "Start Time", "Duration", "Access (ms)", "Max (MB/s)", "Min (MB/s)", "Avg (MB/s)"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(runTable);
        if (runTable.getColumnModel().getColumnCount() > 0) {
            runTable.getColumnModel().getColumn(0).setPreferredWidth(170);
            runTable.getColumnModel().getColumn(1).setPreferredWidth(20);
            runTable.getColumnModel().getColumn(2).setPreferredWidth(40);
            runTable.getColumnModel().getColumn(3).setPreferredWidth(6);
            runTable.getColumnModel().getColumn(4).setPreferredWidth(5);
            runTable.getColumnModel().getColumn(5).setPreferredWidth(6);
            runTable.getColumnModel().getColumn(6).setPreferredWidth(90);
            runTable.getColumnModel().getColumn(7).setPreferredWidth(6);
            runTable.getColumnModel().getColumn(8).setPreferredWidth(10);
            runTable.getColumnModel().getColumn(9).setPreferredWidth(32);
            runTable.getColumnModel().getColumn(10).setPreferredWidth(32);
            runTable.getColumnModel().getColumn(11).setPreferredWidth(32);
        }

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 789, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 118, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable runTable;
    // End of variables declaration//GEN-END:variables

    public void addRun(Benchmark run) {
        DefaultTableModel model = (DefaultTableModel) this.runTable.getModel();
        model.addRow(
                new Object[] {
                    run.diskInfo,
                    run.ioMode,
                    run.blockOrder,
                    run.numSamples,
                    run.numBlocks,
                    run.blockSize,
                    run.getStartTimeString(),
                    run.getDuration(),
                    run.getAccTime(),
                    run.getMax(),
                    run.getMin(),
                    run.getAvg(),
                });
    }
    
    public void clearTable() {
        DefaultTableModel model = (DefaultTableModel) this.runTable.getModel();
        while (model.getRowCount() > 0) {
            model.removeRow(0);
        }
    }
}
