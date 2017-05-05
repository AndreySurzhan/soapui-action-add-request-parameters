package src;

import com.eviware.soapui.SoapUI;
import com.eviware.soapui.impl.rest.RestMethod;
import com.eviware.soapui.impl.rest.RestRequest;
import com.eviware.soapui.impl.rest.RestResource;
import com.eviware.soapui.impl.rest.RestService;
import com.eviware.soapui.impl.rest.support.RestParamsPropertyHolder;
import com.eviware.soapui.impl.support.AbstractInterface;
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

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
            private Interface iFace;
            private String iFaceName;

            public IFace (Interface iFace) {
                this.iFace = iFace;
                this.iFaceName = iFace.getName();
            }

            public Interface getIFace () {
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
            private Project project;
            private String projectName;

            public Proj (Project project) {
                this.project = project;
                this.projectName = project.getName();
            }

            public Project getProject () {
                return this.project = project;
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

        final Workspace workspace = SoapUI.getWorkspace();
        List<? extends Project> projects = workspace.getProjectList();

        final JPanel newPanel = new JPanel();
        final JPanel operationsPanel = new JPanel();
        final JPanel requestPanel = new JPanel();

        newPanel.setLayout(new BoxLayout(newPanel, BoxLayout.Y_AXIS));
        operationsPanel.setLayout(new BoxLayout(operationsPanel, BoxLayout.Y_AXIS));
        requestPanel.setLayout(new BoxLayout(requestPanel, BoxLayout.Y_AXIS));


        ///TEST
        final JLabel nameLabel = new JLabel();
        final JLabel valueLabel = new JLabel();


        WsdlProject testProject = (WsdlProject) workspace.getProjectAt(0);

        AbstractInterface inter = testProject.getInterfaceAt(0);

        Operation[] opers = inter.getAllOperations();

        RestResource req = (RestResource) opers[0];

        String params = req.getPropertyValue("TEST NAME");

        nameLabel.setText("REQUEST NAME - " + req.getName());
        valueLabel.setText("REQUEST PROP VALUE - " + params);

        newPanel.add(nameLabel);
        newPanel.add(valueLabel);

        String[] props = req.getPropertyNames();

        for (String proper: props) {
            final JLabel propLabel = new JLabel();
            propLabel.setText("PROP - " + proper);

            newPanel.add(propLabel);

        }

        ///TEST

        ///Label SETTINGS
        final JLabel serviceLabel = new JLabel();
        newPanel.add(serviceLabel);
        ///Label SETTINGS ENDS

        ///PROJECT COMBOBOX SETTINGS
        final JComboBox projectComboBox = new JComboBox();

        for (Project project : projects) {
            projectComboBox.addItem(new Proj(project));
        }

        projectComboBox.setSelectedIndex(0);

        final Proj selectedProject = (Proj) projectComboBox.getSelectedItem();

        final JComboBox serviceComboBox = new JComboBox();

        List<? extends Interface> iFaces = selectedProject.getProject().getInterfaceList();

        for (Interface iFace : iFaces) {
            serviceComboBox.addItem(new IFace(iFace));
        }

        serviceComboBox.setSelectedIndex(0);

        final IFace selectedIFace = (IFace) serviceComboBox.getSelectedItem();

        serviceLabel.setText(selectedIFace.getIFaceName());

        List <Operation> operations = selectedIFace.getIFace().getOperationList();

        String[] endpoints = selectedIFace.getIFace().getEndpoints();

        for (Operation operation: operations) {
            JLabel operationLabel = new JLabel("WSDL OPERATION - " + operation.getName());
            operationsPanel.add(operationLabel);
        }

        if (endpoints != null) {
            for (String endpoint : endpoints) {
                JLabel endpointLabel = new JLabel("Endpoint: " + endpoint);
                operationsPanel.add(endpointLabel);
            }
        }

        projectComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                JComboBox<Proj> projectComboBox = (JComboBox<Proj>) event.getSource();
                Proj selectedItem = (Proj) projectComboBox.getSelectedItem();

                List<? extends Interface> iFaces = selectedItem.getProject().getInterfaceList();

                serviceComboBox.setModel( new DefaultComboBoxModel() );

                for (Interface iFace : iFaces) {
                    serviceComboBox.addItem(new IFace(iFace));
                }

                serviceComboBox.setSelectedIndex(0);
            }
        });

        serviceComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                JComboBox<IFace> serviceComboBox = (JComboBox<IFace>) event.getSource();
                selectedIFace.setIFace((IFace) serviceComboBox.getSelectedItem());

                serviceLabel.setText(selectedIFace.getIFaceName());


                List<Operation> operations = selectedIFace.getIFace().getOperationList();
                String[] endpoints = selectedIFace.getIFace().getEndpoints();

                operationsPanel.removeAll();

                if (operations != null) {
                    for (Operation operation : operations) {

                        List<Request> requests = operation.getRequestList();

                        if (requests != null) {
                            for (Request request : requests) {
                                JLabel operationLabel = new JLabel("Operation - " + operation.getName() + ": " + request.getName());
                                operationsPanel.add(operationLabel);
                            }
                        }
                    }
                }

                if (endpoints != null) {
                    for (String endpoint : endpoints) {
                        JLabel endpointLabel = new JLabel("Endpoint: " + endpoint);
                        operationsPanel.add(endpointLabel);
                    }
                }
            }
        });
        ///PROJECT COMBOBOX SETTINGS ENDS

        ///OK BUTTON SETTINGS
        JButton btn = new JButton("OK");
        ///OK BUTTON SETTINGS ENDS

        newPanel.add(projectComboBox);
        newPanel.add(serviceComboBox);
        newPanel.add(btn);
        newPanel.add(operationsPanel);
        newPanel.add(requestPanel);
        ///




        ///DIALOG SETTING
        JDialog newDialog = new JDialog();
        newDialog.setSize(300, 300);
        newDialog.add(newPanel);
        ///DIALOG SETTING ENDS

        // iterate though all test cases adding auth props
        UISupport.showDialog(newDialog);
    }
}