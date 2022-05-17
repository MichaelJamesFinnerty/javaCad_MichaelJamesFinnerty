import com.neuronrobotics.bowlerstudio.vitamins.Vitamins

import eu.mihosoft.vrl.v3d.CSG
import eu.mihosoft.vrl.v3d.Cube
import eu.mihosoft.vrl.v3d.Cylinder
import eu.mihosoft.vrl.v3d.RoundedCube
import eu.mihosoft.vrl.v3d.Sphere
import eu.mihosoft.vrl.v3d.Vector3d
import eu.mihosoft.vrl.v3d.parametrics.CSGDatabase
import eu.mihosoft.vrl.v3d.parametrics.IParameterChanged
import eu.mihosoft.vrl.v3d.parametrics.LengthParameter
import eu.mihosoft.vrl.v3d.parametrics.Parameter
import eu.mihosoft.vrl.v3d.parametrics.StringParameter

class SampleMaker implements IParameterChanged{//collection of parts
	ArrayList<CSG> parts = null;
	boolean loading=false;
	ArrayList<CSG> makeSamples(){
		if(parts !=null){
			return parts
		}
		loading=true;
		double myStartSize = 40;
		LengthParameter size 		= new LengthParameter("size",myStartSize,[1000.0,1.0])
		LengthParameter smallerSize 		= new LengthParameter("smaller size",myStartSize/20*12.5,[120.0,1.0])
		CSGDatabase.addParameterListener(size.getName(),this);
		CSGDatabase.addParameterListener(smallerSize.getName(),this);
		// force a value to override the database loaded value
		smallerSize.setMM(size.getMM()/20*12.5);

		int jjj=20;
		
		// Create a cube
		CSG cube = new Cube(	size,// X dimention
					size,// Y dimention
					size//  Z dimention
					).toCSG()
					
		//BowlerStudioController.addCsg(cube)//displays just this item
		//create a rounded cube
		CSG roundedCube = new RoundedCube(	size,// X dimention
						size,// Y dimention
						size//  Z dimention
						)
						.cornerRadius(size.getMM()/6.66)
						.toCSG()
							
		//create a sphere
		CSG sphere = new Sphere(smallerSize).toCSG()
		//create a Cylinder
		CSG cylinder = new Cylinder(	size, // Radius at the top
						smallerSize, // Radius at the bottom
						size, // Height
					         (int)80 //resolution
					         ).toCSG()
		//create an extruded polygon
		CSG polygon = Extrude.points(new Vector3d(0, 0, size.getMM()),// This is the  extrusion depth
		                new Vector3d(0,0),// All values after this are the points in the polygon
		                new Vector3d(size.getMM()*2,0),// Bottom right corner
		                new Vector3d(size.getMM()*1.5,size.getMM()),// upper right corner
		                new Vector3d(size.getMM()/2,size.getMM())// upper left corner
		        );		         
		//perform a difference
		// perform union, difference and intersection
		CSG cubePlusSphere = cube.union(sphere);
		CSG cubeMinusSphere = cube.difference(sphere);
		CSG cubeIntersectSphere = cube.intersect(sphere);
		
		//Scale lets you increas or decrease the sise by a scale factor
		CSG cubeMinusSphereSmall = cubeMinusSphere
						.scalex(jjj*0.5)
						.scaley(jjj*0.5)
						.scalez(jjj*0.5)
						.movez(size.getMM()*jjj*1.5)
		
		//Move and rotate opperations
		//cubeIntersectSphere = cubeIntersectSphere.move(1,2,3);// vector notation
		cubeIntersectSphere = cubeIntersectSphere
					.movex(jjj*1)
					.movey(jjj*2)
					.movez(jjj*3)
		//rotate
		//cubeIntersectSphere = cubeIntersectSphere.rot(15,20,30);// vector notation
		cubeIntersectSphere = cubeIntersectSphere
					.rotx(jjj*15)
					.roty(jjj*20)
					.rotz(jjj*30)
		//set colors
		//cube.setColor(javafx.scene.paint.Color.web("#C71585"));
		
		//make a keepaway shape 
		// this can be a shell or printer keepaway
		// this increases the size by a spacific measurment in mm
		CSG cubeIntersectSphereBigger = cubeIntersectSphere
						.makeKeepaway((double)10.0)
						.movez(size.getMM()*1.5)
		
		parts = new ArrayList<CSG>();
		
		parts.add(cube)
		//parts.add(servo)
		parts.add(sphere.movey(size.getMM()*1.5))
		parts.add(cubePlusSphere.movey(size.getMM()*3))
		parts.add(cubeMinusSphere.movey(size.getMM()*5))
		parts.add(cubeMinusSphereSmall.movey(size.getMM()*6))
		parts.add(cubeIntersectSphere.movey(size.getMM()*7))
		parts.add(cubeIntersectSphereBigger.movey(size.getMM()*9))
		parts.add(cylinder.movex(size.getMM()*4))
		parts.add(polygon.movex(size.getMM()*8))
		parts.add(roundedCube.movex(size.getMM()*12))
		for(int i=0;i<parts.size();i++){
			CSG part=parts.get(i)
			int myIndex=i;
			part.setRegenerate({ 
				makeSamples().get(myIndex)
			})
			.setParameter(size)
			.setColor(javafx.scene.paint.Color.hsb(90+i*10,1.0,0.66))
			
		}
		loading=false;
		return parts
	}
	/**
	 * This is a listener for a parameter changing
	 * @param name
	 * @param p
	 */
	 HashMap<String,String> lastValue = new HashMap<>()
	public void parameterChanged(String name, Parameter p){
		//if(p.getStrValue()==null&& p.getValue()==null)
		//	return
		if(loading)
			return
		//if(lastValue.get(name)!=null )
		//	if(p.getStrValue().contains(lastValue.get(name)))
		//		return
		
		lastValue.put(name,p.getStrValue())
		println "CHANGED: "+name
		parts=null
	}
}
CSGDatabase.clear()
return new SampleMaker().makeSamples()