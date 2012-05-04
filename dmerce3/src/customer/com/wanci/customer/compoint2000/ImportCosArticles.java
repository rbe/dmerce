/*
 * Created on 11.07.2003
 *  
 */
package com.wanci.customer.compoint2000;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Vector;

class ArticleCategory {

	String name;

	ArticleCategory(String name) {
		this.name = name;
	}

}

class Article {

	int number;
	String name;
	double cost;

	Article(int number) {
		this.number = number;
	}

	void setName(String name) {
		this.name = name;
	}

	void setCost(double cost) {
		this.cost = cost;
	}

}

class Articles {

	Vector articles = new Vector();

	Articles() {
	}

	void addArticle(Article article) {
		articles.add(article);
	}

}

/**
 * @author rb
 * @version $Id: ImportCosArticles.java,v 1.2 2004/02/18 15:59:00 rb Exp $
 * 
 * Liest eine Artikelliste aus der Artikelliste.txt der COS AG ein
 *  
 */
class CosArtikelliste {

	/**
	 *  
	 */
	private File f;

	/**
	 * @param f
	 */
	CosArtikelliste(File f) {
		this.f = f;
	}

}

/**
 * @author rb
 * @version $Id: ImportCosArticles.java,v 1.2 2004/02/18 15:59:00 rb Exp $
 * 
 * Liest eine Artikelinfos aus der Artikelinfo.txt der COS AG ein
 *  
 */
class CosArtikelinfos {

	/**
	 *  
	 */
	private File f;

	/**
	 * @param f
	 */
	CosArtikelinfos(File f) {
		this.f = f;
	}

}

/**
 * @author rb
 * @version $Id: ImportCosArticles.java,v 1.2 2004/02/18 15:59:00 rb Exp $
 * 
 * Liest eine Preisliste aus der Preisliste.txt der COS AG ein
 *  
 */
class CosPreisliste {

	/**
	 *  
	 */
	private File f;

	private Articles articles;

	/**
	 * @param f
	 */
	CosPreisliste(File f) {
		this.f = f;
		articles = new Articles();
	}

	void readFile() throws IOException {

		BufferedReader br = new BufferedReader(new FileReader(f));
		String line;
		while ((line = br.readLine()) != null) {

			StringTokenizer st = new StringTokenizer(line, "\t");
			String artNo = st.nextToken();
			if (artNo.equals("v2.0"))
				continue;
			String artName = st.nextToken();
			double artCost =
				Double.valueOf(st.nextToken().replace(',', '.')).doubleValue();
			String artCurr = st.nextToken();
			String artCat = st.nextToken();
			String artSubCat = st.nextToken();

			Article article = new Article(Integer.valueOf(artNo).intValue());
			article.setName(artName);
			article.setCost(artCost);
			articles.addArticle(article);

			System.out.println(
				artNo
					+ ", "
					+ artName
					+ "\n"
					+ "Costs: "
					+ artCost
					+ " "
					+ artCurr
					+ ", Cat: "
					+ artCat
					+ ", "
					+ artSubCat);

		}

	}

}

/**
 * @author rb
 * @version $Id: ImportCosArticles.java,v 1.2 2004/02/18 15:59:00 rb Exp $
 * 
 * Importiert Artikellisten der COS AG (ASCII) in eine SQL-Datenbank
 * 
 * Alle .txt-Dateien, die importiert werden haben als Trennzeichen zwischen den
 * Feldern ASCII-Code 9 (TAB)
 * 
 * Format der Artikelliste.txt: Art-Nr. Bezeichnung Kategorie Subkategorie
 * 
 * Format der Preisliste.txt Art-Nr. Bezeichnung Preis Waehrung Kategorie
 * 
 * Format der Artikelinfos.txt Art-Nr. Beschreibung Text
 * 
 * Es koennen mehrere Zeilen pro Artikel vorkommen!
 *  
 */
public class ImportCosArticles {

	/**
	 * 
	 *  
	 */
	public ImportCosArticles() {

	}

	public static void main(String[] args) throws IOException {

		CosPreisliste c = new CosPreisliste(new File("Preisliste.txt"));
		c.readFile();

	}

}