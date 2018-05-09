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
				
				       File folder = new File("C:\\Users\\ASLAM\\Desktop\\Revision_JBI\\Final List\\Sentences_RuleWise\\11b\\");
				       
						File[] listOfFiles = folder.listFiles();
						System.out.println(listOfFiles.length);
						boolean flagFileName=false;
						for(File file:listOfFiles){
								
							FileReader fr=new FileReader(file);
							String name=file.getName();
							System.out.println(name);
							
					
				    BufferedReader br=new BufferedReader(fr);				  
				    FileWriter filewriter=new FileWriter("C:\\Users\\ASLAM\\Desktop\\Revision_JBI\\Final List\\RuleWiseResults\\11b.txt", true);   
				    PrintWriter pw=new PrintWriter(filewriter);
				    			    
					String text; String line;  String sno=null;
					while((line=br.readLine())!=null){
							if(line.contains("PMID:")) sno=line;
								
					if((line.length()!=0)&&(!(line.contains("_PMID:")))){
						 text=line.replaceAll("^\\[|\\]$","").trim().replaceAll("^Abstract:|Title:", "");
						
						 //text="Diabetes causes heart related problems".
						  String textsplit[]=text.split("(?<=[.])\\s+(?=[^a-z])");
						 // String textsplit[]=text.split("(?<=[.?]),(?=\\s+[A-Z])");  //Means look behind ',' a dot and look ahead of ',' a whitespace followed by capital letter 
						  System.out.println(textsplit.length);
						  e++;
					   for(int x=0;x<textsplit.length; x++){
							  text=textsplit[x].trim().replaceAll("^Abstract:|Title:", "").replaceAll("^\\[|\\]$","");
							  System.out.println("Sentence: "+x+":  "+text);
						   //  System.out.println(text);
						  //   pw2.println("Sentence: "+e+":  "+text);
						      pw.println("Rule 15bExtended");
						      pw.println("Sentence:");
						      pw.println(text);
						    // pw.println(name); // the name of the file 
						     
				       List wordList = tokenizerFactory.getTokenizer(new StringReader(text.trim())).tokenize();
				      
				      Tree tree = lp.apply(wordList);
				      
				      System.out.println(tree.labeledYield());				    
				      GrammaticalStructure gs = gsf.newGrammaticalStructure(tree);
				      Collection tdl=gs.typedDependenciesCCprocessed();
					 // Main.writeImage(tree, tdl,"image.png");
					//Collection<TypedDependency> td = gs.typedDependenciesCollapsed();
					
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
			   			// System.out.println("HELLO  "+ singleWord+" "+srcnindex);
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
				    		   //System.out.println("i="+i+" "+str1+" "+str2+" "+str3);
				    	   }
				    	   
				    	   
				    	  // else System.out.println("i="+i+" "+str1+" "+str2);
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
						    	    	// depNewRl.add(new ElementAccessClass(deprl,srcn,srcntag,srcnindex,destn,destntag,destindex));
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
				  			  // Thread.sleep(5000);
				    	    list2=depNewRl.toArray();		    	
						 
				    	    depNewRl.clear();  
				    	   }  // for loop reduced dependencies with single word replaced ends  
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
						     ElementAccessClass al;      String temp=""; 
						    for(int i=0;i<myobj.length; i++){               // For loop for extracting triplets from the arraylist  al
						    	 al=(ElementAccessClass) myobj[i];// System.out.println(al.depType+" "+al.firstWord+" "+al.firstTag+" "+ al.secondWord+" "+al.secondTag);  }
						    	//Rule 1 
				             	if(((al.depType.contains("nsubj")&&al.firstTag.contains("NN")&&al.secondTag.contains("NN"))||(al.depType.contains("nsubj")&&al.firstTag.contains("NN")&&al.secondTag.contains("VB")))&&(!(al.secondWord.equals("%")||al.firstWord.isEmpty())))        //Rule 1
				             	{   
				             		   ElementAccessClass aj;   int flag=0;      ElementAccessClass ap=new ElementAccessClass();  int count=0;
				             		 for(int j=i+1; j<myobj.length; j++){
				             			 aj=(ElementAccessClass) myobj[j];   
				             			 if(aj.depType.equals("conj:and")&&al.secondWord.equals(aj.firstWord)){
				             				 ElementAccessClass ak;  
				             				 for(int k=j+1;k<myobj.length;k++){
				             					 ak=(ElementAccessClass) myobj[k];
				             					if(ak.depType.equalsIgnoreCase("cop")&&ak.firstTag.contains("NN")&&ak.secondTag.contains("VB")&&(!(ak.secondWord.equals("were")||ak.secondWord.equals("was"))))
						             			{      temp=aj.secondWord;  flag=1;
						             				 pw.println(al.secondWord+" and "+aj.secondWord+" || "+ak.secondWord+" || "+ak.firstWord+ "		-----From Rule 1b: ");
						             				 System.out.println(al.secondWord+" and "+aj.secondWord+" || "+ak.secondWord+" || "+ak.firstWord+ "		-----From Rule 1b: ");
						             				//System.out.println("Flag: "+flag+" "+temp);	 
						             			}		 
				             				 }     				 
				             			 }
				             			 if(aj.depType.equalsIgnoreCase("cop")&&aj.firstTag.contains("NN")&&aj.secondTag.contains("VB")&&(!(aj.secondWord.equals("were")||aj.secondWord.equals("was")||aj.secondWord.equals("be")||aj.secondWord.equals("been")||aj.firstWord.equals("%")))&&(flag==0)&&(!(temp.equals(al.secondWord))))
				             			   {  
				             				 if(j<myobj.length-2) ap=(ElementAccessClass) myobj[j+1];   
				             				if(ap.depType.equals("neg")&&ap.firstWord.equals(aj.firstWord)&&(aj.firstIndex==ap.firstIndex)){
				             					pw.println(al.secondWord+" || "+aj.secondWord+" "+ap.secondWord+" || "+aj.firstWord+ "		-----From Rule 1 neg: ");
					             				 System.out.println(al.secondWord+" || "+aj.secondWord+" "+ap.secondWord+" || "+aj.firstWord+ "		-----From Rule 1 neg: ");
					             				 count=1;
				             				}
				             				if(ap.depType.equals("acl")&&ap.firstWord.equals(al.firstWord)){  // Rule changed on 1/8/2015 in place of "else if" only "if" is used
				             					ElementAccessClass aq=new ElementAccessClass();
				             					 if(j<myobj.length-2)   aq=(ElementAccessClass) myobj[j+2]; 
				             					// System.out.println("TEST"+aq.depType+" "+aq.firstWord+" " +aq.secondWord);
				             					 if(aq.depType.equals("dobj")&&aq.firstWord.equals(ap.secondWord)&&(aq.firstIndex==ap.secondIndex)){
				             						 System.out.println(al.secondWord+" || "+aj.secondWord+" || "+aj.firstWord+" " +aq.firstWord+" "+aq.secondWord+ "		-----From Rule 1c: ");
				             						 pw.println(al.secondWord+" || "+aj.secondWord+" || "+aj.firstWord+" " +aq.firstWord+" "+aq.secondWord+ "		-----From Rule 1c: ");
				             						 count=1;
				             					 }
				             				}
				             				// else{ 
				             					if(count==0){
				             					pw.println(al.secondWord+" || "+aj.secondWord+" || "+aj.firstWord+ "		-----From Rule 1: ");
				             				   System.out.println(al.secondWord+" || "+aj.secondWord+" || "+aj.firstWord+ "		-----From Rule 1: ");
				     	             		   
				             				// System.out.println("From Rule 1:  "+al.get(i).secondWord.substring(0,al.get(i).secondWord.indexOf("-"))+" "+al.get(j).secondWord.substring(0,al.get(j).secondWord.indexOf("-") )+"  "+al.get(j).firstWord);
				             					//}
				             				}
				             			   }
				             			 
				             			if(aj.depType.equals("nmod:of")&&aj.firstWord.equals(al.secondWord)&&(aj.secondIndex-aj.firstIndex<5)){ 
				             				ElementAccessClass ak;
				             				 for(int k=j+1;k<myobj.length;k++){
				             					 ak=(ElementAccessClass) myobj[k];    
				             				 if(ak.depType.equalsIgnoreCase("cop")&&ak.firstTag.contains("NN")&&ak.secondTag.contains("VB")&&ak.firstWord.equals(al.firstWord)&&(!(ak.secondWord.equals("was")||ak.secondWord.equals("be")||ak.firstWord.equals("%")||ak.secondWord.equals("been")))&&(flag==0)&&(!(temp.equals(al.secondWord)))) 
				             				 {     //removed from the !() to capture more ak.secondWord.equals("were")|| on 12/08/2015 
				             					 pw.println(al.secondWord+" of "+aj.secondWord+" || "+ak.secondWord+" || "+ak.firstWord+ "		-----From Rule 1d: ");
					             				  System.out.println(al.secondWord+" of "+aj.secondWord+" || "+ak.secondWord+" || "+ak.firstWord+ "		-----From Rule 1d: ");
					             				  ElementAccessClass am;
					             				  for(int m=k+1; m<myobj.length; m++){
					             					  am=(ElementAccessClass) myobj[m]; 
					             					  if(am.depType.equals("conj:and")&&am.firstWord.equals(ak.firstWord)&&(!(am.secondWord.equals("%")))){
					             						 pw.println(al.secondWord+" of "+aj.secondWord+" || "+ak.secondWord+" || "+am.secondWord+ "		-----From Rule 1de: ");
							             				  System.out.println(al.secondWord+" of "+aj.secondWord+" || "+ak.secondWord+" || "+am.secondWord+ "		-----From Rule 1de: ");
					             					  }
					             					  if(am.depType.equals("nmod:with")&&am.firstWord.equals(ak.firstWord)&&(am.firstIndex==ak.firstIndex)){
					             						 pw.println(al.secondWord+" of "+aj.secondWord+" || "+ak.secondWord+" || "+ak.firstWord+" with "+am.secondWord+ "		-----From Rule 1d: Extended ");
							             				  System.out.println(al.secondWord+" of "+aj.secondWord+" || "+ak.secondWord+" || "+ak.firstWord+" with "+am.secondWord+ "		-----From Rule 1d: Extended ");  
					             					  }
					             					  if(am.depType.equals("advmod")&&am.firstWord.equals(ak.firstWord)&&(am.firstIndex==ak.firstIndex)){
					             						 pw.println(al.secondWord+" of "+aj.secondWord+" || "+ak.secondWord+" || "+am.secondWord+ "		-----From Rule 1df: ");
							             				  System.out.println(al.secondWord+" of "+aj.secondWord+" || "+ak.secondWord+" || "+am.secondWord+ "		-----From Rule 1df: ");
							             				  ElementAccessClass an;
							             				  for(int n=m+1;n<myobj.length;n++){
							             					  an=(ElementAccessClass)myobj[n];
							             					  if(an.depType.equals("conj:and")&&(an.firstIndex==am.secondIndex+1)){
							             						 pw.println(al.secondWord+" of "+aj.secondWord+" || "+ak.secondWord+" || "+am.secondWord+" "+an.firstWord+" and "+an.secondWord+ "		-----From Rule 1dg: ");
									             				  System.out.println(al.secondWord+" of "+aj.secondWord+" || "+ak.secondWord+" || "+am.secondWord+" "+an.firstWord+" and "+an.secondWord+ "		-----From Rule 1dg: ");						  
							             					  }
							             				  }
	  
					             						  
					             					  }
					             					if(am.depType.contains("acl")&&am.firstWord.equals(ak.firstWord)&&(am.firstIndex==ak.firstIndex)&&ak.secondTag.contains("VB")){
					             						 ElementAccessClass an;
							             				  for(int n=m+1;n<myobj.length;n++){
							             					  an=(ElementAccessClass)myobj[n];
							             					  if(an.depType.contains("nmod:")&&an.firstWord.equals(am.secondWord)&&(an.firstIndex==am.secondIndex)){
							             						 pw.println(al.secondWord+" of "+aj.secondWord+" || "+an.firstWord+" "+an.depType.substring(an.depType.indexOf(":")+1)+" || "+an.secondWord+ "		-----From Rule 1d: NEW EXTENSION");
									             				  System.out.println(al.secondWord+" of "+aj.secondWord+" || "+an.firstWord+" "+an.depType.substring(an.depType.indexOf(":")+1)+" || "+an.secondWord+ "		-----From Rule 1d: NEW EXTENSION");
							             					  }
							             				  }
					             						
					             					}  
					             				  }
					     	             		
					             			   }
				             				}
				             			}
				             	   }          
				             	
				             	}
				             	//Rule 3
				             	if(al.depType.contains("nsubj")&&al.firstTag.contains("VB")&&al.secondTag.contains("NN")&&(!(al.firstWord.equals(al.secondWord)||al.secondWord.equals("%")||al.secondWord.equals("100")||al.firstWord.equals("e.")||al.firstWord.equals("stop")||al.secondWord.equals("efforts")||al.secondWord.equals("case")||al.secondWord.equals("·")||al.secondWord.equals("+")||al.secondWord.equals("x")||al.secondWord.equals("one")||al.secondWord.contains("Other_then_")||al.firstWord.equals("seeking")||al.firstWord.equals("-LSB-")||al.secondWord.equals("-RSB-")))){        // Rule 3
				             				ElementAccessClass ak;   int flag=0; int a3c=0; //System.out.println(al.firstWord+" "+al.secondWord);
				             				for(int k=i+1;k<myobj.length;k++){;
				             					ak=(ElementAccessClass) myobj[k];   
				             					if(ak.depType.equalsIgnoreCase("case")&&ak.secondTag.equals("IN")&&(!(ak.firstWord.equals("-RSB-")||ak.firstWord.isEmpty()||(al.firstWord.equals("had")&&ak.secondWord.equals("with"))))){ //Rule 3(a)   &&((ak.firstIndex-ak.secondIndex)<4)
				             						ElementAccessClass am;   
				             						for(int m=k;m<myobj.length;m++){ 
				             							am=(ElementAccessClass) myobj[m];   int count=0; 
				             							//System.out.println("TEST "+ak.firstWord+" "+ak.secondWord);
				             							if(am.depType.contains("nmod:")&&am.firstWord.equalsIgnoreCase(al.firstWord)&&(am.firstIndex==ak.secondIndex-1)&&(!(am.secondTag.equals("CD")||am.secondWord.equals("±")||am.secondWord.equals("-RSB-")||am.secondWord.isEmpty()||am.depType.contains(":above")||am.depType.contains(":tmod")||am.depType.contains(":such_as")||am.secondWord.equals("%")||am.secondWord.equals(">")||am.secondWord.equals("month")||(am.firstWord.equals("followed")&&ak.secondWord.equals("by"))))){         // This portion  "&&ak.firstWord.equalsIgnoreCase(am.secondWord)" was cut from the previous  3a rule and am.firstIndex==ak.firstIndex-1 to capture more relations on 1/8/2015
				             								 ElementAccessClass an;  //am.depType.contains("agent")||
				             							
					             						      for(int n=m+1;n<myobj.length;n++){
					             						    	 an=(ElementAccessClass) myobj[n];  
					             						    	 if(an.depType.contains("nmod")&&am.secondWord.equals(an.firstWord)&&(an.firstIndex==am.secondIndex)&&an.secondTag.contains("NN")&&(!(an.secondWord.equals("%")))){   //&&(an.secondIndex-am.secondIndex<6) removed on 09/2/2016 to capture more
					             						    		System.out.println(al.secondWord+" || "+al.firstWord+"_"+ak.secondWord+" || "+am.secondWord+" "+an.depType.substring(an.depType.indexOf(":")+1)+" "+an.secondWord+"		-----From Rule 3d:  ");
					             						    		pw.println(al.secondWord+" || "+al.firstWord+"_"+ak.secondWord+" || "+am.secondWord+" "+an.depType.substring(an.depType.indexOf(":")+1)+" "+an.secondWord+"		-----From Rule 3d:  ");
					             						    		count =1;
					             						    		ElementAccessClass ae;
					             						    		for(int p=n+1;p<myobj.length;p++){
					             						    			ae=(ElementAccessClass) myobj[p]; 
					             						    			if(ae.depType.equals("conj:and")&&ae.firstWord.equals(an.firstWord)&&(!(ae.secondWord.equals("%")))){
					             						    				System.out.println(al.secondWord+" || "+al.firstWord+"_"+ak.secondWord+" || "+ae.secondWord+"		-----From Rule 3e:  ");
							             						    		pw.println(al.secondWord+" || "+al.firstWord+"_"+ak.secondWord+" || "+ae.secondWord+"		-----From Rule 3e:  ");
					             						    				
					             						    			}
					             						    			if(ae.depType.equals("appos")&&ae.firstWord.equals(an.secondWord)&&(ae.firstIndex==an.secondIndex)&&ae.secondTag.contains("NN")&&(!(ae.secondWord.equals("OR")||ae.secondWord.equals("%")))){
					             						    				System.out.println(al.secondWord+" || "+al.firstWord+"_"+ak.secondWord+" || "+am.secondWord+" "+an.depType.substring(an.depType.indexOf(":")+1)+" "+ae.secondWord+"		-----From Rule 3dm:  ");
							             						    		pw.println(al.secondWord+" || "+al.firstWord+"_"+ak.secondWord+" || "+am.secondWord+" "+an.depType.substring(an.depType.indexOf(":")+1)+" "+ae.secondWord+"		-----From Rule 3dm:  ");

					             						    			}
					             						    		}
					             						    		
					             						    	 }
					             						    	 
					             						    	 
					             						      }
					             						      if(count==0){  
					             						    		//System.out.println("TEST "+ak.firstWord+" "+ak.secondWord);
					             						    	  if(am.depType.substring(am.depType.indexOf(":")+1).equals("agent")){              // extra added to capture the relations with by as agent connects two words with by modefied on Dec 8 2015. Previously there was no if part
					             						    		  System.out.println(al.secondWord+" || "+al.firstWord+"_by || "+am.secondWord+"		-----From Rule 3a: for nmod:agent ");     
					             					 	              pw.println(al.secondWord+" || "+al.firstWord+"_by || "+am.secondWord+"		-----From Rule 3a: 3a: for nmod:agent  "); 
					             					 	              //System.out.println(al.secondWord+" "+ak.secondWord+ " "+ak.firstWord+" || "+al.firstWord+"_by || "+am.secondWord+"		-----From Rule 3a: for nmod:agent ");     
					             					 	            a3c=1;}
					             						    	  else{
					             						    	if(!(al.firstWord.equalsIgnoreCase("associated")&&am.depType.substring(am.depType.indexOf(":")+1).equals("in"))){	  
				             					 	            System.out.println(al.secondWord+" || "+al.firstWord+"_"+am.depType.substring(am.depType.indexOf(":")+1)+" || "+am.secondWord+"		-----From Rule 3a:  ");     
				             					 	            pw.println(al.secondWord+" || "+al.firstWord+"_"+am.depType.substring(am.depType.indexOf(":")+1)+" || "+am.secondWord+"		-----From Rule 3a:  "); 
				             					 	            a3c=1;}
				             					 	            ElementAccessClass ap;
				             					 	            for(int p=m+1; p<myobj.length-1;p++){
				             					 	            	ap=(ElementAccessClass) myobj[p];
				             					 	            	if((ap.depType.contains("appos")||ap.depType.contains("conj:or"))&&ap.firstWord.equals(am.secondWord)&&(ap.firstIndex==am.secondIndex)&&(!(ap.secondWord.equals("P")||ap.secondWord.equals("%")))){
				             					 	            		System.out.println(al.secondWord+" || "+al.firstWord+"_"+am.depType.substring(am.depType.indexOf(":")+1)+" || "+ap.secondWord+"		-----From Rule 3a:  Extended");     
						             					 	            pw.println(al.secondWord+" || "+al.firstWord+"_"+am.depType.substring(am.depType.indexOf(":")+1)+" || "+ap.secondWord+"		-----From Rule 3a:  Extended"); 
						      					 	            		
				             					 	            	}
				             					 	            	if(ap.depType.contains("xcomp")&&ap.firstWord.equals(am.firstWord)&&(ap.firstIndex==am.firstIndex)){				            				 	       
				             					 	            		ElementAccessClass aq;
						             					 	            for(int q=p+1; q<myobj.length-1;q++){
						             					 	            	aq=(ElementAccessClass) myobj[q];
						             					 	            	/*if(aq.depType.equals("dobj")&&aq.firstWord.equals(ap.secondWord)&&(aq.firstIndex==ap.secondIndex)){
						             					 	            		System.out.println(al.secondWord+" || "+al.firstWord+"_"+am.depType.substring(am.depType.indexOf(":")+1)+"|| "+am.secondWord+"		-----From Rule 3a:  Extended 3 ");     
								             					 	            pw.println(al.secondWord+" || "+al.firstWord+"_"+am.depType.substring(am.depType.indexOf(":")+1)+"|| "+am.secondWord+"		-----From Rule 3a:  Extended 3 "); 
								             					 	        
						             					 	            	}*/
						             					 	            	if(aq.depType.contains("nmod:")&&aq.firstWord.equals(ap.secondWord)&&(aq.firstIndex==ap.secondIndex)){
						             					 	            		System.out.println(al.secondWord+" || "+al.firstWord+"_"+am.depType.substring(am.depType.indexOf(":")+1)+" || "+aq.secondWord+"		-----From Rule 3a:  Extended 2");     
								             					 	            pw.println(al.secondWord+" || "+al.firstWord+"_"+am.depType.substring(am.depType.indexOf(":")+1)+" || "+aq.secondWord+"		-----From Rule 3a:  Extended 2"); 
					
						             					 	            	}
						             					 	            	}
				             					 	            	}
				             					 	            	
				             					 	            	
				             					 	            }
				             					 	            }
					             						      }  
				             							}
				             						 if(am.depType.contains("nmod:")&&am.firstWord.equals(al.secondWord)&&(am.firstIndex==al.secondIndex)&&(!(am.secondWord.equals("%")))){
				             							 ElementAccessClass aq; 
				             							 for(int q=m+1; q<myobj.length; q++ ){
				             								 aq=(ElementAccessClass) myobj[q];    //System.out.println("MY TEST"+ ak.firstWord+" "+ak.secondWord);
				             							 if(aq.depType.equals("dobj")&&aq.firstWord.equals(al.firstWord)&&(aq.firstIndex==al.firstIndex)&&(am.secondIndex==aq.firstIndex-1)&&(!(aq.secondTag.equals("CD")))){
				             								System.out.println(al.secondWord+" "+ak.secondWord+" "+ak.firstWord+" || "+al.firstWord+" || "+aq.secondWord+"		----From Rule 3k  " );
						             					    pw.println(al.secondWord+" "+ak.secondWord+" "+ak.firstWord+" || "+al.firstWord+" || "+aq.secondWord+"		----From Rule 3k  " );
						             					    flag=1;
						             					  /*  ElementAccessClass as;
						             					    for(int s=q+1;s<myobj.length;s++){
						             					    	as=(ElementAccessClass) myobj[s];
						             					    	if(as.depType.equals("conj:and")&&as.firstWord.equals(aq.secondWord)&&(as.firstIndex==aq.secondIndex)&&(!(as.secondWord.equals("%")))){
						             					    		System.out.println(al.secondWord+" "+ak.secondWord+" "+ak.firstWord+" || "+al.firstWord+" || "+as.secondWord+"		----From Rule 3k Extended " );
								             					    pw.println(al.secondWord+" "+ak.secondWord+" "+ak.firstWord+" || "+al.firstWord+" || "+as.secondWord+"		----From Rule 3k Extended " );
								             					   
						             					    	}
						             					    }*/
				             							   } 
				             							 if(aq.depType.contains("nmod:")&&aq.firstWord.equals(am.secondWord)&&(aq.firstIndex==am.secondIndex)){
				             								ElementAccessClass ar;
				             								for(int r=q+1;r<myobj.length;r++){
				             									ar=(ElementAccessClass) myobj[r];
				             							       if(ar.depType.equals("dobj")&&ar.firstWord.equals(al.firstWord)&&(ar.firstIndex==al.firstIndex)&&(!(ar.secondWord.equals("%")))){
				             								  System.out.println(al.secondWord+" "+am.depType.substring(am.depType.indexOf(":")+1)+" "+am.secondWord+" "+aq.depType.substring(aq.depType.indexOf(":")+1)+" "+aq.secondWord+" || "+al.firstWord+" || "+ar.secondWord+"		----From Rule 3p  " );
						             					       pw.println(al.secondWord+" "+am.depType.substring(am.depType.indexOf(":")+1)+" "+am.secondWord+" "+aq.depType.substring(aq.depType.indexOf(":")+1)+" "+aq.secondWord+" || "+al.firstWord+" || "+ar.secondWord+"		----From Rule 3p  " );
						             					      flag=1;
						             					     ElementAccessClass as;
					             								for(int s=r+1;s<myobj.length;s++){
					             									as=(ElementAccessClass) myobj[s];
					             									if(as.depType.contains("xcomp")&&as.firstWord.equals(ar.firstWord)&&(as.firstIndex==ar.firstIndex)){
					             										System.out.println(al.secondWord+" "+am.depType.substring(am.depType.indexOf(":")+1)+" "+am.secondWord+" "+aq.depType.substring(aq.depType.indexOf(":")+1)+" "+aq.secondWord+" || "+al.firstWord+" || "+as.secondWord+"		----From Rule 3p  EXTENSION" );
									             					       pw.println(al.secondWord+" "+am.depType.substring(am.depType.indexOf(":")+1)+" "+am.secondWord+" "+aq.depType.substring(aq.depType.indexOf(":")+1)+" "+aq.secondWord+" || "+al.firstWord+" || "+as.secondWord+"		----From Rule 3p  EXTENSION" );

					             									}
					             									}
				             							   } 
				             								}
				             							  }
				             							 }
				             						 }
				             						 if(am.depType.equals("conj:and")&&am.secondWord.equals(ak.firstWord)&&(am.firstIndex==ak.secondIndex+1)&&(!(al.firstWord.equals("assay")&&ak.secondWord.equals("of")||al.firstWord.equals("presented")&&ak.secondWord.equals("of")||am.firstWord.equals(am.secondWord)))){
				             							System.out.println(al.secondWord+" || "+al.firstWord+"_"+ak.secondWord+" || "+am.firstWord+" and "+am.secondWord+" 		-----Rule 3h:");	
				             							pw.println(al.secondWord+" || "+al.firstWord+"_"+ak.secondWord+" || "+am.firstWord+" and "+am.secondWord+" 		-----Rule 3h:");	
				             							}
				             					      }
				             					   }  
				             					
				             					else if (ak.depType.equalsIgnoreCase("dobj")&&ak.firstWord.equalsIgnoreCase(al.firstWord)&&(flag==0)&&(!(ak.secondWord.equals("$")||ak.secondWord.equals("%")||ak.secondTag.equals("JJ")||ak.secondWord.isEmpty()||ak.secondTag.equals("DT")||ak.secondTag.equals("RB")||al.secondWord.contains("following")||ak.secondTag.equals("CD")||ak.secondWord.equals("region")||al.firstWord.equals("typhoid")||ak.secondWord.equals("areas")||ak.secondWord.equals("-RSB-")))){      // Rule 3(b)
				             						System.out.println(al.secondWord+" || "+al.firstWord+" || "+ak.secondWord+"		----From Rule 3b:  " );
				             					    pw.println(al.secondWord+" || "+al.firstWord+" || "+ak.secondWord+"		----From Rule 3b:  " );
				             					    ElementAccessClass am;
				             					   for(int m=k+1;m<myobj.length;m++){
				             							 am=(ElementAccessClass) myobj[m];
				             							 if(am.depType.contains("appos")&&am.firstWord.equals(ak.secondWord)&&(am.firstIndex==ak.secondIndex)&&(!(am.secondWord.equals("%")))){
				             								System.out.println(al.secondWord+" || "+al.firstWord+" || "+am.secondWord+"		----From Rule 3b: EXTENSION " );
						             					    pw.println(al.secondWord+" || "+al.firstWord+" || "+am.secondWord+"		----From Rule 3b: EXTENTION " );
						             					    
				             							 }
				             							 if(am.depType.contains("acl")&&am.firstWord.equals(ak.secondWord)&&(am.firstIndex==ak.secondIndex)&&am.secondTag.contains("VB")){
				             								ElementAccessClass an;
							             					   for(int n=m+1;n<myobj.length-1;n++){
							             							 an=(ElementAccessClass) myobj[n];
							             							 if(an.depType.contains("nmod:")&&an.firstWord.equals(am.secondWord)&&(an.firstIndex==am.secondIndex)){
							             								System.out.println(al.secondWord+" "+al.firstWord+" "+ak.secondWord+" || "+an.firstWord+" "+an.depType.substring(an.depType.indexOf(":")+1)+ " || "+an.secondWord+"		----From Rule 3b:  Extension 2" );
							             								pw.println(al.secondWord+" "+al.firstWord+" "+ak.secondWord+" || "+an.firstWord+" "+an.depType.substring(an.depType.indexOf(":")+1)+ " || "+an.secondWord+"		----From Rule 3b:  Extension 2" );
							             								ElementAccessClass ap;
										             					   for(int p=n+1;p<myobj.length;p++){
										             							 ap=(ElementAccessClass) myobj[p];  
										             							 if(ap.depType.contains("nmod:")&&ap.firstWord.equals(an.secondWord)&&(ap.firstIndex==an.secondIndex)&&ap.secondTag.contains("NN")){
										             								System.out.println(al.secondWord+" "+al.firstWord+" "+ak.secondWord+" || "+an.firstWord+" "+an.depType.substring(an.depType.indexOf(":")+1)+ " || "+an.secondWord+" "+ap.depType.substring(ap.depType.indexOf(":")+1)+" "+ap.secondWord+"		----From Rule 3b:  Extension 3" );
										             								pw.println(al.secondWord+" "+al.firstWord+" "+ak.secondWord+" || "+an.firstWord+" "+an.depType.substring(an.depType.indexOf(":")+1)+ " || "+an.secondWord+" "+ap.depType.substring(ap.depType.indexOf(":")+1)+" "+ap.secondWord+"		----From Rule 3b:  Extension 3" );
										             							
										             								 
										             							 }
										             							 }
									             					  
							             							 }
							             							 }
				             							 }
				             							 }
				             					}
				             					else if(ak.depType.contains("nmod:")&&ak.firstWord.equalsIgnoreCase(al.firstWord)&&ak.secondTag.contains("NN")&&(ak.firstIndex<ak.secondIndex)&&(!(ak.depType.contains("than")||ak.secondWord.isEmpty()||ak.depType.contains(":tmod")||ak.secondWord.equals("%")||ak.depType.contains("within")||ak.secondWord.equals("-RSB-")||ak.secondWord.equals("weeks")||ak.depType.contains(":at")||ak.depType.contains("including")||ak.depType.contains("from")||(ak.depType.contains("with")&&ak.firstWord.equals("have"))||(ak.depType.contains("with")&&ak.firstWord.equals("request"))||(ak.depType.contains("with")&&ak.firstWord.equals("include"))||(ak.depType.contains(":by")&&ak.firstWord.equals("followed"))||(al.firstWord.equals("have")&&ak.depType.contains(":to"))||(al.firstWord.equals("belong")&&ak.depType.contains(":in"))||(al.firstWord.equals("recommends")&&ak.depType.contains(":with"))))){                 // Rule 3c
				             						 ElementAccessClass am;  int count=0;  // ||ak.depType.contains("agent")
				             						 for(int m=k+1;m<myobj.length;m++){
				             							 am=(ElementAccessClass) myobj[m];
				             							 if(am.depType.contains("nmod:in")&&am.firstWord.equals(al.firstWord)&&(am.secondIndex-ak.secondIndex<4)&&(!(am.secondWord.equals(ak.secondWord)||am.depType.substring(am.depType.indexOf(":")+1).equals(ak.depType.substring(ak.depType.indexOf(":")+1))))){
				             								 System.out.println(al.secondWord+" || " +al.firstWord+"_"+ak.depType.substring(ak.depType.indexOf(":")+1)+" || "+ak.secondWord+" "+am.depType.substring(am.depType.indexOf(":")+1)+" "+am.secondWord+"		----Rule: 3c.f");
				             								 pw.println(al.secondWord+" || " +al.firstWord+"_"+ak.depType.substring(ak.depType.indexOf(":")+1)+" || "+ak.secondWord+" "+am.depType.substring(am.depType.indexOf(":")+1)+" "+am.secondWord+"		----Rule: 3c.f");
				             								 count=1;
				             							 }
				             							 if(am.depType.contains("nmod:")&&am.firstWord.equals(ak.secondWord)&&(am.secondIndex-am.firstIndex<4)&&(!(am.firstWord.equals("%")||am.secondWord.equals("%")))){  // just droping rule 3c.g as it gives same results as rule 3d and 9b
				             								 System.out.println(al.secondWord+" || " +al.firstWord+"_"+ak.depType.substring(ak.depType.indexOf(":")+1)+" || "+am.firstWord+" "+am.depType.substring(am.depType.indexOf(":")+1)+" "+am.secondWord+"		----Rule: 3c.g");
				             								 pw.println(al.secondWord+" || " +al.firstWord+"_"+ak.depType.substring(ak.depType.indexOf(":")+1)+" || "+am.firstWord+" "+am.depType.substring(am.depType.indexOf(":")+1)+" "+am.secondWord+"		----Rule: 3c.g");
				             								 count=1;
				             								ElementAccessClass an;
				             								for(int n=m+1;n<myobj.length-1;n++){
				             									an=(ElementAccessClass) myobj[n];
				             									if(an.depType.contains("nmod:")&&an.firstWord.equals(am.secondWord)&&(an.firstIndex==am.secondIndex)){
				             										System.out.println(al.secondWord+" || " +al.firstWord+"_"+ak.depType.substring(ak.depType.indexOf(":")+1)+" || "+am.firstWord+" "+am.depType.substring(am.depType.indexOf(":")+1)+" "+am.secondWord+" "+an.depType.substring(an.depType.indexOf(":")+1)+" "+an.secondWord+"		----Rule: 3c.g Extended");
						             								 pw.println(al.secondWord+" || " +al.firstWord+"_"+ak.depType.substring(ak.depType.indexOf(":")+1)+" || "+am.firstWord+" "+am.depType.substring(am.depType.indexOf(":")+1)+" "+am.secondWord+" "+an.depType.substring(an.depType.indexOf(":")+1)+" "+an.secondWord+"		----Rule: 3c.g Extended");
						             								
				             									}
				             								}
				             								 
				             							 }
				             							 		 
				             						 }
				             						 if((count==0)&&(a3c==0)){
				             						if(!((al.secondWord.equals(ak.secondWord))||(ak.depType.substring(ak.depType.indexOf(":")+1).equals("in"))||(ak.depType.substring(ak.depType.indexOf(":")+1).equals("at")||ak.secondWord.equals("±")||(al.firstWord.equals("were")&&ak.depType.contains(":with"))))){ 
				             						System.out.println(al.secondWord+" || "+al.firstWord+"_"+ak.depType.substring(ak.depType.indexOf(":")+1)+" || "+ak.secondWord +"		-----From Rule 3c:  ");
				             						pw.println(al.secondWord+" || "+al.firstWord+"_"+ak.depType.substring(ak.depType.indexOf(":")+1)+" || "+ak.secondWord +"		-----From Rule 3c:  ");
				             						
				             						   }
				             						}
				             					}
				             					
				             				}   
				             		
				             	 }
				             
				             	// Rule 2
				              if(al.depType.contains("nsubj")&&al.firstTag.contains("NN")&&al.secondTag.contains("NN")&&(!(al.firstWord.equals("h")||al.firstWord.equals("%")||al.secondWord.equals("%")||al.firstWord.equals("fever0")||al.secondWord.equals("causes")||al.secondWord.equals("case")||al.secondWord.equals("th_percentile"))))   //Rule 2
				             	{
				            	     ElementAccessClass aj2;          int count=0;  ElementAccessClass ajcop, ajneg=new ElementAccessClass(), ajtemp=new ElementAccessClass();
				             		 for(int j=i+1; j<myobj.length; j++){
				             			aj2=(ElementAccessClass) myobj[j]; 
				             			if(aj2.depType.equals("cop")){            // saving cop and neg atoms for latter use
				             				ajcop=aj2;
				             				if(j<myobj.length-2)
				             				ajtemp=(ElementAccessClass) myobj[j+1];
				             				if(ajtemp.depType.equals("neg")&&ajtemp.firstWord.equals(ajcop.firstWord)&&(ajtemp.firstIndex==ajcop.firstIndex))
				             				  ajneg=ajtemp;
				             			} 
				             			if(aj2.depType.equalsIgnoreCase("acl")&&aj2.firstWord.equalsIgnoreCase(al.firstWord)&&aj2.secondTag.contains("VB"))
				             			{
				             				 ElementAccessClass ak3;
				             				for(int k=j;k<myobj.length;k++){
				             					ak3=(ElementAccessClass) myobj[k];
				             					if(ak3.depType.contains("nmod:")&&ak3.firstWord.equalsIgnoreCase(aj2.secondWord)&&(!(ak3.secondWord.equals("%")))){	
				             					System.out.println(al.secondWord+" || "+aj2.secondWord+"_"+ak3.depType.substring(ak3.depType.indexOf(":")+1)+" || "+ak3.secondWord+"		-----From Rule 2:  " );
				             					pw.println(al.secondWord+" || "+aj2.secondWord+"_"+ak3.depType.substring(ak3.depType.indexOf(":")+1)+" || "+ak3.secondWord+"		-----From Rule 2:  " );
				             					}
				             				} 
				             			}
				             		if(aj2.depType.equals("conj:or")&&aj2.firstTag.equals("IN")&&aj2.secondTag.equals("IN")){
				             			ElementAccessClass am; 
				             			for(int m=j+1;m<myobj.length;m++){
				             				am=(ElementAccessClass) myobj[m];
				             			if(am.depType.contains("nmod:")&&am.firstWord.equalsIgnoreCase(al.firstWord)&&(am.depType.substring(am.depType.indexOf(":")+1).equals(aj2.firstWord))){  
				             			    ElementAccessClass ak=(ElementAccessClass) myobj[m+1];
				             			   if(ak.depType.contains("nmod:")&&ak.depType.substring(am.depType.indexOf(":")+1).equals(aj2.secondWord)&&ak.secondWord.equals(am.secondWord))
				    	   					//pw.println(al.get(i).secondWord+" || "+al.get(i).firstWord.substring(0,al.get(i).firstWord.indexOf("-"))+"_"+al.get(j).depType.substring(al.get(j).depType.indexOf("_"))+" || "+al.get(j).secondWord +"		-----From Rule 4:  " ); 			
				    	   					System.out.println(al.secondWord+" || "+al.firstWord+"_"+aj2.firstWord+" "+aj2.depType.substring(aj2.depType.indexOf(":")+1)+" "+aj2.secondWord+" || "+am.secondWord +"		-----From Rule 2c: " );
				    	   					pw.println(al.secondWord+" || "+al.firstWord+"_"+aj2.firstWord+" "+aj2.depType.substring(aj2.depType.indexOf(":")+1)+" "+aj2.secondWord+" || "+am.secondWord +"		-----From Rule 2c: " );
				    	   					count=1;
				    	   					} 
				             			}
				             		}
				             		else if(aj2.depType.contains("nmod:")&&aj2.firstWord.equalsIgnoreCase(al.firstWord)&&(count==0)&&(!(aj2.secondTag.equals("RB")||aj2.secondWord.equals("%")||aj2.secondTag.equals("DT")||aj2.firstWord.equals(aj2.secondWord)||aj2.secondWord.equals("percentage")))){ 
					    	   					if(ajneg.firstWord.equals(aj2.firstWord)&&(ajneg.firstIndex==aj2.firstIndex)){
					    	   						System.out.println(al.secondWord+" || "+ajneg.secondWord+"_"+al.firstWord+"_"+aj2.depType.substring(aj2.depType.indexOf(":")+1)+" || "+aj2.secondWord +"		-----From Rule 2b neg : Obtained from rule 4  " );
						    	   					pw.println(al.secondWord+" || "+ajneg.secondWord+"_"+al.firstWord+"_"+aj2.depType.substring(aj2.depType.indexOf(":")+1)+" || "+aj2.secondWord +"		-----From Rule 2b neg: " );
		
					    	   					} else{		
					    	   					System.out.println(al.secondWord+" || "+al.firstWord+"_"+aj2.depType.substring(aj2.depType.indexOf(":")+1)+" || "+aj2.secondWord +"		-----From Rule 2b: Obtained from rule 4  " );
					    	   					pw.println(al.secondWord+" || "+al.firstWord+"_"+aj2.depType.substring(aj2.depType.indexOf(":")+1)+" || "+aj2.secondWord +"		-----From Rule 2b: Obtained from rule 4" );
					    	   					ElementAccessClass ak;
					    	   					for(int k=j+1;k<myobj.length;k++){
					    	   						ak=(ElementAccessClass) myobj[k];
					    	   					if(ak.depType.equals("nmod:")&&ak.firstWord.equals(aj2.secondWord)&&(ak.firstIndex==aj2.secondIndex)){
					    	   						System.out.println(al.secondWord+" || "+al.firstWord+"_"+aj2.depType.substring(aj2.depType.indexOf(":")+1)+" || "+aj2.secondWord+"  "+ak.depType.substring(ak.depType.indexOf(":")+1)+" "+ak.secondWord +"		-----From Rule 2b Ext:   " );
						    	   					pw.println(al.secondWord+" || "+al.firstWord+"_"+aj2.depType.substring(aj2.depType.indexOf(":")+1)+" || "+aj2.secondWord+"  "+ak.depType.substring(ak.depType.indexOf(":")+1)+" "+ak.secondWord +"		-----From Rule 2b Ext: " );
					    	   					  }
					    	   					if(ak.depType.equals("acl")&&ak.firstWord.equals(aj2.secondWord)&&(ak.firstIndex==aj2.secondIndex)&&ak.secondTag.contains("VB")){
					    	   						ElementAccessClass am;
						    	   					for(int m=k+1;m<myobj.length;m++){
						    	   						am=(ElementAccessClass) myobj[m];
						    	   						if(am.depType.contains("nmod:")&&am.firstWord.equals(ak.secondWord)&&(am.firstIndex==ak.secondIndex)&&am.secondTag.contains("NN")){
						    	   							System.out.println(al.secondWord+" || "+am.firstWord+"_"+am.depType.substring(am.depType.indexOf(":")+1)+" || "+am.secondWord +"		-----From Rule 2b: Extended 3  " );
								    	   					pw.println(al.secondWord+" || "+am.firstWord+"_"+am.depType.substring(am.depType.indexOf(":")+1)+" || "+am.secondWord +"		-----From Rule 2b: Extended 3" );
								    	   					
						    	   						}
					    	   						}
					    	   					}
					    	   					}
					    	   					} 	
				             		     }
				             	   }
				             	}
				             /* // Rule 4
				              if(al.depType.contains("nsubj")&&(al.firstTag.contains("NN"))&&al.secondTag.contains("NN")){        // Rule 4
				            	  ElementAccessClass aj;
				   				for(int j=i;j<myobj.length;j++){
				   					aj=(ElementAccessClass) myobj[j];
				   					if(aj.depType.contains("nmod:")&&aj.firstWord.equalsIgnoreCase(al.firstWord)){  
				   					//pw.println(al.get(i).secondWord+" || "+al.get(i).firstWord.substring(0,al.get(i).firstWord.indexOf("-"))+"_"+al.get(j).depType.substring(al.get(j).depType.indexOf("_"))+" || "+al.get(j).secondWord +"		-----From Rule 4:  " ); 			
				   					System.out.println(al.secondWord+" || "+al.firstWord+"_"+aj.depType.substring(aj.depType.indexOf(":")+1)+" || "+aj.secondWord +"		-----From Rule 4:  " );
				   					pw.println(al.secondWord+" || "+al.firstWord+"_"+aj.depType.substring(aj.depType.indexOf(":")+1)+" || "+aj.secondWord +"		-----From Rule 4:  " );
				   					}   
				   				}
				              }
				              */
				              // Rule 5
				              if(al.depType.contains("nsubj")&&al.firstTag.contains("VB")&&al.secondTag.contains("NN")&&(!(al.secondWord.equals("%")||al.secondWord.equals("·")))){
				            	  ElementAccessClass aj;     int count=0;
				     				for(int j=i+1;j<myobj.length;j++){
				     					aj=(ElementAccessClass) myobj[j];
				     					 if(aj.depType.equals("case")&&(al.secondIndex+1==aj.secondIndex)&&(!(aj.secondWord.equals("$")))){
				     						 //System.out.println("TEST 5"+aj.firstWord+" "+aj.secondWord);
					     					ElementAccessClass ap=(ElementAccessClass) myobj[j+1]; String compositWord;
					     					//System.out.println(ap.depType.contains("nmod:")&&ap.secondWord.equals(aj.firstWord)&&al.secondWord.equals(ap.firstWord));
					     					//System.out.println(ap.depType+" "+ap.secondWord+" "+aj.firstWord+" "+al.secondWord+" " +ap.firstWord);
					     					if(ap.depType.contains("nmod:")&&ap.secondWord.equals(aj.firstWord)&&al.secondWord.equals(ap.firstWord)){	
					     						compositWord=al.secondWord+"_"+aj.secondWord+"_"+aj.firstWord;
					     						//System.out.println(compositWord);
					     						ElementAccessClass am;
					    	     				for(int m=j+1;m<myobj.length;m++){
					    	     					am=(ElementAccessClass) myobj[m];	
					    	     					if(am.depType.contains("dobj")&&am.firstWord.equalsIgnoreCase(al.firstWord)&&(!(am.secondWord.equals("%")))){  
					    	     						ElementAccessClass ak;
					    	     				      for(int k=m+1;k<myobj.length;k++){
					    	     				    	 ak=(ElementAccessClass) myobj[k];
					    	     				      if(ak.depType.contains("nmod:")&&ak.firstWord.equalsIgnoreCase(am.secondWord)&&(!(ak.secondWord.equals("%")))){ 
					    	     					   // System.out.println(compositWord+" || "+al.firstWord+"_"+am.secondWord+"_"+ak.depType.substring(ak.depType.indexOf(":")+1)+" || "+ak.secondWord +" 		-----From Rule 5b: ");
					    	     					 // pw.println(compositWord+" || "+al.firstWord+"_"+am.secondWord+"_"+ak.depType.substring(ak.depType.indexOf(":")+1)+" || "+ak.secondWord +" 		-----From Rule 5b: ");
					    	     					   count=1;
					    	     					  System.out.println(compositWord+" || "+al.firstWord+" || "+am.secondWord+"_"+ak.depType.substring(ak.depType.indexOf(":")+1)+" "+ak.secondWord +" 		-----From Rule 5b: Modified");
					    	     					  pw.println(compositWord+" || "+al.firstWord+" || "+am.secondWord+"_"+ak.depType.substring(ak.depType.indexOf(":")+1)+" "+ak.secondWord +" 		-----From Rule 5b: Modified");
					    	     					 ElementAccessClass as;
					    	     				      for(int s=k+1;s<myobj.length;s++){
					    	     				    	 as=(ElementAccessClass) myobj[s];
					    	     				    	 if(as.depType.contains("conj:and")&&as.firstWord.equals(ak.secondWord)&&(as.firstIndex==ak.secondIndex)){
					    	     				    		 System.out.println(compositWord+" || "+al.firstWord+" || "+as.secondWord +" 		-----From Rule 5b:  Extended");
							    	     					  pw.println(compositWord+" || "+al.firstWord+" || "+as.secondWord +" 		-----From Rule 5b:  Extended");
							    	     			
					    	     				    	 }
					    	     				    	 }
					    	     					       }
					    	     					} 
					    	     				  }
					    	     			  	if(am.depType.equals("case")&&(am.secondIndex==ap.secondIndex+1)){
					    	     			  		ElementAccessClass at;
					    	     			  		for(int t=m+1;t<myobj.length;t++){
					    	     			  			at=(ElementAccessClass) myobj[t];
					    	     			  			if(at.depType.contains("nmod:")&&at.firstWord.equals(al.firstWord)&&at.secondTag.contains("NN")&&(at.firstIndex<at.secondIndex)&&(!((at.firstWord.equals("were")&&at.depType.contains("with"))||at.secondWord.equals("%")))){
					    	     			  				System.out.println(compositWord+" "+am.secondWord+" "+am.firstWord+" || "+at.firstWord+" "+at.depType.substring(at.depType.indexOf(":")+1)+" || "+at.secondWord+"		--Rule 5d:");
					    	     			  				pw.println(compositWord+" "+am.secondWord+" "+am.firstWord+" || "+at.firstWord+" "+at.depType.substring(at.depType.indexOf(":")+1)+" || "+at.secondWord+"		--Rule 5d:");

					    	     			  			}
					    	     			  			
					    	     			  		}
					    	     			  	} 
					    	     			  	if(am.depType.equals("nmod:agent")&&am.firstWord.equals(al.firstWord)&&(am.firstIndex==al.firstIndex)){
					    	     			  		System.out.println(compositWord+" || "+am.firstWord+ " by || "+ am.secondWord+" -----New Rule 5e");// New Rule Added on 08/02/2016
					    	     			  		pw.println(compositWord+" || "+am.firstWord+ " by || "+ am.secondWord+" -----New Rule 5e");// New Rule Added on 08/02/2016
					    	     			  	}
					     					} 
					    	     				
					     				 }
					                  }
				     					else if(aj.depType.contains("dobj")&&aj.firstWord.equalsIgnoreCase(al.firstWord)&&(count==0)&&(!(aj.secondWord.equals("$")||aj.secondWord.equals("%")))){  
				     						ElementAccessClass ak;      // ElementAccessClass aza=new ElementAccessClass();
				     				      for(int k=j+1;k<myobj.length;k++){
				     				    	 ak=(ElementAccessClass) myobj[k];    int flag=0; // aza=(ElementAccessClass) myobj[k-1];  &&(aza.firstWord.equals(aza.secondWord)&&(aza.secondIndex==ak.firstIndex+1))
				     					       if(ak.depType.contains("nmod:")&&ak.firstWord.equalsIgnoreCase(aj.secondWord)&&(!(ak.secondWord.equals("$")||ak.secondWord.equals("%")||(aj.secondWord.equals("many")&&ak.depType.substring(ak.depType.indexOf(":")+1).equals("of"))||ak.secondTag.equals("CD")||al.secondWord.contains("following")||al.secondWord.equals(ak.secondWord)))){ 
				     					    	//pw.println(al.get(i).secondWord+" || "+al.get(i).firstWord.substring(0,al.get(i).firstWord.indexOf("-"))+"_"+al.get(j).secondWord+"_"+al.get(k).depType.substring(al.get(k).depType.indexOf("_"))+" || "+al.get(k).secondWord +" 		-----From Rule 5: ");
				     					    	  ElementAccessClass az=new ElementAccessClass(); 
				     					    	  if(k<myobj.length-2) az=(ElementAccessClass) myobj[k+2];
				     					    	     if(az.depType.contains("nmod:")&&az.firstWord.equals(ak.secondWord)){
				     					    	    	    System.out.println(al.secondWord+" || "+al.firstWord+"_"+aj.secondWord+"_"+ak.depType.substring(ak.depType.indexOf(":")+1)+" || "+ak.secondWord +" "+az.depType.substring(az.depType.indexOf(":")+1)+" "+az.secondWord+" 		-----From Rule 5c: ");
							     					        pw.println(al.secondWord+" || "+al.firstWord+"_"+aj.secondWord+"_"+ak.depType.substring(ak.depType.indexOf(":")+1)+" || "+ak.secondWord +" "+az.depType.substring(az.depType.indexOf(":")+1)+" "+az.secondWord+" 		-----From Rule 5c: ");
							     					        flag=1;
							     					       System.out.println(al.secondWord+" || "+al.firstWord+" || "+aj.secondWord+"_"+ak.depType.substring(ak.depType.indexOf(":")+1)+" "+ak.secondWord +" "+az.depType.substring(az.depType.indexOf(":")+1)+" "+az.secondWord+" 		-----From Rule 5c: Formated position ");
							     					        pw.println(al.secondWord+" || "+al.firstWord+" || "+aj.secondWord+"_"+ak.depType.substring(ak.depType.indexOf(":")+1)+" "+ak.secondWord +" "+az.depType.substring(az.depType.indexOf(":")+1)+" "+az.secondWord+" 		-----From Rule 5c: Formated position ");
							     				
				     					    	        }
				     					    		if(flag==0){ 
				     					    			//System.out.println(aza.depType+" "+aza.firstWord+" "+aza.secondWord+" "+aza.secondIndex);
				     					          System.out.println(al.secondWord+" || "+al.firstWord+"_"+aj.secondWord+"_"+ak.depType.substring(ak.depType.indexOf(":")+1)+" || "+ak.secondWord +" 		-----From Rule 5: ");
				     					          pw.println(al.secondWord+" || "+al.firstWord+"_"+aj.secondWord+"_"+ak.depType.substring(ak.depType.indexOf(":")+1)+" || "+ak.secondWord +" 		-----From Rule 5: ");
				     					        
				     					          ElementAccessClass am;     
						     				      for(int m=k+1;m<myobj.length-1;m++){
						     				    	 am=(ElementAccessClass) myobj[m];
						     				    	 if(am.depType.contains("appos")&&am.firstWord.equals(ak.secondWord)&&(am.firstIndex==ak.secondIndex)&&am.secondTag.contains("NN")){
						     				    		 System.out.println(al.secondWord+" || "+al.firstWord+"_"+aj.secondWord+"_"+ak.depType.substring(ak.depType.indexOf(":")+1)+" || "+am.secondWord +" 		-----From Rule 5: Extended");
						     					          pw.println(al.secondWord+" || "+al.firstWord+"_"+aj.secondWord+"_"+ak.depType.substring(ak.depType.indexOf(":")+1)+" || "+am.secondWord +" 		-----From Rule 5: Extended");
						     					        
						     				    	 }
						     				    	 }
				     					           
				     					          
				     					    		}
				     					} 
				     				  }
				     				}
				     				
				                }
				              }
				              // Rule 6
				              if(al.depType.equals("acl")&&al.firstTag.contains("NN")&&al.secondTag.contains("VB")&&(!(al.firstWord.equals("hours")||al.firstWord.equals("need")||al.firstWord.equals("%")||al.secondWord.isEmpty()||al.firstWord.equals("mg")||al.firstWord.equals("x")||al.firstWord.equals("+")||al.secondWord.equals("<")||al.secondWord.equals(">")||al.firstWord.equals("·")||al.secondWord.equals("·")))){
				            	  ElementAccessClass aj; int count=0;    
				            	  for(int j=i+1;j<myobj.length;j++){
				            		  aj=(ElementAccessClass) myobj[j];
				            		  if(aj.depType.contains("dobj")&&aj.firstWord.equalsIgnoreCase(al.secondWord)&&(!(aj.secondWord.isEmpty()||aj.secondWord.equals("·")||aj.secondWord.equals("%")))){  
				            			// pw.println(al.get(i).firstWord+" || "+al.get(i).secondWord+" || "+al.get(j).secondWord +" 				-----From Rule 6: "); 
				            			  System.out.println(al.firstWord+" || "+al.secondWord+" || "+aj.secondWord +" 		-----From Rule 6a: "); 
				            			 pw.println(al.firstWord+" || "+al.secondWord+" || "+aj.secondWord +" 		-----From Rule 6a: "); 
				            			
				            			 ElementAccessClass ap;
				            			 for(int p=j+1;p<myobj.length;p++){
				            			 ap=(ElementAccessClass) myobj[p];      // particular case to capture a particular relation
				            			if(ap.depType.equals("appos")&&ap.firstWord.equals(aj.secondWord)&&ap.secondTag.contains("NN")){   // particular case
					            			  ElementAccessClass ak=new ElementAccessClass();
					            			        if(p<myobj.length-2) 
					            			        	  ak=(ElementAccessClass) myobj[p+1];
					     				    	  if(ak.depType.contains("appos")&&ap.firstWord.contains(ak.firstWord)&&ak.secondWord.contains(ap.secondWord)){ 
					     				    		System.out.println(al.firstWord+" || "+al.secondWord+" || "+ak.secondWord+"		-----Rule 6a.b") ; 
					     				    		pw.println(al.firstWord+" || "+al.secondWord+" || "+ak.secondWord+"		-----Rule 6a.b") ; 
					     				    		  
					     				    	  }
					     				    	 
					     				    	
					            		  }
				            		  }
				            		  
				            		  }
				            		  if(aj.depType.contains("nmod:")&&aj.firstWord.equals(al.secondWord)&&(aj.firstIndex==al.secondIndex)&&(aj.firstTag.contains("VB"))&&(!(aj.firstWord.equals("followed")&&aj.depType.contains(":by")))){  // "by" was removed from nmod on 12/08/15  
				            			  ElementAccessClass af;  ElementAccessClass ag=new ElementAccessClass();
				            			  for(int f=j+1;f<myobj.length;f++){
				            				  af=(ElementAccessClass) myobj[f]; 
				            				   if(af.depType.equals("conj:and")&&af.firstWord.equals(aj.secondWord)&&(!(af.secondTag.equals("CD")||aj.secondTag.equals("CD")||aj.secondWord.equals("%")))){  
					            			// System.out.println(al.firstWord+" || "+al.secondWord+"_"+aj.depType.substring(aj.depType.indexOf(":")+1)+" || "+aj.secondWord+" and "+af.secondWord+"		-----Rule: 6c");
					            			// pw.println(al.firstWord+" || "+al.secondWord+"_"+aj.depType.substring(aj.depType.indexOf(":")+1)+" || "+aj.secondWord+" and "+af.secondWord+"		-----Rule: 6c");
					            			 count=1;
				            				   }  
				            			  }
				            			  if(j<myobj.length-2) ag=(ElementAccessClass) myobj[j+1];// System.out.println(ag.depType+" "+ag.firstWord+" "+ag.secondWord+" "+ag.secondIndex+" "+ aj.firstWord+" "+aj.secondWord+" "+aj.secondIndex);
				            			  if(ag.depType.equals("case")&&ag.secondTag.equals("IN")&&(ag.secondIndex==aj.secondIndex+1)){
			            					   ElementAccessClass ah;// System.out.println("TEST");
						            			  for(int h=j+2;h<myobj.length;h++){
						            				  ah=(ElementAccessClass) myobj[h];
						            				  if(ah.depType.contains("conj:and")&&ag.firstWord.equals(ah.secondWord)&&ah.firstTag.contains("NN")&&ah.secondTag.contains("NN")){
						            					  System.out.println(al.firstWord+" || "+al.secondWord+"_"+aj.depType.substring(aj.depType.indexOf(":")+1)+" || "+aj.secondWord+" "+ag.secondWord+" "+ah.firstWord+" and "+ah.secondWord+"		-----Rule: 6e");
									            			 pw.println(al.firstWord+" || "+al.secondWord+"_"+aj.depType.substring(aj.depType.indexOf(":")+1)+" || "+aj.secondWord+	" "+ag.secondWord+" "+ah.firstWord+" and "+ah.secondWord+"-----Rule: 6e");
									            			 count=1;
						            					  
						            				  }
						            				  
						            			  }
						            			 
			            				   }
				            			  System.out.println(al.firstWord+" ||"+al.secondWord+" "+aj.depType.substring(aj.depType.indexOf(":")+1)+" || "+aj.secondWord+"	----Rule 6c Extend");  // New Rule added on 8/02/2016
				            			  pw.println(al.firstWord+" ||"+al.secondWord+" "+aj.depType.substring(aj.depType.indexOf(":")+1)+" || "+aj.secondWord+"	----Rule 6c Extend");
				            			  ElementAccessClass ap;
				            			  for(int p=j+1;p<myobj.length-1;p++ ){
				            				  ap=(ElementAccessClass) myobj[p];
				            				  if(ap.depType.contains("appos")&&ap.firstWord.equals(aj.secondWord)&&(ap.firstIndex==aj.secondIndex)&&ap.secondTag.contains("NN")){
				            					  
				            					  System.out.println(al.firstWord+" ||"+al.secondWord+" "+aj.depType.substring(aj.depType.indexOf(":")+1)+" || "+ap.secondWord+"	----Rule 6c Extend 2");  // New Rule added on 8/02/2016
						            			  pw.println(al.firstWord+" ||"+al.secondWord+" "+aj.depType.substring(aj.depType.indexOf(":")+1)+" || "+ap.secondWord+"	----Rule 6c Extend 2");
 
				            				  }
				            			  }
					            		 }
				            		   if(aj.depType.contains("nmod:")&&aj.firstWord.equals(al.secondWord)&&(count==0)&&(!(aj.firstWord.equals("followed")||aj.secondWord.equals("%")||aj.secondWord.equals("±")||aj.secondTag.equals("CD")||aj.depType.substring(aj.depType.indexOf(":")+1).equals("tmod")))){   //"by" was removed from the nmod on 12/08/15
				            			// System.out.println(al.firstWord+" || "+al.secondWord+"_"+aj.depType.substring(aj.depType.indexOf(":")+1)+" || "+aj.secondWord+"		-----Rule: 6d");
				            			// pw.println(al.firstWord+" || "+al.secondWord+"_"+aj.depType.substring(aj.depType.indexOf(":")+1)+" || "+aj.secondWord+"		-----Rule: 6d");
				            			 ElementAccessClass ak;
				            			 for(int k=j+1;k<myobj.length;k++){
				            				 ak=(ElementAccessClass) myobj[k];
				            				 if(ak.depType.equals("nmod:of")&&ak.firstTag.contains("VB")&&(ak.firstIndex==aj.secondIndex+1)){
				            					 System.out.println(al.firstWord+" "+al.secondWord+" "+aj.depType.substring(aj.depType.indexOf(":")+1)+" "+aj.secondWord+" || "+ak.firstWord+" of "+" || "+ak.secondWord+"		-----Rule: 6d Extended");
						            			 pw.println(al.firstWord+" "+al.secondWord+" "+aj.depType.substring(aj.depType.indexOf(":")+1)+" "+aj.secondWord+" || "+ak.firstWord+" of "+" || "+ak.secondWord+"		-----Rule: 6d Extended");
	 
				            				 }
				            				 if(ak.depType.equals("nmod:such_as")&&ak.firstWord.equals(aj.secondWord)&&(ak.firstIndex==aj.secondIndex)){
				            					 System.out.println(al.firstWord+" || "+al.secondWord+"_"+aj.depType.substring(aj.depType.indexOf(":")+1)+" || "+aj.secondWord+" such_as "+ak.secondWord+"		-----Rule: 6dg");
						            			 pw.println(al.firstWord+" || "+al.secondWord+"_"+aj.depType.substring(aj.depType.indexOf(":")+1)+" || "+aj.secondWord+" such_as "+ak.secondWord+"		-----Rule: 6dg");
						            		 
				            				 }
				            			 }
				            		 }
				            		  
				            	  }
				            	 
				              }
				              // Rule 7  
				              if(al.depType.equals("appos")&&al.firstTag.contains("NN")&&al.secondTag.contains("NN")&&(!(al.firstWord.equals("month")||al.firstWord.equals("×")||al.firstWord.equals("%")))){
				            	  ElementAccessClass aj;
				            	  for(int j=i+1;j<myobj.length;j++){
				            		  aj=(ElementAccessClass) myobj[j];
				            		  if(aj.depType.equals("acl")&&aj.firstWord.equalsIgnoreCase(al.secondWord)&&(aj.firstIndex==al.secondIndex)){  
				            			  ElementAccessClass ak;
				     				      for(int k=j+1;k<myobj.length;k++){
				     				    	 ak=(ElementAccessClass) myobj[k];
				     					       if(ak.depType.contains("nmod:")&&ak.firstWord.equalsIgnoreCase(aj.secondWord)&&(ak.firstIndex==aj.secondIndex)&&ak.firstTag.contains("VB")&&(!((ak.firstWord.equals("followed")&&ak.depType.substring(ak.depType.indexOf(":")+1).equals("by"))||ak.secondWord.equals(" ±")||ak.firstWord.equals(">")))){
				     					    	   System.out.println(al.firstWord+" || "+ak.firstWord+"_"+ak.depType.substring(ak.depType.indexOf(":")+1)+" || "+ak.secondWord+"		----Rule 7");
				     					    	   pw.println(al.firstWord+" || "+ak.firstWord+"_"+ak.depType.substring(ak.depType.indexOf(":")+1)+" || "+ak.secondWord+"		----Rule 7");
				     					    	  ElementAccessClass am;
						     				      for(int m=k+1;m<myobj.length;m++){
						     				    	 am=(ElementAccessClass) myobj[m];  int count=0;
						     				    	 if(am.depType.contains("nmod:")&&am.firstWord.equals(ak.secondWord)&&(am.firstIndex==ak.secondIndex)&&(count==0)){
						     				    		 System.out.println(al.firstWord+" || "+ak.firstWord+"_"+ak.depType.substring(ak.depType.indexOf(":")+1)+" || "+ak.secondWord+" "+am.depType.substring(am.depType.indexOf(":")+1)+" "+am.secondWord +"		----Rule 7 Extended");
						     					    	   pw.println(al.firstWord+" || "+ak.firstWord+"_"+ak.depType.substring(ak.depType.indexOf(":")+1)+" || "+ak.secondWord+" "+am.depType.substring(am.depType.indexOf(":")+1)+" "+am.secondWord +"		----Rule 7 Extended");
						     				    	 }
						     				    	 if(am.depType.contains("conj:and")&&am.firstWord.equals(ak.secondWord)&&(am.firstIndex==ak.secondIndex)&&am.secondTag.contains("NN")){
						     				    		  System.out.println(al.firstWord+" || "+ak.firstWord+"_"+ak.depType.substring(ak.depType.indexOf(":")+1)+" || "+ak.secondWord+" "+am.depType.substring(am.depType.indexOf(":")+1)+" "+am.secondWord +"		----Rule 7 Extended 2");
						     					    	   pw.println(al.firstWord+" || "+ak.firstWord+"_"+ak.depType.substring(ak.depType.indexOf(":")+1)+" || "+ak.secondWord+" "+am.depType.substring(am.depType.indexOf(":")+1)+" "+am.secondWord +"		----Rule 7 Extended 2");
						     					    	  
 
						     				    	 }
						     				    	 }
				     					    	     
				     					       }
				     				      }
				            		  }
				            		   if(aj.depType.contains("case")){
				            			   ElementAccessClass ak;
				            			   for(int k=j+1;k<myobj.length;k++){
					     				    	 ak=(ElementAccessClass) myobj[k]; 
					     				    	 if(ak.depType.contains("nmod")&&aj.secondWord.equals(ak.depType.substring(ak.depType.indexOf(":")+1))&&(aj.secondIndex==ak.firstIndex+1)&&al.secondWord.equals(ak.firstWord)&&(aj.firstWord.equals(ak.secondWord))&&(ak.firstTag.contains("VB"))&&(!((ak.depType.contains(":of")&&ak.firstWord.equals("year"))||ak.firstWord.equals("%")||ak.secondWord.equals("japan")))){  // Extra  addded  (ak.firstTag.contains("VB")) on 10/08/2015
					     				    			 System.out.println(al.firstWord+" || "+ak.firstWord+"_"+aj.secondWord+" || "+ak.secondWord+"		-----Rule 7b");
					     				    	 		pw.println(al.firstWord+" || "+ak.firstWord+"_"+aj.secondWord+" || "+ak.secondWord+"		-----Rule 7b");
					     				    	 }
					     				    	
					     				    	 if(ak.depType.contains("nmod")&&(aj.secondIndex==ak.firstIndex+1)&&aj.secondWord.contains(ak.depType.substring(ak.depType.indexOf(":")+1))&&ak.secondWord.equals(aj.firstWord)&&ak.firstTag.contains("VB")&&(!((ak.depType.contains(":by")&&ak.firstWord.equals("followed"))||ak.secondWord.equals("%")))){
					     				    		  ElementAccessClass am;
					     				    		 /*for(int m=k+1; m<myobj.length;m++){
					     				    			am=(ElementAccessClass) myobj[m];
					     				    			if(am.depType.equals("conj:and")&&ak.secondWord.equals(am.firstWord)&&(!(am.firstWord.equals(am.secondWord)))){
					     				    				System.out.println(al.firstWord+" || "+ak.firstWord+"_"+aj.secondWord+" || "+ak.secondWord+"_and_"+am.secondWord+"		-----Rule 7d");
						     				    	 		pw.println(al.firstWord+" || "+ak.firstWord+"_"+aj.secondWord+" || "+ak.secondWord+"_and_"+am.secondWord+"		-----Rule 7d");
					     				    			}
					     				    			
					     				    		 }*/
					     				    	 }
					     				    	else if(ak.depType.contains("nmod:")&&(aj.secondIndex==ak.firstIndex+1)&&aj.secondWord.contains(ak.depType.substring(ak.depType.indexOf(":")+1))&&ak.secondWord.equals(aj.firstWord)&&ak.firstTag.contains("VB")&&(!(ak.depType.contains(":by")&&ak.firstWord.equals("followed")))){
					     				    		  System.out.println(al.firstWord+" || "+ak.firstWord+"_"+aj.secondWord+" || "+ak.secondWord+"		-----Rule 7c");
					     				    	 		pw.println(al.firstWord+" || "+ak.firstWord+"_"+aj.secondWord+" || "+ak.secondWord+"		-----Rule 7c");
					     				    			}
				            			   }
				            		   }
				            		  
				            	  }  
				            	  
				              }   //Rule 7 ends
				              // Rule 8
				              if(al.depType.contains("nsubj")&&al.firstTag.contains("VB")&&al.secondTag.contains("NN")){
				            	  ElementAccessClass aj;
				            	  for(int j=i+1;j<myobj.length;j++){
				            		  aj=(ElementAccessClass) myobj[j];  ElementAccessClass ap;
				            		  if(aj.depType.equals("mark")&&(aj.secondIndex==al.firstIndex+1)){
				            			  ap=(ElementAccessClass) myobj[j+1];
				            			  if(ap.depType.equals("xcomp")&&ap.firstWord.equals(al.firstWord)){
				            				  ElementAccessClass ak;
				            				
				            				  for(int k=j+1; k<myobj.length;k++){
				            					  ak=(ElementAccessClass) myobj[k]; 
				            					  
						     				    	 if(ak.depType.contains("dobj")&&ak.firstWord.equals(ap.secondWord)){
						     				    		 ElementAccessClass am;
						     				    		//System.out.println("MAP");
						     				    		 for(int m=k+1;m<myobj.length;m++){
						     				    			 am=(ElementAccessClass) myobj[m];
						     				    			
						     				    		 if(am.depType.contains("nmod:")&&am.firstWord.equals(ak.secondWord)){
						     				    			  ElementAccessClass an;
						     				    			
						     				    			 for(int n=m+1;n<myobj.length;n++){
						     				    				   an=(ElementAccessClass) myobj[n];  ElementAccessClass eq=new ElementAccessClass();
						     				    				   if(an.depType.contains("nmod:")&&an.firstWord.equals(am.secondWord)){
				             								          System.out.println(al.secondWord+"_"+al.firstWord+"_"+aj.secondWord+" "+aj.firstWord+" "+ak.secondWord+" || "+am.depType.substring(am.depType.indexOf(":")+1)+"_"+am.secondWord+"_"+an.depType.substring(an.depType.indexOf(":")+1)+" || "+an.secondWord+"		----Rule: 8");
				             								           pw.println(al.secondWord+"_"+al.firstWord+"_"+aj.secondWord+" "+aj.firstWord+" "+ak.secondWord+" || "+am.depType.substring(am.depType.indexOf(":")+1)+"_"+am.secondWord+"_"+an.depType.substring(an.depType.indexOf(":")+1)+" || "+an.secondWord+"		----Rule: 8");
				             								          if(n<myobj.length-2)   eq=(ElementAccessClass) myobj[n+1];
				             								           if(eq.depType.equals("appos")&&an.secondWord.equals(eq.firstWord)){
				             								        	  System.out.println(al.secondWord+"_"+al.firstWord+"_"+aj.secondWord+" "+aj.firstWord+" "+ak.secondWord+" || "+am.depType.substring(am.depType.indexOf(":")+1)+"_"+am.secondWord+"_"+an.depType.substring(an.depType.indexOf(":")+1)+" || "+eq.secondWord+"		----Rule: 8b");
					             								           pw.println(al.secondWord+"_"+al.firstWord+"_"+aj.secondWord+" "+aj.firstWord+" "+ak.secondWord+" || "+am.depType.substring(am.depType.indexOf(":")+1)+"_"+am.secondWord+"_"+an.depType.substring(an.depType.indexOf(":")+1)+" || "+eq.secondWord+"		----Rule: 8b");
				             								           
				             								           }
						     				    				      }
						     				    				 }
				             							        } 
						     				    		 }
						     				    		 
						     				    	 }
				            					  
				            				  }
				            			  }
				            		  }
				            		  
				            	  }
				              }  //Rule 8 ends 
				             // Rule 9 
				              if(al.depType.contains("nsubj")&&al.firstTag.contains("VBN")&&al.secondTag.contains("NN")&&(!(al.secondWord.contains("following")||al.secondWord.equals("%")||al.secondWord.equals("100")))){
				            	  ElementAccessClass aj;
				            	  for(int j=i+1;j<myobj.length;j++){
				            		  aj=(ElementAccessClass) myobj[j];
				            		  if(aj.depType.equals("case")&&(!(aj.secondWord.equals("than")))){
				            			  ElementAccessClass ak;    //System.out.println(aj.firstWord+" "+aj.secondWord);
				            			  for(int k=j+1;k<myobj.length;k++){
						            		  ak=(ElementAccessClass) myobj[k];
						            		   if(ak.depType.contains("nmod:")&&ak.firstWord.equals(al.firstWord)&&ak.secondWord.equals(aj.firstWord)&&ak.depType.substring(ak.depType.indexOf(":")+1).equals(aj.secondWord)&&(!(ak.secondWord.equals("%")))){
						            			   ElementAccessClass am; 
						            			   for(int m=k+1;m<myobj.length;m++){  
						            				   am=(ElementAccessClass) myobj[m]; ElementAccessClass an;
						            				   if(am.depType.equals("cc")&&(!(ak.secondWord.equals(am.firstWord)))){
						            					   an=(ElementAccessClass)myobj[m+1];
						            					   if(an.depType.equals("case")&&(an.secondIndex==am.secondIndex+1)){
						            						   ElementAccessClass ap;
						            						   for(int p=m+2;p<myobj.length;p++){
						            							   ap=(ElementAccessClass) myobj[p];
						            							   if(ap.depType.equals("nummod")&&ap.firstWord.equals(an.firstWord)&&(ap.secondIndex==an.secondIndex+1)){
						            								   System.out.println(al.secondWord+ " || "+al.firstWord+"_"+aj.secondWord+" || "+ak.secondWord+" "+am.firstWord+" "+am.secondWord+" "+an.secondWord+" "+ap.secondWord+" "+ap.firstWord+"		---Rule: 9");
						            								   pw.println(al.secondWord+ " || "+al.firstWord+"_"+aj.secondWord+" || "+ak.secondWord+" "+am.firstWord+" "+am.secondWord+" "+an.secondWord+" "+ap.secondWord+" "+ap.firstWord+"		---Rule: 9");
						            							   }
						            						   }
						            					   }
						            					   
						            				   }
						            				   if(am.depType.contains("nmod:")&&am.firstWord.equals(ak.secondWord)&&(am.firstIndex==ak.secondIndex)){
						            					   ElementAccessClass as, at,au;
						            					   if(((m+3)<myobj.length)){
						            					   as=(ElementAccessClass) myobj[m+1];
							            				   if(as.depType.equals("cc")&&(as.secondIndex==am.secondIndex+1)){
							            					   at=(ElementAccessClass)myobj[m+2];
							            					   if(at.depType.equals("case")&&(at.secondIndex==as.secondIndex+1)){
							            						   au =(ElementAccessClass)myobj[m+3];
							            							   if(au.depType.equals("nummod")&&au.firstWord.equals(at.firstWord)&&(au.secondIndex==at.secondIndex+1)){
							            								   System.out.println(al.secondWord+ " || "+al.firstWord+"_"+aj.secondWord+" || "+am.firstWord+" "+am.depType.substring(am.depType.indexOf(":")+1)+" "+am.secondWord+" "+as.secondWord+" "+at.secondWord+" "+au.secondWord+" "+au.firstWord+" 	----Rule 9 Extended");
							            								   pw.println(al.secondWord+ " || "+al.firstWord+"_"+aj.secondWord+" || "+am.firstWord+" "+am.depType.substring(am.depType.indexOf(":")+1)+" "+am.secondWord+" "+as.secondWord+" "+at.secondWord+" "+au.secondWord+" "+au.firstWord+" 	----Rule 9 Extended");
							            								   
							            							   }
							            						   
							            					   }
							            				   } 
							            				   }
						            				   }
						            				   
						            			   }
						            			 //  System.out.println(al.secondWord+ " || "+al.firstWord+"_"+aj.secondWord+" || "+ak.secondWord+"		---Rule: 9");
						            			   
						            		   
						            		   }
						            		/*   if(ak.depType.contains("nmod:")&&ak.secondWord.equals(aj.firstWord)&&ak.depType.substring(ak.depType.indexOf(":")+1).equals(aj.secondWord)&&ak.firstTag.equals("VBN")&&(aj.secondIndex==ak.firstIndex+1)&&(!(ak.secondWord.equals("%")))){            //  The portion  "&&((ak.depType.substring(ak.depType.indexOf(":")+1).equals("in")))"  has been cut out  
						            			   ElementAccessClass am;   
						            			   for(int m=k+1;m<myobj.length;m++){
						            				   am=(ElementAccessClass) myobj[m];  
						            				   if(am.depType.contains("nmod:")&&am.firstWord.equals(ak.secondWord)&&(am.secondIndex-am.firstIndex<4)&&(!(am.secondWord.equals("%")))){//System.out.println("TEST");
						            					   System.out.println(al.secondWord+ " || "+al.firstWord+"_"+aj.secondWord+" || "+am.firstWord+" "+am.depType.substring(am.depType.indexOf(":")+1)+" "+am.secondWord+"		---Rule: 9b");
						            					   pw.println(al.secondWord+ " || "+al.firstWord+"_"+aj.secondWord+" || "+am.firstWord+" "+am.depType.substring(am.depType.indexOf(":")+1)+" "+am.secondWord+"		---Rule: 9b");

						            				   }
						            				   
						            			   }
	 					            			   
								            		}*/
						            		  }
				            		  }
				            	  }
				              }
				              // Rule 10
				              if(al.depType.contains("nsubj")&&al.firstTag.equals("JJ")&&al.secondTag.contains("NN")&&(!(al.secondWord.equals("%")||al.secondWord.equals("¿")))){  // Rule 10
				            	  ElementAccessClass aj; 
				            	  for(int j=i+1;j<myobj.length;j++){
				            		  aj=(ElementAccessClass) myobj[j];  
				            		  if(aj.depType.contains("nmod:")&&aj.firstWord.equals(al.secondWord)){
				            			  ElementAccessClass ak;
				            			  for(int k=j+1;k<myobj.length;k++){
				            				  ak=(ElementAccessClass) myobj[k];  
				            				  if(ak.depType.equals("cop")&&ak.firstWord.equals(al.firstWord)&&(!(ak.secondWord.equals("be")||(ak.secondWord.equals("is")&&aj.depType.substring(aj.depType.indexOf(":")+1).equals("of"))||(ak.secondWord.equals("were")&&aj.depType.substring(aj.depType.indexOf(":")+1).equals("of"))||(al.firstWord.equals("due")&&aj.depType.contains(":of"))))){  //System.out.println("TEST  10");
				            					 // System.out.println(al.secondWord+" || "+ak.secondWord+"_"+al.firstWord+"_"+aj.depType.substring(aj.depType.indexOf(":")+1)+" || "+aj.secondWord+"		---Rule 10:");
				            					 // pw.println(al.secondWord+" || "+ak.secondWord+"_"+al.firstWord+"_"+aj.depType.substring(aj.depType.indexOf(":")+1)+" || "+aj.secondWord+"		---Rule 10:");
				            					   					  
				            					  System.out.println(al.secondWord+" "+aj.depType.substring(aj.depType.indexOf(":")+1)+" "+aj.secondWord+" || "+ak.secondWord+" || "+al.firstWord+"		---Rule 10T'':");
				            					  pw.println(al.secondWord+" "+aj.depType.substring(aj.depType.indexOf(":")+1)+" "+aj.secondWord+" || "+ak.secondWord+" || "+al.firstWord+"		---Rule 10T'':");
				            					  ElementAccessClass am;
				            					  for(int m=k+1;m<myobj.length;m++){
				            						  am=(ElementAccessClass) myobj[m];
				            						  if(am.depType.contains("nmod:")&&am.firstWord.equals(al.firstWord)&&(am.firstIndex==al.firstIndex)&&(!(am.secondWord.equals("%")))){
				            							  System.out.println(al.secondWord+" "+aj.depType.substring(aj.depType.indexOf(":")+1)+" "+aj.secondWord+" || "+ak.secondWord+" || "+al.firstWord+" "+ am.depType.substring(am.depType.indexOf(":")+1)+" "+am.secondWord+"		---Rule 10d:");
						            					  pw.println(al.secondWord+" "+aj.depType.substring(aj.depType.indexOf(":")+1)+" "+aj.secondWord+" || "+ak.secondWord+" || "+al.firstWord+" "+ am.depType.substring(am.depType.indexOf(":")+1)+" "+am.secondWord+"		---Rule 10d:");

				            						  }
				            						  
				            					  }

				            				  }
				            				  
				            			  }
				            		  }
				            		if(aj.depType.contains("nmod:")&&aj.firstWord.equals(al.firstWord)&&(aj.secondIndex-aj.firstIndex<5)&&(!(aj.secondWord.equals("%")||aj.secondWord.equals("+")||aj.depType.contains(":npmod")||aj.secondTag.equals("CD")))){
				            			 System.out.println(al.secondWord+" || "+aj.firstWord+"_"+aj.depType.substring(aj.depType.indexOf(":")+1)+" || "+aj.secondWord+"		---Rule 10b:");
		            					  pw.println(al.secondWord+" || "+aj.firstWord+"_"+aj.depType.substring(aj.depType.indexOf(":")+1)+" || "+aj.secondWord+"		---Rule 10b:");
		            					  ElementAccessClass ak;
		            					  for(int k=j+1;k<myobj.length;k++){
		            						  ak=(ElementAccessClass) myobj[k]; //System.out.println("APAPAP");
		            						  if(ak.depType.contains("nmod:")&&ak.firstWord.equals(aj.secondWord)&&(ak.firstIndex==aj.secondIndex)){
		            							  System.out.println(al.secondWord+" || "+aj.firstWord+"_"+aj.depType.substring(aj.depType.indexOf(":")+1)+" || "+aj.secondWord+" "+ak.depType.substring(ak.depType.indexOf(":")+1)+" "+ak.secondWord+"		---Rule 10b Extended:");
		    	            					  pw.println(al.secondWord+" || "+aj.firstWord+"_"+aj.depType.substring(aj.depType.indexOf(":")+1)+" || "+aj.secondWord+" "+ak.depType.substring(ak.depType.indexOf(":")+1)+" "+ak.secondWord+"		---Rule 10b Extended:");
		    	            					
		            						  }
		            					  }
				            		}
				            		if(aj.depType.contains("nmod:")&&aj.firstWord.equals(al.secondWord)&&(aj.firstIndex==al.secondIndex)&&(aj.secondIndex-aj.firstIndex<5)){
				            			ElementAccessClass ak;  //System.out.println("Hello TEST");
				            			for(int k=j+1;k<myobj.length;k++){
				            				ak=(ElementAccessClass) myobj[k];
				            				if(ak.depType.contains("nmod:")&&ak.firstWord.equals(al.firstWord)&&(ak.secondIndex-ak.firstIndex<5)&&(!(ak.secondWord.equals("+")||ak.secondTag.equals("CD")||ak.secondTag.endsWith("±")))){
						            			 System.out.println(al.secondWord+" "+aj.depType.substring(aj.depType.indexOf(":")+1)+" "+aj.secondWord+" || "+ak.firstWord+"_"+ak.depType.substring(ak.depType.indexOf(":")+1)+" || "+ak.secondWord+"		---Rule 10c:");
				            					  pw.println(al.secondWord+" "+aj.depType.substring(aj.depType.indexOf(":")+1)+" "+aj.secondWord+" || "+ak.firstWord+"_"+ak.depType.substring(ak.depType.indexOf(":")+1)+" || "+ak.secondWord+"		---Rule 10c:");
				            					 
				            				}
				            			}
				            			
				            		}
				            	  }
				              }
				              //Rule 11
				              if(al.depType.contains("nsubj")&&al.firstTag.contains("VB")&&al.secondTag.contains("NN")&&(!(al.secondWord.equals("%")))){  // Rule 11
				            	  ElementAccessClass aj;   int count=0;
				            	  for(int j=i+1;j<myobj.length;j++){
				            		  aj=(ElementAccessClass) myobj[j]; 
				            		  if(aj.depType.contains("nmod:")&&aj.firstWord.equals(al.secondWord)){
				            			   ElementAccessClass ap; 
				            			   for(int p=j+1;p<myobj.length;p++){
				            				   ap=(ElementAccessClass) myobj[p]; 
				            			       if(ap.depType.equals("aux")&&ap.firstWord.equals(al.firstWord)&&ap.secondTag.equals("MD")){
					            			     ElementAccessClass ak;   
					            			      for(int k=p+1;k<myobj.length;k++){
					            				  ak=(ElementAccessClass) myobj[k]; 
					            				  if(ak.depType.contains("nmod:")&&ak.firstWord.equals(al.firstWord)&&(!(ak.depType.contains(":such_as")))){
					            					  System.out.println(al.secondWord+" "+aj.depType.substring(aj.depType.indexOf(":")+1)+" "+aj.secondWord+" || "+ap.secondWord+" "+ap.firstWord+" "+ak.depType.substring(ak.depType.indexOf(":")+1)+" || "+ak.secondWord+ "	---Rule 11b:");
					            					  pw.println(al.secondWord+" "+aj.depType.substring(aj.depType.indexOf(":")+1)+" "+aj.secondWord+" || "+ap.secondWord+" "+ap.firstWord+" "+ak.depType.substring(ak.depType.indexOf(":")+1)+" || "+ak.secondWord+ "	---Rule 11b:");
					            					  count=1;
					            				  			}
					            				  
					            				  ElementAccessClass ar;
					            				  for(int r= k+1; r<myobj.length-1;r++){
					            					  ar=(ElementAccessClass) myobj[r]; 
					            					  if(ar.depType.equals("acl")&&ar.firstWord.equalsIgnoreCase(ak.secondWord)&&(ar.firstIndex==ak.secondIndex)&&ar.secondTag.contains("VB")){
					            						  ElementAccessClass as;
					            						  for(int s= r+1; s<myobj.length;s++){
					            							  as=(ElementAccessClass) myobj[s]; 
					            							  if(as.depType.contains("nmod:")&&as.firstWord.equalsIgnoreCase(ar.secondWord)&&(as.firstIndex==ar.secondIndex)&&as.secondTag.contains("NN")){
					            								  System.out.println(al.secondWord+" "+aj.depType.substring(aj.depType.indexOf(":")+1)+" "+aj.secondWord+" || "+ap.secondWord+" "+ap.firstWord+" "+ak.depType.substring(ak.depType.indexOf(":")+1)+" || "+ak.secondWord+" "+as.firstWord+" "+as.depType.substring(as.depType.indexOf(":")+1)+" "+ as.secondWord+"	---Rule 11c:");           // New rule added on 11/01/2016
								            					  pw.println(al.secondWord+" "+aj.depType.substring(aj.depType.indexOf(":")+1)+" "+aj.secondWord+" || "+ap.secondWord+" "+ap.firstWord+" "+ak.depType.substring(ak.depType.indexOf(":")+1)+" || "+ak.secondWord+ " "+as.firstWord+" "+as.depType.substring(as.depType.indexOf(":")+1)+" "+ as.secondWord+"	---Rule 11c:");
								            					 					  }
					            						  }
					            						  
					            					  }
					            					  
					            				  }
					            			    }
					            			     
					            		  }
				            		  } 
				            		  }
				            		  /*if(aj.depType.equals("aux")&&aj.firstWord.equals(al.firstWord)&&aj.secondTag.equals("MD")&&(count==0)){
				            			  ElementAccessClass ak;  //System.out.println("HEllo Test");
				            			  for(int k=j+1;k<myobj.length;k++){
				            				  ak=(ElementAccessClass) myobj[k];
				            				  if(ak.depType.contains("nmod:")&&ak.firstWord.equals(al.firstWord)&&(!(ak.secondTag.equals("RB")||ak.depType.contains(":tmod")))){
				            					  System.out.println(al.secondWord+" || "+aj.secondWord+" "+aj.firstWord+" "+ak.depType.substring(ak.depType.indexOf(":")+1)+" || "+ak.secondWord+ "	---Rule 11:");
				            					  pw.println(al.secondWord+" || "+aj.secondWord+" "+aj.firstWord+" "+ak.depType.substring(ak.depType.indexOf(":")+1)+" || "+ak.secondWord+ "	---Rule 11:");

				            				  }
				            				  }
				            		  } // rule 11 closed
*/				            	  }
				              }
				             // Rule 12 
				              if(al.depType.equals("nmod:by")&&al.firstTag.contains("NN")&&al.secondTag.contains("NN")){
				            	  ElementAccessClass aj;   int count=0;
				            	  for(int j=i+1;j<myobj.length;j++){
				            		  aj=(ElementAccessClass) myobj[j]; 
				            		  if(aj.depType.equals("aux")&&aj.firstTag.contains("VB")&&aj.secondTag.contains("MD")){
				            			  ElementAccessClass ak;   
			            			      for(int k=j+1;k<myobj.length;k++){
			            				     ak=(ElementAccessClass) myobj[k]; 
			            				     if(ak.depType.contains("nmod:")&&ak.firstWord.equals(aj.firstWord)&&(ak.firstIndex==aj.firstIndex)&&ak.secondTag.contains("NN")){
			            				    	 ElementAccessClass am;  
						            			   for(int m=k+1;m<myobj.length;m++){
						            				   am=(ElementAccessClass) myobj[m]; 
						            				   if(am.depType.contains("nmod:")&&am.firstTag.contains("VB")&&(am.firstIndex==ak.secondIndex+1)&&am.secondTag.contains("NN")){
						            					   System.out.println(al.firstWord+" "+al.depType.substring(al.depType.indexOf(":")+1)+" "+al.secondWord+" || "+aj.secondWord+" "+aj.firstWord+" "+ak.depType.substring(ak.depType.indexOf(":")+1)+" || "+ak.secondWord+" "+am.firstWord+" "+am.depType.substring(am.depType.indexOf(":")+1)+" "+am.secondWord+"		---Rule 12");
						            					   pw.println(al.firstWord+" "+al.depType.substring(al.depType.indexOf(":")+1)+" "+al.secondWord+" || "+aj.secondWord+" "+aj.firstWord+" "+ak.depType.substring(ak.depType.indexOf(":")+1)+" || "+ak.secondWord+" "+am.firstWord+" "+am.depType.substring(am.depType.indexOf(":")+1)+" "+am.secondWord+"		---Rule 12");

						            				   }
						            			   }
			            				    	 
			            				    	 
			            				     }
			            				    
			            			          }
				            			  
				            		  }
				            	  }
				              }
				             // Rule 13
				             if(al.depType.contains("nsubj")&&al.firstTag.contains("VB")&&al.secondTag.contains("NN")){
				            	 ElementAccessClass aj;
				            	 for(int j=i+1;j<myobj.length;j++){
				            		 aj=(ElementAccessClass) myobj[j]; ElementAccessClass az=new ElementAccessClass(); 
				            		 if(aj.depType.contains("nmod:")&&aj.firstWord.equals(al.secondWord)&&(al.secondIndex==aj.firstIndex)){
				            			 ElementAccessClass ak;   if(j<myobj.length-1) az=(ElementAccessClass) myobj[j+1];
				             				for(int k=j+1;k<myobj.length;k++){
				             					ak=(ElementAccessClass) myobj[k];   
				             					if(ak.depType.equalsIgnoreCase("case")&&ak.secondTag.equals("IN")&&(!(al.firstWord.equals("followed")&&ak.secondWord.equals("by")||ak.firstWord.isEmpty()))){ 
				             						ElementAccessClass am;    
				             						for(int m=k+1;m<myobj.length;m++){
				             							am=(ElementAccessClass) myobj[m]; 
				             							if(am.depType.contains("nmod:")&&am.firstWord.equalsIgnoreCase(al.firstWord)&&(am.firstIndex==ak.secondIndex-1)&&(am.depType.contains(ak.secondWord))&&(!(am.secondWord.equals("-RSB-")||am.secondWord.equals("%")||am.depType.contains(":tmod")||am.secondWord.equals("±")||am.secondWord.isEmpty()||am.secondTag.equals("CD")))){
				             								    if(az.depType.equals("case")&&(az.secondIndex==aj.secondIndex+1)&&(az.firstIndex==az.secondIndex+1)){
				             									    System.out.println(al.secondWord+" "+aj.depType.substring(aj.depType.indexOf(":")+1)+" "+aj.secondWord+" "+ az.secondWord+" "+az.firstWord+" || "+al.firstWord+"_"+am.depType.substring(am.depType.indexOf(":")+1)+" || "+am.secondWord+"		-----From Rule 13b:  ");
						             					 	        pw.println(al.secondWord+" "+aj.depType.substring(aj.depType.indexOf(":")+1)+" "+aj.secondWord+" "+ az.secondWord+" "+az.firstWord+" || "+al.firstWord+"_"+am.depType.substring(am.depType.indexOf(":")+1)+" || "+am.secondWord+"		-----From Rule 13b:  ");
						             					 	         ElementAccessClass at; 
						             					 	         for(int t=m+1;t<myobj.length;t++){
						             					 	        	 at=(ElementAccessClass) myobj[t];
						             					 	        	 if(at.depType.equals("appos")&&at.firstWord.equals(am.secondWord)&&(at.firstIndex==am.secondIndex)&&at.secondTag.contains("NN")&&(!(at.secondWord.equals("%")))){
						             					 	        		System.out.println(al.secondWord+" "+aj.depType.substring(aj.depType.indexOf(":")+1)+" "+aj.secondWord+" "+ az.secondWord+" "+az.firstWord+" || "+al.firstWord+"_"+am.depType.substring(am.depType.indexOf(":")+1)+" || "+at.secondWord+"		-----From Rule 13d:  ");
								             					 	        pw.println(al.secondWord+" "+aj.depType.substring(aj.depType.indexOf(":")+1)+" "+aj.secondWord+" "+ az.secondWord+" "+az.firstWord+" || "+al.firstWord+"_"+am.depType.substring(am.depType.indexOf(":")+1)+" || "+at.secondWord+"		-----From Rule 13d:  ");
	 
						             					 	        		 
						             					 	        	 }
						             					 	         }
						             					 	        ElementAccessClass aw=new ElementAccessClass();
						             					 	        if(m<myobj.length-2) aw=(ElementAccessClass) myobj[m+1];
						             					 	           //System.out.println(aw.depType+" "+aw.firstWord+ " "+aw.secondWord);
						             					 	           if(aw.depType.equals("case")&&aw.secondWord.equals("including")){
						             					 	        	 ElementAccessClass ap; 
						             								 for(int p=m+1; p<myobj.length;p++){
						             									 ap=(ElementAccessClass) myobj[p]; 
						             									 if(ap.depType.contains("nmod:including")&&am.secondWord.equals(ap.firstWord)){
						             										 
						             										System.out.println(al.secondWord+" "+aj.depType.substring(aj.depType.indexOf(":")+1)+" "+aj.secondWord+" "+ az.secondWord+" "+az.firstWord+" || "+al.firstWord+"_"+am.depType.substring(am.depType.indexOf(":")+1)+" || "+ap.secondWord+"		-----From Rule 13c:  ");
								             					 	        pw.println(al.secondWord+" "+aj.depType.substring(aj.depType.indexOf(":")+1)+" "+aj.secondWord+" "+ az.secondWord+" "+az.firstWord+" || "+al.firstWord+"_"+am.depType.substring(am.depType.indexOf(":")+1)+" || "+ap.secondWord+"		-----From Rule 13c:  ");
								             					 	        
						             									 }
						             								 }
						             					 	           }
						             					 	           
				             								   }else {
				             									  ElementAccessClass ag=new ElementAccessClass();
				             									 if(m<myobj.length-2) ag=(ElementAccessClass) myobj[m+1];// System.out.println(ag.depType+" "+ag.firstWord+" "+ag.secondWord+" "+ag.secondIndex+" "+ aj.firstWord+" "+aj.secondWord+" "+aj.secondIndex);
				           			            			    if(ag.depType.equals("case")&&ag.secondTag.equals("IN")&&(ag.secondIndex==am.secondIndex+1)){
				           		            					   ElementAccessClass ah;// System.out.println("TEST");
				           					            			  for(int h=j+2;h<myobj.length;h++){
				           					            				  ah=(ElementAccessClass) myobj[h];
				           					            				  if(ah.depType.contains("conj:and")&&ag.firstWord.equals(ah.secondWord)&&ah.firstTag.contains("NN")&&ah.secondTag.contains("NN")){
				           					            					 System.out.println(al.secondWord+" "+aj.depType.substring(aj.depType.indexOf(":")+1)+" "+aj.secondWord+ " || "+al.firstWord+"_"+am.depType.substring(am.depType.indexOf(":")+1)+" || "+am.secondWord+" "+ag.secondWord+" "+ah.firstWord+" and "+ah.secondWord+"		-----From Rule 13e:  ");
				 				             					 	        pw.println(al.secondWord+" "+aj.depType.substring(aj.depType.indexOf(":")+1)+" "+aj.secondWord+" || "+al.firstWord+"_"+am.depType.substring(am.depType.indexOf(":")+1)+" || "+am.secondWord+" "+ag.secondWord+" "+ah.firstWord+" and "+ah.secondWord+"		-----From Rule 13e:  ");

				           					            				  }
				           					            				  
				           					            			  }
				           					            			 
				           		            				   }else{
				             								   System.out.println(al.secondWord+" "+aj.depType.substring(aj.depType.indexOf(":")+1)+" "+aj.secondWord+ " || "+al.firstWord+"_"+am.depType.substring(am.depType.indexOf(":")+1)+" || "+am.secondWord+"		-----From Rule 13a:  ");
					             					 	        pw.println(al.secondWord+" "+aj.depType.substring(aj.depType.indexOf(":")+1)+" "+aj.secondWord+" || "+al.firstWord+"_"+am.depType.substring(am.depType.indexOf(":")+1)+" || "+am.secondWord+"		-----From Rule 13a:  ");
					             					 	        ElementAccessClass ap;
					             					 	        for(int p=m+1;p<myobj.length;p++){
					             					 	        	ap=(ElementAccessClass) myobj[p];
					             					 	        	if(ap.depType.contains("appos")&&ap.firstWord.equals(am.secondWord)&&(ap.firstIndex==am.secondIndex)&&ap.secondTag.contains("NN")&&(!(ap.secondWord.equals("%")))){
					             					 	        		System.out.println(al.secondWord+" "+aj.depType.substring(aj.depType.indexOf(":")+1)+" "+aj.secondWord+ " || "+al.firstWord+"_"+am.depType.substring(am.depType.indexOf(":")+1)+" || "+ap.secondWord+"		-----From Rule 13a:  Extended");
							             					 	        pw.println(al.secondWord+" "+aj.depType.substring(aj.depType.indexOf(":")+1)+" "+aj.secondWord+" || "+al.firstWord+"_"+am.depType.substring(am.depType.indexOf(":")+1)+" || "+ap.secondWord+"		-----From Rule 13a:  Extended");

					             					 	        	}
					             					 	        	
					             					 	        	if(ap.depType.contains("nmod:")&&ap.firstWord.equals(am.secondWord)&&(ap.firstIndex==am.secondIndex)&&ap.secondTag.contains("NN")){
					             					 	        		System.out.println(al.secondWord+" "+aj.depType.substring(aj.depType.indexOf(":")+1)+" "+aj.secondWord+ " || "+al.firstWord+"_"+am.depType.substring(am.depType.indexOf(":")+1)+" || "+ap.firstWord+" "+ ap.depType.substring(ap.depType.indexOf(":")+1)+" "+ap.secondWord+"		-----From Rule 13a:  Extended 2");
							             					 	        pw.println(al.secondWord+" "+aj.depType.substring(aj.depType.indexOf(":")+1)+" "+aj.secondWord+" || "+al.firstWord+"_"+am.depType.substring(am.depType.indexOf(":")+1)+" || "+ap.firstWord+" "+ ap.depType.substring(ap.depType.indexOf(":")+1)+" "+ap.secondWord+"		-----From Rule 13a:  Extended 2");

					             					 	        	}
					             					 	        }
				             							        }
				             								   }
				             								 }
				             						}
				             					}
				             				}				     
				            		 }
				            	 }
				            	 
				             } 
				             //Rule 14
				             if(al.depType.contains("nsubj")&&al.firstTag.contains("NN")&&al.secondTag.contains("NN")){
				            	 ElementAccessClass aj;
				            	 for(int j=i+1;j<myobj.length;j++){
				            		 aj=(ElementAccessClass) myobj[j];
				            		 if(aj.depType.equals("acl")&&aj.firstWord.equals(al.firstWord)&&(al.firstIndex==aj.firstIndex)&&aj.secondTag.contains("VB")){
				            			 ElementAccessClass ak;  
				             				for(int k=j+1;k<myobj.length;k++){
				             					ak=(ElementAccessClass) myobj[k]; ElementAccessClass az=new ElementAccessClass();
				             					if(ak.depType.equals("xcomp")&&ak.firstWord.equals(aj.secondWord)&&ak.secondTag.equals("RB")){
				             						az=(ElementAccessClass) myobj[k+1];
				             						 if(az.depType.equals("case")&&(az.secondIndex==ak.secondIndex+1)){
				             							 ElementAccessClass am;
				             							 for(int m=k+1; m<myobj.length; m++){
				             								am=(ElementAccessClass) myobj[m];
				             								 if(am.depType.contains("nmod:")&&am.firstWord.equals(ak.secondWord)&&am.depType.contains(az.secondWord)&&(am.firstIndex==az.secondIndex-1)){
				             									 System.out.println(al.secondWord+" || "+ak.firstWord+" "+ak.secondWord+" "+az.secondWord+" || "+am.secondWord+"		---Rule 14:");
				             									pw.println(al.secondWord+" || "+ak.firstWord+" "+ak.secondWord+" "+az.secondWord+" || "+am.secondWord+"		---Rule 14:");
				             								 }
				             							 }
				             						 }
				             					}
				             					if(ak.depType.contains("nmod:")&&ak.firstTag.contains("VB")&&ak.firstWord.equals(aj.secondWord)&&(ak.firstIndex==aj.secondIndex)){
				             						//System.out.println(al.secondWord+" || "+ak.firstWord+" "+ak.secondWord);
				             						ElementAccessClass am;
				             						for(int m=k+1;m<myobj.length-1; m++){
				             						 am=(ElementAccessClass) myobj[m];
				             						 if(am.depType.contains("nmod:such_as")&&am.firstWord.equals(ak.secondWord)&&(am.firstIndex==ak.secondIndex)){
				             							System.out.println(al.secondWord+" || "+ak.firstWord+" "+ak.depType.substring(ak.depType.indexOf(":")+1)+" || "+am.secondWord+"	----Rule 14b");
				             							pw.println(al.secondWord+" || "+ak.firstWord+" "+ak.depType.substring(ak.depType.indexOf(":")+1)+" || "+am.secondWord+"	----Rule 14b");
				             							ElementAccessClass an;
				             							for(int n=m+1;n<myobj.length-1; n++){
						             						 an=(ElementAccessClass) myobj[n];
						             						 if(an.depType.contains("nmod:")&&an.firstWord.equals(am.secondWord)&&(an.firstIndex==am.secondIndex)&&an.secondTag.contains("NN")){
						             						System.out.println(al.secondWord+" || "+ak.firstWord+" "+ak.depType.substring(ak.depType.indexOf(":")+1)+" || "+an.firstWord+" "+an.depType.substring(an.depType.indexOf(":")+1)+" "+an.secondWord+"	----Rule 14b Extended");
					             							pw.println(al.secondWord+" || "+ak.firstWord+" "+ak.depType.substring(ak.depType.indexOf(":")+1)+" || "+an.firstWord+" "+an.depType.substring(an.depType.indexOf(":")+1)+" "+an.secondWord+"	----Rule 14b Extended");
						             						 }
						             						 }
				             							
				             						 }
				             						if(am.depType.contains("nmod:of")&&am.firstWord.equals(ak.secondWord)&&(am.firstIndex==ak.secondIndex)&&am.secondTag.contains("NN")){
				             							System.out.println(al.secondWord+" || "+ak.firstWord+" "+ak.depType.substring(ak.depType.indexOf(":")+1)+" || "+am.firstWord+" "+am.depType.substring(am.depType.indexOf(":")+1)+" "+am.secondWord+"	----Rule 14c");
				             							pw.println(al.secondWord+" || "+ak.firstWord+" "+ak.depType.substring(ak.depType.indexOf(":")+1)+" || "+am.firstWord+" "+am.depType.substring(am.depType.indexOf(":")+1)+" "+am.secondWord+"	----Rule 14c");
				             							ElementAccessClass an;
				             							for(int n=m+1;n<myobj.length-1; n++){
						             						 an=(ElementAccessClass) myobj[n];
						             						 if(an.depType.contains("nmod:")&&an.firstWord.equals(ak.firstWord)&&(an.firstIndex==ak.firstIndex)&&(an.secondIndex==am.secondIndex+3)&&an.secondTag.contains("NN")){
						             						System.out.println(al.secondWord+" || "+ak.firstWord+" "+ak.depType.substring(ak.depType.indexOf(":")+1)+" || "+am.firstWord+" "+am.depType.substring(am.depType.indexOf(":")+1)+" "+am.secondWord+" "+an.depType.substring(an.depType.indexOf(":")+1)+" "+an.secondWord+ "	----Rule 14c Extended");
					             							pw.println(al.secondWord+" || "+ak.firstWord+" "+ak.depType.substring(ak.depType.indexOf(":")+1)+" || "+am.firstWord+" "+am.depType.substring(am.depType.indexOf(":")+1)+" "+am.secondWord+" "+an.depType.substring(an.depType.indexOf(":")+1)+" "+an.secondWord+ "	----Rule 14c Extended");
						             						 }
						             						 }

				             						}
				             						}
				             						
				             						
				             					}
				             					
				             				}
				            		 }
				            		 if(aj.depType.contains("nsubj")&&aj.firstTag.contains("VB")&&aj.secondWord.equals(al.firstWord)&&(aj.secondIndex==al.firstIndex)){
				            			 ElementAccessClass ak; 
				            			 for(int k=j+1;k<myobj.length-1;k++){
				            				 ak=(ElementAccessClass) myobj[k];
				            				 if(ak.depType.contains("dobj")&&ak.firstWord.equals(aj.firstWord)&&(ak.firstIndex==ak.firstIndex)&&ak.secondTag.contains("NN")){
				            					 System.out.println(al.secondWord+" || "+ ak.firstWord+" || "+ak.secondWord+"	----Rule 14d");
				            					 pw.println(al.secondWord+" || "+ ak.firstWord+" || "+ak.secondWord+"	----Rule 14d");
				            					// System.out.println("YES");
				            				 }
				            			 }
				            			 
				            		 }
				            	 }
				            	 
				             }//rule 14 ends 
				             
				             // Rule 15
				            if(al.depType.equals("dobj")&&al.firstTag.contains("VBP")&&al.secondTag.contains("NN")&&(!(al.secondWord.equals("case")||al.secondWord.equals("%")))){
				            	ElementAccessClass aj;
				            	for(int j=i+1;j<myobj.length;j++){
				            		aj=(ElementAccessClass) myobj[j];
				            		if(aj.depType.contains("nmod:")&&aj.firstWord.equals(al.firstWord)&&(aj.firstIndex==al.firstIndex)&&(aj.firstIndex<aj.secondIndex)&&aj.secondTag.contains("NN")&&(!(aj.secondWord.equals("%")))){
				            			System.out.println(al.secondWord+" || "+al.firstWord+" || "+aj.secondWord+"		---Rule 15");
				            			pw.println(al.secondWord+" || "+al.firstWord+" || "+aj.secondWord+"		---Rule 15");
				            		}
				            		if(aj.depType.contains("nmod:")&&aj.firstWord.equals(al.secondWord)&&(aj.firstIndex==al.secondIndex)&&aj.secondTag.contains("NN")){
				            			ElementAccessClass ak;
						            	for(int k=j+1;k<myobj.length-1;k++){
						            		ak=(ElementAccessClass) myobj[k];
						            		if(ak.depType.contains("nmod:")&&ak.firstWord.equals(al.firstWord)&&(ak.firstIndex==al.firstIndex)&&ak.secondTag.contains("NN")){
						            			System.out.println(al.secondWord+" "+aj.depType.substring(aj.depType.indexOf(":")+1)+" "+aj.secondWord+" || "+al.firstWord+" || "+ak.secondWord+"		---Rule 15b");
						            			pw.println(al.secondWord+" "+aj.depType.substring(aj.depType.indexOf(":")+1)+" "+aj.secondWord+" || "+al.firstWord+" || "+ak.secondWord+"		---Rule 15b");
						            			ElementAccessClass am;
								            	for(int m=k+1;m<myobj.length-1;m++){
								            		am=(ElementAccessClass) myobj[m];	
								            		if(am.depType.contains("acl")&&am.firstWord.equals(ak.secondWord)&&(am.firstIndex==ak.secondIndex)&&am.secondTag.contains("VB")){
								            			ElementAccessClass an;
										            	for(int n=m+1;n<myobj.length-1;n++){
										            		an=(ElementAccessClass) myobj[n];
										            		if(an.depType.contains("nmod:")&&an.firstWord.equals(am.secondWord)&&(an.firstIndex==am.secondIndex)&&an.secondTag.contains("NN")){
										            			System.out.println(al.secondWord+" "+aj.depType.substring(aj.depType.indexOf(":")+1)+" "+aj.secondWord+" || "+an.firstWord+" "+an.depType.substring(an.depType.indexOf(":")+1)+" || "+an.secondWord+"		---Rule 15b Extended");
										            			pw.println(al.secondWord+" "+aj.depType.substring(aj.depType.indexOf(":")+1)+" "+aj.secondWord+" || "+an.firstWord+" "+an.depType.substring(an.depType.indexOf(":")+1)+" || "+an.secondWord+"		---Rule 15b Extended");

										            		}
										            		}
								            		}
								            		}
								            		
						            			
						            		}
						            		}

				            		}
				            		
				            	}
				            	 
				             }// Rule 15 ends
				            //Rule 16
				            if(al.depType.equals("nummod")&&al.firstTag.contains("NN")&&al.secondTag.contains("CD")){
				            	ElementAccessClass aj;
				            	for(int j=i+1;j<myobj.length;j++){
				            		aj=(ElementAccessClass) myobj[j];
				            		if(aj.depType.contains("nsubj")&&aj.firstTag.contains("VB")&&aj.secondWord.equals(al.firstWord)&&(aj.secondIndex==al.firstIndex)){
				            			ElementAccessClass ak;
				            			 for(int k=j+1;k<myobj.length-1;k++){
				            				 ak=(ElementAccessClass) myobj[k];
				            				 if(ak.depType.equals("nmod:of")&&ak.firstWord.equals(aj.secondWord)&&(ak.firstIndex==aj.secondIndex)&&ak.secondTag.contains("NN")){
				            					 
				            					 ElementAccessClass am;
				            					 for(int m=k+1;m<myobj.length-1; m++){
				             						 am=(ElementAccessClass) myobj[m];
				             						 if(am.depType.contains("nmod:including")&&am.firstTag.contains("VB")&&(am.firstIndex==aj.firstIndex)&&am.secondTag.contains("NN")){
				             							System.out.println(al.secondWord+" "+al.firstWord+" of "+ak.secondWord+" || "+aj.firstWord+" || "+am.secondWord+"	----Rule 16");
				             							pw.println(al.secondWord+" "+al.firstWord+" of "+ak.secondWord+" || "+aj.firstWord+" || "+am.secondWord+"	----Rule 16");
				             						 }
				             						 }
				            					 
				            					 
				            				 }
				            			 }
				            		}
				            	}
				            	
				            }
				            //Rule 17
				            if(al.depType.contains("conj:and")&&al.firstTag.equals("JJ")&&al.secondTag.equals("JJ")){
				            	ElementAccessClass aj;
				            	for(int j=i+1;j<myobj.length;j++){
				            		aj=(ElementAccessClass) myobj[j];
				            		if(aj.depType.contains("conj:and")&&aj.secondTag.contains("NN")&&(aj.firstIndex==al.firstIndex)){
				            			ElementAccessClass ak;
				            			 for(int k=j+1;k<myobj.length-1;k++){
				            				 ak=(ElementAccessClass) myobj[k];
				            				 if(ak.depType.contains("nsubj")&&ak.secondWord.equals(aj.secondWord)&&ak.firstTag.contains("NN")){
				            					 ElementAccessClass am;
				            					 for(int m=k+1;m<myobj.length-1; m++){
				             						 am=(ElementAccessClass) myobj[m];
				             						 if(am.depType.contains("acl")&&am.firstWord.equals(ak.firstWord)&&am.secondTag.contains("VB")&&(am.firstIndex==ak.firstIndex)){
				             							 ElementAccessClass an;
				             							for(int n=k+1;n<myobj.length-1; n++){
						             						 an=(ElementAccessClass) myobj[n];
						             						  if(an.depType.contains("nmod:")&&an.firstWord.equals(am.secondWord)&&(an.firstIndex==am.secondIndex)&&an.secondTag.contains("NN")){
						             							  System.out.println(al.firstWord+" ,"+al.secondWord+" and "+aj.secondWord+ " || "+an.firstWord+" "+an.depType.substring(an.depType.indexOf(":")+1)+" || "+an.secondWord+"	----Rule 17");
						             							 pw.println(al.firstWord+" ,"+al.secondWord+" and "+aj.secondWord+ " || "+an.firstWord+" "+an.depType.substring(an.depType.indexOf(":")+1)+" || "+an.secondWord+"	----Rule 17");
									             					
						             						  }
						             						 }
				             						  }
				             					 }
				            				 }
				            			 }
				            		}
				            	}
				            	
				            }
				            // Rule 18
				            if(al.depType.contains("nsubj")&&al.firstTag.contains("VB")&&al.secondTag.contains("NN")){
				            	ElementAccessClass aj;
				            	for(int j=i+1;j<myobj.length;j++){
				            		aj=(ElementAccessClass) myobj[j];
				            		if(aj.depType.contains("conj:and")&&aj.secondTag.contains("NN")&&(aj.firstIndex==al.firstIndex+1)&&aj.firstTag.contains("NN")){				            			
				            			System.out.println(al.secondWord+" || "+al.firstWord+" || "+aj.firstWord+ " and " +aj.secondWord+" ---Rule 18");
				            			pw.println(al.secondWord+" || "+al.firstWord+" || "+aj.firstWord+"  and "+aj.secondWord+" ---Rule 18");
				            		}
				            	}
				            	
				            	
				            	
				            }
				            // Rule 19
				            if(al.depType.equals("nsubj")&&al.secondTag.contains("NN")){
				            	ElementAccessClass aj;
				            	for(int j=i+1;j<myobj.length;j++){
				            		aj=(ElementAccessClass) myobj[j];
				            		if(aj.depType.equals("advcl")&&aj.secondWord.equals(al.firstWord)&&(aj.secondIndex==al.firstIndex)&&aj.firstTag.contains("VB")){
				            			
				            			ElementAccessClass ak;
						            	for(int k=j+1;k<myobj.length;k++){
						            		ak=(ElementAccessClass) myobj[k];
						            		if(ak.depType.equals("aux")&&ak.firstWord.equals(aj.firstWord)&&(ak.firstIndex==aj.firstIndex)&&ak.secondTag.contains("MD")){
						            			
						            			ElementAccessClass am;
								            	for(int m=k+1;m<myobj.length;m++){
								            		am=(ElementAccessClass) myobj[m];
								            		if(am.depType.contains("nmod:as")&&am.firstWord.equals(ak.firstWord)&&(am.firstIndex==ak.firstIndex)){
								            			
								            			ElementAccessClass an;
										            	for(int n=m+1;n<myobj.length;n++){
										            		an=(ElementAccessClass) myobj[n];
										            		if(an.depType.contains("nmod:of")&&an.firstWord.equals(am.secondWord)&&(an.firstIndex==am.secondIndex)){
										            			System.out.println(al.secondWord+" || "+ak.secondWord+" "+ aj.firstWord+" as || "+an.firstWord+" of "+an.secondWord +" ---Rule 19");
										            			pw.println(al.secondWord+" || "+ak.secondWord+" "+ aj.firstWord+" as || "+an.firstWord+" of "+an.secondWord +" ---Rule 19");

										            		}
										            		}
								            		}
								            		}
						            		}
						            		}
				            		}
				            		}
				            	
				            }
				            //Rule 20
				            if(al.depType.contains("case")&&al.firstTag.contains("NN")&&al.secondTag.contains("IN")){
				            	ElementAccessClass aj;
				            	for(int j=i+1;j<myobj.length-1;j++){
				            		aj=(ElementAccessClass) myobj[j];
				            		if(aj.depType.contains("nmod")&&aj.firstTag.contains("VB")&&aj.secondTag.contains("NN")&&aj.secondWord.equals(al.firstWord)&&(aj.secondIndex==al.firstIndex)){
				            			ElementAccessClass ak;
						            	for(int k=j+1;k<myobj.length-1;k++){
						            		ak=(ElementAccessClass) myobj[k];
						            		if(ak.depType.contains("nmod:")&&ak.firstWord.equals(aj.secondWord)&&(ak.firstIndex==aj.secondIndex)){
						            			ElementAccessClass am;
								            	for(int m=k+1;m<myobj.length-1;m++){
								            		am=(ElementAccessClass) myobj[m];
								            		if(am.depType.contains("nsubj")&&am.firstWord.equals(aj.firstWord)&&(am.firstIndex==aj.firstIndex)){								            			
								            			ElementAccessClass an;
										            	for(int n=k+1;n<myobj.length-1;n++){
										            		an=(ElementAccessClass) myobj[n];
										            		if(an.depType.contains("nmod:")&&an.firstWord.equals(ak.secondWord)&&(an.firstIndex==ak.secondIndex)){								
										            			System.out.println(am.secondWord+" || "+am.firstWord+" || "+an.firstWord+" "+an.depType.substring(an.depType.indexOf(":")+1)+" "+an.secondWord+" ---Rule 20" );
										            			pw.println(am.secondWord+" || "+am.firstWord+" || "+an.firstWord+" "+an.depType.substring(an.depType.indexOf(":")+1)+" "+an.secondWord+" ---Rule 20" );

										            		}
										            		}
								            			
								            		}
								            		}
						            			
						            			
						            		}
						            		}
				            		}
				            		}
				            	
				            	
				            	
				            	
				            }
				            //Rule 21
				             if(al.depType.contains("nsubj")&&al.firstTag.contains("NN")&&al.secondTag.contains("NN")){
				            	 ElementAccessClass aj;
					            	for(int j=i+1;j<myobj.length-1;j++){
					            		aj=(ElementAccessClass) myobj[j];
					            		if(aj.depType.contains("nmod:")&&aj.firstWord.equals(al.secondWord)&&(aj.firstIndex==al.secondIndex)&&aj.firstTag.contains("NN")&&aj.secondTag.contains("NN")){
					            			ElementAccessClass ak;
							            	for(int k=j+1;k<myobj.length-1;k++){
							            		ak=(ElementAccessClass) myobj[k];
							            		if(ak.depType.contains("mark")&&ak.firstTag.contains("VB")&&ak.secondTag.contains("IN")){
							            			ElementAccessClass am;
									            	for(int m=k+1;m<myobj.length-1;m++){
									            		am=(ElementAccessClass) myobj[m];
									            		if(am.depType.contains("acl")&&am.firstWord.equals(al.firstWord)&&(am.firstIndex==al.firstIndex)&&am.secondWord.equals(ak.firstWord)&&(am.secondIndex==ak.firstIndex)){
									            			ElementAccessClass an; 
											            	for(int n=m+1;n<myobj.length-1;n++){
											            		an=(ElementAccessClass) myobj[n];
											            		if(an.depType.contains("dobj")&&an.firstWord.equals(am.secondWord)&&(an.firstIndex==am.secondIndex)){
											            			System.out.println(al.secondWord+" "+aj.depType.substring(aj.depType.indexOf(":")+1)+" "+aj.secondWord+" || "+am.firstWord+" "+ak.secondWord+" "+an.firstWord+" || "+an.secondWord+" 	----Rule 21");
											            			pw.println(al.secondWord+" "+aj.depType.substring(aj.depType.indexOf(":")+1)+" "+aj.secondWord+" || "+am.firstWord+" "+ak.secondWord+" "+an.firstWord+" || "+an.secondWord+" 	----Rule 21");

											            		}
											            		}
									            		}
									            		}
							            		}
							            		}
					            			
					            		}
					            		}
				            	 
				             }
				             //Rule 22
				             if(al.depType.endsWith("cc")&&al.firstTag.contains("NN")&&al.secondTag.contains("CC")){
				            	 ElementAccessClass aj;
					            	for(int j=i+1;j<myobj.length-1;j++){
					            		aj=(ElementAccessClass) myobj[j];
					            		if(aj.depType.contains("conj:and")&&aj.firstWord.equals(al.firstWord)&&(aj.firstIndex==al.firstIndex)&&aj.secondTag.contains("NN")){
					            			ElementAccessClass ak;
							            	for(int k=j+1;k<myobj.length-1;k++){
							            		ak=(ElementAccessClass) myobj[k];
							            		if(ak.depType.contains("nsubj")&&ak.firstTag.contains("VB")&&ak.secondWord.equals(aj.secondWord)&&(ak.secondIndex==aj.secondIndex)){
							            				ElementAccessClass am;
									            	for(int m=k+1;m<myobj.length-1;m++){
									            		am=(ElementAccessClass) myobj[m];
									            		if(am.depType.contains("nmod:")&&am.firstWord.equals(ak.firstWord)&&(am.firstIndex==ak.firstIndex)){
									            			ElementAccessClass an;
											            	for(int n=m+1;n<myobj.length;n++){
											            		an=(ElementAccessClass) myobj[n];
											            		if(an.depType.contains("nmod:")&&an.firstWord.equals(am.secondWord)&&(an.firstIndex==am.secondIndex)&&an.secondTag.contains("NN")){
											            			System.out.println(al.firstWord+" || "+ak.firstWord+" "+am.depType.substring(am.depType.indexOf(":")+1)+" || "+am.secondWord+" "+an.depType.substring(an.depType.indexOf(":")+1)+" "+an.secondWord+"	----Rule 22");
											            			pw.println(al.firstWord+" || "+ak.firstWord+" "+am.depType.substring(am.depType.indexOf(":")+1)+" || "+am.secondWord+" "+an.depType.substring(an.depType.indexOf(":")+1)+" "+an.secondWord+"	----Rule 22");
											            			
											            		}
											            		}
										            		
									            		}
									            		}
									            	
							            			
							            		}
							            		}
							            
					            		}
					            		}
				            	 
				             }
				             
			              
				             }   // end of the for loop for extracting triplet
					    
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
						//}
		//pw.close();
	//	pw2.close();
							//}// after flag for skipping the file
							//}	 //flag
							
				} // List of files
						
		  System.out.println("Total sentence processesssed "+e);
		  System.out.println("Executiom COMPLETED ");
		} // main

	}// class 

