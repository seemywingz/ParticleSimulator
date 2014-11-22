package prt;

public class Position {

    /* Position Coordinates and Size */
	double x,y,z,sz;

	Position(double xIN, double yIN,double zIN, double szIN){
		x=xIN;y=yIN;z=zIN;sz=szIN;
	}//..
	Position(double xIN, double yIN,double zIN){
		x=xIN;y=yIN;z=zIN;sz=50;
	}//..
    Position(double xIN, double yIN){
        x=xIN;y=yIN;sz=50;
    }//..
	Position(){
		x=50;y=50;sz=50;
	}//..

    public void setXY(double x, double y){
        this.x=x;this.y=y;
    }//..

    public void setXYZ(double x, double y, double z){
        this.x=x;this.y=y;this.z=z;
    }//..

	public static double calcDistance(Position p1, Position p2){
		return  Math.sqrt(Math.pow(p1.y-p2.y,2) + Math.pow(p1.x-p2.x,2));	
	}//..
	
}// end Position
