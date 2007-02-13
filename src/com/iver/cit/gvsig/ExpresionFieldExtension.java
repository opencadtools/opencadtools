package com.iver.cit.gvsig;

import java.util.BitSet;

import org.apache.bsf.BSFManager;

import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.cit.gvsig.gui.preferences.FieldExpresionPage;
import com.iver.cit.gvsig.project.documents.table.gui.EvalExpresionDialog;
import com.iver.cit.gvsig.project.documents.table.gui.Table;
import com.iver.cit.gvsig.project.documents.table.operators.Abs;
import com.iver.cit.gvsig.project.documents.table.operators.Acos;
import com.iver.cit.gvsig.project.documents.table.operators.Area;
import com.iver.cit.gvsig.project.documents.table.operators.Asin;
import com.iver.cit.gvsig.project.documents.table.operators.Atan;
import com.iver.cit.gvsig.project.documents.table.operators.Ceil;
import com.iver.cit.gvsig.project.documents.table.operators.Cos;
import com.iver.cit.gvsig.project.documents.table.operators.Distinct;
import com.iver.cit.gvsig.project.documents.table.operators.Division;
import com.iver.cit.gvsig.project.documents.table.operators.E;
import com.iver.cit.gvsig.project.documents.table.operators.EndsWith;
import com.iver.cit.gvsig.project.documents.table.operators.Equal;
import com.iver.cit.gvsig.project.documents.table.operators.Equals;
import com.iver.cit.gvsig.project.documents.table.operators.Exp;
import com.iver.cit.gvsig.project.documents.table.operators.Geometry;
import com.iver.cit.gvsig.project.documents.table.operators.IndexOf;
import com.iver.cit.gvsig.project.documents.table.operators.IsNumber;
import com.iver.cit.gvsig.project.documents.table.operators.LastIndexOf;
import com.iver.cit.gvsig.project.documents.table.operators.Length;
import com.iver.cit.gvsig.project.documents.table.operators.LessEquals;
import com.iver.cit.gvsig.project.documents.table.operators.LessThan;
import com.iver.cit.gvsig.project.documents.table.operators.Log;
import com.iver.cit.gvsig.project.documents.table.operators.Minus;
import com.iver.cit.gvsig.project.documents.table.operators.MoreEquals;
import com.iver.cit.gvsig.project.documents.table.operators.MoreThan;
import com.iver.cit.gvsig.project.documents.table.operators.Perimeter;
import com.iver.cit.gvsig.project.documents.table.operators.Pi;
import com.iver.cit.gvsig.project.documents.table.operators.Plus;
import com.iver.cit.gvsig.project.documents.table.operators.PointX;
import com.iver.cit.gvsig.project.documents.table.operators.PointY;
import com.iver.cit.gvsig.project.documents.table.operators.Pow;
import com.iver.cit.gvsig.project.documents.table.operators.Random;
import com.iver.cit.gvsig.project.documents.table.operators.Replace;
import com.iver.cit.gvsig.project.documents.table.operators.Round;
import com.iver.cit.gvsig.project.documents.table.operators.Sin;
import com.iver.cit.gvsig.project.documents.table.operators.Sqrt;
import com.iver.cit.gvsig.project.documents.table.operators.StartsWith;
import com.iver.cit.gvsig.project.documents.table.operators.SubString;
import com.iver.cit.gvsig.project.documents.table.operators.Tan;
import com.iver.cit.gvsig.project.documents.table.operators.Times;
import com.iver.cit.gvsig.project.documents.table.operators.ToDate;
import com.iver.cit.gvsig.project.documents.table.operators.ToDegrees;
import com.iver.cit.gvsig.project.documents.table.operators.ToLowerCase;
import com.iver.cit.gvsig.project.documents.table.operators.ToNumber;
import com.iver.cit.gvsig.project.documents.table.operators.ToRadians;
import com.iver.cit.gvsig.project.documents.table.operators.ToString;
import com.iver.cit.gvsig.project.documents.table.operators.ToUpperCase;
import com.iver.cit.gvsig.project.documents.table.operators.Trim;
import com.iver.utiles.extensionPoints.ExtensionPoints;
import com.iver.utiles.extensionPoints.ExtensionPointsSingleton;
/**
 * @author Vicente Caballero Navarro
 */
public class ExpresionFieldExtension extends Extension{
	//private static Interpreter interpreter=new Interpreter();
	public static final String BEANSHELL="beanshell";
	private static BSFManager interpreter=new BSFManager();
	private Table table=null;
	public void initialize() {
		ExtensionPoints extensionPoints = ExtensionPointsSingleton.getInstance();
    	extensionPoints.add("cad_editing_properties_pages","fieldExpresion",FieldExpresionPage.class);
    	registerOperations();
	}

	public void execute(String actionCommand) {
		com.iver.andami.ui.mdiManager.IWindow window = PluginServices.getMDIManager().getActiveWindow();
		table=(Table)window;
		EvalExpresionDialog eed=new EvalExpresionDialog(table,interpreter);
		PluginServices.getMDIManager().addWindow(eed);
	}
	public void postInitialize() {
	}

	public boolean isEnabled() {
		com.iver.andami.ui.mdiManager.IWindow window = PluginServices.getMDIManager().getActiveWindow();
		if (window instanceof Table) {
			Table table=(Table)window;
			BitSet columnSelected = table.getSelectedFieldIndices();
		    if (!columnSelected.isEmpty() && table.isEditing()) {
		    	return true;
		    }
		}
		return false;
	}

	public boolean isVisible() {
		com.iver.andami.ui.mdiManager.IWindow window = PluginServices.getMDIManager().getActiveWindow();
		if (window instanceof Table) {
			return true;
		}
		return false;
	}
	 private void registerOperations() {
	    	ExtensionPoints extensionPoints = ExtensionPointsSingleton.getInstance();
	    	extensionPoints.add("ColumnOperators",Abs.class.toString(),Abs.class);
	        extensionPoints.add("ColumnOperators",Acos.class.toString(),Acos.class);
	        extensionPoints.add("ColumnOperators",Area.class.toString(),Area.class);
	        extensionPoints.add("ColumnOperators",Asin.class.toString(),Asin.class);
	        extensionPoints.add("ColumnOperators",Atan.class.toString(),Atan.class);
	        extensionPoints.add("ColumnOperators",Ceil.class.toString(),Ceil.class);
	        extensionPoints.add("ColumnOperators",Cos.class.toString(),Cos.class);
	    	extensionPoints.add("ColumnOperators",Distinct.class.toString(),Distinct.class);
	     	extensionPoints.add("ColumnOperators",Division.class.toString(),Division.class);
	     	extensionPoints.add("ColumnOperators",E.class.toString(),E.class);
	     	extensionPoints.add("ColumnOperators",EndsWith.class.toString(),EndsWith.class);
	     	extensionPoints.add("ColumnOperators",Equal.class.toString(),Equal.class);
	     	extensionPoints.add("ColumnOperators",Equals.class.toString(),Equals.class);
	     	extensionPoints.add("ColumnOperators",Exp.class.toString(),Exp.class);
	     	extensionPoints.add("ColumnOperators",Geometry.class.toString(),Geometry.class);
	     	extensionPoints.add("ColumnOperators",IndexOf.class.toString(),IndexOf.class);
	     	extensionPoints.add("ColumnOperators",IsNumber.class.toString(),IsNumber.class);
	     	extensionPoints.add("ColumnOperators",LastIndexOf.class.toString(),LastIndexOf.class);
	     	extensionPoints.add("ColumnOperators",Length.class.toString(),Length.class);
	     	extensionPoints.add("ColumnOperators",LessEquals.class.toString(),LessEquals.class);
	     	extensionPoints.add("ColumnOperators",LessThan.class.toString(),LessThan.class);
	     	extensionPoints.add("ColumnOperators",Log.class.toString(),Log.class);
	     	extensionPoints.add("ColumnOperators",Minus.class.toString(),Minus.class);
	     	extensionPoints.add("ColumnOperators",MoreEquals.class.toString(),MoreEquals.class);
	     	extensionPoints.add("ColumnOperators",MoreThan.class.toString(),MoreThan.class);
	     	extensionPoints.add("ColumnOperators",Perimeter.class.toString(),Perimeter.class);
	     	extensionPoints.add("ColumnOperators",Pi.class.toString(),Pi.class);
	    	extensionPoints.add("ColumnOperators",Plus.class.toString(),Plus.class);
	     	extensionPoints.add("ColumnOperators",PointX.class.toString(),PointX.class);
	     	extensionPoints.add("ColumnOperators",PointY.class.toString(),PointY.class);
	     	extensionPoints.add("ColumnOperators",Pow.class.toString(),Pow.class);
	     	extensionPoints.add("ColumnOperators",Random.class.toString(),Random.class);
	     	extensionPoints.add("ColumnOperators",Replace.class.toString(),Replace.class);
	     	extensionPoints.add("ColumnOperators",Round.class.toString(),Round.class);
	     	extensionPoints.add("ColumnOperators",Sin.class.toString(),Sin.class);
	     	extensionPoints.add("ColumnOperators",Sqrt.class.toString(),Sqrt.class);
	     	extensionPoints.add("ColumnOperators",StartsWith.class.toString(),StartsWith.class);
	     	extensionPoints.add("ColumnOperators",SubString.class.toString(),SubString.class);
	     	extensionPoints.add("ColumnOperators",Tan.class.toString(),Tan.class);
	    	extensionPoints.add("ColumnOperators",Times.class.toString(),Times.class);
	    	extensionPoints.add("ColumnOperators",ToDate.class.toString(),ToDate.class);
	    	extensionPoints.add("ColumnOperators",ToDegrees.class.toString(),ToDegrees.class);
	    	extensionPoints.add("ColumnOperators",ToLowerCase.class.toString(),ToLowerCase.class);
	    	extensionPoints.add("ColumnOperators",ToNumber.class.toString(),ToNumber.class);
	    	extensionPoints.add("ColumnOperators",ToRadians.class.toString(),ToRadians.class);
	    	extensionPoints.add("ColumnOperators",ToString.class.toString(),ToString.class);
	    	extensionPoints.add("ColumnOperators",ToUpperCase.class.toString(),ToUpperCase.class);
	    	extensionPoints.add("ColumnOperators",Trim.class.toString(),Trim.class);
	 }

}
