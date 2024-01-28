
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
                "Drive Model", "Usage (GB)", "Mode", "Order", "Samples", "Blks", "B.Size", "Start Time", "Duration (ms)", "Access (ms)", "Max (MB/s)", "Min (MB/s)", "Avg (MB/s)"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, true, false, false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        runTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        runTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                runTableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(runTable);
        if (runTable.getColumnModel().getColumnCount() > 0) {
            runTable.getColumnModel().getColumn(0).setPreferredWidth(170);
            runTable.getColumnModel().getColumn(1).setPreferredWidth(30);
            runTable.getColumnModel().getColumn(2).setPreferredWidth(10);
            runTable.getColumnModel().getColumn(3).setPreferredWidth(40);
            runTable.getColumnModel().getColumn(4).setPreferredWidth(6);
            runTable.getColumnModel().getColumn(5).setPreferredWidth(5);
            runTable.getColumnModel().getColumn(6).setPreferredWidth(6);
            runTable.getColumnModel().getColumn(7).setPreferredWidth(90);
            runTable.getColumnModel().getColumn(8).setPreferredWidth(6);
            runTable.getColumnModel().getColumn(9).setPreferredWidth(10);
            runTable.getColumnModel().getColumn(10).setPreferredWidth(32);
            runTable.getColumnModel().getColumn(11).setPreferredWidth(32);
            runTable.getColumnModel().getColumn(12).setPreferredWidth(32);
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

    static final int START_TIME_COLUMN = 7;
    
    private void runTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_runTableMouseClicked
        int sRow = runTable.getSelectedRow();
        String timeString = (String) runTable.getValueAt(sRow, START_TIME_COLUMN);
        System.out.println("timeString=" + timeString);

        Benchmark benchmark = App.benchmarks.get(timeString);
        if (benchmark != null) {
            Gui.loadBenchmark(benchmark);
        }
    }//GEN-LAST:event_runTableMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable runTable;
    // End of variables declaration//GEN-END:variables

    public void addRun(Benchmark run) {
        DefaultTableModel model = (DefaultTableModel) this.runTable.getModel();
        model.addRow(
                new Object[] {
                    run.driveModel,
                    run.getUsageColumnDisplay(),
                    run.ioMode,
                    run.blockOrder,
                    run.numSamples,
                    run.numBlocks,
                    run.blockSize,
                    run.getStartTimeString(),
                    run.getDuration(),
                    run.getAccTimeDisplay(),
                    run.getBwMaxDisplay(),
                    run.getBwMinDisplay(),
                    run.getBwAvgDisplay(),
                });
    }
    
    public void clearTable() {
        DefaultTableModel model = (DefaultTableModel) this.runTable.getModel();
        while (model.getRowCount() > 0) {
            model.removeRow(0);
        }
    }
}
