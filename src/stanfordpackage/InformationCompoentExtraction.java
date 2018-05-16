package stanfordpackage;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

import stanfordpackage.ElementAccessClass;
import stanfordpackage.SingleWordTagClass;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeGraphNode;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;

public class InformationCompoentExtraction {

	public static void main(String[] args) throws Exception {
		
				TreebankLanguagePack tlp = new PennTreebankLanguagePack();
			      GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
			      LexicalizedParser lp = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz" );
			      lp.setOptionFlags(new String[]{"-maxLength", "700", "-retainTmpSubcategories"});
			      TokenizerFactory tokenizerFactory = PTBTokenizer.factory( new CoreLabelTokenFactory(), "");
			       int e=0;
			       // folder containing sentence corpus
			       File folder = new File("C:\\Users\\ASLAM\\corpus\\");
			       
					File[] listOfFiles = folder.listFiles();
					System.out.println(listOfFiles.length);
					
					for(File file:listOfFiles){
							
						FileReader fr=new FileReader(file);
						String name=file.getName();
						System.out.println(name);
						
				
			    BufferedReader br=new BufferedReader(fr);
			    FileWriter filewriter=new FileWriter("C:\\Users\\ASLAM\\Desktop\\ICsTriplets.txt", true);// Output file path for text file to write Triplets
			    PrintWriter pw=new PrintWriter(filewriter);
			    			    
				String text; String line;  String sno=null;
				while((line=br.readLine())!=null){
													
				if((line.length()!=0)&&(!(line.contains("_PMID:")))){
													
					  String textsplit[]=line.split("(?<=[.])\\s+(?=[^a-zA-Z])"); // split text into sentences
					  
					  System.out.println(textsplit.length);
					  e++;
				   for(int x=0;x<textsplit.length; x++){
						  text=textsplit[x].trim().replaceAll("^Abstract:|Title:", "").replaceAll("^\\[|\\]$","");							
						  System.out.println("Sentence: "+x+":  "+text);
						  
					      pw.println("Sentence:");
					      pw.println(text);
					   
					     
			        List wordList = tokenizerFactory.getTokenizer(new StringReader(text.trim())).tokenize();
			      
			        Tree tree = lp.apply(wordList);
			      
			      System.out.println(tree.labeledYield());				    
			      GrammaticalStructure gs = gsf.newGrammaticalStructure(tree);
			      Collection tdl=gs.typedDependenciesCCprocessed();
				
				
				System.out.println(tdl);
				
				Object[] list = tdl.toArray();
				System.out.println(list.length);
				TypedDependency tdo;
				String deprl,srcn,destn, srcntag,destntag, singleWord, singleWordTag;
				int srcnindex, destindex;  
				
				 ArrayList<SingleWordTagClass> singleWordAr=new ArrayList<SingleWordTagClass>();   LinkedHashSet<ElementAccessClass> depNewRl=new LinkedHashSet<ElementAccessClass>(); 
				
				 LinkedHashSet<ElementAccessClass> taggedDep=new LinkedHashSet<ElementAccessClass>();  TypedDependency ty=null; TypedDependency tz=null;
			      
				 // Formation of single composite word 
				 
				 for( int i=0; i<list.length; i++){       // for loop for making single list and tagged dependencies without root
					 tdo= (TypedDependency) list[i];
					 deprl=tdo.reln().toString(); 
					         
					 if(!deprl.equals("root")){
						 
					 srcn=tdo.gov().toString().substring(0,tdo.gov().toString().indexOf("/"));
					 srcntag=tdo.gov().tag();
					 srcnindex=tdo.gov().index();
					 
					 destn=tdo.dep().toString().substring(0,tdo.dep().toString().indexOf("/"));
					 destntag=tdo.dep().tag();
					 destindex=tdo.dep().index();
					 taggedDep.add(new ElementAccessClass(deprl,srcn,srcntag,srcnindex,destn,destntag,destindex));
				 
				      	 
				       int count=0;
		   		 if((deprl.equalsIgnoreCase("compound")||deprl.equalsIgnoreCase("amod"))&&(!(srcn.equals("i.e.")||destn.equals("i.e.")||srcn.equals("-RSB-")||destn.equals("-RSB-")||srcn.equals("-LSB-")||destn.equals("-LSB-")||srcn.equals("%")||destn.equals("%")||destn.equals("+")||destn.equals("_")||destn.equals("¿")||srcn.equals("¿")))){   // joining words of nn clause and words of amod clause 
		   		        
		   			   if(i<list.length-2)   ty=(TypedDependency) list[i+1];    int newflag=0;
		   			    if(ty!=null){   
		   			       if(ty.reln().toString().equalsIgnoreCase("compound")&&srcn.equals(ty.gov().toString().substring(0,ty.gov().toString().indexOf("/")))&&(ty.dep().index()==destindex+1)&&((srcnindex-destindex)<=5)&&(!(srcn.equals("one")||ty.dep().toString().substring(0,ty.dep().toString().indexOf("/")).equals("-LSB-")))){  
		   			    	 
		   			    	   singleWord=destn+"_"+ty.dep().toString().substring(0,ty.dep().toString().indexOf("/"))+"_"+srcn;
		   				       singleWordTag=srcntag;	   				     
		   				       singleWordAr.add(new SingleWordTagClass(singleWord, singleWordTag,srcnindex));
		   				      i++;  count=1;
		   			    	  // }
		   			       }else if(ty.reln().toString().equalsIgnoreCase("amod")&&srcn.equals(ty.gov().toString().substring(0,ty.gov().toString().indexOf("/")))&&(ty.dep().index()==destindex+1)&&((srcnindex-destindex)<=5)&&(!(srcn.equals("one")))){
		   				      singleWord=destn+"_"+ty.dep().toString().substring(0,ty.dep().toString().indexOf("/"))+"_"+srcn;
		   				       singleWordTag=srcntag;
		   				      
		   				       singleWordAr.add(new SingleWordTagClass(singleWord, singleWordTag,srcnindex));
		   				        i++;  count=1;
		   			            } 
		   			     }
		   			 if((srcnindex==(destindex+1))&&(deprl.equals("compound")||deprl.equals("amod")&&(count==0))&&(!(srcn.equals("i.e.")||destn.equals("i.e.")||srcn.equals("-RSB-")||destn.equals("-RSB-")||srcn.equals("-LSB-")||destn.equals("-LSB-")||srcn.equals("%")||destn.equals("%")))){   
		   					   singleWord=destn+"_"+srcn; 
		   					   singleWordTag=srcntag;
		   					
		   					   singleWordAr.add(new SingleWordTagClass(singleWord, singleWordTag,srcnindex));
		   			    }	
		   			 if((srcnindex==(destindex+2))&&(deprl.equals("compound")||deprl.equals("amod")&&(count==0))&&(!(srcn.equals("i.e.")||destn.equals("i.e.")||srcn.equals("-RSB-")||destn.equals("-RSB-")||srcn.equals("-LSB-")||destn.equals("-LSB-")||srcn.equals("%")||destn.equals("%")||srcn.equals("one")))){    // This is added on "03/08/2015" to capture some of the non captured amod or cmod when the srcn and destn has difference in their index 2  as in case of Vitamin A subgroup
	   					   singleWord=destn+"_"+srcn; 
	   					   singleWordTag=srcntag;
	   					  
	   					   singleWordAr.add(new SingleWordTagClass(singleWord, singleWordTag,srcnindex));
	   			    }
					 }
		   		 if(deprl.equals("nmod:poss")&&(srcnindex==destindex+2)){
		   			 singleWord=destn+"_"+srcn;
		   			 singleWordTag=destntag;
		   			 singleWordAr.add(new SingleWordTagClass(singleWord, singleWordTag,srcnindex));
		   			 
		   		 }
		   		 if(deprl.equals("advmod")&&(srcnindex==destindex+1)){
		   			 singleWord=destn+"_"+srcn;
		   			 //singleWordTag=destntag;
		   			singleWordTag=srcntag;
		   			// System.out.println("HELLO  "+ singleWord+" "+srcnindex);
		   			 singleWordAr.add(new SingleWordTagClass(singleWord, singleWordTag,srcnindex)); 
		   		 }
		   		 if(deprl.equals("conj:and")&&srcn.equals("loss")&&destn.equals("weight")&&(srcnindex==destindex+1)){
		   			singleWord=destn+"_"+srcn; 
		   			singleWordTag=srcntag;
		   			singleWordAr.add(new SingleWordTagClass(singleWord, singleWordTag,srcnindex)); 
		   		 }
					 } //Outer if 
				 }//for 
				
				
			   
				 Object[] list2=taggedDep.toArray();  
 
				 //TAGGED Dependenceis after replacing the singlewords code starts here
				  String sword, swordTag,str1, str2, str3=""; int swordIndex;
				  ElementAccessClass rtd; //String firstW, secondW;           // DependencyClass repo; String dept,fw,sw;
				 for(int i=0; i<singleWordAr.size();i++){                // for loop for reducing dependencies by collasping and replacing the single words
			    	   sword=singleWordAr.get(i).singleWord;
			    	   swordTag=singleWordAr.get(i).tag;
			    	   swordIndex=singleWordAr.get(i).index;
			    	   str1=sword.substring(0,sword.indexOf("_") );
			    	   str2=sword.substring((sword.indexOf("_")+1));
			    	   if(str2.contains("_")){
			    		   str3=str2.substring(str2.indexOf("_")+1);
			    		   str2=str2.substring(0,str2.indexOf("_"));
			    		   
			    	   }
			    	   			    	   			    	  
			    	  System.out.println(sword+" "+swordTag+" "+swordIndex);
			    	   for(int j=0; j<list2.length; j++){   
			    		   rtd= (ElementAccessClass) list2[j];
	   
			  			    deprl=rtd.depType;
			  				 srcn=rtd.firstWord;
			  				 srcntag=rtd.firstTag;
			  				 srcnindex=rtd.firstIndex;
			  				 
			  				 destn=rtd.secondWord;
			  				 destntag=rtd.secondTag;
			  				 destindex=rtd.secondIndex;
			  				
			  				if(str3.isEmpty()){
			  					if(((srcn.equals(str1)||srcn.equals(str2))&&((srcnindex==swordIndex)||(srcnindex+1==swordIndex)||(srcnindex+2==swordIndex)||(srcnindex-1==swordIndex)||(srcnindex-2==swordIndex)))&&((destn.equals(str1)||destn.equals(str2))&&((destindex==swordIndex)||(destindex+1==swordIndex)||(destindex+2==swordIndex)||(destindex-1==swordIndex)||(destindex-2==swordIndex)))){
			  						 depNewRl.add(new ElementAccessClass(deprl,sword,swordTag,swordIndex,sword,swordTag,swordIndex));   
			  					}
			  					else if(((srcn.equals(str1)||srcn.equals(str2))&&((srcnindex==swordIndex)||(srcnindex+1==swordIndex)||(srcnindex+2==swordIndex)||(srcnindex-1==swordIndex)||(srcnindex-2==swordIndex)))){
					    	     
					    	    	 depNewRl.add(new ElementAccessClass(deprl,sword,swordTag,swordIndex,destn,destntag,destindex));  	
					    	     }
			  					else if((destn.equals(str1)||destn.equals(str2))&&((destindex==swordIndex)||(destindex+1==swordIndex)||(destindex+2==swordIndex)||(destindex-1==swordIndex)||(destindex-2==swordIndex))){
					    	    	
					    	    	 depNewRl.add(new  ElementAccessClass(deprl,srcn,srcntag,srcnindex,sword,swordTag,swordIndex));
					    	     }else{
					    	    	depNewRl.add(rtd);
					    	    	
					    	     }
			  				    
			  				}else{
			  					if(((srcn.equals(str1)||srcn.equals(str2)||srcn.equals(str3))&&((srcnindex==swordIndex)||(srcnindex+1==swordIndex)||(srcnindex+2==swordIndex)||(srcnindex-1==swordIndex)||(srcnindex-2==swordIndex)))&&((destn.equals(str1)||destn.equals(str2)||destn.equals(str3))&&((destindex==swordIndex)||(destindex+1==swordIndex)||(destindex+2==swordIndex)||(destindex-1==swordIndex)||(destindex-2==swordIndex)))){
			  						 depNewRl.add(new ElementAccessClass(deprl,sword,swordTag,swordIndex,sword,swordTag,swordIndex)); 
			  					}
			  				  else if((srcn.equals(str1)||srcn.equals(str2)||srcn.equals(str3))&&((srcnindex==swordIndex)||(srcnindex+1==swordIndex)||(srcnindex+2==swordIndex)||(srcnindex-1==swordIndex)||(srcnindex-2==swordIndex))){
						    	    	
						    	   	 depNewRl.add(new ElementAccessClass(deprl,sword,swordTag,swordIndex,destn,destntag,destindex));
						    	     }
			  					 else if((destn.equals(str1)||destn.equals(str2)||destn.equals(str3))&&((destindex==swordIndex)||(destindex+1==swordIndex)||(destindex+2==swordIndex)||(destindex-1==swordIndex)||(destindex-2==swordIndex))){
						    	    	
						    	    	 depNewRl.add(new  ElementAccessClass(deprl,srcn,srcntag,srcnindex,sword,swordTag,swordIndex));
						    	     }else{
						    	    	 depNewRl.add(rtd);
						    	    	 //depNewRl.add(new ElementAccessClass(deprl,srcn,srcntag,srcnindex,destn,destntag,destindex));
						    	     }
			  				  }
					    	    	  				 
			  			   }
			  			 
			    	    list2=depNewRl.toArray();		    	
					 
			    	    depNewRl.clear();  
			    	   }  // for loop reduced dependencies with single word replaced ends  here
				  taggedDep.clear();
				 ElementAccessClass asl;
				
				 // Triplet extractions from the list of reduced Dependencies
				        //Making final tagged dependency list containing only required dependencies
				 LinkedHashSet<ElementAccessClass> lhsfinal=new  LinkedHashSet<ElementAccessClass>();
				 
				
				
				 for(int j=0;j<list2.length;j++){    // loop for Making final tagged dependency list containing only required dependencies
						asl=(ElementAccessClass) list2[j]; 							
							if(!((asl.depType.equals("compound"))||asl.depType.equals("amod")||asl.depType.equals("det")||asl.depType.equals("root")||asl.depType.equals("dep")))  // removing dep
						lhsfinal.add(new ElementAccessClass(asl.depType,asl.firstWord,asl.firstTag,asl.firstIndex,asl.secondWord,asl.secondTag,asl.secondIndex));
				 }
				
				 System.out.println("Reduced Dependencies");
				  pw.println();
				  pw.println("Reduced dependencies:");
				  for(ElementAccessClass a: lhsfinal){        //This for loop also prints finals list of  reduced dependencies
				    	System.out.println(" "+a.depType+"( "+a.firstWord+" "+a.firstTag+" "+a.firstIndex+" "+a.secondWord+" "+a.secondTag+" "+a.secondIndex+" )");
				    	pw.println(a.depType+"("+a.firstWord+" /"+a.firstTag+" /"+a.firstIndex+", "+a.secondWord+" /"+a.secondTag+" /"+a.secondIndex+")");
				    }
				  
				   pw.println();	    		 	 
			       // pw.println("FIRST WORD  || RELATION || SEOCND WORD ");
			      //  pw.println("--------------------------------------------------");
			       // System.out.println("FIRST WORD  || RELATION || SEOCND WORD ");
			        System.out.println("--------------------------------------------------"); 
					    Object[] myobj=lhsfinal.toArray(); 
					     ElementAccessClass ai;      String temp=""; 
					    for(int i=0;i<myobj.length; i++){               // For loop for extracting triplets from the arraylist  al
					    	 ai=(ElementAccessClass) myobj[i];// System.out.println(al.depType+" "+al.firstWord+" "+al.firstTag+" "+ al.secondWord+" "+al.secondTag);  }
					    	//*********************************
					    	 //Rule 1				    	 
			             	if((ai.depType.contains("nsubj")&&ai.firstTag.contains("NN")&&ai.secondTag.contains("NN"))&&(!(ai.secondWord.equals("%")||ai.firstWord.isEmpty())))        //Rule 1
			             	{
			             		  ElementAccessClass aj;      
				             		 for(int j=0; j<myobj.length; j++){
				             			 aj=(ElementAccessClass) myobj[j];
				             			 
				             			 if(aj.depType.equalsIgnoreCase("cop")&&aj.firstWord.equals(ai.firstWord)&&aj.firstTag.equals(ai.firstTag)&&aj.secondTag.contains("VB"))
				             			   { 
				             				 // Rule 1
				             				 boolean r1_1=false,r1_2=false,r1_3=false,r1_2_1=false,r1_2_2=false;
				             				 
				             				  boolean r1=true;
				             				 ElementAccessClass ak;      
						             		 for(int k=0; k<myobj.length; k++){
						             			 ak=(ElementAccessClass) myobj[k];
						             			 
						             			 if(ak.depType.equalsIgnoreCase("conj:and")&&ak.firstWord.equals(ai.secondWord)&&ak.firstTag.equals(ai.secondTag)&&ak.secondTag.contains("NN"))
						             			   {
						             				 //Rule 1.1
						             				 pw.println(ai.secondWord+" and "+ak.secondWord+ " || "+aj.secondWord+" || "+aj.firstWord+ "		-----From Rule 1.1 ");
						             				 System.out.println(ai.secondWord +" and "+ak.secondWord+ " || "+aj.secondWord+" || "+aj.firstWord+ "		-----From Rule 1.1 ");
						             				r1_1=true;
						             			  }
						             			if(ak.depType.contains("nmod:")&&ak.firstWord.equals(ai.secondWord)&&ak.firstTag.equals(ai.secondTag)&&ak.secondTag.contains("NN"))
						             			   {
						             				 //Rule 1.2
						             				String xx=ak.depType.substring(ak.depType.indexOf(":")+1);
						             				
						             				 r1_2=true;
						             				 ElementAccessClass al;      
								             		 for(int l=0; l<myobj.length; l++){
								             			 al=(ElementAccessClass) myobj[l];
								             			 
								             			 if(al.depType.equalsIgnoreCase("conj:and")&&al.firstWord.equals(ai.firstWord)&&al.firstTag.equals(ai.firstTag)&&al.secondTag.contains("NN"))
								             			   {
								             				 // Rule 1.2.1
								             				 pw.println(ai.secondWord+" "+xx+" "+ak.secondWord+ " || "+aj.secondWord+" || "+al.secondWord+ "		-----From Rule 1.2.1 ");
								             				 System.out.println(ai.secondWord +" "+xx+" "+ak.secondWord+ " || "+aj.secondWord+" || "+al.secondWord+ "		-----From Rule 1.2.1 ");
								             				r1_2_1=true;
								             		      }
								             			 if(al.depType.equalsIgnoreCase("nummod")&&al.firstWord.equals(ai.firstWord)&&al.firstTag.equals(ai.firstTag)&&al.secondTag.contains("CD"))
								             			   {
								             				 // Rule 1.2.2
								             				 pw.println(ai.secondWord+" "+xx+" "+ak.secondWord+ " || "+aj.secondWord+" || "+al.secondWord+" "+ai.firstWord +"		-----From Rule 1.2.2 ");
								             				 System.out.println(ai.secondWord +" "+xx+" "+ak.secondWord+ " || "+aj.secondWord+" || "+al.secondWord+ " "+ai.firstWord +"		-----From Rule 1.2.2 ");
								             				r1_2_2=true;
								             		      }
								             			
								                     } //end for loop l
								             		// if(r1_2_1||r1_2_2) r1_2=false;
								             		 if(r1_2){
								             			 pw.println(ai.secondWord+" "+xx+" "+ak.secondWord+ " || "+aj.secondWord+" || "+aj.firstWord+ "		-----From Rule 1.2 ");
								             			 System.out.println(ai.secondWord +" "+xx+" "+ak.secondWord+ " || "+aj.secondWord+" || "+aj.firstWord+ "		-----From Rule 1.2 ");
								             		 }
						             			 }
						             			
						             			if(ak.depType.equalsIgnoreCase("neg")&&ak.firstWord.equals(ai.firstWord)&&ak.firstTag.equals(ai.firstTag)&&ak.secondTag.contains("RB"))
						             			   {
						             				 //Rule 1.3
						             				 pw.println(ai.secondWord+" || "+aj.secondWord+" "+ak.secondWord+" || "+ai.firstWord+ "		-----From Rule 1.3 ");
						             				 System.out.println(ai.secondWord +" || "+aj.secondWord+" "+ak.secondWord+" || "+ai.firstWord+ "		-----From Rule 1.3 ");
						             				 r1_3=true;
						             			  }
						             		 } // end for loop k				             				
						             		 if(r1_1||r1_2||r1_3||r1_2_1||r1_2_2) r1=false;
						             		 if(r1){
						             			 pw.println(ai.secondWord+" || "+aj.secondWord+" || "+aj.firstWord+ "		-----From Rule 1: ");
					             				  System.out.println(ai.secondWord+" || "+aj.secondWord+" || "+aj.firstWord+ "		-----From Rule 1: ");
						             		    }
				             				}
				             			 
				             			 
				             			   }  // end for loop j
			             	} // Rule 1 and its sub-rule ends here
			             	//*********************************
			             	// Rule 2 starts here
			             	
			             	if((ai.depType.contains("nsubj")&&ai.firstTag.contains("NN")&&ai.secondTag.contains("NN"))&&(!(ai.secondWord.equals("%")||ai.firstWord.isEmpty())))        //Rule 1
			             	{
			             		  ElementAccessClass aj;      
				             		 for(int j=0; j<myobj.length; j++){
				             			 aj=(ElementAccessClass) myobj[j];
				             			 
				             			 if(aj.depType.equalsIgnoreCase("acl")&&aj.firstWord.equals(ai.firstWord)&&aj.firstTag.equals(ai.firstTag)&&aj.secondTag.contains("VB"))
				             			   { 
				             				 // Rule 2
				             				 boolean  r2_1=false, r2_1_1=false, r2_1_1_1=false, r2_3=false, r2_2=false, r2_2_1=false, r2_2_2=false, r2_2_2_1=false;
				             				 ElementAccessClass ak;      
						             		 for(int k=0; k<myobj.length; k++){
						             			 ak=(ElementAccessClass) myobj[k];
						             			 
						             			 if(ak.depType.equalsIgnoreCase("dobj")&&ak.firstWord.equals(aj.secondWord)&&ak.firstTag.equals(aj.secondTag)&&ak.secondTag.contains("NN"))
						             			   { 
						             				 //Rule 2.1
						             				
						             				 r2_1=true;
						             				 ElementAccessClass al;      
								             		 for(int l=0; l<myobj.length; l++){
								             			 al=(ElementAccessClass) myobj[l];
								             			 
								             			 if(al.depType.contains("nmod:")&&al.firstWord.equals(ai.secondWord)&&al.firstTag.equals(ai.secondTag)&&al.secondTag.contains("NN"))
								             			   {
								             				 // Rule 2.1.1
								             				 String xx=al.depType.substring(al.depType.indexOf(":")+1);       				
								             				
								             				r2_1_1=true;
								             				ElementAccessClass am;      
										             		 for(int m=0; m<myobj.length; m++){
										             			 am=(ElementAccessClass) myobj[m];
										             			 
										             			 if(am.depType.equalsIgnoreCase("mark")&&am.firstWord.equals(ak.firstWord)&&am.firstTag.equals(ak.firstTag)&&am.secondTag.contains("IN"))
										             			   {
										             				 // Rule 2.1.1.1										             				 
										             				 pw.println(ai.secondWord+" "+xx+" "+al.secondWord+ " || "+aj.firstWord+" "+am.secondWord+" "+aj.secondWord+" || "+ak.secondWord+ "		-----From Rule 2.1.1.1 ");
										             				 System.out.println(ai.secondWord +" "+xx+" "+al.secondWord+ " || "+aj.firstWord+" "+am.secondWord+" "+aj.secondWord+" || "+ak.secondWord+ "		-----From Rule 2.1.1.1 ");
										             				r2_1_1_1=true;
										             				
										             			   }
										             		 } // end for loop m
										             		 
										             		 if(r2_1_1_1)r2_1_1=r2_1=false;
										             		 if(r2_1_1){
										             			 pw.println(ai.secondWord+" "+xx+" "+al.secondWord+ " || "+aj.secondWord+" || "+ak.secondWord+ "		-----From Rule 2.1.1 ");
									             				 System.out.println(ai.secondWord +" "+xx+" "+al.secondWord+ " || "+aj.secondWord+" || "+ak.secondWord+ "		-----From Rule 2.1.1 ");
									             				r2_1=false;
										             		 }
										             		       		 
								             		      }
								             			 
								             		 }// end for loop l
								             		if(r2_1){
								             			 pw.println(ai.secondWord+ " || "+aj.secondWord+" || "+ak.secondWord+ "		-----From Rule 2.1 ");
							             				 System.out.println(ai.secondWord +" || "+aj.secondWord+" || "+ak.secondWord+ "		-----From Rule 2.1 ");
								             		} 
								             		 
						             			   }
						             			// ****		             		
						             			// 2.2
						             			 if(ak.depType.contains("nmod:")&&ak.firstWord.equals(aj.secondWord)&&ak.firstTag.equals(aj.secondTag)&&ak.secondTag.contains("NN"))
						             			   { 
						             				
						             				 //Rule 2.2
						             				 String xx=ak.depType.substring(ak.depType.indexOf(":")+1);
						             				 
						             				r2_2=true;
						             				 ElementAccessClass al;      
								             		 for(int l=0; l<myobj.length; l++){
								             			 al=(ElementAccessClass) myobj[l];
								             			 
								             			 if(al.depType.contains("nmod:")&&al.firstWord.equals(ai.secondWord)&&al.firstTag.equals(ai.secondTag)&&al.secondTag.contains("NN"))
								             			   {
								             				 // Rule 2.2.1
								             				 String yy=al.depType.substring(al.depType.indexOf(":")+1);
								             				 pw.println(ai.secondWord+" "+yy+" "+al.secondWord+ " || "+aj.secondWord+" "+xx+" || "+ak.secondWord+ "		-----From Rule 2.2.1 ");
								             				 System.out.println(ai.secondWord +" "+yy+" "+al.secondWord+ " || "+aj.secondWord+" "+xx+" || "+ak.secondWord+ "		-----From Rule 2.2.1 ");
								             				r2_2_1=true;
								             			   }
								             			if(al.depType.contains("nmod:")&&al.firstWord.equals(ak.secondWord)&&al.firstTag.equals(ak.secondTag)&&al.secondTag.contains("NN"))
								             			   {
								             				 // Rule 2.2.2
								             				 String yy=al.depType.substring(al.depType.indexOf(":")+1);
								             				
								             				 r2_2_2=true;
								             				 ElementAccessClass am;      
										             		 for(int m=0; m<myobj.length; m++){
										             			 am=(ElementAccessClass) myobj[m];
										             			 
										             			 if(am.depType.contains("nmod:")&&am.firstWord.equals(al.secondWord)&&am.firstTag.equals(al.secondTag)&&am.secondTag.contains("NN"))
										             			   {
										             				 // Rule 2.2.2.1
										             				 
										             				String z=am.depType.substring(am.depType.indexOf(":")+1);
										             				 pw.println(ai.secondWord+" || "+aj.secondWord+" "+xx+" || "+al.secondWord+" "+z+" "+ am.secondWord+"		-----From Rule 2.2.2 ");
										             				 System.out.println(ai.secondWord +" || "+aj.secondWord+" "+xx+" || "+al.secondWord+" "+z+" "+ am.secondWord+"		-----From Rule 2.2.2 ");
										             				r2_2_2_1=true;				             				 
										             				 
										             			   }
										             			 
										             		 }
										             		 if(r2_2_2_1) r2_2_2=false;
										             		 if(r2_2_2){
										             			 pw.println(ai.secondWord+" || "+aj.secondWord+" "+xx+" || "+ak.secondWord+" "+yy+" "+ al.secondWord+"		-----From Rule 2.2.2 ");
									             				 System.out.println(ai.secondWord +" || "+aj.secondWord+" "+xx+" || "+ak.secondWord+" "+yy+" "+ al.secondWord+"		-----From Rule 2.2.2 ");
									             				 
										             		 }
										             		 
								             			   }
								             		 } //end for loop l
                                                     if(r2_2_1||r2_2_2||r2_2_2_1) r2_2=false;
                                                     if(r2_2){
                                                    	 pw.println(ai.secondWord+ " || "+aj.secondWord+" "+xx+" || "+ak.secondWord+ "		-----From Rule 2.2 ");
    						             				 System.out.println(ai.secondWord +" || "+aj.secondWord+" "+xx+" || "+ak.secondWord+ "		-----From Rule 2.2 ");
                                                     }
						             			 
						             			   }						             			
						             			if(ak.depType.contains("nmod:")&&ak.firstWord.equals(aj.secondWord)&&ak.firstTag.equals(aj.secondTag)&&ak.secondTag.contains("VB"))
						             			   { 
						             				 //Rule 2.2*				            				 
						             				 String xx=ak.depType.substring(ak.depType.indexOf(":")+1);
						             				 pw.println(ai.secondWord+ " || "+aj.secondWord+" "+xx+" || "+ak.secondWord+ "		-----From Rule 2.2* ");
						             				 System.out.println(ai.secondWord +" || "+aj.secondWord+" "+xx+" || "+ak.secondWord+ "		-----From Rule 2.2* ");
						             			   }
						             			// ******
						             			// 2.3
						             			if(ak.depType.contains("xcomp")&&ak.firstWord.equals(aj.secondWord)&&ak.firstTag.equals(aj.secondTag)&&ak.secondTag.contains("RB"))
						             			   { 
						             				 //Rule 2.3	
						             				 ElementAccessClass al;      
								             		 for(int l=0; l<myobj.length; l++){
								             			 al=(ElementAccessClass) myobj[l];
								             			 
								             			 if(al.depType.contains("nmod:")&&al.firstWord.equals(ak.secondWord)&&al.firstTag.equals(ak.secondTag)&&al.secondTag.contains("NN"))
								             			   {
								             				 // Rule 2.3
								             				 String xx=al.depType.substring(al.depType.indexOf(":")+1);
								             				 pw.println(ai.secondWord+ " || "+aj.secondWord+" "+ak.secondWord+" "+xx+" || "+al.secondWord+ "		-----From Rule 2.3 ");
								             				 System.out.println(ai.secondWord +" || "+aj.secondWord+" "+ak.secondWord+" "+xx+" || "+al.secondWord+ "		-----From Rule 2.3 ");

								             			   }
								             		 } //end for loop l
						             										             			   }
						             			
						             		 }// end for loop k
				             			   }
				             		   }  // end for loop j
				             		 
			             	}// Rule 2 and its subrules ends here
			             	//******************************
			             	// Rule 3 starts here
			             	  if(ai.depType.contains("nsubj")&&ai.firstTag.contains("VB")&&ai.secondTag.contains("NN")&&(!(ai.secondWord.equals("%")||ai.secondWord.equals("·")))){
				            	  ElementAccessClass aj;     int count=0;
				     				for(int j=0;j<myobj.length;j++){
				     					aj=(ElementAccessClass) myobj[j]; 
				     					if(aj.depType.contains("dobj")&&aj.firstWord.equals(ai.firstWord)&&aj.firstTag.equals(ai.firstTag)&&aj.secondTag.contains("NN")&&(aj.firstIndex==ai.firstIndex)&&(!(aj.secondWord.equals("$")||aj.secondWord.equals("%")))){
				     						//Rule 3
				     						boolean r3_1=false, r3_1_1=false, r3_1_2=false, r3_1_3=false, r3_2=false, r3_3=false, r3_4=false, r3_5=false;
				     						 
				             				boolean  r3=true;
				             				ElementAccessClass ak;     
						     				for(int k=0;k<myobj.length;k++){
						     					ak=(ElementAccessClass) myobj[k]; 
						     					if(ak.depType.contains("nmod:")&&ak.firstWord.equals(aj.secondWord)&&ak.firstTag.equals(aj.secondTag)&&ak.secondTag.contains("NN")){
						     						// Rule 3.1 
						     						 String xx=ak.depType.substring(ak.depType.indexOf(":")+1);
						     						
						             				r3_1=true;
						             				ElementAccessClass al;     //int count=0;
								     				for(int l=0;l<myobj.length;l++){
								     					al=(ElementAccessClass) myobj[l]; 
								     					if(al.depType.contains("nmod:")&&al.firstWord.equals(ai.secondWord)&&al.firstTag.equals(ai.secondTag)&&al.secondTag.contains("NN")){
								     						// Rule 3.1.1
								     						 String y=al.depType.substring(al.depType.indexOf(":")+1);
								     						 pw.println(ai.secondWord+" "+y+" "+al.secondWord+ " || "+ai.firstWord+" || "+ak.firstWord+" "+xx+" "+ak.secondWord+ "		-----From Rule 3.1.1 ");
								             				 System.out.println(ai.secondWord +" "+y+" "+al.secondWord+" || "+ai.firstWord+" || "+ak.firstWord+" "+xx+" "+ak.secondWord+ "		-----From Rule 3.1.1 ");
								             				r3_1_1=true;
								     					}
								     					if(al.depType.contains("nmod:")&&al.firstWord.equals(ak.secondWord)&&al.firstTag.equals(ak.secondTag)&&al.secondTag.contains("NN")){
								     						// Rule 3.1.2
								     						 String y=al.depType.substring(al.depType.indexOf(":")+1);
								     						 pw.println(ai.secondWord+ " || "+ai.firstWord+" || "+ak.firstWord+" "+xx+" "+ak.secondWord+" "+y+" "+ al.secondWord+"		-----From Rule 3.1.2 ");
								             				 System.out.println(ai.secondWord +" || "+ai.firstWord+" || "+ak.firstWord+" "+xx+" "+ak.secondWord+" "+y+" "+ al.secondWord+"		-----From Rule 3.1.2 ");
								             				r3_1_2=true;
								     					}
								     					if(al.depType.contains("appos")&&al.firstWord.equals(ak.secondWord)&&al.firstTag.equals(ak.secondTag)&&al.secondTag.contains("NN")){
								     						// Rule 3.1.3
								     						 pw.println(ai.secondWord+ " || "+ai.firstWord+" "+ak.firstWord+" "+xx+" || "+ al.secondWord+"		-----From Rule 3.1.3 ");
								             				 System.out.println(ai.secondWord +" || "+ai.firstWord+" "+ak.firstWord+" "+xx+" || "+ al.secondWord+"		-----From Rule 3.1.3 ");
								             				r3_1_3=true;
								     					}
								     				}// end for loop l
								     				if(r3_1_1||r3_1_2||r3_1_3)r3_1=false;
								     				if(r3_1){
								     					 pw.println(ai.secondWord+ " || "+ai.firstWord+" || "+ak.firstWord+" "+xx+" "+ak.secondWord+ "		-----From Rule 3.1 ");
							             				 System.out.println(ai.secondWord +" || "+ai.firstWord+" || "+ak.firstWord+" "+xx+" "+ak.secondWord+ "		-----From Rule 3.1 ");
								     				}
						     					}
						     				if(ak.depType.contains("nmod:")&&ak.firstWord.equals(ai.secondWord)&&ak.firstTag.equals(ai.secondTag)&&ak.secondTag.contains("NN")){
						     					// Rule 3.2
						     						String xx=ak.depType.substring(ak.depType.indexOf(":")+1);
						     						pw.println(ai.secondWord+" "+xx+" " +ak.secondWord+" || "+ai.firstWord+" || "+aj.secondWord+ "		-----From Rule 3.2 ");
						             				 System.out.println(ai.secondWord +" "+xx+" "+ak.secondWord+" || "+ai.firstWord+" || "+aj.secondWord+ "		-----From Rule 3.2 ");
						             				r3_2=true;
						     					}
						     				if(ak.depType.contains("nmod:")&&ak.firstWord.equals(ai.secondWord)&&ak.firstTag.equals(ai.secondTag)&&ak.secondTag.contains("NN")){
						     				   
						     					String xx=ak.depType.substring(ak.depType.indexOf(":")+1);
						     					ElementAccessClass al;     
							     				for(int l=0;l<myobj.length;l++){
							     					al=(ElementAccessClass) myobj[l]; 
							     					if(al.depType.contains("nmod:")&&al.firstWord.equals(ak.secondWord)&&al.firstTag.equals(ak.secondTag)&&al.secondTag.contains("NN")){
							     						// Rule 3.3
							     						String y=al.depType.substring(al.depType.indexOf(":")+1);
							     						pw.println(ai.secondWord+" "+xx+" " +ak.secondWord+" "+y+" "+al.secondWord+" || "+ai.firstWord+" || "+aj.secondWord+ "		-----From Rule 3.3 ");
							     						System.out.println(ai.secondWord +" "+xx+" "+ak.secondWord+" "+y+" "+al.secondWord+" || "+ai.firstWord+" || "+aj.secondWord+ "		-----From Rule 3.3 ");
							     						r3_3=true;
							     						}
							     				  	}// end for loop l
						     					}
						     				if(ak.depType.contains("acl")&&ak.firstWord.equals(aj.secondWord)&&ak.firstTag.equals(aj.secondTag)&&ak.secondTag.contains("VB")){
						     					
						     					ElementAccessClass al;     
							     				for(int l=0;l<myobj.length;l++){
							     					al=(ElementAccessClass) myobj[l]; 
							     					if(al.depType.contains("nmod:")&&al.firstWord.equals(ak.secondWord)&&al.firstTag.equals(ak.secondTag)&&al.secondTag.contains("NN")){  						
							     					 
							     						String xx=al.depType.substring(al.depType.indexOf(":")+1);
							     						ElementAccessClass am;     
									     				for(int m=0;m<myobj.length;m++){
									     					am=(ElementAccessClass) myobj[m]; 
									     					if(am.depType.contains("nmod:")&&am.firstWord.equals(al.secondWord)&&am.firstTag.equals(al.secondTag)&&am.secondTag.contains("NN")){  							       
									     						// Rule 3.4
									     						String y=am.depType.substring(am.depType.indexOf(":")+1);
									     						pw.println(ai.secondWord+" "+ai.firstWord+" " +aj.secondWord+" || "+ak.secondWord+" "+ xx+" || "+al.secondWord+ " " +y+" "+am.secondWord+"		-----From Rule 3.4 ");
									     						System.out.println(ai.secondWord +" "+ai.firstWord+" "+aj.secondWord+" || "+ak.secondWord+" "+xx +" || "+al.secondWord+" "+y +" "+am.secondWord+ "		-----From Rule 3.4 ");
									     						r3_4=true;
									     						}
									     					} //end for loop m
									     					
									     					}
							     					
							     				  }  // end for loop l
						     					}
						     				if(ak.depType.contains("appos")&&ak.firstWord.equals(aj.secondWord)&&ak.firstTag.equals(aj.secondTag)&&ak.secondTag.contains("NN")){
						     					// Rule 3.5						     						
						     						pw.println(ai.secondWord+" || "+ai.firstWord+" || "+ak.secondWord+ "		-----From Rule 3.5 ");
						             				 System.out.println(ai.secondWord +" || "+ai.firstWord+" || "+ak.secondWord+ "		-----From Rule 3.5 ");
						             				r3_5=true;
						     					}
						     				
						     				} // end for loop k
						     				if(r3_2||r3_1||r3_3||r3_4||r3_5||r3_1_1||r3_1_2||r3_1_3)r3=false;
						     				if(r3){
						     					 pw.println(ai.secondWord+ " || "+ai.firstWord+" || "+aj.secondWord+ "		-----From Rule 3 ");
					             				 System.out.println(ai.secondWord +" || "+ai.firstWord+" || "+aj.secondWord+ "		-----From Rule 3 ");
						     				}
				     					}
				     				} // end for loop j
			             	  } 
			             	// Rule 3  ends here
			             	//******************************
			             // Rule 4 starts here
			             	 if(ai.depType.contains("nsubj")&&ai.firstTag.contains("VB")&&ai.secondTag.contains("NN")&&(!(ai.secondWord.equals("%")||ai.secondWord.equals("·")))){
				            	  ElementAccessClass aj;    int count=0; 
				     				for(int j=0;j<myobj.length;j++){
				     					aj=(ElementAccessClass) myobj[j]; 
				     					if(aj.depType.contains("nmod:")&&aj.firstWord.equals(ai.firstWord)&&aj.firstTag.equals(ai.firstTag)&&aj.secondTag.contains("NN")&&(!(aj.secondWord.equals("$")||aj.secondWord.equals("%")))){
				     						
				     						//Rule 4
				     						boolean r4_1=false, r4_1_1=false, r4_1_2=false, r4_3=false, r4_2=false, r4_2_1=false, r4_2_2=false, r4_2_3=false, r4_2_3_1=false, r4_2_3_2=false, r4_4=false, r4_5=false, r4_6=false; 
				     						String xx=aj.depType.substring(aj.depType.indexOf(":")+1); if(xx.equals("agent")) xx="by";
				     						 
				     						
				             				 boolean r4=true;
				             				ElementAccessClass ak;     
						     				for(int k=0;k<myobj.length;k++){
						     					ak=(ElementAccessClass) myobj[k]; 
						     					if(ak.depType.contains("nmod:")&&ak.firstWord.equals(aj.secondWord)&&ak.firstTag.equals(aj.secondTag)&&ak.secondTag.contains("NN")){
						     						// Rule 4.1 
						     						 String y=ak.depType.substring(ak.depType.indexOf(":")+1);
						     						 
						             				 r4_1=true;
						             				ElementAccessClass al;     
								     				for(int l=0;l<myobj.length;l++){
								     					al=(ElementAccessClass) myobj[l]; 
								     					if(al.depType.contains("appos")&&al.firstWord.equals(ak.secondWord)&&al.firstTag.equals(ak.secondTag)&&al.secondTag.contains("NN")&&(!(al.secondWord.contains("%")))){
								     						// Rule 4.1.1
								     						 pw.println(ai.secondWord+ " || "+ai.firstWord+" "+xx+" || "+ak.firstWord+" "+y+" "+al.secondWord+ "		-----From Rule 4.1.1 ");
								             				 System.out.println(ai.secondWord +" || "+ai.firstWord+" "+xx+" || "+ak.firstWord+" "+y+" "+al.secondWord+ "		-----From Rule 4.1.1 ");
								             				r4_1_1=true;
								     					}
								     					if(al.depType.contains("nmod:")&&al.firstWord.equals(ak.secondWord)&&al.firstTag.equals(ak.secondTag)&&al.secondTag.contains("NN")){
								     						// Rule 4.1.2
								     						String z=al.depType.substring(al.depType.indexOf(":")+1);
								     						 pw.println(ai.secondWord+ " || "+ai.firstWord+" "+xx+" || "+ak.firstWord+" "+y+" "+al.firstWord+" "+z+" "+al.secondWord+ "		-----From Rule 4.1.2 ");
								             				 System.out.println(ai.secondWord +" || "+ai.firstWord+" "+xx+" || "+ak.firstWord+" "+y+" "+al.firstWord+" "+z+" "+al.secondWord+ "		-----From Rule 4.1.2 ");
								             				r4_1_2=true;
								     					}
								     					
								     				}// end for loop l
						             				if(r4_1_1||r4_1_2)  r4_1=false;
						             				if(r4_1){
						             					pw.println(ai.secondWord+ " || "+ai.firstWord+" "+xx+" || "+ak.firstWord+" "+y+" "+ak.secondWord+ "		-----From Rule 4.1 ");
							             				 System.out.println(ai.secondWord +" || "+ai.firstWord+" "+xx+" || "+ak.firstWord+" "+y+" "+ak.secondWord+ "		-----From Rule 4.1 ");
						             				}
						             				 
						     					}
						     					
						     					 
							     				if(ak.depType.contains("nmod:")&&ak.firstWord.equals(ai.secondWord)&&ak.firstTag.equals(ai.secondTag)&&ak.secondTag.contains("NN")){
							     						// Rule 4.2 
							     					String y=ak.depType.substring(ak.depType.indexOf(":")+1);
							     					
							             		   r4_2=true;
							             		   ElementAccessClass al;     
								     				for(int l=0;l<myobj.length;l++){
								     					al=(ElementAccessClass) myobj[l]; 
								     					if(al.depType.contains("nmod:")&&al.firstWord.equals(ak.secondWord)&&al.firstTag.equals(ak.secondTag)&&al.secondTag.contains("NN")){
								     						// Rule 4.2.1
								     						String z=al.depType.substring(al.depType.indexOf(":")+1);
									     					pw.println(ai.secondWord+" "+y+" "+ak.secondWord+" "+z+" "+al.secondWord+ " || "+ai.firstWord+" "+xx+" || "+aj.secondWord+ "		-----From Rule 4.2.1 ");
									             		    System.out.println(ai.secondWord +" "+y+" "+ak.secondWord+" "+z+" "+al.secondWord+" || "+ai.firstWord+" "+xx+" || "+aj.secondWord+ "		-----From Rule 4.2.1 ");
									             		   r4_2_1=true;
								     					}
								     					if(al.depType.contains("appos")&&al.firstWord.equals(aj.secondWord)&&al.firstTag.equals(aj.secondTag)&&al.secondTag.contains("NN")&&(!(al.secondWord.contains("%")))){
								     						// Rule 4.2.2	     						
									     					pw.println(ai.secondWord+" "+y+" "+ak.secondWord+" || "+ai.firstWord+" "+xx+" || "+al.secondWord+ "		-----From Rule 4.2.2 ");
									             		    System.out.println(ai.secondWord +" "+y+" "+ak.secondWord+" || "+ai.firstWord+" "+xx+" || "+al.secondWord+ "		-----From Rule 4.2.2 ");
									             		   r4_2_2=true;
								     					}
								     					if(al.depType.contains("nmod:")&&al.firstWord.equals(aj.secondWord)&&al.firstTag.equals(aj.secondTag)&&al.secondTag.contains("NN")){
								     						// Rule 4.2.3
								     						String z=al.depType.substring(al.depType.indexOf(":")+1);
									     					
									             		   r4_2_3=true;
									             		   
									             		   ElementAccessClass am;      
										             		 for(int m=0; m<myobj.length; m++){
										             			 am=(ElementAccessClass) myobj[m];
										             			 
										             			 if(am.depType.contains("nmod:")&&am.firstWord.equals(ak.secondWord)&&am.firstTag.equals(ak.secondTag)&&am.secondTag.contains("NN"))
										             			   {
										             				 // Rule 4.2.3.1
										             				String a=am.depType.substring(am.depType.indexOf(":")+1);
										             				pw.println(ai.secondWord+" "+y+" "+ak.secondWord+" "+a+" "+am.secondWord+" || "+ai.firstWord+" "+xx+" || "+al.firstWord+" "+z+" "+al.secondWord+ "		-----From Rule 4.2.3.1 ");
											             		    System.out.println(ai.secondWord +" "+y+" "+ak.secondWord+" "+a+" "+am.secondWord+" || "+ai.firstWord+" "+xx+" || "+al.firstWord+" "+z+" "+al.secondWord+ "		-----From Rule 4.2.3.1 ");
											             		   r4_2_3_1=true;
										             				 
										             			   }
										             			 if(am.depType.contains("conj:and")&&am.secondWord.equals(al.secondWord)&&am.secondTag.equals(al.secondTag)&&am.firstTag.contains("NN"))
										             			   {
										             				 // Rule 4.2.3.2       				
										             				pw.println(ai.secondWord+" "+y+" "+ak.secondWord+" || "+ai.firstWord+" "+xx+" || "+al.firstWord+" "+z+" "+am.firstWord+ " and "+al.secondWord+"		-----From Rule 4.2.3.2 ");
											             		    System.out.println(ai.secondWord +" "+y+" "+ak.secondWord+" || "+ai.firstWord+" "+xx+" || "+al.firstWord+" "+z+" "+am.firstWord+ " and "+al.secondWord+ "		-----From Rule 4.2.3.2 ");
											             		   r4_2_3_2=true;
										             				 
										             			   }
										             		 } // end for loop m
										             		if(r4_2_3_1||r4_2_3_2)r4_2_3=false;
										             		if(r4_2_3){
										             			pw.println(ai.secondWord+" "+y+" "+ak.secondWord+" || "+ai.firstWord+" "+xx+" || "+al.firstWord+" "+z+" "+al.secondWord+ "		-----From Rule 4.2.3 ");
										             		    System.out.println(ai.secondWord +" "+y+" "+ak.secondWord+" || "+ai.firstWord+" "+xx+" || "+al.firstWord+" "+z+" "+al.secondWord+ "		-----From Rule 4.2.3 ");
										             		}
								     					}
								     				}// end for loop l
								     				if(r4_2_1||r4_2_2||r4_2_3||r4_2_3_1||r4_2_3_2)r4_2=false;
								     				if(r4_2){
								     					pw.println(ai.secondWord+" "+y+" "+ak.secondWord+ " || "+ai.firstWord+" "+xx+" || "+aj.secondWord+ "		-----From Rule 4.2 ");
								             		    System.out.println(ai.secondWord +" "+y+" "+ak.secondWord+" || "+ai.firstWord+" "+xx+" || "+aj.secondWord+ "		-----From Rule 4.2 ");
								     				}
							     				}
							     				
							     				if(ak.depType.contains("nmod:in")&&ak.firstWord.equals(ai.firstWord)&&ak.firstTag.equals(ai.firstTag)&&ak.secondTag.contains("NN")&&(!(aj.secondWord.equals(ak.secondWord)||aj.depType.substring(aj.depType.indexOf(":")+1).equals(ak.depType.substring(ak.depType.indexOf(":")+1))))){
						     						// Rule 4.3      					
						     					pw.println(ai.secondWord+ " || "+ai.firstWord+" "+xx+" || "+aj.secondWord+" in " +ak.secondWord+ "		-----From Rule 4.3 ");
						             		    System.out.println(ai.secondWord +" || "+ai.firstWord+" "+xx+" || "+aj.secondWord+ " in " +ak.secondWord+"		-----From Rule 4.3 ");
						             		    r4_3=true;
							     				}
							     				if(ak.depType.contains("conj:and")&&ak.secondWord.equals(aj.secondWord)&&ak.secondTag.equals(aj.secondTag)&&ak.firstTag.contains("NN")){
						     						// Rule 4.4  
							     				count++;
							     				if(count<2){
						     					pw.println(ai.secondWord+ " || "+ai.firstWord+" "+xx+" || "+ak.firstWord+" and " +ak.secondWord+ "		-----From Rule 4.4 ");
						             		    System.out.println(ai.secondWord +" || "+ai.firstWord+" "+xx+" || "+ak.firstWord+ " and " +ak.secondWord+"		-----From Rule 4.4 ");				
						             		       r4_4=true;
							     				}
							     				}
							     				if(ak.depType.contains("nmod:")&&ak.firstWord.equals(ai.secondWord)&&ak.firstTag.equals(ai.secondTag)&&ak.secondTag.contains("NN")){
							     					String y=ak.depType.substring(ak.depType.indexOf(":")+1);
							     					 ElementAccessClass al;     
									     				for(int l=0;l<myobj.length;l++){
									     					al=(ElementAccessClass) myobj[l]; 
									     					if(al.depType.contains("acl")&&al.firstWord.equals(aj.secondWord)&&al.firstTag.equals(aj.secondTag)&&al.secondTag.contains("VB")){									    
											             		  
									     						ElementAccessClass am;      
												             		 for(int m=0; m<myobj.length; m++){
												             			 am=(ElementAccessClass) myobj[m]; 			 
												             			 if(am.depType.contains("nmod:")&&am.firstWord.equals(al.secondWord)&&am.firstTag.equals(al.secondTag)&&am.secondTag.contains("NN"))
												             			   {
												             				 String z=am.depType.substring(am.depType.indexOf(":")+1);
												             				 
												             				ElementAccessClass an;      
														             		 for(int n=0; n<myobj.length; n++){
														             			 an=(ElementAccessClass) myobj[n]; 			 
														             			 if(an.depType.contains("aux")&&an.firstWord.equals(ai.firstWord)&&an.firstTag.equals(ai.firstTag)&&an.secondTag.contains("MD"))
														             			   {
												             				         // Rule 4.5     					
														             				  pw.println(ai.secondWord+ " "+y+" "+ak.secondWord+" || "+an.secondWord+" "+ai.firstWord+" "+xx+" || "+aj.secondWord+" "+al.secondWord+" "+z+" " +am.secondWord+ "		-----From Rule 4.5 ");
														             				  System.out.println(ai.secondWord + " "+y+" "+ak.secondWord+" || "+an.secondWord+" "+ai.firstWord+" "+xx+" || "+aj.secondWord+" "+al.secondWord+" "+z+" " +am.secondWord+"		-----From Rule 4.5 ");
														             				 r4_5=true;
														             			   }
														             		 }
														                 }
												             		 }// end for loop m
									     					}
									     				}// end for loop l
									     		}
							     				
							     				if(ak.depType.contains("acl")&&ak.firstWord.equals(ai.secondWord)&&ak.firstTag.equals(ai.secondTag)&&ak.secondTag.contains("VB")){
							     					
							     					 ElementAccessClass al;     
									     				for(int l=0;l<myobj.length;l++){
									     					al=(ElementAccessClass) myobj[l]; 
									     					if(al.depType.contains("nmod:")&&al.firstWord.equals(ak.secondWord)&&al.firstTag.equals(ak.secondTag)&&al.secondTag.contains("NN")&&(!(al.firstIndex==ai.firstIndex))){									    
									     					// Rule 4.6
									     					
									     						String y=al.depType.substring(al.depType.indexOf(":")+1);
									     						  pw.println(ai.secondWord+ " "+al.firstWord+ " "+y+" "+al.secondWord+" || "+ai.firstWord+" "+xx+" || "+aj.secondWord+ "		-----From Rule 4.6 ");
									             				  System.out.println(ai.secondWord+ " "+al.firstWord+" "+y+" "+al.secondWord+" || "+ai.firstWord+" "+xx+" || "+aj.secondWord+"		-----From Rule 4.6 ");
									             				  r4_6=true;
									     						
									     					}
									     				}
							     				}
							     				
						     				}// end for loop k
						     				if(r4_1||r4_1_2||r4_1_1||r4_2||r4_3||r4_5||r4_6||r4_2_2) r4=false;
						     				if(r4){
						     					 pw.println(ai.secondWord+ " || "+ai.firstWord+" "+xx+" || "+aj.secondWord+ "		-----From Rule 4 ");
					             				 System.out.println(ai.secondWord +" || "+ai.firstWord+" "+xx+" || "+aj.secondWord+ "		-----From Rule 4 ");
						     				}
						     			
						     				
				     					}
				     				}  // end for loop j
			             	 }
			             	  
			             // Rule 4  ends here
			             	//******************************
			             // Rule 5 starts here
			             	 if(ai.depType.contains("nsubj")&&ai.firstTag.contains("NN")&&ai.secondTag.contains("NN")&&(!(ai.secondWord.equals("%")||ai.secondWord.equals("·")))){
				            	  ElementAccessClass aj;     
				     				for(int j=0;j<myobj.length;j++){
				     					aj=(ElementAccessClass) myobj[j]; 
				     					if(aj.depType.contains("nmod:")&&aj.firstWord.equals(ai.firstWord)&&aj.firstTag.equals(ai.firstTag)&&aj.secondTag.contains("NN")&&(!(aj.secondWord.equals("$")||aj.secondWord.equals("%")))){
				     						//Rule 5
				     						boolean r5=false, r5_1=false, r5_1_1=false, r5_1_2=false, r5_2=false, r5_2_1=false, r5_2_2=false, r5_3=false, r5_4=false, r5_5=false , r5_6=false, r5_7=false;
				     						String xx=aj.depType.substring(aj.depType.indexOf(":")+1);
				     						
				             				 r5=true;
				             				
				             				ElementAccessClass ak;     
						     				for(int k=0;k<myobj.length;k++){
						     					ak=(ElementAccessClass) myobj[k]; 
						     					if(ak.depType.contains("nmod:")&&ak.firstWord.equals(aj.secondWord)&&ak.firstTag.equals(aj.secondTag)&&ak.secondTag.contains("NN")){
						     						// Rule 5.1 
						     						 String y=ak.depType.substring(ak.depType.indexOf(":")+1);
						     						 
						             				 r5_1=true;
						             				ElementAccessClass al;     
								     				for(int l=0;l<myobj.length;l++){
								     					al=(ElementAccessClass) myobj[l]; 
								     					if(al.depType.contains("appos")&&al.firstWord.equals(ak.secondWord)&&al.firstTag.equals(ak.secondTag)&&al.secondTag.contains("NN")){
								     						// Rule 5.1.1
								     						 pw.println(ai.secondWord+ " || "+ai.firstWord+" "+xx+" || "+ak.firstWord+" "+y+" "+al.secondWord+ "		-----From Rule 5.1.1 ");
								             				 System.out.println(ai.secondWord +" || "+ai.firstWord+" "+xx+" || "+ak.firstWord+" "+y+" "+al.secondWord+ "		-----From Rule 5.1.1 ");
								             				r5_1_1=true;
								     					}
								     					if(al.depType.contains("nmod:")&&al.firstWord.equals(ak.secondWord)&&al.firstTag.equals(ak.secondTag)&&al.secondTag.contains("NN")){
								     						// Rule 5.1.2
								     						String z=al.depType.substring(al.depType.indexOf(":")+1);
								     						 pw.println(ai.secondWord+ " || "+ai.firstWord+" "+xx+" || "+ak.firstWord+" "+y+" "+al.firstWord+" "+z+" "+al.secondWord+ "		-----From Rule 5.1.2 ");
								             				 System.out.println(ai.secondWord +" || "+ai.firstWord+" "+xx+" || "+ak.firstWord+" "+y+" "+al.firstWord+" "+z+" "+al.secondWord+ "		-----From Rule 5.1.2 ");
								             				r5_1_2=true;
								     					}
								     					
								     				}// end for loop l
						             				 
						             			  if(r5_1_1||r5_1_2)r5_1=false;
						             			  if(r5_1){
						             				 pw.println(ai.secondWord+ " || "+ai.firstWord+" "+xx+" || "+ak.firstWord+" "+y+" "+ak.secondWord+ "		-----From Rule 5.1 ");
						             				 System.out.println(ai.secondWord +" || "+ai.firstWord+" "+xx+" || "+ak.firstWord+" "+y+" "+ak.secondWord+ "		-----From Rule 5.1 "); 
						             			  }
						     					}
						     					
						     					 
							     				if(ak.depType.contains("nmod:")&&ak.firstWord.equals(ai.secondWord)&&ak.firstTag.equals(ai.secondTag)&&ak.secondTag.contains("NN")){
							     						// Rule 5.2 
							     					String y=ak.depType.substring(ak.depType.indexOf(":")+1);
							     					
							     					
							     					r5_2=true;
							             		   ElementAccessClass al;     
								     				for(int l=0;l<myobj.length;l++){
								     					al=(ElementAccessClass) myobj[l]; 
								     					if(al.depType.contains("nmod:")&&al.firstWord.equals(ak.secondWord)&&al.firstTag.equals(ak.secondTag)&&al.secondTag.contains("NN")){
								     						// Rule 5.2.1
								     						String z=al.depType.substring(al.depType.indexOf(":")+1);
									     					pw.println(ai.secondWord+" "+y+" "+ak.secondWord+" "+z+" "+al.secondWord+ " || "+ai.firstWord+" "+xx+" || "+aj.secondWord+ "		-----From Rule 5.2.1 ");
									             		    System.out.println(ai.secondWord +" "+y+" "+ak.secondWord+" "+z+" "+al.secondWord+" || "+ai.firstWord+" "+xx+" || "+aj.secondWord+ "		-----From Rule 5.2.1 ");
									             		   r5_2_1=true;
								     					}
								     					if(al.depType.contains("appos")&&al.firstWord.equals(aj.secondWord)&&al.firstTag.equals(aj.secondTag)&&al.secondTag.contains("NN")){
								     						// Rule 5.2.2	     						
									     					pw.println(ai.secondWord+" "+y+" "+ak.secondWord+" || "+ai.firstWord+" "+xx+" || "+al.secondWord+ "		-----From Rule 5.2.2 ");
									             		    System.out.println(ai.secondWord +" "+y+" "+ak.secondWord+" || "+ai.firstWord+" "+xx+" || "+al.secondWord+ "		-----From Rule 5.2.2 ");
									             		   r5_2_2=true;
								     					}
								     					
								     				}// end for loop l
								     				if(r5_2_1||r5_2_2)r5_2=false;
								     				if(r5_2){
								     					pw.println(ai.secondWord+" "+y+" "+ak.secondWord+ " || "+ai.firstWord+" "+xx+" || "+aj.secondWord+ "		-----From Rule 5.2 ");
								             		    System.out.println(ai.secondWord +" "+y+" "+ak.secondWord+" || "+ai.firstWord+" "+xx+" || "+aj.secondWord+ "		-----From Rule 5.2 ");
								     				}
								     				
							     				}
							     				
							     				if(ak.depType.contains("nmod:in")&&ak.firstWord.equals(ai.firstWord)&&ak.firstTag.equals(ai.firstTag)&&ak.secondTag.contains("NN")&&(!(aj.secondWord.equals(ak.secondWord)))){
						     						// Rule 5.3 
							     				String y=ak.depType.substring(ak.depType.indexOf(":")+1);
						     					pw.println(ai.secondWord+ " || "+ai.firstWord+" "+xx+" || "+aj.secondWord+" "+ y +" " +ak.secondWord+ "		-----From Rule 5.3 ");
						             		    System.out.println(ai.secondWord +" || "+ai.firstWord+" "+xx+" || "+aj.secondWord+ " "+y+" " +ak.secondWord+"		-----From Rule 5.3 ");
						             		    r5_3=true;
							     				}	
							     				if(ak.depType.contains("conj:and")&&ak.secondWord.equals(aj.secondWord)&&ak.secondTag.equals(aj.secondTag)&&ak.firstTag.contains("NN")){
						     						// Rule 5.4     					
						     					pw.println(ai.secondWord+ " || "+ai.firstWord+" "+xx+" || "+ak.firstWord+" and " +ak.secondWord+ "		-----From Rule 5.4 ");
						             		    System.out.println(ai.secondWord +" || "+ai.firstWord+" "+xx+" || "+ak.firstWord+ " and " +ak.secondWord+"		-----From Rule 5.4 ");
						             		   r5_4=true;
							     				}
							     				if(ak.depType.contains("nmod:")&&ak.firstWord.equals(ai.secondWord)&&ak.firstTag.equals(ai.secondTag)&&ak.secondTag.contains("NN")){
							     					String y=ak.depType.substring(ak.depType.indexOf(":")+1);
							     					 ElementAccessClass al;     
									     				for(int l=0;l<myobj.length;l++){
									     					al=(ElementAccessClass) myobj[l]; 
									     					if(al.depType.contains("acl")&&al.firstWord.equals(aj.secondWord)&&al.firstTag.equals(aj.secondTag)&&al.secondTag.contains("VB")){									    
											             		  
									     						ElementAccessClass am;      
												             		 for(int m=0; m<myobj.length; m++){
												             			 am=(ElementAccessClass) myobj[m]; 			 
												             			 if(am.depType.contains("nmod:")&&am.firstWord.equals(al.secondWord)&&am.firstTag.equals(al.secondTag)&&am.secondTag.contains("NN"))
												             			   {
												             				 String z=am.depType.substring(am.depType.indexOf(":")+1);
												             				 
												             				ElementAccessClass an;      
														             		 for(int n=0; n<myobj.length; n++){
														             			 an=(ElementAccessClass) myobj[n]; 			 
														             			 if(an.depType.contains("aux")&&an.firstWord.equals(ai.firstWord)&&an.firstTag.equals(ai.firstTag)&&an.secondTag.contains("MD"))
														             			   {
												             				         // Rule 5.5     					
														             				  pw.println(ai.secondWord+ " "+y+" "+ak.secondWord+" || "+an.secondWord+" "+ai.firstWord+" "+xx+" || "+aj.secondWord+" "+al.secondWord+" "+z+" " +am.secondWord+ "		-----From Rule 5.5 ");
														             				  System.out.println(ai.secondWord + " "+y+" "+ak.secondWord+" || "+an.secondWord+" "+ai.firstWord+" "+xx+" || "+aj.secondWord+" "+al.secondWord+" "+z+" " +am.secondWord+"		-----From Rule 5.5 ");
														             				 r5_5=true;
														             			   }
														             		 }
														                 }
												             		 }// end for loop m
									     					}
									     				}// end for loop l
									     		}
							     				
							     				if(ak.depType.contains("acl")&&ak.firstWord.equals(aj.secondWord)&&ak.firstTag.equals(aj.secondTag)&&ak.secondTag.contains("VB")){
							     					
							     					 ElementAccessClass al;     
									     				for(int l=0;l<myobj.length;l++){
									     					al=(ElementAccessClass) myobj[l]; 
									     					
									     					if(al.depType.contains("nmod:")&&al.firstWord.equals(ak.secondWord)&&al.firstTag.equals(ak.secondTag)&&al.secondTag.contains("NN")){									    
									     					// Rule 5.6			     					
									     						 String y=al.depType.substring(al.depType.indexOf(":")+1);
									             				  pw.println(ai.secondWord+ " || "+al.firstWord+" "+y+" || "+al.secondWord+ "		-----From Rule 5.6 ");
									             				  System.out.println(ai.secondWord +" || "+al.firstWord+" "+y+" || "+al.secondWord+"		-----From Rule 5.6 ");
									             				 
									             				 r5_6=true;
									     						
									     					}
									     				}
							     				}
							     				if(ak.depType.contains("neg")&&ak.firstWord.equals(ai.firstWord)&&ak.firstTag.equals(ai.firstTag)&&ak.secondTag.contains("RB")){
							     					//Rule 5.7
							     					  pw.println(ai.secondWord+" || "+ak.secondWord+" "+ai.firstWord+" "+xx+" || "+aj.secondWord+ "		-----From Rule 5.7 ");
						             				  System.out.println(ai.secondWord +" || "+ak.secondWord+" "+ai.firstWord+" "+xx+" || "+aj.secondWord+"		-----From Rule 5.7 ");
						             				 r5_7=true;
							     				}	
							     				
							     				
						     				}// end for loop k
						     				if(r5_1||r5_2||r5_3||r5_4||r5_5||r5_6||r5_7) r5=false;
						     				if(r5){
						     					 pw.println(ai.secondWord+ " || "+ai.firstWord+" "+xx+" || "+aj.secondWord+ "		-----From Rule 5 ");
					             				 System.out.println(ai.secondWord +" || "+ai.firstWord+" "+xx+" || "+aj.secondWord+ "		-----From Rule 5 ");
						     				}
				     					}
				     				}  // end for loop j
			             	 }
			             // Rule 5  ends here
			             	//******************************
			             // Rule 6 starts here
			             	if(ai.depType.contains("nsubj")&&ai.firstTag.contains("JJ")&&ai.secondTag.contains("NN")&&(!(ai.secondWord.equals("%")||ai.secondWord.equals("·")))){
				            	  ElementAccessClass aj;     
				     				for(int j=0;j<myobj.length;j++){
				     					aj=(ElementAccessClass) myobj[j]; 
				     					if(aj.depType.contains("nmod:")&&aj.firstWord.equals(ai.firstWord)&&aj.firstTag.equals(ai.firstTag)&&aj.secondTag.contains("NN")&&(!(aj.secondWord.equals("$")||aj.secondWord.equals("%")))){
				     						//Rule 6
				     						boolean r6_1=false, r6_1_1=false, r6_1_2=false,  r6_2=false;
				     						String xx=aj.depType.substring(aj.depType.indexOf(":")+1);				
				     						// pw.println(ai.secondWord+ " || "+ai.firstWord+" "+xx+" || "+aj.secondWord+ "		-----From Rule 6 ");
				             				// System.out.println(ai.secondWord +" || "+ai.firstWord+" "+xx+" || "+aj.secondWord+ "		-----From Rule 6 ");
				     						ElementAccessClass ak;     
						     				for(int k=0;k<myobj.length;k++){
						     					ak=(ElementAccessClass) myobj[k]; 
						     					if(ak.depType.contains("cop")&&ak.firstWord.equals(ai.firstWord)&&ak.firstTag.equals(ai.firstTag)&&ak.secondTag.contains("VB")){
						     						// Rule 6.1 
						     						
						     						
						             				r6_1=true;
						             				 ElementAccessClass al;     
									     				for(int l=0;l<myobj.length;l++){
									     					al=(ElementAccessClass) myobj[l]; 
									     					if(al.depType.contains("nmod")&&al.firstWord.equals(ai.secondWord)&&al.firstTag.equals(ai.secondTag)&&al.secondTag.contains("NN")){	
									     						 String y=al.depType.substring(al.depType.indexOf(":")+1);
									     						 pw.println(ai.secondWord+" " +y+" "+al.secondWord+" || "+ak.secondWord+" "+ai.firstWord+" "+xx+" || "+aj.secondWord+ "		-----From Rule 6.1.1 ");
									             				 System.out.println(ai.secondWord +" " +y+" "+al.secondWord+" || "+ak.secondWord+" "+ai.firstWord+" "+xx+" || "+aj.secondWord+ "		-----From Rule 6.1.1 ");
									             				r6_1_1=true;
									     					}
									     					if(al.depType.contains("nmod")&&al.firstWord.equals(aj.secondWord)&&al.firstTag.equals(aj.secondTag)&&al.secondTag.contains("NN")){	
									     						 String y=al.depType.substring(al.depType.indexOf(":")+1);
									     						 pw.println(ai.secondWord+" || "+ak.secondWord+" "+ai.firstWord+" "+xx+" || "+aj.secondWord+ " " +y+" "+al.secondWord+"		-----From Rule 6.1.2 ");
									             				 System.out.println(ai.secondWord +" || "+ak.secondWord+" "+ai.firstWord+" "+xx+" || "+aj.secondWord+ " " +y+" "+al.secondWord+"		-----From Rule 6.1.2 ");
									             				r6_1_2=true;
									     					}
									     				}// end for loop l
									     			if(r6_1_1||r6_1_2)r6_1=false;
									     			if(r6_1){
									     				 pw.println(ai.secondWord+ " || "+ak.secondWord+" "+ai.firstWord+" "+xx+" || "+aj.secondWord+ "		-----From Rule 6.1 ");
							             				 System.out.println(ai.secondWord +" || "+ak.secondWord+" "+ai.firstWord+" "+xx+" || "+aj.secondWord+ "		-----From Rule 6.1 ");
									     			}
						     					}
						     					if(ak.depType.contains("nmod:")&&ak.firstWord.equals(ai.secondWord)&&ak.firstTag.equals(ai.secondTag)&&ak.secondTag.contains("NN")){
						     						// Rule 6.2
						     						 String y=ak.depType.substring(ak.depType.indexOf(":")+1);
						     						 pw.println(ai.secondWord+ " "+y+" "+ak.secondWord+" || "+ai.firstWord+" "+xx+" || "+aj.secondWord+ "		-----From Rule 6.2 ");
						             				 System.out.println(ai.secondWord +" "+y+" "+ak.secondWord+" || "+ai.firstWord+" "+xx+" || "+aj.secondWord+ "		-----From Rule 6.2 ");
						     					}
						     				}// end for loop k
				     					}
				     					
				     				}// end for loop j
				     			}
			             	 
			             	// Rule 6  ends here
			             	//******************************
			                // Rule 7 starts here
			             	  if(ai.depType.equals("acl")&&ai.firstTag.contains("NN")&&ai.secondTag.contains("VB")){  
		            			  ElementAccessClass aj;
		     				      for(int j=0;j<myobj.length;j++){
		     				    	 aj=(ElementAccessClass) myobj[j];
		     					       if(aj.depType.contains("nmod:")&&aj.firstWord.equals(ai.secondWord)&&aj.firstTag.equals(ai.secondTag)&&aj.secondTag.contains("NN")&&(!((aj.firstWord.equals("followed")&&aj.depType.substring(aj.depType.indexOf(":")+1).equals("by"))||aj.secondWord.equals(" ±")||aj.firstWord.equals(">")))){
		     					    	  //Rule 7
		     					    	   boolean r7=false, r7_1=false;
		     					    	   String xx= aj.depType.substring(aj.depType.indexOf(":")+1) ;
		     					    	 
		     					    	  r7=true;
		     					    	  ElementAccessClass ak;
				     				      for(int k=0;k<myobj.length;k++){
				     				    	 ak=(ElementAccessClass) myobj[k];  int count=0;
				     				    	 if(ak.depType.contains("nmod:")&&ak.firstWord.equals(aj.secondWord)&&ak.firstTag.equals(aj.secondTag)&&ak.secondTag.contains("NN")&&(!(ak.secondWord.equals("%")))){
				     				    		//Rule 7.1
				     				    		String y= ak.depType.substring(ak.depType.indexOf(":")+1) ;
				     					        pw.println(ai.firstWord+" || "+ai.secondWord+" "+xx+" || "+aj.secondWord+" "+y+" "+ak.secondWord+"		----Rule Tree 7.1");
				     					       System.out.println(ai.firstWord+" || "+ai.secondWord+" "+xx+" || "+aj.secondWord+" "+y+" "+ak.secondWord+"		----Rule Tree 7.1");
				     					       r7_1=true;
				     				    	 }
				     				    	
				     				    
		     					       } // end for loop k
				     				  if(r7_1)r7=false;  
				     				  if(r7){
				     					 pw.println(ai.firstWord+" || "+ai.secondWord+" "+xx+" || "+aj.secondWord+"		----Rule 7");
		     					    	  System.out.println(ai.firstWord+" || "+ai.secondWord+" "+xx+" || "+aj.secondWord+"		----Rule 7");
				     				  }
		     				      }
			             	  }// end for loop j
			             	 }    	 	

			             	// Rule 7  ends here
			             	//******************************
			             	// Rule 8 starts here
			             	 if(ai.depType.equals("acl")&&ai.firstTag.contains("NN")&&ai.secondTag.contains("VB")){  
		            			  ElementAccessClass aj;
		     				      for(int j=0;j<myobj.length;j++){
		     				    	 aj=(ElementAccessClass) myobj[j];
		     					       if(aj.depType.contains("dobj")&&aj.firstWord.equals(ai.secondWord)&&aj.firstTag.equals(ai.secondTag)&&aj.secondTag.contains("NN")&&(!((aj.firstWord.equals("followed")&&aj.depType.substring(aj.depType.indexOf(":")+1).equals("by"))||aj.secondWord.equals(" ±")||aj.firstWord.equals(">")))){
		     					    	  //Rule 8
		     					    	  boolean r8=false, r8_1=false;
		     					    	 
		     					    	  r8=true;
		     					    	  ElementAccessClass ak;
				     				      for(int k=0;k<myobj.length;k++){
				     				    	 ak=(ElementAccessClass) myobj[k];  int count=0;
				     				    	 if(ak.depType.contains("nmod:")&&ak.firstWord.equals(aj.secondWord)&&ak.firstTag.equals(aj.secondTag)&&ak.secondTag.contains("NN")){
				     				    		//Rule 8.1
				     				    		String xx= ak.depType.substring(ak.depType.indexOf(":")+1) ;
				     					        pw.println(ai.firstWord+" || "+ai.secondWord+" || "+aj.secondWord+" "+xx+" "+ak.secondWord+"		----Rule Tree 8.1");
				     					       System.out.println(ai.firstWord+" || "+ai.secondWord+" || "+aj.secondWord+" "+xx+" "+ak.secondWord+"		----Rule Tree 8.1");
				     					      r8_1=true;
				     				    	 }
				     				    
		     					       } // end for loop k
				     				  if(r8_1)r8=false;
				     				  if(r8){
				     					 pw.println(ai.firstWord+" || "+ai.secondWord+" || "+aj.secondWord+"		----Rule 8");
		     					    	 System.out.println(ai.firstWord+" || "+ai.secondWord+" || "+aj.secondWord+"		----Rule 8"); 
				     				  }
				     				      
		     				      }
			             	  }// end for loop j
			             	 }    	 	

			             	// Rule 8  ends here
			             	//******************************
			             // Rule 9 starts here
			             	if(ai.depType.equals("appos")&&ai.firstTag.contains("NN")&&ai.secondTag.contains("NN")){  
		            			  ElementAccessClass aj;
		     				      for(int j=0;j<myobj.length;j++){
		     				    	 aj=(ElementAccessClass) myobj[j]; 
		     				    	 if(aj.depType.equals("acl")&&aj.firstWord.equals(ai.secondWord)&&aj.firstTag.equals(ai.secondTag)&&aj.secondTag.contains("VB")){  
				            			  ElementAccessClass ak;
				     				      for(int k=0;k<myobj.length;k++){
				     				    	 ak=(ElementAccessClass) myobj[k];
				     					       if(ak.depType.contains("nmod:")&&ak.firstWord.equals(aj.secondWord)&&ak.firstTag.equals(aj.secondTag)&&ak.secondTag.contains("NN")){
				     					    	  //Rule 9
				     					    	   boolean r9=false,r9_1=false;
				     					    	   String xx= ak.depType.substring(ak.depType.indexOf(":")+1) ;
				     					    	   
				     					    	  r9=true;
				     					    	  ElementAccessClass al;
						     				      for(int l=0;l<myobj.length;l++){
						     				    	 al=(ElementAccessClass) myobj[l]; 
						     				    	 if(al.depType.contains("nmod:")&&al.firstWord.equals(ak.secondWord)&&al.firstTag.equals(ak.secondTag)&&al.secondTag.contains("NN")){
						     				    		//Rule 9.1
						     				    		String y= al.depType.substring(al.depType.indexOf(":")+1) ;
						     					        pw.println(ai.firstWord+" || "+aj.secondWord+" "+xx+" || "+ak.secondWord+" "+y+" "+al.secondWord+"		----Rule 9.1");
						     					       System.out.println(ai.firstWord+" || "+aj.secondWord+" "+xx+" || "+ak.secondWord+" "+y+" "+al.secondWord+"		----Rule 9.1");
						     					       r9_1=true;
						     				    	 }
						     				    	
						     				    	 
						     				    	 
						     				    
				     					       } // end for loop k
						     				    if(r9_1)r9=false;
					     				    	if(r9){
					     				    		pw.println(ai.firstWord+" || "+aj.secondWord+" "+xx+" || "+ak.secondWord+"		----Rule 9");
					     					    	System.out.println(ai.firstWord+" || "+aj.secondWord+" "+xx+" || "+ak.secondWord+"		----Rule 9"); 
					     				    	}
				     				      }
					             	  }// end for loop j
					             	 }    	 	

		     					       
		     				      }
		     				    }
			             	// Rule 9  ends here
			             	//******************************
			             	 // Rule 10 starts here
			             	 if(ai.depType.equals("nummod")&&ai.firstTag.contains("NN")&&ai.secondTag.contains("CD")){
					            	ElementAccessClass aj;
					            	for(int j=0;j<myobj.length;j++){
					            		aj=(ElementAccessClass) myobj[j];
					            		 if(aj.depType.contains("nmod:")&&aj.firstWord.equals(ai.firstWord)&&aj.firstTag.equals(ai.firstTag)&&aj.secondTag.contains("NN")){      			
					            			 String xx= aj.depType.substring(aj.depType.indexOf(":")+1) ;
					            			 ElementAccessClass ak;
					            			 for(int k=0;k<myobj.length-1;k++){	            				 
					            				 ak=(ElementAccessClass) myobj[k];
					            				 if(ak.depType.contains("nsubj")&&ak.secondWord.equals(ai.firstWord)&&ak.secondTag.equals(ai.firstTag)&&ak.firstTag.contains("VB")){
					            					
					            					 ElementAccessClass al;
					            					 for(int l=0;l<myobj.length-1; l++){
					             						 al=(ElementAccessClass) myobj[l];
					             						 if(al.depType.contains("nmod:")&&al.firstWord.equals(ak.firstWord)&&al.firstTag.equals(ak.firstTag)&&al.secondTag.contains("NN")){
					             							 //Rule 10
					             							 
					             							System.out.println(ai.secondWord+" "+ai.firstWord+" "+xx+" "+aj.secondWord+" || "+ak.firstWord+" || "+al.secondWord+"	----Rule 10");
					             							pw.println(ai.secondWord+" "+ai.firstWord+" "+xx+" "+" "+aj.secondWord+" || "+ak.firstWord+" || "+al.secondWord+"	----Rule 10");
					             						 }
					             							
					             						 }// end for loop j
					            				 }
					            			   }// end for loop k
					            		     }
					            		   }// end for loop j
					            		}
					            		
					                                                                                                                                                                                                              
			             	
			             	// Rule 10  ends here
			             	//******************************
			             	 // Rule 11 starts here
			             	 if(ai.depType.equals("dobj")&&ai.firstTag.contains("VB")&&ai.secondTag.contains("NN")&&(!(ai.secondWord.equals("case")||ai.secondWord.equals("%")))){
			             		 
					            	ElementAccessClass aj;
					            	for(int j=0;j<myobj.length;j++){
					            		aj=(ElementAccessClass) myobj[j];					            		
					            		if(aj.depType.contains("nmod:")&&aj.firstWord.equals(ai.secondWord)&&aj.firstTag.equals(ai.secondTag)&&(aj.firstIndex==ai.secondIndex)&&aj.secondTag.contains("NN")){
					            			ElementAccessClass ak;
					            			 String xx= aj.depType.substring(aj.depType.indexOf(":")+1) ;
							            	for(int k=0;k<myobj.length-1;k++){
							            		ak=(ElementAccessClass) myobj[k];
							            		
							            		if(ak.depType.contains("nmod:")&&ak.firstWord.equals(ai.firstWord)&&ak.firstTag.equals(ai.firstTag)&&(ak.firstIndex==ai.firstIndex)&&ak.secondTag.contains("NN")){
							            			 String y= ak.depType.substring(ak.depType.indexOf(":")+1) ;
							            			ElementAccessClass al;
									            	for(int l=0;l<myobj.length-1;l++){
									            		al=(ElementAccessClass) myobj[l];	
									            		if(al.depType.contains("acl")&&al.firstTag.equals(ak.secondTag)&&al.firstWord.equals(ak.secondWord)&&(al.firstIndex==ak.secondIndex)&&al.secondTag.contains("VB")){
									            			ElementAccessClass am;
									            			
											            	for(int m=0;m<myobj.length-1;m++){
											            		am=(ElementAccessClass) myobj[m];					            		
											            		if(am.depType.contains("nmod:")&&am.firstWord.equals(al.secondWord)&&am.firstTag.equals(al.secondTag)&&(am.firstIndex==al.secondIndex)&&am.secondTag.contains("NN")){
											            			 String z= am.depType.substring(am.depType.indexOf(":")+1) ;
											            			System.out.println(ai.secondWord+" "+xx+" "+aj.secondWord+" || "+am.firstWord+" "+z+" || "+am.secondWord+"		---Rule 11");
											            			pw.println(ai.secondWord+" "+xx+" "+aj.secondWord+" || "+am.firstWord+" "+z+" || "+am.secondWord+"		---Rule 11");

											            		}
											            		}
									            		}
									            		}
									            		
							            			
							            		}
							            		}

					            		}
					            		
					            	}
					            	 
					             }
			             	 
			             	// Rule 11  ends here
			             	//******************************
			             	// Rule 12 starts here
			             	 if(ai.depType.equals("nsubj")&&ai.secondTag.contains("NN")){
					            	ElementAccessClass aj;
					            	for(int j=i+1;j<myobj.length;j++){
					            		aj=(ElementAccessClass) myobj[j];
					            		if(aj.depType.equals("advcl")&&aj.secondWord.equals(ai.firstWord)&&(aj.secondIndex==ai.firstIndex)&&aj.firstTag.contains("VB")){
					            			
					            			ElementAccessClass ak;
							            	for(int k=j+1;k<myobj.length;k++){
							            		ak=(ElementAccessClass) myobj[k];
							            		if(ak.depType.equals("aux")&&ak.firstWord.equals(aj.firstWord)&&(ak.firstIndex==aj.firstIndex)&&ak.secondTag.contains("MD")){
							            			
							            			ElementAccessClass al;
									            	for(int l=k+1;l<myobj.length;l++){
									            		al=(ElementAccessClass) myobj[l];
									            		if(al.depType.contains("nmod:as")&&al.firstWord.equals(ak.firstWord)&&(al.firstIndex==ak.firstIndex)){
									            			
									            			ElementAccessClass am;
											            	for(int m=l+1;m<myobj.length;m++){
											            		am=(ElementAccessClass) myobj[m];
											            		if(am.depType.contains("nmod:of")&&am.firstWord.equals(al.secondWord)&&(am.firstIndex==al.secondIndex)){
											            			System.out.println(ai.secondWord+" || "+ak.secondWord+" "+ aj.firstWord+" as || "+am.firstWord+" of "+am.secondWord +" ---Rule Tree 12");
											            			pw.println(ai.secondWord+" || "+ak.secondWord+" "+ aj.firstWord+" as || "+am.firstWord+" of "+am.secondWord +" ---Rule Tree 12");

											            		}
											            		}
									            		}
									            		}
							            		}
							            		}
					            		}
					            		}
					            	
					            }

			             	// Rule 12  ends here
			             	//******************************
			             	 // Rule 13 starts here
			             	 if(ai.depType.equals("nsubj")&&ai.firstTag.contains("NN")&&ai.secondTag.contains("NN")){
					            	ElementAccessClass aj;
					            	for(int j=0;j<myobj.length;j++){
					            		aj=(ElementAccessClass) myobj[j];
					            		if(aj.depType.equals("xcomp")&&aj.firstWord.equals(ai.firstWord)&&(aj.firstIndex==ai.firstIndex)&&aj.secondTag.contains("VB")){
					            			ElementAccessClass ak;
							            	for(int k=0;k<myobj.length;k++){
							            		ak=(ElementAccessClass) myobj[k];
							            		if(ak.depType.equals("mark")&&ak.firstWord.equals(aj.secondWord)&&(ak.firstIndex==aj.secondIndex)&&ak.secondTag.contains("TO")){
							            			 ElementAccessClass al;			
						            				  for(int l=0; l<myobj.length;l++){
						            					  al=(ElementAccessClass) myobj[l]; 						            					  
								     				    	 if(al.depType.contains("dobj")&&al.firstWord.equals(aj.secondWord)&&al.firstTag.equals(aj.secondTag)&&(al.firstIndex==aj.secondIndex)&&al.secondTag.contains("NN")){
								     				    		 ElementAccessClass am; 		
								     				    		 for(int m=0;m<myobj.length;m++){
								     				    			 am=(ElementAccessClass) myobj[m];
								     				    			
								     				    		 if(am.depType.contains("nmod:")&&am.firstWord.equals(al.secondWord)&&am.firstTag.equals(al.secondTag)&&am.secondTag.contains("NN")){
								     				    			 String xx=am.depType.substring(am.depType.indexOf(":")+1);
								     				    			 ElementAccessClass an;
								     				    			 for(int n=0;n<myobj.length;n++){
								     				    				   an=(ElementAccessClass) myobj[n];  
								     				    				   if(an.depType.contains("nmod:")&&an.firstWord.equals(am.secondWord)&&an.firstTag.equals(am.secondTag)&&an.secondTag.contains("NN")){
								     				    					  String y=an.depType.substring(an.depType.indexOf(":")+1);
						             								          System.out.println(al.secondWord+"_"+al.firstWord+" "+ak.secondWord+" "+aj.secondWord+" "+al.secondWord+" || "+xx+"_"+am.secondWord+"_"+y+" || "+an.secondWord+"		----Rule: 13");
						             								           pw.println(al.secondWord+"_"+al.firstWord+" "+ak.secondWord+" "+aj.secondWord+" "+al.secondWord+" || "+xx+"_"+am.secondWord+"_"+y+" || "+an.secondWord+"		----Rule: 13");
								     				    				   }
								     				    			 }
								     				    		 }
								     				    		 }
								     				    	 }
						            				   	 
							            		} // end for loop l
							            	}
					            		}  // end for loop k
					            	}
			             	   }// end for loop j
			             	 }
			             	// Rule 13  ends here
			             	//******************************
			             			              
			             }   // end for loop i
				    
				    pw.println();
				   depNewRl.clear();
				   singleWordAr.clear();  
				   taggedDep.clear();
				   lhsfinal.clear();
					  }
				   			 
				} //if line length is non zero
				
		}  // while 
    pw.println("-------------------------------\n");
	br.close();  
	pw.close();
					
	
						
			} // List of files
					
	  System.out.println("Total sentence processesssed "+e);
	  System.out.println("Executiom COMPLETED ");
	} // main
	
}// class 
