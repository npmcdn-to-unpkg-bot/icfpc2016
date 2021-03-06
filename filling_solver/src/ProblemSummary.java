import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProblemSummary {
	public static void main(String[] args) throws Exception {
		ArrayList<String> ids = new ArrayList<>();
		ArrayList<Integer> problemSizes = new ArrayList<>();
		ArrayList<Integer> solutionSizes = new ArrayList<>();
		Path dir = Paths.get("../problems/");
		Pattern p = Pattern.compile("../problems/problem_(\\d+)_(\\d+)_(\\d+)\\.txt");
		Files.list(dir).forEach((Path path) -> {
			String filename = path.toString();
			Matcher matcher = p.matcher(filename);
			if (!matcher.matches()) return;
			ids.add(matcher.group(1));
			problemSizes.add(Integer.parseInt(matcher.group(2)));
			solutionSizes.add(Integer.parseInt(matcher.group(3)));
			String imgFilename = String.format("../problems/img/%s.png", matcher.group(1));
			if (Files.exists(Paths.get(imgFilename))) return;
			System.out.println(filename);
			PartsDecomposer decomposer = new PartsDecomposer();
			try {
				decomposer.readInput(new FileInputStream(filename));
			} catch (Exception e) {
				System.err.println(e);
				return;
			}
			decomposer.edgesArrangement();
			decomposer.decompositeToParts();
			decomposer.outputImages(imgFilename);
		});

		HashMap<Integer, Double> scores = new HashMap<>();
		for (String line : Files.readAllLines(Paths.get("../submitter/submit_results.txt"))) { // need local env...
			String[] elems = line.split(",");
			if (elems[0].equals("solve_id")) continue; // header
			int id = Integer.parseInt(elems[0]);
			double s = Double.parseDouble(elems[1]);
			scores.put(id, s);
		}

		try (PrintWriter writer = new PrintWriter(new FileOutputStream("../problems/summary.html"))) {
			writer.println("<!DOCTYPE html>");
			writer.println("<html>");
			writer.println("<head>");
			writer.println("<title>ICFPC2016 problems summary</title>");
			writer.println(
					"<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css\" integrity=\"sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u\" crossorigin=\"anonymous\" />");
			writer.println("</head>");
			writer.println("<body><div class=\"container\">");
			writer.println("<div class=\"page-header\"><h1>ICFPC2016 problems</h1></div>");
			writer.println("<table class=\"table table-bordered\">");
			writer.println("<thead><tr><th>id</th><th>our score</th><th>prob size</th><th>sol size</th><th>image</th></tr></thead>");
			writer.println("<tbody>");
			for (int i = 0; i < ids.size(); ++i) {
				String idStr = ids.get(i);
				int id = Integer.parseInt(idStr);
				writer.println("<tr>");
				writer
						.println(String.format("<td><a name=\"%s\" href=\"http://2016sv.icfpcontest.org/problem/view/%d\">%s</td>", idStr, id, idStr));
				double score = scores.containsKey(id) ? scores.get(id) : 0.0;
				writer.println(String.format("<td>%.4f</td>", score));
				writer.println(String.format("<td>%d</td>", problemSizes.get(i)));
				writer.println(String.format("<td>%d</td>", solutionSizes.get(i)));
				writer.println(String.format("<td><img src=\"img/%s.png\" height=400px /></td>", idStr));
				writer.println("</tr>");
			}
			writer.println("</tbody>");
			writer.println("</div></body>");
			writer.println("</html>");
		}

	}
}
