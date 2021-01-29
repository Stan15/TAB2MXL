package parser;

import java.util.ArrayList;

public class Bar {

	ArrayList<String> Info;
	String Line;
	
	public Bar(String s) { //bar into string
		this.Line = s;
		
		Info = new ArrayList<>(); //stores entire info of each bar
		
		int vertBarCounter = 0; //accounts for every instance of the vertical bar
		int dashCounter = 0; //accounts for every instance of the dash bar
		
		ArrayList<String> temp = new ArrayList<>(); //gets the info from each line and storing into a string, added into 'info'
		
		for(int i = 1; i < Line.length(); i++) {
			
			if(Line.charAt(i) == '-') { //accounts for each instance of dash
				dashCounter++;
			}
			else if(Line.charAt(i) == '|') { //accounts for each instance of vertical bar
				vertBarCounter++;
				dashCounter = 0; //reset dash counter
			}
			else {
				temp.add(""+ Line.charAt(i)); //extracting info of note played at the instance played
				String t = ""; //temp object
				for(int j = 0; j < temp.size(); j++) {
					t = t+temp.get(j); //
				}
				t = vertBarCounter + ", " + t + ", " + dashCounter; //organizes info of note played at the instance played on the bar
				Info.add(t); //storing into arraylist
				dashCounter = 0; //reset dash counter
			}
		}
		
	}
	
}