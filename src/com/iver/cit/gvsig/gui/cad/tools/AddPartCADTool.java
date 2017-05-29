package com.iver.cit.gvsig.gui.cad.tools;

import org.apache.log4j.Logger;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.cit.gvsig.CADExtension;
import com.iver.cit.gvsig.SelectionGeometryExtension;
import com.iver.cit.gvsig.exceptions.expansionfile.ExpansionFileReadException;
import com.iver.cit.gvsig.exceptions.expansionfile.ExpansionFileWriteException;
import com.iver.cit.gvsig.exceptions.validate.ValidateRowException;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.GeneralPathX;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.v02.FConverter;
import com.iver.cit.gvsig.fmap.edition.EditionEvent;
import com.iver.cit.gvsig.fmap.edition.IRowEdited;
import com.iver.cit.gvsig.fmap.edition.VectorialEditableAdapter;
import com.vividsolutions.jts.geom.Geometry;

public class AddPartCADTool extends EIELPolylineCADTool {
    private IRowEdited rowEdited;

    private static final Logger logger = Logger.getLogger(AddPartCADTool.class);

    @Override
    public void init() {
	super.init();
	rowEdited = (IRowEdited) getVLE().getSelectedRow().get(0);

    }

    @Override
    public void addGeometry(IGeometry geometry) {
	// TODO: fpuga. It's possible make something more straight copying from
	// endGeometry, and avoiding using the geometry received as parameter
	final IFeature feat = (IFeature) rowEdited.getLinkedRow();

	IGeometry orgGeom = feat.getGeometry().cloneGeometry();

	GeneralPathX orgGP = new GeneralPathX(orgGeom.getInternalShape());

	Geometry orgJts = orgGeom.toJTSGeometry();
	Geometry newJts = geometry.toJTSGeometry();
	IGeometry newGeom = FConverter.jts_to_igeometry(orgJts.union(newJts));
	if (orgJts.intersects(newJts)) {
	    newGeom = FConverter.jts_to_igeometry(orgJts.union(newJts));
	}

	// orgGP.append(geometry.getInternalShape(), false);

	if (orgGeom.getGeometryType() == FShape.POLYGON) {
	    // feat.setGeometry(ShapeFactory.createPolygon2D(orgGP));
	    feat.setGeometry(newGeom);
	} else if (orgGeom.getGeometryType() == FShape.LINE) {
	    feat.setGeometry(newGeom);
	} else {
	    throw new RuntimeException("Incorrect geometry type");
	}

	VectorialEditableAdapter vea = getVLE().getVEA();
	try {
	    vea.modifyRow(rowEdited.getIndex(), feat, "add part",
		    EditionEvent.GRAPHIC);

	} catch (ExpansionFileWriteException e) {
	    logger.error(e.getStackTrace(), e);
	    NotificationManager.addError(e.getMessage(), e);
	} catch (ExpansionFileReadException e) {
	    logger.error(e.getStackTrace(), e);
	    NotificationManager.addError(e.getMessage(), e);
	} catch (ValidateRowException e) {
	    logger.error(e.getStackTrace(), e);
	    NotificationManager.addError(e.getMessage(), e);
	} catch (ReadDriverException e) {
	    logger.error(e.getStackTrace(), e);
	    NotificationManager.addError(e.getMessage(), e);
	}
	CADExtension.setCADTool(SelectionGeometryExtension.CAD_TOOL_KEY, true);
	PluginServices.getMainFrame().setSelectedTool(
		SelectionGeometryExtension.CAD_TOOL_KEY);
    }

    @Override
    /**
     * This tool is used as a redigitalization tool, not as an insertion tool,
     * so the listener must not be launched
     */
    public void fireEndGeometry() {
	return;
    }
}
