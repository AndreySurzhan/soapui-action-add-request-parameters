package src;

import com.eviware.soapui.SoapUI;
import com.eviware.soapui.impl.rest.RestMethod;
import com.eviware.soapui.impl.rest.RestRequest;
import com.eviware.soapui.impl.rest.RestResource;
import com.eviware.soapui.impl.rest.RestService;
import com.eviware.soapui.impl.rest.actions.support.NewRestResourceActionBase;
import com.eviware.soapui.impl.rest.support.RestParamProperty;
import com.eviware.soapui.impl.rest.support.RestParameter;
import com.eviware.soapui.impl.rest.support.RestParamsPropertyHolder;
import com.eviware.soapui.impl.support.AbstractInterface;
import com.eviware.soapui.impl.wsdl.WsdlInterface;
import com.eviware.soapui.impl.wsdl.WsdlOperation;
import com.eviware.soapui.impl.wsdl.WsdlProject;
import com.eviware.soapui.impl.wsdl.WsdlRequest;
import com.eviware.soapui.model.ModelItem;
import com.eviware.soapui.model.iface.Interface;
import com.eviware.soapui.model.iface.MessagePart;
import com.eviware.soapui.model.iface.Operation;
import com.eviware.soapui.model.iface.Request;
import com.eviware.soapui.model.project.Project;
import com.eviware.soapui.model.testsuite.TestProperty;
import com.eviware.soapui.model.workspace.Workspace;
import com.eviware.soapui.support.UISupport;
import com.eviware.soapui.support.action.support.AbstractSoapUIAction;
import com.eviware.soapui.support.types.StringList;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.xml.namespace.QName;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AddRequestParameters extends AbstractSoapUIAction
{
    public AddRequestParameters()
    {
        super( "Add Auth Properties", "Add Auth Properties to test cases" );
    }

    public void perform(ModelItem target, Object param )
    {
        class IFace {
            private AbstractInterface iFace;
            private String iFaceName;

            public IFace (AbstractInterface iFace) {
                this.iFace = iFace;
                this.iFaceName = iFace.getName();
            }

            public AbstractInterface getIFace () {
                return this.iFace;
            }

            public String getIFaceName () {
                return this.iFaceName;
            }

            public String toString() {
                return this.iFaceName;
            }

            public void setIFace (IFace iFace) {
                this.iFace = iFace.getIFace();
                this.iFaceName = iFace.getIFaceName();
            }
        }

        class Proj {
            private WsdlProject project;
            private String projectName;

            public Proj (WsdlProject project) {
                this.project = project;
                this.projectName = project.getName();
            }

            public WsdlProject getProject () {
                return this.project;
            }

            public String getProjectName () {
                return this.projectName;
            }

            public String toString () {
                return this.projectName;
            }

            public void setProject (Proj project) {
                this.project = project.getProject();
                this.projectName = project.getProjectName();
            }
        }

        class Oper {
            private RestResource operation;
            private String operationName;

            public Oper (RestResource operation) {
                this.operation = operation;
                this.operationName = operation.getName();
            }

            public RestResource getOperation () {
                return this.operation;
            }

            public String getOperationName () {
                return this.operationName;
            }

            public String toString () {
                return this.operationName;
            }

            public void setOperation (Oper operation) {
                this.operation = operation.getOperation();
                this.operationName = operation.getOperationName();
            }
        }

        class RequestCheckBox {
            private RestRequest restRequest;
            private JCheckBox requestCheckBox;

            public RequestCheckBox (RestRequest restRequest, JCheckBox requestCheckBox) {
                this.restRequest = restRequest;
                this.requestCheckBox = requestCheckBox;
            }

            public RestRequest getRestRequest () {
                return this.restRequest;
            }

            public JCheckBox getCheckBox () {
                return this.requestCheckBox;
            }

            public String toString () {
                return this.restRequest.getName();
            }

            public void setRequestCheckBox (RestRequest restRequest, JCheckBox requestCheckBox) {
                this.restRequest = restRequest;
                this.requestCheckBox = requestCheckBox;
            }
        }

        final JDialog newDialog = new JDialog();
        final JPanel newPanel = new JPanel();
        final JPanel requestCheckBoxesPanel = new JPanel();
        final JPanel comBoxesPanel = new JPanel();

        final JComboBox projectComboBox = new JComboBox();
        final JComboBox serviceComboBox = new JComboBox();

        final JPanel requestParamsPanel = new JPanel(new FlowLayout());
        final JButton addRequestParamButton = addRequestParamButtonGUI();

        final JPanel okCancelButtonsPanel = new JPanel(new FlowLayout());
        final JButton okButton = new JButton("OK");
        final JButton cancelButton = new JButton("CANCEL");

        final Workspace workspace = SoapUI.getWorkspace();
        List<? extends Project> projects = workspace.getProjectList();

        for (Project project : projects) {
            WsdlProject proj;
            proj = (WsdlProject) project;
            projectComboBox.addItem(new Proj(proj));
        }

        projectComboBox.setSelectedIndex(3);
        final Proj selectedProject = (Proj) projectComboBox.getSelectedItem();
        List<Interface> iFaces = selectedProject.getProject().getInterfaceList();

        for (Interface iFace : iFaces) {
            AbstractInterface inter;
            inter = (AbstractInterface) iFace;
            serviceComboBox.addItem(new IFace(inter));
        }

        serviceComboBox.setSelectedIndex(0);
        final IFace selectedIFace = (IFace) serviceComboBox.getSelectedItem();
        Operation[] operations = selectedIFace.getIFace().getAllOperations();
        final List<RequestCheckBox> requestCheckBoxes = new ArrayList<RequestCheckBox>();

        for (Operation operation: operations) {
            RestResource op;
            op = (RestResource) operation;
            List<Request> reqs = op.getRequestList();

            for (Request req : reqs) {
                RestRequest request;
                request = (RestRequest) req;

                //Show list of checkboxes
                JCheckBox reqCheckbox = new JCheckBox("Resource: '" + op.getName() + "' Request: '" + request.getName() + "'");
                RequestCheckBox requestCheckBox = new RequestCheckBox(request, reqCheckbox);

                requestCheckBox.getCheckBox().setSelected(false);
                requestCheckBoxes.add(requestCheckBox);
                requestCheckBoxesPanel.add(requestCheckBox.getCheckBox());
            }
        }

//        final RestResource req = (RestResource) operations[0];
//
//        final RestParamsPropertyHolder params = req.getParams();
//
        ///OK BUTTON SETTINGS
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                for (RequestCheckBox requestCheckBox : requestCheckBoxes) {
                    if (requestCheckBox.getCheckBox().isSelected()) {
                        JLabel isSelectedLabel = new JLabel();
                        isSelectedLabel.setText("=====SELECTED: " + requestCheckBox.getRestRequest().getName());
                        requestCheckBoxesPanel.add(isSelectedLabel);
                        requestCheckBoxesPanel.revalidate();
                    }
                }

//                RestParamProperty newParamProp = params.addProperty("NEW Plugin Param Prop");
//                newParamProp.setDefaultValue(new String("Plugin VALUE"));
//                newParamProp.setStyle(RestParamsPropertyHolder.ParameterStyle.TEMPLATE);
            }
        } );
        ///OK BUTTON SETTINGS ENDS

        ///ADD PARAM BUTTON SETTINGS
        addRequestParamButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JButton addReqParamButton = addRequestParamButtonGUI();
                addReqParamButton.addActionListener(this);

                requestParamsPanel.add(newRequestParamPanelGUI(addReqParamButton));
                requestParamsPanel.revalidate();
            }
        });
        ///ADD PARAM BUTTON SETTINGS ENDS

        projectComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
//                JComboBox<Proj> projectComboBox = (JComboBox<Proj>) event.getSource();
//                Proj selectedItem = (Proj) projectComboBox.getSelectedItem();
//
//                List<? extends Interface> iFaces = selectedItem.getProject().getInterfaceList();
//
//                serviceComboBox.setModel( new DefaultComboBoxModel() );
//
////                for (Interface iFace : iFaces) {
////                    serviceComboBox.addItem(new IFace(iFace));
////                }
//
//                serviceComboBox.setSelectedIndex(0);
            }
        });

        serviceComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
//                JComboBox<IFace> serviceComboBox = (JComboBox<IFace>) event.getSource();
//                selectedIFace.setIFace((IFace) serviceComboBox.getSelectedItem());
//
//                List<Operation> operations = selectedIFace.getIFace().getOperationList();
//
//                operationsPanel.removeAll();
//
//                if (operations != null) {
//                    for (Operation operation : operations) {
//
//                        List<Request> requests = operation.getRequestList();
//
//                        if (requests != null) {
//                            for (Request request : requests) {
//                                JLabel operationLabel = new JLabel("Operation - " + operation.getName() + ": " + request.getName());
//                                operationsPanel.add(operationLabel);
//                            }
//                        }
//                    }
//                }
            }
        });
        ///PROJECT COMBOBOX SETTINGS ENDS


        ///Layout settings

        newDialog.setSize(400, 400);

        newPanel.setLayout(new BoxLayout(newPanel, BoxLayout.Y_AXIS));
        comBoxesPanel.setLayout(new BoxLayout(comBoxesPanel, BoxLayout.Y_AXIS));
        requestCheckBoxesPanel.setLayout(new BoxLayout(requestCheckBoxesPanel, BoxLayout.Y_AXIS));
        requestParamsPanel.setLayout(new BoxLayout(requestParamsPanel, BoxLayout.Y_AXIS));

        okCancelButtonsPanel.add(okButton);
        okCancelButtonsPanel.add(cancelButton);

        comBoxesPanel.add(projectComboBox);
        comBoxesPanel.add(serviceComboBox);

        requestParamsPanel.add(newRequestParamPanelGUI(addRequestParamButton));

        newPanel.add(comBoxesPanel);
        newPanel.add(requestCheckBoxesPanel);
        newPanel.add(requestParamsPanel);
        newPanel.add(okCancelButtonsPanel);

        newDialog.add(newPanel);

        // iterate though all test cases adding auth props
        UISupport.showDialog(newDialog);
    }

    private void operationComboboxAction() {

    }

    private JPanel requestParamPanelGUI() {
        JPanel requestParamPanel = new JPanel(new FlowLayout());
        JComboBox requestStyleComBox = new JComboBox();
        JTextField requestParamNameTextField = new JTextField(10);
        JTextField requestParamValueTextField = new JTextField(10);
        for (RestParamsPropertyHolder.ParameterStyle requestStyle : RestParamsPropertyHolder.ParameterStyle.values()) {
            requestStyleComBox.addItem(requestStyle);
        }
        requestParamPanel.add(requestParamNameTextField);
        requestParamPanel.add(requestParamValueTextField);
        requestParamPanel.add(requestStyleComBox);

        return requestParamPanel;
    }

    private JButton addRequestParamButtonGUI() {
        JButton addRequestParamButton = new JButton();
        try {
            Image img = ImageIO.read(getClass().getResource("addIcon.png"));
            img = img.getScaledInstance(20, 20, Image.SCALE_DEFAULT);
            addRequestParamButton.setIcon(new ImageIcon(img));
        } catch (Exception ex) {
            System.out.println(ex);
        }

        return addRequestParamButton;
    }

    private JPanel newRequestParamPanelGUI(JButton addRequestParamButton) {
        JPanel newRequestParamPanel = new JPanel();
        newRequestParamPanel.add(requestParamPanelGUI());
        newRequestParamPanel.add(addRequestParamButton);

        return newRequestParamPanel;
    }
}