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
		
		
		
		DefaultCategoryDataset cd = new DefaultCategoryDataset();
		for(BenchmarkResult pm: results) {
			cd.addValue(pm.getMin(), "min", pm.getBenchmarkName());
			cd.addValue(pm.getAvg(), "avg", pm.getBenchmarkName());
			cd.addValue(pm.getMax(), "max", pm.getBenchmarkName());
			cd.addValue(pm.getVariance(), "var", pm.getBenchmarkName());
		}
		
		JFreeChart chart = ChartFactory.createBarChart("Performance Results", "Benchmark", "Milliseconds", cd);
		frm.add(new ChartPanel(chart));
		
		
		cd = new DefaultCategoryDataset();
		for(BenchmarkResult pm: results) {
			cd.addValue(pm.getSuccessRate(), "success rate", pm.getBenchmarkName());
		}
		
		chart = ChartFactory.createBarChart("Success Rate", "Benchmark", "Success Rate", cd);
				
		frm.add(new ChartPanel(chart));
		frm.setVisible(true);
		

	}
}