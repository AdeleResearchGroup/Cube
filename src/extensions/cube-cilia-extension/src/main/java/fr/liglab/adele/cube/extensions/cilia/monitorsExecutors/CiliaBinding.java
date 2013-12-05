package fr.liglab.adele.cube.extensions.cilia.monitorsExecutors;



public class  CiliaBinding {

	public String from ;
	public String to ;
	
	public CiliaBinding(String from, String to){
		this.from=from ;
		this.to=to;
	}
	
	@Override
	public boolean equals(Object bObj){
		if( bObj instanceof CiliaBinding){ 
			CiliaBinding b= (CiliaBinding) bObj;
			 if ((this.from.equals(b.from))&&(this.to.equals(b.to))){
/*				 System.out.println("CiliaBinding equals");
				 System.out.println("CiliaBinding " +this.from  + " " + b.from);
				 System.out.println("CiliaBinding " + this.to  + " " + b.to);
				System.out.println("CiliaBinding " + this.from.equals(b.from));
				System.out.println("CiliaBinding " + this.to.equals(b.to)); */
			 	return true;
			 }else{
			 	return false ;
			 }		
		 }else{
//		 	System.out.println("CiliaBinding equals called, not a Cilia binding object");
		 	return false;
		 }
	}
	
	public String toString(){
		return( from +":"+to);
	
	}

}