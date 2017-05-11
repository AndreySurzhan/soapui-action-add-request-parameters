package com.soapui_action_add_request_parameters;

import com.eviware.soapui.SoapUI;
import com.eviware.soapui.impl.rest.RestRequest;
import com.eviware.soapui.impl.rest.RestResource;
import com.eviware.soapui.impl.rest.support.RestParamProperty;
import com.eviware.soapui.impl.rest.support.RestParamsPropertyHolder;
import com.eviware.soapui.impl.support.AbstractInterface;
import com.eviware.soapui.impl.wsdl.WsdlProject;
import com.eviware.soapui.model.ModelItem;
import com.eviware.soapui.model.iface.Interface;
import com.eviware.soapui.model.iface.Operation;
import com.eviware.soapui.model.iface.Request;
import com.eviware.soapui.model.project.Project;
import com.eviware.soapui.model.workspace.Workspace;
import com.eviware.soapui.support.UISupport;
import com.eviware.soapui.support.action.support.AbstractSoapUIAction;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class AddRequestParameters extends AbstractSoapUIAction
{
    public AddRequestParameters()
    {
        super( "Add Request Parameters", "Add specified parameters to selected requests" );
    }

    public void perform(ModelItem target, Object param )
    {
        ///Define GUI
        final JDialog jDialog = new JDialog();
        final JPanel globalPanel = new JPanel();
        final JPanel requestCheckBoxesPanel = new JPanel();
        final JScrollPane requestCheckBoxesScrollPane = new JScrollPane(requestCheckBoxesPanel);
        final JPanel comBoxesPanel = new JPanel();

        final JComboBox projectComboBox = new JComboBox();
        final JComboBox serviceComboBox = new JComboBox();

        final JPanel requestParamsPanel = new JPanel(new FlowLayout());
        final JButton addRequestParamButton = buildAddRequestParamInputsButtonGUI();

        final JPanel okCancelButtonsPanel = new JPanel(new FlowLayout());
        final JButton okButton = new JButton("OK");
        final JButton cancelButton = new JButton("CANCEL");

        final Workspace workspace = SoapUI.getWorkspace();
        List<? extends Project> projects = workspace.getProjectList();
        final List<RequestCheckBox> requestCheckBoxes = new ArrayList<RequestCheckBox>();
        final List<RequestParamGUI> requestParamGUIList = new ArrayList<RequestParamGUI>();

        RequestParamGUI requestParamGUI = new RequestParamGUI();

        requestParamGUIList.add(requestParamGUI);

        for (Project project : projects) {
            WsdlProject proj;
            proj = (WsdlProject) project;
            projectComboBox.addItem(new Proj(proj));
        }

        projectComboBox.setSelectedIndex(0);

        Proj selectedProject = (Proj) projectComboBox.getSelectedItem();
        setServiceComBoxGUI(serviceComboBox, selectedProject);

        IFace selectedIFace = (IFace) serviceComboBox.getSelectedItem();
        setRequestsCheckBoxListGUI(requestCheckBoxesPanel, selectedIFace, requestCheckBoxes);

        ///ACTION LISTENERS
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (requestCheckBoxes.size() == 0) {
                    return;
                }

                for (RequestCheckBox requestCheckBox : requestCheckBoxes) {
                    if (!requestCheckBox.getCheckBox().isSelected()) {
                        continue;
                    }

                    for (RequestParamGUI paramGUI : requestParamGUIList) {
                        if (paramGUI.requestParamNameTextField.getText().isEmpty()) {
                            continue;
                        }

                        RestParamsPropertyHolder param = requestCheckBox.getRestRequest().getParams();
                        RestParamProperty newParamProp = param.addProperty(
                                paramGUI.requestParamNameTextField.getText());
                        newParamProp.setValue(paramGUI.requestParamValueTextField.getText());
                        newParamProp.setStyle((RestParamsPropertyHolder.ParameterStyle)
                                paramGUI.requestParamStyleComboBox.getSelectedItem());
                    }
                }
            }
        } );

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jDialog.dispose();
            }
        });

        addRequestParamButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JButton addReqParamButton = buildAddRequestParamInputsButtonGUI();
                RequestParamGUI requestParamGUI = new RequestParamGUI();

                requestParamGUIList.add(requestParamGUI);
                addReqParamButton.addActionListener(this);

                requestParamsPanel.add(buildRequestParamPanelGUI(addReqParamButton, requestParamGUI));
                requestParamsPanel.revalidate();
            }
        });

        projectComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                JComboBox<Proj> projectComboBox = (JComboBox<Proj>) event.getSource();
                Proj selectedProject = (Proj) projectComboBox.getSelectedItem();
                requestCheckBoxes.clear();
                requestCheckBoxesPanel.removeAll();
                requestCheckBoxesPanel.revalidate();

                setServiceComBoxGUI(serviceComboBox, selectedProject);
            }
        });

        serviceComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                JComboBox<IFace> serviceComboBox = (JComboBox<IFace>) event.getSource();

                requestCheckBoxes.clear();
                requestCheckBoxesPanel.removeAll();

                if (serviceComboBox.getItemCount() == 0) {
                    requestCheckBoxesPanel.revalidate();

                    return;
                }

                IFace selectedIFace = (IFace) serviceComboBox.getSelectedItem();
                setRequestsCheckBoxListGUI(requestCheckBoxesPanel, selectedIFace, requestCheckBoxes);

                requestCheckBoxesPanel.revalidate();
            }
        });

        ///LAYOUT SETTINGS
        jDialog.setSize(800, 600);

        globalPanel.setLayout(new BoxLayout(globalPanel, BoxLayout.Y_AXIS));
        comBoxesPanel.setLayout(new BoxLayout(comBoxesPanel, BoxLayout.Y_AXIS));
        requestCheckBoxesPanel.setLayout(new BoxLayout(requestCheckBoxesPanel, BoxLayout.Y_AXIS));
        requestParamsPanel.setLayout(new BoxLayout(requestParamsPanel, BoxLayout.Y_AXIS));

        //Scroll panel settings from list of request checkboxes
        requestCheckBoxesScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        requestCheckBoxesScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        okCancelButtonsPanel.add(okButton);
        okCancelButtonsPanel.add(cancelButton);

        comBoxesPanel.add(projectComboBox);
        comBoxesPanel.add(serviceComboBox);

        requestParamsPanel.add(buildRequestParamPanelGUI(addRequestParamButton, requestParamGUI));

        globalPanel.add(comBoxesPanel);
        globalPanel.add(requestCheckBoxesScrollPane);
        globalPanel.add(requestParamsPanel);
        globalPanel.add(okCancelButtonsPanel);

        jDialog.add(globalPanel);

        UISupport.showDialog(jDialog);
    }

    ///PRIVATE FUNCTIONS
    private void setServiceComBoxGUI(JComboBox serviceComboBox, Proj project) {
        //Clean combox content
        serviceComboBox.setModel(new DefaultComboBoxModel());
        List<Interface> iFaces = project.getProject().getInterfaceList();

        if (iFaces.size() == 0) {
            return;
        }

        for (Interface iFace : iFaces) {
            AbstractInterface inter;
            inter = (AbstractInterface) iFace;
            serviceComboBox.addItem(new IFace(inter));
        }

        serviceComboBox.setSelectedIndex(0);
    }

    private  void setRequestsCheckBoxListGUI(JPanel requestCheckBoxesPanel, IFace iFace, List<RequestCheckBox> requestCheckBoxes) {
        Operation[] operations = iFace.getIFace().getAllOperations();

        if (operations == null) {
            requestCheckBoxesPanel.revalidate();

            return;
        }

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

        requestCheckBoxesPanel.revalidate();
    }

    private JPanel buildRequestParamInputsPanelGUI(RequestParamGUI requestParamGUI) {
        JPanel requestParamPanel = new JPanel(new FlowLayout());

        requestParamPanel.add(requestParamGUI.requestParamNameTextField);
        requestParamPanel.add(requestParamGUI.requestParamValueTextField);
        requestParamPanel.add(requestParamGUI.requestParamStyleComboBox);

        return requestParamPanel;
    }

    private JButton buildAddRequestParamInputsButtonGUI() {
        JButton addRequestParamButton = new JButton();
        try {
            Image img = ImageIO.read(getClass().getResource("../../../images/addIcon.png"));
            img = img.getScaledInstance(20, 20, Image.SCALE_DEFAULT);
            addRequestParamButton.setIcon(new ImageIcon(img));
        } catch (Exception ex) {
            System.out.println(ex);
        }

        return addRequestParamButton;
    }

    private JPanel buildRequestParamPanelGUI(JButton addRequestParamButton, RequestParamGUI requestParamGUI) {
        JPanel newRequestParamPanel = new JPanel();
        newRequestParamPanel.add(buildRequestParamInputsPanelGUI(requestParamGUI));
        newRequestParamPanel.add(addRequestParamButton);

        return newRequestParamPanel;
    }

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

    class RequestParamGUI {
        public JTextField requestParamNameTextField;
        public JTextField requestParamValueTextField;
        public JComboBox requestParamStyleComboBox;

        private JComboBox buildRequestParamStyleComboBoxGUI () {
            JComboBox requestParamStyleComboBox = new JComboBox();
            for (RestParamsPropertyHolder.ParameterStyle requestStyle : RestParamsPropertyHolder.ParameterStyle.values()) {
                requestParamStyleComboBox.addItem(requestStyle);
            }

            return requestParamStyleComboBox;
        }

        public RequestParamGUI () {
            requestParamNameTextField = new JTextField(10);
            requestParamStyleComboBox = buildRequestParamStyleComboBoxGUI();
            requestParamValueTextField = new JTextField(10);
        }

        public RequestParamGUI getRequestParamGUI () {
            return this;
        }
    }
}
