package FeatureExtraction;

import java.io.BufferedReader;
import java.io.Console;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;


public class ConceptsByRAKE {
	
	static String stopwordsFilePath="/SmartStopWords.txt";
	
	public ConceptsByRAKE(String inputText) {
	this.run(inputText);
	}


	/**
	 * This is a main method which takes input as String and returns the HashMap containing important phrases in it sorted desc by weight.
	 * * @param s
	 * @return
	 */
	public static HashMap<String, Double> GetPhrases(String message){
		HashMap<String, Double>Phrases= new HashMap<String, Double>();
		Vector<String>CndPh= GenerateCandidateKeywords(message);
		HashMap<String, Double>CndScore= calcScore(CndPh);

		for (int i=0; i<CndPh.size();i++){
			Double FinalScore= 0.0;
			String [] words=CndPh.elementAt(i).split(" ");
			for (int j=0; j<words.length;j++){
				FinalScore= FinalScore+CndScore.get(words[j]);
			}
			// to normalize the number of words in a phrase. Divide by number of words in phrase.
			FinalScore=FinalScore/words.length; 
			Phrases.put(CndPh.elementAt(i), FinalScore);
		}
		//sort phrases by weight .		
		return sortHashMapByValuesD(Phrases);

	}

	/**
	 * Takes String of Text and returns Candidate List of Phrases.
	 * @param s
	 * @return
	 */
	public static Vector<String> GenerateCandidateKeywords(String s){
		Vector<String>stopwords= getStopwords();
		Vector<String>CandidatePhrases= new Vector<String>();
		s=s.toLowerCase();
		String[] words=s.split("[0123456789 \\p{P} \\t\\n\\r]");
		int j=0;
		for (int i= 0; i<words.length;i++){
			//System.out.println("w: "+ words[i]);
			if (stopwords.contains(words[i].toLowerCase())){
				String CndPh="";
				for (int k=j; k<i;k++){
					CndPh= CndPh+words[k]+" ";

				}

				j=i+1;

				if (!CndPh.isEmpty()){
					String[]finalCndPh= CndPh.split("[.,]");
					for (int n=0; n<finalCndPh.length;n++){
						if (!finalCndPh[n].equals(" ")){

							CandidatePhrases.add(finalCndPh[n]);
						}
					}
				}
			}
		}

		return CandidatePhrases;

	}

	/**
	 * Takes the List of concepts and Returns weighted list of Phrases.
	 * @param CndPh
	 * @return
	 */
	public static HashMap<String, Double> calcScore(Vector<String>CndPh){
		HashMap<String, Double> Score= new HashMap<String, Double>();
		Set uniWords= new TreeSet();
		for (int i=0; i<CndPh.size();i++){
			String []token= CndPh.elementAt(i).split(" ");
			for (int j=0; j<token.length;j++){
				uniWords.add(token[j]);

			}
		}

		Object[] allWords = uniWords.toArray();
		int[][] matrix= new int[allWords.length][allWords.length];

		for (int i=0; i<matrix.length;i++){
			double deg= 0;
			double fre=0;
			for (int j=0; j<matrix.length;j++){
				matrix[i][j]= calcFreq(CndPh,(String)allWords[i],(String) allWords[j]);
				deg= deg+matrix[i][j];
			}
			fre=fre+matrix[i][i];
			Score.put((String)allWords[i], deg/fre);
		}


		return Score;

	}
	/**
	 * Calculates the Frequency of co-occurence for two keywords together.
	 * @param CndList
	 * @param one
	 * @param two
	 * @return
	 */
	public static int calcFreq(Vector<String>CndList,String one, String two ){
		int result=0;

		for (int i=0; i<CndList.size();i++){
			String s= CndList.elementAt(i);

			if (s.contains(one)&& s.contains(two)){
				result= result+1;

			}
		}

		return result;

	}

	/**
	 * Method to sort the HashMap in desc or asc order by Value. It takes into account if we have duplicate entries in values field.
	 * @param passedMap
	 * @return Sorted Map
	 */
	public static LinkedHashMap sortHashMapByValuesD(HashMap passedMap) {
		List mapKeys = new ArrayList(passedMap.keySet());
		List mapValues = new ArrayList(passedMap.values());
		Collections.sort(mapValues);
		Collections.sort(mapKeys);
		Collections.reverse(mapValues);// to get sort by desc order. remove it to get sort by asc order.

		LinkedHashMap sortedMap = 
				new LinkedHashMap();

		Iterator valueIt = mapValues.iterator();
		while (valueIt.hasNext()) {
			Object val = valueIt.next();
			Iterator keyIt = mapKeys.iterator();

			while (keyIt.hasNext()) {
				Object key = keyIt.next();
				String comp1 = passedMap.get(key).toString();
				String comp2 = val.toString();

				if (comp1.equals(comp2)){
					passedMap.remove(key);
					mapKeys.remove(key);
					sortedMap.put((String)key, (Double)val);
					break;
				}

			}

		}
		return sortedMap;
	}

	
	
	public static Vector<String> getStopwords(){
		Vector<String> stopwords = new Vector<String>();
		
		BufferedReader reader;
		try {
			reader = new BufferedReader
			   (new InputStreamReader
			    (new FileInputStream(stopwordsFilePath)));
		
		
		String line;
				
			while ((line = reader.readLine()) != null) {
				String s1=line.trim();
				stopwords.add(s1);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			return stopwords;
		
	}
	
	public static void run(String inputText ){
		
		HashMap<String, Double>SortedPhrases=GetPhrases(inputText);
		for (String ph:SortedPhrases.keySet()){
			System.out.println(ph + ": "+ SortedPhrases.get(ph));
		}	
	}
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		String inputText= "";
		run(inputText);
		
	}
}
