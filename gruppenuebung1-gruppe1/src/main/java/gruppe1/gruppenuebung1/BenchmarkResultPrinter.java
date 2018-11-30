package gruppe1.gruppenuebung1;

import java.awt.FlowLayout;
import java.util.List;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;


public class BenchmarkResultPrinter {
	
public static void print(String title, List<BenchmarkResult> results) {
		
		JFrame frm = new JFrame();
		frm.setTitle("Performance");

		frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frm.setSize(800, 500);
		frm.setLayout(new FlowLayout());
		
		
		
		DefaultCategoryDataset cd = new DefaultCategoryDataset();
		for(BenchmarkResult pm: results) {
			double min = pm.getMin();
			double avgDelta = pm.getAvg() - min;
			double maxDelta = pm.getMax() - avgDelta - min;
			
			cd.addValue(min, "min", pm.getBenchmarkName());
			cd.addValue(avgDelta, "avg", pm.getBenchmarkName());
			cd.addValue(maxDelta, "max", pm.getBenchmarkName());
		}
		
		JFreeChart chart = ChartFactory.createStackedBarChart(title, "Task", "Milliseconds", cd);
				
		frm.add(new ChartPanel(chart));
		
		frm.setVisible(true);
		

	}

}
