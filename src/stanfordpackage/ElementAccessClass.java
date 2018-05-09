package stanfordpackage;

public class ElementAccessClass {
	 String depType;
     String firstWord;
	 String firstTag;
	 int firstIndex;
	 String secondWord;
     String secondTag;
     int secondIndex;
	//Public int sentenceNo;
	ElementAccessClass(){
		this.depType="aslam";
		this.firstWord="aslam";
		this.firstTag="aslam";
		this.firstIndex=0;
		this.secondWord="aslam";
		this.secondTag="aslam";
		this.secondIndex=0;
		
	}
	ElementAccessClass(String d,String f,String t1,int fi, String s, String t2, int si){
		this.depType=d; this.firstWord=f;  this.firstTag=t1;this.firstIndex=fi; this.secondWord=s;this.secondTag=t2;this.secondIndex=si;
	}
	public void show(){
		System.out.println("Dependecy Type: "+depType+" First Word : "+firstWord+" First word tag "+firstTag+" Second Word: "+secondWord+" Second Word tag "+secondTag);
		
	}
	// Overriding the equals() method to compare two ElementAccessClass object
	 @Override
	    public boolean equals(Object o) {
	 
	        // If the object is compared with itself then return true  
	        if (o == this) {
	            return true;
	        }
	        /* Check if o is an instance of Complex or not
	          "null instanceof [type]" also returns false */
	        if (!(o instanceof ElementAccessClass)) {
	            return false;
	        }
	     // typecast o to Complex so that we can compare data members 
	        ElementAccessClass c = (ElementAccessClass) o;
	         
	        // Compare the data members and return accordingly 
	        return this.depType==c.depType&&this.firstWord==c.firstWord&&this.firstTag==c.firstTag&&this.firstIndex==c.firstIndex&&this.secondWord==c.secondWord&&this.secondTag==c.secondTag&&this.secondIndex==c.secondIndex;
	    }
	
	 @Override
		public int hashCode() {
			int hash = 3;
			hash = 7 * hash + this.depType.hashCode();
			hash = 7 * hash + this.firstWord.hashCode();
			hash = 7 * hash + this.firstTag.hashCode();
			hash = 7 * hash + this.firstIndex;
			hash = 7 * hash + this.secondWord.hashCode();
			hash = 7 * hash + this.secondTag.hashCode();
			hash = 7 * hash + this.secondIndex;
			
			
			return hash;
	 }

}
