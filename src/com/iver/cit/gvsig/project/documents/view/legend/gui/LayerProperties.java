package com.iver.cit.gvsig.project.documents.view.legend.gui;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;
import com.iver.cit.gvsig.fmap.core.symbols.ISymbol;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.rendering.ILegend;
import com.iver.cit.gvsig.fmap.rendering.IVectorLegend;
import com.iver.cit.gvsig.project.documents.view.legend.gui.tablelayers.StatusListener;
import com.iver.cit.gvsig.project.documents.view.legend.gui.tablelayers.TableLayers;

public class LayerProperties extends JPanel implements IWindow {

    private JPanel pWest = null;
    private JPanel pCenter = null;
    private JPanel pSouth = null;
    private JPanel pCenterNorth = null;
    private JPanel pWestNorth = null;
    private JPanel pWestCenter = null;
    private TableLayers tableLayers = null;
    private FLyrVect layer;
    private IVectorLegend legend;
    private JButton bNewSubLayer = null;
    private JButton bDelSubLayer = null;
    private JButton bPresentSubLayer = null;
    private JLabel jLabel = null;
    private JLabel lblPresentSubLayer = null;
    private JPanel pSouthNorth = null;
    private JLabel jLabel1 = null;
    private JLabel lblNumLayers = null;
    private JPanel pSouthCenter = null;
    private JButton bOk = null;
    private JButton bApply = null;
    private JButton bCancel = null;

    /**
     * This is the default constructor
     */
    public LayerProperties(FLayer lyr, ILegend l) {
	super();
	layer = (FLyrVect) lyr;
	legend = (IVectorLegend) l;
	initialize();
    }

    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize() {
	this.setLayout(new BorderLayout());
	this.setSize(534, 328);
	this.add(getPNorth(), java.awt.BorderLayout.WEST);
	this.add(getPCenter(), java.awt.BorderLayout.CENTER);
	this.add(getPSouth(), java.awt.BorderLayout.SOUTH);
	getTableLayers().addStatusListener(new StatusListener() {
	    @Override
	    public void click() {
		lblPresentSubLayer.setText(getTableLayers()
			.getPresentSubLayer());
	    }

	});
    }

    /**
     * This method initializes pNorth
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getPNorth() {
	if (pWest == null) {
	    pWest = new JPanel();
	    pWest.setLayout(new BorderLayout());
	    pWest.add(getPWestNorth(), java.awt.BorderLayout.NORTH);
	    pWest.add(getPWestCenter(), java.awt.BorderLayout.CENTER);
	}
	return pWest;
    }

    /**
     * This method initializes pCenter
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getPCenter() {
	if (pCenter == null) {
	    pCenter = new JPanel();
	    pCenter.setLayout(new BorderLayout());
	    pCenter.add(getPCenterNorth(), java.awt.BorderLayout.NORTH);
	    pCenter.add(getTableLayers(), java.awt.BorderLayout.CENTER);
	}
	return pCenter;
    }

    /**
     * This method initializes pSouth
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getPSouth() {
	if (pSouth == null) {
	    pSouth = new JPanel();
	    pSouth.setLayout(new BorderLayout());
	    pSouth.add(getPSouthNorth(), java.awt.BorderLayout.NORTH);
	    pSouth.add(getPSouthCenter(), java.awt.BorderLayout.CENTER);
	}
	return pSouth;
    }

    /**
     * This method initializes pCenterNorth
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getPCenterNorth() {
	if (pCenterNorth == null) {
	    lblPresentSubLayer = new JLabel();
	    lblPresentSubLayer.setText(getTableLayers().getPresentSubLayer());
	    lblPresentSubLayer
		    .setPreferredSize(new java.awt.Dimension(150, 16));
	    jLabel = new JLabel();
	    jLabel.setText("capa actual");
	    pCenterNorth = new JPanel();
	    pCenterNorth.add(getBNewSubLayer(), null);
	    pCenterNorth.add(getBDelSubLayer(), null);
	    pCenterNorth.add(getBPresentSubLayer(), null);
	    pCenterNorth.add(jLabel, null);
	    pCenterNorth.add(lblPresentSubLayer, null);
	}
	return pCenterNorth;
    }

    /**
     * This method initializes pCenterCenter
     * 
     * @return javax.swing.JPanel
     */
    // private JPanel getPCenterCenter() {
    // if (pCenterCenter == null) {
    // pCenterCenter = new JPanel();
    // pCenterCenter.add(getLayerTable(), null);
    // }
    // return pCenterCenter;
    // }

    /**
     * This method initializes pWestNorth
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getPWestNorth() {
	if (pWestNorth == null) {
	    pWestNorth = new JPanel();
	}
	return pWestNorth;
    }

    /**
     * This method initializes pWestCenter
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getPWestCenter() {
	if (pWestCenter == null) {
	    pWestCenter = new JPanel();
	}
	return pWestCenter;
    }

    /**
     * This method initializes layerTable
     * 
     * @return javax.swing.JTable
     */
    private TableLayers getTableLayers() {
	if (tableLayers == null) {
	    tableLayers = new TableLayers(layer.getSource(), legend);
	}
	return tableLayers;
    }

    public void setLayer(FLayer lyr, ILegend l) {
	layer = (FLyrVect) lyr;
	legend = (IVectorLegend) l;
	getTableLayers().setSource(layer.getSource());
	getTableLayers().setLegend(legend);
    }

    public ILegend getLegend() {
	return legend;
    }

    @Override
    public WindowInfo getWindowInfo() {
	WindowInfo m_viewinfo = new WindowInfo(WindowInfo.MODALDIALOG
		| WindowInfo.RESIZABLE);
	m_viewinfo.setTitle(PluginServices.getText(this, "propiedades_capa"));
	return m_viewinfo;
    }

    /**
     * This method initializes bNewSubLayer
     * 
     * @return javax.swing.JButton
     */
    private JButton getBNewSubLayer() {
	if (bNewSubLayer == null) {
	    bNewSubLayer = new JButton();
	    bNewSubLayer.setText("nueva");
	}
	return bNewSubLayer;
    }

    /**
     * This method initializes bDelSubLayer
     * 
     * @return javax.swing.JButton
     */
    private JButton getBDelSubLayer() {
	if (bDelSubLayer == null) {
	    bDelSubLayer = new JButton();
	    bDelSubLayer.setText("borrar");
	}
	return bDelSubLayer;
    }

    /**
     * This method initializes bPresentSubLayer
     * 
     * @return javax.swing.JButton
     */
    private JButton getBPresentSubLayer() {
	if (bPresentSubLayer == null) {
	    bPresentSubLayer = new JButton();
	    bPresentSubLayer.setText("actual");
	}
	return bPresentSubLayer;
    }

    /**
     * This method initializes pSouthNorth
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getPSouthNorth() {
	if (pSouthNorth == null) {
	    lblNumLayers = new JLabel();
	    lblNumLayers.setText("JLabel");
	    lblNumLayers.setPreferredSize(new java.awt.Dimension(150, 16));
	    jLabel1 = new JLabel();
	    jLabel1.setText("num_capas");
	    pSouthNorth = new JPanel();
	    pSouthNorth.add(jLabel1, null);
	    pSouthNorth.add(lblNumLayers, null);
	}
	return pSouthNorth;
    }

    /**
     * This method initializes pSouthCenter
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getPSouthCenter() {
	if (pSouthCenter == null) {
	    pSouthCenter = new JPanel();
	    pSouthCenter.add(getBOk(), null);
	    pSouthCenter.add(getBApply(), null);
	    pSouthCenter.add(getBCancel(), null);
	}
	return pSouthCenter;
    }

    /**
     * This method initializes bOk
     * 
     * @return javax.swing.JButton
     */
    private JButton getBOk() {
	if (bOk == null) {
	    bOk = new JButton();
	    bOk.setText("aceptar");
	}
	return bOk;
    }

    /**
     * This method initializes bApply
     * 
     * @return javax.swing.JButton
     */
    private JButton getBApply() {
	if (bApply == null) {
	    bApply = new JButton();
	    bApply.setText("aplicar");
	}
	return bApply;
    }

    /**
     * This method initializes bCancel
     * 
     * @return javax.swing.JButton
     */
    private JButton getBCancel() {
	if (bCancel == null) {
	    bCancel = new JButton();
	    bCancel.setText("cancelar");
	}
	return bCancel;
    }

    public String getDescription() {
	return "help text for LayerProperties (change this)";
    }

    public ISymbol getPreviewSymbol() {
	// TODO Implement it
	throw new Error("Not yet implemented!");

    }

    public String getParentTitle() {
	// TODO Implement it
	throw new Error("Not yet implemented!");

    }

    @Override
    public Object getWindowProfile() {
	return WindowInfo.DIALOG_PROFILE;
    }

} // @jve:decl-index=0:visual-constraint="10,10"
