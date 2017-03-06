
package jdiskmark;

import java.awt.Color;
import java.awt.Dimension;
import java.text.NumberFormat;
import javax.swing.JProgressBar;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * Store gui references for easy access
 */
public final class Gui {
    
    public static ChartPanel chartPanel = null;
    public static MainFrame mainFrame = null;
    public static SelectFrame selFrame = null;
    public static XYSeries wSeries, wAvgSeries, wMaxSeries, wMinSeries;
    public static XYSeries rSeries, rAvgSeries, rMaxSeries, rMinSeries;
    public static JFreeChart chart;
    public static JProgressBar progressBar = null;
    public static RunPanel runPanel = null;
    
    public static ChartPanel createChartPanel() {
        
        wSeries = new XYSeries("Writes");
        wAvgSeries = new XYSeries("Write Avg");
        wMaxSeries = new XYSeries("Write Max");
        wMinSeries = new XYSeries("Write Min");
        
        rSeries = new XYSeries("Reads");
        rAvgSeries = new XYSeries("Read Avg");
        rMaxSeries = new XYSeries("Read Max");
        rMinSeries = new XYSeries("Read Min");
        
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(wSeries);
        dataset.addSeries(wAvgSeries);
        dataset.addSeries(wMaxSeries);
        dataset.addSeries(wMinSeries);
        dataset.addSeries(rSeries);
        dataset.addSeries(rAvgSeries);
        dataset.addSeries(rMaxSeries);
        dataset.addSeries(rMinSeries);
        
        chart = ChartFactory.createXYLineChart(
                        "XY Chart", // Title
                        null, // x-axis Label
                        "Bandwidth MB/s", // y-axis Label
                        dataset, // Dataset
                        PlotOrientation.VERTICAL, // Plot Orientation
                        true,// Show Legend
                        true, // Use tooltips
                        false // Configure chart to generate URLs?
                );
        XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.DARK_GRAY);
        ((NumberAxis) plot.getRangeAxis()).setAutoRangeIncludesZero(false);
        NumberAxis range = (NumberAxis) plot.getDomainAxis();
        range.setNumberFormatOverride(NumberFormat.getNumberInstance());
        chart.getTitle().setVisible(false);
        chartPanel = new ChartPanel(chart) {
            // Only way to set the size of chart panel
            // ref: http://www.jfree.org/phpBB2/viewtopic.php?p=75516
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(500, 325);
            }
        };
        
        plot.getRenderer().setSeriesPaint(0, Color.YELLOW);
        plot.getRenderer().setSeriesPaint(1, Color.WHITE);
        plot.getRenderer().setSeriesPaint(2, Color.GREEN);
        plot.getRenderer().setSeriesPaint(3, Color.RED);
        plot.getRenderer().setSeriesPaint(4, Color.LIGHT_GRAY);
        plot.getRenderer().setSeriesPaint(5, Color.ORANGE);
        plot.getRenderer().setSeriesPaint(6, Color.GREEN);
        plot.getRenderer().setSeriesPaint(7, Color.RED);
        updateLegend();
        return chartPanel;
    }
    
    public static void addWriteMark(DiskMark mark) {
        wSeries.add(mark.markNum, mark.bwMbSec);
        wAvgSeries.add(mark.markNum, mark.cumAvg);
        if (App.showMaxMin) {
            wMaxSeries.add(mark.markNum, mark.cumMax);
            wMinSeries.add(mark.markNum, mark.cumMin);
        }
        Gui.mainFrame.refreshWriteMetrics();
        System.out.println(mark.toString());
    }
    public static void addReadMark(DiskMark mark) {
        rSeries.add(mark.markNum, mark.bwMbSec);
        rAvgSeries.add(mark.markNum, mark.cumAvg);
        if (App.showMaxMin) {
            rMaxSeries.add(mark.markNum, mark.cumMax);
            rMinSeries.add(mark.markNum, mark.cumMin);
        }
        Gui.mainFrame.refreshReadMetrics();
        System.out.println(mark.toString());
    }
    
    public static void resetTestData() {
        wSeries.clear();
        rSeries.clear();
        wAvgSeries.clear();
        rAvgSeries.clear();
        wMaxSeries.clear();
        rMaxSeries.clear();
        wMinSeries.clear();
        rMinSeries.clear();
        progressBar.setValue(0);
        Gui.mainFrame.refreshReadMetrics();
        Gui.mainFrame.refreshWriteMetrics();
    }
    
    public static void updateLegend() {
        chart.getXYPlot().getRenderer().setSeriesVisibleInLegend(0, App.writeTest);
        chart.getXYPlot().getRenderer().setSeriesVisibleInLegend(1, App.writeTest);
        chart.getXYPlot().getRenderer().setSeriesVisibleInLegend(2, App.writeTest&&App.showMaxMin);
        chart.getXYPlot().getRenderer().setSeriesVisibleInLegend(3, App.writeTest&&App.showMaxMin);
        
        chart.getXYPlot().getRenderer().setSeriesVisibleInLegend(4, App.readTest);
        chart.getXYPlot().getRenderer().setSeriesVisibleInLegend(5, App.readTest);
        chart.getXYPlot().getRenderer().setSeriesVisibleInLegend(6, App.readTest&&App.showMaxMin);
        chart.getXYPlot().getRenderer().setSeriesVisibleInLegend(7, App.readTest&&App.showMaxMin);
    }
}
