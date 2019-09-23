package algorithms;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.lang.model.element.NestingKind;

public class DefaultTeam {

	public ArrayList<Point> calculFVS(ArrayList<Point> points, int edgeThreshold) {
		ArrayList<Point> fvs = new ArrayList<Point>();

		return gloutonSolution(points, edgeThreshold);
	}

	public ArrayList<Point> gloutonSolution(ArrayList<Point> graphe, int edgeThreshold) {
		// ArrayList<Point> grapheBunShin = (ArrayList<Point>) graphe.clone();
		// ArrayList<Point> fvs = new ArrayList<Point>();
		//
		// Point max = findMax(grapheBunShin, edgeThreshold);
		//
		// while (!isValid(graphe, fvs, edgeThreshold)) {
		// grapheBunShin.remove(max);
		// fvs.add(max);
		// max = findMax(grapheBunShin, edgeThreshold);
		// }

		ArrayList<Point> grapheBunShin = new ArrayList<>();
		ArrayList<Point> grapheBunShin2 = (ArrayList<Point>) graphe.clone();
		ArrayList<Point> fvs = new ArrayList<Point>();

		Point min = findMin(graphe, edgeThreshold);

		for (int i = 0; i < graphe.size(); i++) {

			grapheBunShin.add(min);
			grapheBunShin2.remove(min);

			if (i == 0) {
				min = findMin(grapheBunShin2, edgeThreshold);
				continue;
			}
			if (isValid(grapheBunShin, fvs, edgeThreshold)) {
				min = findMin(grapheBunShin2, edgeThreshold);
				continue;
			} else {
				grapheBunShin.remove(min);
				fvs.add(min);
				min = findMin(grapheBunShin2, edgeThreshold);
			}
		}
		return fvs;
	}

	public ArrayList<Point> localSearchNiaf(ArrayList<Point> fvs, ArrayList<Point> grapheRestant) {
		ArrayList<Point> res = new ArrayList<>();
		for(Point p: grapheRestant) {
			
		}
		return res;
	}

	public Point findMax(ArrayList<Point> graphe, int edgeThreshold) {
		int max = 0;
		Point res = null;
		for (Point p : graphe) {
			if (neighbor(p, graphe, edgeThreshold).size() > max) {
				max = neighbor(p, graphe, edgeThreshold).size();
				res = p;
			}
		}
		return res;
	}

	public Point findMin(ArrayList<Point> graphe, int edgeThreshold) {
		int min = Integer.MAX_VALUE;
		Point res = null;
		for (Point p : graphe) {
			if (neighbor(p, graphe, edgeThreshold).size() < min) {
				min = neighbor(p, graphe, edgeThreshold).size();
				res = p;
			}
		}
		return res;
	}

	public ArrayList<Point> neighbor(Point p, ArrayList<Point> vertices, int edgeThreshold) {
		ArrayList<Point> result = new ArrayList<Point>();
		for (Point point : vertices)
			if (point.distance(p) < edgeThreshold && !point.equals(p))
				result.add((Point) point.clone());
		return result;
	}

	private boolean isMember(ArrayList<Point> points, Point p) {
		for (Point point : points)
			if (point.equals(p))
				return true;
		return false;
	}

	public boolean isValid(ArrayList<Point> origPoints, ArrayList<Point> fvs, int edgeThreshold) {
		ArrayList<Point> vertices = new ArrayList<Point>();
		for (Point p : origPoints)
			if (!isMember(fvs, p))
				vertices.add((Point) p.clone());

		// Looking for loops in subgraph induced by origPoint \setminus fvs
		while (!vertices.isEmpty()) {
			ArrayList<Point> green = new ArrayList<Point>();
			green.add((Point) vertices.get(0).clone());
			ArrayList<Point> black = new ArrayList<Point>();

			while (!green.isEmpty()) {
				for (Point p : neighbor(green.get(0), vertices, edgeThreshold)) {
					if (green.get(0).equals(p))
						continue;
					if (isMember(black, p))
						return false;
					if (isMember(green, p))
						return false;
					green.add((Point) p.clone());
				}
				black.add((Point) green.get(0).clone());
				vertices.remove(green.get(0));
				green.remove(0);
			}
		}

		return true;
	}

}
