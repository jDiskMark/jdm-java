   
package jdiskmark;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleInsets;

/**
 * Store GUI references for easy access
 */
public final class Gui {
    
    public static ChartPanel chartPanel = null;
    public static MainFrame mainFrame = null;
    public static SelectDriveFrame selFrame = null;
    public static XYSeries wSeries, wAvgSeries, wMaxSeries, wMinSeries, wDrvAccess;
    public static XYSeries rSeries, rAvgSeries, rMaxSeries, rMinSeries, rDrvAccess;
    public static NumberAxis msAxis;
    public static JFreeChart chart;
    public static JProgressBar progressBar = null;
    public static BenchmarkPanel runPanel = null;
    
    public static XYLineAndShapeRenderer bwRenderer;
    public static XYLineAndShapeRenderer msRenderer;
    
    public static ChartPanel createChartPanel() {
        
        wSeries = new XYSeries("Write");
        wAvgSeries = new XYSeries("Write Avg");
        wMaxSeries = new XYSeries("Write Max");
        wMinSeries = new XYSeries("Write Min");
        wDrvAccess = new XYSeries("Write Access");
        
        rSeries = new XYSeries("Read");
        rAvgSeries = new XYSeries("Read Avg");
        rMaxSeries = new XYSeries("Read Max");
        rMinSeries = new XYSeries("Read Min");
        rDrvAccess = new XYSeries("Read Access");
        
        // primary dataset mapped against the bw axis
        XYSeriesCollection bwDataset = new XYSeriesCollection();
        bwDataset.addSeries(wSeries);
        bwDataset.addSeries(wAvgSeries);
        bwDataset.addSeries(wMaxSeries);
        bwDataset.addSeries(wMinSeries);
        bwDataset.addSeries(rSeries);
        bwDataset.addSeries(rAvgSeries);
        bwDataset.addSeries(rMaxSeries);
        bwDataset.addSeries(rMinSeries);
        
        // secondary dataset mapped against ns to show disk access time
        XYSeriesCollection msDataset = new XYSeriesCollection();
        msDataset.addSeries(wDrvAccess);
        msDataset.addSeries(rDrvAccess);
        
        // setup plot
        XYPlot plot = new XYPlot();
        plot.setBackgroundPaint(Color.DARK_GRAY.darker());
        plot.setOutlinePaint(Color.WHITE);
        plot.setDataset(0, bwDataset);
        plot.setDataset(1, msDataset);
        
        //customize the plot with renderers and axis
        bwRenderer = new XYLineAndShapeRenderer(true, false);
        msRenderer = new XYLineAndShapeRenderer(true, false);
        
        // configure the bw series colors
        bwRenderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
        bwRenderer.setSeriesPaint(0, Color.YELLOW);     // write
        bwRenderer.setSeriesPaint(1, Color.WHITE);      // w avg
        bwRenderer.setSeriesPaint(2, Color.GREEN);      // w max
        bwRenderer.setSeriesPaint(3, Color.RED);        // w min
        bwRenderer.setSeriesPaint(4, Color.LIGHT_GRAY); // read
        bwRenderer.setSeriesPaint(5, Color.ORANGE);     // r avg
        bwRenderer.setSeriesPaint(6, Color.GREEN.darker()); // r max
        bwRenderer.setSeriesPaint(7, Color.RED.darker());   // r min
        
        // configure the access time ms colors
        msRenderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
        msRenderer.setSeriesPaint(0, Color.CYAN);       // w acc
        msRenderer.setSeriesPaint(1, Color.MAGENTA);    // r acc
        
        // disable lines and enable shapes
        msRenderer.setSeriesLinesVisible(0, false);
        msRenderer.setSeriesLinesVisible(1, false);
        Shape s0 = new Rectangle2D.Double(-2.0, -2.0, 4.0, 4.0);
        Shape s1 = new Rectangle2D.Double(-2.0, -2.0, 4.0, 4.0);
        msRenderer.setSeriesShape(0, s0);
        msRenderer.setSeriesShape(1, s1);
        msRenderer.setSeriesShapesVisible(0, true);
        msRenderer.setSeriesShapesVisible(1, true);
        
        // link renderers to the plot
        plot.setRenderer(0, bwRenderer);
        plot.setRenderer(1, msRenderer);
        
        // y axis on the left
        NumberAxis bwAxis = new NumberAxis("Bandwidth MB/s");
        bwAxis.setAutoRangeIncludesZero(false);
        
        // y axis on the right
        msAxis = new NumberAxis("Average Access Time (ms)");
        msAxis.setAutoRange(true);
        msAxis.setAutoRangeIncludesZero(false);
        
        // x axis on the bottom
        NumberAxis sampleAxis = new NumberAxis();
        sampleAxis.setNumberFormatOverride(NumberFormat.getNumberInstance());
        sampleAxis.setAutoRangeIncludesZero(false);
        
        // link the axis to the plot
        plot.setRangeAxis(0, bwAxis);
        plot.setRangeAxis(1, msAxis);
        plot.setDomainAxis(sampleAxis);
        
        // configure the locations
        plot.setDomainAxisLocation(AxisLocation.BOTTOM_OR_RIGHT);
        plot.setRangeAxisLocation(0, AxisLocation.TOP_OR_LEFT);
        plot.setRangeAxisLocation(1, AxisLocation.BOTTOM_OR_RIGHT);
        
        // add gap between the plot area and axis so they are detached
        plot.setAxisOffset(new RectangleInsets(3, 3, 3, 3));
        
        // Map the data to the appropriate axis
        plot.mapDatasetToRangeAxis(0, 0);
        plot.mapDatasetToRangeAxis(1, 1);
        
        chart = new JFreeChart("", null , plot, true);
        
        chartPanel = new ChartPanel(chart) {
            // Only way to set the size of chart panel
            // ref: http://www.jfree.org/phpBB2/viewtopic.php?p=75516
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(500, 325);
            }
        };
        
        updateLegendAndAxis();
        return chartPanel;
    }
    
    public static void addWriteSample(Sample s) {
        wSeries.add(s.sampleNum, s.bwMbSec);
        wAvgSeries.add(s.sampleNum, s.cumAvg);
        if (App.showMaxMin) {
            wMaxSeries.add(s.sampleNum, s.cumMax);
            wMinSeries.add(s.sampleNum, s.cumMin);
        }
        if (App.showDriveAccess) {
            wDrvAccess.add(s.sampleNum, s.accessTimeMs);
        }
        Gui.mainFrame.refreshWriteMetrics();
        System.out.println(s.toString());
    }
    public static void addReadSample(Sample s) {
        rSeries.add(s.sampleNum, s.bwMbSec);
        rAvgSeries.add(s.sampleNum, s.cumAvg);
        if (App.showMaxMin) {
            rMaxSeries.add(s.sampleNum, s.cumMax);
            rMinSeries.add(s.sampleNum, s.cumMin);
        }
        if (App.showDriveAccess) {
            rDrvAccess.add(s.sampleNum, s.accessTimeMs);
        }
        Gui.mainFrame.refreshReadMetrics();
        System.out.println(s.toString());
    }
    
    public static void resetBenchmarkData() {
        wSeries.clear();
        rSeries.clear();
        wAvgSeries.clear();
        rAvgSeries.clear();
        wMaxSeries.clear();
        rMaxSeries.clear();
        wMinSeries.clear();
        rMinSeries.clear();
        wDrvAccess.clear();
        rDrvAccess.clear();
        progressBar.setValue(0);
        Gui.mainFrame.refreshReadMetrics();
        Gui.mainFrame.refreshWriteMetrics();
    }
    
    public static void updateLegendAndAxis() {
        bwRenderer.setSeriesVisibleInLegend(0, App.writeTest);
        bwRenderer.setSeriesVisibleInLegend(1, App.writeTest);
        bwRenderer.setSeriesVisibleInLegend(2, App.writeTest && App.showMaxMin);
        bwRenderer.setSeriesVisibleInLegend(3, App.writeTest && App.showMaxMin);
        bwRenderer.setSeriesVisibleInLegend(4, App.readTest);
        bwRenderer.setSeriesVisibleInLegend(5, App.readTest);
        bwRenderer.setSeriesVisibleInLegend(6, App.readTest && App.showMaxMin);
        bwRenderer.setSeriesVisibleInLegend(7, App.readTest && App.showMaxMin);
        
        msRenderer.setSeriesVisibleInLegend(0, App.writeTest && App.showDriveAccess);
        msRenderer.setSeriesVisibleInLegend(1, App.readTest && App.showDriveAccess);
        
        msAxis.setVisible(App.showDriveAccess);
    }
    
    /**
     * GH-2 need solution for dropping catch
     */
    static public void processDropCaching() {
        String osName = System.getProperty("os.name");
        if (osName.contains("Linux")) {
            boolean isRoot = false;
            try {
                isRoot = UtilOs.isRunningAsRootLinux();
            } catch (IOException | InterruptedException ex) {
                Logger.getLogger(Gui.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (isRoot) {
                // GH-2 automate catch dropping
                UtilOs.flushDataToDriveLinux();
                UtilOs.dropWriteCacheLinux();
            } else {
                JOptionPane.showMessageDialog(Gui.mainFrame, 
                        """
                        Run jDiskMark as root to automatically clear the disk cache.\n
                        For valid READ measurements please clear the disk cache by
                        using \"sudo sync; echo 1 > /proc/sys/vm/drop_caches\".
                        Press OK to continue when disk cache has been dropped.""",
                        "Clear Disk Cache Now",
                        JOptionPane.PLAIN_MESSAGE);
            }
        } else if (osName.contains("Mac OS")) {
            boolean isRoot = false;
            try {
                isRoot = UtilOs.isRunningAsRootMacOs();
            } catch (IOException | InterruptedException ex) {
                Logger.getLogger(Gui.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (isRoot) {
                // GH-2 automate catch dropping
                UtilOs.flushDataToDriveMacOs();
                UtilOs.dropWriteCacheMacOs();
            } else {
                JOptionPane.showMessageDialog(Gui.mainFrame, 
                        """
                        For valid READ benchmarks please clear the disk cache.
                        Removable drives can be disconnected and reconnected.
                        For system drives use the WRITE and READ operations 
                        independantly by doing a cold reboot after the WRITE
                        Press OK to continue when disk cache has been cleared.""",
                        "Clear Disk Cache Now",
                        JOptionPane.PLAIN_MESSAGE);
            }
        } else if (osName.contains("Windows")) {
            boolean isAdmin = UtilOs.isRunningAsAdminWindows();
            boolean emptyStandbyListExist = Files.exists(Paths.get(".\\EmptyStandbyList.exe"));
            System.out.println("== admin=" + isAdmin);
            System.out.println("== emptyStandbyListExist=" + emptyStandbyListExist);
            if (isAdmin && emptyStandbyListExist) {
                // GH-2 automate catch dropping
                // delays in place of flush calls
                try {
                    Thread.sleep(1300);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Gui.class.getName()).log(Level.SEVERE, null, ex);
                }
                UtilOs.emptyStandbyListWindows();
                try {
                    Thread.sleep(700);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Gui.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                JOptionPane.showMessageDialog(Gui.mainFrame, 
                        """
                        Run jDiskMark as admin to automatically clear the disk cache.\n
                        For valid READ benchmarks please clear the disk cache by
                        using EmptyStandbyList.exe or RAMMap.exe utilities.
                        Removable drives can be disconnected and reconnected.
                        For system drives use the WRITE and READ operations 
                        independantly by doing a cold reboot after the WRITE
                        Press OK to continue when disk cache has been cleared.""",
                        "Clear Disk Cache Now",
                        JOptionPane.PLAIN_MESSAGE);
            }
        } else {
            String messagePrompt = "Unrecognized OS: " + osName + "\n" +
                    """
                    For valid READ benchmarks please clear the disk cache now.
                    Removable drives can be disconnected and reconnected.
                    For system drives use the WRITE and READ operations 
                    independantly by doing a cold reboot after the WRITE
                    Press OK to continue when disk cache has been cleared.""";
            JOptionPane.showMessageDialog(Gui.mainFrame, 
                    messagePrompt,
                    "Clear Disk Cache Now",
                    JOptionPane.PLAIN_MESSAGE);
        }
    }
    
    static public void loadBenchmark(Benchmark benchmark) {
        resetBenchmarkData();
        chart.getTitle().setText(benchmark.getDriveInfo());
        ArrayList<Sample> samples = benchmark.samples;
        System.out.println("samples=" + samples.size());
        for (Sample s : samples) {
            System.out.println(s);
            if (benchmark.ioMode == Benchmark.IOMode.READ) {
                addReadSample(s);
            } else {
                addWriteSample(s);
            }
        }
        
        App.numOfBlocks = benchmark.numBlocks;
        App.numOfSamples = benchmark.numSamples;
        App.blockSizeKb = benchmark.blockSize;
        App.blockSequence = benchmark.blockOrder;
        Gui.mainFrame.loadSettings();
        
        if (benchmark.ioMode ==  Benchmark.IOMode.READ) {
            App.readTest = true;
            App.rAvg = benchmark.runAvg;
            App.rMax = benchmark.runMax;
            App.rMin = benchmark.runMin;
            App.rAcc = benchmark.accAvg;
            Gui.mainFrame.refreshReadMetrics();
        } else {
            App.writeTest = true;
            App.wAvg = benchmark.runAvg;
            App.wMax = benchmark.runMax;
            App.wMin = benchmark.runMin;
            App.wAcc = benchmark.accAvg;
            Gui.mainFrame.refreshWriteMetrics();
        }
    }
}
