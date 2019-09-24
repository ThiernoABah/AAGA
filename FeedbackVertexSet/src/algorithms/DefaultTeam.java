package algorithms;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class DefaultTeam {

	public ArrayList<Point> calculFVS(ArrayList<Point> points, int edgeThreshold) {
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
		int courant = 1;
		int suivant = 0;
		ArrayList<ArrayList<Point>> retour = new ArrayList<ArrayList<Point>>();
		while(suivant<courant) {
			courant=fvs.size();
			retour = improve(fvs, grapheBunShin, edgeThreshold);
			fvs = retour.get(0);
			grapheBunShin = retour.get(1);
			suivant = fvs.size();
		}
		return fvs;
	}

	public ArrayList<ArrayList<Point>> improve(ArrayList<Point> fvs, ArrayList<Point> grapheRestant,
			int edgeThreshold) {
		ArrayList<ArrayList<Point>> retour = new ArrayList<ArrayList<Point>>();
		ArrayList<Point> res = (ArrayList<Point>) grapheRestant.clone();
		ArrayList<Point> fvs2 = (ArrayList<Point>) fvs.clone();
		for (int i = 0; i < fvs.size(); i++) {
			Point p = fvs.get(i);
			for (int j = i + 1; j < fvs.size(); j++) {
				Point q = fvs.get(j);
				if (p.distance(q) > 5 * edgeThreshold) {
					continue;
				}
				for (Point r : grapheRestant) {
					fvs2.remove(p);
					fvs2.remove(q);
					fvs2.add(r);
					res.add(p);
					res.add(q);
					res.remove(r);
					if (isValid(res, fvs2, edgeThreshold)) {
						retour.add(fvs2);
						retour.add(res);
						return retour;
					} else {
						fvs2.add(p);
						fvs2.add(q);
						fvs2.remove(r);
						res.remove(p);
						res.remove(q);
						res.add(r);
					}
				}
			}
		}
		retour.add(fvs);
		retour.add(grapheRestant);
		return retour;
	}

	private boolean isSolution(ArrayList<Point> pointsIn, int edgeThreshold) {
		ArrayList<Point> visited = new ArrayList<Point>();

		while (!pointsIn.isEmpty()) {
			visited.clear();
			visited.add(pointsIn.remove(0));
			for (int i = 0; i < visited.size(); i++) {
				for (Point p : pointsIn)
					if (isEdge(visited.get(i), p, edgeThreshold)) {
						for (Point q : visited)
							if (!q.equals(visited.get(i)) && isEdge(p, q, edgeThreshold))
								return false;
						visited.add(p);
					}
				pointsIn.removeAll(visited);
			}
		}
		return true;
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
		for (Point p : origPoints) {
			if (!isMember(fvs, p)) {
				vertices.add((Point) p.clone());
			}
		}

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

	private ArrayList<Point> localSearch(ArrayList<Point> firstSolution, ArrayList<Point> points, int edgeThreshold) {
		ArrayList<Point> current = removeDuplicates(firstSolution);
		ArrayList<Point> next = (ArrayList<Point>) current.clone();

		System.out.println("LS. First sol: " + current.size());

		do {
			current = next;
			next = remove2add1(current, points, edgeThreshold);
			System.out.println("LS. Current sol: " + current.size() + ". Found next sol: " + next.size());
		} while (score(current) > score(next));

		System.out.println("LS. Last sol: " + current.size());
		return next;

		// return current;
	}

	private ArrayList<Point> remove2add1(ArrayList<Point> candidate, ArrayList<Point> points, int edgeThreshold) {
		ArrayList<Point> test = removeDuplicates(candidate);
		long seed = System.nanoTime();
		Collections.shuffle(test, new Random(seed));
		ArrayList<Point> rest = removeDuplicates(points);
		rest.removeAll(test);

		for (int i = 0; i < test.size(); i++) {
			for (int j = i + 1; j < test.size(); j++) {
				Point q = test.remove(j);
				Point p = test.remove(i);

				for (Point r : rest) {
					test.add(r);
					if (isSolution(test, points, edgeThreshold))
						return test;
					test.remove(r);
				}

				test.add(i, p);
				test.add(j, q);
			}
		}

		return candidate;
	}

	private boolean isSolution(ArrayList<Point> candidateIn, ArrayList<Point> pointsIn, int edgeThreshold) {
		ArrayList<Point> candidate = removeDuplicates(candidateIn);
		ArrayList<Point> rest = removeDuplicates(pointsIn);
		rest.removeAll(candidate);
		ArrayList<Point> visited = new ArrayList<Point>();

		while (!rest.isEmpty()) {
			visited.clear();
			visited.add(rest.remove(0));
			for (int i = 0; i < visited.size(); i++) {
				for (Point p : rest)
					if (isEdge(visited.get(i), p, edgeThreshold)) {
						for (Point q : visited)
							if (!q.equals(visited.get(i)) && isEdge(p, q, edgeThreshold))
								return false;
						visited.add(p);
					}
				rest.removeAll(visited);
			}
		}

		return true;
	}

	private ArrayList<Point> removeDuplicates(ArrayList<Point> points) {
		ArrayList<Point> result = (ArrayList<Point>) points.clone();
		for (int i = 0; i < result.size(); i++) {
			for (int j = i + 1; j < result.size(); j++)
				if (result.get(i).equals(result.get(j))) {
					result.remove(j);
					j--;
				}
		}
		return result;
	}

	private boolean isEdge(Point p, Point q, int edgeThreshold) {
		return p.distance(q) < edgeThreshold;
	}

	private int degree(Point p, ArrayList<Point> points, int edgeThreshold) {
		int degree = -1;
		for (Point q : points)
			if (isEdge(p, q, edgeThreshold))
				degree++;
		return degree;
	}

	private int score(ArrayList<Point> candidate) {
		return candidate.size();
	}

}
