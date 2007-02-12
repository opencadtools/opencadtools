package com.iver.cit.gvsig.project.documents.table.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;
import org.gvsig.gui.beans.AcceptCancelPanel;
import org.gvsig.gui.beans.swing.JButton;

import bsh.EvalError;

import com.hardcode.gdbms.engine.data.driver.DriverException;
import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;
import com.iver.cit.gvsig.ExpresionFieldExtension;
import com.iver.cit.gvsig.fmap.drivers.DriverIOException;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.iver.cit.gvsig.fmap.edition.IEditableSource;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.iver.cit.gvsig.project.documents.table.GraphicOperator;
import com.iver.cit.gvsig.project.documents.table.IOperator;
import com.iver.cit.gvsig.project.documents.table.Index;
import com.iver.cit.gvsig.project.documents.table.operators.Field;
import com.iver.utiles.GenericFileFilter;
import com.iver.utiles.extensionPoints.ExtensionPoint;
import com.iver.utiles.extensionPoints.ExtensionPoints;
import com.iver.utiles.extensionPoints.ExtensionPointsSingleton;


/**
 * DOCUMENT ME!
 *
 * @author Vicente Caballero Navarro
 */
public class EvalExpresionDialog extends JPanel implements IWindow {
    private JPanel pNorth = null;
    private JPanel pCentral = null;
    private JScrollPane jScrollPane = null;
    private JTextArea txtExp = null;
    private AcceptCancelPanel acceptCancel;
    private Table table;
    private FLyrVect lv;
    private JLabel lblColumn = null;
    private JPanel pNorthEast = null;
    private JPanel pNorthCenter = null;
    private JPanel pNorthWest = null;
    private JScrollPane jScrollPane1 = null;
    private JList listFields = null;
    private JRadioButton rbNumber = null;
    private JRadioButton rbString = null;
    private JRadioButton rbDate = null;
    private JScrollPane jScrollPane2 = null;
    private JList listCommand = null;
    //private FieldDescription fieldDescriptor = null;
    //private FieldDescription[] fieldDescriptors = null;
    //private int selectedIndex = -1;
    private BSFManager interpreter = null; // Construct an interpreter
    private Index indexRow = null;
    private SelectableDataSource sds = null;
    private EvalExpresion evalExpresion=null;
    private IEditableSource ies=null;
    private static ArrayList operators=new ArrayList();
    public EvalExpresionDialog(Table table,BSFManager interpreter) {
        super();
        this.interpreter=interpreter;
        this.table = table;
        initialize();

    }
    /**
     * Evaluate operators.
     *
     * @param interpreter
     * @throws BSFException
     *
     * @throws EvalError
     */
    private void evalOperators(BSFManager interpreter) throws BSFException {
        //		 Evaluar las expresiones que aporta cada Operator.
        long t1=System.currentTimeMillis();
    	//int type=getType();
        ExtensionPoints extensionPoints = ExtensionPointsSingleton.getInstance();
        ExtensionPoint extensionPoint =(ExtensionPoint)extensionPoints.get("ColumnOperators");
        Iterator iterator = extensionPoint.keySet().iterator();
        while (iterator.hasNext()) {
            try {
                IOperator operator = (IOperator)extensionPoint.create((String)iterator.next());
                operator.eval(interpreter);
                operators.add(operator);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (ClassCastException e) {
                e.printStackTrace();
            }
        }
//        Operators operatos=new Operators();
//        operatos.eval(interpreter);
        long t2=System.currentTimeMillis();
        System.out.println("Tiempo en evaluar y crear del extension point = "+(t2-t1) );
        //refreshOperators(IOperator.NUMBER);
        long t3=System.currentTimeMillis();
        System.out.println("Tiempo en añadir los operadores correctos = "+(t3-t2) );

    }
    /**
     * This method initializes this
     */
    private void initialize() {
    	try {
	        if (operators.isEmpty()) {
	        	evalOperators(interpreter);
			}
	        evalExpresions();
        } catch (BSFException e) {
			e.printStackTrace();
		}
	    evalExpresion=new EvalExpresion();
	    evalExpresion.setTable(table);
	    lv = (FLyrVect) table.getModel().getAssociatedTable();
        ButtonGroup bg = new ButtonGroup();
        bg.add(getRbNumber());
        bg.add(getRbString());
        bg.add(getRbDate());
        this.setLayout(new BorderLayout());
        this.setSize(549, 372);
        this.add(getTabPrincipal(), java.awt.BorderLayout.CENTER);
        this.add(getAcceptCancel(), java.awt.BorderLayout.SOUTH);
    }

    /**
     * This method initializes pCentral
     *
     * @return javax.swing.JPanel
     */
    private JPanel getPNorth() {
        if (pNorth == null) {
            pNorth = new JPanel();
            pNorth.setLayout(new BorderLayout());
            pNorth.add(getPNorthEast(), java.awt.BorderLayout.EAST);
            pNorth.add(getPNorthCenter(), java.awt.BorderLayout.CENTER);
            pNorth.add(getPNorthWest(), java.awt.BorderLayout.WEST);
        }

        return pNorth;
    }

    /**
     * This method initializes pNorth
     *
     * @return javax.swing.JPanel
     */
    private JPanel getPCentral() {
        if (pCentral == null) {
            pCentral = new JPanel();
            pCentral.setBorder(javax.swing.BorderFactory.createTitledBorder(
                    null, PluginServices.getText(this,"expresion"),
                    javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                    javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
            lblColumn = new JLabel();
            pCentral.add(lblColumn, null);

            pCentral.add(getJScrollPane(), null);
            lblColumn.setText(PluginServices.getText(this, "column") + " : " +
                evalExpresion.getFieldDescriptorSelected().getFieldAlias());
            pCentral.add(getBClear(), null);
        }

        return pCentral;
    }

    /**
     * This method initializes pSouth
     *
     * @return javax.swing.JPanel
     */
    private AcceptCancelPanel getAcceptCancel() {
        if (this.acceptCancel == null) {
            this.acceptCancel = new AcceptCancelPanel(new java.awt.event.ActionListener() {
                        public void actionPerformed(
                            java.awt.event.ActionEvent e) {
                            try {
                            	long t1=System.currentTimeMillis();
                                evalExpresion();
                                long t2=System.currentTimeMillis();
                                System.out.println("Tiempo evaluar expresiones = "+(t2-t1));
                            } catch (DriverException e1) {
                                e1.printStackTrace();
                            } catch (IOException e1) {
								e1.printStackTrace();
							} catch (DriverIOException e1) {
								e1.printStackTrace();
							} catch (BSFException e1) {
								e1.printStackTrace();
							}

                            PluginServices.getMDIManager().closeWindow(EvalExpresionDialog.this);
                        }
                    },
                    new java.awt.event.ActionListener() {
                        public void actionPerformed(
                            java.awt.event.ActionEvent e) {
                            PluginServices.getMDIManager().closeWindow(EvalExpresionDialog.this);
                        }
                    });

            //this.acceptCancel.setBounds(5,150,this.getWidth()-10,30);
        }

        return this.acceptCancel;
    }
    /**
     * Evaluate the expresion.
     *
     * @throws EvalError
     * @throws DriverException
     * @throws DriverIOException
     * @throws IOException
     * @throws DriverIOException
     * @throws IOException
     * @throws BSFException
     */
    private void evalExpresion()
        throws DriverException, IOException, DriverIOException, BSFException {
        long rowCount = sds.getRowCount();
        interpreter.eval(ExpresionFieldExtension.BEANSHELL,null,-1,-1,"java.lang.Object myF () {" +getTxtExp().getText()+ ";};");
        if (rowCount > 0) {
            try {
                interpreter.eval(ExpresionFieldExtension.BEANSHELL,null,-1,-1,"myF()");
            } catch (BSFException ee) {
                int option=JOptionPane.showConfirmDialog((Component) PluginServices.getMainFrame(),
                    PluginServices.getText(this,
                        "error_expresion")+ee+PluginServices.getText(this,"continue?"));
                if (option!=JOptionPane.OK_OPTION) {
                	return;
                }
            }
        }
        ArrayList errors = new ArrayList();
        ies.startComplexRow();
        interpreter.declareBean("ee",evalExpresion,EvalExpresion.class);
        String p= "for (int i=0; i < " +rowCount + "; i++){" +
		"indexRow.set(i);" +
		"java.lang.Object obj = myF();" +
		//"long t1 = System.currentTimeMillis();" +
		"ee.setValue(obj,i);" +
		"ee.saveEdits(i);" +
		//"t2 = System.currentTimeMillis();" +
		//"print(\"Obj = \" + obj.toString());" +
		"}" +
		"ee.saveEdits(-1);" +
		"";
        interpreter.eval(ExpresionFieldExtension.BEANSHELL,null,-1,-1,p);
        ies.endComplexRow(PluginServices.getText(this, "expresion"));
//        try {
//        	saveEdits();
//		} catch (DriverLoadException e) {
//			e.printStackTrace();
//		} catch (EditionException e) {
//			e.printStackTrace();
//		} catch (DriverException e) {
//			e.printStackTrace();
//		} catch (com.iver.cit.gvsig.fmap.DriverException e) {
//			e.printStackTrace();
//		}
        if (!errors.isEmpty()) {
            JOptionPane.showMessageDialog((Component) PluginServices.getMainFrame(),
                PluginServices.getText(this, "evaluate_expresion_with_errors") + " = " +
                errors.size());
        }
        table.refresh();
    }

    private void evalExpresions() throws BSFException {
        ies = table.getModel().getModelo();
        sds = ies.getRecordset();
        interpreter.declareBean("sds", sds,SelectableDataSource.class);
        indexRow=new Index();
        interpreter.declareBean("indexRow", indexRow,Index.class);
    }
    /**
     * Evaluate the fields.
     *
     * @param interpreter
     *
     * @throws EvalError
     */
    int lastType=-1;
	private JButton bClear = null;
	private JTabbedPane tabPrincipal = null;
	private JPanel pPrincipal = null;
	private JPanel pAdvanced = null;
	private JPanel pAdvancedNorth = null;
	private JTextField jTextField = null;
	private JButton bFile = null;
	private JPanel pAdvancedCenter = null;
	private JLabel lblLeng = null;
	private JButton bEval = null;
	private void refreshOperators(int type) {
        if (lastType!=-1 && lastType==type)
        	return;
        lastType=type;
    	ListOperatorsModel lom=(ListOperatorsModel)getListCommand().getModel();
        lom.clear();
           for (int i=0;i<operators.size();i++) {
            IOperator operator = (IOperator)operators.get(i);
            operator.setType(type);
            //Comprobar si tiene una capa asociada y pasarsela al GraphicOperator.
            if ((lv != null) && operator instanceof GraphicOperator) {
                GraphicOperator igo = (GraphicOperator) operator;
                igo.setLayer(lv);
            }
            if (operator.isEnable()) {
                   lom.addOperator(operator);
                   //System.out.println("Operator = "+operator.toString());
            }
        }
        getListCommand().repaint();

    }
    /**
     * This method initializes jScrollPane
     *
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getJScrollPane() {
        if (jScrollPane == null) {
            jScrollPane = new JScrollPane();
            jScrollPane.setPreferredSize(new java.awt.Dimension(480, 80));
            jScrollPane.setViewportView(getTxtExp());
        }

        return jScrollPane;
    }

    /**
     * This method initializes txtExp
     *
     * @return javax.swing.JTextArea
     */
    private JTextArea getTxtExp() {
        if (txtExp == null) {
            txtExp = new JTextArea();
        }

        return txtExp;
    }

    public WindowInfo getWindowInfo() {
        WindowInfo wi = new WindowInfo(WindowInfo.MODALDIALOG);
        wi.setTitle(PluginServices.getText(this, "calculate_expresion"));

        return wi;
    }

    /**
     * This method initializes pNorthEast
     *
     * @return javax.swing.JPanel
     */
    private JPanel getPNorthEast() {
        if (pNorthEast == null) {
            pNorthEast = new JPanel();
            pNorthEast.setBorder(javax.swing.BorderFactory.createTitledBorder(
                    null, PluginServices.getText(this,"commands"),
                    javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                    javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
            pNorthEast.add(getJScrollPane2(), null);
        }

        return pNorthEast;
    }

    /**
     * This method initializes pNorthCenter
     *
     * @return javax.swing.JPanel
     */
    private JPanel getPNorthCenter() {
        if (pNorthCenter == null) {
            pNorthCenter = new JPanel();
            pNorthCenter.setLayout(new BoxLayout(getPNorthCenter(),
                    BoxLayout.Y_AXIS));
            pNorthCenter.setBorder(javax.swing.BorderFactory.createTitledBorder(
                    null, PluginServices.getText(this,"type"),
                    javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                    javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
            pNorthCenter.add(getRbNumber(), null);
            pNorthCenter.add(getRbString(), null);
            pNorthCenter.add(getRbDate(), null);
        }

        return pNorthCenter;
    }

    /**
     * This method initializes pNorthWest
     *
     * @return javax.swing.JPanel
     */
    private JPanel getPNorthWest() {
        if (pNorthWest == null) {
            pNorthWest = new JPanel();
            pNorthWest.setBorder(javax.swing.BorderFactory.createTitledBorder(
                    null, PluginServices.getText(this,"field"),
                    javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                    javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
            pNorthWest.add(getJScrollPane1(), null);
        }

        return pNorthWest;
    }

    /**
     * This method initializes jScrollPane1
     *
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getJScrollPane1() {
        if (jScrollPane1 == null) {
            jScrollPane1 = new JScrollPane();
            jScrollPane1.setPreferredSize(new java.awt.Dimension(175, 100));
            jScrollPane1.setViewportView(getListFields());
        }

        return jScrollPane1;
    }

    /**
     * This method initializes listFields
     *
     * @return javax.swing.JList
     */
    private JList getListFields() {
        if (listFields == null) {
            listFields = new JList();
            listFields.setModel(new ListOperatorsModel());

            ListOperatorsModel lm = (ListOperatorsModel) listFields.getModel();
            FieldDescription[] fds=evalExpresion.getFieldDescriptors();
            for (int i = 0; i < fds.length; i++) {
                Field field=new Field();
                field.setFieldDescription(fds[i]);
                try {
                	field.eval(interpreter);
                } catch (BSFException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                lm.addOperator(field);
            }

            listFields.addMouseListener(new MouseListener() {
                    public void mouseClicked(MouseEvent e) {
                        if (e.getClickCount() == 2) {
                            getTxtExp().setText(((IOperator) listFields.getSelectedValue()).addText(
                                    getTxtExp().getText()));
                        }
                    }

                    public void mouseEntered(MouseEvent e) {
                    }

                    public void mouseExited(MouseEvent e) {
                    }

                    public void mousePressed(MouseEvent e) {
                    }

                    public void mouseReleased(MouseEvent e) {
                    }
                });
        }

        return listFields;
    }

    /**
     * This method initializes rbNumber
     *
     * @return javax.swing.JRadioButton
     */
    private JRadioButton getRbNumber() {
        if (rbNumber == null) {
            rbNumber = new JRadioButton();
            rbNumber.setText(PluginServices.getText(this,"numeric"));
            rbNumber.setSelected(true);
            rbNumber.addChangeListener(new javax.swing.event.ChangeListener() {
                public void stateChanged(javax.swing.event.ChangeEvent e) {
                     if (rbNumber.isSelected())
                         refreshCommands();
                }
            });
        }

        return rbNumber;
    }

    /**
     * This method initializes rbString
     *
     * @return javax.swing.JRadioButton
     */
    private JRadioButton getRbString() {
        if (rbString == null) {
            rbString = new JRadioButton();
            rbString.setText(PluginServices.getText(this,"string"));
            rbString.addChangeListener(new javax.swing.event.ChangeListener() {
                public void stateChanged(javax.swing.event.ChangeEvent e) {
                     if (rbString.isSelected())
                         refreshCommands();
                }
            });
        }

        return rbString;
    }

    /**
     * This method initializes rbData
     *
     * @return javax.swing.JRadioButton
     */
    private JRadioButton getRbDate() {
        if (rbDate == null) {
            rbDate = new JRadioButton();
            rbDate.setText(PluginServices.getText(this,"date"));
            rbDate.addChangeListener(new javax.swing.event.ChangeListener() {
                public void stateChanged(javax.swing.event.ChangeEvent e) {
                    if (rbDate.isSelected())
                         refreshCommands();
                }
            });
        }

        return rbDate;
    }

    /**
     * This method initializes jScrollPane2
     *
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getJScrollPane2() {
        if (jScrollPane2 == null) {
            jScrollPane2 = new JScrollPane();
            jScrollPane2.setPreferredSize(new java.awt.Dimension(175, 100));
            jScrollPane2.setViewportView(getListCommand());
        }

        return jScrollPane2;
    }

    /**
     * Refresh the commands.
     */
    private void refreshCommands() {
        int type=IOperator.NUMBER;
        if (getRbNumber().isSelected()) {
            type=IOperator.NUMBER;
        } else if (getRbString().isSelected()) {
            type=IOperator.STRING;
        } else if (getRbDate().isSelected()) {
            type=IOperator.DATE;
        }
        refreshOperators(type);

    }

    /**
     * This method initializes ListCommand
     *
     * @return javax.swing.JList
     */
    private JList getListCommand() {
        if (listCommand == null) {
            listCommand = new JList();
            listCommand.setModel(new ListOperatorsModel());
            listCommand.addMouseListener(new MouseListener() {
                    public void mouseClicked(MouseEvent e) {
                        if (e.getClickCount() == 2) {
                        	if (listCommand.getSelectedValue()==null)
                        		return;
                            getTxtExp().setText(((IOperator) listCommand.getSelectedValue()).addText(
                                    getTxtExp().getText()));
                        }
                    }

                    public void mouseEntered(MouseEvent e) {
                    }

                    public void mouseExited(MouseEvent e) {
                    }

                    public void mousePressed(MouseEvent e) {
                    }

                    public void mouseReleased(MouseEvent e) {
                    }
                });
            refreshOperators(IOperator.NUMBER);
        }

        return listCommand;
    }

    /**
	 * This method initializes bClear
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getBClear() {
		if (bClear == null) {
			bClear = new JButton();
			bClear.setText(PluginServices.getText(this,"clear_expresion"));
			bClear.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					getTxtExp().setText("");
				}
			});
		}
		return bClear;
	}
	/**
	 * This method initializes tabPrincipal
	 *
	 * @return javax.swing.JTabbedPane
	 */
	private JTabbedPane getTabPrincipal() {
		if (tabPrincipal == null) {
			tabPrincipal = new JTabbedPane();
			tabPrincipal.addTab(PluginServices.getText(this,"general"), null, getPPrincipal(), null);
			tabPrincipal.addTab(PluginServices.getText(this,"advanced"), null, getPAdvanced(), null);
		}
		return tabPrincipal;
	}
	/**
	 * This method initializes pPrincipal
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getPPrincipal() {
		if (pPrincipal == null) {
			pPrincipal = new JPanel();
			pPrincipal.setLayout(new BorderLayout());
			pPrincipal.setPreferredSize(new java.awt.Dimension(540,252));
			pPrincipal.add(getPNorth(), java.awt.BorderLayout.NORTH);
			pPrincipal.add(getPCentral(), java.awt.BorderLayout.CENTER);

		}
		return pPrincipal;
	}
	/**
	 * This method initializes pAdvanced
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getPAdvanced() {
		if (pAdvanced == null) {
			pAdvanced = new JPanel();
			pAdvanced.setLayout(new BorderLayout());
			pAdvanced.add(getPAdvancedNorth(), java.awt.BorderLayout.NORTH);
			pAdvanced.add(getPAdvancedCenter(), java.awt.BorderLayout.CENTER);
		}
		return pAdvanced;
	}
	/**
	 * This method initializes pAdvancedNorth
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getPAdvancedNorth() {
		if (pAdvancedNorth == null) {
			pAdvancedNorth = new JPanel();
			pAdvancedNorth.setPreferredSize(new java.awt.Dimension(873,100));
			pAdvancedNorth.setBorder(javax.swing.BorderFactory.createTitledBorder(null, PluginServices.getText(this,"expresions_from_file"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
			pAdvancedNorth.add(getJTextField(), null);
			pAdvancedNorth.add(getBFile(), null);
			pAdvancedNorth.add(getBEval(), null);
		}
		return pAdvancedNorth;
	}
	/**
	 * This method initializes jTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextField() {
		if (jTextField == null) {
			jTextField = new JTextField();
			jTextField.setPreferredSize(new java.awt.Dimension(250,20));
		}
		return jTextField;
	}
	/**
	 * This method initializes bFile
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getBFile() {
		if (bFile == null) {
			bFile = new JButton();
			bFile.setText(PluginServices.getText(this,"explorer"));
			bFile.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					JFileChooser jfc = new JFileChooser();
					jfc.addChoosableFileFilter(new GenericFileFilter("bs",
							PluginServices.getText(this, "expresions")));

					if (jfc.showOpenDialog((Component) PluginServices.getMainFrame()) == JFileChooser.APPROVE_OPTION) {
						File fileExpresion = jfc.getSelectedFile();
						getJTextField().setText(fileExpresion.getAbsolutePath());

					}
				}
				});
		}
		return bFile;
	}
	private String readFile(File aFile) throws IOException {
		StringBuffer fileContents = new StringBuffer();
		FileReader fileReader = new FileReader(aFile);
		int c;
		while ((c = fileReader.read()) > -1) {
			fileContents.append((char)c);
		}
		fileReader.close();
		return fileContents.toString();
	}
	/**
	 * This method initializes pAdvancedCenter
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getPAdvancedCenter() {
		if (pAdvancedCenter == null) {
			lblLeng = new JLabel();
			lblLeng.setText("");
			pAdvancedCenter = new JPanel();
			pAdvancedCenter.add(lblLeng, null);
		}
		return pAdvancedCenter;
	}

	/**
	 * This method initializes bEval
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getBEval() {
		if (bEval == null) {
			bEval = new JButton();
			bEval.setText(PluginServices.getText(this,"evaluate"));
			bEval.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					File file=new File(getJTextField().getText());
					if (!file.exists()) {
						JOptionPane.showMessageDialog((Component)PluginServices.getMainFrame(),PluginServices.getText(this,"incorrect_file"));
						return;
					}
					try {
						interpreter.eval(ExpresionFieldExtension.BEANSHELL,null,-1,-1,readFile(file));
					} catch (IOException e1) {
						e1.printStackTrace();
					} catch (BSFException e1) {
						e1.printStackTrace();
					}
				}
			});
		}
		return bEval;
	}
} //  @jve:decl-index=0:visual-constraint="10,10"
