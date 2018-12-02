package gruppe1.gruppenuebung1;

import java.awt.FlowLayout;
import java.util.List;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;


public class BenchmarkResultPrinter {
	
	public static void printPerformance(List<BenchmarkResult> results) {
		
		JFrame frm = new JFrame();
		frm.setTitle("Performance");

		frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frm.setSize(1600, 700);
		frm.setLayout(new FlowLayout());
		
		
		
		DefaultCategoryDataset performanceData = new DefaultCategoryDataset();
		DefaultCategoryDataset successData = new DefaultCategoryDataset();
		String bmName;
		for(BenchmarkResult bm: results) {
			if (bm != null) {
				bmName = bm.getBenchmarkName();
				performanceData.addValue(bm.getMin(), "min", bmName);
				performanceData.addValue(bm.getAvg(), "avg", bmName);
				performanceData.addValue(bm.getMax(), "max", bmName);
				performanceData.addValue(bm.getVariance(), "var", bmName);
				successData.addValue(bm.getSuccessRate(), "success rate", bmName);
			}
			
		}
		
		JFreeChart chart = ChartFactory.createBarChart("Performance Results", "Benchmark", "Milliseconds", performanceData);
		frm.add(new ChartPanel(chart));
		
		chart = ChartFactory.createBarChart("Success Rate", "Benchmark", "Success Rate", successData);
		frm.add(new ChartPanel(chart));
		
		frm.setVisible(true);
		

	}
}