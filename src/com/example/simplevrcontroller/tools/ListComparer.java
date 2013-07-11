package com.example.simplevrcontroller.tools;

import java.util.ArrayList;
import java.util.List;


public class ListComparer<B> {

	private List<B> list;
	private ArrayList<B> values;
	private String rep;

	public ListComparer(List<B> list) {
		this.list = list;

		values = new ArrayList<B>();
		
		rep = "";

		for (B b : list) {

			int ch = values.indexOf(b);

			if (ch == -1) {

				ch = values.size();
				values.add(b);

			}

			rep = rep + (char) (ch + 65);

		}
	}

	public int compareTo(List<B> l2) {
		
		String tmp = "";
		
		for (B b : l2) {

			int ch = values.indexOf(b);

			if (ch == -1) {

				ch = values.size();
				values.add(b);

			}

			tmp = tmp + (char) (ch + 65);

		}
		
		//System.out.println(rep);
		//System.out.println(tmp);
		
		Levenshtein lev = new Levenshtein(rep, tmp);

		return lev.getSimilarity();
		
	}

	public static <A> int compare(List<A> l1, List<A> l2) {

		String s1 = "", s2 = "";

		ArrayList<A> vals = new ArrayList<A>();

		for (A a : l1) {

			int c = vals.indexOf(a);

			if (c == -1) {

				c = vals.size();
				vals.add(a);

			}

			s1 = s1 + (char) (c + 65);

		}

		for (A a : l2) {

			int c = vals.indexOf(a);

			if (c == -1) {

				c = vals.size();
				vals.add(a);

			}

			s2 = s2 + (char) (c + 65);

		}

		Levenshtein lev = new Levenshtein(s1, s2);

		return lev.getSimilarity();
	}

	public List<B> getList() {
		return list;
	}

}
